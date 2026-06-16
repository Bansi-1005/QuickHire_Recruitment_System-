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
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author tejan
 */
@Named(value = "adminCDIBean")
@SessionScoped
public class AdminCDIBean implements Serializable {

    // ================= API =================
    private final String BASE_URL = "http://localhost:8080/Quick_Hire/resources/admin";

    @Inject
    private LoginCDIBean loginBean;

    private Collection<Tblrecruiters> recruiterList = new ArrayList<>();
    private Collection<Tblcandidates> candidateList = new ArrayList<>();
    private String activeTab = "CANDIDATES";

    // Skills & Category    
    private Collection<Tblskillcategory> skillCategoryList = new ArrayList<>();
    private Collection<Tblskills> skillList = new ArrayList<>();
    private Tblskillcategory categoryObj = new Tblskillcategory();
    private Tblskills skillObj = new Tblskills();
    private Integer selectedCategoryId;

    // Company
    private Collection<Tblcompany> companyList = new ArrayList<>();
    private Tblcompany companyObj = new Tblcompany();
    private boolean showCompanyForm = false;

    // Jobs
    private Collection<Tbljob> jobList = new ArrayList<>();
    private Tbljob selectedJob;

    // Profile
    private Tblusers userObj = new Tblusers();

    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

    private Part profilePhoto;

    // Notifications
    //skills
    private Collection<Tblskills> pendingSkillList = new ArrayList<>();
    private Collection<Tblskillcategory> pendingCategoryList = new ArrayList<>();
    private Collection<Tblskills> approvedSkillList = new ArrayList<>();
    private Collection<Tblskillcategory> approvedCategoryList = new ArrayList<>();

    //admin dashboard
    private Collection<Tblusers> userList = new ArrayList<>();
    private Collection<Tblapplication> applicationList = new ArrayList<>();
    private Collection<Tblinterview> interviewList = new ArrayList<>();

    public AdminCDIBean() {

    }

    private Client getClient() {
        return ClientBuilder.newBuilder()
                .register(new JwtClientFilter(loginBean.getToken())) // ✅ FIX: attach token
                .build();
    }

    //admin dashboard
    public void loadDashboardPage() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            loadDashboardData();
        }
    }

    public void loadDashboardData() {
        loadUsers();
        loadCandidates();
        loadRecruiters();
        loadCompanies();
        loadJobs();
        loadApplications();
        loadInterviews();
        loadNotificationsPage();
        refreshSkillApprovals();
    }

    public void loadUsers() {
        try {
            userList = getClient()
                    .target(BASE_URL + "/getAllUsers")
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<Collection<Tblusers>>() {
                    });
        } catch (Exception e) {
            e.printStackTrace();
            userList = new ArrayList<>();
        }
    }

    public void loadApplications() {
        try {
            applicationList = getClient()
                    .target(BASE_URL + "/getAllApplications")
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<Collection<Tblapplication>>() {
                    });
        } catch (Exception e) {
            e.printStackTrace();
            applicationList = new ArrayList<>();
        }
    }

    public void loadInterviews() {
        try {
            interviewList = getClient()
                    .target(BASE_URL + "/getAllInterviews")
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<Collection<Tblinterview>>() {
                    });
        } catch (Exception e) {
            e.printStackTrace();
            interviewList = new ArrayList<>();
        }
    }

    public int getTotalUserCount() {
        return userList == null ? 0 : userList.size();
    }

    public int getActiveUserCount() {
        if (userList == null) {
            return 0;
        }

        return (int) userList.stream()
                .filter(u -> u != null && Boolean.TRUE.equals(u.getUserIsActive()))
                .count();
    }

    public int getCandidateCount() {
        return candidateList == null ? 0 : candidateList.size();
    }

    public int getActiveCandidateCount() {
        if (candidateList == null) {
            return 0;
        }

        return (int) candidateList.stream()
                .filter(c -> c != null
                && c.getUserId() != null
                && Boolean.TRUE.equals(c.getUserId().getUserIsActive()))
                .count();
    }

    public int getRecruiterCount() {
        return recruiterList == null ? 0 : recruiterList.size();
    }

    public int getTotalJobCount() {
        return jobList == null ? 0 : jobList.size();
    }

    public int getTotalApplicationCount() {
        return applicationList == null ? 0 : applicationList.size();
    }

    public int getTotalInterviewCount() {
        return interviewList == null ? 0 : interviewList.size();
    }

    public int getScheduledInterviewCount() {
        if (interviewList == null) {
            return 0;
        }

        return (int) interviewList.stream()
                .filter(i -> i != null
                && i.getInterviewStatus() != null
                && (i.getInterviewStatus().equalsIgnoreCase("Scheduled")
                || i.getInterviewStatus().equalsIgnoreCase("Pending")
                || i.getInterviewStatus().equalsIgnoreCase("In Progress")))
                .count();
    }

    public int getPendingSkillCount() {
        return pendingSkillList == null ? 0 : pendingSkillList.size();
    }

    public int getPendingCategoryCount() {
        return pendingCategoryList == null ? 0 : pendingCategoryList.size();
    }

    public int getPendingApprovalCount() {
        return getPendingSkillCount() + getPendingCategoryCount();
    }

    public int getTotalSkillCategoryCount() {
        return skillCategoryList == null ? 0 : skillCategoryList.size();
    }

    public List<Tblusers> getRecentUsers() {
        if (userList == null) {
            return new ArrayList<>();
        }

        return userList.stream()
                .filter(u -> u != null)
                .sorted(Comparator.comparing(Tblusers::getCreatedDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .toList();
    }

    public List<Tbljob> getRecentJobs() {
        if (jobList == null) {
            return new ArrayList<>();
        }

        return jobList.stream()
                .filter(j -> j != null)
                .sorted(Comparator.comparing(Tbljob::getJobPostedDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .toList();
    }

    public List<Tblapplication> getRecentApplications() {
        if (applicationList == null) {
            return new ArrayList<>();
        }

        return applicationList.stream()
                .filter(a -> a != null)
                .sorted(Comparator.comparing(Tblapplication::getApplicationAppliedDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .toList();
    }

    public String getUserRoleName(Tblusers user) {
        if (user == null || user.getRoleId() == null || user.getRoleId().getRoleName() == null) {
            return "-";
        }

        return user.getRoleId().getRoleName();
    }

    public String getUserStatusClass(Tblusers user) {
        return user != null && Boolean.TRUE.equals(user.getUserIsActive()) ? "active" : "inactive";
    }

    public String getJobStatusClass(Tbljob job) {
        if (job == null || job.getJobStatus() == null) {
            return "default";
        }

        String status = job.getJobStatus().toLowerCase();

        if (status.contains("open") || status.contains("active") || status.contains("approved")) {
            return "open";
        }

        if (status.contains("close") || status.contains("reject") || status.contains("inactive")) {
            return "closed";
        }

        return "pending";
    }

    public String getApplicationStatusClass(Tblapplication application) {
        if (application == null || application.getApplicationStatus() == null) {
            return "pending";
        }

        String status = application.getApplicationStatus().toLowerCase();

        if (status.contains("select") || status.contains("shortlist") || status.contains("approve")) {
            return "selected";
        }

        if (status.contains("reject")) {
            return "rejected";
        }

        if (status.contains("review") || status.contains("screen")) {
            return "review";
        }

        return "pending";
    }

    public String getApplicationCandidateName(Tblapplication application) {
        try {
            if (application != null
                    && application.getCandidateId() != null
                    && application.getCandidateId().getUserId() != null
                    && application.getCandidateId().getUserId().getUserName() != null) {
                return application.getCandidateId().getUserId().getUserName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Candidate";
    }

    public String getApplicationJobTitle(Tblapplication application) {
        try {
            if (application != null
                    && application.getJobId() != null
                    && application.getJobId().getJobTitle() != null) {
                return application.getJobId().getJobTitle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Job";
    }

    // ------- GET SESSION USER ------
    private int fetchAdminIdFromSession() {

        HttpSession session = (HttpSession) FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getSession(false);

        if (session != null && session.getAttribute("userId") != null) {

            return (int) session.getAttribute("userId");
        }

        return 0;
    }

    public int getLoggedInAdminId() {
        return fetchAdminIdFromSession();
    }

    /* ===============================================
        All Users
    ===================================================*/
    public void loadUsersPage() {
        activeTab = "CANDIDATES";
        loadCandidates();
        loadRecruiters();
    }

    public void loadRecruiters() {
        try {
            WebTarget target = getClient().target(BASE_URL + "/getAllRecruiters");

            recruiterList = target.request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<Collection<Tblrecruiters>>() {
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadCandidates() {
        try {
            WebTarget target = getClient().target(BASE_URL + "/getAllCandidates");

            candidateList = target.request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<Collection<Tblcandidates>>() {
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showCandidates() {
        activeTab = "CANDIDATES";
    }

    public void showRecruiters() {
        activeTab = "RECRUITERS";
    }

    public String getProfilePhoto(Tblusers user) {

        try {

            if (user != null
                    && user.getProfilePhoto() != null
                    && !user.getProfilePhoto().trim().isEmpty()) {

                return user.getProfilePhoto();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "default-user.png";
    }

    public void toggleUserStatus(Tblusers user) {

        try {

            System.out.println("User ID = " + user.getUserId());
            System.out.println("Current Status = " + user.getUserIsActive());

            WebTarget target = getClient()
                    .target(BASE_URL + "/toggleUserStatus")
                    .queryParam("userId", user.getUserId())
                    .queryParam("status", user.getUserIsActive());

            Response response = target.request().put(
                    jakarta.ws.rs.client.Entity.text("")
            );

            System.out.println("API Status = " + response.getStatus());

            loadUsersPage();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ===============================================
        Manage Skills
    ===================================================*/
    public void loadSkillsPage() {

        if (!FacesContext.getCurrentInstance().isPostback()) {
            activeTab = "PENDING";
            refreshSkillApprovals();
        }
    }

    public void loadSkillCategories() {

        try {

            WebTarget target = getClient()
                    .target(BASE_URL + "/getAllSkillCategories");

            skillCategoryList = target
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<Collection<Tblskillcategory>>() {
                    });

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void loadSkills() {

        try {

            WebTarget target = getClient()
                    .target(BASE_URL + "/getAllSkills");

            skillList = target
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<Collection<Tblskills>>() {
                    });

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void showCategories() {

        activeTab = "CATEGORY";

        refreshSkillApprovals();

    }

    public void showSkills() {

        activeTab = "SKILL";

        refreshSkillApprovals();
    }

    public void saveCategory() {

        try {

            if (categoryObj.getCategoryName() == null
                    || categoryObj.getCategoryName().trim().isEmpty()) {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Category name is required.",
                                null));

                return;
            }

            if (categoryObj.getCategoryId() == null) {

                getClient()
                        .target(BASE_URL + "/addSkillCategory")
                        .request()
                        .post(Entity.entity(
                                categoryObj,
                                MediaType.APPLICATION_JSON));

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Category added successfully.",
                                null));

            } else {

                getClient()
                        .target(BASE_URL + "/updateSkillCategory")
                        .request()
                        .put(Entity.entity(
                                categoryObj,
                                MediaType.APPLICATION_JSON));

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Category updated successfully.",
                                null));
            }

            categoryObj = new Tblskillcategory();

            refreshSkillApprovals();

        } catch (Exception e) {

            e.printStackTrace();

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Unable to save category.",
                            null));
        }
    }

    public void editCategory(Tblskillcategory category) {

        try {

            categoryObj = category;

            activeTab = "CATEGORY";

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void deleteCategory(Integer categoryId) {
        try {
            Response response = getClient()
                    .target(BASE_URL + "/deleteSkillCategory/" + categoryId)
                    .request()
                    .delete();

            if (response.getStatus() == 200) {
                refreshSkillApprovals();

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Category deleted successfully.", null));
            } else {
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Unable to delete category because skills may exist under it.", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Unable to delete category.", null));
        }
    }

    // -----------------Skills-----------------
    public void saveSkill() {

        try {

            if (skillObj.getSkillName() == null
                    || skillObj.getSkillName().trim().isEmpty()) {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Please enter Skill.",
                                null));

                return;
            }

            if (selectedCategoryId == null) {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Please select a category.",
                                null));

                return;
            }

            Tblskillcategory category
                    = new Tblskillcategory();

            category.setCategoryId(
                    selectedCategoryId);

            skillObj.setCategoryId(
                    category);

            if (skillObj.getSkillId() == null) {

                getClient()
                        .target(BASE_URL + "/addSkill")
                        .request()
                        .post(Entity.entity(
                                skillObj,
                                MediaType.APPLICATION_JSON));

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Skill added successfully.",
                                null));

            } else {

                getClient()
                        .target(BASE_URL + "/updateSkill")
                        .request()
                        .put(Entity.entity(
                                skillObj,
                                MediaType.APPLICATION_JSON));

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Skill updated successfully.",
                                null));
            }

            skillObj = new Tblskills();
            selectedCategoryId = null;
            refreshSkillApprovals();

        } catch (Exception e) {

            e.printStackTrace();

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Unable to save skill.",
                            null));
        }
    }

    public void editSkill(Tblskills skill) {

        try {

            skillObj = skill;

            if (skill.getCategoryId() != null) {

                selectedCategoryId
                        = skill.getCategoryId().getCategoryId();
            }

            activeTab = "SKILL";

            System.out.println("EDIT CLICKED");
            System.out.println("Skill ID = " + skillObj.getSkillId());
            System.out.println("Skill Name = " + skillObj.getSkillName());

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void deleteSkill(Integer skillId) {

        try {

            getClient()
                    .target(BASE_URL + "/deleteSkill/" + skillId)
                    .request()
                    .delete();

            refreshSkillApprovals();

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Skill deleted successfully.",
                            null));

        } catch (Exception e) {

            e.printStackTrace();

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Unable to delete skill.",
                            null));
        }
    }

    public String getUserNameById(Integer userId) {
        if (userId == null) {
            return "-";
        }

        try {
            for (Tblusers user : getClient()
                    .target(BASE_URL + "/getAllUsers")
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<Collection<Tblusers>>() {
                    })) {

                if (user != null && user.getUserId() != null
                        && user.getUserId().equals(userId)) {
                    return user.getUserName();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "User " + userId;
    }

    public String getUserRoleById(Integer userId) {
        if (userId == null) {
            return "-";
        }

        try {
            for (Tblusers user : getClient()
                    .target(BASE_URL + "/getAllUsers")
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<Collection<Tblusers>>() {
                    })) {

                if (user != null && user.getUserId() != null
                        && user.getUserId().equals(userId)
                        && user.getRoleId() != null) {
                    return user.getRoleId().getRoleName();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "User";
    }

    /* ===============================================
        Manage Companies
    ===================================================*/
    public void loadCompanies() {
        try {
            showCompanyForm = false;
            companyObj = new Tblcompany();

            WebTarget target = getClient().target(BASE_URL + "/getAllCompanies");
            Response response = target.request().get();

            if (response.getStatus() == 200) {
                companyList = response.readEntity(new GenericType<Collection<Tblcompany>>() {
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getActiveCompanyCount() {

        int count = 0;

        if (companyList != null) {

            for (Tblcompany company : companyList) {

                if (Boolean.TRUE.equals(company.getIsActive())) {
                    count++;
                }
            }
        }

        return count;
    }

    public void showAddCompanyForm() {
        companyObj = new Tblcompany();
        companyObj.setIsActive(true);
        showCompanyForm = true;
    }

    public void cancelCompanyForm() {
        companyObj = new Tblcompany();
        showCompanyForm = false;
    }

    public void saveCompany() {
        try {

            if (companyObj.getCompanyName() == null
                    || companyObj.getCompanyName().trim().isEmpty()) {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Error",
                                "Company Name is required"));

                return;
            }

            Response response;

            if (companyObj.getCompanyId() == null) {

                response = getClient()
                        .target(BASE_URL + "/addCompany")
                        .request()
                        .post(Entity.entity(
                                companyObj,
                                MediaType.APPLICATION_JSON));

                if (response.getStatus() == 200) {

                    FacesContext.getCurrentInstance().addMessage(
                            null,
                            new FacesMessage(
                                    FacesMessage.SEVERITY_INFO,
                                    "Success",
                                    "Company added successfully"));
                }

            } else {

                response = getClient()
                        .target(BASE_URL + "/updateCompany")
                        .request()
                        .put(Entity.entity(
                                companyObj,
                                MediaType.APPLICATION_JSON));

                if (response.getStatus() == 200) {

                    FacesContext.getCurrentInstance().addMessage(
                            null,
                            new FacesMessage(
                                    FacesMessage.SEVERITY_INFO,
                                    "Success",
                                    "Company updated successfully"));
                }
            }

            companyObj = new Tblcompany();

            showCompanyForm = false;

            loadCompanies();

        } catch (Exception e) {

            e.printStackTrace();

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "Unable to save company"));
        }
    }

    public void editCompany(Tblcompany company) {
        try {

            companyObj = company;

            showCompanyForm = true;

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void deleteCompany(Integer companyId) {
        try {

            Response response = getClient()
                    .target(BASE_URL + "/deleteCompany/" + companyId)
                    .request()
                    .delete();

            if (response.getStatus() == 200) {

                loadCompanies();

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Success",
                                "Company deleted successfully"));

            } else {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Error",
                                "Unable to delete company"));
            }

        } catch (Exception e) {

            e.printStackTrace();

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "Unable to delete company"));
        }
    }

    public void toggleCompanyStatus(Tblcompany company) {
        try {

            Response response = getClient()
                    .target(BASE_URL + "/toggleCompanyStatus")
                    .queryParam("companyId", company.getCompanyId())
                    .queryParam("status", company.getIsActive())
                    .request()
                    .put(Entity.text(""));

            if (response.getStatus() == 200) {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Success",
                                "Company status updated successfully"));

                loadCompanies();

            } else {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Error",
                                "Unable to update company status"));
            }

        } catch (Exception e) {

            e.printStackTrace();

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "Unable to update company status"));
        }
    }

    /* ===============================================
        Jobs
    ===================================================*/
    public void loadJobs() {
        try {
            WebTarget target = getClient().target(BASE_URL + "/getAllJobs");

            jobList = target.request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<Collection<Tbljob>>() {
                    });

            jobList = new ArrayList<>(jobList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadJobDetails(Integer jobId) {
        try {

            WebTarget target = getClient().target(
                    BASE_URL + "/getJobByJobId/" + jobId);

            selectedJob = target.request(MediaType.APPLICATION_JSON)
                    .get(Tbljob.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getOpenJobCount() {

        if (jobList == null) {
            return 0;
        }

        return (int) jobList.stream()
                .filter(job -> job.getJobStatus() != null
                && job.getJobStatus().equalsIgnoreCase("Open"))
                .count();
    }

    /* ===============================================
        Profile
    ===================================================*/
    public void loadProfileData() {

        try {

            Integer userId = loginBean.getUserId();

            WebTarget target = getClient()
                    .target(BASE_URL + "/getAdminProfile/" + userId);

            userObj = target.request(MediaType.APPLICATION_JSON)
                    .get(Tblusers.class);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void updateProfile() {

        try {

            Response response = getClient()
                    .target(BASE_URL + "/updateAdminProfile")
                    .request()
                    .put(Entity.entity(
                            userObj,
                            MediaType.APPLICATION_JSON));

            if (response.getStatus() == 200) {

                FacesContext.getCurrentInstance()
                        .addMessage(
                                null,
                                new FacesMessage(
                                        FacesMessage.SEVERITY_INFO,
                                        "Profile updated successfully",
                                        null));

                loadProfileData();

            } else {

                FacesContext.getCurrentInstance()
                        .addMessage(
                                null,
                                new FacesMessage(
                                        FacesMessage.SEVERITY_ERROR,
                                        "Unable to update profile",
                                        null));
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void changePassword() {

        try {
            System.out.println("JSF METHOD CALLED");

            if (!newPassword.equals(confirmPassword)) {

                FacesContext.getCurrentInstance()
                        .addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                        "New password and confirm password do not match", null));
                return;
            }

            Response response = getClient()
                    .target(BASE_URL + "/changeAdminPassword")
                    .queryParam("userId", userObj.getUserId())
                    .queryParam("currentPassword", currentPassword)
                    .queryParam("newPassword", newPassword)
                    .request()
                    .put(Entity.text(""));

            String msg = response.readEntity(String.class);

            System.out.println("Response Status: " + response.getStatus());
            System.out.println("Response Body: " + msg);
            if (response.getStatus() == 200) {

                FacesContext.getCurrentInstance()
                        .addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));

                currentPassword = null;
                newPassword = null;
                confirmPassword = null;

            } else {

                FacesContext.getCurrentInstance()
                        .addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadProfilePhoto() {

        try {

            if (profilePhoto == null
                    || profilePhoto.getSize() == 0) {

                FacesContext.getCurrentInstance()
                        .addMessage(
                                null,
                                new FacesMessage(
                                        FacesMessage.SEVERITY_ERROR,
                                        "Please select photo",
                                        null));

                return;
            }

            String basePath
                    = "D:/QuickHireUploads/profilephotos/";

            Files.createDirectories(Paths.get(basePath));

            String fileName
                    = Paths.get(profilePhoto.getSubmittedFileName())
                            .getFileName()
                            .toString();

            String uniqueName
                    = System.currentTimeMillis()
                    + "_ADMIN_"
                    + fileName;

            Files.copy(
                    profilePhoto.getInputStream(),
                    Paths.get(basePath + uniqueName),
                    StandardCopyOption.REPLACE_EXISTING);

            Response response = getClient()
                    .target(BASE_URL + "/uploadProfilePhoto")
                    .queryParam(
                            "userId",
                            userObj.getUserId())
                    .queryParam(
                            "photo",
                            uniqueName)
                    .request()
                    .put(Entity.text(""));

            if (response.getStatus() == 200) {

                userObj.setProfilePhoto(uniqueName);

                FacesContext.getCurrentInstance()
                        .addMessage(
                                null,
                                new FacesMessage(
                                        FacesMessage.SEVERITY_INFO,
                                        "Profile photo uploaded successfully",
                                        null));

                loadProfileData();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public String getProfilePhotoPath() {

        try {

            if (userObj != null
                    && userObj.getProfilePhoto() != null
                    && !userObj.getProfilePhoto().isEmpty()) {

                return userObj.getProfilePhoto();
            }

        } catch (Exception e) {
        }

        return "default-user.png";
    }

    /* ===============================================
        Notifications
    ===================================================*/
    // ================= NOTIFICATIONS =================
    private List<Tblnotification> notificationList = new ArrayList<>();
    private String notificationFilter = "ALL";

    // ================= NOTIFICATION METHODS =================
    public void loadNotificationsPage() {

        int adminId = getLoggedInAdminId();

        if (adminId == 0) {
            notificationList = new ArrayList<>();
            notificationFilter = "ALL";
            return;
        }

        notificationList = loadAllNotifications(adminId);
        notificationFilter = "ALL";
    }

    public List<Tblnotification> loadAllNotifications(int adminId) {

        try {

            return getClient()
                    .target(BASE_URL + "/getAdminNotifications")
                    .queryParam("adminId", adminId)
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<List<Tblnotification>>() {
                    });

        } catch (Exception e) {

            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Tblnotification> loadUnreadNotifications(int adminId) {

        try {

            return getClient()
                    .target(BASE_URL + "/getAdminUnreadNotifications")
                    .queryParam("adminId", adminId)
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<List<Tblnotification>>() {
                    });

        } catch (Exception e) {

            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void applyFilter(String filter) {

        int adminId = getLoggedInAdminId();

        if (adminId == 0) {
            return;
        }

        notificationFilter = filter;

        switch (filter.toUpperCase()) {

            case "UNREAD":
                notificationList = loadUnreadNotifications(adminId);
                break;

            case "ALL":
            default:
                notificationList = loadAllNotifications(adminId);
                break;
        }
    }

    public void markNotificationAsRead(int notificationId) {

        try {

            Response response = getClient()
                    .target(BASE_URL + "/markNotificationAsRead")
                    .queryParam("notificationId", notificationId)
                    .request()
                    .put(jakarta.ws.rs.client.Entity.text(""));

            if (response.getStatus() == 200) {

                int adminId = getLoggedInAdminId();

                if ("UNREAD".equalsIgnoreCase(notificationFilter)) {
                    notificationList = loadUnreadNotifications(adminId);
                } else {
                    notificationList = loadAllNotifications(adminId);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public List<Tblnotification> getRecentNotifications() {

        if (notificationList == null) {
            return new ArrayList<>();
        }

        return notificationList.stream()
                .limit(5)
                .toList();
    }

    public int getUnreadNotificationCount() {

        if (notificationList == null) {
            return 0;
        }

        return (int) notificationList.stream()
                .filter(n -> n != null && !n.getIsRead())
                .count();
    }

    public String getNotificationBadge(Tblnotification n) {

        if (n == null || n.getNotificationType() == null) {
            return "alert";
        }

        String type = n.getNotificationType().toUpperCase();

        if (type.contains("JOB")) {
            return "job";
        }

        if (type.contains("COMPANY")) {
            return "company";
        }

        if (type.contains("CANDIDATE")) {
            return "candidate";
        }

        if (type.contains("RECRUITER")) {
            return "recruiter";
        }

        return "alert";
    }

    //skills
    public void refreshSkillApprovals() {
        loadPendingSkills();
        loadPendingCategories();
        loadApprovedSkills();
        loadApprovedCategories();
        loadSkillCategories();
        loadSkills();
    }

    public void showPendingApprovals() {
        activeTab = "PENDING";
        refreshSkillApprovals();
    }

    public void loadPendingSkills() {
        try {
            pendingSkillList = getClient()
                    .target(BASE_URL + "/pendingSkills")
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<Collection<Tblskills>>() {
                    });
        } catch (Exception e) {
            e.printStackTrace();
            pendingSkillList = new ArrayList<>();
        }
    }

    public void loadPendingCategories() {
        try {
            pendingCategoryList = getClient()
                    .target(BASE_URL + "/pendingCategories")
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<Collection<Tblskillcategory>>() {
                    });
        } catch (Exception e) {
            e.printStackTrace();
            pendingCategoryList = new ArrayList<>();
        }
    }

    public void loadApprovedSkills() {
        try {
            approvedSkillList = getClient()
                    .target(BASE_URL + "/approvedSkills")
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<Collection<Tblskills>>() {
                    });
        } catch (Exception e) {
            e.printStackTrace();
            approvedSkillList = new ArrayList<>();
        }
    }

    public void loadApprovedCategories() {
        try {
            approvedCategoryList = getClient()
                    .target(BASE_URL + "/approvedCategories")
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<Collection<Tblskillcategory>>() {
                    });
        } catch (Exception e) {
            e.printStackTrace();
            approvedCategoryList = new ArrayList<>();
        }
    }

    public void approveSkill(Integer skillId) {
        callSkillAction("/approveSkill", "skillId", skillId, "Skill approved successfully.");
    }

    public void rejectSkill(Integer skillId) {
        callSkillAction("/rejectSkill", "skillId", skillId, "Skill disapproved successfully.");
    }

    public void approveCategory(Integer categoryId) {
        callSkillAction("/approveCategory", "categoryId", categoryId, "Category approved successfully.");
    }

    public void rejectCategory(Integer categoryId) {
        callSkillAction("/rejectCategory", "categoryId", categoryId, "Category disapproved successfully.");
    }

    private void callSkillAction(String path, String idName, Integer id, String successMessage) {
        try {
            Response response = getClient()
                    .target(BASE_URL + path)
                    .queryParam(idName, id)
                    .queryParam("adminUserId", getLoggedInAdminId())
                    .request()
                    .put(Entity.text(""));

            handleActionResponse(response, successMessage);
        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Action failed.");
        }
    }

    private void handleActionResponse(Response response, String successMessage) {
        if (response.getStatus() == 200) {
            addMessage(FacesMessage.SEVERITY_INFO, successMessage);
            refreshSkillApprovals();
        } else {
            addMessage(FacesMessage.SEVERITY_ERROR, response.readEntity(String.class));
        }
    }

    private void addMessage(FacesMessage.Severity severity, String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, message, null));
    }

    public List<Tblnotification> getNotificationList() {
        return notificationList;
    }

    public void setNotificationList(List<Tblnotification> notificationList) {
        this.notificationList = notificationList;
    }

    public String getNotificationFilter() {
        return notificationFilter;
    }

    public void setNotificationFilter(String notificationFilter) {
        this.notificationFilter = notificationFilter;
    }

    // ================= GETTERS & SETTERS =================
    public LoginCDIBean getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(LoginCDIBean loginBean) {
        this.loginBean = loginBean;
    }

    public Collection<Tblrecruiters> getRecruiterList() {
        return recruiterList;
    }

    public void setRecruiterList(Collection<Tblrecruiters> recruiterList) {
        this.recruiterList = recruiterList;
    }

    public Collection<Tblcandidates> getCandidateList() {
        return candidateList;
    }

    public void setCandidateList(Collection<Tblcandidates> candidateList) {
        this.candidateList = candidateList;
    }

    public String getActiveTab() {
        return activeTab;
    }

    public void setActiveTab(String activeTab) {
        this.activeTab = activeTab;
    }

    public Collection<Tblskillcategory> getSkillCategoryList() {
        return skillCategoryList;
    }

    public void setSkillCategoryList(Collection<Tblskillcategory> skillCategoryList) {
        this.skillCategoryList = skillCategoryList;
    }

    public Collection<Tblskills> getSkillList() {
        return skillList;
    }

    public void setSkillList(Collection<Tblskills> skillList) {
        this.skillList = skillList;
    }

    public Tblskillcategory getCategoryObj() {
        return categoryObj;
    }

    public void setCategoryObj(Tblskillcategory categoryObj) {
        this.categoryObj = categoryObj;
    }

    public Tblskills getSkillObj() {
        return skillObj;
    }

    public void setSkillObj(Tblskills skillObj) {
        this.skillObj = skillObj;
    }

    public Integer getSelectedCategoryId() {
        return selectedCategoryId;
    }

    public void setSelectedCategoryId(Integer selectedCategoryId) {
        this.selectedCategoryId = selectedCategoryId;
    }

    public Collection<Tblcompany> getCompanyList() {
        return companyList;
    }

    public void setCompanyList(Collection<Tblcompany> companyList) {
        this.companyList = companyList;
    }

    public Tblcompany getCompanyObj() {
        return companyObj;
    }

    public void setCompanyObj(Tblcompany companyObj) {
        this.companyObj = companyObj;
    }

    public boolean isShowCompanyForm() {
        return showCompanyForm;
    }

    public void setShowCompanyForm(boolean showCompanyForm) {
        this.showCompanyForm = showCompanyForm;
    }

    public Collection<Tbljob> getJobList() {
        return jobList;
    }

    public void setJobList(Collection<Tbljob> jobList) {
        this.jobList = jobList;
    }

    public Tbljob getSelectedJob() {
        return selectedJob;
    }

    public void setSelectedJob(Tbljob selectedJob) {
        this.selectedJob = selectedJob;
    }

    public Tblusers getUserObj() {
        return userObj;
    }

    public void setUserObj(Tblusers userObj) {
        this.userObj = userObj;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public Part getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(Part profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public Collection<Tblskills> getPendingSkillList() {
        return pendingSkillList;
    }

    public void setPendingSkillList(Collection<Tblskills> pendingSkillList) {
        this.pendingSkillList = pendingSkillList;
    }

    public Collection<Tblskillcategory> getPendingCategoryList() {
        return pendingCategoryList;
    }

    public void setPendingCategoryList(Collection<Tblskillcategory> pendingCategoryList) {
        this.pendingCategoryList = pendingCategoryList;
    }

    public Collection<Tblskills> getApprovedSkillList() {
        return approvedSkillList;
    }

    public void setApprovedSkillList(Collection<Tblskills> approvedSkillList) {
        this.approvedSkillList = approvedSkillList;
    }

    public Collection<Tblskillcategory> getApprovedCategoryList() {
        return approvedCategoryList;
    }

    public void setApprovedCategoryList(Collection<Tblskillcategory> approvedCategoryList) {
        this.approvedCategoryList = approvedCategoryList;
    }

    public Collection<Tblusers> getUserList() {
        return userList;
    }

    public void setUserList(Collection<Tblusers> userList) {
        this.userList = userList;
    }

    public Collection<Tblapplication> getApplicationList() {
        return applicationList;
    }

    public void setApplicationList(Collection<Tblapplication> applicationList) {
        this.applicationList = applicationList;
    }

    public Collection<Tblinterview> getInterviewList() {
        return interviewList;
    }

    public void setInterviewList(Collection<Tblinterview> interviewList) {
        this.interviewList = interviewList;
    }

}
