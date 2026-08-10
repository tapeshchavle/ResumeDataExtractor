package in.service;

import in.model.ATSResult;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Core ATS scoring engine.
 * Analyzes resume text and produces a weighted score out of 100,
 * with category breakdowns differentiated by profile type.
 *
 * Now includes:
 *  - Hyperlink-aware contact detection (PDF/DOCX embedded URLs)
 *  - Role-fit analysis (18 roles)
 *  - Detailed issue/mistake tracking with fix suggestions
 *  - Expanded skill dictionary with 400+ keywords
 *
 * Scoring Categories & Weights:
 *
 *   Category                   Fresher   Experienced
 *   ─────────────────────────  ───────   ───────────
 *   Keyword / Skill Match       35          30
 *   Education Quality           20          10
 *   Experience Section           5          25
 *   Resume Formatting           15          15
 *   Action Verbs & Impact       10          10
 *   Contact Info                10           5
 *   Certifications & Projects    5           5
 */
public class ATSScorer {

    // ── Weight maps ──────────────────────────────────────────────

    private static final Map<String, Integer> FRESHER_WEIGHTS = Map.of(
        "keywordMatch",     35,
        "education",        20,
        "experience",        5,
        "formatting",       15,
        "actionVerbs",      10,
        "contactInfo",      10,
        "certifications",    5
    );

    private static final Map<String, Integer> EXPERIENCED_WEIGHTS = Map.of(
        "keywordMatch",     30,
        "education",        10,
        "experience",       25,
        "formatting",       15,
        "actionVerbs",      10,
        "contactInfo",       5,
        "certifications",    5
    );

    // ── Regex patterns ───────────────────────────────────────────

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}");

    private static final Pattern PHONE_PATTERN =
        Pattern.compile("(\\+?\\d{1,3}[\\s\\-]?)?(\\(?\\d{2,4}\\)?[\\s\\-]?)?\\d{3,4}[\\s\\-]?\\d{3,4}");

    private static final Pattern LINKEDIN_PATTERN =
        Pattern.compile("linkedin\\.com/in/[a-zA-Z0-9\\-_]+", Pattern.CASE_INSENSITIVE);

    private static final Pattern GITHUB_PATTERN =
        Pattern.compile("github\\.com/[a-zA-Z0-9\\-_]+", Pattern.CASE_INSENSITIVE);

    private static final Pattern QUANTIFIED_PATTERN =
        Pattern.compile("\\d+\\s*%|\\$\\s*[\\d,]+|\\d+[xX]|\\d+\\s*(users|clients|projects|teams|members|applications|servers|requests|transactions|endpoints|engineers|developers|repositories|customers|partners|countries|regions|releases|deployments|microservices|services|modules|pages|views|downloads|installations)",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern URL_PATTERN =
        Pattern.compile("https?://[a-zA-Z0-9.\\-/_%~:?#\\[\\]@!$&'()*+,;=]+", Pattern.CASE_INSENSITIVE);

    // ── Main scoring method ──────────────────────────────────────

    /**
     * Score the given resume text with hyperlink URL awareness.
     *
     * @param resumeText    plain text extracted from the resume
     * @param extractedUrls hyperlink URLs from PDF annotations / DOCX relationships
     * @param profileType   "fresher" or "experienced"
     * @return full ATSResult with score, breakdown, skills, roles, issues, recommendations
     */
    public ATSResult score(String resumeText, List<String> extractedUrls, String profileType) {
        if (resumeText == null || resumeText.isBlank()) {
            return emptyResult(profileType);
        }

        if (extractedUrls == null) extractedUrls = List.of();

        String textLower = resumeText.toLowerCase();

        // Combine plain text with hyperlink URLs for contact detection
        String combinedText = buildCombinedText(resumeText, extractedUrls);
        String combinedLower = combinedText.toLowerCase();

        Map<String, Integer> weights = "experienced".equalsIgnoreCase(profileType)
            ? EXPERIENCED_WEIGHTS : FRESHER_WEIGHTS;

        // Analyze each category (returns 0.0 – 1.0 ratio)
        double keywordRatio    = scoreKeywords(textLower);
        double educationRatio  = scoreEducation(textLower, profileType);
        double experienceRatio = scoreExperience(textLower, profileType);
        double formattingRatio = scoreFormatting(resumeText);
        double actionVerbRatio = scoreActionVerbs(textLower);
        double contactRatio    = scoreContactInfo(combinedText);
        double certRatio       = scoreCertifications(textLower);

        // Compute weighted points
        Map<String, Integer> breakdown = new LinkedHashMap<>();
        breakdown.put("keywordMatch",   (int) Math.round(keywordRatio    * weights.get("keywordMatch")));
        breakdown.put("education",      (int) Math.round(educationRatio  * weights.get("education")));
        breakdown.put("experience",     (int) Math.round(experienceRatio * weights.get("experience")));
        breakdown.put("formatting",     (int) Math.round(formattingRatio * weights.get("formatting")));
        breakdown.put("actionVerbs",    (int) Math.round(actionVerbRatio * weights.get("actionVerbs")));
        breakdown.put("contactInfo",    (int) Math.round(contactRatio    * weights.get("contactInfo")));
        breakdown.put("certifications", (int) Math.round(certRatio       * weights.get("certifications")));

        int totalScore = breakdown.values().stream().mapToInt(Integer::intValue).sum();
        totalScore = Math.min(100, Math.max(0, totalScore));

        // Skill analysis
        List<String> matched = findMatchedSkills(textLower);
        List<String> missing = findMissingSkills(textLower, profileType);

        // Contact info flags (check BOTH plain text AND hyperlink URLs)
        boolean hasEmail    = EMAIL_PATTERN.matcher(combinedText).find();
        boolean hasPhone    = PHONE_PATTERN.matcher(combinedText).find();
        boolean hasLinkedIn = LINKEDIN_PATTERN.matcher(combinedLower).find();
        boolean hasGitHub   = GITHUB_PATTERN.matcher(combinedLower).find();

        int wordCount = resumeText.trim().split("\\s+").length;

        // Detailed issues
        List<Map<String, String>> issues = detectIssues(
            textLower, combinedLower, profileType, matched,
            hasEmail, hasPhone, hasLinkedIn, hasGitHub,
            wordCount, breakdown, weights
        );

        // Role recommendations
        List<Map<String, Object>> suggestedRoles = matchRoles(textLower);

        // Recommendations
        List<String> recommendations = generateRecommendations(
            textLower, profileType, matched, missing,
            hasEmail, hasPhone, hasLinkedIn, hasGitHub,
            wordCount, totalScore, breakdown, weights
        );

        // Build result
        ATSResult result = new ATSResult();
        result.setScore(totalScore);
        result.setGrade(ATSResult.gradeFromScore(totalScore));
        result.setProfileType(profileType);
        result.setBreakdown(breakdown);
        result.setMaxBreakdown(new LinkedHashMap<>(weights));
        result.setMatchedSkills(matched);
        result.setMissingSkills(missing);
        result.setRecommendations(recommendations);
        result.setIssues(issues);
        result.setSuggestedRoles(suggestedRoles);
        result.setWordCount(wordCount);
        result.setHasEmail(hasEmail);
        result.setHasPhone(hasPhone);
        result.setHasLinkedIn(hasLinkedIn);
        result.setHasGitHub(hasGitHub);

        return result;
    }

    /**
     * Legacy overload for backward compatibility.
     */
    public ATSResult score(String resumeText, String profileType) {
        return score(resumeText, List.of(), profileType);
    }

    // ═════════════════════════════════════════════════════════════
    //  HELPER: combine plain text + hyperlink URLs
    // ═════════════════════════════════════════════════════════════

    private String buildCombinedText(String plainText, List<String> urls) {
        if (urls.isEmpty()) return plainText;
        StringBuilder sb = new StringBuilder(plainText);
        sb.append("\n\n__EXTRACTED_HYPERLINKS__\n");
        for (String url : urls) {
            sb.append(url).append("\n");
        }
        return sb.toString();
    }

    // ══════════════════════════════════════════════════════════════
    //  Category Scoring (each returns 0.0 – 1.0)
    // ══════════════════════════════════════════════════════════════

    private double scoreKeywords(String textLower) {
        Set<String> allSkills = SkillDictionary.allKeywords();
        int matched = 0;
        for (String skill : allSkills) {
            if (containsWholePhrase(textLower, skill)) {
                matched++;
            }
        }
        // Graduated scale: 5 skills=0.4, 10=0.7, 15=0.9, 18+=1.0
        if (matched >= 18) return 1.0;
        if (matched >= 15) return 0.85 + (matched - 15) * 0.05;
        if (matched >= 10) return 0.65 + (matched - 10) * 0.04;
        if (matched >= 5)  return 0.35 + (matched - 5) * 0.06;
        return matched * 0.07;
    }

    private double scoreEducation(String textLower, String profileType) {
        double score = 0.0;

        if (containsAnySection(textLower, "education", "academic", "qualification")) {
            score += 0.25;
        }

        int degreeCount = 0;
        for (String kw : SkillDictionary.EDUCATION_KEYWORDS) {
            if (containsWholePhrase(textLower, kw)) {
                degreeCount++;
            }
        }
        score += Math.min(0.35, degreeCount * 0.07);

        if (textLower.contains("gpa") || textLower.contains("cgpa") ||
            textLower.contains("percentage") ||
            Pattern.compile("\\d+\\.\\d+\\s*/\\s*(10|4)").matcher(textLower).find()) {
            score += 0.15;
        }

        if (textLower.contains("computer science") || textLower.contains("information technology") ||
            textLower.contains("software engineering") || textLower.contains("data science") ||
            textLower.contains("artificial intelligence")) {
            score += 0.15;
        }

        // Bonus for advanced degrees
        if (textLower.contains("master") || textLower.contains("m.tech") || textLower.contains("mba") ||
            textLower.contains("ph.d") || textLower.contains("phd")) {
            score += 0.1;
        }

        return Math.min(1.0, score);
    }

    private double scoreExperience(String textLower, String profileType) {
        double score = 0.0;

        if (containsAnySection(textLower, "experience", "employment", "work history", "professional experience")) {
            score += 0.2;
        }

        Pattern yearPattern = Pattern.compile("(20\\d{2}\\s*[-–—]\\s*(20\\d{2}|present|current|ongoing))", Pattern.CASE_INSENSITIVE);
        Matcher ym = yearPattern.matcher(textLower);
        int yearRanges = 0;
        while (ym.find()) yearRanges++;
        score += Math.min(0.25, yearRanges * 0.08);

        Matcher qm = QUANTIFIED_PATTERN.matcher(textLower);
        int quantified = 0;
        while (qm.find()) quantified++;
        score += Math.min(0.25, quantified * 0.05);

        // Check for job title patterns
        Pattern titlePattern = Pattern.compile("(software|senior|junior|lead|principal|staff|intern|developer|engineer|architect|manager|director|vp|head|consultant|analyst|specialist|associate)",
            Pattern.CASE_INSENSITIVE);
        Matcher tm = titlePattern.matcher(textLower);
        int titles = 0;
        while (tm.find()) titles++;
        score += Math.min(0.15, titles * 0.03);

        if ("fresher".equalsIgnoreCase(profileType)) {
            if (textLower.contains("internship") || textLower.contains("intern")) {
                score += 0.1;
            }
            if (containsAnySection(textLower, "project", "academic project", "personal project")) {
                score += 0.1;
            }
        }

        return Math.min(1.0, score);
    }

    private double scoreFormatting(String originalText) {
        double score = 0.0;
        String[] lines = originalText.split("\\n");
        String lower = originalText.toLowerCase();

        // Word count scoring (250–800 ideal)
        int wordCount = originalText.trim().split("\\s+").length;
        if (wordCount >= 250 && wordCount <= 800) {
            score += 0.25;
        } else if (wordCount >= 150 && wordCount <= 1100) {
            score += 0.15;
        } else if (wordCount >= 50) {
            score += 0.05;
        }

        // Section headers found
        int sectionCount = 0;
        for (String header : SkillDictionary.SECTION_HEADERS) {
            if (containsWholePhrase(lower, header)) {
                sectionCount++;
            }
        }
        score += Math.min(0.25, sectionCount * 0.05);

        // Line count (structured document)
        if (lines.length >= 20) score += 0.2;
        else if (lines.length >= 10) score += 0.1;
        else if (lines.length >= 5)  score += 0.05;

        // Bullet point usage
        long bulletLines = Arrays.stream(lines)
            .filter(l -> l.trim().startsWith("-") || l.trim().startsWith("•") ||
                         l.trim().startsWith("●") || l.trim().startsWith("*") ||
                         l.trim().matches("^\\d+\\.\\s.*"))
            .count();
        score += Math.min(0.15, bulletLines * 0.02);

        // Penalty for excessive special characters
        long specialCount = originalText.chars()
            .filter(c -> "│┃┆┊►◆★○■□▪▫‣⁃".indexOf(c) >= 0)
            .count();
        if (specialCount < 5) score += 0.15;

        return Math.min(1.0, score);
    }

    private double scoreActionVerbs(String textLower) {
        int found = 0;
        for (String verb : SkillDictionary.ACTION_VERBS) {
            if (containsWholePhrase(textLower, verb)) {
                found++;
            }
        }
        // Graduated: 3=0.5, 5=0.75, 8+=1.0
        if (found >= 8) return 1.0;
        if (found >= 5) return 0.7 + (found - 5) * 0.1;
        if (found >= 3) return 0.4 + (found - 3) * 0.15;
        return found * 0.13;
    }

    private double scoreContactInfo(String combinedText) {
        double score = 0.0;
        String lower = combinedText.toLowerCase();
        if (EMAIL_PATTERN.matcher(combinedText).find())  score += 0.35;
        if (PHONE_PATTERN.matcher(combinedText).find())  score += 0.30;
        if (LINKEDIN_PATTERN.matcher(lower).find())      score += 0.20;
        if (GITHUB_PATTERN.matcher(lower).find())        score += 0.15;
        return Math.min(1.0, score);
    }

    private double scoreCertifications(String textLower) {
        double score = 0.0;

        if (containsAnySection(textLower, "certification", "certificate", "course", "training", "license")) {
            score += 0.25;
        }

        int certCount = 0;
        for (String kw : SkillDictionary.CERTIFICATION_KEYWORDS) {
            if (containsWholePhrase(textLower, kw)) {
                certCount++;
            }
        }
        score += Math.min(0.35, certCount * 0.07);

        if (containsAnySection(textLower, "project", "projects")) {
            score += 0.2;
        }

        // Bonus for specific well-known certs
        if (textLower.contains("aws certified") || textLower.contains("google certified") ||
            textLower.contains("azure certified") || textLower.contains("cka") ||
            textLower.contains("pmp")) {
            score += 0.2;
        }

        return Math.min(1.0, score);
    }

    // ══════════════════════════════════════════════════════════════
    //  Skill Detection
    // ══════════════════════════════════════════════════════════════

    private List<String> findMatchedSkills(String textLower) {
        List<String> matched = new ArrayList<>();
        for (String skill : SkillDictionary.allTechnicalSkills()) {
            if (containsWholePhrase(textLower, skill)) {
                matched.add(capitalizeSkill(skill));
            }
        }
        for (String skill : SkillDictionary.SOFT_SKILLS) {
            if (containsWholePhrase(textLower, skill)) {
                matched.add(capitalizeSkill(skill));
            }
        }
        matched.sort(String::compareToIgnoreCase);
        // Deduplicate
        return matched.stream().distinct().collect(Collectors.toList());
    }

    private List<String> findMissingSkills(String textLower, String profileType) {
        Set<String> importantSkills = new LinkedHashSet<>();

        importantSkills.addAll(List.of("git", "docker", "sql", "linux", "agile", "rest api", "ci/cd"));

        if ("fresher".equalsIgnoreCase(profileType)) {
            importantSkills.addAll(List.of(
                "python", "java", "javascript", "react", "node.js",
                "html", "css", "mysql", "mongodb", "problem solving",
                "data structures", "algorithms", "oop", "git"
            ));
        } else {
            importantSkills.addAll(List.of(
                "aws", "kubernetes", "microservices", "system design",
                "leadership", "mentoring", "terraform", "redis", "kafka",
                "monitoring", "performance optimization", "scalability"
            ));
        }

        List<String> missing = new ArrayList<>();
        for (String skill : importantSkills) {
            if (!containsWholePhrase(textLower, skill)) {
                missing.add(capitalizeSkill(skill));
            }
        }
        return missing.size() > 10 ? missing.subList(0, 10) : missing;
    }

    // ══════════════════════════════════════════════════════════════
    //  ROLE MATCHING (NEW)
    // ══════════════════════════════════════════════════════════════

    private List<Map<String, Object>> matchRoles(String textLower) {
        List<Map<String, Object>> roles = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : SkillDictionary.ROLE_PROFILES.entrySet()) {
            String roleName = entry.getKey();
            List<String> roleSkills = entry.getValue();

            List<String> matchedForRole = new ArrayList<>();
            List<String> missingForRole = new ArrayList<>();

            for (String skill : roleSkills) {
                if (containsWholePhrase(textLower, skill)) {
                    matchedForRole.add(capitalizeSkill(skill));
                } else {
                    missingForRole.add(capitalizeSkill(skill));
                }
            }

            int matchPct = (int) Math.round((double) matchedForRole.size() / roleSkills.size() * 100);

            Map<String, Object> roleResult = new LinkedHashMap<>();
            roleResult.put("role", roleName);
            roleResult.put("matchPercent", matchPct);
            roleResult.put("matchedCount", matchedForRole.size());
            roleResult.put("totalCount", roleSkills.size());
            roleResult.put("matchedSkills", matchedForRole);
            roleResult.put("missingSkills", missingForRole.size() > 5
                ? missingForRole.subList(0, 5) : missingForRole);

            roles.add(roleResult);
        }

        // Sort by match percentage descending
        roles.sort((a, b) -> ((Integer) b.get("matchPercent")).compareTo((Integer) a.get("matchPercent")));

        // Return top 8 roles
        return roles.size() > 8 ? roles.subList(0, 8) : roles;
    }

    // ══════════════════════════════════════════════════════════════
    //  ISSUE DETECTION (NEW)
    // ══════════════════════════════════════════════════════════════

    private List<Map<String, String>> detectIssues(
            String textLower, String combinedLower, String profileType,
            List<String> matchedSkills,
            boolean hasEmail, boolean hasPhone, boolean hasLinkedIn, boolean hasGitHub,
            int wordCount,
            Map<String, Integer> breakdown, Map<String, Integer> weights
    ) {
        List<Map<String, String>> issues = new ArrayList<>();

        // ── Contact Issues ──────────────────────────────────────
        if (!hasEmail) {
            issues.add(issue("critical", "Contact", "Missing Email Address",
                "No email address was found in your resume.",
                "Add a professional email (e.g., yourname@gmail.com) to the top header of your resume."));
        }
        if (!hasPhone) {
            issues.add(issue("critical", "Contact", "Missing Phone Number",
                "No phone number was detected.",
                "Include your phone number with country code (e.g., +91-XXXXX-XXXXX) in the resume header."));
        }
        if (!hasLinkedIn) {
            issues.add(issue("warning", "Contact", "Missing LinkedIn Profile",
                "No LinkedIn URL was found in the resume text or hyperlinks.",
                "Add your LinkedIn profile URL (linkedin.com/in/yourname). You can add it as a hyperlink or plain text."));
        }
        if (!hasGitHub) {
            issues.add(issue("info", "Contact", "Missing GitHub Profile",
                "No GitHub URL was detected.",
                "Add your GitHub profile URL (github.com/yourname) to showcase your code and open-source contributions."));
        }

        // ── Skills Issues ───────────────────────────────────────
        if (matchedSkills.size() < 3) {
            issues.add(issue("critical", "Skills", "Very Few Technical Skills",
                "Only " + matchedSkills.size() + " technical skills were detected. ATS systems rely on keyword matching.",
                "Add a dedicated 'Skills' or 'Technical Skills' section listing your programming languages, frameworks, tools, and technologies."));
        } else if (matchedSkills.size() < 6) {
            issues.add(issue("warning", "Skills", "Low Skill Count",
                matchedSkills.size() + " skills detected. For better ATS scores, aim for 10+ relevant skills.",
                "Expand your skills section with specific technologies, tools, and frameworks you have experience with."));
        }

        if (!containsAnySection(textLower, "skills", "technical skills", "core competencies", "technologies", "tech stack")) {
            issues.add(issue("critical", "Skills", "No Skills Section Found",
                "ATS scanners look for a clear Skills/Technical Skills section.",
                "Create a dedicated section titled 'Technical Skills' or 'Skills' and list your technologies in a clear format."));
        }

        // ── Experience Issues ───────────────────────────────────
        if (!containsAnySection(textLower, "experience", "employment", "work experience", "professional experience")) {
            if ("experienced".equalsIgnoreCase(profileType)) {
                issues.add(issue("critical", "Experience", "No Experience Section",
                    "No work experience section was found in your resume.",
                    "Add a 'Work Experience' or 'Professional Experience' section with job titles, company names, dates, and bullet-point achievements."));
            }
        }

        Matcher qm = QUANTIFIED_PATTERN.matcher(textLower);
        int quantified = 0;
        while (qm.find()) quantified++;
        if (quantified < 2) {
            issues.add(issue("warning", "Experience", "Lack of Quantified Achievements",
                "Your resume has very few quantified results (numbers, percentages, metrics).",
                "Add specific metrics: 'Reduced API latency by 40%', 'Handled 10K+ daily active users', 'Led a team of 5 engineers'."));
        }

        // ── Education Issues ────────────────────────────────────
        if (!containsAnySection(textLower, "education", "academic", "qualification")) {
            issues.add(issue("warning", "Education", "No Education Section",
                "No education section was detected.",
                "Add an 'Education' section with your degree, institution name, graduation year, and GPA/CGPA if strong."));
        }

        if ("fresher".equalsIgnoreCase(profileType)) {
            if (!textLower.contains("gpa") && !textLower.contains("cgpa") && !textLower.contains("percentage")) {
                issues.add(issue("info", "Education", "GPA/CGPA Not Mentioned",
                    "No GPA or CGPA was found. This is especially important for freshers.",
                    "Add your CGPA or percentage if it's above 7.0/10 or 70%."));
            }
        }

        // ── Formatting Issues ───────────────────────────────────
        if (wordCount < 150) {
            issues.add(issue("critical", "Formatting", "Resume Too Short",
                "Your resume has only " + wordCount + " words. This is insufficient for ATS scanning.",
                "Aim for 300–600 words. Add more details about your projects, responsibilities, and technical skills."));
        } else if (wordCount > 1000) {
            issues.add(issue("warning", "Formatting", "Resume Too Long",
                "Your resume has " + wordCount + " words, which may exceed 2 pages.",
                "Trim your resume to 1–2 pages. Focus on the most relevant and recent experience."));
        }

        int sectionCount = 0;
        for (String header : SkillDictionary.SECTION_HEADERS) {
            if (containsWholePhrase(textLower, header)) sectionCount++;
        }
        if (sectionCount < 3) {
            issues.add(issue("warning", "Formatting", "Poor Section Structure",
                "Only " + sectionCount + " sections detected. A well-structured resume should have 4–6 clear sections.",
                "Use clear section headers: Summary, Skills, Experience, Education, Projects, Certifications."));
        }

        // ── Action Verbs Issues ─────────────────────────────────
        int verbCount = 0;
        for (String verb : SkillDictionary.ACTION_VERBS) {
            if (containsWholePhrase(textLower, verb)) verbCount++;
        }
        if (verbCount < 3) {
            issues.add(issue("warning", "Impact", "Weak Action Verbs",
                "Only " + verbCount + " strong action verbs found. Your resume should start bullet points with impactful verbs.",
                "Use verbs like: Architected, Engineered, Optimized, Spearheaded, Automated, Deployed, Scaled, Mentored."));
        }

        // ── Certification Issues ────────────────────────────────
        if ("experienced".equalsIgnoreCase(profileType)) {
            if (!textLower.contains("certified") && !textLower.contains("certification") &&
                !textLower.contains("certificate")) {
                issues.add(issue("info", "Certifications", "No Certifications Found",
                    "Professional certifications add credibility and can boost ATS scores.",
                    "Consider adding industry certifications like AWS Solutions Architect, CKA, PMP, or relevant Coursera/Udemy certificates."));
            }
        }

        // ── Projects Issues ─────────────────────────────────────
        if ("fresher".equalsIgnoreCase(profileType)) {
            if (!containsAnySection(textLower, "project", "projects")) {
                issues.add(issue("critical", "Projects", "No Projects Section",
                    "Projects are essential for freshers who lack work experience.",
                    "Add a 'Projects' section with 2–3 projects. Include: project name, tech stack used, your role, and key achievements."));
            }
        }

        // Sort: critical first, then warning, then info
        Map<String, Integer> severityOrder = Map.of("critical", 0, "warning", 1, "info", 2);
        issues.sort((a, b) -> severityOrder.getOrDefault(a.get("severity"), 3)
                            .compareTo(severityOrder.getOrDefault(b.get("severity"), 3)));

        return issues;
    }

    private Map<String, String> issue(String severity, String category, String title, String description, String fix) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("severity", severity);
        map.put("category", category);
        map.put("title", title);
        map.put("description", description);
        map.put("fix", fix);
        return map;
    }

    // ══════════════════════════════════════════════════════════════
    //  Recommendations
    // ══════════════════════════════════════════════════════════════

    private List<String> generateRecommendations(
            String textLower, String profileType,
            List<String> matched, List<String> missing,
            boolean hasEmail, boolean hasPhone, boolean hasLinkedIn, boolean hasGitHub,
            int wordCount, int totalScore,
            Map<String, Integer> breakdown, Map<String, Integer> weights
    ) {
        List<String> recs = new ArrayList<>();

        if (!hasEmail)    recs.add("Add a professional email address to your resume header.");
        if (!hasPhone)    recs.add("Include a phone number for recruiters to reach you.");
        if (!hasLinkedIn) recs.add("Add your LinkedIn profile URL to boost credibility.");
        if (!hasGitHub)   recs.add("Include a GitHub link to showcase your projects and code contributions.");

        if (matched.size() < 5) {
            recs.add("Add more technical skills relevant to your target role. ATS scanners rely heavily on keyword matching.");
        }
        if (!missing.isEmpty()) {
            String top3 = String.join(", ", missing.subList(0, Math.min(3, missing.size())));
            recs.add("Consider adding these in-demand skills: " + top3 + ".");
        }

        if (breakdown.getOrDefault("actionVerbs", 0) < weights.get("actionVerbs") * 0.5) {
            recs.add("Use stronger action verbs like 'Architected', 'Spearheaded', 'Optimized', 'Engineered' to describe your achievements.");
        }

        Matcher qm = QUANTIFIED_PATTERN.matcher(textLower);
        int quantified = 0;
        while (qm.find()) quantified++;
        if (quantified < 2) {
            recs.add("Add quantified achievements (e.g., 'Reduced API latency by 40%', 'Managed a team of 8 engineers').");
        }

        if (wordCount < 150) {
            recs.add("Your resume is too short (" + wordCount + " words). Aim for 300–600 words to provide enough detail.");
        } else if (wordCount > 1000) {
            recs.add("Your resume is quite long (" + wordCount + " words). Consider trimming to 1–2 pages for better ATS compatibility.");
        }

        if (breakdown.getOrDefault("formatting", 0) < weights.get("formatting") * 0.5) {
            recs.add("Improve resume structure by adding clear section headers: Education, Experience, Skills, Projects, Certifications.");
        }

        if ("fresher".equalsIgnoreCase(profileType)) {
            if (!textLower.contains("project")) {
                recs.add("Add a 'Projects' section showcasing personal or academic projects — this is critical for freshers.");
            }
            if (breakdown.getOrDefault("education", 0) < weights.get("education") * 0.5) {
                recs.add("Strengthen your Education section with GPA/CGPA, relevant coursework, and degree details.");
            }
        } else {
            if (breakdown.getOrDefault("experience", 0) < weights.get("experience") * 0.5) {
                recs.add("Expand your Experience section with role titles, company names, date ranges, and bullet-point achievements.");
            }
            if (!textLower.contains("certification") && !textLower.contains("certified")) {
                recs.add("Add professional certifications (AWS, Azure, PMP, etc.) to strengthen your profile.");
            }
        }

        if (totalScore >= 80) {
            recs.add(0, "🎯 Great resume! Focus on tailoring keywords to each specific job description for even better ATS results.");
        }

        return recs;
    }

    // ══════════════════════════════════════════════════════════════
    //  Utility
    // ══════════════════════════════════════════════════════════════

    private boolean containsWholePhrase(String text, String phrase) {
        if (phrase.contains(" ") || phrase.contains("-") || phrase.contains(".") || phrase.contains("/")) {
            return text.contains(phrase);
        }
        String regex = "\\b" + Pattern.quote(phrase) + "\\b";
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(text).find();
    }

    private boolean containsAnySection(String textLower, String... headers) {
        for (String header : headers) {
            if (containsWholePhrase(textLower, header)) return true;
        }
        return false;
    }

    private String capitalizeSkill(String skill) {
        if (skill == null || skill.isEmpty()) return skill;
        // Common abbreviations that should be uppercase
        Set<String> upperCase = Set.of(
            "sql", "aws", "gcp", "css", "html", "api", "ci/cd", "jwt",
            "sqs", "sns", "iam", "vpc", "rds", "ecs", "eks", "s3",
            "ec2", "aks", "ddd", "tdd", "bdd", "mvp", "mvc", "mvvm",
            "sdk", "ide", "json", "xml", "yaml", "gpu", "cpu", "ram",
            "ssd", "oop", "nlp", "llm", "rag", "sre", "sla", "slo", "sli",
            "etl", "dbt", "mlops", "ssl", "tls", "cors", "xss", "csrf",
            "rbac", "sso", "ner", "gan", "cqrs"
        );
        if (upperCase.contains(skill.toLowerCase())) {
            return skill.toUpperCase();
        }
        if (skill.length() <= 3 && !skill.contains(".")) {
            return skill.toUpperCase();
        }
        // Preserve case for known mixed-case names
        Map<String, String> knownCase = Map.ofEntries(
            Map.entry("node.js", "Node.js"), Map.entry("react.js", "React.js"),
            Map.entry("vue.js", "Vue.js"), Map.entry("next.js", "Next.js"),
            Map.entry("express.js", "Express.js"), Map.entry("nest.js", "NestJS"),
            Map.entry("angular", "Angular"), Map.entry("react", "React"),
            Map.entry("vue", "Vue"), Map.entry("svelte", "Svelte"),
            Map.entry("docker", "Docker"), Map.entry("kubernetes", "Kubernetes"),
            Map.entry("terraform", "Terraform"), Map.entry("jenkins", "Jenkins"),
            Map.entry("kafka", "Kafka"), Map.entry("redis", "Redis"),
            Map.entry("mongodb", "MongoDB"), Map.entry("mysql", "MySQL"),
            Map.entry("postgresql", "PostgreSQL"), Map.entry("graphql", "GraphQL"),
            Map.entry("typescript", "TypeScript"), Map.entry("javascript", "JavaScript"),
            Map.entry("python", "Python"), Map.entry("java", "Java"),
            Map.entry("spring boot", "Spring Boot"), Map.entry("spring", "Spring"),
            Map.entry("hibernate", "Hibernate"), Map.entry("maven", "Maven"),
            Map.entry("gradle", "Gradle"), Map.entry("git", "Git"),
            Map.entry("github", "GitHub"), Map.entry("gitlab", "GitLab"),
            Map.entry("jira", "Jira"), Map.entry("figma", "Figma"),
            Map.entry("pytorch", "PyTorch"), Map.entry("tensorflow", "TensorFlow"),
            Map.entry("langchain", "LangChain"), Map.entry("openai", "OpenAI"),
            Map.entry("elasticsearch", "Elasticsearch"), Map.entry("nginx", "Nginx"),
            Map.entry("intellij", "IntelliJ"), Map.entry("vscode", "VS Code"),
            Map.entry("tailwind", "Tailwind"), Map.entry("bootstrap", "Bootstrap"),
            Map.entry("flutter", "Flutter"), Map.entry("kotlin", "Kotlin"),
            Map.entry("swift", "Swift"), Map.entry("golang", "Golang"),
            Map.entry("rust", "Rust"), Map.entry("scala", "Scala")
        );
        String lower = skill.toLowerCase();
        if (knownCase.containsKey(lower)) return knownCase.get(lower);

        // Default: capitalize each word
        String[] words = skill.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i > 0) sb.append(' ');
            if (!words[i].isEmpty()) {
                sb.append(Character.toUpperCase(words[i].charAt(0)));
                if (words[i].length() > 1) sb.append(words[i].substring(1));
            }
        }
        return sb.toString();
    }

    private ATSResult emptyResult(String profileType) {
        ATSResult result = new ATSResult();
        result.setScore(0);
        result.setGrade("D");
        result.setProfileType(profileType);
        result.setBreakdown(Map.of());
        result.setMaxBreakdown("experienced".equalsIgnoreCase(profileType) ? EXPERIENCED_WEIGHTS : FRESHER_WEIGHTS);
        result.setMatchedSkills(List.of());
        result.setMissingSkills(List.of());
        result.setRecommendations(List.of("Could not extract any text from the uploaded file. Please ensure it is a valid PDF or DOCX."));
        result.setIssues(List.of());
        result.setSuggestedRoles(List.of());
        result.setWordCount(0);
        return result;
    }
}
