var applicantsCurrentPage = 1;
var applicantsPageSize = 20;

var shortlistConfirmButton = null;
var allowShortlistSubmit = false;
var rejectConfirmButton = null;
var allowRejectSubmit = false;
var activeActionRow = null;
var activeActionAppId = null;
var activeScheduleRow = null;
var activeScheduleAppId = null;
var drawerResumeUrl = '';
var INTERVIEW_SLOT_MINUTES = 15;
function pad2(n) {
    return String(n).padStart(2, '0');
}

function formatTimeLabel(h, m) {
    var period = h >= 12 ? 'PM' : 'AM';
    var hour12 = h % 12;
    if (hour12 === 0)
        hour12 = 12;
    return pad2(hour12) + ':' + pad2(m) + ' ' + period;
}

function todayDateValue() {
    var now = new Date();
    return now.getFullYear() + '-' + pad2(now.getMonth() + 1) + '-' + pad2(now.getDate());
}

function populateInterviewTimeOptions() {
    var dateInput = document.getElementById('interviewDateInput');
    var timeSelect = document.getElementById('interviewTimeInput');
    if (!dateInput || !timeSelect) {
        return;
    }

    timeSelect.innerHTML = '<option value="">Select Time</option>';

    var selectedDate = dateInput.value;
    var isToday = selectedDate && selectedDate === todayDateValue();

    // If no date selected, show all 24-hour slots so dropdown isn't empty
    for (var h = 0; h < 24; h++) {
        for (var m = 0; m < 60; m += INTERVIEW_SLOT_MINUTES) {

            // Only filter past times when today is explicitly selected
            if (isToday) {
                var now = new Date();
                if (h < now.getHours()) continue;
                if (h === now.getHours() && m <= now.getMinutes()) continue;
            }

            var opt = document.createElement('option');
            opt.value = pad2(h) + ':' + pad2(m);
            opt.textContent = formatTimeLabel(h, m);
            timeSelect.appendChild(opt);
        }
    }
}
function initInterviewTimePastBlock() {
    var timeInput = document.getElementById('interviewTimeInput');
    if (!timeInput) return;
    
    timeInput.addEventListener('change', function () {
        var dateInput = document.getElementById('interviewDateInput');
        if (!dateInput || !dateInput.value) return;
        
        var isToday = dateInput.value === todayDateValue();
        if (!isToday) return;
        
        var now = new Date();
        var minTime = pad2(now.getHours()) + ':' + pad2(now.getMinutes());
        
        if (this.value && this.value <= minTime) {
            showToast('Please select a future time — that slot has already passed.', 'error');
            this.value = '';  // Clear the invalid selection
        }
    });
}
function onInterviewDateChanged() {
    populateHourOptions();
    // Clear minute and time selections
    var minuteSel = document.getElementById('interviewMinuteInput');
    if (minuteSel) minuteSel.innerHTML = '<option value="">MM</option>';
    var hidden = document.getElementById('interviewTimeInput');
    if (hidden) hidden.value = '';
}

function onInterviewTimeChanged() {
    populateMinuteOptions(); 
    syncHiddenTimeInput();
}
function populateHourOptions() {
    var hourSel = document.getElementById('interviewHourInput');
    if (!hourSel) return;

    var dateInput = document.getElementById('interviewDateInput');
    var isToday = dateInput && dateInput.value === todayDateValue();
    var now = new Date();
    var currentHour = now.getHours(); // 0-23

    hourSel.innerHTML = '<option value="">HH</option>';

    for (var h = 1; h <= 12; h++) {
        // Convert 12-hour to 24-hour for comparison
        // We check both AM and PM by seeing if ANY minute in that slot is valid
        // Just populate all hours — minute filtering handles the rest
        var opt = document.createElement('option');
        opt.value = String(h);
        opt.textContent = pad2(h);

        // For today: disable hours that are completely in the past
        if (isToday) {
            // Check if this hour in AM is past
            // We'll handle this in minute population — just show all hours
        }

        hourSel.appendChild(opt);
    }

    // Repopulate minutes for current selection
    populateMinuteOptions();
}

function populateMinuteOptions() {
    var hourSel   = document.getElementById('interviewHourInput');
    var minuteSel = document.getElementById('interviewMinuteInput');
    var ampmSel   = document.getElementById('interviewAmPmInput');
    if (!hourSel || !minuteSel || !ampmSel) return;

    var selectedHour12 = parseInt(hourSel.value, 10);
    var selectedAmPm   = ampmSel.value;

    minuteSel.innerHTML = '<option value="">MM</option>';

    if (!selectedHour12 || !selectedAmPm) return;

    var hour24 = selectedHour12;
    if (selectedAmPm === 'AM' && selectedHour12 === 12) hour24 = 0;
    if (selectedAmPm === 'PM' && selectedHour12 !== 12) hour24 = selectedHour12 + 12;

    var dateInput = document.getElementById('interviewDateInput');
    var isToday   = dateInput && dateInput.value === todayDateValue();
    var now       = new Date();

    for (var m = 0; m < 60; m++) {
        if (isToday) {
            if (hour24 < now.getHours()) continue;
            if (hour24 === now.getHours() && m <= now.getMinutes()) continue;
        }

        var opt = document.createElement('option');
        opt.value = pad2(m);
        opt.textContent = pad2(m);
        minuteSel.appendChild(opt);
    }

    if (minuteSel.options.length === 1) {
        minuteSel.innerHTML = '<option value="">Past hour</option>';
    }
    // DO NOT call syncHiddenTimeInput() here
}
function syncHiddenTimeInput() {
    var hourSel   = document.getElementById('interviewHourInput');
    var minuteSel = document.getElementById('interviewMinuteInput');
    var ampmSel   = document.getElementById('interviewAmPmInput');
    var hidden    = document.getElementById('interviewTimeInput');
    if (!hourSel || !minuteSel || !ampmSel || !hidden) return;

    var h = parseInt(hourSel.value, 10);
    var m = minuteSel.value;
    var ampm = ampmSel.value;

    if (!h || !m || m === '' || m === 'MM') {
        hidden.value = '';
        return;
    }

    // Convert to 24-hour HH:mm for the bean
    var hour24 = h;
    if (ampm === 'AM' && h === 12) hour24 = 0;
    if (ampm === 'PM' && h !== 12) hour24 = h + 12;

    hidden.value = pad2(hour24) + ':' + m;
}
function validateTimeForToday(input) {
    var dateInput = document.getElementById('interviewDateInput');
    if (!dateInput || !input) return;

    var isToday = dateInput.value === todayDateValue();
    if (!isToday) return; // future date, all times valid

    var now = new Date();
    var currentTime = pad2(now.getHours()) + ':' + pad2(now.getMinutes());

    if (input.value && input.value <= currentTime) {
        showToast('Selected time has already passed. Please choose a future time.', 'error');
        input.value = '';
    }
}
function byIdSuffix(id) {
    return document.getElementById(id) || document.querySelector('[id$="' + id + '"]');
}
function getApplicantRows() {
    return Array.prototype.slice.call(document.querySelectorAll('.applicant-row'));
}

function getFilteredRows() {
    return getApplicantRows().filter(function (row) {
        return row.dataset.filterMatch !== 'false';
    });
}

function getRowAppId(row) {
    return row && row.dataset ? row.dataset.appId : '';
}

function rowText(row, key) {
    return ((row.dataset && row.dataset[key]) || '').toLowerCase();
}

function normalizeStatus(status) {
    return (status || 'Applied').replace(/\s+/g, ' ').trim();
}

function displayStatus(status) {
    status = normalizeStatus(status);
    return status.toLowerCase() === 'rejected' ? 'Rejected Application' : status;
}

function statusClass(status) {
    status = normalizeStatus(status).toLowerCase();
    if (status === 'shortlisted') {
        return 'status-shortlisted';
    }
    if (status === 'rejected') {
        return 'status-rejected';
    }
    if (status === 'selected') {
        return 'status-selected';
    }
    if (status === 'interview scheduled') {
        return 'status-interview';
    }
    return 'status-applied';
}

function updateRowStatus(row, status) {
    if (!row) {
        return;
    }

    row.dataset.status = normalizeStatus(status);
    row.dataset.displayStatus = displayStatus(status);

    var badge = row.querySelector('.status-badge');
    if (badge) {
        badge.className = 'status-badge ' + statusClass(status);
        badge.textContent = displayStatus(status);
    }
}

function findActionCell(row) {
    return row ? row.querySelector('.action-btns') : null;
}

function keepViewAfterAction(row) {
    if (row) {
        row.dataset.filterMatch = 'true';
        row.style.display = '';
    }
    renderApplicantsPagination();
}

function replaceActionsForShortlisted(row) {
    // JSF renders the real Schedule command button after shortlist.
    // Do not create a plain HTML button here, otherwise the modal action is not wired.
}

function replaceActionsForRejected(row) {
    var cell = findActionCell(row);
    if (!cell) {
        return;
    }

    hideActionButtons(cell, ['action-shortlist', 'action-reject', 'action-schedule']);
    removeGeneratedStateButtons(cell);

    var rejected = document.createElement('button');
    rejected.type = 'button';
    rejected.className = 'action-btn action-text action-reject js-state-button';
    rejected.textContent = 'Rejected';
    rejected.disabled = true;
    rejected.title = 'Application rejected';
    cell.appendChild(rejected);
}

function replaceActionsForScheduled(row) {
    var cell = findActionCell(row);
    if (!cell) {
        return;
    }

    hideActionButtons(cell, ['action-shortlist', 'action-reject', 'action-schedule']);
    removeGeneratedStateButtons(cell);

    var scheduled = document.createElement('button');
    scheduled.type = 'button';
    scheduled.className = 'action-btn action-text action-schedule js-state-button';
    scheduled.textContent = 'Scheduled';
    scheduled.disabled = true;
    scheduled.title = 'Interview scheduled';
    cell.appendChild(scheduled);
}

function hideActionButtons(cell, classNames) {
    classNames.forEach(function (className) {
        cell.querySelectorAll('.' + className).forEach(function (btn) {
            if (!btn.classList.contains('action-view')) {
                btn.style.display = 'none';
                btn.disabled = true;
            }
        });
    });
}

function removeGeneratedStateButtons(cell) {
    cell.querySelectorAll('.js-state-button, .js-schedule-visible').forEach(function (btn) {
        btn.remove();
    });
}

function triggerPrepareSchedule(row) {
    if (!row) {
        return;
    }
    activeScheduleRow = row;
    var appId = getRowAppId(row);
    var hidden = row.querySelector('[id$="prepareScheduleHidden_' + appId + '"]');
    if (hidden) {
        hidden.click();
    } else {
        showToast('Unable to open schedule form.', 'error');
    }
}

function findRowByAppId(appId) {
    if (!appId) {
        return null;
    }
    return document.querySelector('.applicant-row[data-app-id="' + appId + '"]');
}

function changeCount(id, delta) {
    var el = document.getElementById(id);
    if (!el) {
        return;
    }
    var current = parseInt(el.textContent, 10);
    if (isNaN(current)) {
        current = 0;
    }
    el.textContent = Math.max(0, current + delta);
}

function updateCountsForStatusChange(oldStatus, newStatus) {
    oldStatus = normalizeStatus(oldStatus).toLowerCase();
    newStatus = normalizeStatus(newStatus).toLowerCase();

    if (oldStatus !== 'shortlisted' && newStatus === 'shortlisted') {
        changeCount('shortlistedCountText', 1);
        changeCount('quickShortlistedCountText', 1);
    }
    if (oldStatus === 'shortlisted' && newStatus !== 'shortlisted') {
        changeCount('shortlistedCountText', -1);
        changeCount('quickShortlistedCountText', -1);
    }
    if (oldStatus !== 'rejected' && newStatus === 'rejected') {
        changeCount('rejectedCountText', 1);
    }
    if (oldStatus === 'rejected' && newStatus !== 'rejected') {
        changeCount('rejectedCountText', -1);
    }
}

function renderApplicantsPagination() {
    var filteredRows = getFilteredRows();
    var totalPages = Math.max(1, Math.ceil(filteredRows.length / applicantsPageSize));

    applicantsCurrentPage = Math.min(Math.max(applicantsCurrentPage, 1), totalPages);

    getApplicantRows().forEach(function (row) {
        row.style.display = 'none';
    });

    filteredRows.forEach(function (row, index) {
        var start = (applicantsCurrentPage - 1) * applicantsPageSize;
        var end = start + applicantsPageSize;
        row.style.display = (index >= start && index < end) ? '' : 'none';
    });

    var pageInfo = document.getElementById('pageInfo');
    var prevBtn = document.getElementById('prevPageBtn');
    var nextBtn = document.getElementById('nextPageBtn');

    if (pageInfo) {
        pageInfo.textContent = 'Page ' + applicantsCurrentPage + ' of ' + totalPages;
    }
    if (prevBtn) {
        prevBtn.disabled = applicantsCurrentPage <= 1;
    }
    if (nextBtn) {
        nextBtn.disabled = applicantsCurrentPage >= totalPages;
    }
}

function goApplicantsPage(step) {
    applicantsCurrentPage += step;
    renderApplicantsPagination();
}

function openProfileDrawerFromRow(button) {
    var row = button ? button.closest('.applicant-row') : null;
    if (!row) {
        showToast('Could not find candidate data.', 'error');
        return;
    }

    var ds = row.dataset;

    openProfileDrawer({
        name: ds.name || 'Candidate',
        email: ds.email || 'Email not provided',
        role: ds.job || 'Job title',
        experience: ds.experience || '-',
        skills: ds.skills || 'Skills not provided',
        education: ds.education || 'Education not provided',
        notes: ds.notes || '',
        location: ds.location || 'Location not provided',
        score: ds.score && ds.score !== '--' ? ds.score + '%' : '--',
        status: ds.displayStatus || ds.status || 'Applied',
        resumeUrl: ds.resumeUrl || ''
    });
}

function openProfileDrawer(profile) {
    var overlay = document.getElementById('profileDrawer');
    if (!overlay) {
        return;
    }

    setText('drawerName', profile.name || 'Candidate');
    setText('drawerRole', profile.role || 'Job title');
    setText('drawerEmail', profile.email || 'Email not provided');
    setText('drawerLocation', profile.location || 'Location not provided');
    setText('drawerExperience', profile.experience || '-');
    setText('drawerSkills', cleanProfileText(profile.skills, 'Skills not provided'));
    setText('drawerEducation', cleanProfileText(profile.education, 'Education not provided'));
    setText('drawerNotes', cleanProfileText(profile.notes, 'Candidate profile details not available.'));
    setText('drawerScore', profile.score || '--');
    setText('drawerStatus', displayStatus(profile.status || 'Applied'));
    setText('drawerAvatar', profile.name ? profile.name.charAt(0).toUpperCase() : 'N');

    drawerResumeUrl = profile.resumeUrl || '';
    var resumeBtn = document.getElementById('drawerResumeBtn');
    if (resumeBtn) {
        resumeBtn.disabled = !drawerResumeUrl;
        resumeBtn.classList.toggle('disabled', !drawerResumeUrl);
        resumeBtn.title = drawerResumeUrl ? 'View Resume' : 'Resume not uploaded';
    }

    overlay.classList.add('open');
}

function cleanProfileText(value, fallback) {
    value = (value || '').replace(/\s+/g, ' ').trim();
    return value || fallback;
}

function setText(id, value) {
    var el = document.getElementById(id);
    if (el) {
        el.textContent = value;
    }
}

function openDrawerResume() {
    if (!drawerResumeUrl) {
        showToast('Resume not uploaded.', 'info');
        return;
    }
    window.open(drawerResumeUrl, '_blank', 'noopener');
}

function closeProfileDrawer() {
    var overlay = document.getElementById('profileDrawer');
    if (overlay) {
        overlay.classList.remove('open');
    }
}

function populateJobFilter() {
    // Job options now come from the CDI bean, so untouched database records are available.
}

function applyStaticFilters() {
    var searchEl = byIdSuffix('searchInput');
    var jobEl = byIdSuffix('jobFilter');
    var statusEl = byIdSuffix('statusFilter');
    var sortEl = byIdSuffix('sortFilter');

    var search = searchEl ? searchEl.value.trim().toLowerCase() : '';
    var job = jobEl ? jobEl.value.trim() : 'All Jobs';
    var status = statusEl ? statusEl.value.trim() : 'All Status';
    var sort = sortEl ? sortEl.value.trim() : 'Newest First';

    getApplicantRows().forEach(function (row) {
        var rowName = rowText(row, 'name');
        var rowEmail = rowText(row, 'email');
        var rowJob = ((row.dataset.job || '').trim());
        var rowJobLower = rowJob.toLowerCase();
        var rowStatus = normalizeStatus(row.dataset.status).toLowerCase();

        var searchMatch = !search
                || rowName.indexOf(search) >= 0
                || rowEmail.indexOf(search) >= 0
                || rowJobLower.indexOf(search) >= 0;

        var jobMatch = job === 'All Jobs'
                || rowJob.toLowerCase() === job.toLowerCase();

        var statusMatch = status === 'All Status'
                || rowStatus === status.toLowerCase();

        row.dataset.filterMatch = (searchMatch && jobMatch && statusMatch) ? 'true' : 'false';
    });

    sortApplicants(sort);
    applicantsCurrentPage = 1;
    renderApplicantsPagination();
}
function sortApplicants(sort) {
    var tbody = document.querySelector('.applicants-table tbody');
    if (!tbody) {
        return;
    }

    var rows = getApplicantRows();
    rows.sort(function (a, b) {
        if (sort === 'Top Score First' || sort === 'Lowest Score First') {
            var scoreA = parseInt(a.dataset.score, 10);
            var scoreB = parseInt(b.dataset.score, 10);
            scoreA = isNaN(scoreA) ? -1 : scoreA;
            scoreB = isNaN(scoreB) ? -1 : scoreB;
            return sort === 'Top Score First' ? scoreB - scoreA : scoreA - scoreB;
        }

        var dateA = Date.parse(a.dataset.applied || '') || 0;
        var dateB = Date.parse(b.dataset.applied || '') || 0;
        if (dateA !== dateB) {
            return sort === 'Oldest First' ? dateA - dateB : dateB - dateA;
        }

        var idA = parseInt(a.dataset.appId, 10) || 0;
        var idB = parseInt(b.dataset.appId, 10) || 0;
        return sort === 'Oldest First' ? idA - idB : idB - idA;
    });

    rows.forEach(function (row) {
        tbody.appendChild(row);
    });
}

function resetStaticFilters() {
    var search = byIdSuffix('searchInput');
    var job = byIdSuffix('jobFilter');
    var status = byIdSuffix('statusFilter');
    var sort = byIdSuffix('sortFilter');

    if (search) {
        search.value = '';
    }
    if (job) {
        job.value = 'All Jobs';
    }
    if (status) {
        status.value = 'All Status';
    }
    if (sort) {
        sort.value = 'Newest First';
    }

    getApplicantRows().forEach(function (row) {
        row.dataset.filterMatch = 'true';
    });

    applicantsCurrentPage = 1;
    renderApplicantsPagination();
}

function showToast(message, type) {
    var existing = document.getElementById('qhToast');
    if (existing) {
        existing.remove();
    }

    var toast = document.createElement('div');
    toast.id = 'qhToast';
    toast.className = 'qh-toast qh-toast-' + (type || 'info');
    toast.textContent = message;
    document.body.appendChild(toast);

    requestAnimationFrame(function () {
        toast.style.opacity = '1';
        toast.style.transform = 'translateY(0)';
    });

    setTimeout(function () {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(20px)';
        setTimeout(function () {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 400);
    }, 2200);
}

function animateScoreBars() {
    document.querySelectorAll('.score-bar-fill').forEach(function (bar) {
        var targetWidth = bar.style.width || '0%';
        bar.style.width = '0%';
        setTimeout(function () {
            bar.style.width = targetWidth;
        }, 120);
    });
}

function handleShortlistAjaxEvent(data) {
    if (!data) {
        return;
    }

    if (data.status === 'begin' && data.source) {
        activeActionRow = data.source.closest('.applicant-row');
        activeActionAppId = getRowAppId(activeActionRow);
    }

    if (data.status === 'success') {
        var row = findRowByAppId(activeActionAppId) || activeActionRow || (shortlistConfirmButton ? shortlistConfirmButton.closest('.applicant-row') : null);
        var oldStatus = row ? row.dataset.status : '';
        updateRowStatus(row, 'Shortlisted');
        updateCountsForStatusChange(oldStatus, 'Shortlisted');
        keepViewAfterAction(row);
        shortlistConfirmButton = null;
        showToast('Shortlisted', 'success');
    }
}

function handleRejectAjaxEvent(data) {
    if (!data) {
        return;
    }

    if (data.status === 'begin' && data.source) {
        activeActionRow = data.source.closest('.applicant-row');
        activeActionAppId = getRowAppId(activeActionRow);
    }

    if (data.status === 'success') {
        var row = findRowByAppId(activeActionAppId) || activeActionRow || (rejectConfirmButton ? rejectConfirmButton.closest('.applicant-row') : null);
        var oldStatus = row ? row.dataset.status : '';
        updateRowStatus(row, 'Rejected');
        updateCountsForStatusChange(oldStatus, 'Rejected');
        keepViewAfterAction(row);
        rejectConfirmButton = null;
        showToast('Rejected', 'error');
    }
}

function confirmShortlistAction(button) {
    if (allowShortlistSubmit) {
        allowShortlistSubmit = false;
        return true;
    }
    shortlistConfirmButton = button;
    openShortlistConfirmModal();
    return false;
}

function openShortlistConfirmModal() {
    var modal = document.getElementById('shortlistConfirmModal');
    if (modal) {
        modal.classList.add('open');
    }
}

function closeShortlistConfirmModal() {
    var modal = document.getElementById('shortlistConfirmModal');
    if (modal) {
        modal.classList.remove('open');
    }
    shortlistConfirmButton = null;
    allowShortlistSubmit = false;
}

function submitShortlistConfirm() {
    if (!shortlistConfirmButton) {
        return;
    }
    var btn = shortlistConfirmButton;
    var modal = document.getElementById('shortlistConfirmModal');
    if (modal) {
        modal.classList.remove('open');
    }
    allowShortlistSubmit = true;
    btn.click();
}

function confirmRejectAction(button) {
    if (allowRejectSubmit) {
        allowRejectSubmit = false;
        return true;
    }
    rejectConfirmButton = button;
    openRejectConfirmModal();
    return false;
}

function openRejectConfirmModal() {
    var modal = document.getElementById('rejectConfirmModal');
    if (modal) {
        modal.classList.add('open');
    }
}

function closeRejectConfirmModal() {
    var modal = document.getElementById('rejectConfirmModal');
    if (modal) {
        modal.classList.remove('open');
    }
}

function submitRejectConfirm() {
    if (!rejectConfirmButton) {
        closeRejectConfirmModal();
        return;
    }
    var btn = rejectConfirmButton;
    closeRejectConfirmModal();
    allowRejectSubmit = true;
    btn.click();
}

// Update openScheduleInterviewModal
function openScheduleInterviewModal() {
    var modal = document.getElementById('scheduleInterviewModal');
    if (modal) modal.classList.add('open');
    setInterviewDateMin();
    populateHourOptions();
}
function closeScheduleInterviewModal() {
    var modal = document.getElementById('scheduleInterviewModal');
    if (modal) {
        modal.classList.remove('open');
    }
}

function openScheduleInterviewAfterAjax(data) {
    if (!data) {
        return;
    }

    if (data.status === 'begin' && data.source) {
        activeScheduleRow = data.source.closest('.applicant-row') || activeScheduleRow;
        activeScheduleAppId = getRowAppId(activeScheduleRow);
    }

    if (data.status === 'success') {
        setTimeout(openScheduleInterviewModal, 60);
        setInterviewDateMin();
        initInterviewTimePastBlock();  

    }
}

//function closeScheduleInterviewAfterAjax(data) {
//    if (!data) {
//        return;
//    }
//
//    if (data.status === 'success') {
//        if (isAjaxValidationFailed(data)) {
//            openScheduleInterviewModal();
//            return;
//        }
//        var row = findRowByAppId(activeScheduleAppId) || activeScheduleRow;
//        var oldStatus = row ? row.dataset.status : '';
//        updateRowStatus(row, 'Interview Scheduled');
//        updateCountsForStatusChange(oldStatus, 'Interview Scheduled');
//        replaceActionsForScheduled(row);
//        keepViewAfterAction(row);
//        closeScheduleInterviewModal();
//        showToast('Scheduled', 'success');
//    }
//}

function closeScheduleInterviewAfterAjax(data) {
    if (!data) {
        return;
    }

    if (data.status === 'success') {
        var errorEls = document.querySelectorAll('.field-error');
        var hasErrors = Array.prototype.some.call(errorEls, function (el) {
            return el.textContent.trim() !== '';
        });

        if (hasErrors) {
            setInterviewDateMin(); // the date field was re-rendered, so re-apply the min restriction
            return; // modal is already open, errors are already visible
        }

        var row = findRowByAppId(activeScheduleAppId) || activeScheduleRow;
        var oldStatus = row ? row.dataset.status : '';
        updateRowStatus(row, 'Interview Scheduled');
        updateCountsForStatusChange(oldStatus, 'Interview Scheduled');
        replaceActionsForScheduled(row);
        keepViewAfterAction(row);
        closeScheduleInterviewModal();
        showToast('Scheduled', 'success');
    }
}
function isAjaxValidationFailed(data) {
    try {
        var xml = data.responseXML;
        return !!(xml && xml.getElementsByTagName('validationFailed').length);
    } catch (e) {
        return false;
    }
}

function afterApplicantTableAjax(data) {
    if (data && data.status === 'success') {
        initApplicantsPage();
    }
}

document.addEventListener('click', function (e) {
    var profileDrawer = document.getElementById('profileDrawer');
    var scheduleModal = document.getElementById('scheduleInterviewModal');
    var shortlistModal = document.getElementById('shortlistConfirmModal');
    var rejectModal = document.getElementById('rejectConfirmModal');

    if (profileDrawer && e.target === profileDrawer) {
        closeProfileDrawer();
    }
    if (scheduleModal && e.target === scheduleModal) {
        closeScheduleInterviewModal();
    }
    if (shortlistModal && e.target === shortlistModal) {
        closeShortlistConfirmModal();
    }
    if (rejectModal && e.target === rejectModal) {
        rejectConfirmButton = null;
        closeRejectConfirmModal();
    }
});
function setInterviewDateMin() {
    const input = document.getElementById('interviewDateInput')
            || document.querySelector('input[type="date"]');
    if (!input)
        return;

    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');

    input.min = `${year}-${month}-${day}`; // date only now
}
function validateInterviewDateBeforeSubmit() {
    var dateInput = document.getElementById('interviewDateInput');
    var hidden    = document.getElementById('interviewTimeInput');
    var errSpan   = document.getElementById('interviewTimeError');

    if (errSpan) errSpan.style.display = 'none';

    if (!dateInput || !dateInput.value) return true;
    if (!hidden || !hidden.value) {
        if (errSpan) { errSpan.textContent = 'Please select a time.'; errSpan.style.display = 'block'; }
        return false;
    }

    var selected = new Date(dateInput.value + 'T' + hidden.value);
    var now = new Date();

    if (selected <= now) {
        if (errSpan) { errSpan.textContent = 'Selected time is in the past. Please choose a future time.'; errSpan.style.display = 'block'; }
        showToast('Please select a future time.', 'error');
        return false;
    }

    return true;
}
function onHourOrAmPmChanged() {
    populateMinuteOptions();
    syncHiddenTimeInput();
}
document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape') {
        closeProfileDrawer();
        closeScheduleInterviewModal();
        closeShortlistConfirmModal();
        rejectConfirmButton = null;
        closeRejectConfirmModal();
    }
});

function initialiseActionState(row) {
    row.dataset.displayStatus = row.dataset.displayStatus || displayStatus(row.dataset.status);
}

function initApplicantsPage() {
    getApplicantRows().forEach(function (row) {
        row.dataset.filterMatch = 'true';
        row.dataset.displayStatus = row.dataset.displayStatus || displayStatus(row.dataset.status);
        initialiseActionState(row);
    });

    animateScoreBars();
    renderApplicantsPagination();
}

window.addEventListener('load', function () {
    setTimeout(initApplicantsPage, 200);
});
