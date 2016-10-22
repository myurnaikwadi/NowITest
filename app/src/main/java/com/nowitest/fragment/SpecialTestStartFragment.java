package com.nowitest.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.nowitest.activity.StartTestActivity;
import com.nowitest.adapter.SpecialTestListAdapter;
import com.nowitest.model.TestItem;
import com.nowitest.network.InternetConnection;
import com.nowitest.util.Constants;
import com.nowitest.util.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpecialTestStartFragment extends Fragment
{

    private RecyclerView activity_notification_recycler_view;
    private SpecialTestListAdapter specialTestListAdapter;
    private ArrayList<TestItem> testItemArrayList;
    private InternetConnection internetConnection = new InternetConnection();
    private Dialog dialogForgotPassword;
    SharedPreferences sharedPreferencesRemember;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.activity_test, container, false);
        findViewByIds(rootView);

        sharedPreferencesRemember = PreferenceManager.getDefaultSharedPreferences(getActivity());

        testItemArrayList = new ArrayList<TestItem>();

        activity_notification_recycler_view.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {

                dialogForgotPassword = new Dialog(getActivity());
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
                            if (internetConnection.isNetworkAvailable(getActivity())) {
                                validateCode(testItemArrayList.get(position).getId(), testItemArrayList.get(position).getTitle(), dialog_enter_code_edittext_code.getText().toString(),
                                        testItemArrayList.get(position).getDuration(), testItemArrayList.get(position).getCourse(), testItemArrayList.get(position).getSubject(),
                                        testItemArrayList.get(position).getMarks(), testItemArrayList.get(position).getTotalQuestions());


                            } else {
                                Toast.makeText(getActivity(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            }
        }));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (internetConnection.isNetworkAvailable(getActivity())) {
                    swipeRefreshLayout.setRefreshing(true);
                    activity_notification_recycler_view.setEnabled(false);
                    testListAPI();

                } else {
                    activity_notification_recycler_view.setEnabled(true);
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();

                }
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        sharedPreferencesRemember = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if(internetConnection.isNetworkAvailable(getActivity())){
            testListAPI();
        }else{
            Toast.makeText(getActivity(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }

    }

    private void findViewByIds(View rootView)
    {
        activity_notification_recycler_view = (RecyclerView) rootView.findViewById(R.id.activity_notification_recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
    }

    public boolean testListAPI()
    {
        swipeRefreshLayout.setRefreshing(true);

        String quizQuestion = "testListAPI";
        final String url = Constants.SERVER_URL+Constants.getAllTest;

        final ProgressDialog dialog = new ProgressDialog(getActivity());
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

                                if(isSpecial.equalsIgnoreCase("1")){
                                    testItemArrayList.add(new TestItem(test_id,title,total_questions,test_time+" minute", total_marks, course, subject, code));
                                }

                            }

                            specialTestListAdapter = new SpecialTestListAdapter(getActivity(),testItemArrayList);
                            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                            linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
                            activity_notification_recycler_view.setLayoutManager(linearLayoutManager);
                            activity_notification_recycler_view.setAdapter(specialTestListAdapter);

                            swipeRefreshLayout.setRefreshing(false);
                            activity_notification_recycler_view.setEnabled(true);

                            dialog.dismiss();
                        }
                        else
                        {
                            swipeRefreshLayout.setRefreshing(false);
                            activity_notification_recycler_view.setEnabled(true);

                            dialog.dismiss();
                        }

                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                        activity_notification_recycler_view.setEnabled(true);

                        dialog.dismiss();
                        Toast.makeText(getActivity(), getResources().getString(R.string.test_not_available), Toast.LENGTH_SHORT).show();
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

                final SharedPreferences sharedPreferencesRemember = PreferenceManager.getDefaultSharedPreferences(getActivity());

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

        final ProgressDialog dialog = new ProgressDialog(getActivity());
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
                        Intent intent = new Intent(getActivity(), StartTestActivity.class);
                        intent.putExtra(Constants.test_id, testId);
                        intent.putExtra(Constants.title, testName);
                        intent.putExtra(Constants.test_time, testDuration);
                        intent.putExtra(Constants.course, testCourse);
                        intent.putExtra(Constants.subject, testSubject);
                        intent.putExtra(Constants.total_marks, testMarks);
                        intent.putExtra(Constants.total_questions, testQuestions);
                        intent.putExtra(Constants.code, testCode);
                        startActivity(intent);
                        getActivity().overridePendingTransition(0,0);
                        dialogForgotPassword.dismiss();

                    } else {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), response.getString(Constants.message), Toast.LENGTH_SHORT).show();
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
}
