package task.omer.businesscard.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;
import task.omer.businesscard.R;
import task.omer.businesscard.adapter.CardSearchAdapter;
import task.omer.businesscard.base.BaseActivity;
import task.omer.businesscard.model.Person;

import static task.omer.businesscard.application.Constant.ADD_CARD_REQUEST_CODE;
import static task.omer.businesscard.application.Constant.CARD_DETAILS_REQUEST_CODE;
import static task.omer.businesscard.application.Constant.KEY_PERSON_LIST;
import static task.omer.businesscard.application.Constant.MY_PERMISSIONS_REQUEST_LOCATION;

public class ListActivity extends BaseActivity {

    List<Person> personList = new ArrayList<>();
    private CardSearchAdapter searchAdapter;

    @BindView(R.id.container_empty)
    LinearLayout containerEmpty;

    @BindView(R.id.container_list)
    LinearLayout containerList;

    @BindView(R.id.edt_search)
    EditText edtSearch;

    @BindView(R.id.lv_cards)
    ListView lvCards;

    @OnItemClick(R.id.lv_cards)
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        startActivityForResult(CardDetailsActivity.newIntent(ListActivity.this, personList.get(pos)), CARD_DETAILS_REQUEST_CODE);
    }

    @OnTextChanged(R.id.edt_search)
    public void searchTextChanged() {
        searchAdapter.filter(edtSearch.getText().toString().toLowerCase());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_person:
                addPersonOnClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        addPersonOnClick();
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_CARD_REQUEST_CODE || requestCode == CARD_DETAILS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                setListData();
            }
        }
    }

    private void setListData() {
        if (Hawk.contains(KEY_PERSON_LIST)) {
            personList = Hawk.get(KEY_PERSON_LIST);

            if(personList.size() == 0){
                containerList.setVisibility(View.GONE);
                containerEmpty.setVisibility(View.VISIBLE);
            }

            if(personList.size() > 0){
                containerEmpty.setVisibility(View.GONE);
                containerList.setVisibility(View.VISIBLE);

                lvCards.setTextFilterEnabled(true);

                searchAdapter = new CardSearchAdapter(this, personList);

                lvCards.setAdapter(searchAdapter);
            }
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ListActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    private void addPersonOnClick() {
        if (checkLocationPermission())
            startActivityForResult(AddCardActivity.newIntent(ListActivity.this), ADD_CARD_REQUEST_CODE);
    }
}