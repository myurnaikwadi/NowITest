package com.nowitest.databasehelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nowitest.model.ChoiceItem;
import com.nowitest.model.FlashcardItem;
import com.nowitest.model.FlashcardPdfItem;
import com.nowitest.model.MyQuizzItem;
import com.nowitest.model.PdfItem;
import com.nowitest.model.QuestionItem;
import com.nowitest.model.SubjectItem;
import com.nowitest.model.SyllabusItem;
import com.nowitest.model.SyllabusPdfItem;
import com.nowitest.model.TestItem;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {


	// Logcat tag
	private static final String LOG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 14;

	// Database Name
	private static final String DATABASE_NAME = "NowITest";

	// Table Names
//	private static final String TABLE_NOTIFICATION= "notification";
	private static final String TABLE_MYQUIZZ= "myquiz";
	private static final String TABLE_TEST= "test";
	private static final String TABLE_QUESTION= "question";
	private static final String TABLE_QUESTION_CHOICE= "question_choices";
	private static final String TABLE_SUBJECTS= "subjects";
	private static final String TABLE_SUBJECTS_PDF= "subjects_pdf";
	private static final String TABLE_SYLABUS= "syllabus";
	private static final String TABLE_SYLABUS_PDF= "syllabus_pdf";
	private static final String TABLE_FLASHCARD= "flashcard";
	private static final String TABLE_FLASHCARD_PDF= "flashcard_pdf";

	// Common column names
	private static final String KEY_ID = "id";
	private static final String KEY_QUESTION_NO = "no";
	private static final String KEY_QUESTION= "question";
	private static final String KEY_ANSWER= "answer";
	private static final String KEY_CORRECT_ANSWER= "correct_answer";


//	// Table Create Statements
//	// Todo table create statement
//	private static final String CREATE_TABLE_TODO = "CREATE TABLE "
//			+ TABLE_NOTIFICATION + "("
//			+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//			+ Constants.notificationText+ " TEXT,"
//			+ Constants.notificationDate+ " TEXT"+
//			")";

	private static final String CREATE_TABLE_TEST = "CREATE TABLE IF NOT EXISTS test (" +
			"test_id integer NOT NULL PRIMARY KEY AUTOINCREMENT, " +
			"title varchar(255) NOT NULL, " +
			"course varchar(20) NOT NULL," +
			"subject varchar(20) NOT NULL," +
			"test_time varchar(255) NOT NULL," +
			"code varchar(255) NOT NULL," +
			"total_marks varchar(20) NOT NULL," +
			"total_questions varchar(20) NOT NULL," +
			"created_at varchar(255) NULL," +
			"updated_at varchar(255) NULL," +
			"isEnabled int(11) NULL," +
			"isDeleted int(11) NULL" +
			")";

	private static final String CREATE_TABLE_QUESTION = "CREATE TABLE IF NOT EXISTS question (" +
			"question_id integer NOT NULL PRIMARY KEY AUTOINCREMENT," +
			"test_id integer NOT NULL," +
			"question text NOT NULL," +
			"questionFile text NOT NULL," +
			"quesIsFile varchar(10) NOT NULL," +
			"marks varchar(20) NOT NULL," +
			"timetaken varchar(255) NOT NULL," +
			"questionNo varchar(255) NOT NULL," +
			"randomNo varchar(255) NOT NULL," +
			"created_at varchar(255) NULL," +
			"updated_at varchar(255) NULL," +
			"isEnabled int(10) NULL," +
			"isDeleted int(10) NULL," +
			"FOREIGN KEY(test_id) REFERENCES test (test_id)"+
			")";

	private static final String CREATE_TABLE_QUESTION_CHOICE = "CREATE TABLE IF NOT EXISTS question_choices (" +
			"choice_id integer NOT NULL PRIMARY KEY AUTOINCREMENT," +
			"question_id integer NOT NULL," +
			"choice text NOT NULL," +
			"is_file varchar(10) NOT NULL," +
			"is_right varchar(10) NOT NULL," +
			"user_choice varchar(10) NOT NULL," +
			"created_at varchar(255) NULL," +
			"updated_at varchar(255) NULL," +
			"isEnabled int(10) NULL," +
			"isDeleted int(10) NULL," +
			"FOREIGN KEY(question_id) REFERENCES question (question_id)"+
			")";

	private static final String CREATE_TABLE_MYQUIZ= "CREATE TABLE "
			+ TABLE_MYQUIZZ+ "("
			+ KEY_QUESTION_NO+ " TEXT PRIMARY KEY,"
			+ KEY_QUESTION+ " TEXT,"
			+ KEY_ANSWER+ " TEXT,"
			+ KEY_CORRECT_ANSWER+ " TEXT"+
			")";

	private static final String CREATE_TABLE_SUBJECT= "CREATE TABLE "
			+ TABLE_SUBJECTS+ "("
			+ "sub_id"+ " int PRIMARY KEY,"
			+ "subject_name"+ " TEXT"+
			")";

	private static final String CREATE_TABLE_SUBJECT_PDF= "CREATE TABLE "
			+ TABLE_SUBJECTS_PDF+ "("
			+ "pdf_id"+ " int PRIMARY KEY,"
			+ "subject_id"+ " int,"
			+ "syllabus"+ " TEXT,"
			+ "file_name"+ " TEXT"+
			")";

	private static final String CREATE_TABLE_SYLLABUS= "CREATE TABLE "
			+ TABLE_SYLABUS+ "("
			+ "syllabus_id"+ " int PRIMARY KEY,"
			+ "title"+ " TEXT"+
			")";

	private static final String CREATE_TABLE_SYLLABUS_PDF= "CREATE TABLE "
			+ TABLE_SYLABUS_PDF+ "("
			+ "id"+ " int PRIMARY KEY,"
			+ "syllabus_id"+ " int,"
			+ "file_name"+ " TEXT,"
			+ "path"+ " TEXT"+
			")";

	private static final String CREATE_TABLE_FLASHCARD= "CREATE TABLE "
			+ TABLE_FLASHCARD+ "("
			+ "flashcard_id"+ " int PRIMARY KEY,"
			+ "title"+ " TEXT"+
			")";

	private static final String CREATE_TABLE_FLASHCARD_PDF= "CREATE TABLE "
			+ TABLE_FLASHCARD_PDF+ "("
			+ "id"+ " int PRIMARY KEY,"
			+ "flashcard_id"+ " int,"
			+ "file_name"+ " TEXT,"
			+ "path"+ " TEXT"+
			")";



	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
//		db.execSQL(CREATE_TABLE_TODO);
		db.execSQL(CREATE_TABLE_MYQUIZ);
		db.execSQL(CREATE_TABLE_TEST);
		db.execSQL(CREATE_TABLE_QUESTION);
		db.execSQL(CREATE_TABLE_QUESTION_CHOICE);
		db.execSQL(CREATE_TABLE_SUBJECT);
		db.execSQL(CREATE_TABLE_SUBJECT_PDF);
		db.execSQL(CREATE_TABLE_SYLLABUS);
		db.execSQL(CREATE_TABLE_SYLLABUS_PDF);
		db.execSQL(CREATE_TABLE_FLASHCARD);
		db.execSQL(CREATE_TABLE_FLASHCARD_PDF);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MYQUIZZ);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION_CHOICE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEST);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECTS_PDF);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SYLABUS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SYLABUS_PDF);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLASHCARD);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLASHCARD_PDF);

		onCreate(db);
	}

//	public long addInTo(NotificationItem cartItemSqlite)
//	{
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		ContentValues values = new ContentValues();
////		values.put(Constants.notificationId, cartItemSqlite.getId());
//		values.put(Constants.notificationText, cartItemSqlite.getTitle());
//		values.put(Constants.notificationDate, cartItemSqlite.getDate());
//
//		// insert row
//		long tag_id = db.insert(TABLE_NOTIFICATION, null, values);
//
//		return tag_id;
//	}

	public long addInToMyQuiz(MyQuizzItem myQuizzItem)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_QUESTION_NO, myQuizzItem.getQuestionId());
		values.put(KEY_QUESTION, myQuizzItem.getQuestion());
		values.put(KEY_ANSWER, myQuizzItem.getAnswer());
		values.put(KEY_CORRECT_ANSWER, myQuizzItem.getCorrectAnswer());

		// insert row
		long tag_id = db.insert(TABLE_MYQUIZZ, null, values);

		return tag_id;
	}


	public long addInToTest(TestItem testItem)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("test_id", testItem.getId());
		values.put("title", testItem.getTitle());
		values.put("code", testItem.getCode());
		values.put("course", testItem.getCourse());
		values.put("test_time", testItem.getDuration());
		values.put("total_marks", testItem.getMarks());
		values.put("subject", testItem.getSubject());
		values.put("total_questions", testItem.getTotalQuestions());

		// insert row
		long tag_id = db.insert(TABLE_TEST, null, values);

		return tag_id;
	}

	public long addInToQuestion(QuestionItem questionItem)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("question_id", questionItem.getQuestionId());
		values.put("test_id", questionItem.getTestId());
		values.put("question", questionItem.getQuestion());
		values.put("quesIsFile", questionItem.getQuesIsFile());
		values.put("marks", questionItem.getMarks());
		values.put("timetaken", questionItem.getTimeTaken());
		values.put("questionFile", questionItem.getQuestionFile());
		values.put("questionNo", questionItem.getQuestionNo());
		values.put("randomNo", questionItem.getRandomNo());

		// insert row
		long tag_id = db.insert(TABLE_QUESTION, null, values);

		return tag_id;
	}

	public long addInToSubject(SubjectItem subjectItem)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("sub_id", subjectItem.getSubjectId());
		values.put("subject_name", subjectItem.getSubjectName());


		// insert row
		long tag_id = db.insert(TABLE_SUBJECTS, null, values);

		return tag_id;
	}

	public long addInToSubjectPdf(PdfItem pdfItem)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("pdf_id", pdfItem.getPdfId());
		values.put("subject_id", pdfItem.getSubjectId());
		values.put("syllabus", pdfItem.getPdfUrl());
		values.put("file_name", pdfItem.getFileName());


		// insert row
		long tag_id = db.insert(TABLE_SUBJECTS_PDF, null, values);

		return tag_id;
	}


	public long addInToSyllabusPdf(SyllabusPdfItem syllabusPdfItem)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("id", syllabusPdfItem.getId());
		values.put("syllabus_id", syllabusPdfItem.getSyllabus_id());
		values.put("file_name", syllabusPdfItem.getFile_name());
		values.put("path", syllabusPdfItem.getPath());


		// insert row
		long tag_id = db.insert(TABLE_SYLABUS_PDF, null, values);

		return tag_id;
	}

	public long addInToSyllabus(SyllabusItem syllabusItem)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("syllabus_id", syllabusItem.getSyllabus_id());
		values.put("title", syllabusItem.getTitle());

		// insert row
		long tag_id = db.insert(TABLE_SYLABUS, null, values);

		return tag_id;
	}

	public long addInToFlashcard(FlashcardItem flashcardItem)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("flashcard_id", flashcardItem.getFlashcard_id());
		values.put("title", flashcardItem.getTitle());


		// insert row
		long tag_id = db.insert(TABLE_FLASHCARD, null, values);

		return tag_id;
	}

	public long addInToFlashcardPdf(FlashcardPdfItem flashcardPdfItem)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("id", flashcardPdfItem.getId());
		values.put("flashcard_id", flashcardPdfItem.getFlashcard_id());
		values.put("file_name", flashcardPdfItem.getFile_name());
		values.put("path", flashcardPdfItem.getPath());


		// insert row
		long tag_id = db.insert(TABLE_FLASHCARD_PDF, null, values);

		return tag_id;
	}


	public long addInToQuestionChoice(ChoiceItem choiceItem)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("choice_id", choiceItem.getChoiceId());
		values.put("question_id", choiceItem.getQuestionID());
		values.put("choice", choiceItem.getChoice());
		values.put("is_file", choiceItem.getIs_file());
		values.put("is_right", choiceItem.getIsRight());
		values.put("user_choice", choiceItem.getUserChoice());

		// insert row
		long tag_id = db.insert(TABLE_QUESTION_CHOICE, null, values);

		return tag_id;
	}

	public long getCount(String tableName) {
		SQLiteDatabase db = this.getReadableDatabase();
		long cnt  = DatabaseUtils.queryNumEntries(db, tableName);
		db.close();
		return cnt;
	}

	public ArrayList<TestItem> getAllTest()
	{
		ArrayList<TestItem> tags = new ArrayList<TestItem>();
		String selectQuery = "Select * from test";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				TestItem t = new TestItem();
				t.setId(c.getString((c.getColumnIndex("test_id"))));
				t.setTitle(c.getString((c.getColumnIndex("title"))));
				t.setCourse(c.getString((c.getColumnIndex("course"))));
				t.setSubject(c.getString((c.getColumnIndex("subject"))));
				t.setDuration(c.getString((c.getColumnIndex("test_time"))));
				t.setCode(c.getString((c.getColumnIndex("code"))));
				t.setMarks(c.getString((c.getColumnIndex("total_marks"))));
				t.setTotalQuestions(c.getString((c.getColumnIndex("total_questions"))));

				// adding to tags list
				tags.add(t);
			} while (c.moveToNext());
		}
		return tags;
	}

	public ArrayList<QuestionItem> getAllQuestions(String testID)
	{
		ArrayList<QuestionItem> tags = new ArrayList<QuestionItem>();
		String selectQuery = "Select * from question where test_id="+testID;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				QuestionItem t = new QuestionItem();
				t.setQuestionId(c.getString((c.getColumnIndex("question_id"))));
				t.setQuestion(c.getString((c.getColumnIndex("question"))));
				t.setTestId(c.getString((c.getColumnIndex("test_id"))));
				t.setQuesIsFile(c.getString((c.getColumnIndex("quesIsFile"))));
				t.setMarks(c.getString((c.getColumnIndex("marks"))));
				t.setTimeTaken(c.getString((c.getColumnIndex("timetaken"))));
				t.setQuestionFile(c.getString((c.getColumnIndex("questionFile"))));
				t.setQuestionNo(c.getString((c.getColumnIndex("questionNo"))));
				t.setRandomNo(c.getString((c.getColumnIndex("randomNo"))));

				// adding to tags list
				tags.add(t);
			} while (c.moveToNext());
		}
		return tags;
	}

	public ArrayList<SubjectItem> getAllSubject()
	{
		ArrayList<SubjectItem> tags = new ArrayList<SubjectItem>();
		String selectQuery = "Select * from subjects";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				SubjectItem t = new SubjectItem();
				t.setSubjectId(c.getString((c.getColumnIndex("sub_id"))));
				t.setSubjectName(c.getString((c.getColumnIndex("subject_name"))));
				// adding to tags list
				tags.add(t);
			} while (c.moveToNext());
		}
		return tags;
	}

	public ArrayList<PdfItem> getSubjectPdf(String subjectId)
	{
		ArrayList<PdfItem> tags = new ArrayList<PdfItem>();
		String selectQuery = "Select * from subjects_pdf where subject_id="+subjectId;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				PdfItem t = new PdfItem();
				t.setPdfId(c.getString((c.getColumnIndex("pdf_id"))));
				t.setSubjectId(c.getString((c.getColumnIndex("subject_id"))));
				t.setPdfUrl(c.getString((c.getColumnIndex("syllabus"))));
				t.setFileName(c.getString((c.getColumnIndex("file_name"))));
				// adding to tags list
				tags.add(t);
			} while (c.moveToNext());
		}
		return tags;
	}

	public ArrayList<SyllabusPdfItem> getSyllabusPdf(String syllabusId)
	{
		ArrayList<SyllabusPdfItem> tags = new ArrayList<SyllabusPdfItem>();
		String selectQuery = "Select * from syllabus_pdf where syllabus_id="+syllabusId;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				SyllabusPdfItem t = new SyllabusPdfItem();
				t.setId(c.getString((c.getColumnIndex("id"))));
				t.setSyllabus_id(c.getString((c.getColumnIndex("syllabus_id"))));
				t.setFile_name(c.getString((c.getColumnIndex("file_name"))));
				t.setPath(c.getString((c.getColumnIndex("path"))));
				// adding to tags list
				tags.add(t);
			} while (c.moveToNext());
		}
		return tags;
	}

	public ArrayList<SyllabusItem> getAllSyllabus()
	{
		ArrayList<SyllabusItem> tags = new ArrayList<SyllabusItem>();
		String selectQuery = "Select * from syllabus";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				SyllabusItem t = new SyllabusItem();
				t.setSyllabus_id(c.getString((c.getColumnIndex("syllabus_id"))));
				t.setTitle(c.getString((c.getColumnIndex("title"))));
				// adding to tags list
				tags.add(t);
			} while (c.moveToNext());
		}
		return tags;
	}

	public ArrayList<FlashcardItem> getAllFlashcard()
	{
		ArrayList<FlashcardItem> tags = new ArrayList<FlashcardItem>();
		String selectQuery = "Select * from flashcard";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				FlashcardItem t = new FlashcardItem();
				t.setFlashcard_id(c.getString((c.getColumnIndex("flashcard_id"))));
				t.setTitle(c.getString((c.getColumnIndex("title"))));
				// adding to tags list
				tags.add(t);
			} while (c.moveToNext());
		}
		return tags;
	}


	public ArrayList<FlashcardPdfItem> getFlashcardPdf(String flashcardId)
	{
		ArrayList<FlashcardPdfItem> tags = new ArrayList<FlashcardPdfItem>();
		String selectQuery = "Select * from flashcard_pdf where flashcard_id="+flashcardId;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				FlashcardPdfItem t = new FlashcardPdfItem();
				t.setId(c.getString((c.getColumnIndex("id"))));
				t.setFlashcard_id(c.getString((c.getColumnIndex("flashcard_id"))));
				t.setFile_name(c.getString((c.getColumnIndex("file_name"))));
				t.setPath(c.getString((c.getColumnIndex("path"))));
				// adding to tags list
				tags.add(t);
			} while (c.moveToNext());
		}
		return tags;
	}

	public ArrayList<ChoiceItem> getAllQuestionChoice(String questionId)
	{
		ArrayList<ChoiceItem> tags = new ArrayList<ChoiceItem>();
		String selectQuery = "Select question_id, choice_id, choice, is_file, is_right, user_choice from question_choices where question_id="+questionId;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				ChoiceItem t = new ChoiceItem();
				t.setChoiceId(c.getString((c.getColumnIndex("choice_id"))));
				t.setChoice(c.getString((c.getColumnIndex("choice"))));
				t.setIs_file(c.getString((c.getColumnIndex("is_file"))));
				t.setIsRight(c.getString((c.getColumnIndex("is_right"))));
				t.setQuestionID(c.getString((c.getColumnIndex("question_id"))));
				t.setUserChoice(c.getString((c.getColumnIndex("user_choice"))));

				// adding to tags list
				tags.add(t);
			} while (c.moveToNext());
		}
		return tags;
	}

	public void deleteAllSubject()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_SUBJECTS,null,null);
	}

	public void deleteAllSubjectPdf()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_SUBJECTS_PDF,null,null);
	}

	public void deleteAllSyllabus()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_SYLABUS,null,null);
	}

	public void deleteAllSyllabusPdf()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_SYLABUS_PDF,null,null);
	}

	public void deleteAllFlashcard()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_FLASHCARD,null,null);
	}

	public void deleteAllFlashcardPdf()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_FLASHCARD_PDF,null,null);
	}

	public void deleteAllQuestionChoices()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_QUESTION_CHOICE,null,null);
	}

	public void deleteAllQuestion()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_QUESTION,null,null);
	}

	public void deleteAllTest()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_TEST,null,null);
	}
//
//
//	public void deleteItem(String id) {
//		SQLiteDatabase db = this.getWritableDatabase();
//		db.delete(TABLE_NOTIFICATION, KEY_ID+ " = ?",
//				new String[]{String.valueOf(id) });
//	}

	public void deleteMyQuizItem(String id)
	{
		Log.d("APAP", id + "");
		Log.d("APAP", "DELETE FROM " + TABLE_MYQUIZZ + " where " + KEY_QUESTION_NO + "=" + id);
		SQLiteDatabase db = this.getWritableDatabase();
//		db.delete(TABLE_MYQUIZZ, KEY_QUESTION_NO + " = ?",
//				new String[]{String.valueOf(id)});
		db.execSQL("DELETE FROM " +TABLE_MYQUIZZ+ " where "+KEY_QUESTION_NO+"="+id);
	}

	public Boolean myQuizzItemExits(String id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String Query = "Select * from " + TABLE_MYQUIZZ + " where " + KEY_QUESTION_NO + " = " + id;
		Cursor cursor = db.rawQuery(Query, null);
		if(cursor.getCount() <= 0){
			cursor.close();
			return false;
		}
		cursor.close();
		return true;
	}
}
