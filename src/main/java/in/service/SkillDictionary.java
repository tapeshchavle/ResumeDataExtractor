package in.service;

import java.util.*;

/**
 * Comprehensive skill/keyword dictionary used for ATS scoring.
 * Contains 400+ keywords: programming languages, frameworks, tools,
 * soft skills, action verbs, section headers, education keywords,
 * and 18 role profiles for job-fit analysis.
 */
public final class SkillDictionary {

    private SkillDictionary() {}

    private static Set<String> setOf(String... items) {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(items)));
    }

    // ══════════════════════════════════════════════════════════════
    //  TECHNICAL SKILLS
    // ══════════════════════════════════════════════════════════════

    public static final Set<String> PROGRAMMING_LANGUAGES = setOf(
        "java", "python", "javascript", "typescript", "c", "c++", "c#",
        "go", "golang", "rust", "ruby", "php", "swift", "kotlin", "scala",
        "r", "matlab", "perl", "dart", "lua", "haskell", "elixir",
        "objective-c", "shell", "bash", "powershell", "sql", "html", "css",
        "groovy", "clojure", "erlang", "fortran", "cobol", "assembly",
        "vhdl", "verilog", "solidity", "zig", "nim", "julia", "ocaml",
        "f#", "visual basic", "apex", "plsql", "pl/sql", "t-sql",
        "sass", "scss", "less", "graphql"
    );

    public static final Set<String> FRAMEWORKS = setOf(
        // Java ecosystem
        "spring", "spring boot", "springboot", "spring mvc", "spring security",
        "spring data", "spring cloud", "spring ai", "spring batch",
        "spring webflux", "hibernate", "jpa", "jakarta ee", "java ee",
        "jsf", "struts", "vaadin", "micronaut", "quarkus", "vert.x",
        "javafx", "apache camel", "dropwizard", "grails",
        // JavaScript/TypeScript ecosystem
        "react", "reactjs", "react.js", "angular", "angularjs", "vue",
        "vuejs", "vue.js", "next.js", "nextjs", "nuxt", "nuxt.js", "svelte",
        "sveltekit", "gatsby", "remix", "astro", "solid.js", "preact",
        "node.js", "nodejs", "express", "express.js", "fastify", "nestjs",
        "nest.js", "hono", "bun", "deno",
        // Python ecosystem
        "django", "flask", "fastapi", "tornado", "pyramid", "bottle",
        "starlette", "celery", "scrapy", "beautifulsoup",
        // Ruby
        "rails", "ruby on rails", "sinatra",
        // PHP
        "laravel", "symfony", "codeigniter", "yii", "wordpress",
        // .NET
        "asp.net", ".net", "dotnet", "blazor", "entity framework",
        "maui", "xamarin", "wpf", "winforms",
        // Mobile
        "flutter", "react native", "ionic", "swiftui", "jetpack compose",
        "android sdk", "ios sdk", "cordova", "capacitor",
        // Frontend
        "tailwindcss", "tailwind", "bootstrap", "material ui", "mui",
        "chakra ui", "ant design", "styled-components", "emotion",
        "jquery", "backbone", "ember", "htmx", "alpine.js",
        "shadcn", "radix", "headless ui",
        // Build tools
        "webpack", "vite", "rollup", "parcel", "esbuild", "turbopack",
        "babel", "swc",
        // Testing
        "junit", "testng", "mockito", "jest", "mocha", "cypress",
        "selenium", "playwright", "puppeteer", "rspec", "pytest",
        "vitest", "testing library", "enzyme", "postman", "karate",
        "gatling", "jmeter", "k6", "locust",
        // API
        "rest", "rest api", "restful", "grpc", "soap", "websocket",
        "graphql", "openapi", "swagger"
    );

    public static final Set<String> DATABASES = setOf(
        "mysql", "postgresql", "postgres", "mongodb", "redis", "sqlite",
        "oracle", "oracle db", "sql server", "mssql", "cassandra", "dynamodb",
        "couchdb", "couchbase", "neo4j", "elasticsearch", "mariadb",
        "firebase", "firestore", "supabase", "cockroachdb", "influxdb",
        "memcached", "h2", "hsqldb", "aurora", "rds", "cosmosdb",
        "timescaledb", "clickhouse", "snowflake", "bigquery", "redshift",
        "presto", "trino", "duckdb", "pinecone", "weaviate", "milvus",
        "qdrant", "chroma", "faiss", "pgvector"
    );

    public static final Set<String> CLOUD_DEVOPS = setOf(
        // AWS
        "aws", "amazon web services", "ec2", "s3", "lambda", "ecs", "eks",
        "fargate", "sqs", "sns", "cloudformation", "cloudwatch", "iam",
        "api gateway", "route 53", "cloudfront", "kinesis", "step functions",
        "sagemaker", "bedrock", "comprehend", "rekognition", "textract",
        // Azure
        "azure", "azure devops", "azure functions", "azure pipelines",
        "azure kubernetes", "aks", "azure cognitive services",
        // GCP
        "gcp", "google cloud", "google cloud platform", "cloud run",
        "cloud functions", "gke", "bigquery", "pub/sub", "vertex ai",
        "app engine", "cloud storage",
        // Containers & Orchestration
        "docker", "kubernetes", "k8s", "helm", "istio", "docker compose",
        "podman", "openshift", "rancher", "nomad", "containerd",
        // IaC & Config
        "terraform", "ansible", "puppet", "chef", "salt", "pulumi",
        "vagrant", "packer",
        // CI/CD
        "jenkins", "ci/cd", "cicd", "github actions", "gitlab ci",
        "circleci", "travis ci", "argo cd", "argocd", "flux",
        "tekton", "concourse", "bamboo", "teamcity", "harness",
        "spinnaker", "drone",
        // Servers & Networking
        "nginx", "apache", "tomcat", "caddy", "envoy", "traefik",
        "haproxy", "kong",
        // OS
        "linux", "unix", "ubuntu", "centos", "rhel", "debian",
        "alpine", "windows server", "macos",
        // Monitoring & Observability
        "grafana", "prometheus", "datadog", "new relic", "splunk",
        "kibana", "logstash", "elk stack", "jaeger", "zipkin",
        "opentelemetry", "dynatrace", "sentry", "pagerduty",
        "cloudflare", "akamai",
        // Others
        "heroku", "vercel", "netlify", "railway", "fly.io",
        "digitalocean", "linode"
    );

    public static final Set<String> DATA_ML = setOf(
        // ML/DL Frameworks
        "machine learning", "deep learning", "tensorflow", "pytorch",
        "keras", "scikit-learn", "sklearn", "xgboost", "lightgbm",
        "catboost", "caffe", "mxnet", "onnx", "jax", "flax",
        // Data Science
        "pandas", "numpy", "scipy", "matplotlib", "seaborn", "plotly",
        "bokeh", "altair", "streamlit", "gradio", "dash",
        // NLP
        "nlp", "natural language processing", "spacy", "nltk",
        "transformers", "bert", "gpt", "t5", "word2vec", "glove",
        "sentiment analysis", "text classification", "named entity recognition",
        "ner", "tokenization",
        // Computer Vision
        "computer vision", "opencv", "yolo", "object detection",
        "image segmentation", "image classification", "gan",
        "stable diffusion", "dall-e", "midjourney",
        // AI/LLM
        "generative ai", "gen ai", "llm", "large language model",
        "rag", "retrieval augmented generation",
        "langchain", "llamaindex", "llama index",
        "openai", "chatgpt", "claude", "gemini", "mistral", "llama",
        "huggingface", "hugging face", "fine-tuning", "fine tuning",
        "prompt engineering", "vector database", "embeddings",
        "agentic ai", "ai agents", "multi-agent", "autogen",
        "crewai", "crew ai", "semantic kernel",
        "spring ai", "ai orchestration",
        // Data Engineering
        "data science", "data analysis", "data analytics",
        "big data", "hadoop", "spark", "apache spark", "pyspark",
        "kafka", "apache kafka", "flink", "apache flink",
        "airflow", "apache airflow", "prefect", "dagster", "luigi",
        "etl", "data pipeline", "data warehouse", "data lake",
        "data engineering", "data modeling", "dbt",
        // BI
        "tableau", "power bi", "looker", "metabase", "superset",
        "qlik", "sisense",
        // Notebooks
        "jupyter", "jupyter notebook", "colab", "google colab",
        "databricks", "zeppelin",
        // MLOps
        "mlops", "mlflow", "kubeflow", "weights and biases", "wandb",
        "bentoml", "seldon", "triton", "model serving"
    );

    public static final Set<String> TOOLS = setOf(
        // Version Control
        "git", "github", "gitlab", "bitbucket", "svn", "mercurial",
        // Project Management
        "jira", "confluence", "slack", "trello", "asana", "notion",
        "linear", "clickup", "monday.com", "basecamp", "shortcut",
        // Design
        "figma", "sketch", "adobe xd", "invision", "zeplin",
        // API Development
        "postman", "swagger", "insomnia", "hoppscotch", "thunder client",
        // Build Tools
        "maven", "gradle", "ant", "make", "cmake", "bazel",
        "npm", "yarn", "pnpm", "pip", "poetry", "conda",
        "cargo", "composer", "bundler", "cocoapods",
        // IDEs
        "intellij", "intellij idea", "vscode", "visual studio code",
        "eclipse", "android studio", "xcode", "pycharm", "webstorm",
        "rider", "neovim", "vim",
        // Code Quality
        "sonarqube", "sonar", "eslint", "prettier", "checkstyle",
        "spotbugs", "pmd", "findbugs", "codeclimate", "snyk",
        // Messaging
        "rabbitmq", "activemq", "zeromq", "nats", "pulsar",
        // Search
        "elasticsearch", "solr", "algolia", "meilisearch", "typesense",
        // Caching
        "redis", "memcached", "ehcache", "hazelcast", "caffeine",
        // Miscellaneous
        "regex", "json", "xml", "yaml", "toml", "protobuf",
        "avro", "thrift", "messagepack"
    );

    // ══════════════════════════════════════════════════════════════
    //  ARCHITECTURE & CONCEPTS
    // ══════════════════════════════════════════════════════════════

    public static final Set<String> ARCHITECTURE_CONCEPTS = setOf(
        "microservices", "monolith", "serverless", "event-driven",
        "event driven architecture", "domain-driven design", "ddd",
        "cqrs", "event sourcing", "saga pattern", "api gateway",
        "service mesh", "soa", "hexagonal architecture", "clean architecture",
        "mvc", "mvvm", "mvp", "design patterns", "solid principles",
        "solid", "dry", "kiss", "yagni",
        "tdd", "test driven development", "bdd", "behavior driven development",
        "pair programming", "code review", "continuous integration",
        "continuous delivery", "continuous deployment",
        "load balancing", "caching", "rate limiting", "circuit breaker",
        "distributed systems", "high availability", "scalability",
        "fault tolerance", "cap theorem", "horizontal scaling",
        "vertical scaling", "sharding", "replication",
        "system design", "low level design", "high level design",
        "api design", "database design", "schema design",
        "oauth", "oauth2", "jwt", "saml", "sso", "rbac",
        "authentication", "authorization", "encryption", "hashing",
        "ssl", "tls", "https", "cors", "csrf", "xss",
        "owasp", "penetration testing", "security audit",
        "performance optimization", "profiling", "benchmarking",
        "concurrency", "multithreading", "async", "reactive programming",
        "functional programming", "object oriented programming", "oop"
    );

    // ══════════════════════════════════════════════════════════════
    //  SOFT SKILLS
    // ══════════════════════════════════════════════════════════════

    public static final Set<String> SOFT_SKILLS = setOf(
        "leadership", "communication", "teamwork", "team player",
        "problem solving", "problem-solving", "critical thinking",
        "time management", "adaptability", "creativity", "innovation",
        "collaboration", "mentoring", "mentorship", "presentation",
        "negotiation", "conflict resolution", "decision making",
        "analytical", "detail oriented", "detail-oriented",
        "self motivated", "self-motivated", "proactive", "initiative",
        "project management", "stakeholder management",
        "agile", "scrum", "kanban", "waterfall", "safe",
        "sprint planning", "retrospective", "daily standup",
        "cross-functional", "remote work", "distributed team",
        "client facing", "customer oriented", "empathy",
        "strategic thinking", "business acumen", "risk management",
        "technical writing", "documentation", "knowledge sharing",
        "coaching", "training", "onboarding"
    );

    // ══════════════════════════════════════════════════════════════
    //  ACTION VERBS
    // ══════════════════════════════════════════════════════════════

    public static final Set<String> ACTION_VERBS = setOf(
        "achieved", "architected", "automated", "built", "created",
        "delivered", "deployed", "designed", "developed", "drove",
        "engineered", "enhanced", "established", "executed",
        "implemented", "improved", "increased", "integrated",
        "launched", "led", "managed", "mentored", "migrated",
        "optimized", "orchestrated", "pioneered", "reduced",
        "refactored", "resolved", "revamped", "scaled",
        "spearheaded", "streamlined", "supervised", "transformed",
        "collaborated", "contributed", "coordinated", "analyzed",
        "configured", "debugged", "documented", "maintained",
        "monitored", "tested", "trained", "upgraded",
        "accelerated", "advocated", "bootstrapped", "championed",
        "consolidated", "customized", "decoupled", "eliminated",
        "facilitated", "formulated", "generated", "identified",
        "initiated", "leveraged", "modularized", "negotiated",
        "overhauled", "presented", "prioritized", "proposed",
        "prototyped", "published", "rationalized", "re-engineered",
        "researched", "restructured", "simplified", "standardized",
        "strengthened", "triaged", "unified", "validated",
        "visualized"
    );

    // ══════════════════════════════════════════════════════════════
    //  SECTION HEADERS
    // ══════════════════════════════════════════════════════════════

    public static final Set<String> SECTION_HEADERS = setOf(
        "education", "experience", "work experience",
        "professional experience", "employment", "employment history",
        "skills", "technical skills", "core competencies",
        "projects", "personal projects", "academic projects",
        "certifications", "certificates", "licenses",
        "achievements", "awards", "honors",
        "summary", "professional summary", "career summary",
        "objective", "career objective",
        "profile", "about me", "about",
        "publications", "research", "papers",
        "volunteer", "volunteering", "community service",
        "interests", "hobbies", "extracurricular",
        "references", "languages", "courses", "training",
        "internship", "internships", "co-op",
        "leadership", "activities", "organizations",
        "tools", "technologies", "tech stack",
        "open source", "contributions"
    );

    // ══════════════════════════════════════════════════════════════
    //  EDUCATION KEYWORDS
    // ══════════════════════════════════════════════════════════════

    public static final Set<String> EDUCATION_KEYWORDS = setOf(
        "bachelor", "bachelors", "b.tech", "btech", "b.e", "b.sc",
        "bsc", "bca", "b.com", "master", "masters", "m.tech", "mtech",
        "m.sc", "msc", "mca", "m.com", "mba", "ph.d", "phd",
        "doctorate", "diploma", "associate", "degree", "university",
        "college", "institute", "school", "gpa", "cgpa", "percentage",
        "first class", "distinction", "honours", "honors",
        "computer science", "information technology", "engineering",
        "electronics", "mechanical", "electrical", "data science",
        "artificial intelligence", "software engineering",
        "mathematics", "statistics", "physics", "chemistry",
        "cum laude", "magna cum laude", "summa cum laude",
        "dean's list", "valedictorian", "salutatorian",
        "coursework", "thesis", "dissertation", "capstone",
        "graduated", "graduating"
    );

    // ══════════════════════════════════════════════════════════════
    //  CERTIFICATION KEYWORDS
    // ══════════════════════════════════════════════════════════════

    public static final Set<String> CERTIFICATION_KEYWORDS = setOf(
        "aws certified", "aws solutions architect", "aws developer",
        "aws sysops", "aws devops", "aws machine learning",
        "azure certified", "azure fundamentals", "azure administrator",
        "azure developer", "azure solutions architect",
        "google certified", "google cloud certified",
        "google professional data engineer", "google professional cloud architect",
        "certified", "certification", "certificate",
        "pmp", "prince2", "scrum master", "csm", "psm",
        "product owner", "pspo", "cspo",
        "cka", "ckad", "cks",
        "comptia", "comptia a+", "comptia security+", "comptia network+",
        "cissp", "cism", "cisa", "ceh",
        "ccna", "ccnp", "ccie",
        "oracle certified", "ocp", "oca",
        "istqb", "itil", "togaf",
        "six sigma", "lean", "green belt", "black belt",
        "coursera", "udemy", "edx", "udacity", "pluralsight",
        "linkedin learning", "codecademy",
        "licensed", "accredited", "professional engineer",
        "tensorflow developer", "databricks certified",
        "hashicorp certified", "terraform associate",
        "kubernetes administrator", "docker certified"
    );

    // ══════════════════════════════════════════════════════════════
    //  ROLE PROFILES
    // ══════════════════════════════════════════════════════════════

    public static final Map<String, List<String>> ROLE_PROFILES = new LinkedHashMap<>();
    static {
        ROLE_PROFILES.put("Java Backend Developer", List.of(
            "java", "spring", "spring boot", "hibernate", "jpa", "maven", "gradle",
            "microservices", "rest api", "sql", "postgresql", "mysql", "docker",
            "junit", "mockito", "kafka", "redis", "spring security",
            "spring data", "spring cloud", "tomcat", "jenkins", "git"
        ));

        ROLE_PROFILES.put("Java Spring Boot Specialist", List.of(
            "java", "spring boot", "spring", "spring mvc", "spring security",
            "spring data", "spring cloud", "spring batch", "spring webflux",
            "hibernate", "jpa", "microservices", "rest api", "maven", "gradle",
            "docker", "kubernetes", "junit", "mockito", "ci/cd", "kafka"
        ));

        ROLE_PROFILES.put("Spring AI / AI Engineer", List.of(
            "java", "spring", "spring boot", "spring ai",
            "machine learning", "deep learning", "llm", "generative ai",
            "langchain", "openai", "rag", "vector database", "embeddings",
            "prompt engineering", "python", "tensorflow", "pytorch",
            "huggingface", "fine-tuning", "nlp", "docker"
        ));

        ROLE_PROFILES.put("Agentic AI Developer", List.of(
            "python", "langchain", "llamaindex", "autogen", "crewai",
            "llm", "generative ai", "agentic ai", "ai agents", "multi-agent",
            "rag", "openai", "vector database", "embeddings",
            "prompt engineering", "semantic kernel", "spring ai",
            "docker", "kubernetes", "api design", "microservices"
        ));

        ROLE_PROFILES.put("Backend Developer (Python)", List.of(
            "python", "django", "flask", "fastapi", "sql", "postgresql",
            "mongodb", "redis", "docker", "rest api", "celery",
            "microservices", "git", "linux", "aws", "ci/cd",
            "pytest", "kubernetes", "kafka", "rabbitmq"
        ));

        ROLE_PROFILES.put("Backend Developer (Node.js)", List.of(
            "javascript", "typescript", "node.js", "express", "nestjs",
            "mongodb", "postgresql", "redis", "docker", "rest api",
            "graphql", "microservices", "git", "aws", "ci/cd",
            "jest", "kubernetes", "kafka", "websocket"
        ));

        ROLE_PROFILES.put("Frontend Developer", List.of(
            "javascript", "typescript", "react", "angular", "vue",
            "html", "css", "tailwind", "next.js", "redux",
            "webpack", "vite", "figma", "jest", "cypress",
            "responsive design", "accessibility", "git",
            "sass", "graphql", "rest api"
        ));

        ROLE_PROFILES.put("Full Stack Developer", List.of(
            "javascript", "typescript", "react", "node.js", "python",
            "java", "sql", "mongodb", "postgresql", "docker",
            "git", "html", "css", "rest api", "aws",
            "ci/cd", "microservices", "redis", "graphql", "agile"
        ));

        ROLE_PROFILES.put("DevOps Engineer", List.of(
            "docker", "kubernetes", "aws", "terraform", "jenkins",
            "ci/cd", "linux", "ansible", "helm", "prometheus",
            "grafana", "git", "python", "bash", "azure",
            "gcp", "argocd", "gitlab ci", "github actions",
            "nginx", "monitoring", "infrastructure"
        ));

        ROLE_PROFILES.put("Cloud Engineer (AWS)", List.of(
            "aws", "ec2", "s3", "lambda", "ecs", "eks",
            "cloudformation", "terraform", "docker", "kubernetes",
            "iam", "vpc", "rds", "dynamodb", "cloudwatch",
            "api gateway", "sns", "sqs", "linux", "python", "ci/cd"
        ));

        ROLE_PROFILES.put("Cloud Engineer (Azure)", List.of(
            "azure", "azure devops", "azure functions", "aks",
            "azure pipelines", "docker", "kubernetes", "terraform",
            "powershell", "linux", "ci/cd", "git", "python",
            ".net", "cosmosdb", "azure cognitive services"
        ));

        ROLE_PROFILES.put("Data Scientist", List.of(
            "python", "r", "machine learning", "deep learning",
            "tensorflow", "pytorch", "pandas", "numpy", "scikit-learn",
            "sql", "tableau", "power bi", "statistics",
            "data analysis", "jupyter", "matplotlib", "seaborn",
            "nlp", "computer vision", "a/b testing"
        ));

        ROLE_PROFILES.put("ML Engineer", List.of(
            "python", "tensorflow", "pytorch", "machine learning",
            "deep learning", "docker", "kubernetes", "mlops",
            "scikit-learn", "mlflow", "aws", "sagemaker",
            "data pipeline", "sql", "spark", "git",
            "ci/cd", "model serving", "feature engineering"
        ));

        ROLE_PROFILES.put("Data Engineer", List.of(
            "python", "sql", "spark", "kafka", "airflow",
            "hadoop", "etl", "data pipeline", "aws", "s3",
            "redshift", "bigquery", "snowflake", "dbt",
            "docker", "linux", "postgresql", "mongodb",
            "data warehouse", "data lake", "scala"
        ));

        ROLE_PROFILES.put("Mobile Developer (Android)", List.of(
            "kotlin", "java", "android", "android sdk", "jetpack compose",
            "firebase", "retrofit", "room", "mvvm", "git",
            "ci/cd", "rest api", "gradle", "coroutines",
            "material design", "testing", "google play"
        ));

        ROLE_PROFILES.put("Mobile Developer (iOS)", List.of(
            "swift", "objective-c", "ios", "ios sdk", "swiftui",
            "xcode", "cocoapods", "core data", "mvvm", "git",
            "ci/cd", "rest api", "firebase", "app store",
            "uikit", "combine", "testing"
        ));

        ROLE_PROFILES.put("QA / Test Automation Engineer", List.of(
            "selenium", "cypress", "playwright", "jest", "junit",
            "testng", "cucumber", "jmeter", "postman", "git",
            "ci/cd", "python", "java", "javascript", "sql",
            "api testing", "performance testing", "agile",
            "istqb", "test planning", "bug tracking"
        ));

        ROLE_PROFILES.put("Site Reliability Engineer (SRE)", List.of(
            "linux", "docker", "kubernetes", "prometheus", "grafana",
            "terraform", "python", "go", "bash", "aws",
            "ci/cd", "monitoring", "incident management",
            "slo", "sli", "sla", "chaos engineering",
            "distributed systems", "networking", "automation"
        ));
    }

    // ══════════════════════════════════════════════════════════════
    //  AGGREGATE METHODS
    // ══════════════════════════════════════════════════════════════

    public static Set<String> allTechnicalSkills() {
        Set<String> all = new HashSet<>();
        all.addAll(PROGRAMMING_LANGUAGES);
        all.addAll(FRAMEWORKS);
        all.addAll(DATABASES);
        all.addAll(CLOUD_DEVOPS);
        all.addAll(DATA_ML);
        all.addAll(TOOLS);
        all.addAll(ARCHITECTURE_CONCEPTS);
        return Collections.unmodifiableSet(all);
    }

    public static Set<String> allKeywords() {
        Set<String> all = new HashSet<>();
        all.addAll(allTechnicalSkills());
        all.addAll(SOFT_SKILLS);
        return Collections.unmodifiableSet(all);
    }
}
