package com.nowitest.model;

/**
 * Created by mobintia-android-developer-1 on 14/1/16.
 */
public class FlashcardItem
{
   String flashcard_id, title;

    public String getFlashcard_id() {
        return flashcard_id;
    }

    public void setFlashcard_id(String flashcard_id) {
        this.flashcard_id = flashcard_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public FlashcardItem(String flashcard_id, String title) {
        this.flashcard_id = flashcard_id;
        this.title = title;
    }

    public FlashcardItem() {
    }


}
