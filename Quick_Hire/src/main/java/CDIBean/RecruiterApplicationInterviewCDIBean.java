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
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author RINKAL
 */
@Named(value = "recruiterApplicationInterviewCDIBean")
@ViewScoped
public class RecruiterApplicationInterviewCDIBean implements Serializable {

    private Tblrecruiters recruiter = new Tblrecruiters();

    private List<Tblapplication> recruiterApplicationList = new ArrayList<>();

    private final RecruiterJerseyClient client = new RecruiterJerseyClient();

    private Map<Integer, BigDecimal> applicationScores = new HashMap<>();
    private Integer selectedApplicationId;

    private Tblinterview interview = new Tblinterview();
    private String interviewDateTime;
    @Inject
    LoginCDIBean loginBean;

    // ================= INTERVIEW MANAGEMENT =================
    private List<Tblinterview> recruiterInterviewList = new ArrayList<>();

    private String scheduledInterviewCount = "0";
    private String completedInterviewCount = "0";
    private String selectedCount = "0";
    private String rejectedCount = "0";
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

            // STEP 1: Load all existing scores in ONE call
            loadAllScores();

            // STEP 2: Generate scores only for unscored apps
            autoScreenAllOnLoad();

        } catch (Exception e) {
            e.printStackTrace();
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

//    // Call this after loading applications to pre-fetch all scores
//    public void loadAllScores() {
//        applicationScores = new HashMap<>();
//        for (Tblapplication app : recruiterApplicationList) {
//            try {
//                BigDecimal score = client.getScreeningScore(app.getApplicationId());
//                if (score != null) {
//                    applicationScores.put(app.getApplicationId(), score);
//                }
//            } catch (Exception e) {
//                // no score yet for this application — skip
//            }
//        }
//    }
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
        return recruiterApplicationList.size();
    }

    public long getNewTodayCount() {
        long count = 0;
        java.util.Calendar today = java.util.Calendar.getInstance();
        for (Tblapplication app : recruiterApplicationList) {
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

    public long getScreenedCount() {
        return recruiterApplicationList.stream()
                .filter(a -> "Screened".equalsIgnoreCase(a.getApplicationStatus())
                || "Shortlisted".equalsIgnoreCase(a.getApplicationStatus())
                || "Selected".equalsIgnoreCase(a.getApplicationStatus())
                || "Rejected".equalsIgnoreCase(a.getApplicationStatus()))
                .count();
    }

    public long getShortlistedCount() {
        return recruiterApplicationList.stream()
                .filter(a -> "Shortlisted".equalsIgnoreCase(a.getApplicationStatus()))
                .count();
    }

    public long getRejectedCount() {
        return recruiterApplicationList.stream()
                .filter(a -> "Rejected".equalsIgnoreCase(a.getApplicationStatus()))
                .count();
    }

    public void shortlistApplication(Integer applicationId) {

        try {

            client.setToken(loginBean.getToken());

            Response response
                    = client.shortlistApplication(
                            applicationId
                    );

            if (response.getStatus() == 200) {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Success",
                                "Applicant shortlisted successfully."
                        )
                );

                refreshApplicationListOnly();

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

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Success",
                                "Interview scheduled successfully."
                        )
                );

                interview = new Tblinterview();
                interviewDateTime = null;
                selectedApplicationId = null;
                loadInterviews();
                getRecruiterApplications();

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

            rejectedCount
                    = client.getRejectedCount(
                            recruiterId
                    );

            totalInterviewCount
                    = client.getTotalInterviewCount(
                            recruiterId
                    );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void prepareConductInterview(
            Integer interviewId) {

        selectedInterviewId = interviewId;

        feedback = null;
        result = null;
    }

    public void conductInterview() {

        try {

            client.setToken(
                    loginBean.getToken()
            );

            Response response
                    = client.conductInterview(
                            selectedInterviewId,
                            feedback,
                            result
                    );

            if (response.getStatus() == 200) {

                FacesContext.getCurrentInstance()
                        .addMessage(
                                null,
                                new FacesMessage(
                                        FacesMessage.SEVERITY_INFO,
                                        "Success",
                                        "Interview completed."
                                )
                        );

                loadInterviews();

            } else {

                FacesContext.getCurrentInstance()
                        .addMessage(
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
        }
    }

    public void prepareReschedule(
            Tblinterview interview) {

        selectedInterview = interview;

        if (interview.getInterviewDate() != null) {

            rescheduleDateTime
                    = new SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm"
                    ).format(
                            interview.getInterviewDate()
                    );
        }
    }

    public void rescheduleInterview() {

        try {

            client.setToken(
                    loginBean.getToken()
            );

            Response response
                    = client.rescheduleInterview(
                            selectedInterview.getInterviewId(),
                            selectedInterview.getInterviewerName(),
                            selectedInterview.getInterviewerMode(),
                            rescheduleDateTime
                    );

            if (response.getStatus() == 200) {

                FacesContext.getCurrentInstance()
                        .addMessage(
                                null,
                                new FacesMessage(
                                        FacesMessage.SEVERITY_INFO,
                                        "Success",
                                        "Interview rescheduled."
                                )
                        );

                loadInterviews();

            } else {

                FacesContext.getCurrentInstance()
                        .addMessage(
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
        }
    }

    private void refreshApplicationListOnly() {
        try {
            Collection<Tblapplication> applications
                    = client.getRecruiterApplications(recruiter.getRecruiterId());
            recruiterApplicationList = applications != null
                    ? new ArrayList<>(applications)
                    : new ArrayList<>();

            loadAllScores();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isScheduleInterviewDisabled(Tblapplication app) {
        return app == null
                || !"Shortlisted".equalsIgnoreCase(app.getApplicationStatus());
    }


    public boolean isInterviewAlreadyScheduled(Tblapplication app) {
        if (app == null || app.getApplicationId() == null) {
            return false;
        }

        if ("Interview Scheduled".equalsIgnoreCase(app.getApplicationStatus())) {
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

    public boolean isShortlistVisible(Tblapplication app) {
        return app != null
                && !isInterviewAlreadyScheduled(app)
                && !"Shortlisted".equalsIgnoreCase(app.getApplicationStatus())
                && !"Selected".equalsIgnoreCase(app.getApplicationStatus())
                && !"Rejected".equalsIgnoreCase(app.getApplicationStatus());
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

    public boolean isConductVisible(
            Tblinterview interview) {

        return interview != null
                && "Scheduled".equalsIgnoreCase(
                        interview.getInterviewStatus()
                );
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

}
