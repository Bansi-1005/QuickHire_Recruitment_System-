/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package EJB;

import Entity.*;
import jakarta.ejb.Local;
import java.util.Collection;

/**
 *
 * @author tejan
 */
@Local
public interface AdminBeanLocal {
    // ================= AUTH =================
    //Tblusers adminLogin(String email, String password);

    // ================= ROLE =================
//    void addRole(Tblrolemaster role);
//    Collection<Tblrolemaster> getRoles();

    // ================= USER =================
    Collection<Tblusers> getAllUsers();
    void updateUserStatus(int userId, String userStatus);
    Collection<Tblusers> searchUsersByEmail(String userEmail);
    public void toggleUserStatus(int userId, boolean status);


    // ================= RECRUITER =================
    public Collection<Tblrecruiters> getAllRecruiters();     
    // ================= CANDIDATE =================
    Collection<Tblcandidates> getAllCandidates();
    
    
    // ================= Manage Skills =================
    
    Collection<Tblskillcategory> getAllSkillCategories();
    void addSkillCategory(Tblskillcategory category);
    void updateSkillCategory(Tblskillcategory category);
    void deleteSkillCategory(Integer categoryId);

    Collection<Tblskills> getAllSkills();
    void addSkill(Tblskills skill);
    void updateSkill(Tblskills skill);
    void deleteSkill(Integer skillId);

    // ================= COMPANY =================
//    void addCompany(Tblcompany company);
//    Collection<Tblcompany> getAllCompanies();
    
    void addCompany(Tblcompany company);
    void updateCompany(Tblcompany company);
    void deleteCompany(Integer companyId);
    Tblcompany findCompanyById(Integer companyId);
    Collection<Tblcompany> getAllCompanies();
    public void toggleCompanyStatus(Integer companyId, boolean status);

    // ================= JOB =================
    Collection<Tbljob> getAllJobs();
    public Tbljob getJobByJobId(Integer jobId);
//    void updateJobStatus(int jobId, String jobStatus);
//    void approveJob(int jobId);
//    void rejectJob(int jobId);
//    Collection<Tbljob> searchJobsByTitle(String jobTitle);

    
    // ========================Profile=======================
    public Tblusers getAdminProfile(Integer userId);
    public void updateAdminProfile(Tblusers user);
    public String changeAdminPassword(Integer userId, String currentPassword, String newPassword);
    public void uploadProfilePhoto(Integer userId, String photo);
    
    
    // ================= APPLICATION =================
//    Collection<Tblapplication> getAllApplications();
//    void updateApplicationStatus(int applicationId, String applicationStatus);

    // ================= NOTIFICATION =================
//    void sendNotification(Tblnotification notification);

    // ================= DASHBOARD =================
//    int totalUsers();
//    int totalJobs();
//    int totalApplications();
//    int totalCandidates();
//    int totalCompanies();
    
    // ================= REPORTS =================
//    Collection<Tblapplication> applicationsPerJob(int jobId);
//    Collection<Tbljob> jobsPerCompany(int companyId);
//    Collection<Tblapplication> selectedApplications();
//    Collection<Object[]> jobWiseApplicationReport();
}
