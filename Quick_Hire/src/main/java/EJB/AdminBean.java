/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author tejan
 */
@Stateless
public class AdminBean implements AdminBeanLocal {
    @PersistenceContext(unitName = "jpu")
    EntityManager em;

    // ================= AUTH =================
//    @Override
//    public Tblusers adminLogin(String email, String password) {
//        try {
//            return em.createNamedQuery("Tblusers.adminLogin", Tblusers.class)
//                    .setParameter("email", email)
//                    .setParameter("password", password)
//                    .getSingleResult();
//        } catch (Exception e) {
//            return null;
//        }
//    }

       // ================= ROLE =================
    @Override
    public void addRole(Tblrolemaster role) {
        em.persist(role);
    }

    @Override
    public Collection<Tblrolemaster> getRoles() {
        return em.createNamedQuery("Tblrolemaster.findAll", Tblrolemaster.class)
                .getResultList();
    }

    // ================= USER =================
    @Override
    public Collection<Tblusers> getAllUsers() {
        return em.createNamedQuery("Tblusers.findAll", Tblusers.class)
                .getResultList();
    }

    @Override
    public void updateUserStatus(int userId, String userStatus) {
        Tblusers user = em.find(Tblusers.class, userId);
        if (user != null) {
            user.setUserStatus(userStatus);
            user.setUpdatedDate(new Date());
            em.merge(user);
        }
    }

    @Override
    public Collection<Tblusers> searchUsersByEmail(String userEmail) {
        return em.createNamedQuery("Tblusers.findByUserEmail", Tblusers.class)
//                .setParameter("userEmail", "%" + userEmail + "%")
                .setParameter("userEmail", userEmail)
                .getResultList();

//        return em.createQuery(
//            "SELECT u FROM Tblusers u WHERE u.userEmail LIKE :email",
//            Tblusers.class)
//            .setParameter("email", "%" + userEmail + "%")
//            .getResultList();
    }

    // ================= CANDIDATE =================
    @Override
    public Collection<Tblcandidates> getAllCandidates() {
        return em.createNamedQuery("Tblcandidates.findAll", Tblcandidates.class)
                .getResultList();
    }

    @Override
    public void deleteCandidate(int candidateId) {
        Tblcandidates c = em.find(Tblcandidates.class, candidateId);
        if (c != null) {
            em.remove(c);
        }
    }

    // ================= COMPANY =================
    @Override
    public void addCompany(Tblcompany company) {
        company.setCreatedDate(new Date());
        company.setCompanyStatus("Pending");
        em.persist(company);
    }

    @Override
    public Collection<Tblcompany> getAllCompanies() {
        return em.createNamedQuery("Tblcompany.findAll", Tblcompany.class)
                .getResultList();
    }

    @Override
    public void approveCompany(int companyId) {
        Tblcompany c = em.find(Tblcompany.class, companyId);
        if (c != null) {
            c.setCompanyStatus("Approved");
            em.merge(c);
        }
    }

    @Override
    public void rejectCompany(int companyId) {
        Tblcompany c = em.find(Tblcompany.class, companyId);
        if (c != null) {
            c.setCompanyStatus("Rejected");
            em.merge(c);
        }
    }

    // ================= JOB =================
    @Override
    public Collection<Tbljob> getAllJobs() {
        return em.createNamedQuery("Tbljob.findAll", Tbljob.class)
                .getResultList();
    }

    @Override
    public void updateJobStatus(int jobId, String jobStatus) {
        Tbljob job = em.find(Tbljob.class, jobId);
        if (job != null) {
            job.setJobStatus(jobStatus);
            em.merge(job);
        }
    }

    @Override
    public void approveJob(int jobId) {
        updateJobStatus(jobId, "Approved");
    }

    @Override
    public void rejectJob(int jobId) {
        updateJobStatus(jobId, "Rejected");
    }

    @Override
    public Collection<Tbljob> searchJobsByTitle(String jobTitle) {
        return em.createNamedQuery("Tbljob.findByJobTitle", Tbljob.class)
//                .setParameter("jobTitle", "%" + jobTitle + "%")
                .setParameter("jobTitle", jobTitle)
                .getResultList();

//        return em.createQuery(
//            "SELECT j FROM Tbljob j WHERE j.jobTitle LIKE :title",
//            Tbljob.class)
//            .setParameter("jobTitle", "%" + jobTitle + "%")
//            .getResultList();
    }

    // ================= APPLICATION =================
    @Override
    public Collection<Tblapplication> getAllApplications() {
        return em.createNamedQuery("Tblapplication.findAll", Tblapplication.class)
                .getResultList();
    }

    @Override
    public void updateApplicationStatus(int applicationId, String applicationStatus) {
        Tblapplication app = em.find(Tblapplication.class, applicationId);
        if (app != null) {
            app.setApplicationStatus(applicationStatus);
            app.setLastUpdatedDate(new Date());
            em.merge(app);
        }
    }

    // ================= NOTIFICATION =================
    @Override
    public void sendNotification(Tblnotification notification) {
        notification.setCreatedDate(new Date());
        em.persist(notification);
    }

    // ================= DASHBOARD =================
    @Override
    public int totalUsers() {
        return ((Long) em.createNamedQuery("Tblusers.count").getSingleResult()).intValue();
    }

    @Override
    public int totalJobs() {
        return ((Long) em.createNamedQuery("Tbljob.count").getSingleResult()).intValue();
    }

    @Override
    public int totalApplications() {
        return ((Long) em.createNamedQuery("Tblapplication.count").getSingleResult()).intValue();
    }

    @Override
    public int totalCandidates() {
        return ((Long) em.createNamedQuery("Tblcandidates.count").getSingleResult()).intValue();
    }

    @Override
    public int totalCompanies() {
        return ((Long) em.createNamedQuery("Tblcompany.count").getSingleResult()).intValue();
    }
    
    // ================= REPORTS =================

    // Total applications for a specific job
    @Override
    public long applicationsPerJob(int jobId) {
        return em.createNamedQuery("Tblapplication.countByJob", Long.class)
                 .setParameter("jobId", jobId)
                 .getSingleResult();
    }

    // Total jobs posted by a company
    @Override
    public long jobsPerCompany(int companyId) {
        return em.createNamedQuery("Tbljob.countByCompany", Long.class)
                 .setParameter("companyId", companyId)
                 .getSingleResult();
    }

    // Total selected candidates
    @Override
    public long selectedApplicationsCount() {
        return em.createNamedQuery("Tblapplication.countSelected", Long.class)
                 .getSingleResult();
    }

    // Job-wise application report
    @Override
    public Collection<Object[]> jobWiseApplicationReport() {
        return em.createNamedQuery("Tbljob.jobWiseApplications", Object[].class)
                 .getResultList();
    }  
}
