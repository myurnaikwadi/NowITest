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

public class TestListAdapter extends RecyclerView.Adapter<TestListAdapter.MyViewHolder> {

    private ArrayList<TestItem> notificationItemArrayList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView item_list_test_textview_title;
        public TextView item_list_test_textview_total_question_;
        public TextView item_list_test_textview_duration_;
        public TextView item_list_test_textview_marks_;

        public MyViewHolder(View view)
        {
            super(view);
            item_list_test_textview_title = (TextView) view.findViewById(R.id.item_list_test_textview_title);
            item_list_test_textview_total_question_ = (TextView) view.findViewById(R.id.item_list_test_textview_total_question_);
            item_list_test_textview_duration_ = (TextView) view.findViewById(R.id.item_list_test_textview_duration_);
            item_list_test_textview_marks_ = (TextView) view.findViewById(R.id.item_list_test_textview_marks_);
        }
    }


    public TestListAdapter(Context context, ArrayList<TestItem> notificationItemArrayList) {
        this.notificationItemArrayList = notificationItemArrayList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_test, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        TestItem issueItem= notificationItemArrayList.get(position);
        holder.item_list_test_textview_title.setText(issueItem.getTitle());
        holder.item_list_test_textview_total_question_.setText(issueItem.getTotalQuestions());
        holder.item_list_test_textview_duration_.setText(issueItem.getDuration());
        holder.item_list_test_textview_marks_.setText(issueItem.getMarks());
    }

    @Override
    public int getItemCount() {
        return notificationItemArrayList.size();
    }
}