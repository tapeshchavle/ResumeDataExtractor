package in.model;

import java.util.List;
import java.util.Map;

/**
 * Complete ATS analysis result returned to the frontend.
 */
public class ATSResult {

    private int score;                          // 0–100
    private String grade;                       // A+, A, B+, B, C, D
    private String profileType;                 // fresher | experienced

    private Map<String, Integer> breakdown;     // category → points earned
    private Map<String, Integer> maxBreakdown;  // category → max possible points

    private List<String> matchedSkills;
    private List<String> missingSkills;
    private List<String> recommendations;

    // NEW: detailed issues with severity, category, and fix suggestions
    private List<Map<String, String>> issues;

    // NEW: suggested roles with match percentage
    private List<Map<String, Object>> suggestedRoles;

    private int wordCount;
    private boolean hasEmail;
    private boolean hasPhone;
    private boolean hasLinkedIn;
    private boolean hasGitHub;

    // ── Score ────────────────────────────────────────
    public int getScore()          { return score; }
    public void setScore(int s)    { this.score = s; }

    public String getGrade()       { return grade; }
    public void setGrade(String g) { this.grade = g; }

    // ── Profile ─────────────────────────────────────
    public String getProfileType()       { return profileType; }
    public void setProfileType(String p) { this.profileType = p; }

    // ── Breakdown ───────────────────────────────────
    public Map<String, Integer> getBreakdown()            { return breakdown; }
    public void setBreakdown(Map<String, Integer> b)      { this.breakdown = b; }

    public Map<String, Integer> getMaxBreakdown()         { return maxBreakdown; }
    public void setMaxBreakdown(Map<String, Integer> b)   { this.maxBreakdown = b; }

    // ── Skills ──────────────────────────────────────
    public List<String> getMatchedSkills()                { return matchedSkills; }
    public void setMatchedSkills(List<String> s)          { this.matchedSkills = s; }

    public List<String> getMissingSkills()                { return missingSkills; }
    public void setMissingSkills(List<String> s)          { this.missingSkills = s; }

    // ── Recommendations ─────────────────────────────
    public List<String> getRecommendations()              { return recommendations; }
    public void setRecommendations(List<String> r)        { this.recommendations = r; }

    // ── Issues ──────────────────────────────────────
    public List<Map<String, String>> getIssues()                   { return issues; }
    public void setIssues(List<Map<String, String>> i)             { this.issues = i; }

    // ── Suggested Roles ─────────────────────────────
    public List<Map<String, Object>> getSuggestedRoles()           { return suggestedRoles; }
    public void setSuggestedRoles(List<Map<String, Object>> r)     { this.suggestedRoles = r; }

    // ── Metadata ────────────────────────────────────
    public int getWordCount()             { return wordCount; }
    public void setWordCount(int w)       { this.wordCount = w; }

    public boolean isHasEmail()           { return hasEmail; }
    public void setHasEmail(boolean e)    { this.hasEmail = e; }

    public boolean isHasPhone()           { return hasPhone; }
    public void setHasPhone(boolean p)    { this.hasPhone = p; }

    public boolean isHasLinkedIn()        { return hasLinkedIn; }
    public void setHasLinkedIn(boolean l) { this.hasLinkedIn = l; }

    public boolean isHasGitHub()          { return hasGitHub; }
    public void setHasGitHub(boolean g)   { this.hasGitHub = g; }

    // ── Grade helper ────────────────────────────────
    public static String gradeFromScore(int score) {
        if (score >= 90) return "A+";
        if (score >= 80) return "A";
        if (score >= 70) return "B+";
        if (score >= 60) return "B";
        if (score >= 45) return "C";
        return "D";
    }
}
