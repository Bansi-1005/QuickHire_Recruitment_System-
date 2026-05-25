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
import Entity.Tblskillcategory;
import Entity.Tblskills;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import util.LocationData;
import jakarta.faces.component.UIComponent;
import jakarta.faces.validator.ValidatorException;
import java.math.BigDecimal;

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

    // ================= VIEW JOBS VARIABLES =================
    private List<Tbljob> jobList = new ArrayList<>();

    private List<Tblskills> allSkills = new ArrayList<>();
    private List<Integer> selectedSkillIds = new ArrayList<>();
    private int totalJobs;
    private int openJobs;
    private int closedJobs;
    private int expiringJobs;

    // ================= SKILL CATEGORY VARIABLES =================
    private List<Tblskillcategory> skillCategories
            = new ArrayList<>();

    private Integer selectedSkillCategory;

    private List<Tblskills> filteredSkills
            = new ArrayList<>();

    private String newCategoryName;

    private String newSkillName;

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

    public void initializeJobForm() {

        loadSkills();

        loadSkillCategories();
            selectedSkillCategory = 0;

//        if (filteredSkills == null || filteredSkills.isEmpty()) {
            filteredSkills = new ArrayList<>(allSkills);
//        }
        
   
    }
    
    // ================= AJAX LISTENER FOR CATEGORY CHANGE =================
public void onSkillCategoryChange(
        jakarta.faces.event.AjaxBehaviorEvent event) {

    System.out.println(
        "onSkillCategoryChange fired, category = "
        + selectedSkillCategory
    );

    loadSkillsByCategory();
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
//            loadSkills();

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

            int recruiterId
                    = recruiter.getRecruiterId();

            // ================= ACTIVE JOBS =================
            try {

                Collection jobs = client.getJobs(
                        Collection.class,
                        String.valueOf(recruiterId)
                );

                activeJobs
                        = (jobs != null)
                                ? jobs.size()
                                : 0;

            } catch (Exception e) {

                e.printStackTrace();

                activeJobs = 0;
            }

            // ================= TOTAL APPLICANTS =================
            try {

                String totalApplicantsStr
                        = client.getTotalApplicants(
                                String.valueOf(recruiterId)
                        );

                totalApplicants
                        = parseInteger(totalApplicantsStr);

            } catch (Exception e) {

                e.printStackTrace();

                totalApplicants = 0;
            }

            // ================= NEW APPLICANTS =================
            try {

                String newApplicantsStr
                        = client.getNewApplicants(
                                String.valueOf(recruiterId)
                        );

                newApplicants
                        = parseInteger(newApplicantsStr);

            } catch (Exception e) {

                e.printStackTrace();

                newApplicants = 0;
            }

            // ================= SHORTLISTED =================
            try {

                String shortlistedStr
                        = client.getShortlisted(
                                String.valueOf(recruiterId)
                        );

                shortlistedCandidates
                        = parseInteger(shortlistedStr);

            } catch (Exception e) {

                e.printStackTrace();

                shortlistedCandidates = 0;
            }

            // ================= TODAY INTERVIEWS =================
            try {

                String todayInterviewStr
                        = client.getTodayInterviews(
                                String.valueOf(recruiterId)
                        );

                todayInterviews
                        = parseInteger(todayInterviewStr);

            } catch (Exception e) {

                e.printStackTrace();

                todayInterviews = 0;
            }

            // ================= UPCOMING INTERVIEWS =================
            try {

                String upcomingStr
                        = client.getUpcomingInterviews(
                                String.valueOf(recruiterId)
                        );

                upcomingInterviews
                        = parseInteger(upcomingStr);

            } catch (Exception e) {

                e.printStackTrace();

                upcomingInterviews = 0;
            }

            // ================= HIRING RATE =================
            try {

                String hiringRateStr
                        = client.getHiringRate(
                                String.valueOf(recruiterId)
                        );

                hiringRate
                        = parseDouble(hiringRateStr);

            } catch (Exception e) {

                e.printStackTrace();

                hiringRate = 0;
            }

            // ================= AVG TIME TO HIRE =================
            try {

                String avgTimeStr
                        = client.getAvgTimeToHire(
                                String.valueOf(recruiterId)
                        );

                avgTimeToHire
                        = parseInteger(avgTimeStr);

            } catch (Exception e) {

                e.printStackTrace();

                avgTimeToHire = 0;
            }

            // ================= TOP CANDIDATES =================
            try {

                Collection<Tblscreeningscore> topList
                        = client.getDashboardTopCandidates(
                                Collection.class,
                                String.valueOf(recruiterId)
                        );

                topCandidateslist
                        = (topList != null)
                                ? new ArrayList<>(topList)
                                : new ArrayList<>();

            } catch (Exception e) {

                e.printStackTrace();

                topCandidateslist
                        = new ArrayList<>();
            }

            // ================= UPCOMING INTERVIEW LIST =================
            try {

                Collection<Tblinterview> interviewData
                        = client.getDashboardUpcomingInterviews(
                                Collection.class,
                                String.valueOf(recruiterId)
                        );

                interviewList
                        = (interviewData != null)
                                ? new ArrayList<>(interviewData)
                                : new ArrayList<>();

            } catch (Exception e) {

                e.printStackTrace();

                interviewList
                        = new ArrayList<>();
            }

            // ================= RECENT ACTIVITIES =================
            try {

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

                recentActivities
                        = new ArrayList<>();
            }

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

            // CHANGED HERE
            SimpleDateFormat timeFormat
                    = new SimpleDateFormat("h:mm a");

            if (isToday) {

                return "Today "
                        + timeFormat.format(interviewDate);

            } else {

                // CHANGED HERE ALSO
                SimpleDateFormat fullFormat
                        = new SimpleDateFormat(
                                "d MMM yyyy  h:mm a"
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
        // ================= CHECK FORM VALIDATION =================
        if (fc.isValidationFailed()) {

            fc.addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "Error While Creating Job"
                    )
            );

            return null;
        }
        try {

            client.setToken(loginBean.getToken());

            generateJobLocation();

            job.setRecruiterId(recruiter);

            // ================= PASS SKILL IDS =================
            Collection<Integer> skillIds = new ArrayList<>();

            if (selectedSkillIds != null && !selectedSkillIds.isEmpty()) {
                skillIds.addAll(selectedSkillIds);
            }

            // ================= API CALL =================
            Response res = client.createJob(job, skillIds);

            String msg = res.readEntity(String.class);

            // ================= SUCCESS =================
            if (res.getStatus() == 200) {

                fc.addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Success",
                                msg
                        )
                );

                job = new Tbljob();
                skillInput = "";
                selectedSkillIds = new ArrayList<>();
                selectedState = null;
                selectedCity = null;
                availableCities = new ArrayList<>();

                return null;
            }

            // ================= ERROR HANDLING =================
            if (msg != null) {

                if (msg.startsWith("compensationMax:")) {

                    String cleanMessage = msg.substring(msg.indexOf(":") + 1).trim();

                    fc.addMessage("postJobForm:compensationMax",
                            new FacesMessage(cleanMessage));

                } else if (msg.startsWith("jobExpiryDate:")) {

                    String cleanMessage = msg.substring(msg.indexOf(":") + 1).trim();

                    fc.addMessage("postJobForm:jobExpiryDate",
                            new FacesMessage(cleanMessage));

                } else {

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

                if (message.startsWith("compensationMax:")) {

                    String cleanMessage = message.substring(message.indexOf(":") + 1).trim();

                    fc.addMessage("postJobForm:compensationMax",
                            new FacesMessage(cleanMessage));

                } else if (message.startsWith("jobExpiryDate:")) {

                    String cleanMessage = message.substring(message.indexOf(":") + 1).trim();

                    fc.addMessage("postJobForm:jobExpiryDate",
                            new FacesMessage(cleanMessage));

                } else {

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

        if (job.getJobCompensationMin() != null) {

            job.setJobCompensationMax(
                    job.getJobCompensationMin()
            );
        }
    }

    // ================= LOAD VIEW JOBS DATA =================
    public void loadViewJobsData() {

        try {

            client.setToken(loginBean.getToken());

            // Load recruiter if not loaded
            if (recruiter == null
                    || recruiter.getRecruiterId() == 0) {

                loadProfile();
            }

            int rid = recruiter.getRecruiterId();

            // ================= GET JOBS =================
            Collection<Tbljob> jobs = client.getJobs(new GenericType<Collection<Tbljob>>() {
            }, String.valueOf(rid));

            // ================= SET JOB LIST =================
            jobList = (jobs != null)
                    ? new ArrayList<>(jobs)
                    : new ArrayList<>();

            // ================= RESET STATS =================
            totalJobs = jobList.size();

            openJobs = 0;

            closedJobs = 0;

            expiringJobs = 0;

            Date now = new Date();

            long sevenDays
                    = 7L * 24 * 60 * 60 * 1000;

            // ================= CALCULATE STATS =================
            for (Tbljob j : jobList) {

                if (j == null) {
                    continue;
                }

                String status = j.getJobStatus();

                // OPEN JOBS
                if ("Open".equalsIgnoreCase(status)) {

                    openJobs++;

                } // CLOSED JOBS
                else if ("Closed".equalsIgnoreCase(status)) {

                    closedJobs++;
                }

                // EXPIRING JOBS
                if (j.getJobExpiryDate() != null
                        && "Open".equalsIgnoreCase(status)) {

                    long diff = j.getJobExpiryDate().getTime()
                            - now.getTime();

                    if (diff > 0
                            && diff <= sevenDays) {

                        expiringJobs++;
                    }
                }
            }

            System.out.println("Loaded Jobs: " + jobList.size());

        } catch (Exception e) {

            e.printStackTrace();

            jobList = new ArrayList<>();
        }
    }

    public void loadSkills() {

        try {

            Integer userId = loginBean != null
                    ? loginBean.getUserId()
                    : null;

            if (userId == null) {

                allSkills = new ArrayList<>();
                filteredSkills = new ArrayList<>();

                return;
            }

            client.setToken(loginBean.getToken());

            Collection<Tblskills> skills
                    = client.getAllSkills(userId);

            allSkills = (skills != null)
                    ? new ArrayList<>(skills)
                    : new ArrayList<>();

            filteredSkills = new ArrayList<>(allSkills);

        } catch (Exception e) {

            e.printStackTrace();

            allSkills = new ArrayList<>();
            filteredSkills = new ArrayList<>();
        }
    }

// ================= LOAD SKILL CATEGORIES =================
    public void loadSkillCategories() {

        try {

            Integer userId = loginBean != null
                    ? loginBean.getUserId()
                    : null;

            if (userId == null) {
                return;
            }

            client.setToken(loginBean.getToken());

            Collection<Tblskillcategory> categories
                    = client.getSkillCategories(userId);

            skillCategories = (categories != null)
                    ? new ArrayList<>(categories)
                    : new ArrayList<>();

        } catch (Exception e) {

            e.printStackTrace();

            skillCategories = new ArrayList<>();
        }
    }
// ================= LOAD SKILLS BY CATEGORY =================

  public void loadSkillsByCategory() {

    try {

        System.out.println("Selected Category: "
                + selectedSkillCategory);

        // ALL CATEGORIES
        if (selectedSkillCategory == null) {

            filteredSkills = new ArrayList<>(allSkills);

            System.out.println("ALL skills loaded: "
                    + filteredSkills.size());

            return;
        }

        client.setToken(loginBean.getToken());

        Integer userId = loginBean.getUserId();

        Collection<Tblskills> skills
                = client.getSkillsByCategory(
                        selectedSkillCategory,
                        userId
                );

        filteredSkills = (skills != null)
                ? new ArrayList<>(skills)
                : new ArrayList<>();

        System.out.println("Filtered skills size: "
                + filteredSkills.size());

    } catch (Exception e) {

        e.printStackTrace();

        filteredSkills = new ArrayList<>();
    }
}

    public void createCategory() {

        try {

            if (newCategoryName == null
                    || newCategoryName.trim().isEmpty()) {

                FacesContext.getCurrentInstance()
                        .addMessage(
                                null,
                                new FacesMessage(
                                        FacesMessage.SEVERITY_ERROR,
                                        "Error",
                                        "Category name is required"
                                )
                        );

                return;
            }

            client.setToken(loginBean.getToken());

            Response res = client.addSkillCategory(
                    newCategoryName,
                    loginBean.getUserId()
            );

            String msg = res.readEntity(String.class);

            if (res.getStatus() == 200) {

                // reload categories
                loadSkillCategories();

                FacesContext.getCurrentInstance()
                        .addMessage(
                                null,
                                new FacesMessage(
                                        FacesMessage.SEVERITY_INFO,
                                        "Success",
                                        msg
                                )
                        );

                newCategoryName = "";

            } else {

                FacesContext.getCurrentInstance()
                        .addMessage(
                                null,
                                new FacesMessage(
                                        FacesMessage.SEVERITY_ERROR,
                                        "Error",
                                        msg
                                )
                        );
            }

        } catch (Exception e) {

            e.printStackTrace();

            FacesContext.getCurrentInstance()
                    .addMessage(
                            null,
                            new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR,
                                    "Error",
                                    "Something went wrong"
                            )
                    );
        }
    }

    public void createSkill() {

        try {

            if (selectedSkillCategory == null) {

                FacesContext.getCurrentInstance()
                        .addMessage(
                                null,
                                new FacesMessage(
                                        FacesMessage.SEVERITY_ERROR,
                                        "Error",
                                        "Select category first"
                                )
                        );

                return;
            }

            if (newSkillName == null
                    || newSkillName.trim().isEmpty()) {

                FacesContext.getCurrentInstance()
                        .addMessage(
                                null,
                                new FacesMessage(
                                        FacesMessage.SEVERITY_ERROR,
                                        "Error",
                                        "Skill name is required"
                                )
                        );

                return;
            }

            client.setToken(loginBean.getToken());

            Response res = client.addSkill(
                    newSkillName,
                    selectedSkillCategory,
                    loginBean.getUserId()
            );

            String msg = res.readEntity(String.class);

            if (res.getStatus() == 200) {

                // reload skills
                loadSkillsByCategory();

                // reload all skills also
                loadSkills();

                FacesContext.getCurrentInstance()
                        .addMessage(
                                null,
                                new FacesMessage(
                                        FacesMessage.SEVERITY_INFO,
                                        "Success",
                                        msg
                                )
                        );

                newSkillName = "";

            } else {

                FacesContext.getCurrentInstance()
                        .addMessage(
                                null,
                                new FacesMessage(
                                        FacesMessage.SEVERITY_ERROR,
                                        "Error",
                                        msg
                                )
                        );
            }

        } catch (Exception e) {

            e.printStackTrace();

            FacesContext.getCurrentInstance()
                    .addMessage(
                            null,
                            new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR,
                                    "Error",
                                    "Something went wrong"
                            )
                    );
        }
    }

    public String getJobSkills(Entity.Tbljob job) {

        try {

            if (job == null || job.getJobId() == null) {
                return "No skills added";
            }

            Collection<Entity.Tblskills> skills
                    = client.getJobSkills(
                            String.valueOf(job.getJobId())
                    );

            if (skills == null || skills.isEmpty()) {
                return "No skills added";
            }

            StringBuilder sb = new StringBuilder();

            for (Entity.Tblskills skill : skills) {

                if (sb.length() > 0) {
                    sb.append(", ");
                }

                sb.append(skill.getSkillName());
            }

            return sb.toString();

        } catch (Exception e) {

            e.printStackTrace();
            return "No skills added";
        }
    }
// ================= FORMAT JOB DATE =================

    public String formatJobDate(Object dateObj) {
        try {
            if (dateObj == null) {
                return "";
            }

            Date date;

            if (dateObj instanceof Date) {
                date = (Date) dateObj;
            } else {
                SimpleDateFormat parser
                        = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                parser.setTimeZone(
                        java.util.TimeZone.getTimeZone("UTC")
                );
                date = parser.parse(dateObj.toString());
            }

            return new SimpleDateFormat("dd MMM yyyy").format(date);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
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

    public List<Tbljob> getJobList() {
        return jobList;
    }

    public void setJobList(List<Tbljob> jobList) {
        this.jobList = jobList;
    }

    public int getTotalJobs() {
        return totalJobs;
    }

    public void setTotalJobs(int totalJobs) {
        this.totalJobs = totalJobs;
    }

    public int getOpenJobs() {
        return openJobs;
    }

    public void setOpenJobs(int openJobs) {
        this.openJobs = openJobs;
    }

    public int getClosedJobs() {
        return closedJobs;
    }

    public void setClosedJobs(int closedJobs) {
        this.closedJobs = closedJobs;
    }

    public int getExpiringJobs() {
        return expiringJobs;
    }

    public void setExpiringJobs(int expiringJobs) {
        this.expiringJobs = expiringJobs;
    }

    public List<Tblskills> getAllSkills() {
        return allSkills;
    }

    public void setAllSkills(List<Tblskills> allSkills) {
        this.allSkills = allSkills;
    }

    public List<Integer> getSelectedSkillIds() {
        return selectedSkillIds;
    }

    public void setSelectedSkillIds(List<Integer> selectedSkillIds) {
        this.selectedSkillIds = selectedSkillIds;
    }

    public List<Tblskills> getFilteredSkills() {
        return filteredSkills;
    }

    public void setFilteredSkills(List<Tblskills> filteredSkills) {
        this.filteredSkills = filteredSkills;
    }

    public List<Tblskillcategory> getSkillCategories() {
        return skillCategories;
    }

    public void setSkillCategories(List<Tblskillcategory> skillCategories) {
        this.skillCategories = skillCategories;
    }

    public Integer getSelectedSkillCategory() {
        return selectedSkillCategory;
    }

    public void setSelectedSkillCategory(Integer selectedSkillCategory) {
        this.selectedSkillCategory = selectedSkillCategory;
    }

    public String getNewCategoryName() {
        return newCategoryName;
    }

    public void setNewCategoryName(String newCategoryName) {
        this.newCategoryName = newCategoryName;
    }

    public String getNewSkillName() {
        return newSkillName;
    }

    public void setNewSkillName(String newSkillName) {
        this.newSkillName = newSkillName;
    }

    public void validateCompensation(FacesContext context, UIComponent component, Object value) {

        // value = MAX compensation field value
        if (value == null) {
            return;
        }

        // MIN compensation from job object
        if (job == null || job.getJobCompensationMin() == null) {
            return;
        }

        BigDecimal max = (BigDecimal) value;
        BigDecimal min = job.getJobCompensationMin();

        // VALIDATION
        if (max.compareTo(min) < 0) {

            throw new ValidatorException(
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Maximum compensation must be greater than minimum compensation",
                            null
                    )
            );
        }
    }

}
