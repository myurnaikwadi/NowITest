package com.nowitest.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.widget.Toast;

import com.nowitest.R;
import com.nowitest.util.Constants;

public class SplashActivity extends ParentActivity {

    private Handler myHandler;
    private static int SPLASH_TIME_OUT = 2000;
    private final static int PERMISSION_REQUEST_CODE101 = 101;
    private final static int PERMISSION_REQUEST_CODE102 = 102;
    SharedPreferences sharedPreferencesRemember;
    private boolean isPermissionGranted, lastPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setStatusBar();
        sharedPreferencesRemember = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);

        if (isDeviceBuildVersionMarshmallow()){
            if(lastPermission == false && isPermissionGranted == true){
                myHandler = new Handler();
                myHandler.postDelayed(myRunnable, SPLASH_TIME_OUT);
            }else {
                getSMSReadPermisson();
            }
        }else{
            myHandler = new Handler();
            myHandler.postDelayed(myRunnable, SPLASH_TIME_OUT);
        }

    }

    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {

            finish();
            if(sharedPreferencesRemember.getString(Constants.sharedPreferenceUserId, "").equalsIgnoreCase("")){
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }else{
               if(sharedPreferencesRemember.getString(Constants.sharedPreferenceUserType, "").equalsIgnoreCase("3")){
                    Intent intent = new Intent(getApplicationContext(), AdminHomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }else{
                   Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                   startActivity(intent);
                   overridePendingTransition(0, 0);
               }

            }
        }
    };



    protected void onStop() {
        super.onStop();
        if (myHandler==null)
        {

        }
        else {
            myHandler.removeCallbacks(myRunnable);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (myHandler==null)
            {

            }
            else {
                myHandler.removeCallbacks(myRunnable);
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isDeviceBuildVersionMarshmallow(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return true;

        return false;
    }


    private void getSMSReadPermisson() {

        int permissionCheckExternalStorage = ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheckExternalStorage != PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                // explanation needed
                ActivityCompat.requestPermissions(SplashActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE101);


            }else{
                // No explanation needed
                ActivityCompat.requestPermissions(SplashActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE101);
            }
        }

        if(permissionCheckExternalStorage == 0){
            isPermissionGranted = true;
            myHandler = new Handler();
            myHandler.postDelayed(myRunnable, SPLASH_TIME_OUT);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode){

            case PERMISSION_REQUEST_CODE101:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

//                    Toast.makeText(SplashActivity.this, "Thanks for granting permission", Toast.LENGTH_SHORT).show();
                    isPermissionGranted = true;
                    lastPermission = true;

                    myHandler = new Handler();
                    myHandler.postDelayed(myRunnable, SPLASH_TIME_OUT);

                }else {

                    Toast.makeText(SplashActivity.this,"read external storage permission denied",Toast.LENGTH_SHORT).show();
                    lastPermission = false;
                }
                break;
        }
    }
}
