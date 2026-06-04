/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/ClientSide/javascript.js to edit this template
 */

/*
    recruiterApplicants.js
    Static preview version for QuickHire – Applicants Page
*/

/* =================================================
   SCHEDULE INTERVIEW MODAL
================================================= */
function openScheduleModal(applicationId, candidateName, jobTitle) {
    var modal = document.getElementById('scheduleModal');
    var subtitle = document.getElementById('scheduleModalSubtitle');
    var hiddenField = document.getElementById('scheduleApplicationId');

    if (!modal) { return; }

    if (subtitle) {
        subtitle.textContent = 'Scheduling for: ' + candidateName + ' → ' + jobTitle;
    }

    if (hiddenField) {
        hiddenField.value = applicationId;
    }

    clearScheduleMessages();
    setDefaultInterviewDateTime();

    modal.style.display = 'flex';

    setTimeout(function () {
        var firstInput = modal.querySelector('select, input, textarea');
        if (firstInput) { firstInput.focus(); }
    }, 120);
}

function closeScheduleModal() {
    var modal = document.getElementById('scheduleModal');
    if (modal) {
        modal.style.display = 'none';
    }
    clearScheduleMessages();
}

function clearScheduleMessages() {
    var msg = document.getElementById('scheduleMessages');
    if (msg) {
        msg.style.display = 'none';
        msg.textContent = '';
    }
}

function setDefaultInterviewDateTime() {
    var dtField = document.getElementById('interviewDateTime');
    if (!dtField || dtField.value) { return; }

    var d = new Date();
    d.setDate(d.getDate() + 1);
    d.setMinutes(0);
    d.setSeconds(0);
    d.setMilliseconds(0);

    var yyyy = d.getFullYear();
    var mm = String(d.getMonth() + 1).padStart(2, '0');
    var dd = String(d.getDate()).padStart(2, '0');
    var hh = String(d.getHours()).padStart(2, '0');

    dtField.value = yyyy + '-' + mm + '-' + dd + ' ' + hh + ':00';
}

function submitScheduleForm() {
    var round = document.getElementById('interviewRound');
    var roundName = document.getElementById('roundName');
    var dateTime = document.getElementById('interviewDateTime');
    var mode = document.getElementById('interviewMode');
    var interviewer = document.getElementById('interviewerName');
    var msg = document.getElementById('scheduleMessages');

    if (!round.value || !roundName.value.trim() || !dateTime.value.trim() || !mode.value || !interviewer.value.trim()) {
        msg.style.display = 'block';
        msg.textContent = 'Please fill all required interview details.';
        return;
    }

    msg.style.display = 'none';
    closeScheduleModal();
    showToast('Interview scheduled successfully! 📅', 'success');
}

/* =================================================
   PROFILE DRAWER
================================================= */
function openProfileDrawer(name, role, experience, skills, notes, score, status) {
    var overlay = document.getElementById('profileDrawer');
    if (!overlay) { return; }

    document.getElementById('drawerName').textContent = name;
    document.getElementById('drawerRole').textContent = role;
    document.getElementById('drawerExperience').textContent = experience;
    document.getElementById('drawerSkills').textContent = skills;
    document.getElementById('drawerNotes').textContent = notes;
    document.getElementById('drawerScore').textContent = score;
    document.getElementById('drawerStatus').textContent = status;
    document.getElementById('drawerAvatar').textContent = name.charAt(0).toUpperCase();

    overlay.classList.add('open');
}

function closeProfileDrawer() {
    var overlay = document.getElementById('profileDrawer');
    if (overlay) {
        overlay.classList.remove('open');
    }
}

/* =================================================
   INSIGHTS PANEL
================================================= */
function openInsightsPanel() {
    var panel = document.getElementById('insightsPanel');
    if (panel) {
        panel.classList.add('open');
    }
}

function closeInsightsPanel() {
    var panel = document.getElementById('insightsPanel');
    if (panel) {
        panel.classList.remove('open');
    }
}

/* =================================================
   SCREENING
================================================= */
function showScreeningLoader() {
    var loader = document.getElementById('screeningLoader');
    if (loader) {
        loader.style.display = 'flex';
    }
}

function hideScreeningLoader() {
    var loader = document.getElementById('screeningLoader');
    if (loader) {
        loader.style.opacity = '0';
        loader.style.transition = 'opacity 0.3s ease';
        setTimeout(function () {
            loader.style.display = 'none';
            loader.style.opacity = '1';
            loader.style.transition = '';
        }, 300);
    }
}

function simulateScreenAll() {
    showScreeningLoader();
    setTimeout(function () {
        hideScreeningLoader();
        showToast('Screening scores generated for all pending applicants! ⚡', 'success');
    }, 1600);
}

function simulateScreenSingle(candidateName, score) {
    showToast(candidateName + ' screened successfully. Match score: ' + score + '%.', 'success');
}

/* =================================================
   FILTER ACTIONS
================================================= */
function applyStaticFilters() {
    showToast('Static preview mode: filters UI applied.', 'info');
}

function resetStaticFilters() {
    var search = document.getElementById('searchInput');
    var selects = document.querySelectorAll('.filter-select');

    if (search) {
        search.value = '';
    }

    selects.forEach(function (item) {
        item.selectedIndex = 0;
    });

    showToast('Filters reset.', 'info');
}

/* =================================================
   TOAST
================================================= */
function showToast(message, type) {
    var existing = document.getElementById('qhToast');
    if (existing) { existing.remove(); }

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

(function injectToastStyles() {
    if (document.getElementById('qhToastStyle')) { return; }

    var style = document.createElement('style');
    style.id = 'qhToastStyle';
    style.textContent = [
        '.qh-toast {',
        '  position: fixed;',
        '  bottom: 28px;',
        '  right: 28px;',
        '  padding: 14px 22px;',
        '  border-radius: 14px;',
        '  font-size: 14px;',
        '  font-weight: 700;',
        '  color: #fff;',
        '  background: linear-gradient(90deg,#0f9b8e,#1e3c72);',
        '  box-shadow: 0 12px 30px rgba(15,35,65,0.22);',
        '  opacity: 0;',
        '  transform: translateY(20px);',
        '  transition: opacity 0.35s ease, transform 0.35s ease;',
        '  z-index: 99999;',
        '  pointer-events: none;',
        '  max-width: 320px;',
        '}',
        '.qh-toast-success { background: linear-gradient(90deg,#10b981,#059669); }',
        '.qh-toast-error   { background: linear-gradient(90deg,#ef4444,#b91c1c); }',
        '.qh-toast-info    { background: linear-gradient(90deg,#3b82f6,#1d4ed8); }'
    ].join('\n');

    document.head.appendChild(style);
})();

/* =================================================
   SCORE BAR ANIMATION
================================================= */
function animateScoreBars() {
    var bars = document.querySelectorAll('.score-bar-fill');
    bars.forEach(function (bar) {
        var targetWidth = bar.style.width;
        bar.style.width = '0%';
        setTimeout(function () {
            bar.style.width = targetWidth;
        }, 120);
    });
}

/* =================================================
   GLOBAL EVENTS
================================================= */
document.addEventListener('click', function (e) {
    var scheduleModal = document.getElementById('scheduleModal');
    if (scheduleModal && e.target === scheduleModal) {
        closeScheduleModal();
    }

    var profileDrawer = document.getElementById('profileDrawer');
    if (profileDrawer && e.target === profileDrawer) {
        closeProfileDrawer();
    }

    var insightsPanel = document.getElementById('insightsPanel');
    if (insightsPanel && e.target === insightsPanel) {
        closeInsightsPanel();
    }
});

document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape') {
        closeScheduleModal();
        closeProfileDrawer();
        closeInsightsPanel();
    }
});

/* =================================================
   INIT
================================================= */
function initApplicantsPage() {
    animateScoreBars();
}

window.addEventListener('load', function () {
    setTimeout(initApplicantsPage, 200);
});