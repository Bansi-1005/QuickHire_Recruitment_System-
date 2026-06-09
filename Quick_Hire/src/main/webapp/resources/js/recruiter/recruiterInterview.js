var interviewsCurrentPage = 1;
var interviewsPageSize = 10;
var pendingCancelRow = null;

function getInterviewRows() {
    return Array.prototype.slice.call(document.querySelectorAll('.interview-row'));
}

function normalizeText(value) {
    return (value || '').toString().toLowerCase().trim();
}

function initInterviewPage() {
    var search = document.getElementById('interviewSearchInput');
    if (search) {
        search.addEventListener('keydown', function (event) {
            if (event.key === 'Enter') {
                event.preventDefault();
                applyInterviewFilters();
            }
        });
    }

    updateTotalCount();
    applyInterviewFilters(false);
}

function updateTotalCount() {
    var countEl = document.getElementById('totalInterviewCount');
    if (countEl) {
        countEl.textContent = getInterviewRows().length;
    }
}

function getRowText(row) {
    return [
        row.getAttribute('data-candidate'),
        row.getAttribute('data-email'),
        row.getAttribute('data-job'),
        row.getAttribute('data-interviewer'),
        row.textContent
    ].join(' ');
}

function applyInterviewFilters(showMessage) {
    var searchValue = normalizeText(document.getElementById('interviewSearchInput') && document.getElementById('interviewSearchInput').value);
    var statusValue = document.getElementById('statusFilterSelect') ? document.getElementById('statusFilterSelect').value : 'all';
    var resultValue = document.getElementById('resultFilterSelect') ? document.getElementById('resultFilterSelect').value : 'all';
    var modeValue = document.getElementById('modeFilterSelect') ? document.getElementById('modeFilterSelect').value : 'all';
    var matchedCount = 0;

    getInterviewRows().forEach(function (row) {
        var rowText = normalizeText(getRowText(row));
        var rowStatus = row.getAttribute('data-status') || '';
        var rowResult = row.getAttribute('data-result') || 'Pending';
        var rowMode = row.getAttribute('data-mode') || '';
        var matched = true;

        if (searchValue && rowText.indexOf(searchValue) === -1) matched = false;
        if (statusValue !== 'all' && rowStatus !== statusValue) matched = false;
        if (resultValue !== 'all' && rowResult !== resultValue) matched = false;
        if (modeValue !== 'all' && rowMode !== modeValue) matched = false;

        row.setAttribute('data-filtered', matched ? 'visible' : 'hidden');
        if (matched) matchedCount += 1;
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
    if (prev) prev.disabled = interviewsCurrentPage <= 1;
    if (next) next.disabled = interviewsCurrentPage >= totalPages;

    var emptyState = document.getElementById('emptyState');
    if (emptyState) emptyState.style.display = visibleRows.length ? 'none' : 'block';
}

function goInterviewPage(direction) {
    interviewsCurrentPage += direction;
    renderInterviewsPagination();
}

function updateFilterSummary(count) {
    var summary = document.getElementById('filterSummary');
    if (summary) {
        summary.textContent = count + ' visible';
    }
}

function exportInterviewsCsv() {
    var rows = getInterviewRows().filter(function (row) {
        return row.getAttribute('data-filtered') !== 'hidden';
    });
    var csvRows = [['Candidate', 'Email', 'Job', 'Schedule', 'Mode', 'Status', 'Result']];

    rows.forEach(function (row) {
        var cells = row.querySelectorAll('td');
        csvRows.push([
            row.getAttribute('data-candidate') || '',
            row.getAttribute('data-email') || '',
            row.getAttribute('data-job') || '',
            cells[2] ? cells[2].innerText.replace(/\s+/g, ' ').trim() : '',
            row.getAttribute('data-mode') || '',
            row.getAttribute('data-status') || '',
            row.getAttribute('data-result') || 'Pending'
        ]);
    });

    if (!rows.length) {
        showToast('No interviews available to export.', 'error');
        return;
    }

    var csv = csvRows.map(function (row) {
        return row.map(function (value) {
            return '"' + String(value).replace(/"/g, '""') + '"';
        }).join(',');
    }).join('\r\n');

    var blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
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
    var rows = getInterviewRows().filter(function (row) {
        return row.getAttribute('data-filtered') !== 'hidden';
    });

    if (list) {
        list.innerHTML = '';
        if (!rows.length) {
            list.innerHTML = '<div class="table-empty-state"><h3>No interviews found</h3><p>Reset filters to view calendar items.</p></div>';
        } else {
            rows.forEach(function (row) {
                var cells = row.querySelectorAll('td');
                var item = document.createElement('div');
                item.className = 'calendar-item';
                item.innerHTML =
                    '<div class="calendar-date-tile"><strong>' + escapeHtml(getCalendarDay(cells[2])) + '</strong><span>' + escapeHtml(getCalendarMonth(cells[2])) + '</span></div>' +
                    '<div class="calendar-item-main"><strong>' + escapeHtml(row.getAttribute('data-candidate') || 'Candidate') + '</strong>' +
                    '<span>' + escapeHtml(row.getAttribute('data-job') || 'Job position') + '</span>' +
                    '<small>' + escapeHtml(cells[2] ? cells[2].innerText.replace(/\s+/g, ' ').trim() : '') + '</small></div>' +
                    '<div class="calendar-item-side"><span class="mode-badge mode-' + normalizeText(row.getAttribute('data-mode')) + '">' + escapeHtml(row.getAttribute('data-mode') || 'Mode') + '</span>' +
                    '<span class="status-badge status-' + normalizeText(row.getAttribute('data-status')) + '">' + escapeHtml(row.getAttribute('data-status') || 'Scheduled') + '</span></div>';
                list.appendChild(item);
            });
        }
    }

    openModal(document.getElementById('calendarPanel'));
}

function closeCalendarPanel() {
    closeModal(document.getElementById('calendarPanel'));
}

function openCancelInterviewModal(button) {
    var row = button ? button.closest('.interview-row') : null;
    pendingCancelRow = row;

    if (!row) {
        showToast('Unable to find interview row.', 'error');
        return;
    }

    setText('cancelCandidateName', row.getAttribute('data-candidate') || 'Candidate');
    setText('cancelJobTitle', row.getAttribute('data-job') || 'Job title');
    setText('cancelCandidateAvatar', getInitial(row.getAttribute('data-candidate')));
    openModal(document.getElementById('cancelInterviewModal'));
}

function closeCancelInterviewModal() {
    pendingCancelRow = null;
    closeModal(document.getElementById('cancelInterviewModal'));
}

function confirmCancelInterview() {
    var row = pendingCancelRow;

    if (!row) {
        closeCancelInterviewModal();
        return;
    }

    row.setAttribute('data-status', 'Cancelled');
    var badge = row.querySelector('.status-badge');
    if (badge) {
        badge.className = 'status-badge status-cancelled';
        badge.textContent = 'Cancelled';
    }

    closeCancelInterviewModal();
    applyInterviewFilters(false);
    showToast('Interview cancelled in this view. Connect a backend cancel action when available.', 'success');
}

function openViewInterviewModal(name, email, job, date, time, mode, interviewer, status, result, feedback) {
    setText('viewCandidateName', name || 'Candidate');
    setText('viewCandidateEmail', email || '');
    setText('viewJobTitle', job || 'Job title');
    setText('viewInterviewDate', date || '-');
    setText('viewInterviewTime', time || '-');
    setText('viewInterviewMode', mode || '-');
    setText('viewInterviewerName', interviewer || '-');
    setText('viewInterviewStatus', status || '-');
    setText('viewInterviewResult', result || 'Pending');
    setText('viewModeSummary', mode || '-');
    setText('viewStatusSummary', status || '-');
    setText('viewResultSummary', result || 'Pending');
    setText('viewFeedback', feedback || 'No feedback provided yet');
    setText('viewCandidateAvatar', getInitial(name));
    openModal(document.getElementById('viewInterviewModal'));
}

function closeViewInterviewModal() {
    closeModal(document.getElementById('viewInterviewModal'));
}

function openConductInterviewModal(event) {
    if (event && event.status && event.status !== 'success') return;

    var data = document.getElementById('conductInterviewData');
    var name = data ? data.getAttribute('data-name') : '';
    var job = data ? data.getAttribute('data-job') : '';
    var feedback = data ? data.getAttribute('data-feedback') : '';
    var result = data ? data.getAttribute('data-result') : '';

    setText('conductCandidateName', name || 'Candidate');
    setText('conductJobTitle', job || 'Job title');
    setText('conductCandidateAvatar', getInitial(name));
    setValue('feedbackTextarea', feedback || '');
    setValue('resultSelect', result || 'Pending');

    openModal(document.getElementById('conductInterviewModal'));
}

function closeConductInterviewModal() {
    closeModal(document.getElementById('conductInterviewModal'));
}

function submitConductInterview() {
    var feedback = (document.getElementById('feedbackTextarea') && document.getElementById('feedbackTextarea').value || '').trim();
    var result = document.getElementById('resultSelect') ? document.getElementById('resultSelect').value : 'Pending';

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
        showToast('Interview result saved successfully.', 'success');
        setTimeout(initInterviewPage, 250);
    }
}

function openRescheduleModal(event) {
    if (event && event.status && event.status !== 'success') return;

    var nameEl = document.getElementById('interviewForm:rescheduleCandidateNameHidden');
    var jobEl = document.getElementById('interviewForm:rescheduleJobTitleHidden');
    var dateEl = document.getElementById('interviewForm:rescheduleNewDateTime');
    var modeEl = document.getElementById('interviewForm:rescheduleNewMode');
    var interviewerEl = document.getElementById('interviewForm:rescheduleNewInterviewer');
    var name = nameEl ? nameEl.value : '';

    setText('rescheduleCandidateName', name || 'Candidate');
    setText('rescheduleJobTitle', jobEl ? jobEl.value : 'Job title');
    setText('rescheduleCandidateAvatar', getInitial(name));
    setValue('rescheduleDateTime', dateEl ? dateEl.value : '');
    setValue('rescheduleMode', modeEl ? modeEl.value : 'Online');
    setValue('rescheduleInterviewer', interviewerEl ? interviewerEl.value : '');

    openModal(document.getElementById('rescheduleModal'));
}

function closeRescheduleModal() {
    closeModal(document.getElementById('rescheduleModal'));
}

function submitReschedule() {
    var date = document.getElementById('rescheduleDateTime') ? document.getElementById('rescheduleDateTime').value : '';
    var mode = document.getElementById('rescheduleMode') ? document.getElementById('rescheduleMode').value : 'Online';
    var interviewer = (document.getElementById('rescheduleInterviewer') && document.getElementById('rescheduleInterviewer').value || '').trim();

    if (!date) {
        showToast('Please select a new date and time.', 'error');
        return;
    }

    if (!interviewer) {
        showToast('Please enter interviewer name.', 'error');
        return;
    }

    setValue('interviewForm:rescheduleNewDateTime', date);
    setValue('interviewForm:rescheduleNewMode', mode);
    setValue('interviewForm:rescheduleNewInterviewer', interviewer);
    clickById('interviewForm:submitRescheduleBtn');
}

function handleRescheduleSubmitEvent(event) {
    if (!event || event.status === 'success') {
        closeRescheduleModal();
        showToast('Interview rescheduled successfully.', 'success');
        setTimeout(initInterviewPage, 250);
    }
}

function openModal(overlay) {
    if (!overlay) return;
    overlay.style.display = 'flex';
    window.requestAnimationFrame(function () {
        overlay.classList.add('open');
    });
    document.body.style.overflow = 'hidden';
}

function closeModal(overlay) {
    if (!overlay) return;
    overlay.classList.remove('open');
    overlay.style.display = 'none';
    document.body.style.overflow = '';
}

function setText(id, value) {
    var el = document.getElementById(id);
    if (el) el.textContent = value || '';
}

function setValue(id, value) {
    var el = document.getElementById(id);
    if (el) el.value = value || '';
}

function clickById(id) {
    var el = document.getElementById(id);
    if (el) el.click();
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
    var toast = document.createElement('div');
    toast.className = 'qh-toast qh-toast-' + (type || 'info');
    toast.textContent = message;
    document.body.appendChild(toast);

    window.requestAnimationFrame(function () {
        toast.classList.add('show');
    });

    setTimeout(function () {
        toast.classList.remove('show');
        setTimeout(function () {
            if (toast.parentNode) toast.parentNode.removeChild(toast);
        }, 260);
    }, 2800);
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

function getCalendarText(cell) {
    return cell ? cell.innerText.replace(/\s+/g, ' ').trim() : '';
}

function getCalendarDay(cell) {
    var text = getCalendarText(cell);
    var match = text.match(/\b(\d{1,2})\b/);
    return match ? match[1] : '--';
}

function getCalendarMonth(cell) {
    var text = getCalendarText(cell);
    var match = text.match(/\b([A-Za-z]{3,})\b/);
    return match ? match[1].slice(0, 3) : 'Date';
}
