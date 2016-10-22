package com.nowitest.model;

/**
 * Created by mobintia-android-developer-1 on 14/1/16.
 */
public class PdfItem
{
   String subjectId, pdfId, pdfUrl, fileName;

    public PdfItem() {
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getPdfId() {
        return pdfId;
    }

    public void setPdfId(String pdfId) {
        this.pdfId = pdfId;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public PdfItem(String subjectId, String pdfId, String pdfUrl, String fileName) {
        this.subjectId = subjectId;
        this.pdfId = pdfId;
        this.pdfUrl = pdfUrl;
        this.fileName = fileName;
    }
}
