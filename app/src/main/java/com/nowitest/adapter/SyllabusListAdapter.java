package com.nowitest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowitest.R;
import com.nowitest.model.SyllabusItem;

import java.util.ArrayList;

/**
 * Created by Pushkar Tamhane on 20/8/16.
 */

public class SyllabusListAdapter extends RecyclerView.Adapter<SyllabusListAdapter.MyViewHolder> {

    private ArrayList<SyllabusItem> subjectItemArrayList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView item_list_notice_center_textview_description;

        public MyViewHolder(View view)
        {
            super(view);
            item_list_notice_center_textview_description = (TextView) view.findViewById(R.id.item_list_notice_center_textview_description);
        }
    }


    public SyllabusListAdapter(Context context, ArrayList<SyllabusItem> subjectItemArrayList) {
        this.subjectItemArrayList = subjectItemArrayList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_subjects, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        SyllabusItem issueItem= subjectItemArrayList.get(position);
        holder.item_list_notice_center_textview_description.setText(issueItem.getTitle());
    }

    @Override
    public int getItemCount() {
        return subjectItemArrayList.size();
    }
}