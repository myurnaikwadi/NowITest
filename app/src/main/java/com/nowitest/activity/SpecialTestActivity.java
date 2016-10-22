package com.nowitest.activity;

//import android.app.FragmentTransaction;

import android.os.Bundle;

import com.nowitest.R;
import com.nowitest.fragment.SpecialTestFragment;

public class SpecialTestActivity extends ParentActivity {

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        setActionBarCustomWithoutBack(getString(R.string.actionbarTitleSpecialTest));

        final android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();

        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SpecialTestFragment fragment = new SpecialTestFragment();
        fragmentTransaction.replace(R.id.container_frameLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_test, menu);
//        final Menu mMenu = menu;
//        final MenuItem item = menu.findItem(R.id.action_custom_button_sync);
//        item.getActionView().setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
////                Toast.makeText(getApplicationContext(), "Sync clicked", Toast.LENGTH_SHORT).show();
//            }
//        });
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }
}
