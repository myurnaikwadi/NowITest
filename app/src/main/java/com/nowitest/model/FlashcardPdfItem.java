package com.nowitest.model;

/**
 * Created by mobintia-android-developer-1 on 14/1/16.
 */
public class FlashcardPdfItem
{
   String id, flashcard_id, file_name, path;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlashcard_id() {
        return flashcard_id;
    }

    public void setFlashcard_id(String flashcard_id) {
        this.flashcard_id = flashcard_id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public FlashcardPdfItem(String id, String flashcard_id, String file_name, String path) {
        this.id = id;
        this.flashcard_id = flashcard_id;
        this.file_name = file_name;
        this.path = path;
    }

    public FlashcardPdfItem() {
    }


}
