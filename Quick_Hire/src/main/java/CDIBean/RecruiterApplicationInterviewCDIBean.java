/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package CDIBean;

import Client.RecruiterJerseyClient;
import Entity.Tblapplication;
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
public class RecruiterApplicationInterviewCDIBean  implements Serializable {

    private Tblrecruiters recruiter = new Tblrecruiters();

    private List<Tblapplication> recruiterApplicationList = new ArrayList<>();

    private final RecruiterJerseyClient client = new RecruiterJerseyClient();

    private Map<Integer, BigDecimal> applicationScores = new HashMap<>();
    private Integer selectedApplicationId;

    @Inject
    LoginCDIBean loginBean;

    public RecruiterApplicationInterviewCDIBean() {
    }

    @PostConstruct
    public void init() {
        getRecruiterApplications();
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
                    = client.getRecruiterApplications(
                            recruiter.getRecruiterId()
                    );

            recruiterApplicationList = applications != null
                    ? new ArrayList<>(applications)
                    : new ArrayList<>();

            loadAllScores();

        } catch (Exception e) {

            e.printStackTrace();

            recruiterApplicationList = new ArrayList<>();

            FacesContext.getCurrentInstance()
                    .addMessage(
                            null,
                            new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR,
                                    "Error",
                                    "Unable to load applications."
                            )
                    );
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

    // Call this after loading applications to pre-fetch all scores
    public void loadAllScores() {
        applicationScores = new HashMap<>();
        for (Tblapplication app : recruiterApplicationList) {
            try {
                BigDecimal score = client.getScreeningScore(app.getApplicationId());
                if (score != null) {
                    applicationScores.put(app.getApplicationId(), score);
                }
            } catch (Exception e) {
                // no score yet for this application — skip
            }
        }
    }

// Called from the Screen button on each row
    public void screenApplicant(Integer applicationId) {
        try {
            client.setToken(loginBean.getToken());

            // Check if score already exists
            if (applicationScores.containsKey(applicationId)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "Already Screened", "Score already generated for this applicant."));
                return;
            }

            Response res = client.generateScreeningScore(applicationId);

            if (res.getStatus() == 200) {
                // Fetch the newly generated score
                BigDecimal newScore = client.getScreeningScore(applicationId);
                if (newScore != null) {
                    applicationScores.put(applicationId, newScore);
                }
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Success", "Screening score generated."));
            } else {
                String msg = res.readEntity(String.class);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", msg));
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", "Unable to generate score."));
        }
    }

// Called from Screen All button
    public void screenAllPending() {
        try {
            client.setToken(loginBean.getToken());
            int count = 0;
            for (Tblapplication app : recruiterApplicationList) {
                if ("Applied".equalsIgnoreCase(app.getApplicationStatus())
                        && !applicationScores.containsKey(app.getApplicationId())) {
                    try {
                        client.generateScreeningScore(app.getApplicationId());
                        count++;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            // Reload all scores after bulk generation
            loadAllScores();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Done", count + " scores generated."));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

// Helper used in XHTML — returns the score for a given application
    public BigDecimal getScoreForApplication(Integer applicationId) {
        return applicationScores.getOrDefault(applicationId, null);
    }
    public boolean isNotScreened(Integer applicationId) {
    return !applicationScores.containsKey(applicationId);
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

}
