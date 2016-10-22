package com.nowitest.activity;

//import android.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.nowitest.adapter.FlashcardPdfListAdapter;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FlashcardPdfActivity extends ParentActivity {

    private RecyclerView activity_notification_recycler_view;
    private FlashcardPdfListAdapter flashcardPdfListAdapter;
    private ArrayList<FlashcardPdfItem> flashcardPdfItemArrayList;
    private InternetConnection internetConnection = new InternetConnection();
    SharedPreferences sharedPreferencesRemember;
    private String intentSubjectId;
    ArrayList<Integer> downloadIds = new ArrayList<Integer>();
    ThinDownloadManager downloadManager;
    DownloadListener listener = new DownloadListener();
    int downloadedSize = 0, totalsize;
    float per = 0;
    private ProgressDialog progressDialog;
    DatabaseHelper databaseHelper;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        progressDialog = new ProgressDialog(FlashcardPdfActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);

        flashcardPdfItemArrayList = new ArrayList<FlashcardPdfItem>();

        downloadIds = new ArrayList<Integer>();
        downloadManager = new ThinDownloadManager(Constants.DOWNLOAD_THREAD_POOL_SIZE);

        Intent intent = getIntent();
        intentSubjectId = intent.getStringExtra(Constants.flashcard_id);

        setActionBarCustomWithoutBack(getString(R.string.actionbarTitlepdf));
        findViewByIds();

        activity_notification_recycler_view.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {

                final String pdfPath = Constants.createPDFFolder(Constants.Foldersyllabus);
                String targetFileName = pdfPath + "/" + flashcardPdfItemArrayList.get(position).getFile_name();

                System.out.println("File details: " + pdfPath + " " + targetFileName);

                if (Constants.fileExistsOrNot(targetFileName) == true) {

                    File myFile = new File(targetFileName);
                    try {
                        FileOpen.openFile(getApplicationContext(), myFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                    System.out.println("Image name: " + flashcardPdfItemArrayList.get(position).getFile_name());
//                    String[] splittedString = flashcardPdfItemArrayList.get(position).getFile_name().split("\\.");
//                    System.out.println("Image name: " + splittedString.length);
//                    if (splittedString[1].equalsIgnoreCase("pdf")) {
//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setDataAndType(path, "application/pdf");
//                        startActivity(intent);
//                    }

                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.file_not_downloaded_yet), Toast.LENGTH_SHORT).show();
//                    downloadAndOpenPDF(flashcardPdfItemArrayList.get(position).getPath(), targetFileName);
                }
//                Uri downloadUri = Uri.parse(pdfItemArrayList.get(position).getPdfUrl());
//
//                String targetFileName = pdfPath + "/" +pdfItemArrayList.get(position).getFileName() ;
//                Uri destinationUri = Uri.parse(targetFileName);
//                final DownloadRequest request =
//                        new DownloadRequest(downloadUri)
//                                .setDestinationURI(destinationUri)
//                                .setPriority(DownloadRequest.Priority.HIGH)
//                                .setDownloadListener(listener);
//                downloadIds.add(downloadManager.add(request));
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

    private void findViewByIds(){
        activity_notification_recycler_view = (RecyclerView) findViewById(R.id.activity_notification_recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();

        sharedPreferencesRemember = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        databaseHelper = new DatabaseHelper(getApplicationContext());

        flashcardPdfItemArrayList.clear();
        flashcardPdfItemArrayList = databaseHelper.getFlashcardPdf(intentSubjectId);

        flashcardPdfListAdapter = new FlashcardPdfListAdapter(getApplicationContext(),flashcardPdfItemArrayList);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
        activity_notification_recycler_view.setLayoutManager(linearLayoutManager);
        activity_notification_recycler_view.setAdapter(flashcardPdfListAdapter);

//        if(internetConnection.isNetworkAvailable(getApplicationContext())){
//            getCourseSubList();
//        }else{
//            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
//        }

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
                if(internetConnection.isNetworkAvailable(getApplicationContext())){
                    syncData();
                }else{
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

        final ProgressDialog dialog = new ProgressDialog(FlashcardPdfActivity.this);
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

                        flashcardPdfItemArrayList.clear();
                        flashcardPdfItemArrayList = databaseHelper.getFlashcardPdf(intentSubjectId);

                        flashcardPdfListAdapter = new FlashcardPdfListAdapter(getApplicationContext(),flashcardPdfItemArrayList);
                        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                        linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
                        activity_notification_recycler_view.setLayoutManager(linearLayoutManager);
                        activity_notification_recycler_view.setAdapter(flashcardPdfListAdapter);

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


    public static class FileOpen {

        public static void openFile(Context context, File url) throws IOException {
            // Create URI
            File file=url;
            Uri uri = Uri.fromFile(file);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            // Check what kind of file you are trying to open, by comparing the url with extensions.
            // When the if condition is matched, plugin sets the correct intent (mime) type,
            // so Android knew what application to use to open the file
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if(url.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if(url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            }else if(url.toString().contains(".pps") || url.toString().contains(".ppsx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            }
            else if(url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if(url.toString().contains(".zip") || url.toString().contains(".rar")) {
                // WAV audio file
                intent.setDataAndType(uri, "application/x-wav");
            } else if(url.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if(url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if(url.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if(url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if(url.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if(url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else {
                //if you want you can also define the intent type for any other file

                //additionally use else clause below, to manage other unknown extensions
                //in this case, Android will show all applications installed on the device
                //so you can choose which application to use
                intent.setDataAndType(uri, "*/*");
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
