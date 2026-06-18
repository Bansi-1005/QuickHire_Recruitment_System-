var interviewsCurrentPage = 1;
var interviewsPageSize = 10;
var pendingCancelButton = null;
var allowCancelSubmit = false;
var activeViewInterviewRow = null;

function getInterviewRows() {
    return Array.prototype.slice.call(document.querySelectorAll('.interview-row'));
}

function normalizeText(value) {
    return (value || '').toString().toLowerCase().replace(/\s+/g, ' ').trim();
}

function initInterviewPage() {
    var search = document.getElementById('interviewSearchInput');
    if (search && !search.dataset.bound) {
        search.dataset.bound = 'true';
        search.addEventListener('keydown', function (event) {
            if (event.key === 'Enter') {
                event.preventDefault();
                applyInterviewFilters();
            }
        });
    }
    applyInterviewFilters(false);
}

function getRowText(row) {
    return [
        row.getAttribute('data-candidate'),
        row.getAttribute('data-email'),
        row.getAttribute('data-job'),
        row.getAttribute('data-interviewer'),
        row.getAttribute('data-mode'),
        row.getAttribute('data-status'),
        row.getAttribute('data-result')
    ].join(' ');
}

function applyInterviewFilters(showMessage) {
    var searchValue = normalizeText(getValue('interviewSearchInput'));
    var statusValue = getValue('statusFilterSelect') || 'all';
    var resultValue = getValue('resultFilterSelect') || 'all';
    var modeValue = getValue('modeFilterSelect') || 'all';
    var matchedCount = 0;

    getInterviewRows().forEach(function (row) {
        var matched = true;
        var rowStatus = row.getAttribute('data-status') || '';
        var rowResult = row.getAttribute('data-result') || 'Pending';
        var rowMode = row.getAttribute('data-mode') || '';

        if (searchValue && normalizeText(getRowText(row)).indexOf(searchValue) === -1)
            matched = false;
        if (statusValue !== 'all' && rowStatus !== statusValue)
            matched = false;
        if (resultValue !== 'all' && rowResult !== resultValue)
            matched = false;
        if (modeValue !== 'all' && rowMode !== modeValue)
            matched = false;

        row.setAttribute('data-filtered', matched ? 'visible' : 'hidden');
        if (matched)
            matchedCount += 1;
    });

    interviewsCurrentPage = 1;
    renderInterviewsPagination();
    updateFilterSummary(matchedCount);

    if (showMessage !== false) {
        showToast(matchedCount + ' interview' + (matchedCount === 1 ? '' : 's') + ' found.', 'info');
    }
}

function resetInterviewFilters() {
    setValue('interviewSearchInput', '');
    setValue('statusFilterSelect', 'all');
    setValue('resultFilterSelect', 'all');
    setValue('modeFilterSelect', 'all');
    interviewsCurrentPage = 1;
    applyInterviewFilters(false);
    showToast('Filters reset. Showing all interviews.', 'info');
}

function renderInterviewsPagination() {
    var rows = getInterviewRows();
    var visibleRows = rows.filter(function (row) {
        return row.getAttribute('data-filtered') !== 'hidden';
    });
    var totalPages = Math.max(1, Math.ceil(visibleRows.length / interviewsPageSize));
    var startIndex;
    var endIndex;

    interviewsCurrentPage = Math.min(Math.max(1, interviewsCurrentPage), totalPages);
    startIndex = (interviewsCurrentPage - 1) * interviewsPageSize;
    endIndex = startIndex + interviewsPageSize;

    rows.forEach(function (row) {
        row.style.display = 'none';
    });

    visibleRows.slice(startIndex, endIndex).forEach(function (row) {
        row.style.display = '';
    });

    setText('showingStart', visibleRows.length ? String(startIndex + 1) : '0');
    setText('showingEnd', String(Math.min(endIndex, visibleRows.length)));
    setText('totalShowing', String(visibleRows.length));
    setText('pageInfo', 'Page ' + interviewsCurrentPage + ' of ' + totalPages);

    var prev = document.getElementById('prevInterviewPageBtn');
    var next = document.getElementById('nextInterviewPageBtn');
    if (prev)
        prev.disabled = interviewsCurrentPage <= 1;
    if (next)
        next.disabled = interviewsCurrentPage >= totalPages;

    var emptyState = document.getElementById('emptyState');
    if (emptyState)
        emptyState.style.display = visibleRows.length ? 'none' : 'block';
}

function goInterviewPage(direction) {
    interviewsCurrentPage += direction;
    renderInterviewsPagination();
}

function updateFilterSummary(count) {
    setText('filterSummary', count + ' visible');
}

function exportInterviewsCsv() {
    var rows = getInterviewRows().filter(function (row) {
        return row.getAttribute('data-filtered') !== 'hidden';
    });
    var csvRows = [['Candidate', 'Email', 'Job', 'Date', 'Time', 'Mode', 'Interviewer', 'Status', 'Result']];

    if (!rows.length) {
        showToast('No interviews available to export.', 'error');
        return;
    }

    rows.forEach(function (row) {
        csvRows.push([
            row.getAttribute('data-candidate') || '',
            row.getAttribute('data-email') || '',
            row.getAttribute('data-job') || '',
            row.getAttribute('data-date') || '',
            row.getAttribute('data-time') || '',
            row.getAttribute('data-mode') || '',
            row.getAttribute('data-interviewer') || '',
            row.getAttribute('data-status') || '',
            row.getAttribute('data-result') || 'Pending'
        ]);
    });

    var csv = csvRows.map(function (row) {
        return row.map(function (value) {
            return '"' + String(value).replace(/"/g, '""') + '"';
        }).join(',');
    }).join('\r\n');

    var blob = new Blob([csv], {type: 'text/csv;charset=utf-8;'});
    var link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = 'quickhire-interviews.csv';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(link.href);
    showToast('Interview CSV exported.', 'success');
}

function openCalendarPanel() {
    var list = document.getElementById('calendarList');

    var rows = getInterviewRows()
            .map(function (row) {
                return {
                    row: row,
                    dateObj: parseInterviewDateTime(row)
                };
            })
            .filter(function (item) {
                return isUpcomingInterviewStatus(item.row.getAttribute('data-status')) &&
                        item.dateObj &&
                        item.dateObj.getTime() >= startOfToday().getTime();
            })
            .sort(function (a, b) {
                return a.dateObj.getTime() - b.dateObj.getTime();
            });

    if (list) {
        list.innerHTML = '';

        if (!rows.length) {
            list.innerHTML = '<div class="table-empty-state"><h3>No upcoming interviews</h3><p>Scheduled and rescheduled interviews will appear here by nearest date.</p></div>';
        } else {
            rows.forEach(function (itemData) {
                var row = itemData.row;
                var dateObj = itemData.dateObj;
                var item = document.createElement('div');

                item.className = 'calendar-item';
                item.innerHTML =
                        '<div class="calendar-date-tile"><strong>' + escapeHtml(getCalendarDay(row, dateObj)) + '</strong><span>' + escapeHtml(getCalendarMonth(row, dateObj)) + '</span></div>' +
                        '<div class="calendar-item-main"><strong>' + escapeHtml(row.getAttribute('data-candidate') || 'Candidate') + '</strong>' +
                        '<span>' + escapeHtml(row.getAttribute('data-job') || 'Job position') + '</span>' +
                        '<small>' + escapeHtml((row.getAttribute('data-date') || '') + ' ' + (row.getAttribute('data-time') || '')) + '</small></div>' +
                        '<div class="calendar-item-side"><span class="mode-badge mode-' + normalizeText(row.getAttribute('data-mode')) + '">' + escapeHtml(row.getAttribute('data-mode') || 'Mode') + '</span>' +
                        '<span class="status-badge status-' + normalizeText(row.getAttribute('data-status')) + '">' + escapeHtml(row.getAttribute('data-status') || 'Scheduled') + '</span></div>';

                list.appendChild(item);
            });
        }
    }

    openModal(document.getElementById('calendarPanel'));
}

    function openConductInterviewModal(event) {
        if (event && event.status && event.status !== 'success') {
            return;
        }

        var data = document.getElementById('conductInterviewData');

        if (!data) {
            showToast('Unable to load interview details.', 'error');
            return;
        }

        var candidateName = data.getAttribute('data-name') || 'Candidate';
        var jobTitle = data.getAttribute('data-job') || 'Job title';
        var feedback = data.getAttribute('data-feedback') || '';
        var result = data.getAttribute('data-result') || 'Pending';

        setText('conductCandidateName', candidateName);
        setText('conductJobTitle', jobTitle);
        setText('conductCandidateAvatar', getInitial(candidateName));

        setValue('feedbackTextarea', feedback);
        setValue('resultSelect', result);

        openModal(document.getElementById('conductInterviewModal'));
    }

function closeCalendarPanel() {
    closeModal(document.getElementById('calendarPanel'));
}

function rememberViewInterviewRow(button) {
    activeViewInterviewRow = button ? button.closest('.interview-row') : null;
}

function openViewInterviewModalAfterAjax(event) {
    if (!event || event.status !== 'success') {
        return;
    }

    openViewInterviewModalFromRow(activeViewInterviewRow);
    copyInterviewHistoryIntoModal();
}

function openViewInterviewModal(button) {
    activeViewInterviewRow = button ? button.closest('.interview-row') : null;
    openViewInterviewModalFromRow(activeViewInterviewRow);
    copyInterviewHistoryIntoModal();
}

function openViewInterviewModalFromRow(row) {
    if (!row) {
        showToast('Unable to find interview details.', 'error');
        return;
    }

    var status = row.getAttribute('data-status') || '-';
    var result = row.getAttribute('data-result') || 'Pending';
    var feedback = row.getAttribute('data-feedback') || '';

    setText('viewCandidateName', row.getAttribute('data-candidate') || 'Candidate');
    setText('viewCandidateEmail', row.getAttribute('data-email') || '');
    setText('viewJobTitle', row.getAttribute('data-job') || 'Job title');
    setText('viewInterviewDate', row.getAttribute('data-date') || '-');
    setText('viewInterviewTime', row.getAttribute('data-time') || '-');
    setText('viewInterviewMode', row.getAttribute('data-mode') || '-');
    setText('viewInterviewerName', row.getAttribute('data-interviewer') || '-');
    setText('viewInterviewStatus', status);
    setText('viewInterviewResult', result);
    setText('viewModeSummary', row.getAttribute('data-mode') || '-');
    setText('viewStatusSummary', status);
    setText('viewResultSummary', result);
    setText('viewFeedback', feedback || 'No feedback provided yet');
    setText('viewCandidateAvatar', getInitial(row.getAttribute('data-candidate')));
    toggleCurrentDecisionSections(status, result, feedback);
    openModal(document.getElementById('viewInterviewModal'));
}

function toggleCurrentDecisionSections(status, result, feedback) {
    var completed = isCompletedStatus(status);
    var hasFinalResult = isFinalResult(result);
    var hasFeedback = hasUsefulFeedback(feedback);

    toggleElement('viewResultSummaryCard', completed && hasFinalResult);
    toggleElement('viewInterviewResultRow', completed && hasFinalResult);
    toggleElement('viewFeedbackSection', completed && hasFeedback);
}

function copyInterviewHistoryIntoModal() {
    var source = document.getElementById('viewInterviewHistoryMarkup');
    var target = document.getElementById('viewHistoryList');
    var currentInterviewId = activeViewInterviewRow ? activeViewInterviewRow.getAttribute('data-interview-id') : '';
    var items;
    var wrapper;

    if (!target) {
        return;
    }

    if (!source || !source.innerHTML.trim()) {
        target.innerHTML = '<p class="history-empty">No past interview history yet.</p>';
        return;
    }

    target.innerHTML = '';
    wrapper = document.createElement('div');
    wrapper.className = 'history-stack';

    items = Array.prototype.slice.call(source.querySelectorAll('.history-item')).reverse();
    items.forEach(function (item) {
        var historyInterviewId = item.getAttribute('data-history-interview-id') || '';
        var clone = item.cloneNode(true);

        if (currentInterviewId && historyInterviewId === currentInterviewId) {
            return;
        }

        decorateHistoryItem(clone, 'Past history');
        wrapper.appendChild(clone);
    });

    if (!wrapper.children.length) {
        target.innerHTML = '<p class="history-empty">No past interview history yet.</p>';
        return;
    }

    target.appendChild(wrapper);
}

function decorateHistoryItem(item, label) {
    var labelNode = item ? item.querySelector('.history-label') : null;
    var status = item ? (item.getAttribute('data-history-status') || '') : '';
    var result = item ? (item.getAttribute('data-history-result') || 'Pending') : 'Pending';
    var feedback = item ? (item.getAttribute('data-history-feedback') || '') : '';
    var resultNode = item ? item.querySelector('.history-result') : null;
    var feedbackNode = item ? item.querySelector('.history-feedback') : null;
    var completed = isCompletedStatus(status);

    if (labelNode) {
        labelNode.textContent = label || 'Interview record';
    }

    if (resultNode && (!completed || !isFinalResult(result))) {
        resultNode.remove();
    }

    if (feedbackNode && (!completed || !hasUsefulFeedback(feedback))) {
        feedbackNode.remove();
    }

    if (item) {
        item.setAttribute('aria-label', 'Past interview history: ' + status);
    }
}

function isCompletedStatus(status) {
    return normalizeText(status) === 'completed';
}

function isFinalResult(result) {
    var value = normalizeText(result);
    return value === 'selected' || value === 'rejected';
}

function hasUsefulFeedback(feedback) {
    var value = normalizeText(feedback);
    return value && value !== 'no feedback provided' && value !== 'no feedback provided yet';
}

function toggleElement(id, show) {
    var el = document.getElementById(id);
    if (el) {
        el.style.display = show ? '' : 'none';
    }
}

function closeViewInterviewModal() {
    closeModal(document.getElementById('viewInterviewModal'));
}

function openCalendarPanel() {
    var list = document.getElementById('calendarList');

    var rows = getInterviewRows()
            .map(function (row) {
                return {
                    row: row,
                    dateObj: parseInterviewDateTime(row)
                };
            })
            .filter(function (item) {
                return isCalendarInterviewStatus(item.row.getAttribute('data-status'));
            })
            .sort(function (a, b) {
                if (!a.dateObj && !b.dateObj) {
                    return 0;
                }
                if (!a.dateObj) {
                    return 1;
                }
                if (!b.dateObj) {
                    return -1;
                }
                return a.dateObj.getTime() - b.dateObj.getTime();
            });

    if (list) {
        list.innerHTML = '';

        if (!rows.length) {
            list.innerHTML = '<div class="table-empty-state"><h3>No scheduled interviews</h3><p>Scheduled and rescheduled interviews will appear here.</p></div>';
        } else {
            rows.forEach(function (itemData) {
                var row = itemData.row;
                var dateObj = itemData.dateObj;
                var item = document.createElement('div');

                item.className = 'calendar-item';
                item.innerHTML =
                        '<div class="calendar-date-tile"><strong>' + escapeHtml(getCalendarDay(row, dateObj)) + '</strong><span>' + escapeHtml(getCalendarMonth(row, dateObj)) + '</span></div>' +
                        '<div class="calendar-item-main"><strong>' + escapeHtml(row.getAttribute('data-candidate') || 'Candidate') + '</strong>' +
                        '<span>' + escapeHtml(row.getAttribute('data-job') || 'Job position') + '</span>' +
                        '<small>' + escapeHtml((row.getAttribute('data-date') || '') + ' ' + (row.getAttribute('data-time') || '')) + '</small></div>' +
                        '<div class="calendar-item-side"><span class="mode-badge mode-' + normalizeText(row.getAttribute('data-mode')) + '">' + escapeHtml(row.getAttribute('data-mode') || 'Mode') + '</span>' +
                        '<span class="status-badge status-' + normalizeText(row.getAttribute('data-status')) + '">' + escapeHtml(row.getAttribute('data-status') || 'Scheduled') + '</span></div>';

                list.appendChild(item);
            });
        }
    }

    openModal(document.getElementById('calendarPanel'));
}
function closeConductInterviewModal() {
    closeModal(document.getElementById('conductInterviewModal'));
}

function submitConductInterview() {
    var feedback = (getValue('feedbackTextarea') || '').trim();
    var result = getValue('resultSelect') || 'Pending';

    if (!feedback) {
        showToast('Please enter interview feedback.', 'error');
        return;
    }

    if (result === 'Pending') {
        showToast('Please select Selected or Rejected before submitting.', 'error');
        return;
    }

    setValue('interviewForm:conductFeedback', feedback);
    setValue('interviewForm:conductResult', result);
    clickById('interviewForm:submitConductBtn');
}

function handleConductSubmitEvent(event) {
    if (!event || event.status === 'success') {
        closeConductInterviewModal();
        showToast('Interview result saved.', 'success');
        setTimeout(initInterviewPage, 120);
    }
}

function openRescheduleModal(event) {
    if (event && event.status && event.status !== 'success')
        return;

    var name = getValue('interviewForm:rescheduleCandidateNameHidden');
    setText('rescheduleCandidateName', name || 'Candidate');
    setText('rescheduleJobTitle', getValue('interviewForm:rescheduleJobTitleHidden') || 'Job title');
    setText('rescheduleCandidateAvatar', getInitial(name));
    setValue('rescheduleMode', getValue('interviewForm:rescheduleNewMode') || 'Online');
    setValue('rescheduleInterviewer', getValue('interviewForm:rescheduleNewInterviewer') || '');

    // Set min date to today
    setRescheduleDateMin();

    // Read existing datetime from bean e.g. "2026-06-20T10:30"
    var existingDateTime = getValue('interviewForm:rescheduleNewDateTime') || '';
    setValue('rescheduleDateTime', existingDateTime);

    // Pre-fill date input
    var dateInput = document.getElementById('rescheduleDateInput');
    var existingDate = '';
    var existingHour24 = -1;
    var existingMinute = -1;

    if (existingDateTime && existingDateTime.indexOf('T') !== -1) {
        var parts = existingDateTime.split('T');
        existingDate = parts[0];          // "2026-06-20"
        var timeParts = parts[1].split(':');
        existingHour24 = parseInt(timeParts[0], 10);   // 0-23
        existingMinute = parseInt(timeParts[1], 10);   // 0-59
    }

    if (dateInput) {
        dateInput.value = existingDate;
    }

    // Populate hours first
    populateRescheduleHourOptions();

    // Now pre-select the correct hour and AM/PM
    if (existingHour24 >= 0) {
        var ampm = existingHour24 >= 12 ? 'PM' : 'AM';
        var hour12 = existingHour24 % 12;
        if (hour12 === 0) hour12 = 12;

        var ampmSel = document.getElementById('rescheduleAmPmInput');
        if (ampmSel) {
            ampmSel.value = ampm;
        }

        var hourSel = document.getElementById('rescheduleHourInput');
        if (hourSel) {
            hourSel.value = String(hour12);
        }

        // Populate minutes based on selected hour
        populateRescheduleMinuteOptions();

        // Pre-select the minute
        var minuteSel = document.getElementById('rescheduleMinuteInput');
        if (minuteSel && existingMinute >= 0) {
            minuteSel.value = pad2(existingMinute);
        }

        // Sync the hidden input
        syncRescheduleHiddenInput();
    }

    openModal(document.getElementById('rescheduleModal'));
}
function closeRescheduleModal() {
    closeModal(document.getElementById('rescheduleModal'));
}
function todayDateValue() {
    var now = new Date();
    return now.getFullYear() + '-' +
           String(now.getMonth() + 1).padStart(2, '0') + '-' +
           String(now.getDate()).padStart(2, '0');
}

function pad2(n) {
    return String(n).padStart(2, '0');
}

function onRescheduleDateChanged() {
    // Clear time selections when date changes
    var minuteSel = document.getElementById('rescheduleMinuteInput');
    if (minuteSel) minuteSel.innerHTML = '<option value="">MM</option>';
    var hidden = document.getElementById('rescheduleDateTime');
    if (hidden) hidden.value = '';
    populateRescheduleHourOptions();
}

function onRescheduleHourOrAmPmChanged() {
    populateRescheduleMinuteOptions();
    syncRescheduleHiddenInput();
}

function populateRescheduleHourOptions() {
    var hourSel = document.getElementById('rescheduleHourInput');
    if (!hourSel) return;

    hourSel.innerHTML = '<option value="">HH</option>';
    for (var h = 1; h <= 12; h++) {
        var opt = document.createElement('option');
        opt.value = String(h);
        opt.textContent = pad2(h);
        hourSel.appendChild(opt);
    }
    populateRescheduleMinuteOptions();
}

function populateRescheduleMinuteOptions() {
    var hourSel   = document.getElementById('rescheduleHourInput');
    var minuteSel = document.getElementById('rescheduleMinuteInput');
    var ampmSel   = document.getElementById('rescheduleAmPmInput');
    var errSpan   = document.getElementById('rescheduleTimeError');
    if (!hourSel || !minuteSel || !ampmSel) return;

    var selectedHour12 = parseInt(hourSel.value, 10);
    var selectedAmPm   = ampmSel.value;

    minuteSel.innerHTML = '<option value="">MM</option>';

    if (!selectedHour12 || !selectedAmPm) return;

    // Convert to 24-hour
    var hour24 = selectedHour12;
    if (selectedAmPm === 'AM' && selectedHour12 === 12) hour24 = 0;
    if (selectedAmPm === 'PM' && selectedHour12 !== 12) hour24 = selectedHour12 + 12;

    var dateInput = document.getElementById('rescheduleDateInput');
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
        if (errSpan) {
            errSpan.textContent = 'This hour has already passed. Please select a future hour.';
            errSpan.style.display = 'block';
        }
    } else {
        if (errSpan) errSpan.style.display = 'none';
    }
}

function syncRescheduleHiddenInput() {
    var hourSel   = document.getElementById('rescheduleHourInput');
    var minuteSel = document.getElementById('rescheduleMinuteInput');
    var ampmSel   = document.getElementById('rescheduleAmPmInput');
    var dateInput = document.getElementById('rescheduleDateInput');
    var hidden    = document.getElementById('rescheduleDateTime');
    if (!hourSel || !minuteSel || !ampmSel || !dateInput || !hidden) return;

    var h    = parseInt(hourSel.value, 10);
    var m    = minuteSel.value;
    var ampm = ampmSel.value;
    var date = dateInput.value;

    if (!h || !m || m === '' || m === 'MM' || !date) {
        hidden.value = '';
        return;
    }

    var hour24 = h;
    if (ampm === 'AM' && h === 12) hour24 = 0;
    if (ampm === 'PM' && h !== 12) hour24 = h + 12;

    // Format as yyyy-MM-ddTHH:mm for the bean
    hidden.value = date + 'T' + pad2(hour24) + ':' + m;
}

function setRescheduleDateMin() {
    var input = document.getElementById('rescheduleDateInput');
    if (!input) return;
    var now = new Date();
    input.min = now.getFullYear() + '-' +
                pad2(now.getMonth() + 1) + '-' +
                pad2(now.getDate());
}
function submitReschedule() {
    var hidden      = document.getElementById('rescheduleDateTime');
    var dateInput   = document.getElementById('rescheduleDateInput');
    var mode        = getValue('rescheduleMode') || 'Online';
    var interviewer = (getValue('rescheduleInterviewer') || '').trim();
    var errSpan     = document.getElementById('rescheduleTimeError');

    if (errSpan) errSpan.style.display = 'none';

    if (!dateInput || !dateInput.value) {
        showToast('Please select a new date.', 'error');
        return;
    }

    if (!hidden || !hidden.value) {
        showToast('Please select a valid time.', 'error');
        return;
    }

    // Validate not in past
    var selected = new Date(hidden.value);
    if (selected <= new Date()) {
        if (errSpan) {
            errSpan.textContent = 'Selected time is in the past. Please choose a future time.';
            errSpan.style.display = 'block';
        }
        showToast('Please select a future date and time.', 'error');
        return;
    }

    if (!interviewer) {
        showToast('Please enter interviewer name.', 'error');
        return;
    }

    setValue('interviewForm:rescheduleNewDateTime', hidden.value);
    setValue('interviewForm:rescheduleNewMode', mode);
    setValue('interviewForm:rescheduleNewInterviewer', interviewer);
    clickById('interviewForm:submitRescheduleBtn');
}
function handleRescheduleSubmitEvent(event) {
    if (!event || event.status === 'success') {
        closeRescheduleModal();
        showToast('Interview rescheduled.', 'success');
        setTimeout(initInterviewPage, 120);
    }
}

function confirmCancelInterviewAction(button) {
    if (allowCancelSubmit) {
        allowCancelSubmit = false;
        return true;
    }

    pendingCancelButton = button;
    var row = button ? button.closest('.interview-row') : null;
    if (row) {
        setText('cancelCandidateName', row.getAttribute('data-candidate') || 'Candidate');
        setText('cancelJobTitle', row.getAttribute('data-job') || 'Job title');
        setText('cancelCandidateAvatar', getInitial(row.getAttribute('data-candidate')));
    }
    openModal(document.getElementById('cancelInterviewModal'));
    return false;
}

function closeCancelInterviewModal() {
    pendingCancelButton = null;
    allowCancelSubmit = false;
    closeModal(document.getElementById('cancelInterviewModal'));
}

function submitCancelInterview() {
    var button = pendingCancelButton;
    closeModal(document.getElementById('cancelInterviewModal'));
    pendingCancelButton = null;
    if (button) {
        allowCancelSubmit = true;
        button.click();
    }
}

function handleCancelAjaxEvent(event) {
    if (!event || event.status === 'success') {
        closeCancelInterviewModal();
        showToast('Interview cancelled.', 'success');
        setTimeout(initInterviewPage, 120);
    }
}

function openModal(overlay) {
    if (!overlay)
        return;
    overlay.style.display = 'flex';
    window.requestAnimationFrame(function () {
        overlay.classList.add('open');
    });
    document.body.style.overflow = 'hidden';
}

function closeModal(overlay) {
    if (!overlay)
        return;
    overlay.classList.remove('open');
    overlay.style.display = 'none';
    document.body.style.overflow = '';
}

function setText(id, value) {
    var el = document.getElementById(id);
    if (el)
        el.textContent = value || '';
}

function getValue(id) {
    var el = document.getElementById(id);
    return el ? el.value : '';
}

function setValue(id, value) {
    var el = document.getElementById(id);
    if (el)
        el.value = value || '';
}

function clickById(id) {
    var el = document.getElementById(id);
    if (el)
        el.click();
}

function getInitial(name) {
    var clean = (name || '').trim();
    return clean ? clean.charAt(0).toUpperCase() : 'C';
}

function escapeHtml(value) {
    return String(value || '')
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
}

function showToast(message, type) {
    var existing = document.getElementById('qhToast');
    if (existing)
        existing.remove();

    var toast = document.createElement('div');
    toast.id = 'qhToast';
    toast.className = 'qh-toast qh-toast-' + (type || 'info');
    toast.textContent = message;
    document.body.appendChild(toast);

    window.requestAnimationFrame(function () {
        toast.classList.add('show');
    });

    setTimeout(function () {
        toast.classList.remove('show');
        setTimeout(function () {
            if (toast.parentNode)
                toast.parentNode.removeChild(toast);
        }, 260);
    }, 2400);
}

function setMinDateTime(id) {
    var input = document.getElementById(id);
    if (!input)
        return;
    var now = new Date();
    now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
    input.min = now.toISOString().slice(0, 16);
}

function isCalendarInterviewStatus(status) {
    var value = normalizeText(status);
    return value === 'scheduled' || value === 'rescheduled';
}

function startOfToday() {
    var today = new Date();
    today.setHours(0, 0, 0, 0);
    return today;
}

function parseInterviewDateTime(row) {
    var dateText = row ? (row.getAttribute('data-date') || '') : '';
    var timeText = row ? (row.getAttribute('data-time') || '') : '';
    var parts = dateText.trim().split(/\s+/);
    var timeMatch = timeText.trim().match(/^(\d{1,2}):(\d{2})\s*(AM|PM)$/i);
    var months = {
        jan: 0, feb: 1, mar: 2, apr: 3, may: 4, jun: 5,
        jul: 6, aug: 7, sep: 8, oct: 9, nov: 10, dec: 11
    };
    var day;
    var month;
    var year;
    var hour;
    var minute;
    var ampm;

    if (parts.length < 3 || !timeMatch) {
        return null;
    }

    day = parseInt(parts[0], 10);
    month = months[parts[1].toLowerCase().slice(0, 3)];
    year = parseInt(parts[2], 10);
    hour = parseInt(timeMatch[1], 10);
    minute = parseInt(timeMatch[2], 10);
    ampm = timeMatch[3].toUpperCase();

    if (isNaN(day) || month === undefined || isNaN(year) || isNaN(hour) || isNaN(minute)) {
        return null;
    }

    if (ampm === 'PM' && hour < 12) {
        hour += 12;
    }
    if (ampm === 'AM' && hour === 12) {
        hour = 0;
    }

    return new Date(year, month, day, hour, minute, 0, 0);
}

function getCalendarDay(row, parsedDate) {
    if (parsedDate) {
        return String(parsedDate.getDate());
    }
    var text = row ? row.getAttribute('data-date') : '';
    var match = (text || '').match(/\b(\d{1,2})\b/);
    return match ? match[1] : '--';
}

function getCalendarMonth(row, parsedDate) {
    if (parsedDate) {
        return parsedDate.toLocaleString('en-US', {month: 'short'});
    }
    var text = row ? row.getAttribute('data-date') : '';
    var match = (text || '').match(/\b([A-Za-z]{3,})\b/);
    return match ? match[1].slice(0, 3) : 'Date';
}

document.addEventListener('click', function (event) {
    if (event.target && event.target.classList && event.target.classList.contains('modal-overlay')) {
        closeModal(event.target);
    }
});

document.addEventListener('keydown', function (event) {
    if (event.key === 'Escape') {
        closeViewInterviewModal();
        closeConductInterviewModal();
        closeRescheduleModal();
        closeCalendarPanel();
        closeCancelInterviewModal();
    }
});

window.addEventListener('load', function () {
    setTimeout(initInterviewPage, 150);
});
