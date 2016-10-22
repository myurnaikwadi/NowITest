package com.nowitest.activity;

//import android.app.FragmentTransaction;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.nowitest.Controller;
import com.nowitest.R;
import com.nowitest.adapter.MostWaitedAdapter;
import com.nowitest.adapter.PopupTestListAdapter;
import com.nowitest.model.MostWaitedModel;
import com.nowitest.model.ReviewTestItem;
import com.nowitest.network.InternetConnection;
import com.nowitest.util.Constants;
import com.nowitest.util.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsActivity extends ParentActivity {

    private TextView dropdown_actionbar_textview_header_title, activity_analytics_tetxview_timetaken, activity_analytics_tetxview_timetaken_for_question,
            activity_analytics_percentage, sync_button;
    private PopupTestListAdapter reviewTestListAdapter;
    private ArrayList<ReviewTestItem> reviewTestItemArrayList;
    private PieChart activity_analytics_pie_chart_1, activity_analytics_pie_chart_2;
    private String selectedTestId = "0", selectedTestName;
    private InternetConnection internetConnection = new InternetConnection();
    private LinearLayout activity_analyatics_linearlayout_aggrigate;
    private RelativeLayout activity_analyatics_relativelayout_answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        findViewByIds();

        reviewTestItemArrayList = new ArrayList<ReviewTestItem>();

        if(internetConnection.isNetworkAvailable(getApplicationContext())){
            studentAttemptedTest();
        }else{
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }
        dropdown_actionbar_textview_header_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu();
            }
        });

        activity_analytics_tetxview_timetaken_for_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetConnection.isNetworkAvailable(getApplicationContext())){
                    getMostWaited();
                }else{
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void findViewByIds(){

        dropdown_actionbar_textview_header_title = (TextView) findViewById(R.id.dropdown_actionbar_textview_header_title);
        activity_analytics_percentage = (TextView) findViewById(R.id.activity_analytics_percentage);
//        activity_analytics_tetxview_timetaken = (TextView) findViewById(R.id.activity_analytics_tetxview_timetaken);
        activity_analytics_tetxview_timetaken_for_question = (TextView) findViewById(R.id.activity_analytics_tetxview_timetaken_for_question);
        sync_button = (TextView) findViewById(R.id.sync_button);
//        activity_analytics_pie_chart_1 = (PieChart) findViewById(R.id.activity_analytics_pie_chart_1);
//        activity_analytics_pie_chart_2 = (PieChart) findViewById(R.id.activity_analytics_pie_chart_2);
        activity_analyatics_linearlayout_aggrigate = (LinearLayout) findViewById(R.id.activity_analyatics_linearlayout_aggrigate);
        activity_analyatics_relativelayout_answer = (RelativeLayout) findViewById(R.id.activity_analyatics_relativelayout_answer);
        sync_button.setVisibility(View.GONE);
    }


    public void showMenu()
    {
        final Dialog dialogMenu= new Dialog(AnalyticsActivity.this);
        dialogMenu.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogMenu.setContentView(R.layout.activity_test_popup);
        dialogMenu.show();

        Window window = dialogMenu.getWindow();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        final RecyclerView activity_notification_recycler_view = (RecyclerView)dialogMenu.findViewById(R.id.activity_notification_recycler_view);

        reviewTestListAdapter = new PopupTestListAdapter(getApplicationContext(),reviewTestItemArrayList);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
        activity_notification_recycler_view.setLayoutManager(linearLayoutManager);
        activity_notification_recycler_view.setAdapter(reviewTestListAdapter);

        activity_notification_recycler_view.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                dropdown_actionbar_textview_header_title.setText("ANALYATICS-" + reviewTestItemArrayList.get(position).getTitle());
                selectedTestId = reviewTestItemArrayList.get(position).getId();
                dialogMenu.dismiss();

                if(internetConnection.isNetworkAvailable(getApplicationContext())){
                    getAnalytics();
                }else{
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        }));
    }


    public boolean studentAttemptedTest()
    {
        String quizQuestion = "studentAttemptedTest";
        final String url = Constants.SERVER_URL+Constants.studentAttemptedTest;

        final ProgressDialog dialog = new ProgressDialog(AnalyticsActivity.this);
        dialog.setMessage(getResources().getString(R.string.please_wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Log.d("studentAttemptedTest", url);

        StringRequest quizQuestionRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s)
            {
                Log.d("studentAttemptedTest",s);
                try
                {
                    reviewTestItemArrayList = new ArrayList<ReviewTestItem>();
                    reviewTestItemArrayList.add(new ReviewTestItem("0","OVERALL"));

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
                                reviewTestItemArrayList.add(new ReviewTestItem(test_id,title));
                            }
                            dialog.dismiss();

                            if(internetConnection.isNetworkAvailable(getApplicationContext())){
                                getAnalytics();
                            }else{
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            dialog.dismiss();
                        }

                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.test_not_available), Toast.LENGTH_SHORT).show();
                    }

                }
                catch (JSONException e)
                {
                    dialog.dismiss();
                    Log.d("studentAttemptedTest",e.toString());
                }

            }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                dialog.dismiss();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                final SharedPreferences sharedPreferencesRemember = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                HashMap<String,String> data = new HashMap<>();
                data.put(Constants.ismobile, Constants.isMobileValue);
                data.put(Constants.userid, sharedPreferencesRemember.getString(Constants.sharedPreferenceUserId, ""));
                Log.d("studentAttemptedTest",data.toString());
                return data;
            }
        };
        Controller.getInstance().addToRequestQueue(quizQuestionRequest, quizQuestion);

        return  false;
    }

    public boolean getAnalytics()
    {
        String quizQuestion = "getAnalytics";
        final String url = Constants.SERVER_URL+Constants.getAnalytics;

        final ProgressDialog dialog = new ProgressDialog(AnalyticsActivity.this);
        dialog.setMessage(getResources().getString(R.string.please_wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Log.d("getAnalytics", url);

        StringRequest quizQuestionRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s)
            {
                Log.d("getAnalytics",s);
                try
                {
                    JSONObject response = new JSONObject(s);

                    if (response.getString(Constants.status).equalsIgnoreCase(Constants.status200))
                    {
                        dialog.dismiss();
                        JSONObject jsonObjectData = response.getJSONObject(Constants.data);

                        //String QuestionNo = jsonObjectData.getString(Constants.QuestionNo);
                        //String timetaken = jsonObjectData.getString(Constants.timetaken);
                        String correctAns = jsonObjectData.getString(Constants.correctAns);
                        String wrongAns = jsonObjectData.getString(Constants.wrongAns);
                        String notAttempt = jsonObjectData.getString(Constants.notAttempt);
                        String aggregateCorrect = jsonObjectData.getString(Constants.aggregateCorrect);
                        String aggregateWrong = jsonObjectData.getString(Constants.aggregateWrong);

                        double totalPercentage = Constants.roundTwoDecimals(Double.parseDouble(aggregateCorrect));
                        activity_analytics_percentage.setText("Total Percentage: "+totalPercentage+"%");

                        ArrayList<Integer> colorCodes = new ArrayList<Integer>();
                        colorCodes.add(10183880);
                        colorCodes.add(418936);
                        colorCodes.add(8936);

                        PieChart activity_analytics_pie_chart_1 = new PieChart(getApplicationContext()),
                                activity_analytics_pie_chart_2 = new PieChart(getApplicationContext());

                        /*ArrayList<Entry> entriesAggrigate = new ArrayList<Entry>();
                        entriesAggrigate.add(new Entry(Float.parseFloat(aggregateCorrect), 0));
                        entriesAggrigate.add(new Entry(Float.parseFloat(aggregateWrong), 1));

                        PieDataSet datasetAggregate = new PieDataSet(entriesAggrigate,"");
                        datasetAggregate.setColors(ColorTemplate.COLORFUL_COLORS);

                        ArrayList<String> labelsAggrigate = new ArrayList<String>();
                        labelsAggrigate.add("Aggrigate Correct");
                        labelsAggrigate.add("Aggrigate Wrong");

                        PieData dataAggrigate = new PieData(labelsAggrigate, datasetAggregate); // initialize Piedata<br />
                        activity_analytics_pie_chart_1.setData(dataAggrigate);
                        activity_analytics_pie_chart_1.setLayoutParams(params);
                        activity_analytics_pie_chart_1.setDescription("");
                        activity_analyatics_linearlayout_aggrigate.removeAllViewsInLayout();
                        activity_analyatics_linearlayout_aggrigate.addView(activity_analytics_pie_chart_1);*/


                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                               450, 450
                        );


                        activity_analytics_pie_chart_2.setLayoutParams(params);


                        ArrayList<Entry> entriesAnswer = new ArrayList<Entry>();
                        entriesAnswer.add(new Entry(Float.parseFloat(correctAns), 0));
                        entriesAnswer.add(new Entry(Float.parseFloat(wrongAns), 1));
                        entriesAnswer.add(new Entry(Float.parseFloat(notAttempt), 2));

                        PieDataSet datasetAnswer = new PieDataSet(entriesAnswer, "");
                        datasetAnswer.setColors(ColorTemplate.COLORFUL_COLORS);

                        ArrayList<String> labelsAnswer = new ArrayList<String>();
                        labelsAnswer.add("Correct");
                        labelsAnswer.add("Incorrect");
                        labelsAnswer.add("Not Attempted");

                        PieData dataAnswer = new PieData(labelsAnswer, datasetAnswer); // initialize Piedata<br />
                        activity_analytics_pie_chart_2.setData(dataAnswer);


                        activity_analytics_pie_chart_2.setDescription("");

                        /*activity_analytics_tetxview_timetaken.setText(timetaken);
                        activity_analytics_tetxview_timetaken_for_question.setText(QuestionNo);*/


                        activity_analyatics_relativelayout_answer.removeAllViewsInLayout();

                        activity_analyatics_relativelayout_answer.addView(activity_analytics_pie_chart_2);

                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
                    }

                }
                catch (JSONException e)
                {
                    dialog.dismiss();
                    Log.d("getAnalytics",e.toString());
                }

            }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                dialog.dismiss();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                final SharedPreferences sharedPreferencesRemember = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                HashMap<String,String> data = new HashMap<>();
                data.put(Constants.ismobile, Constants.isMobileValue);
                data.put(Constants.test_id, selectedTestId);
                data.put(Constants.userid, sharedPreferencesRemember.getString(Constants.sharedPreferenceUserId, ""));
                Log.d("getAnalytics",data.toString());
                return data;
            }
        };
        Controller.getInstance().addToRequestQueue(quizQuestionRequest, quizQuestion);

        return  false;
    }


    public boolean getMostWaited()
    {
        String quizQuestion = "getMostWaiting";
        final String url = Constants.SERVER_URL+Constants.getMostWaiting;

        final ProgressDialog dialog = new ProgressDialog(AnalyticsActivity.this);
        dialog.setMessage(getResources().getString(R.string.please_wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Log.d("getMostWaiting", url);

        StringRequest quizQuestionRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s)
            {
                Log.d("getMostWaiting",s);
                try
                {
                    JSONObject response = new JSONObject(s);

                    if (response.getString(Constants.status).equalsIgnoreCase(Constants.status200))
                    {
                        dialog.dismiss();
                        JSONArray jsonArrayData = response.getJSONArray(Constants.data);

                        ArrayList<MostWaitedModel> mostWaitedModelArrayList = new ArrayList<MostWaitedModel>();

                        if(jsonArrayData.length() > 0) {
                            for (int i = 0; i < jsonArrayData.length(); i++) {
                                JSONObject jsonObjectData = jsonArrayData.getJSONObject(i);
                                String question = jsonObjectData.getString(Constants.question);
                                String timetaken = jsonObjectData.getString(Constants.timetaken);
                                mostWaitedModelArrayList.add(new MostWaitedModel(i+"",question,timetaken));
                            }

                            final Dialog dialog = new Dialog(AnalyticsActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_most_waited);
                            dialog.show();

                            RecyclerView dialog_most_waited_recycler_view = (RecyclerView) dialog.findViewById(R.id.dialog_most_waited_recycler_view);
                            TextView dialog_most_waited_textview_title = (TextView) dialog.findViewById(R.id.dialog_most_waited_textview_title);
                            ImageView dialog_most_waited_imageview_cancel = (ImageView) dialog.findViewById(R.id.dialog_most_waited_imageview_cancel);

                            dialog_most_waited_textview_title.setText(getResources().getString(R.string.most_waited_question));


                            MostWaitedAdapter mostWaitedAdapter = new MostWaitedAdapter(getApplicationContext(), mostWaitedModelArrayList);
                            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                            linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
                            dialog_most_waited_recycler_view.setLayoutManager(linearLayoutManager);
                            dialog_most_waited_recycler_view.setAdapter(mostWaitedAdapter);

                            dialog_most_waited_imageview_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                        }else{
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
                    }

                }
                catch (JSONException e)
                {
                    dialog.dismiss();
                    Log.d("getMostWaiting",e.toString());
                }

            }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                dialog.dismiss();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                final SharedPreferences sharedPreferencesRemember = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                HashMap<String,String> data = new HashMap<>();
                data.put(Constants.ismobile, Constants.isMobileValue);
                data.put(Constants.test_id, selectedTestId);
                data.put(Constants.userid, sharedPreferencesRemember.getString(Constants.sharedPreferenceUserId, ""));
                Log.d("getMostWaiting",data.toString());
                return data;
            }
        };
        Controller.getInstance().addToRequestQueue(quizQuestionRequest, quizQuestion);

        return  false;
    }
}
