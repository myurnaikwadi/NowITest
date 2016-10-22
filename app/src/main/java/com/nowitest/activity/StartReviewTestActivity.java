package com.nowitest.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.nowitest.R;
import com.nowitest.databasehelper.DatabaseHelper;
import com.nowitest.model.ChoiceItem;
import com.nowitest.model.QuestionItem;
import com.nowitest.model.TestItem;
import com.nowitest.network.InternetConnection;
import com.nowitest.util.Constants;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pushkar Tamhane on 20/8/16.
 */
public class StartReviewTestActivity extends ParentActivity {

    public static ArrayList<QuestionItem> questionItems;
    private ArrayList<ChoiceItem> choiceItems;
    private ArrayList<ChoiceItem> choiceItemsAll;
    public static HashMap<String,ArrayList<ChoiceItem>> map;
    private int totalQuestion = 0;
    private int currentQuestion = 0;
//    private RelativeLayout outerQuizz;
    private Boolean flagForHomePress=false;
    private TextView activity_quizz_textView_question, activity_quizz_textView_question_no;
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
    private TextView activity_quizz_textView_actionbar_title, activity_quizz_textView_actionbar_timing;
    private ArrayList<String> totalTimeTakenForQuestion;
    DownloadListener listener = new DownloadListener();
    ArrayList<Integer> downloadIds = new ArrayList<Integer>();
    ThinDownloadManager downloadManager;
    private int noOfCorrectAns = 0;
    private int noOfwrongAns = 0;
    private int noOfNotAttempted = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_test);

        downloadIds = new ArrayList<Integer>();
        downloadManager = new ThinDownloadManager(Constants.DOWNLOAD_THREAD_POOL_SIZE);
        databaseHelper = new DatabaseHelper(getApplicationContext());

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
        long actualTime = TimeUnit.MINUTES.toMillis(Long.parseLong(splittedTime[0]));
        timer = new CounterClass(actualTime,1000);
        findViewByIds();

        activity_quizz_textView_actionbar_title.setText(intentTestName);

        questionItems = new ArrayList<QuestionItem>();
        questionItems = databaseHelper.getAllQuestions(intentTestId);

        map = new HashMap<String, ArrayList<ChoiceItem>>();
        choiceItemsAll = new ArrayList<ChoiceItem>();


        for(int i = 0; i < questionItems.size(); i++){

            choiceItems = new ArrayList<ChoiceItem>();
            choiceItems = databaseHelper.getAllQuestionChoice(questionItems.get(i).getQuestionId());
            Log.d("hihiC", choiceItems.size() + "");

            for(int j = 0; j < choiceItems.size() ; j++){
                choiceItems.get(j).setUserChoice("0");
               // choiceItemsAll.add(new ChoiceItem(choiceItems.get(j).getQuestionID(), choiceItems.get(j).getChoiceId(), choiceItems.get(j).getChoice(), choiceItems.get(j).getIsRight(), "0", choiceItems.get(j).getIs_file()));
            }

            map.put(questionItems.get(i).getQuestionId(), choiceItems);
//            for(int j = 0; j < choiceItems.size() ; j++){
//                choiceItems.add(new ChoiceItem(choiceItems.get(j).getQuestionID(), choiceItems.get(j).getChoiceId(), choiceItems.get(j).getChoice(), choiceItems.get(j).getIsRight(), "0", choiceItems.get(j).getIs_file()));
//                choiceItemsAll.add(new ChoiceItem(choiceItems.get(j).getQuestionID(), choiceItems.get(j).getChoiceId(), choiceItems.get(j).getChoice(), choiceItems.get(j).getIsRight(), "0", choiceItems.get(j).getIs_file()));
//            }

        }

        totalQuestion = questionItems.size();
        currentQuestion = 0;
        Log.d("hihiQ", questionItems.size() + "");
        Log.d("hihiC", choiceItems.size() + "");
        Log.d("hihiM", map.size() + "");
//
//        Collections.shuffle(questionItems);
//        Collections.shuffle(choiceItems);
//        Collections.shuffle(choiceItemsAll);
        setQuestion(0);
//        if(internetConnection.isNetworkAvailable(getApplicationContext())){
//            quizQuestion();
//        }else{
//            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
//        }


        activity_start_test_button_save_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentQuestion == (questionItems.size() - 1)) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(StartReviewTestActivity.this);

                    builder.setMessage(getResources().getString(R.string.Are_you_sure));

                    builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
                            timer.cancel();
                            result = generateResult();
//                            addPracticeTest();
                            dialog.dismiss();
//                            submitResult(result);

                            Intent intentNewActivity = new Intent(getApplicationContext(), PractiveTestAnalytics.class);
                            intentNewActivity.putExtra(Constants.correct_answer, noOfCorrectAns+"");
                            intentNewActivity.putExtra(Constants.wrongAns, noOfwrongAns+"");
                            intentNewActivity.putExtra(Constants.notAttempt, noOfNotAttempted+"");
                            intentNewActivity.putExtra(Constants.name, intentTestName);
                            startActivity(intentNewActivity);
                            overridePendingTransition(0, 0);
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
                } else {

                    String[] splittedTime = activity_quizz_textView_actionbar_timing.getText().toString().split(" ");

                    totalTimeTakenForQuestion.add(splittedTime[2]);
//                    activity_quizz_textView_question.setFocusable(true);
//                    activity_start_test_scrollview.smoothScrollTo(0,0);
                    activity_start_test_scrollview.scrollTo(0,0);
                    noOfQuestionAtt++;
                    currentQuestion = currentQuestion + 1;
                    setQuestion(currentQuestion);
                }
            }
        });

        activity_quizz_imageview_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("Image path****: " + Constants.SERVER_URL_IMAGE+questionItems.get(currentQuestion).getQuestionFile());
                System.out.println("Image path********* " + questionItems.get(currentQuestion).getQuestion());
                System.out.println("Position: " + currentQuestion);

                Intent intent = new Intent(getApplicationContext(), FullScreenImageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.imagepath, Constants.SERVER_URL_IMAGE+questionItems.get(currentQuestion).getQuestionFile());
                startActivity(intent);

//                final String questionImagePath = Constants.createPDFFolder(Constants.FolderquestionImages);
//
//                final Dialog settingsDialog = new Dialog(StartReviewTestActivity.this);
//
//                LayoutInflater inflater = getLayoutInflater();
//                View newView = (View) inflater.inflate(R.layout.activity_fullsize_imageview, null);
//                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//                settingsDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//                settingsDialog.setContentView(newView);
//                settingsDialog.setCanceledOnTouchOutside(false);
//
//                ImageView activity_fullsize_imageview= (ImageView) newView.findViewById(R.id.activity_fullsize_imageview);
//                ImageView activity_fullsize_imageview_close= (ImageView) newView.findViewById(R.id.activity_fullsize_imageview_close);
//
//                String imagePath = questionImagePath + "/"+questionItems.get(currentQuestion).getQuestionFile();
//
//                if (imagePath.equalsIgnoreCase(""))
//                {
//                }
//                else {
//                    System.out.println("Image path****: " + imagePath);
//
//                    if(imagePath.contains(questionImagePath)){
//                        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
//                        activity_fullsize_imageview.setImageBitmap(bmp);
//                    }else {
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
        for (int j=0;j<questionItems.size();j++)
        {
            String trueAns="";
            String userAns="";
            for (int i=0;i<map.get(questionItems.get(j).getQuestionId()).size();i++)
            {
                if (map.get(questionItems.get(j).getQuestionId()).get(i).getIsRight().equalsIgnoreCase("1"))
                {

                    trueAns = trueAns + map.get(questionItems.get(j).getQuestionId()).get(i).getChoice() + " , ";
                }

                if (map.get(questionItems.get(j).getQuestionId()).get(i).getUserChoice().equalsIgnoreCase("1"))
                {

                    userAns = userAns + map.get(questionItems.get(j).getQuestionId()).get(i).getChoice() + " , ";
                }
            }

            if (userAns.equalsIgnoreCase("") ||userAns.equalsIgnoreCase(null) || userAns.length()<1)
            {
            }
            else
            {
                questionItems.get(j).setAnsGiven(true);
            }

                if (trueAns.equalsIgnoreCase(userAns)) {
                    count = count + 1;
                }
        }


        for (int j = 0; j < questionItems.size(); j++) {
            String trueAns = "";
            String userAns = "";
            String correctAnsId = "";
            String userAnsId = "";

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

            System.out.println("Local Analyatics: User ans: "+userAnsId+" Correct ans: "+correctAnsId);

            String[] splittedUserAns = userAnsId.split(",");
            String[] splittedCorrectAns = correctAnsId.split(",");

            ArrayList<String> userAnsArrayList = new ArrayList<String>();
            ArrayList<String> correctAnsArrayList = new ArrayList<String>();

            for (int i = 0; i < splittedUserAns.length; i++){
                userAnsArrayList.add(splittedUserAns[i]);
            }
            for (int i = 0; i < splittedCorrectAns.length; i++){
                correctAnsArrayList.add(splittedCorrectAns[i]);
            }

            Collections.sort(userAnsArrayList);
            Collections.sort(correctAnsArrayList);

            if(userAnsId.equalsIgnoreCase("")){
                noOfNotAttempted = noOfNotAttempted + 1;
            }else if(userAnsArrayList.equals(correctAnsArrayList)){
                noOfCorrectAns = noOfCorrectAns + 1;
            }else{
                noOfwrongAns = noOfwrongAns + 1;
            }

            if (userAns.equalsIgnoreCase("") || userAns.equalsIgnoreCase(null) || userAns.length() < 1) {
            } else {
                questionItems.get(j).setAnsGiven(true);
            }

            if (trueAns.equalsIgnoreCase(userAns)) {
                count = count + 1;
            }
        }

            System.out.println("Local Analyatics: Correct ans: "+noOfCorrectAns+" Wrong ans: "+noOfwrongAns+" not attempted: "+noOfNotAttempted);
        Log.d("hihiCont", count + "");
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

    public void setQuestion(final int no)
    {
        activity_quizz_textView_question_no.setText((currentQuestion+1) +") ");

        final String questionImagePath = Constants.createPDFFolder(Constants.FolderquestionImages);

        if(questionItems.get(no).getQuestionFile().equalsIgnoreCase("")){
            activity_quizz_imageview_question.setVisibility(View.GONE);
        }else {
            activity_quizz_imageview_question.setVisibility(View.VISIBLE);

            if (questionItems.get(no).getQuestionFile().equalsIgnoreCase(""))
            {
            }
            else {

                Bitmap bmp = BitmapFactory.decodeFile(questionImagePath + "/" + questionItems.get(no).getQuestionFile());
                activity_quizz_imageview_question.setImageBitmap(bmp);

//                Picasso.with(getApplicationContext()).load(Constants.SERVER_URL_IMAGE+questionItems.get(no).getQuestionFile()).placeholder(R.drawable.ic_launcher).into(activity_quizz_imageview_question);
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
                    System.out.println("Image name " + questionImagePath+"/"+map.get(questionItems.get(no).getQuestionId()).get(i).getChoice());
                    Bitmap bmp = BitmapFactory.decodeFile(questionImagePath+"/"+map.get(questionItems.get(no).getQuestionId()).get(i).getChoice());
                    imageViewAnswer.setImageBitmap(bmp);
                    //Picasso.with(getApplicationContext()).load(Constants.SERVER_URL_IMAGE+map.get(questionItems.get(no).getQuestionId()).get(i).getChoice()).placeholder(R.drawable.ic_launcher).into(imageViewAnswer);
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
                            } else {
                                cb.setButtonDrawable(getResources().getDrawable(R.drawable.checkbox_regular));
                                map.get(questionItems.get(no).getQuestionId()).get(i).setUserChoice("0");
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
                    intent.putExtra(Constants.imagepath, questionImagePath+"/"+ map.get(questionItems.get(no).getQuestionId()).get(finalI).getChoice());
                    startActivity(intent);


//                    final String questionImagePath = Constants.createPDFFolder(Constants.FolderquestionImages);
//
//                    final Dialog settingsDialog = new Dialog(StartReviewTestActivity.this);
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
//
//                    String imagePath = questionImagePath + "/"+map.get(questionItems.get(no).getQuestionId()).get(finalI).getChoice();
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



    public void addPracticeTest()
    {
        databaseHelper = new DatabaseHelper(getApplicationContext());

        TestItem testItem = new TestItem(intentTestId,intentTestName, intentTotalQuestions, intentTestTime, intentMarks, intentCourse, intentSubject, intentTestCode);
        databaseHelper.addInToTest(testItem);

        for(int i = 0 ; i < questionItems.size(); i++){

            QuestionItem questionItem= new QuestionItem(questionItems.get(i).getQuestionId(), questionItems.get(i).getQuestion(), questionItems.get(i).getQuestionFile(), false,false, questionItems.get(i).getTestId(), questionItems.get(i).getQuesIsFile(), questionItems.get(i).getMarks(), questionItems.get(i).getTimeTaken(), questionItems.get(i).getQuestionNo(), questionItems.get(i).getRandomNo());
            databaseHelper.addInToQuestion(questionItem);
        }

        for(int i = 0 ; i < choiceItemsAll.size(); i++){

            ChoiceItem choiceItem= new ChoiceItem(choiceItemsAll.get(i).getQuestionID(), choiceItemsAll.get(i).getChoiceId(), choiceItemsAll.get(i).getChoice(), choiceItemsAll.get(i).getIsRight(), "0", choiceItemsAll.get(i).getIs_file());
            databaseHelper.addInToQuestionChoice(choiceItem);
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

            Intent intentNewActivity = new Intent(getApplicationContext(), PractiveTestAnalytics.class);
            intentNewActivity.putExtra(Constants.correct_answer, noOfCorrectAns+"");
            intentNewActivity.putExtra(Constants.wrongAns, noOfwrongAns+"");
            intentNewActivity.putExtra(Constants.notAttempt, noOfNotAttempted+"");
            intentNewActivity.putExtra(Constants.name, intentTestName);
            startActivity(intentNewActivity);
            overridePendingTransition(0, 0);
            finish();
        }
        @SuppressLint("NewApi")
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            System.out.println("Time Remaining  "+hms);
            activity_quizz_textView_actionbar_timing.setText("Time Remaining " + hms);
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
        super.onBackPressed();

        if (currentQuestion == (questionItems.size() - 1)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(StartReviewTestActivity.this);

            builder.setMessage(getResources().getString(R.string.Are_you_sure));

            builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog

                    timer.cancel();
                    result = generateResult();
                    addPracticeTest();
                    dialog.dismiss();

                    Intent intentNewActivity = new Intent(getApplicationContext(), PractiveTestAnalytics.class);
                    intentNewActivity.putExtra(Constants.correct_answer, noOfCorrectAns+"");
                    intentNewActivity.putExtra(Constants.wrongAns, noOfwrongAns+"");
                    intentNewActivity.putExtra(Constants.notAttempt, noOfNotAttempted+"");
                    intentNewActivity.putExtra(Constants.name, intentTestName);
                    startActivity(intentNewActivity);
                    overridePendingTransition(0, 0);
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
        } else if (currentQuestion == 1) {

            AlertDialog.Builder builder = new AlertDialog.Builder(StartReviewTestActivity.this);
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
