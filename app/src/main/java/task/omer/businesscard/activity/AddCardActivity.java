package task.omer.businesscard.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.orhanobut.hawk.Hawk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import task.omer.businesscard.R;
import task.omer.businesscard.base.BaseActivity;
import task.omer.businesscard.fragment.MySupportMapFragment;
import task.omer.businesscard.model.Person;
import task.omer.businesscard.util.GPSTracker;

import static task.omer.businesscard.application.Constant.KEY_PERSON_LIST;
import static task.omer.businesscard.application.Constant.PICK_PHOTO_REQUEST_CODE;

public class AddCardActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, MySupportMapFragment.OnTouchListener {

    MySupportMapFragment mSupportMapFragment;
    GoogleMap map;
    GPSTracker gpsTracker;
    LatLng selectedLocation;

    String photo = null;

    @BindView(R.id.main_scroll)
    ScrollView scrollView;

    @BindView(R.id.container_selected_image)
    RelativeLayout containerSelectedImage;

    @BindView(R.id.iv_selected_image)
    ImageView ivSelectedImage;

    @BindView(R.id.container_choose_photo)
    LinearLayout containerChoosePhoto;

    @BindView(R.id.edt_fullname)
    EditText edtFullName;

    @BindView(R.id.edt_phone)
    EditText edtPhone;

    @BindView(R.id.edt_email)
    EditText edtEmail;

    @BindView(R.id.edt_company)
    EditText edtCompany;

    @BindView(R.id.edt_job)
    EditText edtJob;

    @BindView(R.id.edt_location_search)
    EditText edtLocationSearch;

    @BindView(R.id.iv_map_search)
    ImageView ivMapSearch;

    @BindView(R.id.btn_save_person)
    Button btnSavePerson;

    @OnClick({R.id.container_choose_photo, R.id.container_selected_image, R.id.iv_map_search, R.id.iv_current_location, R.id.btn_save_person})
    public void viewsClick(View view) {
        switch (view.getId()) {
            case R.id.container_choose_photo:
                getImageFromGallery();
                break;
            case R.id.container_selected_image:
                removeSelectedImageFromImageView();
                break;
            case R.id.iv_map_search:
                searchLocation();
                break;
            case R.id.iv_current_location:
                setUpMap();
                break;
            case R.id.btn_save_person:
                closeKeyboard(view);
                savePerson();
                break;
        }
    }

    @OnTextChanged(R.id.edt_location_search)
    public void searchTextChanged() {
        if (edtLocationSearch.getText().length() > 0) {
            ivMapSearch.setVisibility(View.VISIBLE);
        } else {
            ivMapSearch.setVisibility(View.GONE);
        }
    }

    @NonNull
    public static Intent newIntent(Context context) {
        return new Intent(context, AddCardActivity.class);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_add_card;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gpsTracker = new GPSTracker(this);

        try {
            mSupportMapFragment = (MySupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
            if (mSupportMapFragment != null) {
                mSupportMapFragment.getMapAsync(this);
                mSupportMapFragment.setListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_PHOTO_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    getSelectedImage(data);
                }
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(this);
        setUpMap();
    }

    @Override
    public void onTouch() {
        scrollView.requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        map.clear();
        MarkerOptions k = new MarkerOptions()
                .position(new LatLng(latLng.latitude, latLng.longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
                .draggable(false);

        map.addMarker(k);
        selectedLocation = latLng;
    }

    public void setUpMap() {
        LatLng latLng = new LatLng(gpsTracker.latitude, gpsTracker.longitude);
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
                .draggable(false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (map != null) {
            map.clear();
            map.addMarker(options);
            selectedLocation = latLng;
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.setMyLocationEnabled(false);
            map.getUiSettings().setZoomControlsEnabled(true);
            map.getUiSettings().setZoomGesturesEnabled(true);
            map.getUiSettings().setCompassEnabled(false);
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.getUiSettings().setRotateGesturesEnabled(true);
            map.getUiSettings().setTiltGesturesEnabled(false);

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        }
    }

    private void searchLocation() {
        LatLng latLng = ConvertLocationToPoint(edtLocationSearch.getText().toString());
        if (latLng != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        }
    }

    public LatLng ConvertLocationToPoint(String search) {
        LatLng latLng = null;
        Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocationName(search, 5);

            if (addresses.size() > 0) {
                latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
            } else {
                showAlertDialog();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return latLng;
    }

    private void showAlertDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(R.string.map_dialog_title_text);
        adb.setMessage(R.string.map_dialog_message_text);
        adb.setPositiveButton(R.string.map_dialog_close_text, null);
        adb.show();
    }

    @NonNull
    private Person getEnteredPerson() {
        String fullName = edtFullName.getText().toString().length() > 0 ? edtFullName.getText().toString() : null;
        String phone = edtPhone.getText().toString().length() > 0 ? edtPhone.getText().toString() : null;
        String email = edtEmail.getText().toString().length() > 0 ? edtEmail.getText().toString() : null;
        String company = edtCompany.getText().toString().length() > 0 ? edtCompany.getText().toString() : null;
        String job = edtJob.getText().toString().length() > 0 ? edtJob.getText().toString() : null;

        return new Person(photo, fullName, phone, email, company, job, selectedLocation);
    }

    private void getImageFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PICK_PHOTO_REQUEST_CODE);
    }

    private void getSelectedImage(Intent data) {
        if (data.getData() != null) {
            photo = data.getData().toString();
            if (photo != null) {
                setSelectedImageToImageView();
            } else {
                Toast.makeText(this, R.string.file_not_found_text, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setSelectedImageToImageView() {
        containerChoosePhoto.setVisibility(View.GONE);
        containerSelectedImage.setVisibility(View.VISIBLE);
        Glide.with(this).load(photo).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivSelectedImage);
    }

    private void removeSelectedImageFromImageView() {
        containerSelectedImage.setVisibility(View.GONE);
        containerChoosePhoto.setVisibility(View.VISIBLE);
        ivSelectedImage.setImageResource(android.R.color.transparent);
    }

    private void savePerson() {
        Person person = getEnteredPerson();

        clearInputs();

        List<Person> personList = new ArrayList<>();

        if (Hawk.contains(KEY_PERSON_LIST)) {
            personList = Hawk.get(KEY_PERSON_LIST);
        }

        personList.add(person);

        Toast.makeText(this, R.string.save_successfully_text, Toast.LENGTH_SHORT).show();

        Hawk.put(KEY_PERSON_LIST, personList);

        setResult(RESULT_OK, new Intent());

        finish();
    }

    private void clearInputs() {
        edtFullName.setText("");
        edtPhone.setText("");
        edtEmail.setText("");
        edtCompany.setText("");
        edtJob.setText("");
    }
}