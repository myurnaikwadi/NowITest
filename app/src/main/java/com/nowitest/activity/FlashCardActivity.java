package com.nowitest.activity;

//import android.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nowitest.Controller;
import com.nowitest.R;
import com.nowitest.adapter.FlashcardListAdapter;
import com.nowitest.databasehelper.DatabaseHelper;
import com.nowitest.model.FlashcardItem;
import com.nowitest.model.FlashcardPdfItem;
import com.nowitest.model.PdfItem;
import com.nowitest.model.SubjectItem;
import com.nowitest.model.SyllabusItem;
import com.nowitest.model.SyllabusPdfItem;
import com.nowitest.network.InternetConnection;
import com.nowitest.util.Constants;
import com.nowitest.util.RecyclerItemClickListener;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FlashCardActivity extends ParentActivity {

    private RecyclerView activity_notification_recycler_view;
    private FlashcardListAdapter flashcardListAdapter;
    private ArrayList<FlashcardItem> flashcardItems;
    private InternetConnection internetConnection = new InternetConnection();
    SharedPreferences sharedPreferencesRemember;
    private DatabaseHelper databaseHelper;
    DownloadListener listener = new DownloadListener();
    ArrayList<Integer> downloadIds = new ArrayList<Integer>();
    ThinDownloadManager downloadManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        downloadIds = new ArrayList<Integer>();
        downloadManager = new ThinDownloadManager(Constants.DOWNLOAD_THREAD_POOL_SIZE);

        setActionBarCustomWithoutBack(getString(R.string.actionbarTitleFlashCard));
        findViewByIds();

        flashcardItems = new ArrayList<FlashcardItem>();

        activity_notification_recycler_view.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {

                Intent intent = new Intent(getApplicationContext(), FlashcardPdfActivity.class);
                intent.putExtra(Constants.flashcard_id, flashcardItems.get(position).getFlashcard_id());
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        }));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (internetConnection.isNetworkAvailable(getApplicationContext())) {
                    swipeRefreshLayout.setRefreshing(true);
                    activity_notification_recycler_view.setEnabled(false);
                    syncData();

                } else {
                    activity_notification_recycler_view.setEnabled(true);
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void findViewByIds(){
        activity_notification_recycler_view = (RecyclerView) findViewById(R.id.activity_notification_recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();

        sharedPreferencesRemember = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        databaseHelper = new DatabaseHelper(getApplicationContext());

        flashcardItems.clear();
        flashcardItems = databaseHelper.getAllFlashcard();

        flashcardListAdapter = new FlashcardListAdapter(getApplicationContext(),flashcardItems);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
        activity_notification_recycler_view.setLayoutManager(linearLayoutManager);
        activity_notification_recycler_view.setAdapter(flashcardListAdapter);
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
        final Menu mMenu = menu;
        final MenuItem item = menu.findItem(R.id.action_custom_button_sync);
        item.getActionView().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (internetConnection.isNetworkAvailable(getApplicationContext())) {
                    syncData();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public boolean syncData()
    {
        swipeRefreshLayout.setRefreshing(true);

        String quizQuestion = "syncData";
        final String url = Constants.SERVER_URL+Constants.syncPDF;

        final ProgressDialog dialog = new ProgressDialog(FlashCardActivity.this);
        dialog.setMessage(getResources().getString(R.string.please_wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Log.d("syncData", url);

        StringRequest quizQuestionRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s)
            {
                Log.d("syncData",s);
                try
                {
                    JSONObject response = new JSONObject(s);

                    if (response.getString(Constants.status).equalsIgnoreCase(Constants.status200))
                    {
//                        JSONArray jsonArrayData = response.getJSONArray(Constants.data);
                        JSONObject jsonObjectData = response.getJSONObject(Constants.data);

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

                        flashcardItems.clear();
                        flashcardItems = databaseHelper.getAllFlashcard();

                        flashcardListAdapter = new FlashcardListAdapter(getApplicationContext(),flashcardItems);
                        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                        linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
                        activity_notification_recycler_view.setLayoutManager(linearLayoutManager);
                        activity_notification_recycler_view.setAdapter(flashcardListAdapter);

                        swipeRefreshLayout.setRefreshing(false);
                        activity_notification_recycler_view.setEnabled(true);

                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.sync_success), Toast.LENGTH_SHORT).show();

                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                        activity_notification_recycler_view.setEnabled(true);
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), response.getString(Constants.message), Toast.LENGTH_SHORT).show();
                    }

                }
                catch (JSONException e)
                {
                    swipeRefreshLayout.setRefreshing(false);
                    activity_notification_recycler_view.setEnabled(true);
                    dialog.dismiss();
                    Log.d("syncData",e.toString());
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
                final SharedPreferences sharedPreferencesRemember = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                HashMap<String,String> data = new HashMap<>();
                data.put(Constants.ismobile, Constants.isMobileValue);
                data.put(Constants.course, sharedPreferencesRemember.getString(Constants.sharedPreferenceCourse, ""));

                Log.d("syncData",data.toString());
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
