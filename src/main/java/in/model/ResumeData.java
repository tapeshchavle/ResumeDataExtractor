package in.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds raw parsed data extracted from a resume file.
 */
public class ResumeData {

    private String rawText;
    private String fileName;
    private String fileType; // "pdf" or "docx"
    private int wordCount;
    private List<String> extractedUrls; // hyperlink URLs from PDF annotations / DOCX hyperlinks

    public ResumeData() {
        this.extractedUrls = new ArrayList<>();
    }

    public ResumeData(String rawText, String fileName, String fileType) {
        this.rawText = rawText;
        this.fileName = fileName;
        this.fileType = fileType;
        this.wordCount = rawText == null ? 0 : rawText.trim().split("\\s+").length;
        this.extractedUrls = new ArrayList<>();
    }

    public String getRawText()          { return rawText; }
    public String getFileName()         { return fileName; }
    public String getFileType()         { return fileType; }
    public int getWordCount()           { return wordCount; }
    public List<String> getExtractedUrls() { return extractedUrls; }

    public void setRawText(String rawText) {
        this.rawText = rawText;
        this.wordCount = rawText == null ? 0 : rawText.trim().split("\\s+").length;
    }
    public void setFileName(String fileName)             { this.fileName = fileName; }
    public void setFileType(String fileType)              { this.fileType = fileType; }
    public void setExtractedUrls(List<String> urls)       { this.extractedUrls = urls; }
    public void addExtractedUrl(String url)               { this.extractedUrls.add(url); }
}
