/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package CDIBean;

import Client.RecruiterJerseyClient;
import Entity.Tbleducation;
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
import jakarta.servlet.http.Part;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author RINKAL
 */
@Named(value = "recruiterCDIBean")
@SessionScoped
public class RecruiterCDIBean implements Serializable {

    private Tblrecruiters recruiter = new Tblrecruiters();

    private final RecruiterJerseyClient client
            = new RecruiterJerseyClient();

    @Inject
    LoginCDIBean loginBean;

    private int recruiterId;

    private int todayInterviews;
    private int newApplicants;
    private int shortlistedCandidates;
    private double hiringRate;

    private int activeJobs;
    private int totalApplicants;
    private int upcomingInterviews;
    private int avgTimeToHire;

    private List<Tblscreeningscore> topCandidateslist
            = new ArrayList<>();

    private List<Tblinterview> interviewList
            = new ArrayList<>();

    private List<Tblnotification> recentActivities
            = new ArrayList<>();

    private Tbljob job = new Tbljob();

    private String skillInput;

    private Integer expYears;
    private Integer expMonths;

    private List<Integer> yearList;
    private List<Integer> monthList;

    private String modalActionType;

    private Integer editJobId;
    private boolean editMode = false;

    private List<String> availableStates
            = new ArrayList<>();

    private List<String> availableCities
            = new ArrayList<>();

    private String selectedState;

    private String selectedCity;

    private List<Tbljob> jobList = new ArrayList<>();

    private List<Tblskills> allSkills = new ArrayList<>();
    private List<Tbleducation> educationList = new ArrayList<>();
    private List<Integer> selectedSkillIds = new ArrayList<>();
    private List<Integer> selectedEducationIds = new ArrayList<>();
    private List<Tbleducation> jobEducationList = new ArrayList<>();
    private int totalJobs;
    private int openJobs;
    private int closedJobs;
    private int expiringJobs;
    private Integer editingExpiryJobId;

    private List<Tblskillcategory> skillCategories
            = new ArrayList<>();

    private Integer selectedSkillCategory;

    private List<Tblskills> filteredSkills
            = new ArrayList<>();

    private String newCategoryName;

    private String newSkillName;

    private String skillActionType;

    private Map<Integer, Integer> jobApplicationTotalCounts = new HashMap<>();
    private Map<Integer, Map<String, Integer>> jobApplicationStatusCounts = new HashMap<>();

    private Part profilePhoto;

    // RecruiterCDIBean
    public RecruiterCDIBean() {
    }

    @PostConstruct

    // init
    public void init() {

        availableStates = LocationData.getStates();
        availableCities = LocationData.getAllCities();

        yearList = new ArrayList<>();
        monthList = new ArrayList<>();

        for (int i = 0; i <= 50; i++) {
            yearList.add(i);
        }

        for (int i = 0; i <= 11; i++) {
            monthList.add(i);
        }

    }

    // initLocationData
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
        try {
            client.setToken(loginBean.getToken());

            if (recruiter == null || recruiter.getRecruiterId() == 0) {
                loadProfile();
            }

            loadSkills();
            loadSkillCategories();
            loadEducation();

            selectedSkillCategory = 0;
            filteredSkills = new ArrayList<>(allSkills);

            String jobIdParam = FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getRequestParameterMap()
                    .get("jobId");

            if (jobIdParam != null && !jobIdParam.trim().isEmpty()) {
                editJobId = Integer.valueOf(jobIdParam.trim());
                loadJobForEdit();
                return;
            }

            clearPostJobFormState();

        } catch (Exception e) {
            e.printStackTrace();
            clearPostJobFormState();
        }
    }

    private void clearPostJobFormState() {
        editJobId = null;
        editMode = false;

        job = new Tbljob();

        selectedSkillIds = new ArrayList<>();
        selectedEducationIds = new ArrayList<>();

        selectedState = null;
        selectedCity = null;
        availableCities = new ArrayList<>();

        expYears = null;
        expMonths = null;

        skillInput = "";
        selectedSkillCategory = 0;
        modalActionType = null;
        newCategoryName = null;
        newSkillName = null;
    }

    // onSkillCategoryChange
    public void onSkillCategoryChange(
            jakarta.faces.event.AjaxBehaviorEvent event) {
        loadSkillsByCategory();
    }

    // onWorkModeChange
    public void onWorkModeChange() {

        selectedState = null;
        selectedCity = null;

        availableCities = new ArrayList<>();

        job.setJobState(null);
        job.setJobCity(null);
        job.setJobLocation(null);

        generateJobLocation();
    }

// onStateChange
    public void onStateChange() {

        if (selectedState != null
                && !selectedState.isEmpty()) {

            availableCities = LocationData.getCitiesByState(selectedState);

        } else {

            availableCities = LocationData.getAllCities();
        }

        selectedCity = null;
    }

    // onCityChange
    public void onCityChange() {

        job.setJobState(selectedState);
        job.setJobCity(selectedCity);

        generateJobLocation();
    }

    // generateJobLocation
    public void generateJobLocation() {

        String workMode = job.getWorkMode();

        if (workMode == null
                || workMode.trim().isEmpty()) {

            job.setJobLocation("");
            return;
        }

        if (workMode.equalsIgnoreCase("Remote")) {

            job.setJobLocation("Remote");

            job.setJobState(null);
            job.setJobCity(null);

            return;
        }

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

    // loadProfile
    public void loadProfile() {

        try {

            client.setToken(loginBean.getToken());

            int userId = loginBean.getUserId();

            recruiter = client.getProfile(
                    Tblrecruiters.class,
                    String.valueOf(userId)
            );

            if (recruiter != null && recruiter.getRecruiterId() != null) {
                recruiterId = recruiter.getRecruiterId();
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void uploadProfilePhoto() {
        try {
            if (profilePhoto == null || profilePhoto.getSize() == 0) {
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Photo is optional",
                                "Choose a photo only if you want to update it."
                        )
                );
                return;
            }

            String submittedName = Paths.get(profilePhoto.getSubmittedFileName())
                    .getFileName()
                    .toString()
                    .replaceAll("[^a-zA-Z0-9._-]", "_");

            String lowerName = submittedName.toLowerCase();
            if (!lowerName.endsWith(".jpg")
                    && !lowerName.endsWith(".jpeg")
                    && !lowerName.endsWith(".png")) {
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Invalid photo",
                                "Only JPG and PNG files are allowed."
                        )
                );
                return;
            }

            String basePath = "D:/QuickHireUploads/profilephotos/";
            Files.createDirectories(Paths.get(basePath));

            String uniqueName = System.currentTimeMillis()
                    + "_RECRUITER_"
                    + submittedName;

            Files.copy(
                    profilePhoto.getInputStream(),
                    Paths.get(basePath + uniqueName),
                    StandardCopyOption.REPLACE_EXISTING
            );

            client.setToken(loginBean.getToken());

            Response response = client.uploadProfilePhoto(
                    loginBean.getUserId(),
                    uniqueName
            );

            if (response.getStatus() == 200) {
                if (recruiter != null && recruiter.getUserId() != null) {
                    recruiter.getUserId().setProfilePhoto(uniqueName);
                }

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Profile photo uploaded",
                                "Your profile photo has been updated."
                        )
                );

                loadProfile();
            } else {
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Upload failed",
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
                            "Upload failed",
                            "Something went wrong while uploading photo."
                    )
            );
        }
    }

    public String getProfilePhotoPath() {
        try {
            if (recruiter != null
                    && recruiter.getUserId() != null
                    && recruiter.getUserId().getProfilePhoto() != null
                    && !recruiter.getUserId().getProfilePhoto().trim().isEmpty()) {
                return recruiter.getUserId().getProfilePhoto();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "default-user.png";
    }

    public String getProfilePhotoUrl() {
        try {
            if (recruiter != null
                    && recruiter.getUserId() != null
                    && recruiter.getUserId().getProfilePhoto() != null
                    && !recruiter.getUserId().getProfilePhoto().trim().isEmpty()) {

                String contextPath = FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .getRequestContextPath();

                String fileName = recruiter.getUserId()
                        .getProfilePhoto()
                        .trim();

                return contextPath + "/profilephotos/" + fileName
                        + "?v=" + System.currentTimeMillis();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    // loadDashboardData
    public void loadDashboardData() {

        try {

            client.setToken(loginBean.getToken());

            if (recruiter == null
                    || recruiter.getRecruiterId() == 0) {

                loadProfile();
            }

            if (recruiter == null
                    || recruiter.getRecruiterId() == null) {

                System.out.println("Recruiter not loaded");

                return;
            }
            int recruiterId
                    = recruiter.getRecruiterId();

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

    // updateProfile
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

    // formatInterviewDate
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
                    = new SimpleDateFormat("h:mm a");

            if (isToday) {

                return "Today "
                        + timeFormat.format(interviewDate);

            } else {

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

    // getTimeAgo
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

    // getTomorrowDate
    public String getTomorrowDate() {

        return java.time.LocalDate.now()
                .plusDays(1)
                .toString();
    }

    // saveJob
    public String saveJob() {

        FacesContext fc = FacesContext.getCurrentInstance();

        try {

            client.setToken(loginBean.getToken());

            generateJobLocation();

            job.setRecruiterId(recruiter);

            int years = (expYears != null) ? expYears : 0;
            int months = (expMonths != null) ? expMonths : 0;

            int totalMonths = (years * 12) + months;

            job.setExperienceRequired(totalMonths);

            Collection<Integer> skillIds = new ArrayList<>();

            if (selectedSkillIds != null && !selectedSkillIds.isEmpty()) {
                skillIds.addAll(selectedSkillIds);
            }

            Collection<Integer> educationIds = new ArrayList<>();

            if (selectedEducationIds != null
                    && !selectedEducationIds.isEmpty()) {

                educationIds.addAll(selectedEducationIds);
            }

            Response res;

            if (editMode) {
                res = client.updateJob(
                        job,
                        skillIds,
                        educationIds
                );
            } else {
                res = client.createJob(
                        job,
                        skillIds,
                        educationIds
                );
            }

            String msg = res.readEntity(String.class);

            if (res.getStatus() == 200) {

                fc.addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Success",
                                msg
                        )
                );

                boolean wasEditMode = editMode;
                job = new Tbljob();

                expYears = null;
                expMonths = null;

                skillInput = "";

                selectedSkillIds = new ArrayList<>();
                selectedEducationIds = new ArrayList<>();

                selectedSkillCategory = null;
                filteredSkills = new ArrayList<>(allSkills);

                selectedState = null;
                selectedCity = null;
                availableCities = new ArrayList<>();

                if (wasEditMode) {
                    return "recruiterViewJobs?faces-redirect=true";
                } else {

                    return null;
                }
            }

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

    // updateJob
    public void updateJob() {

        FacesContext fc = FacesContext.getCurrentInstance();

        try {

            client.setToken(loginBean.getToken());

            generateJobLocation();

            job.setRecruiterId(recruiter);

            int years = (expYears != null) ? expYears : 0;
            int months = (expMonths != null) ? expMonths : 0;

            int totalMonths = (years * 12) + months;

            job.setExperienceRequired(totalMonths);

            Collection<Integer> skillIds = new ArrayList<>();

            if (selectedSkillIds != null
                    && !selectedSkillIds.isEmpty()) {

                skillIds.addAll(selectedSkillIds);
            }

            Collection<Integer> educationIds = new ArrayList<>();

            if (selectedEducationIds != null
                    && !selectedEducationIds.isEmpty()) {

                educationIds.addAll(selectedEducationIds);
            }

            Response res = client.updateJob(
                    job,
                    skillIds,
                    educationIds
            );

            String msg = res.readEntity(String.class);

            if (res.getStatus() == 200) {

                fc.addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Success",
                                msg
                        )
                );

                loadViewJobsData();

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

        } catch (Exception e) {

            e.printStackTrace();

            fc.addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            e.getMessage()
                    )
            );
        }
    }

    // loadJobForEdit
    public void loadJobForEdit() {

        FacesContext fc = FacesContext.getCurrentInstance();

        try {
            if (editJobId == null || editJobId <= 0) {
                editMode = false;
                return;
            }

            client.setToken(loginBean.getToken());

            if (recruiter == null || recruiter.getRecruiterId() == 0) {
                loadProfile();
            }

            Collection<Tbljob> jobs = client.getJobs(
                    new GenericType<Collection<Tbljob>>() {
            },
                    String.valueOf(recruiter.getRecruiterId())
            );

            Tbljob selectedJob = null;

            if (jobs != null) {
                for (Tbljob j : jobs) {
                    if (j != null
                            && j.getJobId() != null
                            && j.getJobId().equals(editJobId)) {
                        selectedJob = j;
                        break;
                    }
                }
            }

            if (selectedJob == null) {
                editMode = false;
                job = new Tbljob();

                fc.addMessage(null, new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Error",
                        "Job not found for edit"
                ));
                return;
            }

            editMode = true;
            job = selectedJob;

            job.setJobType(normalizeJobTypeForForm(job.getJobType()));
            job.setWorkMode(normalizeWorkModeForForm(job.getWorkMode()));
            job.setJobStatus(normalizeJobStatusForForm(job.getJobStatus()));
            job.setJobCompensationType(normalizeCompensationTypeForForm(job.getJobCompensationType()));
            job.setJobCompensationPeriod(normalizeCompensationPeriodForForm(job.getJobCompensationPeriod()));

            int totalMonths = job.getExperienceRequired() != null
                    ? job.getExperienceRequired()
                    : 0;

            expYears = totalMonths / 12;
            expMonths = totalMonths % 12;

            selectedState = job.getJobState();
            selectedCity = job.getJobCity();

            if (selectedState != null && !selectedState.trim().isEmpty()) {
                availableCities = LocationData.getCitiesByState(selectedState);
            } else {
                availableCities = new ArrayList<>();
            }

            selectedSkillIds = new ArrayList<>();

            try {
                Collection<Tblskills> jobSkills
                        = client.getJobSkills(String.valueOf(editJobId));

                selectedSkillIds = new ArrayList<>();

                if (jobSkills != null) {
                    for (Tblskills skill : jobSkills) {
                        if (skill != null && skill.getSkillId() != null) {
                            selectedSkillIds.add(skill.getSkillId());
                        }
                    }
                }

                System.out.println("Loaded skill ids for job "
                        + editJobId + ": " + selectedSkillIds);

            } catch (Exception skillException) {
                skillException.printStackTrace();
                selectedSkillIds = new ArrayList<>();
            }

            selectedEducationIds = new ArrayList<>();

            try {
                Collection<Tbleducation> jobEducation = client.getJobEducation(String.valueOf(editJobId));

                if (jobEducation != null) {
                    for (Tbleducation education : jobEducation) {
                        if (education != null && education.getEducationId() != null) {
                            selectedEducationIds.add(education.getEducationId());
                        }
                    }
                }
            } catch (Exception educationException) {
                selectedEducationIds = new ArrayList<>();
            }

        } catch (Exception e) {
            e.printStackTrace();

            editMode = false;
            job = new Tbljob();

            fc.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Error",
                    "Unable to load job for edit"
            ));
        }
    }

    // normalizeJobTypeForForm
    private String normalizeJobTypeForForm(String value) {
        if (value == null) {
            return null;
        }

        String v = value.trim().toLowerCase().replace("-", " ").replace("_", " ");
        v = v.replaceAll("\\s+", " ");

        switch (v) {
            case "full time":
            case "fulltime":
                return "Full-time";

            case "part time":
            case "parttime":
                return "Part-time";

            case "internship":
                return "Internship";

            case "contract":
                return "Contract";

            default:
                return value.trim();
        }
    }

    // normalizeWorkModeForForm
    private String normalizeWorkModeForForm(String value) {
        if (value == null) {
            return null;
        }

        String v = value.trim().toLowerCase().replace("-", " ").replace("_", " ");
        v = v.replaceAll("\\s+", " ");

        switch (v) {
            case "remote":
                return "Remote";

            case "hybrid":
                return "Hybrid";

            case "onsite":
            case "on site":
                return "On-site";

            default:
                return value.trim();
        }
    }

    // normalizeJobStatusForForm
    private String normalizeJobStatusForForm(String value) {
        if (value == null) {
            return null;
        }

        String v = value.trim().toLowerCase();

        switch (v) {
            case "open":
                return "Open";

            case "closed":
                return "Closed";

            case "onhold":
            case "on hold":
                return "Onhold";

            default:
                return value.trim();
        }
    }

    // normalizeCompensationTypeForForm
    private String normalizeCompensationTypeForForm(String value) {
        if (value == null) {
            return null;
        }

        String v = value.trim().toLowerCase().replace("-", " ").replace("_", " ");
        v = v.replaceAll("\\s+", " ");

        switch (v) {
            case "salary":
                return "salary";

            case "stipend":
                return "stipend";

            case "hourly":
            case "hourly pay":
                return "hourly";

            default:
                return v;
        }
    }

    // normalizeCompensationPeriodForForm
    private String normalizeCompensationPeriodForForm(String value) {
        if (value == null) {
            return null;
        }

        String v = value.trim().toLowerCase().replace("-", " ").replace("_", " ");
        v = v.replaceAll("\\s+", " ");

        switch (v) {
            case "month":
            case "monthly":
                return "monthly";

            case "year":
            case "yearly":
            case "annual":
            case "annum":
                return "yearly";

            case "week":
            case "weekly":
                return "weekly";

            case "day":
            case "daily":
                return "daily";

            case "hour":
            case "hourly":
                return "hourly";

            default:
                return v;
        }
    }

    // formatExperience
    public String formatExperience(Integer totalMonths) {

        if (totalMonths == null || totalMonths <= 0) {
            return "Fresher";
        }

        int years = totalMonths / 12;
        int months = totalMonths % 12;

        StringBuilder result = new StringBuilder();

        if (years > 0) {
            result.append(years)
                    .append(years == 1 ? " year" : " years");
        }

        if (months > 0) {
            if (result.length() > 0) {
                result.append(" ");
            }

            result.append(months)
                    .append(months == 1 ? " month" : " months");
        }

        return result.toString();
    }

    // isJobExpired
    public boolean isJobExpired(Tbljob job) {

        if (job == null || job.getJobExpiryDate() == null) {
            return false;
        }

        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date today = cal.getTime();

        return job.getJobExpiryDate().before(today);
    }

    // startOfToday
    private Date startOfToday() {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    // getTodayInputDate
    public String getTodayInputDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(startOfToday());
    }

    // formatDateInput
    public String formatDateInput(Date date) {
        if (date == null) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("Asia/Kolkata"));

        return sdf.format(date);
    }

    // updateJobExpiryDate
    public void updateJobExpiryDate(Tbljob selectedJob) {

        try {

            client.setToken(loginBean.getToken());

            if (selectedJob == null || selectedJob.getJobId() == null) {
                return;
            }

            if (recruiter == null || recruiter.getRecruiterId() == 0) {
                loadProfile();
            }

            if (selectedJob.getJobExpiryDate() == null) {
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Expiry date required",
                                "Please select a valid expiry date."
                        )
                );
                return;
            }

            if (selectedJob.getJobExpiryDate().before(startOfToday())) {
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Invalid expiry date",
                                "Expiry date cannot be in the past."
                        )
                );
                return;
            }

            String expiryDate = formatDateInput(selectedJob.getJobExpiryDate());

            Response res = client.updateJobExpiryDate(
                    selectedJob.getJobId(),
                    recruiter.getRecruiterId(),
                    expiryDate
            );

            String msg = res.readEntity(String.class);

            if (res.getStatus() == 200) {

                selectedJob.setJobStatus("Open");
                editingExpiryJobId = null;
                recalculateViewJobStats();
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Success",
                                msg
                        )
                );

            } else {

                FacesContext.getCurrentInstance().addMessage(
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

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "Unable to update expiry date."
                    )
            );
        }
    }

    // recalculateViewJobStats
    private void recalculateViewJobStats() {

        totalJobs = jobList != null ? jobList.size() : 0;
        openJobs = 0;
        closedJobs = 0;
        expiringJobs = 0;

        Date today = startOfToday();

        long sevenDays = 7L * 24 * 60 * 60 * 1000;

        if (jobList == null) {
            return;
        }

        for (Tbljob j : jobList) {

            if (j == null) {
                continue;
            }

            String status = j.getJobStatus();

            if ("Open".equalsIgnoreCase(status)) {
                openJobs++;

            } else if ("Closed".equalsIgnoreCase(status)) {
                closedJobs++;
            }

            if (j.getJobExpiryDate() != null
                    && "Open".equalsIgnoreCase(status)) {

                long diff = j.getJobExpiryDate().getTime()
                        - today.getTime();

                if (diff >= 0 && diff <= sevenDays) {
                    expiringJobs++;
                }
            }
        }
    }

    // startExpiryEdit
    public void startExpiryEdit(Tbljob selectedJob) {
        if (selectedJob != null) {
            editingExpiryJobId = selectedJob.getJobId();
        }
    }

    // cancelExpiryEdit
    public void cancelExpiryEdit(Tbljob selectedJob) {
        editingExpiryJobId = null;
    }

    // isEditingExpiryJob
    public boolean isEditingExpiryJob(Tbljob selectedJob) {
        return selectedJob != null
                && selectedJob.getJobId() != null
                && selectedJob.getJobId().equals(editingExpiryJobId);
    }

    // editingExpiryJob
    public boolean editingExpiryJob(Tbljob selectedJob) {
        return isEditingExpiryJob(selectedJob);
    }

    // toggleJobStatus
    public void toggleJobStatus(Tbljob selectedJob) {

        try {

            client.setToken(loginBean.getToken());

            if (selectedJob == null
                    || selectedJob.getJobId() == null) {

                return;
            }

            if (recruiter == null
                    || recruiter.getRecruiterId() == 0) {

                loadProfile();
            }

            Response res = client.toggleJobStatus(
                    selectedJob.getJobId(),
                    recruiter.getRecruiterId()
            );

            String msg = res.readEntity(String.class);

            if (res.getStatus() == 200) {

                if ("Open".equalsIgnoreCase(selectedJob.getJobStatus())) {

                    selectedJob.setJobStatus("Closed");

                } else if ("Closed".equalsIgnoreCase(selectedJob.getJobStatus())) {

                    selectedJob.setJobStatus("Open");
                }

                recalculateViewJobStats();

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Success",
                                msg
                        )
                );

            } else {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Action required",
                                msg
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
                            "Unable to update job status"
                    )
            );
        }
    }

    // resetJobForm
    public String resetJobForm() {
        clearPostJobFormState();
        return "recruiterPostJob?faces-redirect=true";
    }

    // parseInteger
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

    // parseDouble
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

    // syncMaxCompensation
    public void syncMaxCompensation() {

        if (job.getJobCompensationMin() != null) {

            job.setJobCompensationMax(
                    job.getJobCompensationMin()
            );
        }
    }

    // loadViewJobsData
    public void loadViewJobsData() {

        try {

            client.setToken(loginBean.getToken());

            if (recruiter == null
                    || recruiter.getRecruiterId() == 0) {

                loadProfile();
            }

            int rid = recruiter.getRecruiterId();

            Collection<Tbljob> jobs = client.getJobs(new GenericType<Collection<Tbljob>>() {
            }, String.valueOf(rid));

            jobList = (jobs != null)
                    ? new ArrayList<>(jobs)
                    : new ArrayList<>();

            recalculateViewJobStats();
            loadJobApplicationCounts();

            System.out.println("Loaded Jobs: " + jobList.size());

        } catch (Exception e) {

            e.printStackTrace();

            jobList = new ArrayList<>();
        }
    }

    private void loadJobApplicationCounts() {
        jobApplicationTotalCounts = new HashMap<>();
        jobApplicationStatusCounts = new HashMap<>();

        if (jobList == null || jobList.isEmpty()) {
            return;
        }

        client.setToken(loginBean.getToken());

        for (Tbljob item : jobList) {
            if (item == null || item.getJobId() == null) {
                continue;
            }

            Integer jobId = item.getJobId();

            jobApplicationTotalCounts.put(
                    jobId,
                    parseInteger(client.getJobApplicationCount(jobId, null))
            );

            Map<String, Integer> statusCounts = new HashMap<>();
            statusCounts.put("Applied", parseInteger(client.getJobApplicationCount(jobId, "Applied")));
            statusCounts.put("Shortlisted", parseInteger(client.getJobApplicationCount(jobId, "Shortlisted")));
            statusCounts.put("Rejected", parseInteger(client.getJobApplicationCount(jobId, "Rejected")));
            statusCounts.put("Selected", parseInteger(client.getJobApplicationCount(jobId, "Selected")));

            jobApplicationStatusCounts.put(jobId, statusCounts);
        }
    }

    public int getJobApplicationTotalCount(Integer jobId) {
        if (jobId == null) {
            return 0;
        }
        return jobApplicationTotalCounts.getOrDefault(jobId, 0);
    }

    public int getJobApplicationStatusCount(Integer jobId, String status) {
        if (jobId == null || status == null) {
            return 0;
        }

        Map<String, Integer> statusCounts = jobApplicationStatusCounts.get(jobId);
        if (statusCounts == null) {
            return 0;
        }

        return statusCounts.getOrDefault(status, 0);
    }

    // loadSkills
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

    // loadSkillCategories
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

    // loadSkillsByCategory
    public void loadSkillsByCategory() {

        try {

            System.out.println("Selected Category: "
                    + selectedSkillCategory);

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

    // getJobEducation
    public String getJobEducation(Integer jobId) {

        try {

            if (jobId == null || jobId <= 0) {
                return "";
            }

            client.setToken(loginBean.getToken());

            Collection<Tbleducation> educations
                    = client.getJobEducation(String.valueOf(jobId));

            if (educations == null || educations.isEmpty()) {
                return "No education added";
            }

            StringBuilder sb = new StringBuilder();

            for (Tbleducation e : educations) {

                if (sb.length() > 0) {
                    sb.append(", ");
                }

                sb.append(e.getEducationName());
            }

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // loadEducation
    public void loadEducation() {

        try {

            client.setToken(loginBean.getToken());

            Collection<Tbleducation> educations
                    = client.getAllEducation();

            educationList = (educations != null)
                    ? new ArrayList<>(educations)
                    : new ArrayList<>();

        } catch (Exception e) {

            e.printStackTrace();

            educationList = new ArrayList<>();
        }
    }

    // addSkillAndOrCategory
    public void addSkillAndOrCategory() {

        try {

            FacesContext fc = FacesContext.getCurrentInstance();

            Integer userId = loginBean.getUserId();

            if (userId == null || userId <= 0) {

                fc.addMessage(
                        "postJobForm:skillMessages",
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Error",
                                "Invalid user session"
                        )
                );

                return;
            }

            String categoryName = null;
            if ("category".equals(modalActionType)
                    && newCategoryName != null
                    && !newCategoryName.trim().isEmpty()) {
                categoryName = newCategoryName.trim();
            }

            String skillNames = null;

            if (newSkillName != null
                    && !newSkillName.trim().isEmpty()) {

                skillNames = newSkillName.trim();
            }

            boolean hasCategory
                    = categoryName != null
                    && !categoryName.isEmpty();

            boolean hasExistingCategory
                    = selectedSkillCategory != null
                    && selectedSkillCategory > 0;

            boolean hasSkills
                    = skillNames != null
                    && !skillNames.isEmpty();

            if ("category".equals(modalActionType)) {

                if (!hasCategory) {

                    fc.addMessage(
                            "postJobForm:categoryMessages",
                            new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR,
                                    null,
                                    "Please enter category name"
                            )
                    );

                    return;
                }
            }

            if ("skill".equals(modalActionType)) {

                if (!hasExistingCategory) {

                    fc.addMessage(
                            "postJobForm:skillMessages",
                            new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR,
                                    null,
                                    "Please select category"
                            )
                    );

                    return;
                }

                if (!hasSkills) {

                    fc.addMessage(
                            "postJobForm:skillMessages",
                            new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR,
                                    null,
                                    "Please enter skill name"
                            )
                    );

                    return;
                }
            }

            client.setToken(loginBean.getToken());

            Response res
                    = client.addSkillAndOrCategory(
                            categoryName,
                            skillNames,
                            selectedSkillCategory,
                            userId
                    );

            String msg = res.readEntity(String.class);

            if (res.getStatus() == 200) {

                loadSkillCategories();

                loadSkills();

                loadSkillsByCategory();

                if (hasCategory && !hasSkills) {

                    fc.addMessage(
                            "postJobForm:categoryMessages",
                            new FacesMessage(
                                    FacesMessage.SEVERITY_INFO,
                                    "Success",
                                    msg
                            )
                    );

                    newCategoryName = "";
                } else if (hasSkills) {

                    fc.addMessage(
                            "postJobForm:skillMessages",
                            new FacesMessage(
                                    FacesMessage.SEVERITY_INFO,
                                    "Success",
                                    msg
                            )
                    );

                    newSkillName = "";
                }

            } else {

                if (hasCategory && !hasSkills) {

                    fc.addMessage(
                            "postJobForm:categoryMessages",
                            new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR,
                                    "Error",
                                    msg
                            )
                    );
                } else {

                    fc.addMessage(
                            "postJobForm:skillMessages",
                            new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR,
                                    "Error",
                                    msg
                            )
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            FacesContext.getCurrentInstance()
                    .addMessage(
                            "postJobForm:skillMessages",
                            new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR,
                                    "Error",
                                    "Something went wrong"
                            )
                    );
        }
    }

    // getJobSkills
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

    private static final NumberFormat inrFormat
            = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    static {
        inrFormat.setMaximumFractionDigits(0);
    }

    // getFormattedCompensation
    public String getFormattedCompensation(Entity.Tbljob j) {

        if (j == null) {
            return "Not disclosed";
        }

        BigDecimal min = j.getJobCompensationMin();
        BigDecimal max = j.getJobCompensationMax();
        String period = j.getJobCompensationPeriod();
        String type = j.getJobCompensationType();

        if (min == null && max == null) {
            return "Not disclosed";
        }

        StringBuilder result = new StringBuilder();

        if (min != null && max != null) {

            if (min.compareTo(max) == 0) {
                result.append(inrFormat.format(min));
            } else {
                result.append(inrFormat.format(min))
                        .append(" - ")
                        .append(inrFormat.format(max));
            }

        } else if (min != null) {

            result.append("From ").append(inrFormat.format(min));

        } else {

            result.append("Up to ").append(inrFormat.format(max));
        }

        if (period != null && !period.trim().isEmpty()) {

            switch (period.trim().toLowerCase()) {

                case "year":
                case "yearly":
                case "annual":
                case "annum":
                    result.append(" / year");
                    break;

                case "month":
                case "monthly":
                    result.append(" / month");
                    break;

                default:
                    result.append(" / ").append(period.trim());
            }
        }

        if (type != null && !type.trim().isEmpty()) {
            result.append(" (").append(type).append(")");
        }

        return result.toString();
    }

    // formatJobDate
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

    // getRecruiter
    public Tblrecruiters getRecruiter() {
        return recruiter;
    }

    // setRecruiter
    public void setRecruiter(Tblrecruiters recruiter) {
        this.recruiter = recruiter;
    }

    // getRecruiterId
    public int getRecruiterId() {
        return recruiterId;
    }

    // setRecruiterId
    public void setRecruiterId(int recruiterId) {
        this.recruiterId = recruiterId;
    }

    // getTodayInterviews
    public int getTodayInterviews() {
        return todayInterviews;
    }

    // setTodayInterviews
    public void setTodayInterviews(int todayInterviews) {
        this.todayInterviews = todayInterviews;
    }

    // getNewApplicants
    public int getNewApplicants() {
        return newApplicants;
    }

    // setNewApplicants
    public void setNewApplicants(int newApplicants) {
        this.newApplicants = newApplicants;
    }

    // getShortlistedCandidates
    public int getShortlistedCandidates() {
        return shortlistedCandidates;
    }

    // setShortlistedCandidates
    public void setShortlistedCandidates(int shortlistedCandidates) {
        this.shortlistedCandidates = shortlistedCandidates;
    }

    // getHiringRate
    public double getHiringRate() {
        return hiringRate;
    }

    // setHiringRate
    public void setHiringRate(double hiringRate) {
        this.hiringRate = hiringRate;
    }

    // getActiveJobs
    public int getActiveJobs() {
        return activeJobs;
    }

    // setActiveJobs
    public void setActiveJobs(int activeJobs) {
        this.activeJobs = activeJobs;
    }

    // getTotalApplicants
    public int getTotalApplicants() {
        return totalApplicants;
    }

    // setTotalApplicants
    public void setTotalApplicants(int totalApplicants) {
        this.totalApplicants = totalApplicants;
    }

    // getUpcomingInterviews
    public int getUpcomingInterviews() {
        return upcomingInterviews;
    }

    // setUpcomingInterviews
    public void setUpcomingInterviews(int upcomingInterviews) {
        this.upcomingInterviews = upcomingInterviews;
    }

    // getAvgTimeToHire
    public int getAvgTimeToHire() {
        return avgTimeToHire;
    }

    // setAvgTimeToHire
    public void setAvgTimeToHire(int avgTimeToHire) {
        this.avgTimeToHire = avgTimeToHire;
    }

    // getTopCandidateslist
    public List<Tblscreeningscore> getTopCandidateslist() {
        return topCandidateslist;
    }

    // setTopCandidateslist
    public void setTopCandidateslist(List<Tblscreeningscore> topCandidateslist) {
        this.topCandidateslist = topCandidateslist;
    }

    // getInterviewList
    public List<Tblinterview> getInterviewList() {
        return interviewList;
    }

    // setInterviewList
    public void setInterviewList(List<Tblinterview> interviewList) {
        this.interviewList = interviewList;
    }

    // getRecentActivities
    public List<Tblnotification> getRecentActivities() {
        return recentActivities;
    }

    // setRecentActivities
    public void setRecentActivities(List<Tblnotification> recentActivities) {
        this.recentActivities = recentActivities;
    }

    // getJob
    public Tbljob getJob() {
        return job;
    }

    // setJob
    public void setJob(Tbljob job) {
        this.job = job;
    }

    // getSkillInput
    public String getSkillInput() {
        return skillInput;
    }

    // setSkillInput
    public void setSkillInput(String skillInput) {
        this.skillInput = skillInput;
    }

    // getAvailableStates
    public List<String> getAvailableStates() {
        return availableStates;
    }

    // setAvailableStates
    public void setAvailableStates(List<String> availableStates) {
        this.availableStates = availableStates;
    }

    // getAvailableCities
    public List<String> getAvailableCities() {
        return availableCities;
    }

    // setAvailableCities
    public void setAvailableCities(List<String> availableCities) {
        this.availableCities = availableCities;
    }

    // getSelectedState
    public String getSelectedState() {
        return selectedState;
    }

    // setSelectedState
    public void setSelectedState(String selectedState) {
        this.selectedState = selectedState;
    }

    // getSelectedCity
    public String getSelectedCity() {
        return selectedCity;
    }

    // setSelectedCity
    public void setSelectedCity(String selectedCity) {
        this.selectedCity = selectedCity;
    }

    // getJobList
    public List<Tbljob> getJobList() {
        return jobList;
    }

    // setJobList
    public void setJobList(List<Tbljob> jobList) {
        this.jobList = jobList;
    }

    // getTotalJobs
    public int getTotalJobs() {
        return totalJobs;
    }

    // setTotalJobs
    public void setTotalJobs(int totalJobs) {
        this.totalJobs = totalJobs;
    }

    // getOpenJobs
    public int getOpenJobs() {
        return openJobs;
    }

    // setOpenJobs
    public void setOpenJobs(int openJobs) {
        this.openJobs = openJobs;
    }

    // getClosedJobs
    public int getClosedJobs() {
        return closedJobs;
    }

    // setClosedJobs
    public void setClosedJobs(int closedJobs) {
        this.closedJobs = closedJobs;
    }

    // getExpiringJobs
    public int getExpiringJobs() {
        return expiringJobs;
    }

    // setExpiringJobs
    public void setExpiringJobs(int expiringJobs) {
        this.expiringJobs = expiringJobs;
    }

    // getAllSkills
    public List<Tblskills> getAllSkills() {
        return allSkills;
    }

    // setAllSkills
    public void setAllSkills(List<Tblskills> allSkills) {
        this.allSkills = allSkills;
    }

    // getSelectedSkillIds
    public List<Integer> getSelectedSkillIds() {
        return selectedSkillIds;
    }

    // setSelectedSkillIds
    public void setSelectedSkillIds(List<Integer> selectedSkillIds) {
        this.selectedSkillIds = selectedSkillIds;
    }

    // getFilteredSkills
    public List<Tblskills> getFilteredSkills() {
        return filteredSkills;
    }

    // setFilteredSkills
    public void setFilteredSkills(List<Tblskills> filteredSkills) {
        this.filteredSkills = filteredSkills;
    }

    // getSkillCategories
    public List<Tblskillcategory> getSkillCategories() {
        return skillCategories;
    }

    // setSkillCategories
    public void setSkillCategories(List<Tblskillcategory> skillCategories) {
        this.skillCategories = skillCategories;
    }

    // getSelectedSkillCategory
    public Integer getSelectedSkillCategory() {
        return selectedSkillCategory;
    }

    // setSelectedSkillCategory
    public void setSelectedSkillCategory(Integer selectedSkillCategory) {
        this.selectedSkillCategory = selectedSkillCategory;
    }

    // getNewCategoryName
    public String getNewCategoryName() {
        return newCategoryName;
    }

    // setNewCategoryName
    public void setNewCategoryName(String newCategoryName) {
        this.newCategoryName = newCategoryName;
    }

    // getNewSkillName
    public String getNewSkillName() {
        return newSkillName;
    }

    // setNewSkillName
    public void setNewSkillName(String newSkillName) {
        this.newSkillName = newSkillName;
    }

    // getExpYears
    public Integer getExpYears() {
        return expYears;
    }

    // setExpYears
    public void setExpYears(Integer expYears) {
        this.expYears = expYears;
    }

    // getExpMonths
    public Integer getExpMonths() {
        return expMonths;
    }

    // setExpMonths
    public void setExpMonths(Integer expMonths) {
        this.expMonths = expMonths;
    }

    // getYearList
    public List<Integer> getYearList() {
        return yearList;
    }

    // setYearList
    public void setYearList(List<Integer> yearList) {
        this.yearList = yearList;
    }

    // getMonthList
    public List<Integer> getMonthList() {
        return monthList;
    }

    // setMonthList
    public void setMonthList(List<Integer> monthList) {
        this.monthList = monthList;
    }

    // getSkillActionType
    public String getSkillActionType() {
        return skillActionType;
    }

    // setSkillActionType
    public void setSkillActionType(String skillActionType) {
        this.skillActionType = skillActionType;
    }

    // getEducationList
    public List<Tbleducation> getEducationList() {
        return educationList;
    }

    // setEducationList
    public void setEducationList(List<Tbleducation> educationList) {
        this.educationList = educationList;
    }

    // getModalActionType
    public String getModalActionType() {
        return modalActionType;
    }

    // setModalActionType
    public void setModalActionType(String modalActionType) {
        this.modalActionType = modalActionType;
    }

    // getSelectedEducationIds
    public List<Integer> getSelectedEducationIds() {
        return selectedEducationIds;
    }

    // setSelectedEducationIds
    public void setSelectedEducationIds(List<Integer> selectedEducationIds) {
        this.selectedEducationIds = selectedEducationIds;
    }

    // getJobEducationList
    public List<Tbleducation> getJobEducationList() {
        return jobEducationList;
    }

    // setJobEducationList
    public void setJobEducationList(List<Tbleducation> jobEducationList) {
        this.jobEducationList = jobEducationList;
    }

    // getEditJobId
    public Integer getEditJobId() {
        return editJobId;
    }

    // setEditJobId
    public void setEditJobId(Integer editJobId) {
        this.editJobId = editJobId;
    }

    // isEditMode
    public boolean isEditMode() {
        return editMode;
    }

    // setEditMode
    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    // getEditingExpiryJobId
    public Integer getEditingExpiryJobId() {
        return editingExpiryJobId;
    }

    // setEditingExpiryJobId
    public void setEditingExpiryJobId(Integer editingExpiryJobId) {
        this.editingExpiryJobId = editingExpiryJobId;
    }

    public Part getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(Part profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    // validateCompensation
    public void validateCompensation(FacesContext context, UIComponent component, Object value) {

        if (value == null) {
            return;
        }

        if (job == null || job.getJobCompensationMin() == null) {
            return;
        }

        BigDecimal max = (BigDecimal) value;
        BigDecimal min = job.getJobCompensationMin();

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
