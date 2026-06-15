/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package CDIBean;

import Client.RecruiterJerseyClient;
import Entity.Tblapplication;
import Entity.Tblinterview;
import Entity.Tblrecruiters;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.core.Response;
import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author RINKAL
 */
@Named(value = "recruiterApplicationInterviewCDIBean")
@ViewScoped
public class RecruiterApplicationInterviewCDIBean implements Serializable {

    private Tblrecruiters recruiter = new Tblrecruiters();

    private List<Tblapplication> allRecruiterApplicationList = new ArrayList<>();
    private List<Tblapplication> recruiterApplicationList = new ArrayList<>();
    private List<Tblinterview> selectedInterviewHistory = new ArrayList<>();

    private final RecruiterJerseyClient client = new RecruiterJerseyClient();

    private Map<Integer, BigDecimal> applicationScores = new HashMap<>();
    private Integer selectedApplicationId;
    private Integer lastChangedApplicationId;
    private String applicantSearchText;
    private String applicantJobFilter = "All Jobs";
    private String applicantStatusFilter = "All Status";
    private String applicantSortFilter = "Newest First";

    private Tblinterview interview = new Tblinterview();
    private String interviewDateTime;
    
    @Inject
    LoginCDIBean loginBean;

    // ================= INTERVIEW MANAGEMENT =================
    private List<Tblinterview> recruiterInterviewList = new ArrayList<>();

    private String scheduledInterviewCount = "0";
    private String completedInterviewCount = "0";
    private String selectedCount = "0";
    private String interviewRejectedCount = "0";   // ← renamed
    private String totalInterviewCount = "0";

// Conduct Interview Fields
    private Integer selectedInterviewId;
    private String feedback;
    private String result;

// Reschedule Fields
    private Tblinterview selectedInterview;
    private String rescheduleDateTime;

    public RecruiterApplicationInterviewCDIBean() {
    }

    @PostConstruct
    public void init() {
        getRecruiterApplications();

        loadInterviews();

    }

    public void getRecruiterApplications() {
        try {
            client.setToken(loginBean.getToken());

            recruiter = client.getProfile(
                    Tblrecruiters.class,
                    String.valueOf(loginBean.getUserId())
            );

            if (recruiter == null || recruiter.getRecruiterId() == null) {
                recruiterApplicationList = new ArrayList<>();
                return;
            }

            Collection<Tblapplication> applications
                    = client.getRecruiterApplications(recruiter.getRecruiterId());

            recruiterApplicationList = applications != null
                    ? new ArrayList<>(applications)
                    : new ArrayList<>();
            allRecruiterApplicationList = new ArrayList<>(recruiterApplicationList);

            loadAllScores();

            autoScreenAllOnLoad();

        } catch (Exception e) {
            e.printStackTrace();
            allRecruiterApplicationList = new ArrayList<>();
            recruiterApplicationList = new ArrayList<>();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", "Unable to load applications."));
        }
    }

    public String formatApplicationDate(Date date) {

        if (date == null) {
            return "";
        }

        return new SimpleDateFormat("dd MMM yyyy").format(date);
    }

    public String formatExperience(Integer months) {

        if (months == null || months <= 0) {
            return "Fresher";
        }

        int years = months / 12;
        int remainingMonths = months % 12;

        if (years > 0 && remainingMonths > 0) {
            return years + " yrs " + remainingMonths + " months";
        }

        if (years > 0) {
            return years + " yrs";
        }

        return remainingMonths + " months";
    }

    public String getCandidateInitial(Tblapplication application) {

        try {
            String name = application.getCandidateId()
                    .getUserId()
                    .getUserName();

            if (name != null && !name.trim().isEmpty()) {
                return name.substring(0, 1).toUpperCase();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "?";
    }

    public String getStatusClass(String status) {

        if (status == null) {
            return "status-applied";
        }

        switch (status.toLowerCase()) {

            case "screened":
                return "status-screened";

            case "shortlisted":
                return "status-shortlisted";

            case "rejected":
                return "status-rejected";

            case "selected":
                return "status-selected";

            default:
                return "status-applied";
        }
    }

    public void loadAllScores() {
        try {
            if (recruiter == null || recruiter.getRecruiterId() == null) {
                return;
            }

            // ONE single REST call instead of one per application
            Map<Integer, BigDecimal> scores = client.getAllScreeningScores(
                    recruiter.getRecruiterId()
            );

            applicationScores = scores != null ? scores : new HashMap<>();

        } catch (Exception e) {
            e.printStackTrace();
            applicationScores = new HashMap<>();
        }
    }

    //// Helper used in XHTML — returns the score for a given application
    public BigDecimal getScoreForApplication(Integer applicationId) {
        return applicationScores.getOrDefault(applicationId, null);
    }

    // ================= AUTO SCREEN ON PAGE LOAD =================
    public void autoScreenAllOnLoad() {
        try {
            client.setToken(loginBean.getToken());

            List<Integer> unscoredIds = new ArrayList<>();
            for (Tblapplication app : recruiterApplicationList) {
                if (!applicationScores.containsKey(app.getApplicationId())) {
                    unscoredIds.add(app.getApplicationId());
                }
            }

            if (unscoredIds.isEmpty()) {
                System.out.println("All applications already scored.");
                return;
            }

            System.out.println("Auto-screening " + unscoredIds.size() + " unscored applications");

            for (Integer appId : unscoredIds) {
                try {
                    Response res = client.generateScreeningScore(appId);
                    if (res.getStatus() != 200) {
                        System.err.println("Score generation failed for appId: " + appId);
                    }
                } catch (Exception ex) {
                    System.err.println("Score failed for appId " + appId + ": " + ex.getMessage());
                }
            }

            // ONE bulk call to reload all scores after generation
            loadAllScores();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

// Returns score as int string like "78" or "--" if not yet screened
    public String getScoreDisplay(Integer applicationId) {
        BigDecimal score = applicationScores.get(applicationId);
        if (score == null) {
            return "--";
        }
        return String.valueOf(score.intValue());
    }

// Returns CSS class for score pill
    public String getScorePillClass(Integer applicationId) {
        BigDecimal score = applicationScores.get(applicationId);
        if (score == null) {
            return "score-mid";
        }
        int s = score.intValue();
        if (s >= 70) {
            return "score-high";
        }
        if (s >= 40) {
            return "score-mid";
        }
        return "score-low";
    }

// Returns width% string for the score bar fill
    public String getScoreBarWidth(Integer applicationId) {
        BigDecimal score = applicationScores.get(applicationId);
        if (score == null) {
            return "0%";
        }
        return score.intValue() + "%";
    }

    public int getTotalApplicationsCount() {
        return allRecruiterApplicationList != null && !allRecruiterApplicationList.isEmpty()
                ? allRecruiterApplicationList.size()
                : recruiterApplicationList.size();
    }

    public int gettotalApplicationsCount() {
        return getTotalApplicationsCount();
    }

    public long getNewTodayCount() {
        long count = 0;
        java.util.Calendar today = java.util.Calendar.getInstance();
        List<Tblapplication> source = allRecruiterApplicationList != null && !allRecruiterApplicationList.isEmpty()
                ? allRecruiterApplicationList
                : recruiterApplicationList;
        for (Tblapplication app : source) {
            if (app.getApplicationAppliedDate() != null) {
                java.util.Calendar appDate = java.util.Calendar.getInstance();
                appDate.setTime(app.getApplicationAppliedDate());
                if (today.get(java.util.Calendar.YEAR) == appDate.get(java.util.Calendar.YEAR)
                        && today.get(java.util.Calendar.DAY_OF_YEAR) == appDate.get(java.util.Calendar.DAY_OF_YEAR)) {
                    count++;
                }
            }
        }
        return count;
    }

    public long getShortlistedCount() {
        try {
            if (recruiter == null || recruiter.getRecruiterId() == null) {
                return 0;
            }
            String count = client.getShortlisted(
                    String.valueOf(recruiter.getRecruiterId()));
            return Long.parseLong(count);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getRejectedCount() {
        try {
            if (recruiter == null || recruiter.getRecruiterId() == null) {
                return 0;
            } else {
                String count = client.getRejectedApplicationCount(
                        String.valueOf(recruiter.getRecruiterId())
                );
                return Long.parseLong(count);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void shortlistApplication(Integer applicationId) {
        System.out.println("SHORTLIST METHOD CALLED");
        System.out.println("Application Id = " + applicationId);
        try {

            client.setToken(loginBean.getToken());

            Response response
                    = client.shortlistApplication(
                            applicationId
                    );

            if (response.getStatus() == 200) {
                lastChangedApplicationId = applicationId;
                updateLocalApplicationStatus(applicationId, "Shortlisted");
            } else {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Error",
                                response.readEntity(String.class)
                        )
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "Unable to shortlist applicant."
                    )
            );
        }
    }

    public boolean isScheduleInterviewDone(Tblapplication app) {
        if (app == null) {
            return false;
        }
        String s = app.getApplicationStatus();
        return "Interview Scheduled".equalsIgnoreCase(s)
                || "Selected".equalsIgnoreCase(s);
    }

    public void rejectApplication(Integer applicationId) {
        try {
            client.setToken(loginBean.getToken());

            Response response = client.rejectApplication(applicationId);

            if (response.getStatus() == 200) {
                lastChangedApplicationId = applicationId;
                updateLocalApplicationStatus(applicationId, "Rejected");
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Error", response.readEntity(String.class)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", "Unable to reject application."));
        }
    }

    public void prepareInterview(Integer applicationId) {
        if (applicationId == null) {
            return;
        }
        selectedApplicationId = applicationId;
        interview = new Tblinterview();
        interview.setInterviewStatus("Scheduled");
        interviewDateTime = null;
    }

    public void scheduleInterview() {

        try {

            if (selectedApplicationId == null
                    || selectedApplicationId <= 0) {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Error",
                                "Please select an application."
                        )
                );

                return;
            }

            Tblapplication application
                    = new Tblapplication();

            application.setApplicationId(
                    selectedApplicationId
            );

            interview.setApplicationId(
                    application
            );
            interview.setInterviewStatus("Scheduled");

            if (interviewDateTime != null
                    && !interviewDateTime.trim().isEmpty()) {

                interview.setInterviewDate(
                        new SimpleDateFormat(
                                "yyyy-MM-dd'T'HH:mm"
                        ).parse(interviewDateTime)
                );
            }
            interview.setInterviewStatus("Scheduled");

            client.setToken(
                    loginBean.getToken()
            );

            Response response
                    = client.scheduleInterview(
                            interview
                    );

            if (response.getStatus() == 200) {
                lastChangedApplicationId = selectedApplicationId;
                updateLocalApplicationStatus(selectedApplicationId, "Interview Scheduled");
                interview = new Tblinterview();
                interviewDateTime = null;
                loadInterviews();

            } else {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Error",
                                response.readEntity(String.class)
                        )
                );
            }

        } catch (ParseException e) {

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "Please select a valid interview date and time."
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "Unable to schedule interview."
                    )
            );
        }
    }

    public void prepareViewInterviewHistory(Integer applicationId) {
        try {
            client.setToken(loginBean.getToken());

            Collection<Tblinterview> history
                    = client.getInterviewHistoryByApplication(applicationId);

            selectedInterviewHistory = history != null
                    ? new ArrayList<>(history)
                    : new ArrayList<>();

        } catch (Exception e) {
            e.printStackTrace();
            selectedInterviewHistory = new ArrayList<>();
            addError("Unable to load interview history.");
        }
    }

    public void loadInterviews() {

        try {

            client.setToken(loginBean.getToken());

            recruiter = client.getProfile(
                    Tblrecruiters.class,
                    String.valueOf(loginBean.getUserId())
            );

            if (recruiter == null
                    || recruiter.getRecruiterId() == null) {

                recruiterInterviewList = new ArrayList<>();
                return;
            }

            Collection<Tblinterview> interviews
                    = client.getRecruiterInterviews(
                            recruiter.getRecruiterId()
                    );

            recruiterInterviewList
                    = interviews != null
                            ? new ArrayList<>(interviews)
                            : new ArrayList<>();

            loadInterviewCounts();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void loadInterviewCounts() {

        try {

            Integer recruiterId
                    = recruiter.getRecruiterId();

            scheduledInterviewCount
                    = client.getScheduledInterviewCount(
                            recruiterId
                    );

            completedInterviewCount
                    = client.getCompletedInterviewCount(
                            recruiterId
                    );

            selectedCount
                    = client.getSelectedCount(
                            recruiterId
                    );

            interviewRejectedCount = client.getRejectedCount(recruiterId);

            totalInterviewCount
                    = client.getTotalInterviewCount(
                            recruiterId
                    );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

//    public void conductInterview() {
//
//        try {
//
//            client.setToken(
//                    loginBean.getToken()
//            );
//
//            Response response
//                    = client.conductInterview(
//                            selectedInterviewId,
//                            feedback,
//                            result
//                    );
//
//            if (response.getStatus() == 200) {
//
//                FacesContext.getCurrentInstance()
//                        .addMessage(
//                                null,
//                                new FacesMessage(
//                                        FacesMessage.SEVERITY_INFO,
//                                        "Success",
//                                        "Interview completed."
//                                )
//                        );
//
//                loadInterviews();
//
//            } else {
//
//                FacesContext.getCurrentInstance()
//                        .addMessage(
//                                null,
//                                new FacesMessage(
//                                        FacesMessage.SEVERITY_ERROR,
//                                        "Error",
//                                        response.readEntity(String.class)
//                                )
//                        );
//            }
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }
//    }
//    public void prepareReschedule(
//            Tblinterview interview) {
//
//        selectedInterview = interview;
//
//        if (interview.getInterviewDate() != null) {
//
//            rescheduleDateTime
//                    = new SimpleDateFormat(
//                            "yyyy-MM-dd'T'HH:mm"
//                    ).format(
//                            interview.getInterviewDate()
//                    );
//        }
//    }
//    public void rescheduleInterview() {
//
//        try {
//
//            client.setToken(
//                    loginBean.getToken()
//            );
//
//            Response response
//                    = client.rescheduleInterview(
//                            selectedInterview.getInterviewId(),
//                            selectedInterview.getInterviewerName(),
//                            selectedInterview.getInterviewerMode(),
//                            rescheduleDateTime
//                    );
//
//            if (response.getStatus() == 200) {
//
//                FacesContext.getCurrentInstance()
//                        .addMessage(
//                                null,
//                                new FacesMessage(
//                                        FacesMessage.SEVERITY_INFO,
//                                        "Success",
//                                        "Interview rescheduled."
//                                )
//                        );
//
//                loadInterviews();
//
//            } else {
//
//                FacesContext.getCurrentInstance()
//                        .addMessage(
//                                null,
//                                new FacesMessage(
//                                        FacesMessage.SEVERITY_ERROR,
//                                        "Error",
//                                        response.readEntity(String.class)
//                                )
//                        );
//            }
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }
//    }
    private void refreshApplicationListOnly() {
        try {
            Collection<Tblapplication> applications
                    = client.getRecruiterApplications(recruiter.getRecruiterId());
            recruiterApplicationList = applications != null
                    ? new ArrayList<>(applications)
                    : new ArrayList<>();
            allRecruiterApplicationList = new ArrayList<>(recruiterApplicationList);

            loadAllScores();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLocalApplicationStatus(Integer applicationId, String status) {
        if (applicationId == null || status == null || recruiterApplicationList == null) {
            return;
        }
        updateStatusInList(recruiterApplicationList, applicationId, status);
        updateStatusInList(allRecruiterApplicationList, applicationId, status);
    }

    private void updateStatusInList(List<Tblapplication> list, Integer applicationId, String status) {
        if (list == null) {
            return;
        }
        for (Tblapplication app : list) {
            if (app != null
                    && app.getApplicationId() != null
                    && app.getApplicationId().equals(applicationId)) {
                app.setApplicationStatus(status);
                app.setLastUpdatedDate(new Date());
            }
        }
    }

    public void applyApplicantFilters() {
        reloadAllApplicationsFromDatabaseForFiltering();

        List<Tblapplication> source = allRecruiterApplicationList != null
                ? allRecruiterApplicationList
                : new ArrayList<>();

        String search = safeLower(applicantSearchText);
        String job = applicantJobFilter != null ? applicantJobFilter.trim() : "All Jobs";
        String status = applicantStatusFilter != null ? applicantStatusFilter.trim() : "All Status";

        List<Tblapplication> filtered = new ArrayList<>();
        for (Tblapplication app : source) {
            if (matchesApplicantSearch(app, search)
                    && matchesApplicantJob(app, job)
                    && matchesApplicantStatus(app, status)) {
                filtered.add(app);
            }
        }

        sortApplicantList(filtered);
        recruiterApplicationList = filtered;
    }

    public void resetApplicantFilters() {
        reloadAllApplicationsFromDatabaseForFiltering();

        applicantSearchText = null;
        applicantJobFilter = "All Jobs";
        applicantStatusFilter = "All Status";
        applicantSortFilter = "Newest First";
        recruiterApplicationList = allRecruiterApplicationList != null
                ? new ArrayList<>(allRecruiterApplicationList)
                : new ArrayList<>();
        sortApplicantList(recruiterApplicationList);
    }

    private void reloadAllApplicationsFromDatabaseForFiltering() {
        try {
            if (recruiter == null || recruiter.getRecruiterId() == null) {
                return;
            }
            client.setToken(loginBean.getToken());
            Collection<Tblapplication> applications = client.getRecruiterApplications(recruiter.getRecruiterId());
            allRecruiterApplicationList = applications != null
                    ? new ArrayList<>(applications)
                    : new ArrayList<>();
            loadAllScores();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean matchesApplicantSearch(Tblapplication app, String search) {
        if (search == null || search.isEmpty()) {
            return true;
        }
        return safeLower(getCandidateName(app)).contains(search)
                || safeLower(getCandidateEmail(app)).contains(search)
                || safeLower(getJobTitle(app)).contains(search);
    }

    private boolean matchesApplicantJob(Tblapplication app, String selectedJob) {
        if (selectedJob == null || selectedJob.isEmpty() || "All Jobs".equalsIgnoreCase(selectedJob)) {
            return true;
        }
        return selectedJob.equalsIgnoreCase(getJobTitle(app));
    }

    private boolean matchesApplicantStatus(Tblapplication app, String selectedStatus) {
        if (selectedStatus == null || selectedStatus.isEmpty() || "All Status".equalsIgnoreCase(selectedStatus)) {
            return true;
        }
        String actual = normalizeStatus(app != null ? app.getApplicationStatus() : null);
        return selectedStatus.equalsIgnoreCase(actual)
                || ("Rejected Application".equalsIgnoreCase(selectedStatus) && "Rejected".equalsIgnoreCase(actual));
    }

    private void sortApplicantList(List<Tblapplication> list) {
        if (list == null) {
            return;
        }
        String sort = applicantSortFilter != null ? applicantSortFilter.trim() : "Newest First";
        Comparator<Tblapplication> comparator;
        if ("Top Score First".equalsIgnoreCase(sort) || "Lowest Score First".equalsIgnoreCase(sort)) {
            comparator = Comparator.comparing(
                    (Tblapplication app) -> getNumericScore(app),
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            if ("Top Score First".equalsIgnoreCase(sort)) {
                comparator = comparator.reversed();
            }
        } else {
            comparator = Comparator.comparing(
                    Tblapplication::getApplicationAppliedDate,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            if (!"Oldest First".equalsIgnoreCase(sort)) {
                comparator = comparator.reversed();
            }
        }
        list.sort(comparator);
    }

    private BigDecimal getNumericScore(Tblapplication app) {
        if (app == null || app.getApplicationId() == null) {
            return null;
        }
        return applicationScores.get(app.getApplicationId());
    }

    private String getCandidateName(Tblapplication app) {
        try {
            return app.getCandidateId().getUserId().getUserName();
        } catch (Exception e) {
            return "";
        }
    }

    private String getCandidateEmail(Tblapplication app) {
        try {
            return app.getCandidateId().getUserId().getUserEmail();
        } catch (Exception e) {
            return "";
        }
    }

    private String getJobTitle(Tblapplication app) {
        try {
            return app.getJobId().getJobTitle();
        } catch (Exception e) {
            return "";
        }
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ENGLISH).trim();
    }

    private String normalizeStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return "Applied";
        }
        return status.trim().replaceAll("\\s+", " ");
    }

    public List<String> getApplicantJobOptions() {
        List<String> jobs = new ArrayList<>();
        if (allRecruiterApplicationList == null) {
            return jobs;
        }
        for (Tblapplication app : allRecruiterApplicationList) {
            String title = getJobTitle(app);
            if (title != null && !title.trim().isEmpty() && !jobs.contains(title.trim())) {
                jobs.add(title.trim());
            }
        }
        jobs.sort(String::compareToIgnoreCase);
        return jobs;
    }

    public String getDisplayStatus(Tblapplication app) {
        if (app == null || app.getApplicationStatus() == null || app.getApplicationStatus().trim().isEmpty()) {
            return "Applied";
        }
        String status = app.getApplicationStatus().trim();
        if ("Rejected".equalsIgnoreCase(status)) {
            return "Rejected Application";
        }
        return status;
    }

    public String getCandidateLocation(Tblapplication app) {
        try {
            String city = app.getCandidateId().getCandidateCity();
            String state = app.getCandidateId().getCandidateState();
            if (city != null && !city.trim().isEmpty() && state != null && !state.trim().isEmpty()) {
                return city + ", " + state;
            }
            if (city != null && !city.trim().isEmpty()) {
                return city;
            }
            if (state != null && !state.trim().isEmpty()) {
                return state;
            }
        } catch (Exception e) {
            // Keep profile drawer resilient if optional candidate fields are null.
        }
        return "Location not provided";
    }

    public String getCandidateSkillsText(Tblapplication app) {
        try {
            if (app == null || app.getApplicationId() == null) {
                return "Skills not provided";
            }

            client.setToken(loginBean.getToken());

            String skills = client.getCandidateSkillsText(app.getApplicationId());

            if (skills == null || skills.trim().isEmpty()) {
                return "Skills not provided";
            }

            return skills;

        } catch (Exception e) {
            e.printStackTrace();
            return "Skills not provided";
        }
    }

    public String getCandidateEducationText(Tblapplication app) {
        try {
            if (app == null || app.getApplicationId() == null) {
                return "Education not provided";
            }

            client.setToken(loginBean.getToken());

            String education = client.getCandidateEducationText(app.getApplicationId());

            if (education == null || education.trim().isEmpty()) {
                return "Education not provided";
            }

            return education;

        } catch (Exception e) {
            e.printStackTrace();
            return "Education not provided";
        }
    }

    public String getCandidateProfileNotes(Tblapplication app) {
        StringBuilder notes = new StringBuilder();
        notes.append("Applied for ").append(app.getJobId().getJobTitle());
        notes.append(" on ").append(formatApplicationDate(app.getApplicationAppliedDate()));
        notes.append(". Location: ").append(getCandidateLocation(app)).append(".");
        return notes.toString();
    }

    public String getCandidateResumeUrl(Tblapplication app) {
        String fileName = getActiveResumeFileName(app);
        if (fileName == null || fileName.trim().isEmpty()) {
            return "";
        }
        try {
            String encoded = URLEncoder.encode(fileName, "UTF-8");
            return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()
                    + "/resume?file=" + encoded;
        } catch (Exception e) {
            return "";
        }
    }

    private String getActiveResumeFileName(Tblapplication app) {
        try {
            if (app == null) {
                return "";
            }

            if (app.getResumeId() != null && app.getResumeId().getResumeFile() != null) {
                return onlyFileName(app.getResumeId().getResumeFile());
            }

            Object candidate = app.getCandidateId();
            Collection<Object> resumes = (Collection<Object>) candidate.getClass()
                    .getMethod("getTblresumeCollection")
                    .invoke(candidate);

            if (resumes == null || resumes.isEmpty()) {
                return "";
            }

            Object fallback = null;
            for (Object resume : resumes) {
                if (resume == null) {
                    continue;
                }
                fallback = resume;
                Object activeValue = resume.getClass().getMethod("getIsActive").invoke(resume);
                if (Boolean.TRUE.equals(activeValue)) {
                    return onlyFileName((String) resume.getClass().getMethod("getResumeFile").invoke(resume));
                }
            }

            if (fallback != null) {
                return onlyFileName((String) fallback.getClass().getMethod("getResumeFile").invoke(fallback));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String onlyFileName(String path) {
        if (path == null || path.trim().isEmpty()) {
            return "";
        }
        String normalized = path.replace("\\", "/");
        return new File(normalized).getName();
    }

    public boolean isScheduleInterviewDisabled(Tblapplication app) {
        return app == null
                || !"Shortlisted".equalsIgnoreCase(app.getApplicationStatus());
    }

    public boolean isInterviewAlreadyScheduled(Tblapplication app) {
        if (app == null || app.getApplicationId() == null) {
            return false;
        }

        String status = app.getApplicationStatus();

        if ("Interview Scheduled".equalsIgnoreCase(status)
                || "Selected".equalsIgnoreCase(status)
                || "Rejected".equalsIgnoreCase(status)) {
            return true;
        }

        if (recruiterInterviewList == null) {
            return false;
        }

        for (Tblinterview intv : recruiterInterviewList) {
            if (intv != null
                    && intv.getApplicationId() != null
                    && intv.getApplicationId().getApplicationId() != null
                    && intv.getApplicationId().getApplicationId().equals(app.getApplicationId())
                    && !"Cancelled".equalsIgnoreCase(intv.getInterviewStatus())) {
                return true;
            }
        }

        return false;
    }

    public boolean isRejectVisible(Tblapplication app) {
        if (app == null) {
            return false;
        }
        String s = app.getApplicationStatus();
        if (s == null) {
            return true;
        }
        s = s.trim();  // ADD THIS
        return !"Rejected".equalsIgnoreCase(s)
                && !"Shortlisted".equalsIgnoreCase(s)
                && !"Selected".equalsIgnoreCase(s)
                && !"Interview Scheduled".equalsIgnoreCase(s);
    }

    public boolean isShortlistVisible(Tblapplication app) {
        if (app == null) {
            return false;
        }
        String s = app.getApplicationStatus();
        if (s == null) {
            return true;
        }
        s = s.trim();  // ADD THIS
        return !"Shortlisted".equalsIgnoreCase(s)
                && !"Selected".equalsIgnoreCase(s)
                && !"Rejected".equalsIgnoreCase(s)
                && !"Interview Scheduled".equalsIgnoreCase(s);
    }

    public boolean isRejectedState(Tblapplication app) {
        if (app == null) {
            return false;
        }
        String s = app.getApplicationStatus();
        if (s == null) {
            return false;
        }
        return "Rejected".equalsIgnoreCase(s.trim());  // ADD TRIM
    }

    public boolean canScheduleInterview(Tblapplication app) {
        return app != null
                && "Shortlisted".equalsIgnoreCase(app.getApplicationStatus())
                && !isInterviewAlreadyScheduled(app);
    }

    public boolean isSelectedButtonVisible(Tblapplication app) {

        return app != null
                && "Shortlisted".equalsIgnoreCase(
                        app.getApplicationStatus()
                );
    }

    public String formatInterviewDate(Date date) {

        if (date == null) {
            return "";
        }

        return new SimpleDateFormat("dd MMM yyyy")
                .format(date);
    }

    public String formatInterviewTime(Date date) {

        if (date == null) {
            return "";
        }

        return new SimpleDateFormat("hh:mm a")
                .format(date);
    }

    public String getCandidateInitialForInterview(
            Tblinterview interview) {

        try {

            String name
                    = interview.getApplicationId()
                            .getCandidateId()
                            .getUserId()
                            .getUserName();

            if (name != null
                    && !name.isEmpty()) {

                return name.substring(0, 1)
                        .toUpperCase();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "?";
    }

    public void prepareConductInterview(Integer interviewId) {
        selectedInterviewId = interviewId;
        selectedInterview = findInterviewById(interviewId);
        feedback = selectedInterview != null ? selectedInterview.getFeedback() : null;
        result = selectedInterview != null && selectedInterview.getResult() != null
                ? selectedInterview.getResult()
                : "Pending";
    }

    public void conductInterview() {
        try {
            if (selectedInterviewId == null || selectedInterviewId <= 0) {
                addError("Please select an interview.");
                return;
            }
            if (feedback == null || feedback.trim().isEmpty()) {
                addError("Feedback is required.");
                return;
            }
            if (result == null || result.trim().isEmpty() || "Pending".equalsIgnoreCase(result)) {
                addError("Please select Selected or Rejected.");
                return;
            }

            client.setToken(loginBean.getToken());
            Response response = client.conductInterview(selectedInterviewId, feedback.trim(), result.trim());

            if (response.getStatus() == 200) {
                addInfo("Interview completed.");
                selectedInterviewId = null;
                selectedInterview = null;
                feedback = null;
                result = null;
                loadInterviews();
                getRecruiterApplications();
            } else {
                addError(response.readEntity(String.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
            addError("Unable to complete interview.");
        }
    }

    public void prepareReschedule(Tblinterview interview) {
        selectedInterview = interview;
        if (interview != null && interview.getInterviewDate() != null) {
            rescheduleDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(interview.getInterviewDate());
        } else {
            rescheduleDateTime = null;
        }
    }

    public void rescheduleInterview() {
        try {
            if (selectedInterview == null || selectedInterview.getInterviewId() == null) {
                addError("Please select an interview.");
                return;
            }
            if (rescheduleDateTime == null || rescheduleDateTime.trim().isEmpty()) {
                addError("Please select a new date and time.");
                return;
            }
            if (selectedInterview.getInterviewerName() == null
                    || selectedInterview.getInterviewerName().trim().isEmpty()) {
                addError("Interviewer name is required.");
                return;
            }
            if (selectedInterview.getInterviewerMode() == null
                    || selectedInterview.getInterviewerMode().trim().isEmpty()) {
                addError("Interview mode is required.");
                return;
            }

            client.setToken(loginBean.getToken());
            Response response = client.rescheduleInterview(
                    selectedInterview.getInterviewId(),
                    selectedInterview.getInterviewerName().trim(),
                    selectedInterview.getInterviewerMode().trim(),
                    rescheduleDateTime.trim()
            );

            if (response.getStatus() == 200) {
                addInfo("Interview rescheduled.");
                selectedInterview = null;
                rescheduleDateTime = null;
                loadInterviews();
                getRecruiterApplications();
            } else {
                addError(response.readEntity(String.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
            addError("Unable to reschedule interview.");
        }
    }

    public void cancelInterview(Integer interviewId) {
        try {
            if (interviewId == null || interviewId <= 0) {
                addError("Invalid interview.");
                return;
            }

            client.setToken(loginBean.getToken());
            Response response = client.cancelInterview(interviewId);

            if (response.getStatus() == 200) {
                addInfo("Interview cancelled.");
                loadInterviews();
                getRecruiterApplications();
            } else {
                addError(response.readEntity(String.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
            addError("Unable to cancel interview.");
        }
    }

    private Tblinterview findInterviewById(Integer interviewId) {
        if (interviewId == null || recruiterInterviewList == null) {
            return null;
        }
        for (Tblinterview item : recruiterInterviewList) {
            if (item != null && interviewId.equals(item.getInterviewId())) {
                return item;
            }
        }
        return null;
    }

    public boolean isConductVisible(Tblinterview interview) {
        if (interview == null) {
            return false;
        }
        String status = normalizeInterviewValue(interview.getInterviewStatus());
        String result = normalizeInterviewValue(interview.getResult());
        return ("Scheduled".equalsIgnoreCase(status) || "Rescheduled".equalsIgnoreCase(status))
                && (result.isEmpty() || "Pending".equalsIgnoreCase(result));
    }

    public boolean isRescheduleVisible(Tblinterview interview) {
        if (interview == null) {
            return false;
        }
        String status = normalizeInterviewValue(interview.getInterviewStatus());
        return "Scheduled".equalsIgnoreCase(status) || "Rescheduled".equalsIgnoreCase(status);
    }

    public boolean isCancelVisible(Tblinterview interview) {
        return isRescheduleVisible(interview);
    }

    public String getInterviewModeClass(String mode) {
        String value = normalizeInterviewValue(mode).toLowerCase(Locale.ENGLISH);
        if ("offline".equals(value)) {
            return "mode-offline";
        }
        if ("phone".equals(value)) {
            return "mode-phone";
        }
        return "mode-online";
    }

    public String getInterviewStatusClass(String status) {
        String value = normalizeInterviewValue(status).toLowerCase(Locale.ENGLISH);
        if ("completed".equals(value)) {
            return "status-completed";
        }
        if ("cancelled".equals(value)) {
            return "status-cancelled";
        }
        if ("rescheduled".equals(value)) {
            return "status-rescheduled";
        }
        return "status-scheduled";
    }

    public String getInterviewResultClass(String result) {
        String value = normalizeInterviewValue(result).toLowerCase(Locale.ENGLISH);
        if ("selected".equals(value)) {
            return "result-selected";
        }
        if ("rejected".equals(value)) {
            return "result-rejected";
        }
        return "result-pending";
    }

    private String normalizeInterviewValue(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ");
    }

    private void addInfo(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", message));
    }

    private void addError(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", message));
    }

    public Tblapplication getTopCandidateApplication() {
        Tblapplication topApp = null;
        BigDecimal topScore = null;

        if (recruiterApplicationList == null || recruiterApplicationList.isEmpty()) {
            return null;
        }

        for (Tblapplication app : recruiterApplicationList) {
            if (app == null || app.getApplicationId() == null) {
                continue;
            }

            BigDecimal score = applicationScores.get(app.getApplicationId());

            if (score == null) {
                continue;
            }

            if (topScore == null || score.compareTo(topScore) > 0) {
                topScore = score;
                topApp = app;
            }
        }

        return topApp;
    }

    public String getTopCandidateName() {
        Tblapplication app = getTopCandidateApplication();

        if (app == null
                || app.getCandidateId() == null
                || app.getCandidateId().getUserId() == null
                || app.getCandidateId().getUserId().getUserName() == null) {
            return "No scored candidate";
        }

        return app.getCandidateId().getUserId().getUserName();
    }

    public String getTopCandidateJob() {
        Tblapplication app = getTopCandidateApplication();

        if (app == null || app.getJobId() == null || app.getJobId().getJobTitle() == null) {
            return "No job available";
        }

        return app.getJobId().getJobTitle();
    }

    public String getTopCandidateScoreDisplay() {
        Tblapplication app = getTopCandidateApplication();

        if (app == null || app.getApplicationId() == null) {
            return "--";
        }

        BigDecimal score = applicationScores.get(app.getApplicationId());

        if (score == null) {
            return "--";
        }

        return score.setScale(0, RoundingMode.HALF_UP).toPlainString() + "%";
    }

    public String getSelectedInterviewCandidateName() {
        try {
            if (selectedInterview == null
                    || selectedInterview.getApplicationId() == null
                    || selectedInterview.getApplicationId().getCandidateId() == null
                    || selectedInterview.getApplicationId().getCandidateId().getUserId() == null
                    || selectedInterview.getApplicationId().getCandidateId().getUserId().getUserName() == null) {
                return "";
            }

            return selectedInterview.getApplicationId()
                    .getCandidateId()
                    .getUserId()
                    .getUserName();

        } catch (Exception e) {
            return "";
        }
    }

    public String getSelectedInterviewJobTitle() {
        try {
            if (selectedInterview == null
                    || selectedInterview.getApplicationId() == null
                    || selectedInterview.getApplicationId().getJobId() == null
                    || selectedInterview.getApplicationId().getJobId().getJobTitle() == null) {
                return "";
            }

            return selectedInterview.getApplicationId()
                    .getJobId()
                    .getJobTitle();

        } catch (Exception e) {
            return "";
        }
    }

    public Tblinterview getInterview() {
        return interview;
    }

    public void setInterview(Tblinterview interview) {
        this.interview = interview;
    }

    public String getInterviewDateTime() {
        return interviewDateTime;
    }

    public void setInterviewDateTime(String interviewDateTime) {
        this.interviewDateTime = interviewDateTime;
    }

    public List<Tblapplication> getRecruiterApplicationList() {
        return recruiterApplicationList;
    }

    public void setRecruiterApplicationList(List<Tblapplication> recruiterApplicationList) {
        this.recruiterApplicationList = recruiterApplicationList;
    }

    public Tblrecruiters getRecruiter() {
        return recruiter;
    }

    public void setRecruiter(Tblrecruiters recruiter) {
        this.recruiter = recruiter;
    }

    public Map<Integer, BigDecimal> getApplicationScores() {
        return applicationScores;
    }

    public void setApplicationScores(Map<Integer, BigDecimal> applicationScores) {
        this.applicationScores = applicationScores;
    }

    public Integer getSelectedApplicationId() {
        return selectedApplicationId;
    }

    public void setSelectedApplicationId(Integer selectedApplicationId) {
        this.selectedApplicationId = selectedApplicationId;
    }

    public Integer getLastChangedApplicationId() {
        return lastChangedApplicationId;
    }

    public String getApplicantSearchText() {
        return applicantSearchText;
    }

    public void setApplicantSearchText(String applicantSearchText) {
        this.applicantSearchText = applicantSearchText;
    }

    public String getApplicantJobFilter() {
        return applicantJobFilter;
    }

    public void setApplicantJobFilter(String applicantJobFilter) {
        this.applicantJobFilter = applicantJobFilter;
    }

    public String getApplicantStatusFilter() {
        return applicantStatusFilter;
    }

    public void setApplicantStatusFilter(String applicantStatusFilter) {
        this.applicantStatusFilter = applicantStatusFilter;
    }

    public String getApplicantSortFilter() {
        return applicantSortFilter;
    }

    public void setApplicantSortFilter(String applicantSortFilter) {
        this.applicantSortFilter = applicantSortFilter;
    }

    public List<Tblinterview> getRecruiterInterviewList() {
        return recruiterInterviewList;
    }

    public void setRecruiterInterviewList(
            List<Tblinterview> recruiterInterviewList) {
        this.recruiterInterviewList = recruiterInterviewList;
    }

    public String getScheduledInterviewCount() {
        return scheduledInterviewCount;
    }

    public String getCompletedInterviewCount() {
        return completedInterviewCount;
    }

    public String getSelectedCount() {
        return selectedCount;
    }

    public String getTotalInterviewCount() {
        return totalInterviewCount;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Tblinterview getSelectedInterview() {
        return selectedInterview;
    }

    public void setSelectedInterview(
            Tblinterview selectedInterview) {
        this.selectedInterview = selectedInterview;
    }

    public String getRescheduleDateTime() {
        return rescheduleDateTime;
    }

    public void setRescheduleDateTime(
            String rescheduleDateTime) {
        this.rescheduleDateTime = rescheduleDateTime;
    }

    // Change this getter if it exists, or add it:
    public String getInterviewRejectedCount() {
        return interviewRejectedCount;
    }

    public void setInterviewRejectedCount(String interviewRejectedCount) {
        this.interviewRejectedCount = interviewRejectedCount;
    }

    public List<Tblinterview> getSelectedInterviewHistory() {
        return selectedInterviewHistory;
    }

    public void setSelectedInterviewHistory(List<Tblinterview> selectedInterviewHistory) {
        this.selectedInterviewHistory = selectedInterviewHistory;
    }

}
