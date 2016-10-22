package com.nowitest.activity;

//import android.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import com.nowitest.R;
import com.nowitest.fragment.LoginFragment;
import com.nowitest.util.Constants;

public class LoginActivity extends ParentActivity {

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        setActionBarCustomWithoutBack(getString(R.string.actionbarTitleHome));

        Constants.createNowITestFolder();
        final android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();

        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LoginFragment fragment = new LoginFragment();
        fragmentTransaction.replace(R.id.container_frameLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed()
    {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            finish();
            return;
        } else {
            Toast.makeText(getBaseContext(), R.string.toast_exit, Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
    }

}
