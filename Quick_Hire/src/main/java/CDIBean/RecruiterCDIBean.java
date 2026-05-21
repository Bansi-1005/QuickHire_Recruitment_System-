/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package CDIBean;

import Client.RecruiterJerseyClient;
import Entity.Tblinterview;
import Entity.Tbljob;
import Entity.Tblnotification;
import Entity.Tblrecruiters;
import Entity.Tblscreeningscore;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.core.Response;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import util.LocationData;

/**
 *
 * @author RINKAL
 */
@Named(value = "recruiterCDIBean")
@SessionScoped
public class RecruiterCDIBean implements Serializable {

    // ================= RECRUITER =================
    private Tblrecruiters recruiter = new Tblrecruiters();

    private final RecruiterJerseyClient client
            = new RecruiterJerseyClient();

    @Inject
    LoginCDIBean loginBean;

    // ================= DASHBOARD VARIABLES =================
    private int recruiterId;

    private int todayInterviews;
    private int newApplicants;
    private int shortlistedCandidates;
    private double hiringRate;

    private int activeJobs;
    private int totalApplicants;
    private int upcomingInterviews;
    private int avgTimeToHire;

    // ================= DASHBOARD LISTS =================
    private List<Tblscreeningscore> topCandidateslist
            = new ArrayList<>();

    private List<Tblinterview> interviewList
            = new ArrayList<>();

    private List<Tblnotification> recentActivities
            = new ArrayList<>();

    // ================= JOB POST VARIABLES =================
    private Tbljob job = new Tbljob();

    private String skillInput;

    // ================= LOCATION VARIABLES =================
    private List<String> availableStates
            = new ArrayList<>();

    private List<String> availableCities
            = new ArrayList<>();

    private String selectedState;

    private String selectedCity;

    // ================= CONSTRUCTOR =================
    public RecruiterCDIBean() {
    }

    // ================= INIT =================
    @PostConstruct
    public void init() {

        availableStates = LocationData.getStates();
    }

    // ================= INIT LOCATION DATA =================
    public void initLocationData() {

        availableStates = LocationData.getStates();

        if (selectedState != null
                && !selectedState.isEmpty()) {

            availableCities
                    = LocationData.getCitiesByState(selectedState);

        } else {

            availableCities = new ArrayList<>();
        }
    }

    // ================= WORK MODE CHANGE =================
    public void onWorkModeChange() {

        selectedState = null;
        selectedCity = null;

        availableCities = new ArrayList<>();

        job.setJobState(null);
        job.setJobCity(null);
        job.setJobLocation(null);

        generateJobLocation();
    }

    // ================= STATE CHANGE =================
    public void onStateChange() {

        if (selectedState != null
                && !selectedState.isEmpty()) {

            availableCities
                    = LocationData.getCitiesByState(selectedState);

        } else {

            availableCities = new ArrayList<>();
        }

        selectedCity = null;

        job.setJobState(selectedState);
        job.setJobCity(null);

        generateJobLocation();
    }

    // ================= CITY CHANGE =================
    public void onCityChange() {

        job.setJobState(selectedState);
        job.setJobCity(selectedCity);

        generateJobLocation();
    }

    // ================= GENERATE JOB LOCATION =================
    public void generateJobLocation() {

        String workMode = job.getWorkMode();

        if (workMode == null
                || workMode.trim().isEmpty()) {

            job.setJobLocation("");
            return;
        }

        // REMOTE
        if (workMode.equalsIgnoreCase("Remote")) {

            job.setJobLocation("Remote");

            job.setJobState(null);
            job.setJobCity(null);

            return;
        }

        // HYBRID / ON-SITE
        StringBuilder sb = new StringBuilder();

        sb.append(workMode);

        if (selectedState != null
                && !selectedState.isEmpty()) {

            sb.append(" - ")
                    .append(selectedState);
        }

        if (selectedCity != null
                && !selectedCity.isEmpty()) {

            sb.append(", ")
                    .append(selectedCity);
        }

        job.setJobLocation(sb.toString());

        job.setJobState(selectedState);
        job.setJobCity(selectedCity);
    }

    // ================= LOAD PROFILE =================
    public void loadProfile() {

        try {

            client.setToken(loginBean.getToken());

            int userId = loginBean.getUserId();

            recruiter = client.getProfile(
                    Tblrecruiters.class,
                    String.valueOf(userId)
            );

            recruiterId = recruiter.getRecruiterId();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // ================= LOAD DASHBOARD =================
    public void loadDashboardData() {

        try {

            client.setToken(loginBean.getToken());

            if (recruiter == null
                    || recruiter.getRecruiterId() == 0) {

                loadProfile();
            }

            int recruiterId = recruiter.getRecruiterId();

            // ACTIVE JOBS
            Collection jobs
                    = client.getJobs(
                            Collection.class,
                            String.valueOf(recruiterId)
                    );

            activeJobs
                    = (jobs != null)
                            ? jobs.size()
                            : 0;

            // TOTAL APPLICANTS
            String totalApplicantsStr
                    = client.getTotalApplicants(
                            String.valueOf(recruiterId)
                    );

            totalApplicants
                    = parseInteger(totalApplicantsStr);

            // NEW APPLICANTS
            String newApplicantsStr
                    = client.getNewApplicants(
                            String.valueOf(recruiterId)
                    );

            newApplicants
                    = parseInteger(newApplicantsStr);

            // SHORTLISTED
            String shortlistedStr
                    = client.getShortlisted(
                            String.valueOf(recruiterId)
                    );

            shortlistedCandidates
                    = parseInteger(shortlistedStr);

            // TODAY INTERVIEWS
            String todayInterviewStr
                    = client.getTodayInterviews(
                            String.valueOf(recruiterId)
                    );

            todayInterviews
                    = parseInteger(todayInterviewStr);

            // UPCOMING INTERVIEWS
            String upcomingStr
                    = client.getUpcomingInterviews(
                            String.valueOf(recruiterId)
                    );

            upcomingInterviews
                    = parseInteger(upcomingStr);

            // HIRING RATE
            String hiringRateStr
                    = client.getHiringRate(
                            String.valueOf(recruiterId)
                    );

            hiringRate
                    = parseDouble(hiringRateStr);

            // AVG TIME TO HIRE
            String avgTimeStr
                    = client.getAvgTimeToHire(
                            String.valueOf(recruiterId)
                    );

            avgTimeToHire
                    = parseInteger(avgTimeStr);

            // TOP CANDIDATES
            Collection<Tblscreeningscore> topList
                    = client.getDashboardTopCandidates(
                            Collection.class,
                            String.valueOf(recruiterId)
                    );

            topCandidateslist
                    = (topList != null)
                            ? new ArrayList<>(topList)
                            : new ArrayList<>();

            // INTERVIEWS
            Collection<Tblinterview> interviewData
                    = client.getDashboardUpcomingInterviews(
                            Collection.class,
                            String.valueOf(recruiterId)
                    );

            interviewList
                    = (interviewData != null)
                            ? new ArrayList<>(interviewData)
                            : new ArrayList<>();

            // ACTIVITIES
            Collection<Tblnotification> activityData
                    = client.getRecentActivities(
                            Collection.class,
                            String.valueOf(
                                    loginBean.getUserId()
                            )
                    );

            recentActivities
                    = (activityData != null)
                            ? new ArrayList<>(activityData)
                            : new ArrayList<>();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // ================= UPDATE PROFILE =================
    public void updateProfile() {

        try {

            client.setToken(loginBean.getToken());

            Response res
                    = client.updateProfile(recruiter);

            String responseMsg
                    = res.readEntity(String.class);

            FacesMessage message;

            if (res.getStatus() == 200) {

                message = new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        "Profile updated successfully",
                        "Your changes have been saved."
                );

                loadProfile();

            } else {

                message = new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Update failed",
                        responseMsg
                );
            }

            FacesContext.getCurrentInstance()
                    .addMessage(null, message);

        } catch (Exception e) {

            e.printStackTrace();

            FacesContext.getCurrentInstance()
                    .addMessage(
                            null,
                            new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR,
                                    "Update failed",
                                    "Something went wrong."
                            )
                    );
        }
    }

    // ================= FORMAT INTERVIEW DATE =================
    public String formatInterviewDate(Object interviewObj) {

        try {

            if (interviewObj == null) {
                return "";
            }

            Date interviewDate;

            if (interviewObj instanceof Date) {

                interviewDate = (Date) interviewObj;

            } else {

                String dateStr = interviewObj.toString();

                SimpleDateFormat parser
                        = new SimpleDateFormat(
                                "yyyy-MM-dd'T'HH:mm:ss"
                        );

                parser.setTimeZone(
                        java.util.TimeZone.getTimeZone("UTC")
                );

                interviewDate = parser.parse(dateStr);
            }

            Calendar today = Calendar.getInstance();

            Calendar interviewCal
                    = Calendar.getInstance();

            interviewCal.setTime(interviewDate);

            boolean isToday
                    = today.get(Calendar.YEAR)
                    == interviewCal.get(Calendar.YEAR)
                    && today.get(Calendar.DAY_OF_YEAR)
                    == interviewCal.get(Calendar.DAY_OF_YEAR);

            SimpleDateFormat timeFormat
                    = new SimpleDateFormat("h a");

            if (isToday) {

                return "Today "
                        + timeFormat.format(interviewDate);

            } else {

                SimpleDateFormat fullFormat
                        = new SimpleDateFormat(
                                "d MMMM yyyy  h a"
                        );

                return fullFormat.format(interviewDate);
            }

        } catch (Exception e) {

            e.printStackTrace();

            return "";
        }
    }

    // ================= TIME AGO =================
    public String getTimeAgo(Object dateObj) {

        try {

            if (dateObj == null) {
                return "";
            }

            Date createdDate;

            if (dateObj instanceof Date) {

                createdDate = (Date) dateObj;

            } else {

                String dateStr = dateObj.toString();

                SimpleDateFormat parser
                        = new SimpleDateFormat(
                                "yyyy-MM-dd'T'HH:mm:ss"
                        );

                parser.setTimeZone(
                        java.util.TimeZone.getTimeZone("UTC")
                );

                createdDate = parser.parse(dateStr);
            }

            long diffMillis
                    = System.currentTimeMillis()
                    - createdDate.getTime();

            long seconds = diffMillis / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (seconds < 60) {

                return "Just now";

            } else if (minutes < 60) {

                return minutes
                        + (minutes == 1
                                ? " min ago"
                                : " mins ago");

            } else if (hours < 24) {

                return hours
                        + (hours == 1
                                ? " hr ago"
                                : " hrs ago");

            } else if (days < 7) {

                return days
                        + (days == 1
                                ? " day ago"
                                : " days ago");

            } else {

                SimpleDateFormat sdf
                        = new SimpleDateFormat(
                                "dd MMM yyyy"
                        );

                return sdf.format(createdDate);
            }

        } catch (Exception e) {

            e.printStackTrace();

            return "";
        }
    }

    // ================= TOMORROW DATE =================
    public String getTomorrowDate() {

        return java.time.LocalDate.now()
                .plusDays(1)
                .toString();
    }

    // ================= SAVE JOB =================
    public String saveJob() {

        FacesContext fc = FacesContext.getCurrentInstance();

        try {

            client.setToken(loginBean.getToken());

            generateJobLocation();

            job.setRecruiterId(recruiter);

            Response res = client.createJob(job);

            String msg = res.readEntity(String.class);

            // SUCCESS
            if (res.getStatus() == 200) {

                fc.addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Success",
                                msg
                        )
                );

                // reset form
                job = new Tbljob();

                skillInput = "";

                selectedState = null;

                selectedCity = null;

                availableCities = new ArrayList<>();

                return null;
            }

            // API VALIDATION / OTHER ERRORS
            if (msg != null) {

                // compensation validation
                if (msg.startsWith("compensationMax:")) {

                    String cleanMessage
                            = msg.substring(msg.indexOf(":") + 1).trim();

                    fc.addMessage(
                            "postJobForm:compensationMax",
                            new FacesMessage(cleanMessage)
                    );
                } // expiry date validation
                else if (msg.startsWith("jobExpiryDate:")) {

                    String cleanMessage
                            = msg.substring(msg.indexOf(":") + 1).trim();

                    fc.addMessage(
                            "postJobForm:jobExpiryDate",
                            new FacesMessage(cleanMessage)
                    );
                } // global errors
                else {

                    fc.addMessage(
                            null,
                            new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR,
                                    "Error",
                                    msg
                            )
                    );
                }
            }

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            String message = e.getMessage();

            if (message != null) {

                // compensation validation
                if (message.startsWith("compensationMax:")) {

                    String cleanMessage
                            = message.substring(message.indexOf(":") + 1).trim();

                    fc.addMessage(
                            "postJobForm:compensationMax",
                            new FacesMessage(cleanMessage)
                    );
                } // expiry date validation
                else if (message.startsWith("jobExpiryDate:")) {

                    String cleanMessage
                            = message.substring(message.indexOf(":") + 1).trim();

                    fc.addMessage(
                            "postJobForm:jobExpiryDate",
                            new FacesMessage(cleanMessage)
                    );
                } // global errors
                else {

                    fc.addMessage(
                            null,
                            new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR,
                                    "Error",
                                    message
                            )
                    );
                }
            }

            return null;
        }
    }
// ================= SAVE DRAFT =================

//    public String saveDraft() {
//
//        job.setJobStatus("Draft");
//
//        return saveJob();
//    }
    // ================= HELPER METHODS =================
    private int parseInteger(String value) {

        try {

            return (value != null
                    && !value.isEmpty())
                    ? Integer.parseInt(value)
                    : 0;

        } catch (Exception e) {

            return 0;
        }
    }

    private double parseDouble(String value) {

        try {

            return (value != null
                    && !value.isEmpty())
                    ? Double.parseDouble(value)
                    : 0;

        } catch (Exception e) {

            return 0;
        }
    }

// ================= SYNC MAX COMPENSATION =================
    public void syncMaxCompensation() {

        if (job.getJobCompensationMin() != null
                && (job.getJobCompensationMax() == null
                || job.getJobCompensationMax().compareTo(java.math.BigDecimal.ZERO) == 0)) {

            job.setJobCompensationMax(
                    job.getJobCompensationMin()
            );
        }
    }

    // ================= GETTERS & SETTERS =================
    public Tblrecruiters getRecruiter() {
        return recruiter;
    }

    public void setRecruiter(Tblrecruiters recruiter) {
        this.recruiter = recruiter;
    }

    public int getRecruiterId() {
        return recruiterId;
    }

    public void setRecruiterId(int recruiterId) {
        this.recruiterId = recruiterId;
    }

    public int getTodayInterviews() {
        return todayInterviews;
    }

    public void setTodayInterviews(int todayInterviews) {
        this.todayInterviews = todayInterviews;
    }

    public int getNewApplicants() {
        return newApplicants;
    }

    public void setNewApplicants(int newApplicants) {
        this.newApplicants = newApplicants;
    }

    public int getShortlistedCandidates() {
        return shortlistedCandidates;
    }

    public void setShortlistedCandidates(int shortlistedCandidates) {
        this.shortlistedCandidates = shortlistedCandidates;
    }

    public double getHiringRate() {
        return hiringRate;
    }

    public void setHiringRate(double hiringRate) {
        this.hiringRate = hiringRate;
    }

    public int getActiveJobs() {
        return activeJobs;
    }

    public void setActiveJobs(int activeJobs) {
        this.activeJobs = activeJobs;
    }

    public int getTotalApplicants() {
        return totalApplicants;
    }

    public void setTotalApplicants(int totalApplicants) {
        this.totalApplicants = totalApplicants;
    }

    public int getUpcomingInterviews() {
        return upcomingInterviews;
    }

    public void setUpcomingInterviews(int upcomingInterviews) {
        this.upcomingInterviews = upcomingInterviews;
    }

    public int getAvgTimeToHire() {
        return avgTimeToHire;
    }

    public void setAvgTimeToHire(int avgTimeToHire) {
        this.avgTimeToHire = avgTimeToHire;
    }

    public List<Tblscreeningscore> getTopCandidateslist() {
        return topCandidateslist;
    }

    public void setTopCandidateslist(List<Tblscreeningscore> topCandidateslist) {
        this.topCandidateslist = topCandidateslist;
    }

    public List<Tblinterview> getInterviewList() {
        return interviewList;
    }

    public void setInterviewList(List<Tblinterview> interviewList) {
        this.interviewList = interviewList;
    }

    public List<Tblnotification> getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(List<Tblnotification> recentActivities) {
        this.recentActivities = recentActivities;
    }

    public Tbljob getJob() {
        return job;
    }

    public void setJob(Tbljob job) {
        this.job = job;
    }

    public String getSkillInput() {
        return skillInput;
    }

    public void setSkillInput(String skillInput) {
        this.skillInput = skillInput;
    }

    public List<String> getAvailableStates() {
        return availableStates;
    }

    public void setAvailableStates(List<String> availableStates) {
        this.availableStates = availableStates;
    }

    public List<String> getAvailableCities() {
        return availableCities;
    }

    public void setAvailableCities(List<String> availableCities) {
        this.availableCities = availableCities;
    }

    public String getSelectedState() {
        return selectedState;
    }

    public void setSelectedState(String selectedState) {
        this.selectedState = selectedState;
    }

    public String getSelectedCity() {
        return selectedCity;
    }

    public void setSelectedCity(String selectedCity) {
        this.selectedCity = selectedCity;
    }
}
