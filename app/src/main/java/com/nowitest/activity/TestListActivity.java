package com.nowitest.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nowitest.Controller;
import com.nowitest.R;
import com.nowitest.adapter.TestListAdapter;
import com.nowitest.databasehelper.DatabaseHelper;
import com.nowitest.model.TestItem;
import com.nowitest.network.InternetConnection;
import com.nowitest.util.Constants;
import com.nowitest.util.RecyclerItemClickListener;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pushkar Tamhane on 20/8/16.
 */
public class TestListActivity extends ParentActivity {

    private RecyclerView activity_notification_recycler_view;
    private TestListAdapter notificationsListAdapter;
    private ArrayList<TestItem> testItemArrayList;
    private InternetConnection internetConnection = new InternetConnection();
    private Dialog dialogForgotPassword;
    private DatabaseHelper databaseHelper;
    SharedPreferences sharedPreferencesRemember;
    private SwipeRefreshLayout swipeRefreshLayout;
    DownloadListener listener = new DownloadListener();
    ArrayList<Integer> downloadIds = new ArrayList<Integer>();
    ThinDownloadManager downloadManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        setActionBarCustomWithoutBack(getResources().getString(R.string.actionbarTitleTest));
        findViewByIds();

        downloadIds = new ArrayList<Integer>();
        downloadManager = new ThinDownloadManager(Constants.DOWNLOAD_THREAD_POOL_SIZE);
        databaseHelper = new DatabaseHelper(getApplicationContext());

        System.out.println("Test Count: "+databaseHelper.getCount("test"));
        System.out.println("Question Count: "+databaseHelper.getCount("question"));
        System.out.println("Answer Count: "+databaseHelper.getCount("question_choices"));

        testItemArrayList = new ArrayList<TestItem>();

        activity_notification_recycler_view.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {

                dialogForgotPassword = new Dialog(TestListActivity.this);
                dialogForgotPassword.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogForgotPassword.setContentView(R.layout.dialog_enter_code);
                dialogForgotPassword.setCanceledOnTouchOutside(true);
                dialogForgotPassword.setCancelable(true);
                dialogForgotPassword.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                dialogForgotPassword.show();

                final EditText dialog_enter_code_edittext_code = (EditText) dialogForgotPassword.findViewById(R.id.dialog_enter_code_edittext_code);
                final TextView dialog_enter_code_textviewt_testname = (TextView) dialogForgotPassword.findViewById(R.id.dialog_enter_code_textviewt_testname);
                final Button dialog_enter_code_button_submit = (Button) dialogForgotPassword.findViewById(R.id.dialog_enter_code_button_submit);

                dialog_enter_code_textviewt_testname.setText(testItemArrayList.get(position).getTitle());

                dialog_enter_code_button_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (dialog_enter_code_edittext_code.getText().toString().trim().equalsIgnoreCase("")) {
                            dialog_enter_code_edittext_code.setError(getResources().getString(R.string.error_validation_code));
                        } else {
                            if (internetConnection.isNetworkAvailable(getApplicationContext())) {
                                validateCode(testItemArrayList.get(position).getId(), testItemArrayList.get(position).getTitle(), dialog_enter_code_edittext_code.getText().toString(),
                                        testItemArrayList.get(position).getDuration(), testItemArrayList.get(position).getCourse(), testItemArrayList.get(position).getSubject(),
                                        testItemArrayList.get(position).getMarks(), testItemArrayList.get(position).getTotalQuestions());


                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            }
        }));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (internetConnection.isNetworkAvailable(getApplicationContext())) {
                    swipeRefreshLayout.setRefreshing(true);
                    activity_notification_recycler_view.setEnabled(false);
                    testListAPI();

                } else {
                    activity_notification_recycler_view.setEnabled(true);
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void findViewByIds(){
        activity_notification_recycler_view = (RecyclerView) findViewById(R.id.activity_notification_recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();

        sharedPreferencesRemember = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(internetConnection.isNetworkAvailable(getApplicationContext())){
            testListAPI();
        }else{
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }

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

    public boolean testListAPI()
    {
        swipeRefreshLayout.setRefreshing(true);

        String quizQuestion = "testListAPI";
        final String url = Constants.SERVER_URL+Constants.getAllTest;

        final ProgressDialog dialog = new ProgressDialog(TestListActivity.this);
        dialog.setMessage(getResources().getString(R.string.please_wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Log.d("testListAPI", url);

        StringRequest quizQuestionRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s)
            {
                Log.d("testListAPI",s);
                try
                {
                    testItemArrayList.clear();

                    JSONObject response = new JSONObject(s);

                    if (response.getString(Constants.status).equalsIgnoreCase(Constants.status200))
                    {
                        dialog.dismiss();
                        JSONArray jsonArrayData = response.getJSONArray(Constants.data);

                        if (jsonArrayData.length() > 0) {

                            for(int i=0; i < jsonArrayData.length(); i++){
                                JSONObject jsonObjectData = jsonArrayData.getJSONObject(i);
                                String test_id = jsonObjectData.getString(Constants.test_id);
                                String title = jsonObjectData.getString(Constants.title);
                                String course = jsonObjectData.getString(Constants.course);
                                String subject = jsonObjectData.getString(Constants.subject);
                                String code = jsonObjectData.getString(Constants.code);
                                String test_time = jsonObjectData.getString(Constants.test_time);
                                String total_marks = jsonObjectData.getString(Constants.total_marks);
                                String total_questions = jsonObjectData.getString(Constants.total_questions);
                                String isSpecial = jsonObjectData.getString(Constants.isSpecial);

                                if(isSpecial.equalsIgnoreCase("0")) {
                                    testItemArrayList.add(new TestItem(test_id, title, total_questions, test_time + " minute", total_marks, course, subject, code));
                                }

                                JSONArray questionArray = jsonObjectData.getJSONArray(Constants.questions);

                                if (questionArray.length() > 0) {

                                    for (int j = 0; j < questionArray.length(); j++) {
                                        JSONObject object = questionArray.getJSONObject(j);
                                        String question_id = object.getString(Constants.question_id);
                                        String question = object.getString(Constants.question);
                                        String questionFile = object.getString(Constants.questionFile);
                                        String is_file = object.getString(Constants.is_file);
                                        String question_marks = object.getString(Constants.question_marks);
                                        String questionNo = object.getString(Constants.questionNo);

                                        if (questionFile.equalsIgnoreCase(""))
                                        {
                                        }
                                        else {
                                            final String questionImagePath = Constants.createPDFFolder(Constants.FolderquestionImages);

                                            Uri downloadUri = Uri.parse(Constants.SERVER_URL_IMAGE+questionFile);

                                            String targetFileName = questionImagePath + "/" + questionFile;
                                            Uri destinationUri = Uri.parse(targetFileName);
                                            final DownloadRequest request =
                                                    new DownloadRequest(downloadUri)
                                                            .setDestinationURI(destinationUri)
                                                            .setPriority(DownloadRequest.Priority.HIGH)
                                                            .setDownloadListener(listener);
                                            downloadIds.add(downloadManager.add(request));
                                        }

                                        JSONArray choiceArray = object.getJSONArray(Constants.options);



                                        for (int k = 0; k < choiceArray.length(); k++) {

                                            final String questionImagePath = Constants.createPDFFolder(Constants.FolderquestionImages);

                                            JSONObject jsonObject = choiceArray.getJSONObject(k);
                                            String choiceId = jsonObject.getString(Constants.choice_id);
                                            String choice = jsonObject.getString(Constants.choice);
                                            String isRight = jsonObject.getString(Constants.is_right);
                                            String isChoiceFile = jsonObject.getString(Constants.is_file);


                                            if(isChoiceFile.equalsIgnoreCase("0") || isChoiceFile.equalsIgnoreCase("")){
                                            }else {
                                                System.out.println("Choice FILES: " + Constants.SERVER_URL_IMAGE + choice);
                                                Uri downloadUri = Uri.parse(Constants.SERVER_URL_IMAGE + choice);

                                                String targetFileName = questionImagePath + "/" + choice;
                                                Uri destinationUri = Uri.parse(targetFileName);
                                                final DownloadRequest request =
                                                        new DownloadRequest(downloadUri)
                                                                .setDestinationURI(destinationUri)
                                                                .setPriority(DownloadRequest.Priority.HIGH)
                                                                .setDownloadListener(listener);
                                                downloadIds.add(downloadManager.add(request));
                                            }
                                        }

                                    }
                                }
                            }

                            notificationsListAdapter = new TestListAdapter(getApplicationContext(),testItemArrayList);
                            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                            linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
                            activity_notification_recycler_view.setLayoutManager(linearLayoutManager);
                            activity_notification_recycler_view.setAdapter(notificationsListAdapter);

                            swipeRefreshLayout.setRefreshing(false);
                            activity_notification_recycler_view.setEnabled(true);

                            dialog.dismiss();
                        }
                        else
                        {
                            swipeRefreshLayout.setRefreshing(false);
                            activity_notification_recycler_view.setEnabled(true);

                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.test_not_available), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                        activity_notification_recycler_view.setEnabled(true);
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.test_not_available), Toast.LENGTH_SHORT).show();
                    }

                }
                catch (JSONException e)
                {
                    swipeRefreshLayout.setRefreshing(false);
                    activity_notification_recycler_view.setEnabled(true);
                    dialog.dismiss();
                    Log.d("testListAPI",e.toString());
                }

            }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                swipeRefreshLayout.setRefreshing(false);
                activity_notification_recycler_view.setEnabled(true);

                dialog.dismiss();
            }
        }
        ){


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                final SharedPreferences sharedPreferencesRemember = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                HashMap<String,String> data = new HashMap<>();
                data.put(Constants.ismobile, Constants.isMobileValue);
                data.put(Constants.course, sharedPreferencesRemember.getString(Constants.sharedPreferenceCourse, ""));
                data.put(Constants.userid, sharedPreferencesRemember.getString(Constants.sharedPreferenceUserId, ""));
                Log.d("testListAPI",data.toString());
                return data;
            }
        };
        Controller.getInstance().addToRequestQueue(quizQuestionRequest, quizQuestion);

        return  false;
    }


    public boolean validateCode(final String testId, final String testName, final String testCode, final String testDuration, final String testCourse, final String testSubject
            , final String testMarks, final String testQuestions)
    {
        String quizQuestion = "validateCode";
        final String url = Constants.SERVER_URL+Constants.validateCode;

        final ProgressDialog dialog = new ProgressDialog(TestListActivity.this);
        dialog.setMessage(getResources().getString(R.string.please_wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Log.d("validateCode", url);

        StringRequest quizQuestionRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s)
            {
                Log.d("validateCode",s);
                try
                {
                    JSONObject response = new JSONObject(s);

                    if (response.getString(Constants.status).equalsIgnoreCase(Constants.status200))
                    {
                        dialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), StartTestActivity.class);
                        intent.putExtra(Constants.test_id, testId);
                        intent.putExtra(Constants.title, testName);
                        intent.putExtra(Constants.test_time, testDuration);
                        intent.putExtra(Constants.course, testCourse);
                        intent.putExtra(Constants.subject, testSubject);
                        intent.putExtra(Constants.total_marks, testMarks);
                        intent.putExtra(Constants.total_questions, testQuestions);
                        intent.putExtra(Constants.code, testCode);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        dialogForgotPassword.dismiss();

                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), response.getString(Constants.message), Toast.LENGTH_SHORT).show();
                    }

                }
                catch (JSONException e)
                {
                    dialog.dismiss();
                    Log.d("validateCode",e.toString());
                }

            }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                dialog.dismiss();
            }
        }
        ){

            //TODO change limit and topic id dynamically.
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> data = new HashMap<>();
                data.put(Constants.ismobile, Constants.isMobileValue);
                data.put(Constants.code, testCode);
                data.put(Constants.test_id, testId);
                Log.d("validateCode",data.toString());
                return data;
            }
        };
        Controller.getInstance().addToRequestQueue(quizQuestionRequest, quizQuestion);

        return  false;
    }

    class DownloadListener implements DownloadStatusListener {

        @Override
        public void onDownloadComplete(int i) {
            downloadCompleted(i);
        }

        private void downloadCompleted(int i) {
            int indexToRemove = -1;
            for (int index = 0; index < downloadIds.size(); index++) {
                if (downloadIds.get(index) == i) {
                    indexToRemove = index;
                    break;
                }
            }
            if (indexToRemove != -1) {
                downloadIds.remove(indexToRemove);
            }
        }

        @Override
        public void onDownloadFailed(int i, int i2, String s) {
            downloadCompleted(i);
        }

        @Override
        public void onProgress(int i, long l, long l2, int i2) {

        }
    }
}
