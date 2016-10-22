package com.nowitest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowitest.R;
import com.nowitest.model.MostWaitedModel;

import java.util.ArrayList;

public class MostWaitedAdapter extends RecyclerView.Adapter<MostWaitedAdapter.MyViewHolder> {

    private ArrayList<MostWaitedModel> mostWaitedModelArrayList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView list_item_most_waited_textview_question_no;
        public TextView list_item_most_waited_textview_time_taken;



        public MyViewHolder(View view)
        {
            super(view);
            list_item_most_waited_textview_question_no = (TextView) view.findViewById(R.id.list_item_most_waited_textview_question_no);
            list_item_most_waited_textview_time_taken = (TextView) view.findViewById(R.id.list_item_most_waited_textview_time_taken);
        }
    }


    public MostWaitedAdapter(Context context, ArrayList<MostWaitedModel> mostWaitedModelArrayList) {
        this.mostWaitedModelArrayList = mostWaitedModelArrayList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_most_waited, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
        MostWaitedModel issueItem= mostWaitedModelArrayList.get(position);
        holder.list_item_most_waited_textview_question_no.setText(issueItem.getQuestionNo());
        holder.list_item_most_waited_textview_time_taken.setText(issueItem.getTimeTaken());
    }

    @Override
    public int getItemCount() {
        return mostWaitedModelArrayList.size();
    }
}



