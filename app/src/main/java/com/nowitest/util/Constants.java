package com.nowitest.util;

import android.os.Environment;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by king on 2/10/15.
 */
public class Constants {

//    public static String SERVER_URL= "http://192.168.1.26/Mobiquiz/api/";
    public static final int DOWNLOAD_THREAD_POOL_SIZE = 10;
//    public static String SERVER_URL= "http://testifymobintia.com/Mobiquiz/api/";
//    public static String SERVER_URL= "http://192.168.1.21/nowitest/api/";
//    public static String SERVER_URL_IMAGE= "http://192.168.1.21/nowitest/uploads/Questions/";
//    public static String SERVER_URL= "http://192.168.0.101/nowitest/api/";
//    public static String SERVER_URL_IMAGE= "http://192.168.0.101/nowitest/uploads/Questions/";
//    public static String SERVER_URL= "http://testifymobintia.com/nowitest/api/";
//    public static String SERVER_URL_IMAGE= "http://testifymobintia.com/nowitest/uploads/Questions/";
//    public static String SERVER_URL= "http://198.162.1.44:8080/nowitest/api/";
    public static String SERVER_URL= "http://192.168.1.173:8080/nowitest/api/";
    public static String SERVER_URL_IMAGE= "http://192.168.1.173:8080/nowitest/uploads/Questions/";
    public static String getQuestion = "question/index.php/getquestion";
    public static String getAllTest = "test/index.php/getAllTest";
    public static String validateCode = "test/index.php/validateCode";
    public static String signIn = "student/index.php/signIn";
    public static String signin = "admin/index.php/signin";
    public static String getQuestions = "question/index.php/getQuestions";
    public static String submitTest = "test/index.php/submitTest";
    public static String getAnalytics = "test/index.php/getAnalytics";
    public static String studentAttemptedTest = "test/index.php/studentAttemptedTest";
    public static String getCourseSubList = "sub/index.php/getCourseSubList";
    public static String getSubjPdf = "sub/index.php/getSubjPdf";
    public static String getMostWaiting = "test/index.php/getTopWaiting";
    public static String getNotice = "notice/index.php/getNotice";
    public static String syncPDF = "student/index.php/syncPDF";
    public static String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static String status= "status";
    public static String status200= "200";
    public static String status400= "400";
    public static String message= "message";
    public static String data= "data";
    public static String ismobile= "ismobile";
    public static String course= "course";
    public static String subject= "subject";
    public static String test_id= "test_id";
    public static String title= "title";
    public static String test_time= "test_time";
    public static String code= "code";
    public static String total_marks= "total_marks";
    public static String total_questions= "total_questions";
    public static String password= "password";
    public static String email= "email";
    public static String first_name= "first_name";
    public static String last_name= "last_name";
    public static String student_id= "student_id";
    public static String sharedPreferenceUserId= "sharedPreferenceUserId";
    public static String sharedPreferenceFirstName= "sharedPreferenceFirstName";
    public static String sharedPreferenceLastName= "sharedPreferenceLastName";
    public static String sharedPreferenceCourse= "sharedPreferenceCourse";
    public static String sharedPreferenceUserType= "sharedPreferenceUserType";
    public static String user_choice= "user_choice";
    public static String correct_answer= "correct_answer";
    public static String question_marks= "question_marks";
    public static String time_taken= "time_taken";
    public static String imagepath= "imagepath";
    public static String userid= "userid";
    public static String QuestionNo= "QuestionNo";
    public static String timetaken= "timetaken";
    public static String correctAns= "correctAns";
    public static String wrongAns= "wrongAns";
    public static String notAttempt= "notAttempt";
    public static String aggregateCorrect= "aggregateCorrect";
    public static String aggregateWrong= "aggregateWrong";
    public static String isSpecial= "isSpecial";
    public static String cat_name= "cat_name";
    public static String cat_id= "cat_id";
    public static String pdf_id= "pdf_id";
    public static String subject_id= "subject_id";
    public static String syllabus= "syllabus";
    public static String file_name= "file_name";
    public static String Foldersyllabus= "syllabus";
    public static String FolderquestionImages= "Questions";
    public static String questionNo= "questionNo";
    public static String subjects= "subjects";
    public static String sub_id= "sub_id";
    public static String subject_name= "subject_name";
    public static String pdf= "pdf";
    public static String syllabus_id= "syllabus_id";
    public static String path= "path";
    public static String flashcard= "flashcard";
    public static String flashcard_id= "flashcard_id";
    public static String notice_id= "notice_id";
    public static String notice= "notice";
    public static String created_at= "created_at";
    public static String user_type= "user_type";
    public static String username= "username";
    public static String questions= "questions";


    public static String questionic="questionic";
    public static String id= "id";
    public static String question="question";
    public static String questionFile="questionFile";
    public static String is_file="is_file";
    public static String question_id="question_id";
    public static String options="options";
    public static String choice_id="choice_id";
    public static String choice="choice";
    public static String is_right="is_right";
    public static String flag= "flag";
    public static String flag1="1";
    public static String flag2="2";
    public static String flag3="3";
    public static String subName="subName";
    public static String idBundle= "idBundle";
    public static String name= "name";
    public static String api_keyValue= "145236";
    public static String api_key= "api_key";

    public static String isMobileValue= "1";
    public static String limit="limit";
    public static String topic_id="topic_id";

    public static void deleteAllFiles(){

        final String pathQuestions = Environment.getExternalStorageDirectory() + "/NowITest/Questions";
        final String pathSyllabus = Environment.getExternalStorageDirectory() + "/NowITest/syllabus";

        File fileQuestions = new File(pathQuestions);
        if( fileQuestions.exists() ) {
            if (fileQuestions.isDirectory()) {
                File[] filesCategory = fileQuestions.listFiles();
                System.out.println("Size main: "+filesCategory.length);
                for(int i=0; i<filesCategory.length; i++) {
                    if(filesCategory[i].isDirectory()) {
                    }
                    else {
                        filesCategory[i].delete();
                    }
                }
            }
            fileQuestions.delete();
        }

        File fileSyllabus = new File(pathSyllabus);
        if( fileSyllabus.exists() ) {
            if (fileSyllabus.isDirectory()) {
                File[] filesCategory = fileSyllabus.listFiles();
                System.out.println("Size main: "+filesCategory.length);
                for(int i=0; i<filesCategory.length; i++) {
                    if(filesCategory[i].isDirectory()) {
                    }
                    else {
                        filesCategory[i].delete();
                    }
                }
            }
            fileSyllabus.delete();
        }
    }
    public static String createPDFFolder(String folderName){
        createNowITestFolder();
        final String PATH = Environment.getExternalStorageDirectory() + "/NowITest/"+ folderName;
        File folder = new File(PATH);
        if (folder.exists()) {
        }else {
            folder.mkdir();
        }
        return PATH;
    }

    public static String createNowITestFolder(){

        final String PATH = Environment.getExternalStorageDirectory() + "/NowITest";
        File folder = new File(PATH);
        if (folder.exists()) {
        }else {
            folder.mkdir();
        }
        return PATH;
    }

    public static boolean fileExistsOrNot(String filePath){

        boolean status;
        File file = new File(filePath);
        if(file.exists())
            status=true;
        else
            status=false;

        return status;
    }

    public static double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    public static String replaceString(String str) {
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

//    public static void showDialog(final Activity myActivity){
//
//        final AlertDialog alertDialog = new AlertDialog.Builder(myActivity).create();
////
//        // Setting Dialog Message
//        alertDialog.setMessage(myActivity.getResources().getString(R.string.no_internet_connection));
//
//        alertDialog.setCanceledOnTouchOutside(false);
//
//        // Setting OK Button
//        alertDialog.setButton(myActivity.getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                // Write your code here to execute after dialog closed
//                alertDialog.dismiss();
//                myActivity.finish();
//            }
//        });
//
//        // Showing Alert Message
//        alertDialog.show();
//    }
}
