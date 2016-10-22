package com.nowitest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowitest.R;
import com.nowitest.model.TestItem;

import java.util.ArrayList;

/**
 * Created by Pushkar Tamhane on 20/8/16.
 */

public class SpecialTestListAdapter extends RecyclerView.Adapter<SpecialTestListAdapter.MyViewHolder> {

    private ArrayList<TestItem> notificationItemArrayList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView fragment_special_test_textview_test_title;
        public TextView fragment_special_test_textview_total_question_;
        public TextView fragment_special_test_textview_duration_;
        public TextView fragment_special_test_textview_marks_;

        public MyViewHolder(View view)
        {
            super(view);
            fragment_special_test_textview_test_title = (TextView) view.findViewById(R.id.fragment_special_test_textview_test_title);
            fragment_special_test_textview_total_question_ = (TextView) view.findViewById(R.id.fragment_special_test_textview_total_question_);
            fragment_special_test_textview_duration_ = (TextView) view.findViewById(R.id.fragment_special_test_textview_duration_);
            fragment_special_test_textview_marks_ = (TextView) view.findViewById(R.id.fragment_special_test_textview_marks_);
        }
    }


    public SpecialTestListAdapter(Context context, ArrayList<TestItem> notificationItemArrayList) {
        this.notificationItemArrayList = notificationItemArrayList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_special_test, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        TestItem issueItem= notificationItemArrayList.get(position);
        holder.fragment_special_test_textview_test_title.setText(issueItem.getTitle());
        holder.fragment_special_test_textview_total_question_.setText(issueItem.getTotalQuestions());
        holder.fragment_special_test_textview_duration_.setText(issueItem.getDuration());
        holder.fragment_special_test_textview_marks_.setText(issueItem.getMarks());
    }

    @Override
    public int getItemCount() {
        return notificationItemArrayList.size();
    }
}