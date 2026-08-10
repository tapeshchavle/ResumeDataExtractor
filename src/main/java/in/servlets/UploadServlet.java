package in.servlets;

import in.model.ATSResult;
import in.model.ResumeData;
import in.service.ATSScorer;
import in.service.ResumeParser;

import jakarta.json.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Handles resume file uploads and returns ATS score analysis as JSON.
 *
 * POST /upload
 *   - multipart/form-data
 *   - Fields: "resume" (file), "profileType" ("fresher"|"experienced")
 *   - Returns: JSON ATSResult with score, breakdown, skills, issues, roles
 */
@WebServlet(name = "UploadServlet", urlPatterns = {"/upload"})
@MultipartConfig(
    maxFileSize      = 10 * 1024 * 1024,  // 10 MB
    maxRequestSize   = 12 * 1024 * 1024,  // 12 MB
    fileSizeThreshold =  1 * 1024 * 1024   //  1 MB before writing to disk
)
public class UploadServlet extends HttpServlet {

    private final ResumeParser parser = new ResumeParser();
    private final ATSScorer scorer    = new ATSScorer();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        try {
            // 1. Read the uploaded file part
            Part filePart = request.getPart("resume");
            if (filePart == null || filePart.getSize() == 0) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "No resume file uploaded. Please select a PDF or DOCX file.");
                return;
            }

            // 2. Determine file type
            String fileName = getFileName(filePart);
            String fileType = detectFileType(fileName, filePart.getContentType());
            if (fileType == null) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Unsupported file format. Only PDF and DOCX files are accepted.");
                return;
            }

            // 3. Read profile type
            String profileType = request.getParameter("profileType");
            if (profileType == null || profileType.isBlank()) {
                profileType = "fresher";
            }
            profileType = profileType.trim().toLowerCase();
            if (!"fresher".equals(profileType) && !"experienced".equals(profileType)) {
                profileType = "fresher";
            }

            // 4. Parse resume — extracts text AND hyperlink URLs
            ResumeData resumeData;
            try (InputStream is = filePart.getInputStream()) {
                resumeData = parser.parse(is, fileType, fileName);
            }

            if (resumeData.getRawText() == null || resumeData.getRawText().isBlank()) {
                sendError(response, 422,
                    "Could not extract text from the uploaded file. The file may be image-based or corrupted.");
                return;
            }

            // 5. Score the resume (with hyperlink URLs for contact detection)
            ATSResult result = scorer.score(
                resumeData.getRawText(),
                resumeData.getExtractedUrls(),
                profileType
            );

            // 6. Serialize to JSON and respond
            String json = toJson(result);
            response.setStatus(HttpServletResponse.SC_OK);
            try (PrintWriter out = response.getWriter()) {
                out.print(json);
                out.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "An unexpected error occurred: " + e.getMessage());
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // ── Helpers ──────────────────────────────────────────────────

    private String getFileName(Part part) {
        String header = part.getHeader("content-disposition");
        if (header != null) {
            for (String token : header.split(";")) {
                if (token.trim().startsWith("filename")) {
                    return token.substring(token.indexOf('=') + 1).trim()
                               .replace("\"", "");
                }
            }
        }
        return "unknown";
    }

    private String detectFileType(String fileName, String contentType) {
        String nameLower = fileName.toLowerCase();
        if (nameLower.endsWith(".pdf")) return "pdf";
        if (nameLower.endsWith(".docx")) return "docx";

        if (contentType != null) {
            if (contentType.contains("pdf")) return "pdf";
            if (contentType.contains("wordprocessingml") ||
                contentType.contains("openxmlformats")) return "docx";
        }
        return null;
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        JsonObject json = Json.createObjectBuilder()
            .add("error", true)
            .add("message", message)
            .build();
        try (PrintWriter out = response.getWriter()) {
            out.print(json.toString());
            out.flush();
        }
    }

    /**
     * Serialize ATSResult to JSON using Jakarta JSON-P.
     */
    private String toJson(ATSResult r) {
        JsonObjectBuilder builder = Json.createObjectBuilder()
            .add("score",       r.getScore())
            .add("grade",       r.getGrade())
            .add("profileType", r.getProfileType())
            .add("wordCount",   r.getWordCount())
            .add("hasEmail",    r.isHasEmail())
            .add("hasPhone",    r.isHasPhone())
            .add("hasLinkedIn", r.isHasLinkedIn())
            .add("hasGitHub",   r.isHasGitHub());

        // Breakdown
        JsonObjectBuilder bdBuilder = Json.createObjectBuilder();
        if (r.getBreakdown() != null) {
            r.getBreakdown().forEach(bdBuilder::add);
        }
        builder.add("breakdown", bdBuilder);

        // Max breakdown
        JsonObjectBuilder maxBdBuilder = Json.createObjectBuilder();
        if (r.getMaxBreakdown() != null) {
            r.getMaxBreakdown().forEach(maxBdBuilder::add);
        }
        builder.add("maxBreakdown", maxBdBuilder);

        // Matched skills
        builder.add("matchedSkills",   toJsonArray(r.getMatchedSkills()));
        builder.add("missingSkills",   toJsonArray(r.getMissingSkills()));
        builder.add("recommendations", toJsonArray(r.getRecommendations()));

        // Issues (NEW)
        JsonArrayBuilder issuesBuilder = Json.createArrayBuilder();
        if (r.getIssues() != null) {
            for (Map<String, String> issue : r.getIssues()) {
                JsonObjectBuilder ib = Json.createObjectBuilder();
                issue.forEach(ib::add);
                issuesBuilder.add(ib);
            }
        }
        builder.add("issues", issuesBuilder);

        // Suggested Roles (NEW)
        JsonArrayBuilder rolesBuilder = Json.createArrayBuilder();
        if (r.getSuggestedRoles() != null) {
            for (Map<String, Object> role : r.getSuggestedRoles()) {
                JsonObjectBuilder rb = Json.createObjectBuilder();
                for (Map.Entry<String, Object> entry : role.entrySet()) {
                    Object val = entry.getValue();
                    if (val instanceof String s)       rb.add(entry.getKey(), s);
                    else if (val instanceof Integer i)  rb.add(entry.getKey(), i);
                    else if (val instanceof List<?> list) {
                        JsonArrayBuilder ab = Json.createArrayBuilder();
                        for (Object item : list) {
                            if (item instanceof String si) ab.add(si);
                        }
                        rb.add(entry.getKey(), ab);
                    }
                }
                rolesBuilder.add(rb);
            }
        }
        builder.add("suggestedRoles", rolesBuilder);

        return builder.build().toString();
    }

    private JsonArray toJsonArray(List<String> list) {
        JsonArrayBuilder ab = Json.createArrayBuilder();
        if (list != null) {
            list.forEach(ab::add);
        }
        return ab.build();
    }
}
