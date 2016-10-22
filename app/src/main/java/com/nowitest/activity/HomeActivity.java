package com.nowitest.activity;

//import android.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.nowitest.R;
import com.nowitest.adapter.StudentHomeButtonListAdapter;
import com.nowitest.model.StudentHomeButtonItem;
import com.nowitest.util.Constants;

import java.util.ArrayList;

public class HomeActivity extends ParentActivity {

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    private ArrayList<StudentHomeButtonItem> studentHomeButtonItemArrayList = new ArrayList<StudentHomeButtonItem>();
    private StudentHomeButtonListAdapter studentHomeButtonListAdapter;
    private GridView activity_home_student_gridview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_student);

        findViewByIds();

        studentHomeButtonItemArrayList.add(new StudentHomeButtonItem("", getResources().getString(R.string.test), getResources().getDrawable(R.drawable.test_icon)));
        studentHomeButtonItemArrayList.add(new StudentHomeButtonItem("", getResources().getString(R.string.syllabus), getResources().getDrawable(R.drawable.syllabus_icon)));
        studentHomeButtonItemArrayList.add(new StudentHomeButtonItem("", getResources().getString(R.string.review), getResources().getDrawable(R.drawable.review_icon)));
        studentHomeButtonItemArrayList.add(new StudentHomeButtonItem("", getResources().getString(R.string.analytics), getResources().getDrawable(R.drawable.analytics_icon)));
        studentHomeButtonItemArrayList.add(new StudentHomeButtonItem("", getResources().getString(R.string.course), getResources().getDrawable(R.drawable.course_icon)));
        studentHomeButtonItemArrayList.add(new StudentHomeButtonItem("", getResources().getString(R.string.flashcard), getResources().getDrawable(R.drawable.flashcard_icon)));
        studentHomeButtonItemArrayList.add(new StudentHomeButtonItem("", getResources().getString(R.string.special_test), getResources().getDrawable(R.drawable.special_test_communication_icon)));
        studentHomeButtonItemArrayList.add(new StudentHomeButtonItem("", getResources().getString(R.string.logout), getResources().getDrawable(R.drawable.logout_icon)));

        studentHomeButtonListAdapter = new StudentHomeButtonListAdapter(getApplicationContext(), studentHomeButtonItemArrayList);
        activity_home_student_gridview.setAdapter(studentHomeButtonListAdapter);

        activity_home_student_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                System.out.println("Grid position: "+position);
                if (position == 0) {
                    Intent intent = new Intent(getApplicationContext(), TestListActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                } else if (position == 1) {
                    Intent intent = new Intent(getApplicationContext(), SyllabusActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                } else if (position == 2) {
                    Intent intent = new Intent(getApplicationContext(), ReviewTestListActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                } else if (position == 3) {
                    Intent intent = new Intent(getApplicationContext(), AnalyticsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                } else if (position == 4) {
                    Intent intent = new Intent(getApplicationContext(), CourseActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                } else if (position == 5) {
                    Intent intent = new Intent(getApplicationContext(), FlashCardActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                } else if (position == 6) {
                    Intent intent = new Intent(getApplicationContext(), SpecialTestActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                } else if (position == 7) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
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
            }
        });
    }

    private void findViewByIds(){
        activity_home_student_gridview = (GridView) findViewById(R.id.activity_home_student_gridview);
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
