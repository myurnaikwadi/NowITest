package com.nowitest.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nowitest.R;
import com.nowitest.activity.ReviewTestActivity;
import com.nowitest.activity.StartReviewTestActivity;
import com.nowitest.model.TestItem;
import com.nowitest.util.Constants;

import java.util.ArrayList;

/**
 * Created by Pushkar Tamhane on 20/8/16.
 */

public class ReviewTestListAdapter extends RecyclerView.Adapter<ReviewTestListAdapter.MyViewHolder> {

    private ArrayList<TestItem> reviewTestItemArrayList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView item_list_riew_test_textview_title;
        public TextView item_list_riew_test_button_start_practice_test;
        public RelativeLayout item_list_review_test_relativelayout;

        public MyViewHolder(View view)
        {
            super(view);
            item_list_riew_test_textview_title = (TextView) view.findViewById(R.id.item_list_riew_test_textview_title);
            item_list_riew_test_button_start_practice_test = (TextView) view.findViewById(R.id.item_list_riew_test_button_start_practice_test);
            item_list_review_test_relativelayout = (RelativeLayout) view.findViewById(R.id.item_list_review_test_relativelayout);

            item_list_review_test_relativelayout.setOnClickListener(this);
            item_list_riew_test_button_start_practice_test.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == item_list_riew_test_button_start_practice_test.getId())
            {
                Intent intent = new Intent(context, StartReviewTestActivity.class);
                intent.putExtra(Constants.test_id, reviewTestItemArrayList.get(getAdapterPosition()).getId());
                intent.putExtra(Constants.title, reviewTestItemArrayList.get(getAdapterPosition()).getTitle());
                intent.putExtra(Constants.test_time, reviewTestItemArrayList.get(getAdapterPosition()).getDuration());
                intent.putExtra(Constants.course, reviewTestItemArrayList.get(getAdapterPosition()).getCourse());
                intent.putExtra(Constants.subject, reviewTestItemArrayList.get(getAdapterPosition()).getSubject());
                intent.putExtra(Constants.total_marks, reviewTestItemArrayList.get(getAdapterPosition()).getMarks());
                intent.putExtra(Constants.total_questions, reviewTestItemArrayList.get(getAdapterPosition()).getTotalQuestions());
                intent.putExtra(Constants.code, reviewTestItemArrayList.get(getAdapterPosition()).getCode());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }else if (v.getId() == item_list_review_test_relativelayout.getId())
            {
                Intent intent = new Intent(context, ReviewTestActivity.class);
                intent.putExtra(Constants.test_id, reviewTestItemArrayList.get(getAdapterPosition()).getId());
                intent.putExtra(Constants.title, reviewTestItemArrayList.get(getAdapterPosition()).getTitle());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }


    public ReviewTestListAdapter(Context context, ArrayList<TestItem> reviewTestItemArrayList) {
        this.reviewTestItemArrayList = reviewTestItemArrayList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_review_test, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        TestItem issueItem= reviewTestItemArrayList.get(position);
        holder.item_list_riew_test_textview_title.setText(issueItem.getTitle());
    }

    @Override
    public int getItemCount() {
        return reviewTestItemArrayList.size();
    }
}