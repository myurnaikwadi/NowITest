package com.nowitest.activity;

//import android.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nowitest.R;
import com.nowitest.databasehelper.DatabaseHelper;
import com.nowitest.util.Constants;

public class AdminHomeActivity extends ParentActivity {

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;
    private TextView activity_admin_home_textview_clear_all_data;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        databaseHelper = new DatabaseHelper(getApplicationContext());

        setActionBarCustomWithoutBack(getString(R.string.actionbarTitleAdmin));
        findViewByIds();

        activity_admin_home_textview_clear_all_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AdminHomeActivity.this);
                alertDialogBuilder.setTitle("");
                alertDialogBuilder.setMessage(R.string.Dialog_clear_data_message);
                alertDialogBuilder.setPositiveButton(R.string.Dialog_exit_yes,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                databaseHelper.deleteAllSubject();
                                databaseHelper.deleteAllSubjectPdf();
                                databaseHelper.deleteAllSyllabus();
                                databaseHelper.deleteAllSyllabusPdf();
                                databaseHelper.deleteAllFlashcard();
                                databaseHelper.deleteAllFlashcardPdf();
                                databaseHelper.deleteAllQuestionChoices();
                                databaseHelper.deleteAllQuestion();
                                databaseHelper.deleteAllTest();
                                Constants.deleteAllFiles();

                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.cleared_all_data), Toast.LENGTH_SHORT).show();
                            }

                        });

                alertDialogBuilder.setNegativeButton(R.string.Dialog_exit_no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();

                            }

                        });
                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();
            }
        });
    }

    private void findViewByIds(){
        activity_admin_home_textview_clear_all_data = (TextView) findViewById(R.id.activity_admin_home_textview_clear_all_data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin_home, menu);
        final Menu mMenu = menu;
        final MenuItem item = menu.findItem(R.id.custom_menu_logout_button);
        item.getActionView().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AdminHomeActivity.this);
                alertDialogBuilder.setTitle("");
                alertDialogBuilder.setMessage(R.string.Dialog_logout_message);
                alertDialogBuilder.setPositiveButton(R.string.Dialog_exit_yes,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                sharedPreferences.edit().remove(Constants.sharedPreferenceUserId).commit();
                                sharedPreferences.edit().remove(Constants.sharedPreferenceFirstName).commit();
                                sharedPreferences.edit().remove(Constants.sharedPreferenceLastName).commit();
                                sharedPreferences.edit().remove(Constants.sharedPreferenceCourse).commit();
                                sharedPreferences.edit().remove(Constants.sharedPreferenceUserType).commit();

                                finish();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }

                        });

                alertDialogBuilder.setNegativeButton(R.string.Dialog_exit_no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();

                            }

                        });
                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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
