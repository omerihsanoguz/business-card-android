package task.omer.businesscard.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import task.omer.businesscard.application.BusinessApplication;

public abstract class BaseFragment extends Fragment {

    public BusinessApplication application;

    protected final String TAG = this.getClass().getSimpleName();

    public BaseFragment() {
    }

    @SuppressWarnings("ConstantConditions")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(this.setLayoutId(), container, false);

        application = (BusinessApplication) getActivity().getApplicationContext();

        ButterKnife.bind(this, view);

        return view;
    }

    @LayoutRes
    public abstract int setLayoutId();

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}