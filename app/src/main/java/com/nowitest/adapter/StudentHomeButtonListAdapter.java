package com.nowitest.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.nowitest.R;
import com.nowitest.model.StudentHomeButtonItem;

import java.util.ArrayList;

public class StudentHomeButtonListAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<StudentHomeButtonItem> ShopCategoryItems;

    public StudentHomeButtonListAdapter(Context context, ArrayList<StudentHomeButtonItem> ShopCategoryItems)
    {
        this.context = context;
        this.ShopCategoryItems = ShopCategoryItems;
    }

    @Override
    public int getCount() {
        return ShopCategoryItems.size();
    }

    @Override
    public Object getItem(int position) {
        return ShopCategoryItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_list_home_button, null);
        }

        Button activity_home_student_button_test = (Button) convertView.findViewById(R.id.activity_home_student_button_test);
        View item_list_home_button_verticle_line = (View) convertView.findViewById(R.id.item_list_home_button_verticle_line);

//        list_shop_category_imageView_category.setImageResource(ShopCategoryItems.get(position).getImage());
        activity_home_student_button_test.setText(ShopCategoryItems.get(position).getName());
        activity_home_student_button_test.setCompoundDrawablesWithIntrinsicBounds(null,ShopCategoryItems.get(position).getImage(), null, null);

        if(position == 3 || position == 7){
            item_list_home_button_verticle_line.setVisibility(View.GONE);
        }else{
            item_list_home_button_verticle_line.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
}
