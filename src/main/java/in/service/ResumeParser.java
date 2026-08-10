package in.service;

import in.model.ResumeData;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.text.PDFTextStripper;

import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

/**
 * Extracts plain text AND embedded hyperlinks from PDF and DOCX resume files.
 *
 * The hyperlink extraction is critical because many resumes have LinkedIn/GitHub
 * URLs as clickable hyperlinks rather than plain-text URLs. Without this,
 * ATS scanners would miss them.
 */
public class ResumeParser {

    /**
     * Extracts text + hyperlinks from the given input stream.
     *
     * @param input    the file input stream
     * @param fileType "pdf" or "docx"
     * @param fileName original file name
     * @return ResumeData with raw text and extracted URLs
     * @throws IOException if parsing fails
     */
    public ResumeData parse(InputStream input, String fileType, String fileName) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("Input stream cannot be null");
        }

        return switch (fileType.toLowerCase()) {
            case "pdf"  -> parsePdf(input, fileName);
            case "docx" -> parseDocx(input, fileName);
            default     -> throw new IllegalArgumentException(
                "Unsupported file type: " + fileType + ". Only PDF and DOCX are supported.");
        };
    }

    /**
     * Legacy method for backward compatibility.
     */
    public String parseText(InputStream input, String fileType) throws IOException {
        ResumeData data = parse(input, fileType, "unknown");
        return data.getRawText();
    }

    // ═════════════════════════════════════════════════════════════
    //  PDF Parsing
    // ═════════════════════════════════════════════════════════════

    private ResumeData parsePdf(InputStream input, String fileName) throws IOException {
        byte[] bytes = input.readAllBytes();
        ResumeData data = new ResumeData();
        data.setFileName(fileName);
        data.setFileType("pdf");

        try (PDDocument doc = Loader.loadPDF(bytes)) {
            // 1. Extract plain text
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(doc);
            data.setRawText(text != null ? text.trim() : "");

            // 2. Extract hyperlink annotations from every page
            List<String> urls = new ArrayList<>();
            for (PDPage page : doc.getPages()) {
                try {
                    for (PDAnnotation annotation : page.getAnnotations()) {
                        if (annotation instanceof PDAnnotationLink link) {
                            PDAction action = link.getAction();
                            if (action instanceof PDActionURI uriAction) {
                                String uri = uriAction.getURI();
                                if (uri != null && !uri.isBlank()) {
                                    urls.add(uri.trim());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // Some PDFs have malformed annotations — skip gracefully
                }
            }
            data.setExtractedUrls(urls);
        }

        return data;
    }

    // ═════════════════════════════════════════════════════════════
    //  DOCX Parsing
    // ═════════════════════════════════════════════════════════════

    private ResumeData parseDocx(InputStream input, String fileName) throws IOException {
        ResumeData data = new ResumeData();
        data.setFileName(fileName);
        data.setFileType("docx");

        try (XWPFDocument doc = new XWPFDocument(input)) {
            // 1. Extract plain text
            StringBuilder sb = new StringBuilder();
            for (XWPFParagraph para : doc.getParagraphs()) {
                String text = para.getText();
                if (text != null && !text.isBlank()) {
                    sb.append(text.trim()).append("\n");
                }
            }
            data.setRawText(sb.toString().trim());

            // 2. Extract hyperlinks from document relationships
            List<String> urls = new ArrayList<>();
            try {
                String hyperlinkRelType = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/hyperlink";
                PackageRelationshipCollection rels = doc.getPackagePart()
                    .getRelationshipsByType(hyperlinkRelType);
                for (PackageRelationship rel : rels) {
                    String uri = rel.getTargetURI().toString();
                    if (uri != null && !uri.isBlank()) {
                        urls.add(uri.trim());
                    }
                }
            } catch (Exception e) {
                // Some DOCX files may not have hyperlink relationships
            }
            data.setExtractedUrls(urls);
        }

        return data;
    }
}
