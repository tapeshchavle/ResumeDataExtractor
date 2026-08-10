/**
 * upload.js — Handles the resume upload form:
 *   - Drag & drop
 *   - File validation
 *   - Animated loading steps
 *   - POST to /upload
 *   - Stores result in sessionStorage and navigates to result.html
 */
(function () {
    'use strict';

    // ── DOM References ─────────────────────────────────────────
    const uploadZone   = document.getElementById('uploadZone');
    const fileInput    = document.getElementById('fileInput');
    const filePreview  = document.getElementById('filePreview');
    const fileIcon     = document.getElementById('fileIcon');
    const fileName     = document.getElementById('fileName');
    const fileSize     = document.getElementById('fileSize');
    const fileRemove   = document.getElementById('fileRemove');
    const submitBtn    = document.getElementById('submitBtn');
    const uploadForm   = document.getElementById('uploadForm');
    const errorMsg     = document.getElementById('errorMessage');
    const errorText    = document.getElementById('errorText');
    const loadingOvl   = document.getElementById('loadingOverlay');

    const MAX_SIZE = 10 * 1024 * 1024; // 10 MB
    const ALLOWED  = ['application/pdf',
                      'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];

    let selectedFile = null;

    // ── Upload Zone Click → open file picker ───────────────────
    uploadZone.addEventListener('click', (e) => {
        if (e.target.closest('.file-remove')) return;
        fileInput.click();
    });

    // ── Drag & Drop ────────────────────────────────────────────
    uploadZone.addEventListener('dragover', (e) => {
        e.preventDefault();
        uploadZone.classList.add('dragover');
    });

    uploadZone.addEventListener('dragleave', () => {
        uploadZone.classList.remove('dragover');
    });

    uploadZone.addEventListener('drop', (e) => {
        e.preventDefault();
        uploadZone.classList.remove('dragover');
        const files = e.dataTransfer.files;
        if (files.length > 0) {
            handleFile(files[0]);
        }
    });

    // ── File Input Change ──────────────────────────────────────
    fileInput.addEventListener('change', () => {
        if (fileInput.files.length > 0) {
            handleFile(fileInput.files[0]);
        }
    });

    // ── Remove File ────────────────────────────────────────────
    fileRemove.addEventListener('click', (e) => {
        e.stopPropagation();
        clearFile();
    });

    // ── Handle Selected File ───────────────────────────────────
    function handleFile(file) {
        hideError();

        // Validate type
        const ext = file.name.split('.').pop().toLowerCase();
        if (!ALLOWED.includes(file.type) && ext !== 'pdf' && ext !== 'docx') {
            showError('Please upload a PDF or DOCX file.');
            return;
        }

        // Validate size
        if (file.size > MAX_SIZE) {
            showError('File is too large. Maximum size is 10 MB.');
            return;
        }

        selectedFile = file;
        fileIcon.textContent = ext === 'pdf' ? '📕' : '📘';
        fileName.textContent = file.name;
        fileSize.textContent = formatSize(file.size);
        filePreview.classList.add('visible');
        uploadZone.classList.add('has-file');
        submitBtn.disabled = false;
    }

    function clearFile() {
        selectedFile = null;
        fileInput.value = '';
        filePreview.classList.remove('visible');
        uploadZone.classList.remove('has-file');
        submitBtn.disabled = true;
    }

    // ── Form Submit ────────────────────────────────────────────
    uploadForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        hideError();

        if (!selectedFile) {
            showError('Please select a resume file first.');
            return;
        }

        // Show loading overlay
        showLoading();

        // Build FormData
        const formData = new FormData();
        formData.append('resume', selectedFile);
        const profileType = document.querySelector('input[name="profileType"]:checked').value;
        formData.append('profileType', profileType);

        try {
            const response = await fetch('upload', {
                method: 'POST',
                body: formData
            });

            const data = await response.json();

            if (!response.ok || data.error) {
                hideLoading();
                showError(data.message || 'Something went wrong. Please try again.');
                return;
            }

            // Simulate final loading steps
            await animateLoadingSteps();

            // Store result & navigate
            sessionStorage.setItem('atsResult', JSON.stringify(data));
            window.location.href = 'result.html';

        } catch (err) {
            hideLoading();
            showError('Network error: ' + err.message);
        }
    });

    // ── Loading Animation ──────────────────────────────────────
    function showLoading() {
        loadingOvl.classList.add('visible');
    }

    function hideLoading() {
        loadingOvl.classList.remove('visible');
        // Reset steps
        for (let i = 1; i <= 4; i++) {
            const step = document.getElementById('step' + i);
            step.classList.remove('active', 'done');
        }
    }

    async function animateLoadingSteps() {
        const delays = [400, 600, 500, 400];
        for (let i = 1; i <= 4; i++) {
            const step = document.getElementById('step' + i);
            step.classList.add('active');
            await sleep(delays[i - 1]);
            step.classList.remove('active');
            step.classList.add('done');
        }
        await sleep(300);
    }

    // ── Error Helpers ──────────────────────────────────────────
    function showError(msg) {
        errorText.textContent = msg;
        errorMsg.classList.add('visible');
    }

    function hideError() {
        errorMsg.classList.remove('visible');
    }

    // ── Utilities ──────────────────────────────────────────────
    function formatSize(bytes) {
        if (bytes < 1024) return bytes + ' B';
        if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
        return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
    }

    function sleep(ms) {
        return new Promise(r => setTimeout(r, ms));
    }
})();
