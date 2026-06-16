/*
 * recruiterNotification.js
 * Splits recruiter notifications from recruiter activities.
 */

(function () {
    var initialVisibleCount = 50;
    var currentVisibleCount = initialVisibleCount;
    var loadStep = 50;
    var activeMode = 'notifications';
    var activeTab = 'all';
    var firstInitDone = false;

    function byId(id) {
        return document.getElementById(id);
    }

    function toArray(nodeList) {
        return Array.prototype.slice.call(nodeList || []);
    }

    function getItems() {
        return toArray(document.querySelectorAll('.notification-item'));
    }

    function normalizeText(value) {
        return (value || '').toString().trim().toLowerCase();
    }

    function getFieldValue(id) {
        var el = byId(id);
        return el ? el.value : '';
    }

    function setFieldValue(id, value) {
        var el = byId(id);
        if (el) {
            el.value = value || '';
        }
    }

    function setText(id, value) {
        var el = byId(id);
        if (el) {
            el.textContent = value;
        }
    }

    function getReadValue(item) {
        return normalizeText(item.getAttribute('data-isread')) === 'true';
    }

    function getMode(item) {
        return item.getAttribute('data-mode') || 'notifications';
    }

    function parseIsoDate(value) {
        if (!value) {
            return null;
        }

        var date = new Date(value);
        return isNaN(date.getTime()) ? null : date;
    }

    function getCreatedDate(item) {
        return parseIsoDate(item.getAttribute('data-creatediso'));
    }

    function startOfToday() {
        var today = new Date();
        today.setHours(0, 0, 0, 0);
        return today;
    }

    function matchesMode(item) {
        return getMode(item) === activeMode;
    }

    function matchesActiveTab(item) {
        var isRead = getReadValue(item);

        if (activeMode === 'activities') {
            return activeTab === 'all' || activeTab === 'read';
        }

        if (activeTab === 'unread') {
            return !isRead;
        }

        if (activeTab === 'read') {
            return isRead;
        }

        return true;
    }

    function matchesDateFilter(item, dateValue) {
        var created;
        var today;
        var fromDate;

        if (!dateValue) {
            return true;
        }

        created = getCreatedDate(item);
        if (!created) {
            return true;
        }

        today = startOfToday();
        fromDate = new Date(today.getTime());

        if (dateValue === 'today') {
            return created >= today;
        }

        if (dateValue === 'last7') {
            fromDate.setDate(today.getDate() - 6);
            return created >= fromDate;
        }

        if (dateValue === 'last30') {
            fromDate.setDate(today.getDate() - 29);
            return created >= fromDate;
        }

        if (dateValue === 'month') {
            fromDate = new Date(today.getFullYear(), today.getMonth(), 1);
            return created >= fromDate;
        }

        return true;
    }

    function getItemText(item, selector, attributeName) {
        var attr = item.getAttribute(attributeName);
        var el;

        if (attr) {
            return attr;
        }

        el = item.querySelector(selector);
        return el ? el.textContent : '';
    }

    function matchesFilters(item) {
        var typeValue = normalizeText(getFieldValue('typeFilter'));
        var statusValue = normalizeText(getFieldValue('statusFilter'));
        var dateValue = normalizeText(getFieldValue('dateFilter'));
        var searchValue = normalizeText(getFieldValue('notificationSearch'));
        var itemType = normalizeText(item.getAttribute('data-notificationtype'));
        var itemTitle = normalizeText(getItemText(item, '.notification-item-title', 'data-notificationtitle'));
        var itemMessage = normalizeText(getItemText(item, '.notification-item-message', 'data-notificationmessage'));
        var itemSender = normalizeText(getItemText(item, '.notification-item-sender', 'data-sender'));
        var isRead = getReadValue(item);

        if (!matchesMode(item) || !matchesActiveTab(item)) {
            return false;
        }

        if (typeValue && itemType !== typeValue) {
            return false;
        }

        if (statusValue === 'true' && !isRead) {
            return false;
        }

        if (statusValue === 'false' && isRead) {
            return false;
        }

        if (!matchesDateFilter(item, dateValue)) {
            return false;
        }

        if (searchValue) {
            return itemTitle.indexOf(searchValue) !== -1
                    || itemMessage.indexOf(searchValue) !== -1
                    || itemSender.indexOf(searchValue) !== -1
                    || itemType.indexOf(searchValue) !== -1;
        }

        return true;
    }

    function getFilteredItems() {
        return getItems().filter(matchesFilters);
    }

    function updateActiveState() {
        toArray(document.querySelectorAll('.notification-form-control')).forEach(function (field) {
            if (field.value && field.value.length > 0) {
                field.classList.add('has-value');
            } else {
                field.classList.remove('has-value');
            }
        });
    }

    function updateModeText() {
        if (activeMode === 'activities') {
            setText('notificationListHeading', 'Activity List');
            setText('notificationListSubheading', 'Actions and activity records created under your recruiter account.');
            return;
        }

        setText('notificationListHeading', 'Notification List');
        setText('notificationListSubheading', 'Messages sent to your recruiter account by others.');
    }

    function updateResultsCount(count) {
        setText('notificationResultsCount', count + (count === 1 ? ' result' : ' results'));
    }

    function updateStatCounters() {
        var items = getItems();
        var notificationItems = items.filter(function (item) {
            return getMode(item) === 'notifications';
        });
        var activityItems = items.filter(function (item) {
            return getMode(item) === 'activities';
        });
        var visibleModeItems = items.filter(function (item) {
            return getMode(item) === activeMode;
        });
        var unread = notificationItems.filter(function (item) {
            return !getReadValue(item);
        }).length;
        var read = notificationItems.length - unread;

        setText('totalNotifications', String(notificationItems.length));
        setText('unreadNotifications', String(unread));
        setText('readNotifications', String(read));
        setText('activityNotifications', String(activityItems.length));
        setText('modeNotificationsCount', String(notificationItems.length));
        setText('modeActivitiesCount', String(activityItems.length));

        if (activeMode === 'activities') {
            setText('tabAllCount', String(activityItems.length));
            setText('tabUnreadCount', '0');
            setText('tabReadCount', String(activityItems.length));
        } else {
            setText('tabAllCount', String(visibleModeItems.length));
            setText('tabUnreadCount', String(unread));
            setText('tabReadCount', String(read));
        }
    }

    function applyVisibility() {
        var items = getItems();
        var filtered = getFilteredItems();
        var shownCount = 0;

        items.forEach(function (item) {
            if (filtered.indexOf(item) === -1) {
                item.classList.add('notification-hidden');
                return;
            }

            if (shownCount < currentVisibleCount) {
                item.classList.remove('notification-hidden');
                shownCount++;
            } else {
                item.classList.add('notification-hidden');
            }
        });

        var emptyState = byId('notificationEmptyState');
        if (emptyState) {
            emptyState.style.display = filtered.length === 0 && items.length > 0 ? 'flex' : 'none';
        }

        var loadMoreWrap = byId('loadMoreWrap');
        if (loadMoreWrap) {
            loadMoreWrap.style.display = filtered.length > currentVisibleCount ? 'flex' : 'none';
        }

        updateModeText();
        updateResultsCount(filtered.length);
        updateStatCounters();
    }

    function setActiveMode(modeName) {
        activeMode = modeName || 'notifications';

        if (activeMode === 'activities' && activeTab === 'unread') {
            activeTab = 'all';
        }

        toArray(document.querySelectorAll('.notification-mode-btn')).forEach(function (btn) {
            btn.classList.toggle('notification-mode-btn-active', btn.getAttribute('data-mode') === activeMode);
        });

        var markAllBtn = byId('notificationActionForm:markAllReadBtn');
        if (markAllBtn) {
            markAllBtn.style.display = activeMode === 'notifications' ? '' : 'none';
        }

        var hideReadBtn = byId('clearReadBtn');
        if (hideReadBtn) {
            hideReadBtn.style.display = activeMode === 'notifications' ? '' : 'none';
        }

        var unreadTab = document.querySelector('.notification-tab-btn[data-tab="unread"]');
        if (unreadTab) {
            unreadTab.style.display = activeMode === 'notifications' ? '' : 'none';
        }
    }

    function setActiveTab(tabName) {
        activeTab = tabName || 'all';

        toArray(document.querySelectorAll('.notification-tab-btn')).forEach(function (btn) {
            btn.classList.toggle('notification-tab-btn-active', btn.getAttribute('data-tab') === activeTab);
        });
    }

    function switchMode(modeName) {
        currentVisibleCount = initialVisibleCount;
        setActiveMode(modeName);
        setActiveTab(activeTab);
        applyVisibility();
    }

    function switchTab(tabName) {
        currentVisibleCount = initialVisibleCount;
        setActiveTab(tabName);
        applyVisibility();
    }

    function applyFilters() {
        currentVisibleCount = initialVisibleCount;
        updateActiveState();
        applyVisibility();
    }

    function clearFilters() {
        setFieldValue('typeFilter', '');
        setFieldValue('statusFilter', '');
        setFieldValue('dateFilter', '');
        setFieldValue('notificationSearch', '');
        currentVisibleCount = initialVisibleCount;
        updateActiveState();
        setActiveTab('all');
        applyVisibility();
    }

    function hideReadNotifications() {
        activeMode = 'notifications';
        setFieldValue('statusFilter', 'false');
        currentVisibleCount = initialVisibleCount;
        updateActiveState();
        setActiveMode('notifications');
        setActiveTab('unread');
        applyVisibility();
    }

    function loadMoreNotifications() {
        currentVisibleCount += loadStep;
        applyVisibility();
    }

    function bindOnce(element, eventName, key, handler) {
        if (!element || element.getAttribute('data-' + key) === 'true') {
            return;
        }

        element.setAttribute('data-' + key, 'true');
        element.addEventListener(eventName, handler);
    }

    function bindEvents() {
        toArray(document.querySelectorAll('.notification-mode-btn')).forEach(function (btn) {
            bindOnce(btn, 'click', 'mode-bound', function (event) {
                event.preventDefault();
                switchMode(btn.getAttribute('data-mode'));
            });
        });

        toArray(document.querySelectorAll('.notification-tab-btn')).forEach(function (btn) {
            bindOnce(btn, 'click', 'tab-bound', function (event) {
                event.preventDefault();
                switchTab(btn.getAttribute('data-tab'));
            });
        });

        toArray(document.querySelectorAll('.notification-select-control')).forEach(function (select) {
            bindOnce(select, 'change', 'select-bound', applyFilters);
        });

        bindOnce(byId('notificationSearch'), 'input', 'search-bound', applyFilters);

        bindOnce(byId('notificationSearch'), 'keydown', 'search-key-bound', function (event) {
            if (event.key === 'Enter') {
                event.preventDefault();
                applyFilters();
            }
        });

        bindOnce(byId('clearFiltersBtn'), 'click', 'clear-bound', function (event) {
            event.preventDefault();
            clearFilters();
        });

        bindOnce(byId('clearReadBtn'), 'click', 'hide-read-bound', function (event) {
            event.preventDefault();
            hideReadNotifications();
        });

        bindOnce(byId('loadMoreBtn'), 'click', 'load-more-bound', function (event) {
            event.preventDefault();
            loadMoreNotifications();
        });
    }

    function resetBrowserRestoredFiltersOnce() {
        if (firstInitDone) {
            return;
        }

        setFieldValue('typeFilter', '');
        setFieldValue('statusFilter', '');
        setFieldValue('dateFilter', '');
        setFieldValue('notificationSearch', '');
        firstInitDone = true;
    }

    function init() {
        bindEvents();
        resetBrowserRestoredFiltersOnce();
        setActiveMode(activeMode);
        setActiveTab(activeTab);
        updateActiveState();
        applyVisibility();
    }

    function afterAjax(data) {
        if (!data || data.status === 'success') {
            window.setTimeout(function () {
                currentVisibleCount = initialVisibleCount;
                init();
            }, 0);
        }
    }

    window.recruiterNotificationAfterAjax = afterAjax;
    window.loadMoreNotifications = loadMoreNotifications;
    window.applyNotificationFilters = applyFilters;
    window.clearNotificationFilters = clearFilters;

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
