package task.omer.businesscard.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import butterknife.ButterKnife;
import task.omer.businesscard.application.BusinessApplication;

public abstract class BaseActivity extends AppCompatActivity {

    public BusinessApplication application;

    protected final String TAG = this.getClass().getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.getLayoutResId());
        ButterKnife.bind(this);

        application = (BusinessApplication) this.getApplicationContext();
    }

    @LayoutRes
    public abstract int getLayoutResId();

    public void closeKeyboard(View view) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
