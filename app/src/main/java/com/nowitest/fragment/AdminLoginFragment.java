package com.nowitest.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nowitest.Controller;
import com.nowitest.R;
import com.nowitest.activity.AdminHomeActivity;
import com.nowitest.network.InternetConnection;
import com.nowitest.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AdminLoginFragment extends Fragment
{

    private Button activity_student_login_button_login;
    SharedPreferences sharedPreferencesRemember;
    private EditText activity_student_login_edittext_username,activity_student_login_edittext_password;
    private InternetConnection internetConnection = new InternetConnection();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_student_login, container, false);
        findViewByIds(rootView);

        sharedPreferencesRemember = PreferenceManager.getDefaultSharedPreferences(getActivity());

        activity_student_login_button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateForm()) {
                    if (internetConnection.isNetworkAvailable(getActivity())) {
                        loginUser(activity_student_login_edittext_username.getText().toString(), activity_student_login_edittext_password.getText().toString());
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return rootView;
    }

    private void findViewByIds(View rootView)
    {
        activity_student_login_button_login = (Button) rootView.findViewById(R.id.activity_student_login_button_login);
        activity_student_login_edittext_username = (EditText) rootView.findViewById(R.id.activity_student_login_edittext_username);
        activity_student_login_edittext_password = (EditText) rootView.findViewById(R.id.activity_student_login_edittext_password);
    }

    private boolean validateForm(){
        if(activity_student_login_edittext_username.getText().toString().trim().equalsIgnoreCase("")){
            activity_student_login_edittext_username.setError(getResources().getString(R.string.error_username));
            return false;
        }if(activity_student_login_edittext_password.getText().toString().trim().equalsIgnoreCase("")){
            activity_student_login_edittext_password.setError(getResources().getString(R.string.error_password));
            return false;
        }if(!activity_student_login_edittext_username.getText().toString().matches(Constants.emailPattern)){
            activity_student_login_edittext_username.setError(getResources().getString(R.string.error_invalid_email));
            return false;
        }
        return true;
    }

    public boolean loginUser(final String username, final String password)
    {
        String quizQuestion = "loginUser";
        final String url = Constants.SERVER_URL+Constants.signin;

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getResources().getString(R.string.please_wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Log.d("loginUser", url);

        StringRequest quizQuestionRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s)
            {
                Log.d("loginUser",s);
                try
                {
                    JSONObject response = new JSONObject(s);

                    if (response.getString(Constants.status).equalsIgnoreCase(Constants.status200))
                    {
                        JSONArray jsonArrayData = response.getJSONArray(Constants.data);
                        JSONObject jsonObjectData = jsonArrayData.getJSONObject(0);

                        String id = jsonObjectData.getString(Constants.id);
                        String name = jsonObjectData.getString(Constants.name);
                        String user_type = jsonObjectData.getString(Constants.user_type);

                        SharedPreferences.Editor editor = sharedPreferencesRemember.edit();
                        editor.putString(Constants.sharedPreferenceUserId, id);
                        editor.putString(Constants.sharedPreferenceFirstName, name);
                        editor.putString(Constants.sharedPreferenceUserType, user_type);
                        editor.commit();

                        dialog.dismiss();

                        getActivity().finish();
                        Intent intent = new Intent(getActivity(), AdminHomeActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(0, 0);
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), response.getString(Constants.message), Toast.LENGTH_SHORT).show();
                    }

                }
                catch (JSONException e)
                {
                    dialog.dismiss();
                    Log.d("loginUser",e.toString());
                }

            }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                dialog.dismiss();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> data = new HashMap<>();
                data.put(Constants.ismobile, Constants.isMobileValue);
                data.put(Constants.username, username);
                data.put(Constants.password, password);
                Log.d("loginUser",data.toString());
                return data;
            }
        };
        Controller.getInstance().addToRequestQueue(quizQuestionRequest, quizQuestion);

        return  false;
    }
}
