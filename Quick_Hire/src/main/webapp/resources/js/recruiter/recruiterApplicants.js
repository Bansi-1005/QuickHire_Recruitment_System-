var applicantsCurrentPage = 1;
var applicantsPageSize = 20;
var shortlistConfirmButton = null;
var allowShortlistSubmit = false;

function getApplicantRows() {
    return Array.prototype.slice.call(
        document.querySelectorAll('.applicant-row')
    );
}

// AFTER:
function handleShortlistAjaxEvent(data) {
    if (!data) { return; }
    if (data.status === 'success') {
        showToast('Candidate shortlisted successfully.', 'success');
    }
    if (data.status === 'complete') {
        setTimeout(function () {
            renderApplicantsPagination();
            lockShortlistedActions();   // <-- ADD THIS LINE ONLY
        }, 120);
    }
}

function renderApplicantsPagination() {
    var rows = getApplicantRows();
    var totalPages = Math.max(1, Math.ceil(rows.length / applicantsPageSize));

    if (applicantsCurrentPage > totalPages) {
        applicantsCurrentPage = totalPages;
    }

    if (applicantsCurrentPage < 1) {
        applicantsCurrentPage = 1;
    }

    rows.forEach(function (row, index) {
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

function openProfileDrawer(name, role, experience, skills, notes, score, status) {
    var overlay = document.getElementById('profileDrawer');
    if (!overlay) {
        return;
    }

    var drawerName = document.getElementById('drawerName');
    var drawerRole = document.getElementById('drawerRole');
    var drawerExperience = document.getElementById('drawerExperience');
    var drawerSkills = document.getElementById('drawerSkills');
    var drawerNotes = document.getElementById('drawerNotes');
    var drawerScore = document.getElementById('drawerScore');
    var drawerStatus = document.getElementById('drawerStatus');
    var drawerAvatar = document.getElementById('drawerAvatar');

    if (drawerName) drawerName.textContent = name;
    if (drawerRole) drawerRole.textContent = role;
    if (drawerExperience) drawerExperience.textContent = experience;
    if (drawerSkills) drawerSkills.textContent = skills;
    if (drawerNotes) drawerNotes.textContent = notes;
    if (drawerScore) drawerScore.textContent = score;
    if (drawerStatus) drawerStatus.textContent = status;
    if (drawerAvatar) drawerAvatar.textContent = (name && name.length) ? name.charAt(0).toUpperCase() : 'N';

    overlay.classList.add('open');
}

function closeProfileDrawer() {
    var overlay = document.getElementById('profileDrawer');
    if (overlay) {
        overlay.classList.remove('open');
    }
}

function applyStaticFilters() {
    showToast('Filters applied.', 'info');
}

function resetStaticFilters() {
    var search = document.getElementById('searchInput');
    var selects = document.querySelectorAll('.filter-select');

    if (search) {
        search.value = '';
    }

    selects.forEach(function (item) {
        if (item.id === 'jobFilter' || item.id === 'statusFilter' || item.id === 'sortFilter') {
            item.selectedIndex = 0;
        }
    });

    applicantsCurrentPage = 1;
    renderApplicantsPagination();
    showToast('Filters reset.', 'info');
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
    }, 3000);
}

function animateScoreBars() {
    var bars = document.querySelectorAll('.score-bar-fill');

    bars.forEach(function (bar) {
        var targetWidth = bar.style.width || '0%';
        bar.style.width = '0%';

        setTimeout(function () {
            bar.style.width = targetWidth;
        }, 120);
    });
}

// AFTER:
function lockShortlistedActions() {
    document.querySelectorAll('.action-shortlist').forEach(function (btn) {
        if (btn.closest('.applicant-row')) {
            var row = btn.closest('.applicant-row');
            var statusBadge = row ? row.querySelector('.status-badge') : null;
            if (statusBadge && statusBadge.textContent.trim() === 'Shortlisted') {
                btn.disabled = true;
                btn.classList.add('disabled');
                btn.value = '';
                btn.textContent = '';
                btn.style.display = 'none';   // <-- ADD THIS LINE ONLY
            }
        }
    });
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
        closeShortlistConfirmModal();
        return;
    }

    var btn = shortlistConfirmButton;
    var modal = document.getElementById('shortlistConfirmModal');

    allowShortlistSubmit = true;

    if (modal) {
        modal.classList.remove('open');
    }

    shortlistConfirmButton = null;
    btn.click();
}

function openScheduleInterviewModal() {
    var modal = document.getElementById('scheduleInterviewModal');
    if (modal) {
        modal.classList.add('open');
    }
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

    if (data.status === 'complete') {
        setTimeout(function () {
            openScheduleInterviewModal();
        }, 80);
    }
}

function closeScheduleInterviewAfterAjax(data) {
    if (!data) {
        return;
    }

    if (data.status === 'success') {
        showToast('Interview scheduled successfully.', 'success');
    }

    if (data.status === 'complete') {
        closeScheduleInterviewModal();

        setTimeout(function () {
            animateScoreBars();
            renderApplicantsPagination();
        }, 120);
    }
}

document.addEventListener('click', function (e) {
    var profileDrawer = document.getElementById('profileDrawer');
    var scheduleModal = document.getElementById('scheduleInterviewModal');
    var shortlistModal = document.getElementById('shortlistConfirmModal');

    if (profileDrawer && e.target === profileDrawer) {
        closeProfileDrawer();
    }

    if (scheduleModal && e.target === scheduleModal) {
        closeScheduleInterviewModal();
    }

    if (shortlistModal && e.target === shortlistModal) {
        closeShortlistConfirmModal();
    }
});

document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape') {
        closeProfileDrawer();
        closeScheduleInterviewModal();
        closeShortlistConfirmModal();
    }
});

function initApplicantsPage() {
    animateScoreBars();
    renderApplicantsPagination();
    lockShortlistedActions();
}

window.addEventListener('load', function () {
    setTimeout(initApplicantsPage, 200);
});