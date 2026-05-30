/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/ClientSide/javascript.js to edit this template
 */


// =============================================
// CONSTANTS
// =============================================

var currentVisibleCount = 25;
var loadStep            = 10;

// =============================================
// DOM HELPERS
// =============================================

function getTableRows() {
    return document.querySelectorAll('.viewjobs-table-row');
}

function getMobileCards() {
    return document.querySelectorAll('.viewjobs-mobile-card');
}

function getNonHiddenItems(items) {
    return Array.prototype.filter.call(items, function (item) {
        return item.getAttribute('data-filtered') !== 'hidden';
    });
}

function normalizeText(value) {
    return (value || '').toString().trim().toLowerCase();
}

function escapeHtml(text) {
    var div = document.createElement('div');
    div.textContent = text || '';
    return div.innerHTML;
}

// =============================================
// RESULTS COUNT
// =============================================

function updateResultsCount() {
    var allRows      = getTableRows();
    var nonHidden    = getNonHiddenItems(allRows);
    var countText    = nonHidden.length + ' results';

    var topCount  = document.getElementById('jobResultsCountTop');
    var mainCount = document.getElementById('jobResultsCount');

    if (topCount)  { topCount.textContent  = countText; }
    if (mainCount) { mainCount.textContent = countText; }
}

// =============================================
// EMPTY STATE
// =============================================

function toggleEmptyState() {
    var allRows   = getTableRows();
    var nonHidden = getNonHiddenItems(allRows);
    var emptyState = document.getElementById('viewJobsEmptyState');

    if (emptyState) {
        emptyState.style.display = nonHidden.length === 0 ? 'flex' : 'none';
    }
}

// =============================================
// LOAD MORE BUTTON
// =============================================

function toggleLoadMoreButton() {
    var btn  = document.getElementById('loadMoreBtn');
    var wrap = document.getElementById('loadMoreWrap');

    if (!wrap || !btn) {
        return;
    }

    var allRows      = getTableRows();

    var nonHiddenRows = getNonHiddenItems(allRows);
    var totalNonHidden = nonHiddenRows.length;

    var shouldShow = totalNonHidden > currentVisibleCount;

    wrap.style.display = shouldShow ? 'flex'        : 'none';
    btn.style.display  = shouldShow ? 'inline-flex' : 'none';
}

// =============================================
// APPLY VISIBILITY (pagination)
// =============================================

function applyInitialVisibility() {
    var tableRows   = getTableRows();
    var mobileCards = getMobileCards();

    // ---- TABLE ROWS ----
    var shownTableCount = 0;

    Array.prototype.forEach.call(tableRows, function (row) {

        if (row.getAttribute('data-filtered') === 'hidden') {
            row.style.display = 'none';
            return;
        }

        if (shownTableCount < currentVisibleCount) {
            row.style.display = 'table-row';
            shownTableCount++;
        } else {
            row.style.display = 'none';
        }
    });

    var shownMobileCount = 0;

    Array.prototype.forEach.call(mobileCards, function (card) {

        if (card.getAttribute('data-filtered') === 'hidden') {
            card.style.display = 'none';
            return;
        }

        if (shownMobileCount < currentVisibleCount) {
            card.style.display = '';
            shownMobileCount++;
        } else {
            card.style.display = 'none';
        }
    });

    updateResultsCount();
    toggleEmptyState();
    toggleLoadMoreButton();
}

// =============================================
// LOAD MORE ACTION
// =============================================

function loadMoreJobs() {
    currentVisibleCount += loadStep;
    applyInitialVisibility();
}

// =============================================
// ACTIVE STATE FOR FILTERS
// =============================================

function updateActiveState() {
    var selects = document.querySelectorAll('.viewjobs-select-control');
    var search  = document.getElementById('jobSearch');

    Array.prototype.forEach.call(selects, function (select) {
        select.classList.toggle('has-value', !!select.value);
    });

    if (search) {
        search.classList.toggle('has-value', !!search.value.trim());
    }
}

// =============================================
// DATE FILTER (placeholder — always true for now)
// =============================================

function matchesDateFilter(postedDateText, dateValue) {
    if (!dateValue) { return true; }

    var today = new Date();
    today.setHours(0, 0, 0, 0);

    // Try to parse the displayed date (format: "dd MMM yyyy" e.g. "29 May 2026")
    var parts = (postedDateText || '').trim().split(' ');

    if (parts.length !== 3) { return true; }

    var months = {
        'jan':0,'feb':1,'mar':2,'apr':3,'may':4,'jun':5,
        'jul':6,'aug':7,'sep':8,'oct':9,'nov':10,'dec':11
    };

    var day   = parseInt(parts[0], 10);
    var month = months[parts[1].toLowerCase().substring(0, 3)];
    var year  = parseInt(parts[2], 10);

    if (isNaN(day) || month === undefined || isNaN(year)) { return true; }

    var postedDate = new Date(year, month, day);
    postedDate.setHours(0, 0, 0, 0);

    var diffDays = Math.floor(
        (today.getTime() - postedDate.getTime()) / (1000 * 60 * 60 * 24)
    );

    var dv = normalizeText(dateValue);

    if (dv === 'today')        { return diffDays === 0; }
    if (dv === 'last 7 days')  { return diffDays >= 0 && diffDays <= 7; }
    if (dv === 'last 30 days') { return diffDays >= 0 && diffDays <= 30; }
    if (dv === 'this month') {
        return postedDate.getMonth() === today.getMonth() &&
               postedDate.getFullYear() === today.getFullYear();
    }

    return true;
}

// =============================================
// APPLY FILTERS
// =============================================

function applyFilters() {
    var workMode  = normalizeText(document.getElementById('workModeFilter')  ? document.getElementById('workModeFilter').value  : '');
    var status    = normalizeText(document.getElementById('statusFilter')    ? document.getElementById('statusFilter').value    : '');
    var state     = normalizeText(document.getElementById('stateFilter')     ? document.getElementById('stateFilter').value     : '');
    var city      = normalizeText(document.getElementById('cityFilter')      ? document.getElementById('cityFilter').value      : '');
    var jobType   = normalizeText(document.getElementById('jobTypeFilter')   ? document.getElementById('jobTypeFilter').value   : '');
    var dateValue = normalizeText(document.getElementById('dateFilter')      ? document.getElementById('dateFilter').value      : '');
    var search    = normalizeText(document.getElementById('jobSearch')       ? document.getElementById('jobSearch').value       : '');

    var tableRows   = getTableRows();
    var mobileCards = getMobileCards();

    Array.prototype.forEach.call(tableRows, function (row, index) {
        var title       = normalizeText(row.getAttribute('data-title'));
        var rowWorkMode = normalizeText(row.getAttribute('data-workmode'));
        var rowStatus   = normalizeText(row.getAttribute('data-status'));
        var rowState    = normalizeText(row.getAttribute('data-state'));
        var rowCity     = normalizeText(row.getAttribute('data-city'));
        var rowJobType  = normalizeText(row.getAttribute('data-jobtype'));
        var rowPosted   = normalizeText(row.getAttribute('data-posted'));
        var rowSkills   = normalizeText(row.getAttribute('data-skills'));

        var isMatch =
            (!workMode  || rowWorkMode === workMode)  &&
            (!status    || rowStatus   === status)    &&
            (!state     || rowState    === state)     &&
            (!city      || rowCity     === city)      &&
            (!jobType   || rowJobType  === jobType)   &&
            matchesDateFilter(row.getAttribute('data-posted'), dateValue) &&
            (
                !search ||
                title.indexOf(search)       !== -1 ||
                rowCity.indexOf(search)     !== -1 ||
                rowState.indexOf(search)    !== -1 ||
                rowSkills.indexOf(search)   !== -1
            );

        var filterVal = isMatch ? 'visible' : 'hidden';
        row.setAttribute('data-filtered', filterVal);

        if (mobileCards[index]) {
            mobileCards[index].setAttribute('data-filtered', filterVal);
        }
    });

    // Reset pagination and re-apply visibility
    currentVisibleCount = loadStep;
    applyInitialVisibility();
}

// =============================================
// CLEAR FILTERS
// =============================================

function clearFilters() {
    var ids = [
        'workModeFilter', 'statusFilter', 'stateFilter',
        'cityFilter', 'jobTypeFilter', 'dateFilter', 'jobSearch'
    ];

    ids.forEach(function (id) {
        var el = document.getElementById(id);
        if (!el) { return; }
        el.value = '';
        el.classList.remove('has-value');
    });

    Array.prototype.forEach.call(getTableRows(), function (row) {
        row.setAttribute('data-filtered', 'visible');
    });

    Array.prototype.forEach.call(getMobileCards(), function (card) {
        card.setAttribute('data-filtered', 'visible');
    });

    currentVisibleCount = loadStep;
    updateActiveState();
    applyInitialVisibility();
}

// =============================================
// MODAL — BUILD HELPERS
// =============================================

function buildStatusBadge(statusText) {
    var status = normalizeText(statusText);
    var cls    = 'viewjobs-pill viewjobs-pill-neutral';

    if (status === 'open')   { cls = 'viewjobs-pill viewjobs-pill-open';   }
    if (status === 'closed') { cls = 'viewjobs-pill viewjobs-pill-closed'; }
    if (status === 'onhold') { cls = 'viewjobs-pill viewjobs-pill-hold';   }

    return '<span class="' + cls + '">' + escapeHtml(statusText || '-') + '</span>';
}

function getSkillsArray(skillsRaw) {
    return (skillsRaw || '')
        .split(',')
        .map(function (item) { return item.trim(); })
        .filter(function (item) { return item.length > 0; });
}

function buildSkillsHtml(skillsRaw) {
    var skills = getSkillsArray(skillsRaw);

    if (!skills.length) {
        return '<span class="viewjobs-modal-skill">No skills added</span>';
    }

    return skills.map(function (skill) {
        return '<span class="viewjobs-modal-skill">' + escapeHtml(skill) + '</span>';
    }).join('');
}

// =============================================
// MODAL — OPEN / CLOSE
// =============================================

function openJobModal(sourceElement) {
    var modal = document.getElementById('jobDetailsModal');
    if (!modal || !sourceElement) { return; }

    var title       = sourceElement.getAttribute('data-title')       || 'Job Title';
    var city        = sourceElement.getAttribute('data-city')        || '';
    var state       = sourceElement.getAttribute('data-state')       || '';
    var workMode    = sourceElement.getAttribute('data-workmode')    || '-';
    var status      = sourceElement.getAttribute('data-status')      || '-';
    var jobType     = sourceElement.getAttribute('data-jobtype')     || '-';
    var posted      = sourceElement.getAttribute('data-posted')      || '-';
    var expiry      = sourceElement.getAttribute('data-expiry')      || '-';
    var vacancies   = sourceElement.getAttribute('data-vacancies')   || '-';
    var skills      = sourceElement.getAttribute('data-skills')      || '';
    var description = sourceElement.getAttribute('data-description') || 'No description available.';

    var locationText = 'Location not specified';
    if (city && state)  { locationText = city + ', ' + state; }
    else if (city)      { locationText = city; }
    else if (state)     { locationText = state; }

    var els = {
        title:      document.getElementById('modalJobTitle'),
        location:   document.getElementById('modalJobLocation'),
        statusWrap: document.getElementById('modalJobStatusWrap'),
        workMode:   document.getElementById('modalWorkMode'),
        jobType:    document.getElementById('modalJobType'),
        vacancies:  document.getElementById('modalVacancies'),
        posted:     document.getElementById('modalPostedDate'),
        expiry:     document.getElementById('modalExpiryDate'),
        statusText: document.getElementById('modalStatusText'),
        skills:     document.getElementById('modalSkills'),
        desc:       document.getElementById('modalDescription')
    };

    if (els.title)      { els.title.textContent      = title; }
    if (els.location)   { els.location.textContent   = locationText; }
    if (els.statusWrap) { els.statusWrap.innerHTML   = buildStatusBadge(status); }
    if (els.workMode)   { els.workMode.textContent   = workMode; }
    if (els.jobType)    { els.jobType.textContent     = jobType; }
    if (els.vacancies)  { els.vacancies.textContent  = vacancies; }
    if (els.posted)     { els.posted.textContent     = posted; }
    if (els.expiry)     { els.expiry.textContent     = expiry; }
    if (els.statusText) { els.statusText.textContent = status; }
    if (els.skills)     { els.skills.innerHTML       = buildSkillsHtml(skills); }
    if (els.desc)       { els.desc.textContent       = description; }

    modal.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeJobModal() {
    var modal = document.getElementById('jobDetailsModal');
    if (!modal) { return; }
    modal.classList.remove('active');
    document.body.style.overflow = '';
}

// =============================================
// BIND VIEW BUTTONS
// =============================================

function bindViewButtons() {
    var viewButtons = document.querySelectorAll('.view-job-btn');

    Array.prototype.forEach.call(viewButtons, function (btn) {
        // Remove old listener to avoid duplicates
        btn.removeEventListener('click', btn._viewHandler);

        btn._viewHandler = function (e) {
            e.preventDefault();
            var source =
                btn.closest('.viewjobs-table-row') ||
                btn.closest('.viewjobs-mobile-card');
            openJobModal(source);
        };

        btn.addEventListener('click', btn._viewHandler);
    });
}

// =============================================
// BIND MODAL CLOSE EVENTS
// =============================================

function bindModalEvents() {
    var modal    = document.getElementById('jobDetailsModal');
    var closeBtn = document.getElementById('jobModalCloseBtn');

    if (closeBtn) {
        closeBtn.addEventListener('click', closeJobModal);
    }

    if (modal) {
        modal.addEventListener('click', function (e) {
            if (e.target === modal) { closeJobModal(); }
        });
    }

    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') { closeJobModal(); }
    });
}

// =============================================
// INITIALISE
// =============================================

(function () {

    var selects   = document.querySelectorAll('.viewjobs-select-control');
    var search    = document.getElementById('jobSearch');
    var applyBtn  = document.getElementById('applyFiltersBtn');
    var clearBtn  = document.getElementById('clearFiltersBtn');

    // Filter select change
    Array.prototype.forEach.call(selects, function (select) {
        select.addEventListener('change', updateActiveState);
    });

    if (search) {
        search.addEventListener('input', updateActiveState);
        search.addEventListener('keydown', function (e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                applyFilters();
            }
        });
    }

    if (applyBtn) {
        applyBtn.addEventListener('click', function (e) {
            e.preventDefault();
            applyFilters();
        });
    }

    if (clearBtn) {
        clearBtn.addEventListener('click', function (e) {
            e.preventDefault();
            clearFilters();
        });
    }

    setTimeout(function () {
        var allRows   = getTableRows();


        Array.prototype.forEach.call(allRows, function (row) {
            row.setAttribute('data-filtered', 'visible');
        });

        updateActiveState();
        applyInitialVisibility();
        bindViewButtons();
        bindModalEvents();

    }, 150);

    window.loadMoreJobs = loadMoreJobs;

})();