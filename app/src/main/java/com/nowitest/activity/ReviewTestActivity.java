package com.nowitest.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nowitest.R;
import com.nowitest.databasehelper.DatabaseHelper;
import com.nowitest.model.ChoiceItem;
import com.nowitest.model.QuestionItem;
import com.nowitest.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Pushkar Tamhane on 20/8/16.
 */
public class ReviewTestActivity extends ParentActivity {

    private String intentTestId, intentTestName;
    public static ArrayList<QuestionItem> questionItems;
    private ArrayList<ChoiceItem> choiceItems;
    private ArrayList<ChoiceItem> choiceItemsAll;
    public static HashMap<String,ArrayList<ChoiceItem>> map;
    private int totalQuestion = 0;
    private int currentQuestion = 0;
    DatabaseHelper databaseHelper;
    private TextView activity_quizz_textView_question, activity_quizz_textView_time_taken, activity_quizz_textView_question_no;
    private ImageView activity_quizz_imageview_question;
    private LinearLayout dynamicCheckBox;
    private ScrollView activity_start_test_scrollview;
    private Button activity_start_test_button_previous, activity_start_test_button_next;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_test_new);

        Intent intent = getIntent();
        intentTestId = intent.getStringExtra(Constants.test_id);
        intentTestName = intent.getStringExtra(Constants.title);

        databaseHelper = new DatabaseHelper(getApplicationContext());
        setActionBarCustomWithoutBack(intentTestName);
        findViewByIds();

        questionItems = new ArrayList<QuestionItem>();
        questionItems = databaseHelper.getAllQuestions(intentTestId);

        map = new HashMap<String, ArrayList<ChoiceItem>>();
        choiceItemsAll = new ArrayList<ChoiceItem>();


        for(int i = 0; i < questionItems.size(); i++){

            choiceItems = new ArrayList<ChoiceItem>();
            choiceItems = databaseHelper.getAllQuestionChoice(questionItems.get(i).getQuestionId());
            Log.d("hihiC", choiceItems.size() + "");
            map.put(questionItems.get(i).getQuestionId(), choiceItems);
        }

        totalQuestion = questionItems.size();
        currentQuestion = 0;
        Log.d("hihiQ", questionItems.size() + "");
        Log.d("hihiC", choiceItems.size() + "");
        Log.d("hihiM", map.size() + "");

        activity_start_test_button_previous.setVisibility(View.GONE);

        activity_start_test_button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestion == (questionItems.size() - 1)) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ReviewTestActivity.this);

                    builder.setMessage(getResources().getString(R.string.exit_review_test));

                    builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

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
                    activity_start_test_button_previous.setVisibility(View.VISIBLE);
                    activity_start_test_scrollview.scrollTo(0, 0);

                    currentQuestion = currentQuestion + 1;
                    setQuestion(currentQuestion);
                }
            }
        });

        activity_start_test_button_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestion == 0) {
                    activity_start_test_button_previous.setVisibility(View.GONE);

                } else {
                    activity_start_test_scrollview.scrollTo(0,0);
                    currentQuestion = currentQuestion - 1;
                    setQuestion(currentQuestion);

                    if(currentQuestion == 0){
                        activity_start_test_button_previous.setVisibility(View.GONE);
                    }else{
                        activity_start_test_button_previous.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        activity_quizz_imageview_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("Image path****: " + Constants.SERVER_URL_IMAGE+questionItems.get(currentQuestion).getQuestionFile());
                System.out.println("Image path********* " + questionItems.get(currentQuestion).getQuestion());
                System.out.println("Position: " + currentQuestion);

                final String questionImagePath = Constants.createPDFFolder(Constants.FolderquestionImages);
                Intent intent = new Intent(getApplicationContext(), FullScreenImageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.imagepath, questionImagePath + "/"+questionItems.get(currentQuestion).getQuestionFile());
                startActivity(intent);

//                final String questionImagePath = Constants.createPDFFolder(Constants.FolderquestionImages);
//
//                final Dialog settingsDialog = new Dialog(ReviewTestActivity.this);
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

        setQuestion(0);
    }

    private void findViewByIds(){
        activity_quizz_textView_question = (TextView) findViewById(R.id.activity_quizz_textView_question);
        activity_quizz_textView_question_no = (TextView) findViewById(R.id.activity_quizz_textView_question_no);
        activity_quizz_imageview_question = (ImageView) findViewById(R.id.activity_quizz_imageview_question);
        activity_quizz_textView_time_taken = (TextView) findViewById(R.id.activity_quizz_textView_time_taken);
        dynamicCheckBox= (LinearLayout) findViewById(R.id.dynamicCheckBox);
        activity_start_test_scrollview= (ScrollView) findViewById(R.id.activity_start_test_scrollview);
        activity_start_test_button_previous= (Button) findViewById(R.id.activity_start_test_button_previous);
        activity_start_test_button_next= (Button) findViewById(R.id.activity_start_test_button_next);
    }

    public void setQuestion(final int no)
    {

        activity_quizz_textView_question_no.setText(questionItems.get(no).getQuestionNo() +") "+"   "+questionItems.get(no).getRandomNo());

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

//        activity_quizz_textView_question.setText((currentQuestion+1) +") "+questionItems.get(no).getQuestion());
        System.out.println("Time taken: " + questionItems.get(no).getTimeTaken());
        activity_quizz_textView_time_taken.setText("Time Taken "+questionItems.get(no).getTimeTaken());

        dynamicCheckBox.removeAllViews();
        for (int i=0;i<map.get(questionItems.get(no).getQuestionId()).size();i++)
        {


            final TextView textViewAnswers = new TextView(getApplicationContext());
            final ImageView imageViewAnswer = new ImageView(getApplicationContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 5, 0, 0);

            if (map.get(questionItems.get(no).getQuestionId()).get(i).getIs_file().equalsIgnoreCase("1"))
            {
                LinearLayout.LayoutParams paramsImage = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        200
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
                    Bitmap bmp = BitmapFactory.decodeFile(questionImagePath + "/" + map.get(questionItems.get(no).getQuestionId()).get(i).getChoice());
                    imageViewAnswer.setImageBitmap(bmp);
                    //Picasso.with(getApplicationContext()).load(Constants.SERVER_URL_IMAGE+map.get(questionItems.get(no).getQuestionId()).get(i).getChoice()).placeholder(R.drawable.ic_launcher).into(imageViewAnswer);
                }
                paramsImage.topMargin = 3;
                paramsImage.bottomMargin = 35;
                textViewAnswers.setLayoutParams(paramsImage);
//                cb.setBackgroundColor(getResources().getColor(R.color.white));

                if(map.get(questionItems.get(no).getQuestionId()).get(i).getIsRight().equalsIgnoreCase("1")){
//                    imageViewAnswer.setBackground(getResources().getDrawable(R.drawable.test_name_ans_box_active));
//                    textViewAnswers.setBackgroundColor(getResources().getColor(R.color.actionBar));
                    myll.setBackground(getResources().getDrawable(R.drawable.test_name_ans_box_active));
                    imageViewAnswer.setPadding(0, 40, 0, 10);
                }else{
                    imageViewAnswer.setPadding(0, 40, 0, 10);
//                    imageViewAnswer.setBackground(getResources().getDrawable(R.drawable.test_name_ans_box_inactive));
                    myll.setBackground(getResources().getDrawable(R.drawable.test_name_ans_box_inactive));
                }

                if(map.get(questionItems.get(no).getQuestionId()).get(i).getIsRight().equalsIgnoreCase("1")){
                    textViewAnswers.setBackground(getResources().getDrawable(R.drawable.test_name_ans_box_active));
                }else{
                    textViewAnswers.setBackground(getResources().getDrawable(R.drawable.test_name_ans_box_inactive));
                }

//                if(map.get(questionItems.get(no).getQuestionId()).get(i).getUserChoice().equalsIgnoreCase("1")){
//                    textViewAnswers.setBackgroundColor(getResources().getColor(R.color.actionBar));
//                }else{
//                }
//
//                if(map.get(questionItems.get(no).getQuestionId()).get(i).getIsRight().equalsIgnoreCase(map.get(questionItems.get(no).getQuestionId()).get(i).getUserChoice())){
//                    textViewAnswers.setBackground(getResources().getDrawable(R.drawable.test_name_ans_box_active));
//                }else if(map.get(questionItems.get(no).getQuestionId()).get(i).getIsRight().equalsIgnoreCase("1")){
//                    textViewAnswers.setBackgroundColor(getResources().getColor(R.color.tab_inactive_text));
//                }else{
//                    textViewAnswers.setBackground(getResources().getDrawable(R.drawable.test_name_ans_box_inactive));
//                }

//                if(map.get(questionItems.get(no).getQuestionId()).get(i).getIsRight().equalsIgnoreCase("1")){
//                    if(map.get(questionItems.get(no).getQuestionId()).get(i).getUserChoice().equalsIgnoreCase("1")){
//                        myll.setBackgroundColor(getResources().getColor(R.color.green));
//                    }else{
//                        myll.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//                    }
//
//                }


                if(map.get(questionItems.get(no).getQuestionId()).get(i).getUserChoice().equalsIgnoreCase("1")){
                    if(map.get(questionItems.get(no).getQuestionId()).get(i).getIsRight().equalsIgnoreCase("1")){
                        myll.setBackgroundColor(getResources().getColor(R.color.green));
                    }else{
                        myll.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    }

                    if(map.get(questionItems.get(no).getQuestionId()).get(i).getIsRight().equalsIgnoreCase("1")){
                        myll.setBackgroundColor(getResources().getColor(R.color.green));
                    }

                }else {

                }

//                if(map.get(questionItems.get(no).getQuestionId()).get(i).getIsRight().equalsIgnoreCase(map.get(questionItems.get(no).getQuestionId()).get(i).getUserChoice())){
////                    textViewAnswers.setBackground(getResources().getDrawable(R.drawable.test_name_ans_box_active));
//                    textViewAnswers.setBackgroundColor(getResources().getColor(R.color.green));
//                }else if(!map.get(questionItems.get(no).getQuestionId()).get(i).getIsRight().equalsIgnoreCase(map.get(questionItems.get(no).getQuestionId()).get(i).getUserChoice())){
//                    textViewAnswers.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//                }

                if(map.get(questionItems.get(no).getQuestionId()).get(i).getIsRight().equalsIgnoreCase(map.get(questionItems.get(no).getQuestionId()).get(i).getUserChoice())){
                    textViewAnswers.setBackground(getResources().getDrawable(R.drawable.test_name_ans_box_active));
                }else{
                    textViewAnswers.setBackground(getResources().getDrawable(R.drawable.test_name_ans_box_inactive));
                }

                textViewAnswers.setId(Integer.parseInt(map.get(questionItems.get(no).getQuestionId()).get(i).getChoiceId()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    textViewAnswers.setPadding(15,15,10,15);
                }
                else {
                    textViewAnswers.setPadding(50, 15, 10, 15);
                }
                //myll.addView(textViewAnswers);
                myll.addView(imageViewAnswer);
                dynamicCheckBox.addView(myll);
            }else{
                params.bottomMargin = 35;
                textViewAnswers.setLayoutParams(params);
//                cb.setBackgroundColor(getResources().getColor(R.color.white));

                boolean flag = false;
                if(map.get(questionItems.get(no).getQuestionId()).get(i).getIsRight().equalsIgnoreCase("1")){
                    textViewAnswers.setBackground(getResources().getDrawable(R.drawable.test_name_ans_box_active));
                    flag = true;
                }else{
                    textViewAnswers.setBackground(getResources().getDrawable(R.drawable.test_name_ans_box_inactive));
                }

//                if(map.get(questionItems.get(no).getQuestionId()).get(i).getIsRight().equalsIgnoreCase("1")){
//                    textViewAnswers.setBackgroundColor(getResources().getColor(R.color.green));
//                }


                if(map.get(questionItems.get(no).getQuestionId()).get(i).getUserChoice().equalsIgnoreCase("1")){
                    if(map.get(questionItems.get(no).getQuestionId()).get(i).getIsRight().equalsIgnoreCase("1")){
                        textViewAnswers.setBackgroundColor(getResources().getColor(R.color.green));

                    }else{
                        textViewAnswers.setBackgroundColor(getResources().getColor(R.color.colorAccent));


//                        if(map.get(questionItems.get(no).getQuestionId()).get(i).getIsRight().equalsIgnoreCase("1")){
//                            textViewAnswers.setBackgroundColor(getResources().getColor(R.color.green));
//                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    {
                        textViewAnswers.setPadding(15, 55, 10, 55);
                    }
                    else {
                        textViewAnswers.setPadding(50, 55, 10, 55);
                    }

                }else {

                    if(map.get(questionItems.get(no).getQuestionId()).get(i).getIsRight().equalsIgnoreCase("1")){
                        textViewAnswers.setBackgroundColor(getResources().getColor(R.color.green));
                    }

                    int temp = 0;
                    for(int k = 0; k < map.get(questionItems.get(no).getQuestionId()).size(); k++){
                        if(map.get(questionItems.get(no).getQuestionId()).get(k).getUserChoice().equalsIgnoreCase("0")){
                            temp++;
                        }else{
                            temp--;
                        }
                    }
                    if(temp == 3){
                        textViewAnswers.setBackground(getResources().getDrawable(R.drawable.test_name_ans_box_active));
                    }
//                    if(map.get(questionItems.get(no).getQuestionId()).get(i).getIsRight().equalsIgnoreCase("1")
//                            && flag == false){
//                        textViewAnswers.setBackgroundColor(getResources().getColor(R.color.green));
//                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    {
                        textViewAnswers.setPadding(15, 55, 10, 55);
                    }
                    else {
                        textViewAnswers.setPadding(50, 55, 10, 55);
                    }
                }
//                if(map.get(questionItems.get(no).getQuestionId()).get(i).getIsRight().equalsIgnoreCase("1")){
//                    if(map.get(questionItems.get(no).getQuestionId()).get(i).getUserChoice().equalsIgnoreCase("1")){
//                        textViewAnswers.setBackgroundColor(getResources().getColor(R.color.green));
//                    }else{
//                        textViewAnswers.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//                    }
//
//                }



//                if(map.get(questionItems.get(no).getQuestionId()).get(i).getIsRight().equalsIgnoreCase(map.get(questionItems.get(no).getQuestionId()).get(i).getUserChoice())){
//                    textViewAnswers.setBackground(getResources().getDrawable(R.drawable.test_name_ans_box_active));
//                }else if(map.get(questionItems.get(no).getQuestionId()).get(i).getIsRight().equalsIgnoreCase("1")){
//                    textViewAnswers.setBackgroundColor(getResources().getColor(R.color.tab_inactive_text));
//                }else{
//                    textViewAnswers.setBackground(getResources().getDrawable(R.drawable.test_name_ans_box_inactive));
//                }

//                if(map.get(questionItems.get(no).getQuestionId()).get(i).getIsRight().equalsIgnoreCase(map.get(questionItems.get(no).getQuestionId()).get(i).getUserChoice())){
//                    textViewAnswers.setBackground(getResources().getDrawable(R.drawable.test_name_ans_box_active));
//                }else{
//                    textViewAnswers.setBackground(getResources().getDrawable(R.drawable.test_name_ans_box_inactive));
//                }

//                if(map.get(questionItems.get(no).getQuestionId()).get(i).getIsRight().equalsIgnoreCase("1")){
//                    textViewAnswers.setBackground(getResources().getDrawable(R.drawable.test_name_ans_box_active));
//                }else{
//                    textViewAnswers.setBackground(getResources().getDrawable(R.drawable.test_name_ans_box_inactive));
//                }
//
//                if(map.get(questionItems.get(no).getQuestionId()).get(i).getUserChoice().equalsIgnoreCase("1")){
//                    textViewAnswers.setBackgroundColor(getResources().getColor(R.color.actionBar));
//                }else{
//                }

                System.out.println(" is right: " + map.get(questionItems.get(no).getQuestionId()).get(i).getIsRight());
                System.out.println(" is right user choice: " + map.get(questionItems.get(no).getQuestionId()).get(i).getUserChoice());
//                if(map.get(questionItems.get(no).getQuestionId()).get(i).getIsRight().equalsIgnoreCase(map.get(questionItems.get(no).getQuestionId()).get(i).getUserChoice())){
////                    textViewAnswers.setBackground(getResources().getDrawable(R.drawable.test_name_ans_box_active));
//                    textViewAnswers.setBackgroundColor(getResources().getColor(R.color.green));
//                }
//                /*else if(!map.get(questionItems.get(no).getQuestionId()).get(i).getIsRight().equalsIgnoreCase(map.get(questionItems.get(no).getQuestionId()).get(i).getUserChoice())){
//                    textViewAnswers.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//                }*/
//                else{
//                    textViewAnswers.setBackground(getResources().getDrawable(R.drawable.test_name_ans_box_inactive));
//                }

                textViewAnswers.setId(Integer.parseInt(map.get(questionItems.get(no).getQuestionId()).get(i).getChoiceId()));
                textViewAnswers.setText(map.get(questionItems.get(no).getQuestionId()).get(i).getChoice());
                textViewAnswers.setTextColor(getResources().getColor(R.color.black));
                textViewAnswers.setTextSize(16);

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
//                {
//                    textViewAnswers.setPadding(15,15,10,15);
//                }
//                else {
//                    textViewAnswers.setPadding(50, 15, 10, 15);
//                }

                dynamicCheckBox.addView(textViewAnswers);
            }

            Log.d("hihi_", i + map.get(questionItems.get(no).getQuestionId()).get(i).getUserChoice());
//            if (map.get(questionItems.get(no).getQuestionId()).get(i).getUserChoice().equalsIgnoreCase("1"))
//            {
//                cb.setChecked(true);
//            }
//
//            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//
//                    for (int i = 0; i < map.get(questionItems.get(no).getQuestionId()).size(); i++) {
//                        if (map.get(questionItems.get(no).getQuestionId()).get(i).getChoiceId().equalsIgnoreCase(String.valueOf(cb.getId()))) {
//                            if (b) {
//                                cb.setButtonDrawable(getResources().getDrawable(R.drawable.checkbox_pressed));
//                                map.get(questionItems.get(no).getQuestionId()).get(i).setUserChoice("1");
//                            } else {
//                                cb.setButtonDrawable(getResources().getDrawable(R.drawable.checkbox_regular));
//                                map.get(questionItems.get(no).getQuestionId()).get(i).setUserChoice("0");
//                            }
//                        }
//                    }
//                }
//            });
//

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
//                    final Dialog settingsDialog = new Dialog(ReviewTestActivity.this);
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
        }
    }
}
