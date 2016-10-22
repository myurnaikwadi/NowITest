package com.nowitest.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nowitest.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mobintia on 1/8/16.
 */
public class SpecialTestFragment extends Fragment {

//    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    RelativeLayout relativelayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_login, container, false);
        findViewByIds(rootView);
        return rootView;

    }
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_employer_job_details);
//
//        findViewByIds();
//
//    }

    private void findViewByIds(View rootView){

//        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
//        toolbar.setTitle(getActivity().getResources().getString(R.string.app_name));
//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupTabs();

        relativelayout = (RelativeLayout)rootView.findViewById(R.id.relativelayout);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(((AppCompatActivity) getActivity()).getSupportFragmentManager());

        adapter.addFrag(new SpecialTestStartFragment(), getString(R.string.tab_special_test));
        adapter.addFrag(new NoticeCenterFragment(), getString(R.string.tab_notice_center));

        viewPager.setAdapter(adapter);
    }

    private void setupTabs() {

        LayoutInflater mInflater3 = (LayoutInflater)((AppCompatActivity) getActivity()).getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View convertView3 = mInflater3.inflate(R.layout.list_tab_item, null);

        final TextView tabOne1  = (TextView) convertView3.findViewById(R.id.list_tab_item_title);


        tabOne1.setText(getString(R.string.tab_special_test));
        tabLayout.getTabAt(0).setCustomView(convertView3);


        LayoutInflater mInflater2 = (LayoutInflater)((AppCompatActivity) getActivity()).getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View convertView2 = mInflater2.inflate(R.layout.list_tab_item, null);
        final TextView tabTwo1 = (TextView) convertView2.findViewById(R.id.list_tab_item_title);

        tabTwo1.setText(getString(R.string.tab_notice_center));
        tabLayout.getTabAt(1).setCustomView(convertView2);

        LayoutInflater mInflater1 = (LayoutInflater)((AppCompatActivity) getActivity()).getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View convertView1 = mInflater1.inflate(R.layout.list_tab_item, null);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position == 0) {
                    tabOne1.setTextColor(getResources().getColor(R.color.white));
                    tabTwo1.setTextColor(getResources().getColor(R.color.tab_inactive_text));
//                    tabOne1.setBackground(getResources().getDrawable(R.drawable.active_tab));
//                    tabTwo1.setBackground(getResources().getDrawable(R.drawable.inactive_tab));
                } else if (position == 1) {
                    tabOne1.setTextColor(getResources().getColor(R.color.tab_inactive_text));
                    tabTwo1.setTextColor(getResources().getColor(R.color.white));
//                    tabTwo1.setBackground(getResources().getDrawable(R.drawable.active_tab));
//                    tabOne1.setBackground(getResources().getDrawable(R.drawable.inactive_tab));
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home)
        {
            getActivity().finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
