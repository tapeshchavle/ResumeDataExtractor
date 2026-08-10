/**
 * result.js — Reads ATSResult from sessionStorage and renders:
 *   - Animated score circle
 *   - Grade badge
 *   - Issues & Fixes dropdown (NEW)
 *   - Category breakdown bars
 *   - Best Fit Roles (NEW)
 *   - Matched / Missing skill tags
 *   - Contact info badges
 *   - Recommendations list
 */
(function () {
    'use strict';

    // ── Load Data ──────────────────────────────────────────────
    const raw = sessionStorage.getItem('atsResult');
    if (!raw) {
        window.location.href = 'index.html';
        return;
    }

    const data = JSON.parse(raw);

    // ── Category Labels ────────────────────────────────────────
    const CATEGORY_LABELS = {
        keywordMatch:   'Keyword & Skill Match',
        education:      'Education Quality',
        experience:     'Experience Section',
        formatting:     'Resume Formatting',
        actionVerbs:    'Action Verbs & Impact',
        contactInfo:    'Contact Information',
        certifications: 'Certifications & Projects'
    };

    const CATEGORY_ICONS = {
        keywordMatch:   '🎯',
        education:      '🎓',
        experience:     '💼',
        formatting:     '📝',
        actionVerbs:    '⚡',
        contactInfo:    '📧',
        certifications: '🏆'
    };

    // ══════════════════════════════════════════════════════════
    //  SCORE CIRCLE
    // ══════════════════════════════════════════════════════════

    const scoreProgress = document.getElementById('scoreProgress');
    const scoreNumber   = document.getElementById('scoreNumber');
    const gradeBadge    = document.getElementById('gradeBadge');
    const profileBadge  = document.getElementById('profileBadge');

    function gradeClass(grade) {
        const map = { 'A+': 'grade-a-plus', 'A': 'grade-a', 'B+': 'grade-b-plus',
                      'B': 'grade-b', 'C': 'grade-c', 'D': 'grade-d' };
        return map[grade] || 'grade-d';
    }

    function animateCount(target) {
        let current = 0;
        const duration = 2000;
        const step = Math.ceil(target / (duration / 16));
        const timer = setInterval(() => {
            current += step;
            if (current >= target) {
                current = target;
                clearInterval(timer);
            }
            scoreNumber.textContent = current;
        }, 16);
    }

    const circumference = 2 * Math.PI * 90;
    const offset = circumference - (data.score / 100) * circumference;
    scoreProgress.classList.add(gradeClass(data.grade));

    setTimeout(() => {
        scoreProgress.style.strokeDashoffset = offset;
        animateCount(data.score);
    }, 300);

    gradeBadge.textContent = 'Grade: ' + data.grade;
    gradeBadge.classList.add(gradeClass(data.grade));

    const profileEmoji = data.profileType === 'experienced' ? '💼' : '🎓';
    const profileLabel = data.profileType === 'experienced' ? 'Experienced' : 'Fresher';
    profileBadge.innerHTML = profileEmoji + ' ' + profileLabel;

    const metaBadges = document.getElementById('metaBadges');
    metaBadges.innerHTML = `
        <span class="meta-badge">📝 ${data.wordCount} words</span>
        <span class="meta-badge">📊 Score: ${data.score}/100</span>
    `;

    // ══════════════════════════════════════════════════════════
    //  ISSUES & FIXES (NEW)
    // ══════════════════════════════════════════════════════════

    const issuesToggle = document.getElementById('issuesToggle');
    const issuesList   = document.getElementById('issuesList');
    const issueCountBadges = document.getElementById('issueCountBadges');
    const issues = data.issues || [];

    // Count by severity
    const counts = { critical: 0, warning: 0, info: 0 };
    issues.forEach(i => { counts[i.severity] = (counts[i.severity] || 0) + 1; });

    // Render count badges in toggle header
    let badgesHtml = '';
    if (counts.critical > 0) badgesHtml += `<span class="issue-count-badge critical">🔴 ${counts.critical}</span>`;
    if (counts.warning > 0)  badgesHtml += `<span class="issue-count-badge warning">🟡 ${counts.warning}</span>`;
    if (counts.info > 0)     badgesHtml += `<span class="issue-count-badge info">🔵 ${counts.info}</span>`;
    if (issues.length === 0) badgesHtml = '<span class="issue-count-badge info">✅ No issues</span>';
    issueCountBadges.innerHTML = badgesHtml;

    // Render issue items
    issues.forEach(issue => {
        const div = document.createElement('div');
        div.className = 'issue-item';
        div.innerHTML = `
            <div class="issue-severity ${issue.severity}"></div>
            <div class="issue-content">
                <div class="issue-header">
                    <span class="issue-title">${issue.title}</span>
                    <span class="issue-category-badge">${issue.category}</span>
                </div>
                <div class="issue-description">${issue.description}</div>
                <div class="issue-fix">
                    <span class="fix-icon">💡</span>
                    <span><strong>Fix:</strong> ${issue.fix}</span>
                </div>
            </div>
        `;
        issuesList.appendChild(div);
    });

    if (issues.length === 0) {
        const div = document.createElement('div');
        div.className = 'issue-item';
        div.innerHTML = `
            <div class="issue-severity info"></div>
            <div class="issue-content">
                <div class="issue-title">🎉 No major issues found!</div>
                <div class="issue-description">Your resume is well-structured and covers all essential elements.</div>
            </div>
        `;
        issuesList.appendChild(div);
    }

    // Toggle expand/collapse
    issuesToggle.addEventListener('click', () => {
        const isExpanded = issuesList.classList.contains('visible');
        if (isExpanded) {
            issuesList.classList.remove('visible');
            issuesToggle.classList.remove('expanded');
        } else {
            issuesList.classList.add('visible');
            issuesToggle.classList.add('expanded');
        }
    });

    // Auto-expand if there are critical issues
    if (counts.critical > 0) {
        issuesList.classList.add('visible');
        issuesToggle.classList.add('expanded');
    }

    // ══════════════════════════════════════════════════════════
    //  BREAKDOWN BARS
    // ══════════════════════════════════════════════════════════

    const breakdownGrid = document.getElementById('breakdownGrid');
    const breakdown     = data.breakdown || {};
    const maxBreakdown  = data.maxBreakdown || {};

    const categories = Object.keys(CATEGORY_LABELS);
    categories.forEach(cat => {
        const earned = breakdown[cat] || 0;
        const max    = maxBreakdown[cat] || 1;
        const pct    = Math.round((earned / max) * 100);

        let barClass = 'high';
        if (pct < 50) barClass = 'low';
        else if (pct < 75) barClass = 'medium';

        const item = document.createElement('div');
        item.className = 'breakdown-item glass-sm';
        item.innerHTML = `
            <div class="breakdown-header">
                <span class="breakdown-name">${CATEGORY_ICONS[cat] || ''} ${CATEGORY_LABELS[cat]}</span>
                <span class="breakdown-score">${earned}/${max}</span>
            </div>
            <div class="bar-track">
                <div class="bar-fill ${barClass}" data-width="${pct}"></div>
            </div>
        `;
        breakdownGrid.appendChild(item);
    });

    setTimeout(() => {
        document.querySelectorAll('.bar-fill').forEach(bar => {
            bar.style.width = bar.dataset.width + '%';
        });
    }, 400);

    // ══════════════════════════════════════════════════════════
    //  BEST FIT ROLES (NEW)
    // ══════════════════════════════════════════════════════════

    const rolesGrid = document.getElementById('rolesGrid');
    const roles = data.suggestedRoles || [];

    roles.forEach((role, index) => {
        const pct = role.matchPercent || 0;
        let levelClass = 'high';
        if (pct < 40) levelClass = 'low';
        else if (pct < 65) levelClass = 'medium';

        const card = document.createElement('div');
        card.className = 'role-card glass-sm' + (index === 0 ? ' top-match' : '');

        // Build skills pills (show top 6 matched + top 3 missing)
        const matchedHtml = (role.matchedSkills || []).slice(0, 6)
            .map(s => `<span class="role-skill matched">${s}</span>`).join('');
        const missingHtml = (role.missingSkills || []).slice(0, 3)
            .map(s => `<span class="role-skill missing">${s}</span>`).join('');

        card.innerHTML = `
            <div class="role-name">${role.role}</div>
            <div class="role-match-row">
                <span class="role-match-pct ${levelClass}">${pct}%</span>
                <div class="role-bar-track">
                    <div class="role-bar-fill ${levelClass}" data-width="${pct}"></div>
                </div>
                <span class="role-match-count">${role.matchedCount}/${role.totalCount}</span>
            </div>
            <div class="role-skills">
                ${matchedHtml}${missingHtml}
            </div>
        `;
        rolesGrid.appendChild(card);
    });

    // Animate role bars
    setTimeout(() => {
        document.querySelectorAll('.role-bar-fill').forEach(bar => {
            bar.style.width = bar.dataset.width + '%';
        });
    }, 600);

    // ══════════════════════════════════════════════════════════
    //  SKILLS
    // ══════════════════════════════════════════════════════════

    const matchedTags = document.getElementById('matchedTags');
    const missingTags = document.getElementById('missingTags');

    (data.matchedSkills || []).forEach(skill => {
        const tag = document.createElement('span');
        tag.className = 'skill-tag matched';
        tag.textContent = skill;
        matchedTags.appendChild(tag);
    });

    if ((data.matchedSkills || []).length === 0) {
        matchedTags.innerHTML = '<span style="color:var(--text-muted);font-size:0.85rem;">No technical skills detected</span>';
    }

    (data.missingSkills || []).forEach(skill => {
        const tag = document.createElement('span');
        tag.className = 'skill-tag missing';
        tag.textContent = skill;
        missingTags.appendChild(tag);
    });

    if ((data.missingSkills || []).length === 0) {
        missingTags.innerHTML = '<span style="color:var(--text-muted);font-size:0.85rem;">Great coverage!</span>';
    }

    // ══════════════════════════════════════════════════════════
    //  CONTACT INFO
    // ══════════════════════════════════════════════════════════

    const contactBadges = document.getElementById('contactBadges');
    const contacts = [
        { key: 'hasEmail',    label: 'Email',    icon: '📧' },
        { key: 'hasPhone',    label: 'Phone',    icon: '📱' },
        { key: 'hasLinkedIn', label: 'LinkedIn', icon: '🔗' },
        { key: 'hasGitHub',   label: 'GitHub',   icon: '💻' }
    ];

    contacts.forEach(c => {
        const found = data[c.key];
        const badge = document.createElement('div');
        badge.className = 'contact-badge ' + (found ? 'found' : 'not-found');
        badge.innerHTML = `${c.icon} ${c.label} ${found ? '✓' : '✗'}`;
        contactBadges.appendChild(badge);
    });

    // ══════════════════════════════════════════════════════════
    //  RECOMMENDATIONS
    // ══════════════════════════════════════════════════════════

    const recList = document.getElementById('recList');

    (data.recommendations || []).forEach(rec => {
        const li = document.createElement('li');
        li.className = 'rec-item';
        let icon = '💡';
        if (rec.includes('🎯')) icon = '🎯';
        else if (rec.includes('email') || rec.includes('phone') || rec.includes('LinkedIn') || rec.includes('GitHub')) icon = '📬';
        else if (rec.includes('skill') || rec.includes('keyword')) icon = '🎯';
        else if (rec.includes('action verb')) icon = '⚡';
        else if (rec.includes('quantif')) icon = '📈';
        else if (rec.includes('section') || rec.includes('structure') || rec.includes('format')) icon = '📝';
        else if (rec.includes('short') || rec.includes('long') || rec.includes('word')) icon = '📏';
        else if (rec.includes('certif')) icon = '🏆';
        else if (rec.includes('project')) icon = '🛠️';
        else if (rec.includes('Education') || rec.includes('GPA')) icon = '🎓';
        else if (rec.includes('Experience') || rec.includes('role')) icon = '💼';

        li.innerHTML = `<span class="rec-icon">${icon}</span><span>${rec}</span>`;
        recList.appendChild(li);
    });

    if ((data.recommendations || []).length === 0) {
        const li = document.createElement('li');
        li.className = 'rec-item';
        li.innerHTML = '<span class="rec-icon">🎉</span><span>Your resume looks great! No major issues found.</span>';
        recList.appendChild(li);
    }

})();
