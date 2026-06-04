/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/ClientSide/javascript.js to edit this template
 */

/*
 * recruiterViewJob.js
 */

// =============================================
// CONSTANTS
// =============================================


var initialVisibleCount = 25;
var currentVisibleCount = initialVisibleCount;
var loadStep = 10;

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
    var allRows = getTableRows();
    var nonHidden = getNonHiddenItems(allRows);
    var countText = nonHidden.length + ' results';

    var topCount = document.getElementById('jobResultsCountTop');
    var mainCount = document.getElementById('jobResultsCount');

    if (topCount) {
        topCount.textContent = countText;
    }
    if (mainCount) {
        mainCount.textContent = countText;
    }
}

// =============================================
// EMPTY STATE
// =============================================

function toggleEmptyState() {
    var allRows = getTableRows();
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
    var btn = document.getElementById('loadMoreBtn');
    var wrap = document.getElementById('loadMoreWrap');

    if (!wrap || !btn) {
        return;
    }

    var allRows = getTableRows();
    var nonHiddenRows = getNonHiddenItems(allRows);
    var totalNonHidden = nonHiddenRows.length;
    var shouldShow = totalNonHidden > currentVisibleCount;

    wrap.style.display = shouldShow ? 'flex' : 'none';
    btn.style.display = shouldShow ? 'inline-flex' : 'none';
}

// =============================================
// APPLY VISIBILITY (pagination)
// =============================================

function applyInitialVisibility() {
    var tableRows = getTableRows();
    var mobileCards = getMobileCards();

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
    var search = document.getElementById('jobSearch');

    Array.prototype.forEach.call(selects, function (select) {
        select.classList.toggle('has-value', !!select.value);
    });

    if (search) {
        search.classList.toggle('has-value', !!search.value.trim());
    }
}

// =============================================
// DATE FILTER
// =============================================

function matchesDateFilter(postedDateText, dateValue) {
    if (!dateValue) {
        return true;
    }

    var today = new Date();
    today.setHours(0, 0, 0, 0);

    var parts = (postedDateText || '').trim().split(' ');
    if (parts.length !== 3) {
        return true;
    }

    var months = {
        'jan': 0, 'feb': 1, 'mar': 2, 'apr': 3, 'may': 4, 'jun': 5,
        'jul': 6, 'aug': 7, 'sep': 8, 'oct': 9, 'nov': 10, 'dec': 11
    };

    var day = parseInt(parts[0], 10);
    var month = months[parts[1].toLowerCase().substring(0, 3)];
    var year = parseInt(parts[2], 10);

    if (isNaN(day) || month === undefined || isNaN(year)) {
        return true;
    }

    var postedDate = new Date(year, month, day);
    postedDate.setHours(0, 0, 0, 0);

    var diffDays = Math.floor(
            (today.getTime() - postedDate.getTime()) / (1000 * 60 * 60 * 24)
            );

    var dv = normalizeText(dateValue);

    if (dv === 'today') {
        return diffDays === 0;
    }
    if (dv === 'last 7 days') {
        return diffDays >= 0 && diffDays <= 7;
    }
    if (dv === 'last 30 days') {
        return diffDays >= 0 && diffDays <= 30;
    }
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
    var workMode = normalizeText(document.getElementById('workModeFilter') ? document.getElementById('workModeFilter').value : '');
    var status = normalizeText(document.getElementById('statusFilter') ? document.getElementById('statusFilter').value : '');
    var state = normalizeText(document.getElementById('filterForm:stateFilter') ? document.getElementById('filterForm:stateFilter').value : '');
    var city = normalizeText(document.getElementById('filterForm:cityFilter') ? document.getElementById('filterForm:cityFilter').value : '');
    var jobType = normalizeText(document.getElementById('jobTypeFilter') ? document.getElementById('jobTypeFilter').value : '');
    var dateValue = normalizeText(document.getElementById('dateFilter') ? document.getElementById('dateFilter').value : '');
    var search = normalizeText(document.getElementById('jobSearch') ? document.getElementById('jobSearch').value : '');

    var tableRows = getTableRows();
    var mobileCards = getMobileCards();

    Array.prototype.forEach.call(tableRows, function (row, index) {
        var title = normalizeText(row.getAttribute('data-title'));
        var rowWorkMode = normalizeText(row.getAttribute('data-workmode'));
        var rowStatus = normalizeText(row.getAttribute('data-status'));
        var rowState = normalizeText(row.getAttribute('data-state'));
        var rowCity = normalizeText(row.getAttribute('data-city'));
        var rowJobType = normalizeText(row.getAttribute('data-jobtype'));
        var rowSkills = normalizeText(row.getAttribute('data-skills'));

        var workModeMatch =
                !workMode ||
                rowWorkMode.includes(workMode);

        var statusMatch =
                !status ||
                rowStatus.includes(status);

        var stateMatch =
                !state ||
                rowState.includes(state);

        var cityMatch =
                !city ||
                rowCity.includes(city);

        var jobTypeMatch =
                !jobType ||
                rowJobType.includes(jobType);

        var dateMatch =
                matchesDateFilter(
                        row.getAttribute('data-posted'),
                        dateValue
                        );

        var searchMatch =
                !search ||
                title.includes(search) ||
                rowCity.includes(search) ||
                rowState.includes(search) ||
                rowSkills.includes(search);

        var isMatch =
                workModeMatch &&
                statusMatch &&
                stateMatch &&
                cityMatch &&
                jobTypeMatch &&
                dateMatch &&
                searchMatch;

        var filterVal = isMatch ? 'visible' : 'hidden';
        row.setAttribute('data-filtered', filterVal);

        if (mobileCards[index]) {
            mobileCards[index].setAttribute('data-filtered', filterVal);
        }
    });

    currentVisibleCount = initialVisibleCount;
    applyInitialVisibility();
}

// =============================================
// CLEAR FILTERS
// =============================================

function clearFilters() {
    var ids = [
        'workModeFilter', 'statusFilter', 'filterForm:stateFilter',
        'filterForm:cityFilter', 'jobTypeFilter', 'dateFilter', 'jobSearch'
    ];

    ids.forEach(function (id) {
        var el = document.getElementById(id);
        if (!el) {
            return;
        }
        el.value = '';
        el.classList.remove('has-value');
    });

    Array.prototype.forEach.call(getTableRows(), function (row) {
        row.setAttribute('data-filtered', 'visible');
    });

    Array.prototype.forEach.call(getMobileCards(), function (card) {
        card.setAttribute('data-filtered', 'visible');
    });

    currentVisibleCount = initialVisibleCount;
    updateActiveState();
    applyInitialVisibility();
}

// =============================================
// MODAL HELPERS
// =============================================

function buildStatusBadge(statusText) {
    var status = normalizeText(statusText);
    var cls = 'viewjobs-pill viewjobs-pill-neutral';

    if (status === 'open') {
        cls = 'viewjobs-pill viewjobs-pill-open';
    }
    if (status === 'closed') {
        cls = 'viewjobs-pill viewjobs-pill-closed';
    }
    if (status === 'onhold') {
        cls = 'viewjobs-pill viewjobs-pill-hold';
    }

    return '<span class="' + cls + '">' + escapeHtml(statusText || '-') + '</span>';
}

function getItemsArray(raw) {
    return (raw || '')
            .split(',')
            .map(function (item) {
                return item.trim();
            })
            .filter(function (item) {
                return item.length > 0;
            });
}

function buildChipHtml(raw, emptyText, chipClass) {
    var items = getItemsArray(raw);
    var cls = chipClass || 'viewjobs-modal-skill';

    if (!items.length) {
        return '<span class="' + cls + '">' + escapeHtml(emptyText || 'Not added') + '</span>';
    }

    return items.map(function (item) {
        return '<span class="' + cls + '">' + escapeHtml(item) + '</span>';
    }).join('');
}

function formatCompensationFromData(type, min, max, period, fallbackDisplay) {
    if (fallbackDisplay && fallbackDisplay.trim()) {
        return fallbackDisplay;
    }

    var compType = (type || '').trim();
    var compMin = (min || '').toString().trim();
    var compMax = (max || '').toString().trim();
    var compPeriod = (period || '').toString().trim().toLowerCase();

    if (!compType && !compMin && !compMax) {
        return 'Compensation not specified.';
    }

    var range = '-';

    if (compMin && compMax) {
        range = '₹' + compMin + ' - ₹' + compMax;
    } else if (compMin) {
        range = '₹' + compMin;
    } else if (compMax) {
        range = 'Up to ₹' + compMax;
    }

    var prefix = compType ? compType + ': ' : '';
    var suffix = compPeriod ? ' ' + compPeriod : '';

    return prefix + range + suffix;
}

// =============================================
// MODAL OPEN / CLOSE
// =============================================

function openJobModal(sourceElement) {
    var modal = document.getElementById('jobDetailsModal');
    if (!modal || !sourceElement) {
        return;
    }

    var jobId = sourceElement.getAttribute('data-jobid') || '';
    var title = sourceElement.getAttribute('data-title') || 'Job Title';
    var city = sourceElement.getAttribute('data-city') || '';
    var state = sourceElement.getAttribute('data-state') || '';
    var workMode = sourceElement.getAttribute('data-workmode') || '-';
    var status = sourceElement.getAttribute('data-status') || '-';
    var jobType = sourceElement.getAttribute('data-jobtype') || '-';
    var posted = sourceElement.getAttribute('data-posted') || '-';
    var expiry = sourceElement.getAttribute('data-expiry') || '-';
    var vacancies = sourceElement.getAttribute('data-vacancies') || '-';
    var experience = sourceElement.getAttribute('data-experience') || '-';
    var skills = sourceElement.getAttribute('data-skills') || '';
    var education = sourceElement.getAttribute('data-education') || '';
    var description = sourceElement.getAttribute('data-description') || 'No description available.';
    var compensationType = sourceElement.getAttribute('data-compensation-type') || '';
    var compensationMin = sourceElement.getAttribute('data-compensation-min') || '';
    var compensationMax = sourceElement.getAttribute('data-compensation-max') || '';
    var compensationPeriod = sourceElement.getAttribute('data-compensation-period') || '';
    var compensationDisplay = sourceElement.getAttribute('data-compensation-display') || '';

    var applicationsTotal = sourceElement.getAttribute('data-applications-total') || '0';
    var applicationsApplied = sourceElement.getAttribute('data-applications-applied') || '0';
    var applicationsShortlisted = sourceElement.getAttribute('data-applications-shortlisted') || '0';
    var applicationsRejected = sourceElement.getAttribute('data-applications-rejected') || '0';
    var applicationsSelected = sourceElement.getAttribute('data-applications-selected') || '0';

    var locationText = 'Location not specified';
    if (city && state) {
        locationText = city + ', ' + state;
    } else if (city) {
        locationText = city;
    } else if (state) {
        locationText = state;
    }

    var compensationText = formatCompensationFromData(
            compensationType,
            compensationMin,
            compensationMax,
            compensationPeriod,
            compensationDisplay
            );

    var els = {
        title: document.getElementById('modalJobTitle'),
        location: document.getElementById('modalJobLocation'),
        locationText: document.getElementById('modalLocationText'),
        statusWrap: document.getElementById('modalJobStatusWrap'),
        workMode: document.getElementById('modalWorkMode'),
        jobType: document.getElementById('modalJobType'),
        vacancies: document.getElementById('modalVacancies'),
        experience: document.getElementById('modalExperience'),
        posted: document.getElementById('modalPostedDate'),
        expiry: document.getElementById('modalExpiryDate'),
        statusText: document.getElementById('modalStatusText'),
        skills: document.getElementById('modalSkills'),
        education: document.getElementById('modalEducation'),
        compensation: document.getElementById('modalCompensation'),
        desc: document.getElementById('modalDescription'),
        total: document.getElementById('modalApplicationsTotal'),
        applied: document.getElementById('modalApplicationsApplied'),
        shortlisted: document.getElementById('modalApplicationsShortlisted'),
        rejected: document.getElementById('modalApplicationsRejected'),
        selected: document.getElementById('modalApplicationsSelected'),
        editLink: document.getElementById('modalEditLink')

    };

    if (els.title) {
        els.title.textContent = title;
    }
//    if (els.location) {
//        els.location.textContent = locationText;
//    }
    if (els.locationText) {
        els.locationText.textContent = locationText;
    }
    if (els.statusWrap) {
        els.statusWrap.innerHTML = buildStatusBadge(status);
    }
    if (els.workMode) {
        els.workMode.textContent = workMode;
    }
    if (els.jobType) {
        els.jobType.textContent = jobType;
    }
    if (els.vacancies) {
        els.vacancies.textContent = vacancies;
    }
    if (els.experience) {
        els.experience.textContent = experience;
    }
    if (els.posted) {
        els.posted.textContent = posted;
    }
    if (els.expiry) {
        els.expiry.textContent = expiry;
    }
    if (els.statusText) {
        els.statusText.textContent = status;
    }
    if (els.skills) {
        els.skills.innerHTML = buildChipHtml(skills, 'No skills added', 'viewjobs-modal-skill');
    }
    if (els.education) {
        els.education.innerHTML = buildChipHtml(education, 'No education added', 'viewjobs-modal-education-chip');
    }
    if (els.compensation) {
        els.compensation.textContent = compensationText;
    }
    if (els.desc) {
        els.desc.textContent = description;
    }
    if (els.total) {
        els.total.textContent = applicationsTotal;
    }
    if (els.applied) {
        els.applied.textContent = applicationsApplied;
    }
    if (els.shortlisted) {
        els.shortlisted.textContent = applicationsShortlisted;
    }
    if (els.rejected) {
        els.rejected.textContent = applicationsRejected;
    }
    if (els.selected) {
        els.selected.textContent = applicationsSelected;
    }
    if (els.editLink && jobId) {
        els.editLink.href = 'recruiterPostJob.jsf?jobId=' + encodeURIComponent(jobId);
    }

    var modalBody = document.querySelector('.viewjobs-modal-body');

    if (modalBody) {
        modalBody.scrollTop = 0;
    }
    modal.classList.add('active');
    modal.setAttribute('aria-hidden', 'false');
    document.body.classList.add('modal-open');

}
function handleJobStatusChange(data) {

    if (data.status === 'success') {

        bindViewButtons();
        applyInitialVisibility();

        var wrapper = document.getElementById(
                'messageForm:jobStatusMessageWrapper'
                );

        if (!wrapper) {
            return;
        }

        var hasMessage =
                wrapper.innerText &&
                wrapper.innerText.trim().length > 0;

        if (!hasMessage) {
            return;
        }

        wrapper.style.display = 'block';
        wrapper.style.pointerEvents = 'auto';

        setTimeout(function () {

            wrapper.style.opacity = '1';
            wrapper.style.transform = 'translateX(0)';

        }, 10);

        setTimeout(function () {

            wrapper.style.opacity = '0';
            wrapper.style.transform = 'translateX(40px)';

            setTimeout(function () {

                wrapper.style.display = 'none';
                wrapper.style.pointerEvents = 'none';

            }, 500);

        }, 2500);
    }
}

function closeJobModal() {
    var modal = document.getElementById('jobDetailsModal');
    if (!modal) {
        return;
    }
    modal.classList.remove('active');
    modal.setAttribute('aria-hidden', 'true');

    document.body.classList.remove('modal-open');
}

// =============================================
// BIND VIEW BUTTONS
// =============================================

function bindViewButtons() {
    var viewButtons = document.querySelectorAll('.view-job-btn');

    Array.prototype.forEach.call(viewButtons, function (btn) {
        if (btn._viewHandler) {
            btn.removeEventListener('click', btn._viewHandler);
        }

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
// BIND MODAL EVENTS
// =============================================

function bindModalEvents() {
    var modal = document.getElementById('jobDetailsModal');
    var closeBtn = document.getElementById('jobModalCloseBtn');

    if (closeBtn && !closeBtn._closeBound) {
        closeBtn.addEventListener('click', closeJobModal);
        closeBtn._closeBound = true;
    }

    if (modal && !modal._overlayBound) {
        modal.addEventListener('click', function (e) {
            if (e.target === modal) {
                closeJobModal();
            }
        });
        modal._overlayBound = true;
    }

    if (!document._jobModalEscBound) {
        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape') {
                closeJobModal();
            }
        });
        document._jobModalEscBound = true;
    }
}




function csvValue(value) {
    value = value || '';
    value = value.toString().replace(/"/g, '""');
    return '"' + value + '"';
}

function exportJobList() {
    var rows = Array.prototype.slice.call(getTableRows());

    var exportRows = rows.filter(function (row) {
        return row.getAttribute('data-filtered') !== 'hidden';
    });

    if (!exportRows.length) {
        alert('No jobs available to export.');
        return;
    }

    var headers = [
        'Job ID',
        'Job Title',
        'Work Mode',
        'Status',
        'State',
        'City',
        'Job Type',
        'Posted Date',
        'Expiry Date',
        'Vacancies',
        'Experience',
        'Skills',
        'Education',
        'Compensation',
        'Applications Total',
        'Applied',
        'Shortlisted',
        'Rejected',
        'Selected'
    ];

    var csvRows = [];
    csvRows.push(headers.map(csvValue).join(','));

    exportRows.forEach(function (row) {
        var data = [
            row.getAttribute('data-jobid'),
            row.getAttribute('data-title'),
            row.getAttribute('data-workmode'),
            row.getAttribute('data-status'),
            row.getAttribute('data-state'),
            row.getAttribute('data-city'),
            row.getAttribute('data-jobtype'),
            row.getAttribute('data-posted'),
            row.getAttribute('data-expiry'),
            row.getAttribute('data-vacancies'),
            row.getAttribute('data-experience'),
            row.getAttribute('data-skills'),
            row.getAttribute('data-education'),
            row.getAttribute('data-compensation-display'),
            row.getAttribute('data-applications-total'),
            row.getAttribute('data-applications-applied'),
            row.getAttribute('data-applications-shortlisted'),
            row.getAttribute('data-applications-rejected'),
            row.getAttribute('data-applications-selected')
        ];

        csvRows.push(data.map(csvValue).join(','));
    });

    var csvContent = csvRows.join('\n');
    var blob = new Blob([csvContent], {
        type: 'text/csv;charset=utf-8;'
    });

    var link = document.createElement('a');
    var url = URL.createObjectURL(blob);

    link.href = url;
    link.download = 'recruiter_job_list.csv';
    document.body.appendChild(link);
    link.click();

    document.body.removeChild(link);
    URL.revokeObjectURL(url);
}




// =============================================
// INITIALISE
// =============================================

(function () {
    document.body.classList.remove('modal-open');

    var selects = document.querySelectorAll('.viewjobs-select-control');
    var search = document.getElementById('jobSearch');
    var applyBtn = document.getElementById('applyFiltersBtn');
    var clearBtn = document.getElementById('clearFiltersBtn');

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
        var allRows = getTableRows();
        var mobileCards = getMobileCards();

        Array.prototype.forEach.call(allRows, function (row) {
            row.setAttribute('data-filtered', 'visible');
        });

        Array.prototype.forEach.call(mobileCards, function (card) {
            card.setAttribute('data-filtered', 'visible');
        });

        updateActiveState();
        applyInitialVisibility();
        bindViewButtons();
        bindModalEvents();
    }, 150);

    window.loadMoreJobs = loadMoreJobs;
    window.closeJobModal = closeJobModal;
})();