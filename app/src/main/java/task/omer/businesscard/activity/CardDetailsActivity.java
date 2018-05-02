package task.omer.businesscard.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.orhanobut.hawk.Hawk;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import task.omer.businesscard.R;
import task.omer.businesscard.base.BaseActivity;
import task.omer.businesscard.fragment.MySupportMapFragment;
import task.omer.businesscard.model.Person;

import static task.omer.businesscard.application.Constant.KEY_PERSON;
import static task.omer.businesscard.application.Constant.KEY_PERSON_LIST;

public class CardDetailsActivity extends BaseActivity implements OnMapReadyCallback, MySupportMapFragment.OnTouchListener {

    MySupportMapFragment mSupportMapFragment;
    GoogleMap map;
    Person person;

    @BindView(R.id.main_scroll)
    ScrollView scrollView;

    @BindView(R.id.civ_person_photo)
    CircleImageView civPersonPhoto;

    @BindView(R.id.tv_fullname)
    TextView tvFullName;

    @BindView(R.id.tv_phone)
    TextView tvPhone;

    @BindView(R.id.tv_email)
    TextView tvEmail;

    @BindView(R.id.tv_company)
    TextView tvCompany;

    @BindView(R.id.tv_job)
    TextView tvJob;

    @OnClick({R.id.btn_delete_person})
    public void viewsClick(View view) {
        switch (view.getId()) {
            case R.id.btn_delete_person:
                closeKeyboard(view);
                showDeleteDialog();
                break;
        }
    }

    @NonNull
    public static Intent newIntent(Context context, Person person) {
        Intent intent = new Intent(context, CardDetailsActivity.class);
        intent.putExtra(KEY_PERSON, person);
        return intent;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_card_details;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null && getIntent().getExtras() != null) {
            person = getIntent().getExtras().getParcelable(KEY_PERSON);
            bindUserDetails();
        }
    }

    private void bindUserDetails() {
        if (person != null) {
            Glide.with(this).load(person.getPhoto()).error(R.drawable.user_photo_holder).diskCacheStrategy(DiskCacheStrategy.ALL).into(civPersonPhoto);
            tvFullName.setText(person.getFullName() != null ? person.getFullName() : "");
            tvPhone.setText(person.getPhone() != null ? person.getPhone() : "");
            tvEmail.setText(person.getEmail() != null ? person.getEmail() : "");
            tvCompany.setText(person.getCompany() != null ? person.getCompany() : "");
            tvJob.setText(person.getJob() != null ? person.getJob() : "");
            initMap();
        }
    }

    private void initMap() {
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
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        setUpMap();
    }

    @Override
    public void onTouch() {
        scrollView.requestDisallowInterceptTouchEvent(true);
    }

    public void setUpMap() {
        MarkerOptions k = new MarkerOptions()
                .position(person.getLocation())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
                .draggable(false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (map != null) {
            map.clear();
            map.addMarker(k);
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.setMyLocationEnabled(false);
            map.getUiSettings().setZoomControlsEnabled(true);
            map.getUiSettings().setZoomGesturesEnabled(true);
            map.getUiSettings().setCompassEnabled(false);
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.getUiSettings().setRotateGesturesEnabled(true);
            map.getUiSettings().setTiltGesturesEnabled(false);

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(person.getLocation(), 18));
        }
    }

    private void deletePerson() {
        if (Hawk.contains(KEY_PERSON_LIST)) {
            List<Person> personList = Hawk.get(KEY_PERSON_LIST);

            if(personList.remove(person)){
                Toast.makeText(this, R.string.deleted_successfully, Toast.LENGTH_SHORT).show();

                Hawk.put(KEY_PERSON_LIST, personList);

                setResult(RESULT_OK, new Intent());

                finish();
            }
        }
    }

    private void showDeleteDialog() {

        new MaterialDialog.Builder(this)
                .content(R.string.dialog_content_text)
                .theme(Theme.LIGHT)
                .cancelable(false)
                .positiveColor(ContextCompat.getColor(this, R.color.popup_positive))
                .negativeColor(ContextCompat.getColor(this, R.color.popup_negative))
                .contentColor(ContextCompat.getColor(this, R.color.black_text_color))
                .positiveText(R.string.dialog_positive_text).onPositive(new MaterialDialog.SingleButtonCallback() {

            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                deletePerson();
            }
        })
                .negativeText(R.string.dialog_negative_text).onNegative(new MaterialDialog.SingleButtonCallback() {

            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        })
                .show();
    }
}