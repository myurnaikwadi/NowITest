package com.nowitest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.nowitest.R;
import com.nowitest.util.Constants;

import java.util.ArrayList;

/**
 * Created by mobintia on 28/9/16.
 */
public class PractiveTestAnalytics extends ParentActivity {

    private TextView activity_analytics_tetxview_timetaken_for_question, activity_analytics_percentage;
    private String intentCorrectAns, intentwrongAns, intentNotAttemptedAns, testName;
    private LinearLayout activity_analytics_include;
    private LinearLayout activity_analyatics_linearlayout_aggrigate;
    private RelativeLayout activity_analyatics_relativelayout_answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        Intent intent = getIntent();
        intentCorrectAns = intent.getStringExtra(Constants.correct_answer);
        intentwrongAns = intent.getStringExtra(Constants.wrongAns);
        intentNotAttemptedAns = intent.getStringExtra(Constants.notAttempt);
        testName = intent.getStringExtra(Constants.name);

        setActionBarCustomWithoutBack(getResources().getString(R.string.actionbarTitleOneTestAnalytics) + " " + testName);
        findViewByIds();


        ArrayList<Integer> colorCodes = new ArrayList<Integer>();
        colorCodes.add(10183880);
        colorCodes.add(418936);
        colorCodes.add(8936);

        PieChart activity_analytics_pie_chart_2 = new PieChart(getApplicationContext());


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                450, 450
        );

        activity_analytics_pie_chart_2.setLayoutParams(params);

        ArrayList<Entry> entriesAnswer = new ArrayList<Entry>();
        entriesAnswer.add(new Entry(Float.parseFloat(intentCorrectAns), 0));
        entriesAnswer.add(new Entry(Float.parseFloat(intentwrongAns), 1));
        entriesAnswer.add(new Entry(Float.parseFloat(intentNotAttemptedAns), 2));

        PieDataSet datasetAnswer = new PieDataSet(entriesAnswer, "");
        datasetAnswer.setColors(ColorTemplate.COLORFUL_COLORS);

        ArrayList<String> labelsAnswer = new ArrayList<String>();
        labelsAnswer.add("Correct");
        labelsAnswer.add("Incorrect");
        labelsAnswer.add("Not Attempted");

        PieData dataAnswer = new PieData(labelsAnswer, datasetAnswer); // initialize Piedata<br />
        activity_analytics_pie_chart_2.setData(dataAnswer);

        activity_analytics_pie_chart_2.setDescription("");

        activity_analyatics_relativelayout_answer.removeAllViewsInLayout();
        activity_analyatics_relativelayout_answer.addView(activity_analytics_pie_chart_2);
    }


    private void findViewByIds(){

        activity_analytics_percentage = (TextView) findViewById(R.id.activity_analytics_percentage);
        activity_analytics_tetxview_timetaken_for_question = (TextView) findViewById(R.id.activity_analytics_tetxview_timetaken_for_question);
        activity_analytics_include = (LinearLayout) findViewById(R.id.activity_analytics_include);
        activity_analytics_include.setVisibility(View.GONE);
        activity_analyatics_linearlayout_aggrigate = (LinearLayout) findViewById(R.id.activity_analyatics_linearlayout_aggrigate);
        activity_analyatics_relativelayout_answer = (RelativeLayout) findViewById(R.id.activity_analyatics_relativelayout_answer);

        activity_analytics_tetxview_timetaken_for_question.setVisibility(View.GONE);
    }
}
