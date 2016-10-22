package com.nowitest.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nowitest.Controller;
import com.nowitest.R;
import com.nowitest.adapter.NoticeCenterListAdapter;
import com.nowitest.model.NoticeCenterItem;
import com.nowitest.network.InternetConnection;
import com.nowitest.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NoticeCenterFragment extends Fragment
{
    private RecyclerView activity_notification_recycler_view;
    private NoticeCenterListAdapter noticeCenterListAdapter;
    private ArrayList<NoticeCenterItem> noticeCenterItemArrayList;
    private InternetConnection internetConnection = new InternetConnection();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.activity_test, container, false);
        findViewByIds(rootView);

        noticeCenterItemArrayList = new ArrayList<NoticeCenterItem>();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (internetConnection.isNetworkAvailable(getActivity())) {
                    swipeRefreshLayout.setRefreshing(true);
                    activity_notification_recycler_view.setEnabled(false);
                    getNotice();
                } else {
                    activity_notification_recycler_view.setEnabled(true);
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    private void findViewByIds(View rootView)
    {
        activity_notification_recycler_view = (RecyclerView) rootView.findViewById(R.id.activity_notification_recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(internetConnection.isNetworkAvailable(getActivity())){
            getNotice();
        }else{
            Toast.makeText(getActivity(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean getNotice()
    {
        swipeRefreshLayout.setRefreshing(true);

        String quizQuestion = "getNotice";
        final String url = Constants.SERVER_URL+Constants.getNotice;

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getResources().getString(R.string.please_wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Log.d("getNotice", url);

        StringRequest quizQuestionRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s)
            {
                Log.d("getNotice",s);
                try
                {
                    noticeCenterItemArrayList.clear();

                    JSONObject response = new JSONObject(s);

                    if (response.getString(Constants.status).equalsIgnoreCase(Constants.status200))
                    {
                        dialog.dismiss();
                        JSONArray jsonArrayData = response.getJSONArray(Constants.data);

                        if (jsonArrayData.length() > 0) {

                            for(int i=0; i < jsonArrayData.length(); i++){
                                JSONObject jsonObjectData = jsonArrayData.getJSONObject(i);
                                String notice_id = jsonObjectData.getString(Constants.notice_id);
                                String course = jsonObjectData.getString(Constants.course);
                                String title = jsonObjectData.getString(Constants.title);
                                String notice = jsonObjectData.getString(Constants.notice);
                                String created_at = jsonObjectData.getString(Constants.created_at);

                                String date_before = created_at;
                                String date_after = formateDateFromstring("yyyy-MM-dd HH:mm:ss", "dd, MMM yyyy", date_before);

                                noticeCenterItemArrayList.add(new NoticeCenterItem(notice_id,notice,date_after));
                            }

                            noticeCenterListAdapter = new NoticeCenterListAdapter(getActivity(),noticeCenterItemArrayList);
                            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                            linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
                            activity_notification_recycler_view.setLayoutManager(linearLayoutManager);
                            activity_notification_recycler_view.setAdapter(noticeCenterListAdapter);

                            swipeRefreshLayout.setRefreshing(false);
                            activity_notification_recycler_view.setEnabled(true);

                            dialog.dismiss();
                        }
                        else
                        {
                            swipeRefreshLayout.setRefreshing(false);
                            activity_notification_recycler_view.setEnabled(true);

                            Toast.makeText(getActivity(), getResources().getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                        activity_notification_recycler_view.setEnabled(true);

                        dialog.dismiss();
                        Toast.makeText(getActivity(), getResources().getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e)
                {
                    swipeRefreshLayout.setRefreshing(false);
                    activity_notification_recycler_view.setEnabled(true);

                    dialog.dismiss();
                    Log.d("getNotice",e.toString());
                }

            }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                swipeRefreshLayout.setRefreshing(false);
                activity_notification_recycler_view.setEnabled(true);
                dialog.dismiss();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                final SharedPreferences sharedPreferencesRemember = PreferenceManager.getDefaultSharedPreferences(getActivity());

                HashMap<String,String> data = new HashMap<>();
                data.put(Constants.ismobile, Constants.isMobileValue);
                data.put(Constants.course, sharedPreferencesRemember.getString(Constants.sharedPreferenceCourse, ""));
                Log.d("getNotice",data.toString());
                return data;
            }
        };
        Controller.getInstance().addToRequestQueue(quizQuestionRequest, quizQuestion);

        return  false;
    }

    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate){

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {

        }
        return outputDate;
    }
}
