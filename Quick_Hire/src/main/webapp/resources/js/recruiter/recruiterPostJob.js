/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/ClientSide/javascript.js to edit this template
 */


function handleValidationGlobalMessage(data) {
    if (data.status !== 'success') {
        return;
    }

    setTimeout(function () {

        var msgBox = document.getElementById('globalMessageBox');
        if (!msgBox) {
            return;
        }

        msgBox.style.display    = 'block';
        msgBox.style.opacity    = '1';
        msgBox.style.transition = '';

        var hasSuccess = msgBox.querySelector('.postjob-msg-success');
        var hasError   = msgBox.querySelector('.postjob-msg-error');
        var hasWarning = msgBox.querySelector('.postjob-msg-warning');

        var hasFieldErrors = false;
        var fieldErrors = document.querySelectorAll('.postjob-field-error');
        for (var i = 0; i < fieldErrors.length; i++) {
            if (fieldErrors[i].textContent.trim() !== '') {
                hasFieldErrors = true;
                break;
            }
        }

        if (!hasSuccess && !hasError && !hasWarning) {
            if (hasFieldErrors) {
                msgBox.style.display = 'none';
                for (var j = 0; j < fieldErrors.length; j++) {
                    if (fieldErrors[j].textContent.trim() !== '') {
                        fieldErrors[j].scrollIntoView({
                            behavior: 'smooth',
                            block: 'center'
                        });
                        break;
                    }
                }
            } else {
                msgBox.style.display = 'none';
            }
            syncPersistedSkillsToListbox();
            updateSelectedSkillUi();
            return;
        }

        msgBox.style.display = 'block';
        msgBox.style.opacity = '1';

        if (hasSuccess && !hasError && !hasWarning) {
            setTimeout(function () {
                msgBox.style.transition = 'opacity 0.5s ease';
                msgBox.style.opacity    = '0';
                setTimeout(function () {
                    msgBox.style.display    = 'none';
                    msgBox.style.opacity    = '1';
                    msgBox.style.transition = '';
                }, 500);
            }, 2500);
        }

        syncPersistedSkillsToListbox();
        updateSelectedSkillUi();

    }, 400);
}
function handleGlobalMessages() {
    var msgBox = document.getElementById('globalMessageBox');
    if (!msgBox) return;

    // textContent works even when element is display:none
    var text = msgBox.textContent || '';
    if (text.trim() === '') return;

    msgBox.style.display = 'block';
    msgBox.style.opacity = '1';

    setTimeout(function () {
        msgBox.style.transition = 'opacity 0.5s ease';
        msgBox.style.opacity = '0';

        setTimeout(function () {
            msgBox.style.display = 'none';
        }, 500);

    }, 2000);
}

  


function getHiddenSkillsListbox() {
        return document.getElementById('postJobForm:skills');
    }

function getSkillSearchInput() {
    return document.getElementById('skillSearchInput');
}

function getSkillSuggestionsBox() {
    return document.getElementById('skillSuggestionsBox');
}

function getSkillSuggestionsList() {
    return document.getElementById('skillSuggestionsList');
}

function getSelectedSkillChips() {
    return document.getElementById('selectedSkillChips');
}

function getSelectedSkillsEmptyState() {
    return document.getElementById('selectedSkillsEmptyState');
}

function getSkillCountBadge() {
    return document.getElementById('skillCountBadge');
}

function getSelectedSkillsSummaryText() {
    return document.getElementById('selectedSkillsSummaryText');
}

function getCategoryDropdown() {
    return document.getElementById('postJobForm:skillCategory');
}

function getModalOverlay() {
    return document.getElementById('skillModalOverlay');
}

function getSkillActionType() {
    return document.getElementById('skillActionType');
}

function getCategorySection() {
    return document.getElementById('categorySection');
}

function getSkillSection() {
    return document.getElementById('skillSection');
}

function getAllSkillsData() {
    var scriptTag = document.getElementById('allSkillsJson');
    if (!scriptTag) {
        return [];
    }

    try {
        return JSON.parse(scriptTag.textContent.trim());
    } catch (e) {
        console.error('Invalid allSkillsJson', e);
        return [];
    }
}

function getFilteredSkillsData() {
    var scriptTag = document.getElementById('filteredSkillsJson');
    if (!scriptTag) return [];

    try {
        var data = JSON.parse(scriptTag.textContent.trim());
        return (data && data.length) ? data : getAllSkillsData();
    } catch (e) {
        console.error('Invalid filteredSkillsJson', e);
        return getAllSkillsData(); // IMPORTANT fallback
    }
}
function extractSkillsFromListbox() {
    var listbox = getHiddenSkillsListbox();
    var skills = [];

    if (!listbox) {
        return skills;
    }

    for (var i = 0; i < listbox.options.length; i++) {
        skills.push({
            id: String(listbox.options[i].value),
            name: listbox.options[i].text
        });
    }

    return skills;
}

function getSelectedSkillIds() {
    var selected = [];

    if (window.persistedSelectedSkills && Array.isArray(window.persistedSelectedSkills)) {
        selected = window.persistedSelectedSkills.slice();
    }

    var listbox = getHiddenSkillsListbox();

    if (listbox) {
        for (var i = 0; i < listbox.options.length; i++) {
            if (listbox.options[i].selected) {
                var value = String(listbox.options[i].value);
                if (selected.indexOf(value) === -1) {
                    selected.push(value);
                }
            }
        }
    }

    return selected;
}

function setSelectedInListbox(skillId, shouldSelect) {
    var listbox = getHiddenSkillsListbox();

    if (!window.persistedSelectedSkills || !Array.isArray(window.persistedSelectedSkills)) {
        window.persistedSelectedSkills = [];
    }

    if (listbox) {
        for (var i = 0; i < listbox.options.length; i++) {
            if (String(listbox.options[i].value) === String(skillId)) {
                listbox.options[i].selected = shouldSelect;
                break;
            }
        }
    }

    if (shouldSelect) {
        if (window.persistedSelectedSkills.indexOf(String(skillId)) === -1) {
            window.persistedSelectedSkills.push(String(skillId));
        }
    } else {
        window.persistedSelectedSkills = window.persistedSelectedSkills.filter(function (id) {
            return id !== String(skillId);
        });
    }

    syncPersistedSkillsToListbox();
}

function syncPersistedSkillsToListbox() {
    var listbox = getHiddenSkillsListbox();

    if (!listbox) {
        return;
    }

    if (!window.persistedSelectedSkills || !Array.isArray(window.persistedSelectedSkills)) {
        window.persistedSelectedSkills = [];
    }

    for (var i = 0; i < listbox.options.length; i++) {
        listbox.options[i].selected =
                window.persistedSelectedSkills.indexOf(String(listbox.options[i].value)) !== -1;
    }
}

function updateSelectedSkillUi() {
    var allSkills = getAllSkillsData();
    var selectedIds = getSelectedSkillIds();
    var selectedChipBox = getSelectedSkillChips();
    var emptyState = getSelectedSkillsEmptyState();
    var countBadge = getSkillCountBadge();
    var summaryText = getSelectedSkillsSummaryText();

    if (!selectedChipBox) {
        return;
    }

    selectedChipBox.innerHTML = '';
    var selectedCount = 0;

    for (var i = 0; i < allSkills.length; i++) {
        if (selectedIds.indexOf(String(allSkills[i].id)) !== -1) {
            selectedCount++;

            var chip = document.createElement('span');
            chip.className = 'postjob-skill-tag postjob-skill-tag-selected';
            chip.innerHTML =
                    '<span>' + escapeHtml(allSkills[i].name) + '</span>' +
                    '<button type="button" class="postjob-skill-tag-remove" onclick="removeSkillTag(\'' + String(allSkills[i].id) + '\')">✕</button>';

            selectedChipBox.appendChild(chip);
        }
    }

    if (emptyState) {
        emptyState.style.display = selectedCount === 0 ? 'flex' : 'none';
    }

    if (countBadge) {
        countBadge.textContent = selectedCount + ' Selected';
    }

    if (summaryText) {
        summaryText.textContent = 'Total Skills Added: ' + selectedCount;
    }
}

// ================= SKILL PAGINATION =================
var skillSuggestionLimit = 8;
var currentSkillOffset = 0;
var currentMatchedSkills = [];

function renderSkillSuggestions(filterText) {


var suggestionList = getSkillSuggestionsList();

if (!suggestionList) {
    return;
}

// ================= LOAD SKILLS =================
var skills = [];

var filtered = getFilteredSkillsData();

if (filtered && filtered.length > 0) {
    skills = filtered;
}

// FALLBACK TO ALL SKILLS
if (skills.length === 0) {

    var allSkills = getAllSkillsData();

    if (allSkills && allSkills.length > 0) {
        skills = allSkills;
    }
}

if (skills.length === 0) {
    skills = extractSkillsFromListbox();
}

if (skills.length > 0) {

    window.lastKnownSkillsData = skills;

} else if (window.lastKnownSkillsData
        && window.lastKnownSkillsData.length > 0) {

    skills = window.lastKnownSkillsData;
}

// ================= SAFETY =================
if (!skills || skills.length === 0) {

    suggestionList.innerHTML =
            '<div class="postjob-no-suggestion">' +
            'No skills available.' +
            '</div>';

    return;
}

suggestionList.innerHTML = '';

var selectedIds = getSelectedSkillIds();

var query = (filterText || '').toLowerCase().trim();

currentMatchedSkills = [];

for (var i = 0; i < skills.length; i++) {

    var skillId = String(skills[i].id || '');
    var skillName = String(skills[i].name || '');

    var alreadySelected =
            selectedIds.indexOf(skillId) !== -1;

    var nameMatches =
            query === '' ||
            skillName.toLowerCase().indexOf(query) !== -1;

    if (!alreadySelected && nameMatches) {

        currentMatchedSkills.push({
            id: skillId,
            name: skillName
        });
    }
}

currentSkillOffset = 0;

renderMoreSkillSuggestions();

}
function renderMoreSkillSuggestions() {

    var suggestionList = getSkillSuggestionsList();

    if (!suggestionList) {
        return;
    }

    var oldLoadMore = document.getElementById('loadMoreSkillsBtn');

    if (oldLoadMore) {
        oldLoadMore.remove();
    }

    // ================= NEXT BATCH =================
    var nextSkills = currentMatchedSkills.slice(
            currentSkillOffset,
            currentSkillOffset + skillSuggestionLimit
            );

    // ================= NO SKILLS =================
    if (currentMatchedSkills.length === 0
            && currentSkillOffset === 0) {

        suggestionList.innerHTML =
                '<div class="postjob-no-suggestion">' +
                'No matching skills found.' +
                '</div>';

        return;
    }

    // ================= RENDER SKILLS =================
    for (var i = 0; i < nextSkills.length; i++) {

        var item = document.createElement('button');

        item.type = 'button';

        item.className = 'postjob-suggestion-item';

        item.innerHTML =
                escapeHtml(nextSkills[i].name);

        item.onclick = (function (skillId) {

            return function () {
                addSkillTag(skillId);
            };

        })(nextSkills[i].id);

        suggestionList.appendChild(item);
    }

    currentSkillOffset += nextSkills.length;

    // ================= LOAD MORE BUTTON =================
    if (currentSkillOffset < currentMatchedSkills.length) {

        var loadMoreBtn = document.createElement('button');

        loadMoreBtn.type = 'button';

        loadMoreBtn.id = 'loadMoreSkillsBtn';

        loadMoreBtn.className =
                'postjob-load-more-btn';

        loadMoreBtn.innerHTML =
                'Load More Skills';

        loadMoreBtn.onclick = function () {
            renderMoreSkillSuggestions();
        };

        suggestionList.appendChild(loadMoreBtn);
    }
}

function addSkillTag(skillId) {

    setSelectedInListbox(skillId, true);

    updateSelectedSkillUi();
    var wrapper =
            document.getElementById(
                    'postJobForm:skillMessageWrapper'
                    );

    if (wrapper) {

        var errorMsg =
                wrapper.querySelector('.postjob-msg-error');

        if (errorMsg) {

            wrapper.innerHTML = '';

            wrapper.style.display = 'none';

            wrapper.style.opacity = '1';
        }
    }

    // ALSO REMOVE FIELD ERROR
    var skillFieldError =
            document.getElementById(
                    'postJobForm:skillsMessage'
                    );

    if (skillFieldError) {

        skillFieldError.innerHTML = '';

        skillFieldError.style.display = 'none';
    }

    // RESET INPUT
    var input = getSkillSearchInput();

    if (input) {

        input.value = '';

    }

    // REFRESH SUGGESTIONS
    renderSkillSuggestions('');
}

function removeSkillTag(skillId) {
    setSelectedInListbox(skillId, false);
    updateSelectedSkillUi();

    var input = getSkillSearchInput();
    renderSkillSuggestions(input ? input.value : '');
}

function closeSkillModal() {

var modal = getModalOverlay();

if (modal) {
modal.style.display = 'none';
}

resetSkillModal();
}


function openSkillModal() {

resetSkillModal();

var modal = getModalOverlay();

if (modal) {
modal.style.display = 'flex';
}
}

function autoHideMessages(wrapperId) {

var wrapper = document.getElementById(wrapperId);

if (!wrapper) {
return;
}

var text = wrapper.innerText || wrapper.textContent || '';

if (text.trim() === '') {
return;
}

wrapper.style.display = 'block';
wrapper.style.opacity = '1';

setTimeout(function () {

wrapper.style.transition = 'opacity 0.5s ease';
wrapper.style.opacity = '0';

setTimeout(function () {

wrapper.style.display = 'none';
wrapper.style.opacity = '1';

}, 500);

}, 2000);
}

function resetSkillModal() {

var actionType = getSkillActionType();

if (actionType) {
actionType.value = '';
}

var categorySection = getCategorySection();
var skillSection = getSkillSection();

if (categorySection) {
categorySection.style.display = 'none';
}

if (skillSection) {
skillSection.style.display = 'none';
}

var categoryInput =
document.getElementById('postJobForm:modalNewCategoryName');

if (categoryInput) {
categoryInput.value = '';
}

var skillInput =
document.getElementById('postJobForm:modalNewSkillName');

if (skillInput) {
skillInput.value = '';
}

var skillCategory =
document.getElementById('postJobForm:modalSkillCategory');

if (skillCategory) {
skillCategory.selectedIndex = 0;
}

var categoryWrapper =
document.getElementById('postJobForm:categoryMessageWrapper');

if (categoryWrapper) {
categoryWrapper.innerHTML = '';
categoryWrapper.style.display = 'none';
}

var skillWrapper =
document.getElementById('postJobForm:skillMessageWrapper');

if (skillWrapper) {
skillWrapper.innerHTML = '';
skillWrapper.style.display = 'none';
}
}

function toggleSkillModalSections() {

var actionType = getSkillActionType();

var categorySection = getCategorySection();

var skillSection = getSkillSection();

var hiddenField =
document.getElementById('postJobForm:modalActionTypeHidden');

if (!categorySection || !skillSection) {
return;
}

categorySection.style.display = 'none';
skillSection.style.display = 'none';

if (!actionType) {
return;
}

if (hiddenField) {
hiddenField.value = actionType.value;
}

if (actionType.value === 'category') {

categorySection.style.display = 'block';

} else if (actionType.value === 'skill') {

skillSection.style.display = 'block';
}
}


function bindSkillSearchEvents() {
    var input = getSkillSearchInput();

    if (!input) {
        return;
    }

    input.onfocus = function () {
        renderSkillSuggestions(input.value);
    };

    input.oninput = function () {
        renderSkillSuggestions(input.value);
    };

    input.onkeydown = function (e) {
        if (e.key === 'Enter') {
            e.preventDefault();

            var firstSuggestion = document.querySelector('.postjob-suggestion-item');
            if (firstSuggestion) {
                firstSuggestion.click();
            }
        }
    };
}

function initSkillsAutocompleteUi() {

    if (!window.persistedSelectedSkills ||
            !Array.isArray(window.persistedSelectedSkills)) {

        window.persistedSelectedSkills = [];
    }

    // ================= EDIT MODE BOOT =================
    
    if (window.persistedSelectedSkills.length === 0) {
        var listbox = getHiddenSkillsListbox();
        if (listbox) {
            for (var i = 0; i < listbox.options.length; i++) {
                if (listbox.options[i].selected) {
                    window.persistedSelectedSkills.push(
                        String(listbox.options[i].value)
                    );
                }
            }
        }
    }

    syncPersistedSkillsToListbox();

    updateSelectedSkillUi();

    bindSkillSearchEvents();

    var suggestionList = getSkillSuggestionsList();

    if (suggestionList &&
            suggestionList.children.length === 0) {

        renderSkillSuggestions('');
    }
}
function handleSkillCategoryChange(data) {

    if (data.status === 'success') {
        setTimeout(function () {
            var input = getSkillSearchInput();
            currentSkillOffset = 0;
            renderSkillSuggestions(input ? input.value : '');
            updateSelectedSkillUi();
        }, 150);
    }
}

function handleCategoryCreatedAndSwitchToSkill(data) {

if (data.status === 'success') {

setTimeout(function () {

initSkillsAutocompleteUi();

var categoryWrapper =
    document.getElementById('postJobForm:categoryMessageWrapper');

var hasError = false;

if (categoryWrapper) {

var errorMsg =
        categoryWrapper.querySelector('.postjob-msg-error');

if (errorMsg) {
    hasError = true;
}
}


if (hasError) {

var dropdown = getSkillActionType();

if (dropdown) {

    dropdown.value = 'category';

    toggleSkillModalSections();
}

return;
}

// ================= SUCCESS CASE =================

if (categoryWrapper) {

categoryWrapper.style.display = 'block';

categoryWrapper.style.opacity = '1';
}

setTimeout(function () {

if (categoryWrapper) {

    categoryWrapper.style.transition =
            'opacity 0.5s ease';

    categoryWrapper.style.opacity = '0';
}

setTimeout(function () {

    if (categoryWrapper) {

        categoryWrapper.style.display = 'none';

        categoryWrapper.style.opacity = '1';
    }

    var dropdown = getSkillActionType();

    if (dropdown) {

        dropdown.value = 'skill';

        toggleSkillModalSections();
    }

}, 500);

}, 2000);

}, 100);
}
}

function handleSkillCreated(data) {

if (data.status === 'success') {

setTimeout(function () {

var modal = getModalOverlay();

if (modal) {
modal.style.display = 'flex';
}

var dropdown = getSkillActionType();

if (dropdown) {

dropdown.value = 'skill';

toggleSkillModalSections();
}

initSkillsAutocompleteUi();

var skillInput = document.getElementById('postJobForm:modalNewSkillName');

var wrapper = document.getElementById('postJobForm:skillMessageWrapper');

var hasError = false;

if (wrapper) {

var errorMsg =
        wrapper.querySelector('.postjob-msg-error');

if (errorMsg) {
    hasError = true;
}
}


if (hasError) {

if (wrapper) {

    wrapper.style.display = 'block';
    wrapper.style.opacity = '1';
}

return;
}


if (wrapper) {

wrapper.style.display = 'block';
wrapper.style.opacity = '1';

setTimeout(function () {

    wrapper.style.transition =
            'opacity 0.5s ease';

    wrapper.style.opacity = '0';

    setTimeout(function () {

        wrapper.innerHTML = '';
        wrapper.style.display = 'none';
        wrapper.style.opacity = '1';

    }, 500);

}, 2000);
}

}, 100);
}
}
function handleEducationSelectionChange(data) {
    if (data.status === 'success') {
        setTimeout(function () {
            var checkboxList = document.querySelectorAll('#postJobForm\\:educationSelect input[type="checkbox"]');
            var labels = document.querySelectorAll('#postJobForm\\:educationSelect label');

            checkboxList.forEach(function (cb, index) {
                if (labels[index]) {
                    if (cb.checked) {
                        labels[index].classList.add('selected');
                    } else {
                        labels[index].classList.remove('selected');
                    }
                }
            });

            var msg = document.getElementById('postJobForm:educationMessage');
            if (msg) {
                msg.style.display = msg.textContent.trim() ? 'block' : 'none';
            }
        }, 100);
    }
}
function escapeHtml(value) {
    if (value === null || value === undefined) {
        return '';
    }

    return String(value)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
}

function initializeSkillUiAfterAjax() {

    setTimeout(function () {

        initSkillsAutocompleteUi();
        toggleSkillModalSections();

    }, 300);
}
window.addEventListener('load', function () {

    setTimeout(function () {

        initializeSkillUiAfterAjax();

    }, 300);
});
if (window.jsf && jsf.ajax) {

    jsf.ajax.addOnEvent(function (data) {

        if (data.status === 'success') {

            setTimeout(function () {

                initializeSkillUiAfterAjax();

            }, 100);
        }
    });
}




