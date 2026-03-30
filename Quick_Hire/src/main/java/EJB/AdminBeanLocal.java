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
    void addRole(Tblrolemaster role);
    Collection<Tblrolemaster> getRoles();

    // ================= USER =================
    Collection<Tblusers> getAllUsers();
    void updateUserStatus(int userId, String userStatus);
    Collection<Tblusers> searchUsersByEmail(String userEmail);

    // ================= CANDIDATE =================
    Collection<Tblcandidates> getAllCandidates();
    void deleteCandidate(int candidateId);

    // ================= COMPANY =================
    void addCompany(Tblcompany company);
    Collection<Tblcompany> getAllCompanies();
    void approveCompany(int companyId);
    void rejectCompany(int companyId);

    // ================= JOB =================
    Collection<Tbljob> getAllJobs();
    void updateJobStatus(int jobId, String jobStatus);
    void approveJob(int jobId);
    void rejectJob(int jobId);
    Collection<Tbljob> searchJobsByTitle(String jobTitle);

    // ================= APPLICATION =================
    Collection<Tblapplication> getAllApplications();
    void updateApplicationStatus(int applicationId, String applicationStatus);

    // ================= NOTIFICATION =================
    void sendNotification(Tblnotification notification);

    // ================= DASHBOARD =================
    int totalUsers();
    int totalJobs();
    int totalApplications();
    int totalCandidates();
    int totalCompanies();
    
    // ================= REPORTS =================
    Collection<Tblapplication> applicationsPerJob(int jobId);
    Collection<Tbljob> jobsPerCompany(int companyId);
    Collection<Tblapplication> selectedApplications();
    Collection<Object[]> jobWiseApplicationReport();
}
