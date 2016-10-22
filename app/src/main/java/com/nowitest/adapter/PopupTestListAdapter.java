package com.nowitest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowitest.R;
import com.nowitest.model.ReviewTestItem;

import java.util.ArrayList;

/**
 * Created by Pushkar Tamhane on 20/8/16.
 */

public class PopupTestListAdapter extends RecyclerView.Adapter<PopupTestListAdapter.MyViewHolder> {

    private ArrayList<ReviewTestItem> reviewTestItemArrayList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView list_tab_item_title;

        public MyViewHolder(View view)
        {
            super(view);
            list_tab_item_title = (TextView) view.findViewById(R.id.list_tab_item_title);
        }
    }


    public PopupTestListAdapter(Context context, ArrayList<ReviewTestItem> reviewTestItemArrayList) {
        this.reviewTestItemArrayList = reviewTestItemArrayList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_test_popup, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        ReviewTestItem issueItem= reviewTestItemArrayList.get(position);
        holder.list_tab_item_title.setText(issueItem.getTitle());
    }

    @Override
    public int getItemCount() {
        return reviewTestItemArrayList.size();
    }
}