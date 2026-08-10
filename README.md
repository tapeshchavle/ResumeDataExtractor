# ATS Resume Scorer & Analyzer

A production-grade **ATS (Applicant Tracking System) Resume Scorer and Career Fit Analyzer** built with **Jakarta EE 10** and deployed on an embedded **Apache Tomcat 10.1** server.

It extracts text and embedded hyperlinks from PDF and DOCX resumes, analyzes key skills, formatting, and impact metrics, and calculates an ATS compatibility score along with target role recommendations.

---

## Key Features

1. **Hyperlink-Aware Contact Extraction**:
   - Detects plain-text URLs as well as embedded PDF hyperlink annotations (`PDAnnotationLink`) and DOCX relationships.
   - Accurately checks for LinkedIn and GitHub profiles even when hyperlinked under anchor text like "LinkedIn" or "Portfolio".

2. **Differentiated Profile Scoring (Fresher vs. Experienced)**:
   - **Fresher Profile**: Emphasizes Education (20%), Skills (35%), Projects (5%), and Formatting (15%).
   - **Experienced Profile**: Emphasizes Work Experience (25%), Skills (30%), Action Verbs/Impact (10%), and Certifications (5%).

3. **"Best Fit Roles" Analysis**:
   - Matches candidate resumes against 18 tech role profiles including:
     - Java Backend Developer
     - Java Spring Boot Specialist
     - Spring AI / AI Engineer
     - Agentic AI Developer
     - Full Stack Developer
     - DevOps Engineer, Cloud Engineer (AWS/Azure), Data Scientist, SRE, and more.
   - Computes role match percentage (%) and highlights matched vs. missing skills for each role.

4. **Interactive "Issues & Fixes" Dropdown**:
   - Automatically detects structural, formatting, and content mistakes (e.g., missing contact info, low keyword density, missing projects, weak action verbs).
   - Categorizes issues by severity (**Critical**, **Warning**, **Info**) and provides actionable advice for fixing each issue in the resume.

5. **Skill Heatmap & Recommendation Engine**:
   - Powered by a 400+ keyword dictionary covering programming languages, frameworks, databases, cloud platforms, AI/ML tools, soft skills, and architecture concepts.
   - Displays matched skills and suggests missing high-impact keywords.

6. **Modern Dark Glassmorphism UI**:
   - Built with HTML5, CSS3 (Glassmorphism + CSS Animations), and JavaScript.
   - Drag-and-drop file upload zone, smooth progress step animation, and animated SVG score circle.

---

## Technology Stack

- **Backend Framework**: Jakarta EE 10 (Servlets 6.0, JSON-P)
- **Application Server**: Embedded Apache Tomcat 10.1 (managed via Cargo Maven Plugin)
- **Build System**: Apache Maven 3.x
- **PDF Parser**: Apache PDFBox 3.x
- **DOCX Parser**: Apache POI 5.x
- **Frontend**: Vanilla HTML5, CSS3, JavaScript (No external JS frameworks required)

---

## How to Build & Run

### Prerequisites
- **Java**: JDK 17 or higher
- **Maven**: Apache Maven 3.8+

### 1. Build the Project
Clone the repository and package the WAR file using Maven:
```bash
mvn clean package
```

### 2. Run the Embedded Tomcat Server
Start the embedded Tomcat 10 server on port `8080`:
```bash
mvn org.codehaus.cargo:cargo-maven3-plugin:1.10.14:run
```

### 3. Open in Browser
Open your browser and navigate to:
```
http://localhost:8080/ResumeDataExtractor/
```

---

## API Documentation

### `POST /upload`
Uploads a resume file for ATS analysis.

- **Content-Type**: `multipart/form-data`
- **Body Parameters**:
  - `resume`: File (`.pdf` or `.docx`, max 10MB)
  - `profileType`: String (`fresher` or `experienced`)

#### Sample Response:
```json
{
  "score": 88,
  "grade": "A",
  "profileType": "experienced",
  "wordCount": 350,
  "hasEmail": true,
  "hasPhone": true,
  "hasLinkedIn": true,
  "hasGitHub": true,
  "breakdown": {
    "keywordMatch": 30,
    "education": 10,
    "experience": 20,
    "formatting": 14,
    "actionVerbs": 9,
    "contactInfo": 5,
    "certifications": 0
  },
  "issues": [
    {
      "severity": "warning",
      "category": "Certifications",
      "title": "No Certifications Found",
      "description": "Professional certifications add credibility and can boost ATS scores.",
      "fix": "Consider adding industry certifications like AWS Solutions Architect or CKA."
    }
  ],
  "suggestedRoles": [
    {
      "role": "Java Spring Boot Specialist",
      "matchPercent": 92,
      "matchedCount": 11,
      "totalCount": 12,
      "matchedSkills": ["Java", "Spring Boot", "Spring", "Microservices", "REST API"],
      "missingSkills": ["Kubernetes"]
    }
  ]
}
```

### `GET /health`
Returns the status of the backend server.
```json
{
  "status": "UP",
  "app": "ATS Resume Scorer",
  "version": "1.0.0"
}
```
