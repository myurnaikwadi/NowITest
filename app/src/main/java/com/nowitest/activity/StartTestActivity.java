package com.nowitest.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nowitest.Controller;
import com.nowitest.R;
import com.nowitest.databasehelper.DatabaseHelper;
import com.nowitest.model.ChoiceItem;
import com.nowitest.model.QuestionItem;
import com.nowitest.model.TestItem;
import com.nowitest.network.InternetConnection;
import com.nowitest.util.Constants;
import com.squareup.picasso.Picasso;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pushkar Tamhane on 20/8/16.
 */
public class StartTestActivity extends ParentActivity {

    public static ArrayList<QuestionItem> questionItems;
    private ArrayList<ChoiceItem> choiceItems;
    private ArrayList<ChoiceItem> choiceItemsAll;
    public static HashMap<String,ArrayList<ChoiceItem>> map;
    private int totalQuestion = 0;
    private int currentQuestion = 0;
//    private RelativeLayout outerQuizz;
    private Boolean flagForHomePress=false;
    private TextView activity_quizz_textView_question;
    private ImageView activity_quizz_imageview_question;
    private LinearLayout dynamicCheckBox;
    private int noOfQuestionAtt=0;
    private Button activity_start_test_button_save_next;
    private ScrollView activity_start_test_scrollview;
    public static int result=0;
    DatabaseHelper databaseHelper;
    private String intentTestId, intentTestName, intentTestTime, intentCourse, intentSubject, intentTestCode, intentTotalQuestions, intentMarks;
    private InternetConnection internetConnection = new InternetConnection();
    private CounterClass timer;
    private TextView activity_quizz_textView_actionbar_title, activity_quizz_textView_actionbar_timing, activity_quizz_textView_question_no;
    private ArrayList<String> totalTimeTakenForQuestion;
    private JSONObject jsonObject= new JSONObject();
    private JSONArray jsonArrayQuestion = new JSONArray();
    private String oldTimeTaken="";
    boolean isImageFitToScreen = false;
    long actualTime;
    DownloadListener listener = new DownloadListener();
    ArrayList<Integer> downloadIds = new ArrayList<Integer>();
    ThinDownloadManager downloadManager;
    ZoomControls zoom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_test);

        downloadIds = new ArrayList<Integer>();
        downloadManager = new ThinDownloadManager(Constants.DOWNLOAD_THREAD_POOL_SIZE);

        Intent intent = getIntent();
        intentTestId = intent.getStringExtra(Constants.test_id);
        intentTestName = intent.getStringExtra(Constants.title);
        intentTestTime = intent.getStringExtra(Constants.test_time);
        intentCourse = intent.getStringExtra(Constants.course);
        intentSubject = intent.getStringExtra(Constants.subject);
        intentTestCode = intent.getStringExtra(Constants.code);
        intentTotalQuestions = intent.getStringExtra(Constants.total_questions);
        intentMarks = intent.getStringExtra(Constants.total_marks);

        totalTimeTakenForQuestion = new ArrayList<String>();

        String[] splittedTime = intentTestTime.split(" ");
        actualTime = TimeUnit.MINUTES.toMillis(Long.parseLong(splittedTime[0]));
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(actualTime), TimeUnit.MILLISECONDS.toMinutes(actualTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(actualTime)), TimeUnit.MILLISECONDS.toSeconds(actualTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(actualTime)));
        oldTimeTaken = hms;
        timer = new CounterClass(actualTime,1000);
        findViewByIds();

        activity_quizz_textView_actionbar_title.setText(intentTestName);

        if(internetConnection.isNetworkAvailable(getApplicationContext())){
            quizQuestion();
        }else{
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }


        activity_start_test_button_save_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    String[] splittedTime = activity_quizz_textView_actionbar_timing.getText().toString().split(" ");

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                    Date date1 = simpleDateFormat.parse(oldTimeTaken);
                    Date date2 = simpleDateFormat.parse(splittedTime[2]);

                    oldTimeTaken = splittedTime[2];

                    long difference = date1.getTime() - date2.getTime();
                    int days = (int) (difference / (1000 * 60 * 60 * 24));
                    int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
                    int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
                    int timeInSeconds = (int) (difference / 1000);

                    timeInSeconds = timeInSeconds - (hours * 3600);
                    timeInSeconds = timeInSeconds - (min * 60);
                    int seconds = timeInSeconds;
//                        hours = (hours < 0 ? -hours : hours);
                    Log.i("======= Hours", " :: " + hours);
                    System.out.println("Time taken: " + (hours + ":" + min + ":" + seconds));

                    totalTimeTakenForQuestion.add((hours + ":" + min + ":" + seconds));
                } catch (Exception e) {
                    System.out.println("Exception time: " + e);
                }

                if (currentQuestion == (questionItems.size() - 1)) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(StartTestActivity.this);

                    builder.setMessage(getResources().getString(R.string.Are_you_sure));

                    builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
                            timer.cancel();
                            result = generateResult();
                            addPracticeTest();
                            dialog.dismiss();
//                            submitResult(result);

                            if (internetConnection.isNetworkAvailable(getApplicationContext())) {
                                submitTest();
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                            }
                        }

                    });

                    builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    if (alert.isShowing()) {

                    } else {
                        alert.show();
                    }
                } else {
                    activity_start_test_scrollview.scrollTo(0, 0);
                    noOfQuestionAtt++;
                    currentQuestion = currentQuestion + 1;
                    setQuestion(currentQuestion);
                }
            }
        });

        activity_quizz_imageview_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("Image path****: " + Constants.SERVER_URL_IMAGE + questionItems.get(currentQuestion).getQuestionFile());
                System.out.println("Image path********* " + questionItems.get(currentQuestion).getQuestion());
                System.out.println("Position: " + currentQuestion);

                Intent intent = new Intent(getApplicationContext(), FullScreenImageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.imagepath, Constants.SERVER_URL_IMAGE+questionItems.get(currentQuestion).getQuestionFile());
                startActivity(intent);

//                final String questionImagePath = Constants.createPDFFolder(Constants.FolderquestionImages);
//
//                final Dialog settingsDialog = new Dialog(StartTestActivity.this);
//
//                LayoutInflater inflater = getLayoutInflater();
//                View newView = (View) inflater.inflate(R.layout.activity_fullsize_imageview, null);
//                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//                settingsDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//                settingsDialog.setContentView(newView);
//                settingsDialog.setCanceledOnTouchOutside(false);
//
//
//                final ImageView activity_fullsize_imageview = (ImageView) newView.findViewById(R.id.activity_fullsize_imageview);
//                ImageView activity_fullsize_imageview_close = (ImageView) newView.findViewById(R.id.activity_fullsize_imageview_close);
////                ZoomControls zoomControls1 = (ZoomControls) newView.findViewById(R.id.zoomControls1);
//
//                String imagePath = Constants.SERVER_URL_IMAGE + questionItems.get(currentQuestion).getQuestionFile();
//
//                if (imagePath.equalsIgnoreCase("")) {
//                } else {
//                    System.out.println("Image path****: " + imagePath);
//
//                    if (imagePath.contains(questionImagePath)) {
//                        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
//                        activity_fullsize_imageview.setImageBitmap(bmp);
//                    } else {
//                        Picasso.with(getApplicationContext()).load(imagePath).placeholder(R.drawable.ic_launcher).into(activity_fullsize_imageview);
//                    }
//                }
//
//                activity_fullsize_imageview_close.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        settingsDialog.dismiss();
//                    }
//                });
//
//                settingsDialog.show();


//                zoom.setOnZoomInClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        // TODO Auto-generated method stub
//
//                        float x = activity_fullsize_imageview.getScaleX();
//                        float y = activity_fullsize_imageview.getScaleY();
//
//                        activity_fullsize_imageview.setScaleX((float) (x + 1));
//                        activity_fullsize_imageview.setScaleY((float) (y + 1));
//                    }
//                });
//
//                zoom.setOnZoomOutClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        // TODO Auto-generated method stub
//
//
//                        float x = activity_fullsize_imageview.getScaleX();
//                        float y = activity_fullsize_imageview.getScaleY();
//
//                        activity_fullsize_imageview.setScaleX((float) (x - 1));
//                        activity_fullsize_imageview.setScaleY((float) (y - 1));
//                    }
//                });
            }
        });



    }

    private void findViewByIds(){
        activity_quizz_textView_question = (TextView) findViewById(R.id.activity_quizz_textView_question);
        activity_quizz_textView_question_no = (TextView) findViewById(R.id.activity_quizz_textView_question_no);
        activity_quizz_imageview_question = (ImageView) findViewById(R.id.activity_quizz_imageview_question);
        activity_quizz_textView_actionbar_timing = (TextView) findViewById(R.id.activity_quizz_textView_actionbar_timing);
        activity_quizz_textView_actionbar_title = (TextView) findViewById(R.id.activity_quizz_textView_actionbar_title);
        dynamicCheckBox= (LinearLayout) findViewById(R.id.dynamicCheckBox);
        activity_start_test_button_save_next= (Button) findViewById(R.id.activity_start_test_button_save_next);
        activity_start_test_scrollview= (ScrollView) findViewById(R.id.activity_start_test_scrollview);

//        outerQuizz = (RelativeLayout) findViewById(R.id.outerQuizz);
    }

    public int generateResult()
    {
        int count = 0;
        try {
//            jsonObject.put(Constants.test_id, intentTestId);
//            jsonObject.put(Constants.sharedPreferenceUserId, sharedPreferencesRemember.getString(Constants.sharedPreferenceUserId, ""));

            System.out.println("JSON***:" + questionItems.size());

            if(totalTimeTakenForQuestion.size() == 0){
                for (int i =0 ; i < questionItems.size(); i++){
                    totalTimeTakenForQuestion.add(("00:00:00"));
                }
            }else if(totalTimeTakenForQuestion.size() < questionItems.size()){
                for (int i = currentQuestion ; i < questionItems.size(); i++){
                    totalTimeTakenForQuestion.add(("00:00:00"));
                }
            }

            JSONArray questionJsonArray = new JSONArray();

            for (int j = 0; j < questionItems.size(); j++) {
                String trueAns = "";
                String userAns = "";
                String correctAnsId = "";
                String userAnsId = "";

                JSONObject jsonObjectQuestion = new JSONObject();
                jsonObjectQuestion.put(Constants.question_id, questionItems.get(j).getQuestionId());
                jsonObjectQuestion.put(Constants.question_marks, questionItems.get(j).getMarks());
                jsonObjectQuestion.put(Constants.questionNo, questionItems.get(j).getQuestionNo());
                jsonObjectQuestion.put(Constants.time_taken, totalTimeTakenForQuestion.get(j));

                for (int i = 0; i < map.get(questionItems.get(j).getQuestionId()).size(); i++) {
                    if (map.get(questionItems.get(j).getQuestionId()).get(i).getIsRight().equalsIgnoreCase("1")) {
                        trueAns = trueAns + map.get(questionItems.get(j).getQuestionId()).get(i).getChoice() + " , ";
                        correctAnsId = correctAnsId + map.get(questionItems.get(j).getQuestionId()).get(i).getChoiceId();
                        //if(i != (map.get(questionItems.get(j).getQuestionId()).size() -1)){
                            correctAnsId = correctAnsId + ",";
                        //}
                    }
                    if (map.get(questionItems.get(j).getQuestionId()).get(i).getUserChoice().equalsIgnoreCase("1")) {
                        userAns = userAns + map.get(questionItems.get(j).getQuestionId()).get(i).getChoice() + " , ";
                        userAnsId = userAnsId + map.get(questionItems.get(j).getQuestionId()).get(i).getChoiceId();
                        //if(i != (map.get(questionItems.get(j).getQuestionId()).size() -1)){
                            userAnsId = userAnsId + ",";
                        //}

                        for(int k = 0; k < choiceItemsAll.size(); k++){
                            if(choiceItemsAll.get(k).getChoiceId().equalsIgnoreCase(map.get(questionItems.get(j).getQuestionId()).get(i).getChoiceId())){
                                if(map.get(questionItems.get(j).getQuestionId()).get(i).getUserChoice().equalsIgnoreCase("1")){
                                    choiceItemsAll.get(k).setUserChoice("1");
                                }
                            }
//                            int indexOfChoideId = choiceItemsAll.get(k).getChoiceId().indexOf(map.get(questionItems.get(j).getQuestionId()).get(i).getChoiceId());
//                            choiceItemsAll.get(indexOfChoideId).setUserChoice("1");
                        }
                    }else{
                        for(int k = 0; k < choiceItemsAll.size(); k++){
                            if(choiceItemsAll.get(k).getChoiceId().equalsIgnoreCase(map.get(questionItems.get(j).getQuestionId()).get(i).getChoiceId())){
                                if(map.get(questionItems.get(j).getQuestionId()).get(i).getUserChoice().equalsIgnoreCase("0")){
                                    choiceItemsAll.get(k).setUserChoice("0");
                                }
                            }
//                            int indexOfChoideId = choiceItemsAll.get(k).getChoiceId().indexOf(map.get(questionItems.get(j).getQuestionId()).get(i).getChoiceId());
//                            choiceItemsAll.get(indexOfChoideId).setUserChoice("1");
                        }
                    }
                }

                userAnsId = Constants.replaceString(userAnsId);
                correctAnsId = Constants.replaceString(correctAnsId);

                jsonObjectQuestion.put(Constants.user_choice, userAnsId);
                jsonObjectQuestion.put(Constants.correct_answer, correctAnsId);
                questionJsonArray.put(jsonObjectQuestion);

                if (userAns.equalsIgnoreCase("") || userAns.equalsIgnoreCase(null) || userAns.length() < 1) {
                } else {
                    questionItems.get(j).setAnsGiven(true);
                }

                if (trueAns.equalsIgnoreCase(userAns)) {
                    count = count + 1;
                }
            }

            jsonObject.put(Constants.data, questionJsonArray);
            jsonArrayQuestion = questionJsonArray;

            Log.d("hihiCont", count + "");
        }catch (Exception e){
            System.out.println("JSON Exception: " + e);
        }
        return count;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_start_test, menu);
//        final Menu mMenu = menu;
//        final MenuItem item = menu.findItem(R.id.custom_menu_start_test);
//        final TextView custom_menu_start_test_textview_timer = (TextView) menu.findItem(R.id.custom_menu_start_test_textview_timer);
//        item.getActionView().setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Sync clicked", Toast.LENGTH_SHORT).show();
//            }
//        });
//        global_custom_menu_start_test_textview_timer = custom_menu_start_test_textview_timer;
//        timer.start();
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }


    public boolean quizQuestion()
    {
        String quizQuestion = "quizQuestion";
        final String url = Constants.SERVER_URL+Constants.getQuestions;

        final ProgressDialog dialog = new ProgressDialog(StartTestActivity.this);
        dialog.setMessage(getResources().getString(R.string.please_wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Log.d("hihi", url);

        StringRequest quizQuestionRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s)
            {
                Log.d("hih",s);
                try
                {
                    JSONObject response = new JSONObject(s);

                    if (response.getString(Constants.status).equalsIgnoreCase(Constants.status200))
                    {
                        JSONArray questionArray = response.getJSONArray(Constants.data);

                        if (questionArray.length() > 0) {
                            questionItems = new ArrayList<QuestionItem>();

                            map = new HashMap<String, ArrayList<ChoiceItem>>();
                            choiceItemsAll = new ArrayList<ChoiceItem>();
                            for (int i = 0; i < questionArray.length(); i++) {
                                JSONObject object = questionArray.getJSONObject(i);
                                String question_id = object.getString(Constants.question_id);
                                String question = object.getString(Constants.question);
                                String questionFile = object.getString(Constants.questionFile);
                                String is_file = object.getString(Constants.is_file);
                                String question_marks = object.getString(Constants.question_marks);
                                String questionNo = object.getString(Constants.questionNo);

                                questionItems.add(new QuestionItem(question_id, question, questionFile, false,false, intentTestId, is_file, question_marks, "", questionNo, "0"));

                                JSONArray choiceArray = object.getJSONArray(Constants.options);

                                choiceItems = new ArrayList<ChoiceItem>();

                                for (int j = 0; j < choiceArray.length(); j++) {
                                    JSONObject jsonObject = choiceArray.getJSONObject(j);
                                    String choiceId = jsonObject.getString(Constants.choice_id);
                                    String choice = jsonObject.getString(Constants.choice);
                                    String isRight = jsonObject.getString(Constants.is_right);
                                    String isChoiceFile = jsonObject.getString(Constants.is_file);

                                    choiceItems.add(new ChoiceItem(question_id, choiceId, choice, isRight, "0", isChoiceFile));
                                    choiceItemsAll.add(new ChoiceItem(question_id, choiceId, choice, isRight, "0", isChoiceFile));
                                }
                                map.put(question_id, choiceItems);
                            }

                            dialog.dismiss();
                            totalQuestion = questionItems.size();
                            currentQuestion = 0;
                            Log.d("hihiQ", questionItems.size() + "");
                            Log.d("hihiC", choiceItems.size() + "");
                            Log.d("hihiM", map.size() + "");

                            Collections.shuffle(questionItems);
                            Collections.shuffle(choiceItems);
                            Collections.shuffle(choiceItemsAll);
                            setQuestion(0);
                        }
                        else
                        {
                            dialog.dismiss();
                            flagForHomePress = true;
                        }

                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), response.getString(Constants.message).toString(), Toast.LENGTH_SHORT).show();
                    }

                }
                catch (JSONException e)
                {
                    dialog.dismiss();
                    Log.d("hi_ap_e",e.toString());
                }

            }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                dialog.dismiss();
            }
        }
        ){

            //TODO change limit and topic id dynamically.
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> data = new HashMap<>();
                data.put(Constants.ismobile, Constants.isMobileValue);
                data.put(Constants.test_id, intentTestId);
                Log.d("hihi",data.toString());
                return data;
            }
        };
        Controller.getInstance().addToRequestQueue(quizQuestionRequest, quizQuestion);

        return  false;
    }


    public void setQuestion(final int no)
    {
        activity_quizz_textView_question_no.setText((currentQuestion + 1) + ") ");

        questionItems.get(no).setRandomNo("("+(currentQuestion + 1)+")");

        if(questionItems.get(no).getQuestionFile().equalsIgnoreCase("")){
            activity_quizz_imageview_question.setVisibility(View.GONE);
        }else {
            activity_quizz_imageview_question.setVisibility(View.VISIBLE);

            if (questionItems.get(no).getQuestionFile().equalsIgnoreCase(""))
            {
            }
            else {
                Picasso.with(getApplicationContext()).load(Constants.SERVER_URL_IMAGE+questionItems.get(no).getQuestionFile()).placeholder(R.drawable.ic_launcher).into(activity_quizz_imageview_question);

                final String questionImagePath = Constants.createPDFFolder(Constants.FolderquestionImages);

                Uri downloadUri = Uri.parse(Constants.SERVER_URL_IMAGE+questionItems.get(no).getQuestionFile());

                String targetFileName = questionImagePath + "/" + questionItems.get(no).getQuestionFile();
                Uri destinationUri = Uri.parse(targetFileName);
                final DownloadRequest request =
                        new DownloadRequest(downloadUri)
                                .setDestinationURI(destinationUri)
                                .setPriority(DownloadRequest.Priority.HIGH)
                                .setDownloadListener(listener);
                downloadIds.add(downloadManager.add(request));
            }
        }

        if(questionItems.get(no).getQuestion().equalsIgnoreCase("")){
            activity_quizz_textView_question.setVisibility(View.GONE);
        }else{
            activity_quizz_textView_question.setVisibility(View.VISIBLE);
            activity_quizz_textView_question.setText(questionItems.get(no).getQuestion());
        }

        dynamicCheckBox.removeAllViews();
        for (int i=0;i<map.get(questionItems.get(no).getQuestionId()).size();i++)
        {
            final CheckBox cb = new CheckBox(getApplicationContext());
            final ImageView imageViewAnswer = new ImageView(getApplicationContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 5, 0, 0);

            if (map.get(questionItems.get(no).getQuestionId()).get(i).getIs_file().equalsIgnoreCase("1"))
            {
                final String questionImagePath = Constants.createPDFFolder(Constants.FolderquestionImages);

                LinearLayout.LayoutParams paramsImage = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        180
                );
                LinearLayout myll = new LinearLayout(getApplicationContext());
                myll.setLayoutParams(paramsImage);
                myll.setOrientation(LinearLayout.HORIZONTAL);
                myll.setGravity(Gravity.CENTER_VERTICAL);

                imageViewAnswer.setLayoutParams(paramsImage);
                if (map.get(questionItems.get(no).getQuestionId()).get(i).getIs_file().equalsIgnoreCase(""))
                {
                }
                else {
                    Picasso.with(getApplicationContext()).load(Constants.SERVER_URL_IMAGE+map.get(questionItems.get(no).getQuestionId()).get(i).getChoice()).placeholder(R.drawable.ic_launcher).into(imageViewAnswer);
                }
                paramsImage.topMargin = 3;
                paramsImage.bottomMargin = 15;
                cb.setLayoutParams(paramsImage);
//                cb.setBackgroundColor(getResources().getColor(R.color.white));
                cb.setButtonDrawable(getResources().getDrawable(R.drawable.selector_checkbox_answer));
                cb.setId(Integer.parseInt(map.get(questionItems.get(no).getQuestionId()).get(i).getChoiceId()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    cb.setPadding(15,15,10,15);
                }
                else {
                    cb.setPadding(50, 15, 10, 15);
                }
                myll.addView(cb);
                myll.addView(imageViewAnswer);
                dynamicCheckBox.addView(myll);

                Uri downloadUri = Uri.parse(Constants.SERVER_URL_IMAGE+map.get(questionItems.get(no).getQuestionId()).get(i).getChoice());

                String targetFileName = questionImagePath + "/" + map.get(questionItems.get(no).getQuestionId()).get(i).getChoice();
                Uri destinationUri = Uri.parse(targetFileName);
                final DownloadRequest request =
                        new DownloadRequest(downloadUri)
                                .setDestinationURI(destinationUri)
                                .setPriority(DownloadRequest.Priority.HIGH)
                                .setDownloadListener(listener);
                downloadIds.add(downloadManager.add(request));

            }else{
                params.bottomMargin = 55;
                cb.setLayoutParams(params);
//                cb.setBackgroundColor(getResources().getColor(R.color.white));
                cb.setButtonDrawable(getResources().getDrawable(R.drawable.selector_checkbox_answer));
                cb.setId(Integer.parseInt(map.get(questionItems.get(no).getQuestionId()).get(i).getChoiceId()));
                cb.setText(map.get(questionItems.get(no).getQuestionId()).get(i).getChoice());
                cb.setTextColor(getResources().getColor(R.color.black));
                cb.setTextSize(16);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    cb.setPadding(15,15,10,15);
                }
                else {
                    cb.setPadding(50, 15, 10, 15);
                }

                dynamicCheckBox.addView(cb);
            }

            Log.d("hihi_", i + map.get(questionItems.get(no).getQuestionId()).get(i).getUserChoice());
            if (map.get(questionItems.get(no).getQuestionId()).get(i).getUserChoice().equalsIgnoreCase("1"))
            {
                cb.setChecked(true);
            }

            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    for (int i = 0; i < map.get(questionItems.get(no).getQuestionId()).size(); i++) {
                        if (map.get(questionItems.get(no).getQuestionId()).get(i).getChoiceId().equalsIgnoreCase(String.valueOf(cb.getId()))) {
                            if (b) {
                                cb.setButtonDrawable(getResources().getDrawable(R.drawable.checkbox_pressed));
                                map.get(questionItems.get(no).getQuestionId()).get(i).setUserChoice("1");
                                //changeValuesOfSelected(true, no);
                            } else {
                                cb.setButtonDrawable(getResources().getDrawable(R.drawable.checkbox_regular));
                                map.get(questionItems.get(no).getQuestionId()).get(i).setUserChoice("0");
                                //changeValuesOfSelected(false, no);
                            }
                        }
                    }
                }
            });


            final int finalI = i;
            imageViewAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getApplicationContext(), FullScreenImageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constants.imagepath, Constants.SERVER_URL_IMAGE + map.get(questionItems.get(no).getQuestionId()).get(finalI).getChoice());
                    startActivity(intent);

//                    final String questionImagePath = Constants.createPDFFolder(Constants.FolderquestionImages);
//
//                    final Dialog settingsDialog = new Dialog(StartTestActivity.this);
//
//                    LayoutInflater inflater = getLayoutInflater();
//                    View newView = (View) inflater.inflate(R.layout.activity_fullsize_imageview, null);
//                    settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//                    settingsDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//                    settingsDialog.setContentView(newView);
//                    settingsDialog.setCanceledOnTouchOutside(false);
//
//                    ImageView activity_fullsize_imageview= (ImageView) newView.findViewById(R.id.activity_fullsize_imageview);
//                    ImageView activity_fullsize_imageview_close= (ImageView) newView.findViewById(R.id.activity_fullsize_imageview_close);
////                    zoom = (ZoomControls) newView.findViewById(R.id.zoomControls1);
//                    String imagePath = Constants.SERVER_URL_IMAGE+map.get(questionItems.get(no).getQuestionId()).get(finalI).getChoice();
//
//                    if (imagePath.equalsIgnoreCase(""))
//                    {
//                    }
//                    else {
//                        System.out.println("Image path****: " + imagePath);
//
//                        if(imagePath.contains(questionImagePath)){
//                            Bitmap bmp = BitmapFactory.decodeFile(imagePath);
//                            activity_fullsize_imageview.setImageBitmap(bmp);
//                        }else {
//                            Picasso.with(getApplicationContext()).load(imagePath).placeholder(R.drawable.ic_launcher).into(activity_fullsize_imageview);
//                        }
//                    }
//
//                    activity_fullsize_imageview_close.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            settingsDialog.dismiss();
//                        }
//                    });
//
//                    settingsDialog.show();
                }
            });

            if(no == 0){
                timer.start();

            }

        }
    }


    private void changeValuesOfSelected(boolean ischecked, int no){

        System.out.println("SIze of hashmap: " + map.get(questionItems.get(no).getQuestionId()).size());

        for (int i = 0; i < map.get(questionItems.get(no).getQuestionId()).size(); i++) {

            if(ischecked == true){
                if(choiceItemsAll.get(i).getChoiceId().contains(map.get(questionItems.get(no).getQuestionId()).get(i).getChoiceId())) {
                    int indexOfChoideId = choiceItemsAll.get(i).getChoiceId().indexOf(map.get(questionItems.get(no).getQuestionId()).get(i).getChoiceId());
                    choiceItemsAll.get(indexOfChoideId).setUserChoice("1");
                }
            }else{
                if(choiceItemsAll.get(i).getChoiceId().contains(map.get(questionItems.get(no).getQuestionId()).get(i).getChoiceId())) {
                    int indexOfChoideId = choiceItemsAll.get(i).getChoiceId().indexOf(map.get(questionItems.get(no).getQuestionId()).get(i).getChoiceId());
                    choiceItemsAll.get(indexOfChoideId).setUserChoice("0");
                }
            }
        }
    }

    public void addPracticeTest()
    {
        try {

//            for(int j = 0; j < choiceItemsAll.size(); j++){
//                System.out.println("User choice before: " + choiceItemsAll.get(j).getUserChoice());
//            }
//
//            for(int j = 0; j < choiceItemsAll.size(); j++){
//                System.out.println("User choice after****: " + choiceItemsAll.get(j).getUserChoice());
//            }

            databaseHelper = new DatabaseHelper(getApplicationContext());

            TestItem testItem = new TestItem(intentTestId,intentTestName, intentTotalQuestions, intentTestTime, intentMarks, intentCourse, intentSubject, intentTestCode);
            databaseHelper.addInToTest(testItem);

            for(int i = 0 ; i < questionItems.size(); i++){

                System.out.println("Time taken: " + totalTimeTakenForQuestion.get(i));

                QuestionItem questionItem= new QuestionItem(questionItems.get(i).getQuestionId(), questionItems.get(i).getQuestion(), questionItems.get(i).getQuestionFile(), false,false, questionItems.get(i).getTestId(), questionItems.get(i).getQuesIsFile(), questionItems.get(i).getMarks(), totalTimeTakenForQuestion.get(i), questionItems.get(i).getQuestionNo(), questionItems.get(i).getRandomNo());
                databaseHelper.addInToQuestion(questionItem);
            }


//            for (int j = 0; j < currentQuestion; j++) {
//
//                for (int i = 0; i < map.get(questionItems.get(j).getQuestionId()).size(); i++) {
//                    ChoiceItem choiceItem = new ChoiceItem(map.get(questionItems.get(j).getQuestionId()).get(i).getQuestionID(), map.get(questionItems.get(j).getQuestionId()).get(i).getChoiceId(),
//                            map.get(questionItems.get(j).getQuestionId()).get(i).getChoice(), map.get(questionItems.get(j).getQuestionId()).get(i).getIsRight(),
//                            map.get(questionItems.get(j).getQuestionId()).get(i).getUserChoice(), map.get(questionItems.get(j).getQuestionId()).get(i).getIs_file());
//                    System.out.println("Choices arraylist: "+ choiceItem);
////                    databaseHelper.addInToQuestionChoice(choiceItem);
//                }
//
//            }
//
//
            for(int i = 0 ; i < choiceItemsAll.size(); i++){
                ChoiceItem choiceItem= new ChoiceItem(choiceItemsAll.get(i).getQuestionID(), choiceItemsAll.get(i).getChoiceId(), choiceItemsAll.get(i).getChoice(), choiceItemsAll.get(i).getIsRight(), choiceItemsAll.get(i).getUserChoice(), choiceItemsAll.get(i).getIs_file());
                databaseHelper.addInToQuestionChoice(choiceItem);
            }
//
            System.out.println("JSON OBJECT: "+jsonObject.toString());
        }catch (Exception e){

        }

    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    private class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override public void onFinish() {
            activity_quizz_textView_actionbar_timing.setText("Completed.");
            System.out.println("Completed.");
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.time_up), Toast.LENGTH_SHORT).show();

            timer.cancel();
            result = generateResult();
            addPracticeTest();

            if (internetConnection.isNetworkAvailable(getApplicationContext())) {
                submitTest();
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        }
        @SuppressLint("NewApi")
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            System.out.println("Time Remaining  "+hms);
            activity_quizz_textView_actionbar_timing.setText("Time Remaining " + hms);

//            if(actualTime == millis){
//                String[] splittedTime = activity_quizz_textView_actionbar_timing.getText().toString().split(" ");
//
//                if (currentQuestion == 0) {
//                    oldTimeTaken = splittedTime[2];
//                }
//            }

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

//        if(timer == null){
//        }else {
//            timer.cancel();
//        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        if (currentQuestion > 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(StartTestActivity.this);

            builder.setMessage(getResources().getString(R.string.Are_you_sure));

            builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog

                    timer.cancel();
                    result = generateResult();
                    addPracticeTest();
                    dialog.dismiss();
                    if(internetConnection.isNetworkAvailable(getApplicationContext())){
                        submitTest();
                    }else{
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }

//                            submitResult(result);
                }

            });

            builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            if (alert.isShowing()) {

            } else {
                alert.show();
            }
        } else if (currentQuestion == 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(StartTestActivity.this);
            builder.setMessage(getResources().getString(R.string.Are_you_sure_can_exam));
            builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog
                    timer.cancel();
                    dialog.dismiss();
                    finish();
                }
            });

            builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            if (alert.isShowing()) {

            } else {
                alert.show();
            }
        }
    }


    public boolean submitTest()
    {
        String quizQuestion = "submitTest";
        final String url = Constants.SERVER_URL+Constants.submitTest;

        final ProgressDialog dialog = new ProgressDialog(StartTestActivity.this);
        dialog.setMessage(getResources().getString(R.string.please_wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Log.d("submitTest", url);

        StringRequest quizQuestionRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s)
            {
                Log.d("submitTest",s);
                try
                {
                    JSONObject response = new JSONObject(s);

                    if (response.getString(Constants.status).equalsIgnoreCase(Constants.status200))
                    {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), response.getString(Constants.message).toString(), Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(getApplicationContext(), OneTestAnalyticsActivity.class);
                        intent.putExtra(Constants.test_id, intentTestId);
                        intent.putExtra(Constants.name, intentTestName);
                        startActivity(intent);

                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), response.getString(Constants.message).toString(), Toast.LENGTH_SHORT).show();
                    }

                }
                catch (JSONException e)
                {
                    dialog.dismiss();
                    Log.d("submitTest",e.toString());
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
                data.put(Constants.data, jsonArrayQuestion.toString());
                data.put(Constants.ismobile, Constants.isMobileValue);
                data.put(Constants.test_id, intentTestId);
                data.put(Constants.userid, sharedPreferencesRemember.getString(Constants.sharedPreferenceUserId, ""));
                Log.d("submitTest",data.toString());
                return data;
            }
        };
        Controller.getInstance().addToRequestQueue(quizQuestionRequest, quizQuestion);

        return  false;
    }


    public String replaceString(String str) {
        String newstr = "";
        if (null != str && str.length() > 0 )
        {
            int endIndex = str.lastIndexOf(",");
            if (endIndex != -1)
            {
                newstr = str.substring(0, endIndex); // not forgot to put check if(endIndex != -1)
            }
        }
        return newstr;
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
