package task.omer.businesscard.application;

import android.support.multidex.MultiDexApplication;

import com.orhanobut.hawk.Hawk;

public class BusinessApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Hawk.init(this).build();
    }
}