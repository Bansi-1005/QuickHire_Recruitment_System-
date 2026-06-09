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

/**
 *
 * @author tejan
 */
@Named(value = "adminCDIBean")
@SessionScoped
public class AdminCDIBean implements Serializable {

    // ================= API =================
    private final String BASE_URL = "http://localhost:8080/Quick_Hire/resources/admin";
    
    @Inject private LoginCDIBean loginBean;
    
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
    
    
    
    
    
    
    
    public AdminCDIBean() {

    }
    
    private Client getClient() {
        return ClientBuilder.newBuilder()
                .register(new JwtClientFilter(loginBean.getToken())) // ✅ FIX: attach token
                .build();
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

            activeTab = "CATEGORY";

            loadSkillCategories();
            loadSkills();
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

        loadSkillCategories();
    }

    public void showSkills() {

        activeTab = "SKILL";

        loadSkills();
    }
    
    public void saveCategory() {

        try {

            if (categoryObj.getCategoryName() == null
                    || categoryObj.getCategoryName().trim().isEmpty()) {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_WARN,
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

            loadSkillCategories();
            loadSkills();

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

                loadSkillCategories();
                loadSkills();

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Category deleted successfully.",
                                null));

            } else {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Unable to delete category because skills may exist under it.",
                                null));
            }

        } catch (Exception e) {

            e.printStackTrace();

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Unable to delete category.",
                            null));
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
                               FacesMessage.SEVERITY_WARN,
                               "Please enter Skill.",
                               null));

               return;
           }

           if (selectedCategoryId == null) {

               FacesContext.getCurrentInstance().addMessage(
                       null,
                       new FacesMessage(
                               FacesMessage.SEVERITY_WARN,
                               "Please select a category.",
                               null));

               return;
           }

           Tblskillcategory category =
                   new Tblskillcategory();

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

           loadSkills();

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

                selectedCategoryId =
                        skill.getCategoryId().getCategoryId();
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

            loadSkills();

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
                companyList = response.readEntity(new GenericType<Collection<Tblcompany>>() {});
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
                    .get(new GenericType<Collection<Tbljob>>() {});

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
    
    private Tblusers userObj = new Tblusers();

    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

    private Part profilePhoto;

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

            String basePath =
                    "D:/QuickHireUploads/profilephotos/";

            Files.createDirectories(Paths.get(basePath));

            String fileName =
                    Paths.get(profilePhoto.getSubmittedFileName())
                            .getFileName()
                            .toString();

            String uniqueName =
                    System.currentTimeMillis()
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
    
}
