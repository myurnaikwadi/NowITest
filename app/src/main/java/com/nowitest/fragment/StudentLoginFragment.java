package com.nowitest.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.nowitest.activity.HomeActivity;
import com.nowitest.databasehelper.DatabaseHelper;
import com.nowitest.model.FlashcardItem;
import com.nowitest.model.FlashcardPdfItem;
import com.nowitest.model.PdfItem;
import com.nowitest.model.SubjectItem;
import com.nowitest.model.SyllabusItem;
import com.nowitest.model.SyllabusPdfItem;
import com.nowitest.network.InternetConnection;
import com.nowitest.util.Constants;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudentLoginFragment extends Fragment
{

    private Button activity_student_login_button_login;
    private EditText activity_student_login_edittext_username,activity_student_login_edittext_password;
    private InternetConnection internetConnection = new InternetConnection();
    SharedPreferences sharedPreferencesRemember;
    private DatabaseHelper databaseHelper;
    DownloadListener listener = new DownloadListener();
    ArrayList<Integer> downloadIds = new ArrayList<Integer>();
    ThinDownloadManager downloadManager;
//    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_student_login, container, false);

        findViewByIds(rootView);

        sharedPreferencesRemember = PreferenceManager.getDefaultSharedPreferences(getActivity());
        databaseHelper = new DatabaseHelper(getActivity());
        downloadIds = new ArrayList<Integer>();
        downloadManager = new ThinDownloadManager(Constants.DOWNLOAD_THREAD_POOL_SIZE);

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
        final String url = Constants.SERVER_URL+Constants.signIn;

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

                        String student_id = jsonObjectData.getString(Constants.student_id);
                        String first_name = jsonObjectData.getString(Constants.first_name);
                        String last_name = jsonObjectData.getString(Constants.last_name);
                        String course = jsonObjectData.getString(Constants.course);

                        databaseHelper.deleteAllSubject();
                        databaseHelper.deleteAllSubjectPdf();
                        databaseHelper.deleteAllSyllabus();
                        databaseHelper.deleteAllSyllabusPdf();
                        databaseHelper.deleteAllFlashcard();
                        databaseHelper.deleteAllFlashcardPdf();

                        JSONArray jsonArraySubjects = jsonObjectData.getJSONArray(Constants.subjects);
                        if(jsonArraySubjects.length() > 0){

                            for(int i = 0; i< jsonArraySubjects.length(); i++){
                                JSONObject jsonObjectSubjects = jsonArraySubjects.getJSONObject(i);
                                String sub_id = jsonObjectSubjects.getString(Constants.sub_id);
                                String subject_name = jsonObjectSubjects.getString(Constants.subject_name);

                                SubjectItem subjectItem= new SubjectItem(sub_id, subject_name);
                                databaseHelper.addInToSubject(subjectItem);

                                JSONArray jsonArrayPdf = jsonObjectSubjects.getJSONArray(Constants.pdf);
                                if(jsonArrayPdf.length() > 0){

                                    for(int j = 0; j< jsonArrayPdf.length(); j++){
                                        JSONObject jsonObjectPdf = jsonArrayPdf.getJSONObject(j);
                                        String pdf_id = jsonObjectPdf.getString(Constants.pdf_id);
                                        String subject_id = jsonObjectPdf.getString(Constants.subject_id);
                                        String syllabus = jsonObjectPdf.getString(Constants.syllabus);
                                        String file_name = jsonObjectPdf.getString(Constants.file_name);

                                        PdfItem pdfItem= new PdfItem(subject_id, pdf_id, syllabus, file_name);
                                        databaseHelper.addInToSubjectPdf(pdfItem);

                                        final String pdfPath = Constants.createPDFFolder(Constants.Foldersyllabus);
                                        String targetFileName = pdfPath + "/" +file_name;

                                        System.out.println("File details: " + pdfPath + " " + targetFileName);

                                        Uri downloadUri = Uri.parse(syllabus);
                                        Uri destinationUri = Uri.parse(targetFileName);
                                        final DownloadRequest request =
                                                new DownloadRequest(downloadUri)
                                                        .setDestinationURI(destinationUri)
                                                        .setPriority(DownloadRequest.Priority.HIGH)
                                                        .setDownloadListener(listener);
                                        downloadIds.add(downloadManager.add(request));

//                                        downloadAndOpenPDF(syllabus, targetFileName);
                                    }
                                }
                            }
                        }

                        JSONArray jsonArraySyllabus = jsonObjectData.getJSONArray(Constants.syllabus);
                        if(jsonArraySyllabus.length() > 0) {

                            for (int i = 0; i < jsonArraySyllabus.length(); i++) {
                                JSONObject jsonObjectSyllabus = jsonArraySyllabus.getJSONObject(i);
                                String syllabus_id = jsonObjectSyllabus.getString(Constants.syllabus_id);
                                String title = jsonObjectSyllabus.getString(Constants.title);

                                SyllabusItem syllabusItem= new SyllabusItem(syllabus_id, title);
                                databaseHelper.addInToSyllabus(syllabusItem);

                                JSONArray jsonArrayPdf = jsonObjectSyllabus.getJSONArray(Constants.pdf);
                                if(jsonArrayPdf.length() > 0){

                                    for(int j = 0; j< jsonArrayPdf.length(); j++){
                                        JSONObject jsonObjectPdf = jsonArrayPdf.getJSONObject(j);
                                        String id = jsonObjectPdf.getString(Constants.id);
                                        String file_name = jsonObjectPdf.getString(Constants.file_name);
                                        String path = jsonObjectPdf.getString(Constants.path);

                                        SyllabusPdfItem syllabusPdfItem= new SyllabusPdfItem(id, syllabus_id, file_name, path);
                                        databaseHelper.addInToSyllabusPdf(syllabusPdfItem);

                                        final String pdfPath = Constants.createPDFFolder(Constants.Foldersyllabus);
                                        String targetFileName = pdfPath + "/" +file_name;

                                        System.out.println("File details: " + pdfPath + " " + targetFileName);

                                        Uri downloadUri = Uri.parse(path);
                                        Uri destinationUri = Uri.parse(targetFileName);
                                        final DownloadRequest request =
                                                new DownloadRequest(downloadUri)
                                                        .setDestinationURI(destinationUri)
                                                        .setPriority(DownloadRequest.Priority.HIGH)
                                                        .setDownloadListener(listener);
                                        downloadIds.add(downloadManager.add(request));
                                    }
                                }
                            }
                        }


                        JSONArray jsonArrayFlashcard = jsonObjectData.getJSONArray(Constants.flashcard);
                        if(jsonArrayFlashcard.length() > 0) {

                            for (int i = 0; i < jsonArrayFlashcard.length(); i++) {
                                JSONObject jsonObjectFlashcard = jsonArrayFlashcard.getJSONObject(i);
                                String flashcard_id = jsonObjectFlashcard.getString(Constants.flashcard_id);
                                String title = jsonObjectFlashcard.getString(Constants.title);

                                FlashcardItem syllabusItem= new FlashcardItem(flashcard_id, title);
                                databaseHelper.addInToFlashcard(syllabusItem);

                                JSONArray jsonArrayPdf = jsonObjectFlashcard.getJSONArray(Constants.pdf);
                                if(jsonArrayPdf.length() > 0){

                                    for(int j = 0; j< jsonArrayPdf.length(); j++){
                                        JSONObject jsonObjectPdf = jsonArrayPdf.getJSONObject(j);
                                        String id = jsonObjectPdf.getString(Constants.id);
                                        String file_name = jsonObjectPdf.getString(Constants.file_name);
                                        String path = jsonObjectPdf.getString(Constants.path);

                                        FlashcardPdfItem syllabusPdfItem= new FlashcardPdfItem(id, flashcard_id, file_name, path);
                                        databaseHelper.addInToFlashcardPdf(syllabusPdfItem);

                                        final String pdfPath = Constants.createPDFFolder(Constants.Foldersyllabus);
                                        String targetFileName = pdfPath + "/" +file_name;

                                        System.out.println("File details: " + pdfPath + " " + targetFileName);

                                        Uri downloadUri = Uri.parse(path);
                                        Uri destinationUri = Uri.parse(targetFileName);
                                        final DownloadRequest request =
                                                new DownloadRequest(downloadUri)
                                                        .setDestinationURI(destinationUri)
                                                        .setPriority(DownloadRequest.Priority.HIGH)
                                                        .setDownloadListener(listener);
                                        downloadIds.add(downloadManager.add(request));
                                    }
                                }
                            }
                        }

                        SharedPreferences.Editor editor = sharedPreferencesRemember.edit();
                        editor.putString(Constants.sharedPreferenceUserId, student_id);
                        editor.putString(Constants.sharedPreferenceFirstName, first_name);
                        editor.putString(Constants.sharedPreferenceLastName, last_name);
                        editor.putString(Constants.sharedPreferenceCourse, course);
                        editor.putString(Constants.sharedPreferenceUserType, "");
                        editor.commit();

                        dialog.dismiss();

                        getActivity().finish();
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(0,0);
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
                data.put(Constants.email, username);
                data.put(Constants.password, password);
                Log.d("loginUser",data.toString());
                return data;
            }
        };
        Controller.getInstance().addToRequestQueue(quizQuestionRequest, quizQuestion);

        return  false;
    }

    class DownloadListener implements DownloadStatusListener {

        @Override
        public void onDownloadComplete(int i) {
            downloadCompleted(i);
        }

        private void downloadCompleted(int i) {
            int indexToRemove = -1;
            for (int index = 0; index < downloadIds.size(); index++) {
                if (downloadIds.get(index) == i) {
                    indexToRemove = index;
                    break;
                }
            }
            if (indexToRemove != -1) {
                downloadIds.remove(indexToRemove);
            }
        }

        @Override
        public void onDownloadFailed(int i, int i2, String s) {
            downloadCompleted(i);
        }

        @Override
        public void onProgress(int i, long l, long l2, int i2) {

        }
    }
}
