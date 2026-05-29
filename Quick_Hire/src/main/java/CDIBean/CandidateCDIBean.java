/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package CDIBean;

import Client.JwtClientFilter;
import Entity.*;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.servlet.http.Part;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author tejan
 */
@Named(value = "candidateCDIBean")
@SessionScoped
public class CandidateCDIBean implements Serializable {

    private int candidateId;
    private Tblcandidates candidateObj = new Tblcandidates();
    private Part resumeFile;
    private Collection<Tblresume> resumeList = new ArrayList<>();
    private Part profilePhoto;
        
    // ================= JOBS =================
    private String searchTitle;
    private String searchLocation;
    private String searchSkill;
    private String searchJobType;
    
    private Collection<Tbljob> allJobs = new ArrayList<>();
    private Collection<Tbljob> jobList = new ArrayList<>();
    private Collection<Tbljob> recommendedJobs = new ArrayList<>();
    private Collection<String> jobTypeList = new ArrayList<>();
    
    // ================= APPLICATIONS =================
    private Collection<Tblapplication> applicationList = new ArrayList<>();
    private Map<Integer, String> applicationStatusMap = new HashMap<>();

    // ================= NOTIFICATIONS =================
    private Collection<Tblnotification> notificationList = new ArrayList<>();
    
    // ================= SKILLS =================
    private Integer selectedSkillId;
    private Collection<Tblskills> candidateSkills = new ArrayList<>(); 
    private Integer selectedCategoryId;
    private Collection<Tblskillcategory> allCategories = new ArrayList<>();
    private Collection<Tblskills> filteredSkills = new ArrayList<>();
   
    // ================= INTERVIEWS =================
    private Collection<Tblinterview> interviewList = new ArrayList<>();

    // ================= API =================
    private final String BASE_URL = "http://localhost:8080/Quick_Hire/resources/candidate";
    @Inject private LoginCDIBean loginBean;
    
    
    
    
    // ================= INIT METHOD ========================== 
    @PostConstruct
    public void init() {
        loadJobs();
        loadJobTypes();
        loadAllCategories();
        loadUserData();
        generateRecommendedJobs();
    }
    
    
    
    
    
    
    // ================= AUTH METHODS ==========================
    
    // ------ CREATE CLIENT WITH JWT ------
    private Client getClient() {
        return ClientBuilder.newBuilder()
                .register(new JwtClientFilter(loginBean.getToken())) // ✅ FIX: attach token
                .build();
    }
    
    // ------- GET SESSION USER ------
    private int fetchUserIdFromSession() {
        HttpSession session = (HttpSession) FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getSession(false);

        if (session != null && session.getAttribute("userId") != null) {
            return (int) session.getAttribute("userId");
        }

        return 0;
    }
    
    public int getLoggedInCandidateId() {
        return fetchUserIdFromSession();
    }
    
    // ----- LOAD USER DATA ----------
    public void loadUserData() {

        int userId = getLoggedInCandidateId();

        if (userId > 0) {

            loadCandidateProfile(userId);

            if (candidateId > 0) {

                loadApplications(candidateId);
                loadCandidateSkills();
                loadInterviews(); 
                loadResumes();
            }

            loadNotifications(userId);
                     
            generateRecommendedJobs(); 
        }
    }
    
    
    
    
    
    // ================= PROFILE METHODS =======================
    
    // ----- LOAD PROFILE -------
    public void loadCandidateProfile(int userId) {
        try {
            WebTarget target = getClient().target(BASE_URL + "/getCandidateProfile")
                    .queryParam("userId", userId);

            Tblcandidates c = target.request(MediaType.APPLICATION_JSON)
                    .get(Tblcandidates.class);

            if (c != null) {
                this.candidateObj = c;
                this.candidateId = c.getCandidateId();
                System.out.println("Candidate ID = " + candidateId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // -------- UPDATE PROFILE -------------
        
    public void updateProfile() {

        try {

            // ================= PROFILE PHOTO FOLDER =================
            String basePath = "D:/QuickHireUploads/";
            String profilePhotoPath = basePath + "profilephotos/";

            Files.createDirectories(Paths.get(profilePhotoPath));

            // ================= PROFILE PHOTO =================
            if (profilePhoto != null && profilePhoto.getSize() > 0) {

                String photoName = Paths.get(
                        profilePhoto.getSubmittedFileName()
                ).getFileName().toString();

                String lowerPhoto = photoName.toLowerCase();

                // VALIDATION
                if (!(lowerPhoto.endsWith(".png")
                        || lowerPhoto.endsWith(".jpg")
                        || lowerPhoto.endsWith(".jpeg"))) {

                    FacesContext.getCurrentInstance().addMessage(
                            null,
                            new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR,
                                    "Invalid Photo",
                                    "Only PNG/JPG/JPEG allowed"
                            )
                    );
                    return;
                }

                // UNIQUE FILE NAME
                String uniquePhotoName =
                        System.currentTimeMillis() + "_PHOTO_" + photoName;

                // SAVE FILE
                InputStream photoInput = profilePhoto.getInputStream();

                Files.copy(
                        photoInput,
                        Paths.get(profilePhotoPath + uniquePhotoName),
                        StandardCopyOption.REPLACE_EXISTING
                );

                // SAVE INTO USER TABLE
                if (candidateObj.getUserId() != null) {
                    candidateObj.getUserId().setProfilePhoto(uniquePhotoName);
                }                
            }

            // ================= API CALL =================
            WebTarget target = getClient()
                    .target(BASE_URL + "/updateCandidateProfile");

            Response response = target
                    .request(MediaType.APPLICATION_JSON)
                    .put(
                            jakarta.ws.rs.client.Entity.entity(
                                    candidateObj,
                                    MediaType.APPLICATION_JSON
                            )
                    );

            String responseMsg = response.readEntity(String.class);

            FacesMessage message;

            if (response.getStatus() == 200) {

                message = new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        "Profile updated successfully",
                        null
                );

                loadUserData();

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
                                    "Error",
                                    "Something went wrong"
                            )
                    );
        }
    }
    
    public String getProfilePhotoPath() {

        try {

            if(candidateObj != null
                    && candidateObj.getUserId() != null
                    && candidateObj.getUserId().getProfilePhoto() != null
                    && !candidateObj.getUserId().getProfilePhoto().isEmpty()) {

                return candidateObj.getUserId().getProfilePhoto();
            }

        } catch(Exception e) {
        }

        return "default-user.png";
    }

     
    public int getProfileCompletion() {

        if (candidateObj == null) return 0;

        int score = 0;

        // ================= USER INFO (30) =================
        if (candidateObj.getUserId() != null) {

            if (candidateObj.getUserId().getUserName() != null
                    && !candidateObj.getUserId().getUserName().isEmpty()) {
                score += 10;
            }

            if (candidateObj.getUserId().getUserEmail() != null
                    && !candidateObj.getUserId().getUserEmail().isEmpty()) {
                score += 10;
            }

            if (candidateObj.getUserId().getProfilePhoto() != null
                    && !candidateObj.getUserId().getProfilePhoto().isEmpty()) {
                score += 10;
            }
        }

        // ================= CANDIDATE INFO (30) =================
        if (candidateObj.getCandidateCity() != null && !candidateObj.getCandidateCity().isEmpty()) {
            score += 10;
        }

        if (candidateObj.getCandidateState() != null && !candidateObj.getCandidateState().isEmpty()) {
            score += 10;
        }

        if (candidateObj.getCandidateExperience() != null
                && candidateObj.getCandidateExperience() > 0) {
            score += 10;
        }

        // ================= SKILLS (20) =================
        if (candidateSkills != null) {
            score += Math.min(candidateSkills.size() * 5, 20);
        }

        // ================= RESUME (20) =================
        if (resumeList != null) {
            score += Math.min(resumeList.size() * 10, 20);
        }

        return Math.min(score, 100);
    }
    
    
    public List<String> getProfileMissingItems() {

        List<String> missing = new ArrayList<>();

        if (candidateObj.getCandidateCity() == null
                || candidateObj.getCandidateCity().isEmpty()) {

            missing.add("Add city");
        }

        if (candidateObj.getCandidateState() == null
                || candidateObj.getCandidateState().isEmpty()) {

            missing.add("Add state");
        }

        if (candidateSkills == null || candidateSkills.isEmpty()) {

            missing.add("Add skills");
        }

        if (resumeList == null || resumeList.isEmpty()) {

            missing.add("Upload resume");
        }

        if (candidateObj.getUserId() != null &&
            (candidateObj.getUserId().getProfilePhoto() == null
            || candidateObj.getUserId().getProfilePhoto().isEmpty())) {

            missing.add("Upload profile photo");
        }

        return missing;
    }
    
    
    
    
    
    
    // ================= RESUME METHODS ========================
    
    // ----- LOAD RESUMES ------
    public void loadResumes() {

        try {

            WebTarget target = getClient()
                    .target(BASE_URL + "/getCandidateResumes")
                    .queryParam("candidateId", candidateId);

            resumeList = target.request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<Collection<Tblresume>>() {});

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // ---- UPLOAD RESUME -------
    public void uploadResume() {

        try {

            if (resumeFile != null && resumeFile.getSize() > 0) {

                String basePath = "D:/QuickHireUploads/resumes/";

                Files.createDirectories(Paths.get(basePath));

                String fileName = Paths.get(
                        resumeFile.getSubmittedFileName()
                ).getFileName().toString();

                String uniqueName =
                        System.currentTimeMillis() + "_RESUME_" + fileName;

                InputStream input = resumeFile.getInputStream();

                Files.copy(
                        input,
                        Paths.get(basePath + uniqueName),
                        StandardCopyOption.REPLACE_EXISTING
                );

                WebTarget target = getClient()
                        .target(BASE_URL + "/uploadResume")
                        .queryParam("candidateId", candidateId)
                        .queryParam("resumeFile", uniqueName);

                target.request().post(null);

                loadResumes();

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage("Resume Uploaded")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // ---------- DELETE RESUME -----------
    public void deleteResume(int resumeId) {

        try {

            WebTarget target = getClient()
                    .target(BASE_URL + "/deleteResume")
                    .queryParam("resumeId", resumeId);

            target.request().delete();

            loadResumes();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --------- TOGGLE STATUS ------------
    public void toggleResumeStatus(Tblresume resume) {

        try {

            System.out.println("Resume ID = " + resume.getResumeId());
            System.out.println("Status = " + resume.getIsActive());

            WebTarget target = getClient()
                    .target(BASE_URL + "/toggleResumeStatus")
                    .queryParam("resumeId", resume.getResumeId())
                    .queryParam("status", resume.getIsActive());

            Response response = target.request().put(
                    jakarta.ws.rs.client.Entity.text("")
            );

            System.out.println("API Status = " + response.getStatus());

            loadResumes();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     
        
    
    
    
    
    // ================= JOB METHODS ===========================
    
    // ------- LOAD JOBS ------
    public void loadJobs() {
        try {
            WebTarget target = getClient().target(BASE_URL + "/getAllJobs");

            allJobs = target.request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<Collection<Tbljob>>() {});

            jobList = new ArrayList<>(allJobs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // ---------- LOAD JOB TYPES -----------
    public void loadJobTypes() {

        try {

            Collection<String> types = new ArrayList<>();

            for (Tbljob job : allJobs) {

                if (job.getJobType() != null
                        && !job.getJobType().trim().isEmpty()
                        && !types.contains(job.getJobType())) {

                    types.add(job.getJobType());
                }
            }

            jobTypeList = types;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------- SEARCH JOB ---------
    public void searchJobs() {

        try {

            // LOAD ALL IF EMPTY
            if ((searchTitle == null || searchTitle.trim().isEmpty())
                    && (searchLocation == null || searchLocation.trim().isEmpty())
                    && (searchSkill == null || searchSkill.trim().isEmpty())
                    && (searchJobType == null || searchJobType.trim().isEmpty())) {

                jobList = new ArrayList<>(allJobs);
                return;
            }

            Collection<Tbljob> filteredJobs = new ArrayList<>();

            for (Tbljob job : allJobs) {

                boolean matchTitle = true;
                boolean matchLocation = true;
                boolean matchSkill = true;
                boolean matchType = true;

                // TITLE
                if (searchTitle != null && !searchTitle.trim().isEmpty()) {

                    matchTitle =
                            job.getJobTitle() != null
                            && job.getJobTitle()
                                    .toLowerCase()
                                    .contains(searchTitle.toLowerCase());
                }

                // LOCATION
                if (searchLocation != null && !searchLocation.trim().isEmpty()) {

                    matchLocation =
                            job.getJobLocation() != null
                            && job.getJobLocation()
                                    .toLowerCase()
                                    .contains(searchLocation.toLowerCase());
                }

                // JOB TYPE
                if (searchJobType != null && !searchJobType.trim().isEmpty()) {

                    matchType =
                            job.getJobType() != null
                            && job.getJobType()
                                    .equalsIgnoreCase(searchJobType);
                }

                // SKILL
                if (searchSkill != null && !searchSkill.trim().isEmpty()) {

                    matchSkill = false;

                    if (job.getTblskillsCollection() != null) {

                        for (Tblskills skill : job.getTblskillsCollection()) {

                            if (skill.getSkillName() != null
                                    && skill.getSkillName()
                                            .toLowerCase()
                                            .contains(searchSkill.toLowerCase())) {

                                matchSkill = true;
                                break;
                            }
                        }
                    }
                }

                // FINAL MATCH
                if (matchTitle && matchLocation && matchSkill && matchType) {

                    filteredJobs.add(job);
                }
            }

            jobList = filteredJobs;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // -------- RESET ---------
    public void resetSearch() {

        searchTitle = "";
        searchLocation = "";
        searchSkill = "";
        searchJobType = "";

        jobList = new ArrayList<>(allJobs);
    }
    
    // -------- GENERATE RECOMMENDED JOBS ----------
    public void generateRecommendedJobs() {

        recommendedJobs = new ArrayList<>();

        // safety checks
        if (candidateObj == null
                || allJobs == null
                || candidateSkills == null) {
            return;
        }

        Integer experience = candidateObj.getCandidateExperience();

        // score map
        Map<Tbljob, Integer> scoredJobs = new HashMap<>();

        for (Tbljob job : allJobs) {

            // SKIP ALREADY APPLIED JOBS
            if (isApplied(job.getJobId())) {
                continue;
            }

            int score = 0;

            // ================= EXPERIENCE MATCH =================
            if (experience != null
                    && job.getExperienceRequired() != null) {

                int diff = experience - job.getExperienceRequired();

                // exact or higher experience
                if (diff >= 0) {

                    score += 30;

                } else if (diff >= -1) {

                    // allow small gap
                    score += 15;

                } else {

                    // too much gap
                    continue;
                }
            }

            // ================= SKILLS MATCH =================
            int skillMatches = 0;

            if (job.getTblskillsCollection() != null) {

                for (Tblskills jobSkill : job.getTblskillsCollection()) {

                    for (Tblskills candSkill : candidateSkills) {

                        if (jobSkill.getSkillName() != null
                                && candSkill.getSkillName() != null
                                && jobSkill.getSkillName()
                                        .equalsIgnoreCase(
                                                candSkill.getSkillName())) {

                            skillMatches++;
                            break;
                        }
                    }
                }
            }

            // scoring
            score += skillMatches * 20;

            // location bonus
            if (candidateObj.getCandidateCity() != null
                    && job.getJobCity() != null
                    && candidateObj.getCandidateCity()
                            .equalsIgnoreCase(job.getJobCity())) {

                score += 10;
            }

            // ONLY GOOD MATCHES
            if (score >= 30) {

                scoredJobs.put(job, score);
            }
        }

        // SORT + LIMIT TOP 5
        recommendedJobs = scoredJobs.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();
    }
    
    private int getJobScore(Tbljob job) {

        int score = 0;

        Integer experience = candidateObj.getCandidateExperience();

        if (experience != null && job.getExperienceRequired() != null) {
            if (experience >= job.getExperienceRequired()) {
                score += 30;
            }
        }

        int skillMatchCount = 0;

        if (job.getTblskillsCollection() != null && candidateSkills != null) {

            for (Tblskills jobSkill : job.getTblskillsCollection()) {
                for (Tblskills candSkill : candidateSkills) {

                    if (jobSkill.getSkillName() != null
                            && candSkill.getSkillName() != null
                            && jobSkill.getSkillName().equalsIgnoreCase(candSkill.getSkillName())) {

                        skillMatchCount++;
                        break;
                    }
                }
            }
        }

        score += skillMatchCount * 10;

        return score;
    }
    
    
    
    
    
    
    // ================= APPLICATION METHODS ===================
        
    public void loadApplications(int candidateId) {
        try {
            WebTarget target = getClient()
                    .target(BASE_URL + "/getCandidateApplications")
                    .queryParam("candidateId", candidateId);

            applicationList = target.request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<Collection<Tblapplication>>() {});

            // ✅ CLEAR OLD DATA
            applicationStatusMap.clear();

            // ✅ PRELOAD STATUS (NO EL CALLS ANYMORE)
            for (Tblapplication app : applicationList) {

                //String status = getApplicationStatusAPI(app.getApplicationId());

                applicationStatusMap.put(app.getApplicationId(), app.getApplicationStatus());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
     public List<Tblapplication> getRecentApplications() {

        return applicationList.stream()
                .limit(5)
                .toList();
    }
    
    private String getApplicationStatusAPI(int applicationId) {
        try {
            WebTarget target = getClient()
                    .target(BASE_URL + "/getApplicationStatus")
                    .queryParam("applicationId", applicationId);

            return target.request(MediaType.APPLICATION_JSON)
                    .get(String.class);

        } catch (Exception e) {
            return "Unknown";
        }
    }
    
    
    public String getApplicationStatus(int applicationId) {

        try {

            WebTarget target = getClient()
                    .target(BASE_URL + "/getApplicationStatus")
                    .queryParam("applicationId", applicationId);

            return target.request(MediaType.APPLICATION_JSON)
                    .get(String.class);

        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown";
        }
    }
    
    public void deleteApplication(int applicationId) {
        try {

            WebTarget target = getClient()
                    .target(BASE_URL + "/deleteApplication")
                    .queryParam("applicationId", applicationId);

            target.request().delete();

            loadApplications(candidateId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // ------- APPLY JOB ---------
    public void applyJob(int jobId, int candidateId) {
        try {
            if (isApplied(jobId)) {
                return;
            }

            WebTarget target = getClient().target(BASE_URL + "/applyForJob");

            Tblapplication app = new Tblapplication();
            
            // IMPORTANT: set IDs properly
            app.setJobId(new Entity.Tbljob(jobId));
            app.setCandidateId(new Entity.Tblcandidates(candidateId));

            target.request(MediaType.APPLICATION_JSON)
                    .post(jakarta.ws.rs.client.Entity.entity(app, MediaType.APPLICATION_JSON));

            //loadApplications(candidateId);
            loadUserData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isApplied(Integer jobId) {
        if (applicationList == null) {
            return false;
        }

        for (Tblapplication app : applicationList) {

            if (app.getJobId() != null
                    && app.getJobId().getJobId() != null
                    && app.getJobId().getJobId().equals(jobId)) {

                return true;
            }
        }

        return false;
    }
    
    public int getApplicationsByStatus(String status) {
        int count = 0;

        if (applicationList != null) {

            for (Tblapplication app : applicationList) {

                String currentStatus = applicationStatusMap.get(app.getApplicationId());

                if (currentStatus != null
                        && currentStatus.equalsIgnoreCase(status)) {

                    count++;
                }
            }
        }

        return count;
    }
    
    
    
    
    
    
    
    // ================= SKILL METHODS =========================
    
    // -------- LOAD CANDIDATE SKILLS ---------
    
    public void loadAllCategories() {

        try {

            WebTarget target = getClient()
                    .target(BASE_URL + "/getAllSkillCategories");

            allCategories = target
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<
                            Collection<Tblskillcategory>>() {});

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    
    public void loadSkillsByCategory() {

        try {

            // reset old selected skill
            selectedSkillId = null;

            if (selectedCategoryId == null) {

                filteredSkills = new ArrayList<>();
                return;
            }

            WebTarget target = getClient()
                    .target(BASE_URL + "/getSkillsByCategory")
                    .queryParam("categoryId", selectedCategoryId);

            filteredSkills = target
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<Collection<Tblskills>>() {});

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    
    public void loadCandidateSkills() {

        try {

            // Always ensure profile is loaded
            if (candidateId == 0) {

                int userId = getLoggedInCandidateId();

                if (userId > 0) {

                    loadCandidateProfile(userId);
                }
            }

            // still not found
            if (candidateId == 0) {

                candidateSkills = new ArrayList<>();
                return;
            }

            WebTarget target = getClient()
                    .target(BASE_URL + "/getCandidateSkills")
                    .queryParam("candidateId", candidateId);

            Response response = target
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() == 200) {

                candidateSkills = response.readEntity(
                        new GenericType<Collection<Tblskills>>() {}
                );

                System.out.println("Candidate Skills Loaded: "
                        + candidateSkills.size());

            } else {

                candidateSkills = new ArrayList<>();

                System.out.println("No skills found");
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
        
    }
   
    // ------ ADD SKILL -----
    public void addSkill() {

        try {

            if (selectedSkillId == null) {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Please select skill",
                                null
                        )
                );

                return;
            }

            WebTarget target = getClient()
                    .target(BASE_URL + "/addSkillToCandidate")
                    .queryParam("candidateId", candidateId)
                    .queryParam("skillId", selectedSkillId);

            Response response = target
                    .request(MediaType.APPLICATION_JSON)
                    .post(null);

            String msg = response.readEntity(String.class);

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            msg,
                            null
                    )
            );

            loadCandidateSkills();

            selectedSkillId = null;
            filteredSkills = new ArrayList<>();
            selectedCategoryId = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------- REMOVE SKILL ---------
    public void removeSkill(int skillId) {

        try {

            WebTarget target = getClient()
                    .target(BASE_URL + "/removeSkillFromCandidate")
                    .queryParam("candidateId", candidateId)
                    .queryParam("skillId", skillId);

            Response response = target
                    .request()
                    .delete();

            String msg = response.readEntity(String.class);

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            msg,
                            null
                    )
            );

            loadCandidateSkills();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    
    
    
    // ================= INTERVIEW METHODS =====================
    
    // ------- LOAD INTERVIEWS -------
    public void loadInterviews() {
        try {

            // RESET LIST
            interviewList = new ArrayList<>();

            if (applicationList != null) {

                for (Tblapplication app : applicationList) {

                    WebTarget target = getClient()
                            .target(BASE_URL + "/getCandidateInterviews")
                            .queryParam("applicationId", app.getApplicationId());

                    Collection<Tblinterview> temp = target
                            .request(MediaType.APPLICATION_JSON)
                            .get(new GenericType<Collection<Tblinterview>>() {});

                    // ADD UNIQUE INTERVIEWS ONLY
                    if (temp != null && !temp.isEmpty()) {

                        for (Tblinterview interview : temp) {

                            boolean exists = false;

                            for (Tblinterview existing : interviewList) {

                                if (existing.getInterviewId()
                                        .equals(interview.getInterviewId())) {

                                    exists = true;
                                    break;
                                }
                            }

                            if (!exists) {
                                interviewList.add(interview);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    
    
    
    // ================= NOTIFICATION METHODS ==================
    
    // -------- LOAD NOTIFICATIONS -------
    public void loadNotifications(int userId) {
        try {
            WebTarget target = getClient().target(BASE_URL + "/getCandidateNotifications")
                    .queryParam("userId", userId);

            notificationList = target.request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<Collection<Tblnotification>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Tblnotification> getRecentNotifications() {

        return notificationList.stream()
                .limit(5)
                .toList();
    }
    
    
   
    

    
    
    
    
    
    // ================= GETTERS & SETTERS =================

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }
    
    public Tblcandidates getCandidateObj() {
        return candidateObj;
    }

    public void setCandidateObj(Tblcandidates candidateObj) {
        this.candidateObj = candidateObj;
    }
    
    public Part getResumeFile() {
        return resumeFile;
    }

    public void setResumeFile(Part resumeFile) {
        this.resumeFile = resumeFile;
    }
    
    public Collection<Tblresume> getResumeList() {
        return resumeList;
    }

    public void setResumeList(Collection<Tblresume> resumeList) {
        this.resumeList = resumeList;
    }
    
    public Part getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(Part profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
    
    public String getSearchTitle() {
        return searchTitle;
    }

    public void setSearchTitle(String searchTitle) {
        this.searchTitle = searchTitle;
    }

    public String getSearchLocation() {
        return searchLocation;
    }

    public void setSearchLocation(String searchLocation) {
        this.searchLocation = searchLocation;
    }

    public String getSearchSkill() {
        return searchSkill;
    }

    public void setSearchSkill(String searchSkill) {
        this.searchSkill = searchSkill;
    }

    public String getSearchJobType() {
        return searchJobType;
    }

    public void setSearchJobType(String searchJobType) {
        this.searchJobType = searchJobType;
    }
    
    public Collection<Tbljob> getAllJobs() {
        return allJobs;
    }

    public void setAllJobs(Collection<Tbljob> allJobs) {
        this.allJobs = allJobs;
    }
    
    public Collection<Tbljob> getJobList() {
        return jobList;
    }
    
    public void setJobList(Collection<Tbljob> jobList) {
        this.jobList = jobList;
    }

    public Collection<Tbljob> getRecommendedJobs() {
        return recommendedJobs;
    }

    public void setRecommendedJobs(Collection<Tbljob> recommendedJobs) {
        this.recommendedJobs = recommendedJobs;
    }

    public Collection<String> getJobTypeList() {
        return jobTypeList;
    }

    public void setJobTypeList(Collection<String> jobTypeList) {
        this.jobTypeList = jobTypeList;
    }

    public Collection<Tblapplication> getApplicationList() {
        return applicationList;
    }
    
    public void setApplicationList(Collection<Tblapplication> applicationList) {
        this.applicationList = applicationList;
    }
    
    public Map<Integer, String> getApplicationStatusMap() {
        return applicationStatusMap;
    }

    public void setApplicationStatusMap(Map<Integer, String> applicationStatusMap) {
        this.applicationStatusMap = applicationStatusMap;
    }

    public Collection<Tblnotification> getNotificationList() {
        return notificationList;
    }

    public void setNotificationList(Collection<Tblnotification> notificationList) {
        this.notificationList = notificationList;
    }
    
    public Integer getSelectedSkillId() {
        return selectedSkillId;
    }

    public void setSelectedSkillId(Integer selectedSkillId) {
        this.selectedSkillId = selectedSkillId;
    }

    public Collection<Tblskills> getCandidateSkills() {
        return candidateSkills;
    }

    public void setCandidateSkills(Collection<Tblskills> candidateSkills) {
        this.candidateSkills = candidateSkills;
    }
    
    public Integer getSelectedCategoryId() {
        return selectedCategoryId;
    }

    public void setSelectedCategoryId(Integer selectedCategoryId) {
        this.selectedCategoryId = selectedCategoryId;
    }

    public Collection<Tblskillcategory> getAllCategories() {
        return allCategories;
    }

    public void setAllCategories(Collection<Tblskillcategory> allCategories) {
        this.allCategories = allCategories;
    }

    public Collection<Tblskills> getFilteredSkills() {
        return filteredSkills;
    }

    public void setFilteredSkills(Collection<Tblskills> filteredSkills) {
        this.filteredSkills = filteredSkills;
    }

    public Collection<Tblinterview> getInterviewList() {
        return interviewList;
    }

    public void setInterviewList(Collection<Tblinterview> interviewList) {
        this.interviewList = interviewList;
    }
    
}
