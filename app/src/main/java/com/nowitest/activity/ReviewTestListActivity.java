package com.nowitest.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.nowitest.R;
import com.nowitest.adapter.ReviewTestListAdapter;
import com.nowitest.databasehelper.DatabaseHelper;
import com.nowitest.model.TestItem;

import java.util.ArrayList;

/**
 * Created by Pushkar Tamhane on 20/8/16.
 */
public class ReviewTestListActivity extends ParentActivity {

    private RecyclerView activity_notification_recycler_view;
    private ReviewTestListAdapter reviewTestListAdapter;
    private ArrayList<TestItem> reviewTestItemArrayList;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        setActionBarCustomWithoutBack(getResources().getString(R.string.actionbarTitleReviewTest));
        databaseHelper = new DatabaseHelper(getApplicationContext());
        findViewByIds();

        reviewTestItemArrayList = new ArrayList<TestItem>();

        reviewTestItemArrayList = databaseHelper.getAllTest();

//        reviewTestItemArrayList.add(new ReviewTestItem("1","Test Name 1"));
//        reviewTestItemArrayList.add(new ReviewTestItem("2","Test Name 2"));
//        reviewTestItemArrayList.add(new ReviewTestItem("3","Test Name 3"));
//        reviewTestItemArrayList.add(new ReviewTestItem("4","Test Name 4"));
//        reviewTestItemArrayList.add(new ReviewTestItem("5","Test Name 5"));
//        reviewTestItemArrayList.add(new ReviewTestItem("6","Test Name 6"));
//        reviewTestItemArrayList.add(new ReviewTestItem("7","Test Name 7"));
//        reviewTestItemArrayList.add(new ReviewTestItem("8","Test Name 8"));
//        reviewTestItemArrayList.add(new ReviewTestItem("9","Test Name 9"));
//        reviewTestItemArrayList.add(new ReviewTestItem("10","Test Name 10"));


        reviewTestListAdapter = new ReviewTestListAdapter(getApplicationContext(),reviewTestItemArrayList);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
        activity_notification_recycler_view.setLayoutManager(linearLayoutManager);
        activity_notification_recycler_view.setAdapter(reviewTestListAdapter);

//        activity_notification_recycler_view.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//
//
//            }
//        }));

    }

    private void findViewByIds(){
        activity_notification_recycler_view = (RecyclerView) findViewById(R.id.activity_notification_recycler_view);
    }

}
