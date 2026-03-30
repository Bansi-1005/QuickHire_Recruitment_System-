/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
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
        try {
            if (role == null) return;
            role.setCreatedDate(new Date());
            em.persist(role);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<Tblrolemaster> getRoles() {
        try {
            return em.createNamedQuery("Tblrolemaster.findAll", Tblrolemaster.class)
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // ================= USER =================
    @Override
    public Collection<Tblusers> getAllUsers() {
        try {
            return em.createNamedQuery("Tblusers.findAll", Tblusers.class)
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void updateUserStatus(int userId, String userStatus) {
        try {
            if (userStatus == null || userStatus.trim().isEmpty()) return;

            Tblusers user = em.find(Tblusers.class, userId);
            if (user != null) {
                user.setUserStatus(userStatus);
                user.setUpdatedDate(new Date());
                em.merge(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<Tblusers> searchUsersByEmail(String userEmail) {
        try {
            if (userEmail == null || userEmail.trim().isEmpty()) {
                return new ArrayList<>();
            }

            return em.createNamedQuery("Tblusers.findByUserEmail", Tblusers.class)
                    .setParameter("userEmail", userEmail)
                    .getResultList();
            
//            return em.createQuery(
//            "SELECT u FROM Tblusers u WHERE u.userEmail LIKE :email",
//            Tblusers.class)
//            .setParameter("email", "%" + userEmail + "%")
//            .getResultList();

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // ================= CANDIDATE =================
    @Override
    public Collection<Tblcandidates> getAllCandidates() {
        try {
            return em.createNamedQuery("Tblcandidates.findAll", Tblcandidates.class)
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteCandidate(int candidateId) {
        try {
            Tblcandidates c = em.find(Tblcandidates.class, candidateId);
            if (c != null) {
                em.remove(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= COMPANY =================
    @Override
    public void addCompany(Tblcompany company) {
        try {
            if (company == null) return;

            company.setCreatedDate(new Date());
            company.setCompanyStatus("Pending");

            em.persist(company);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<Tblcompany> getAllCompanies() {
        try {
            return em.createNamedQuery("Tblcompany.findAll", Tblcompany.class)
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void approveCompany(int companyId) {
        try {
            Tblcompany c = em.find(Tblcompany.class, companyId);
            if (c != null) {
                c.setCompanyStatus("Approved");
                em.merge(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rejectCompany(int companyId) {
        try {
            Tblcompany c = em.find(Tblcompany.class, companyId);
            if (c != null) {
                c.setCompanyStatus("Rejected");
                em.merge(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= JOB =================
    @Override
    public Collection<Tbljob> getAllJobs() {
        try {
            return em.createNamedQuery("Tbljob.findAll", Tbljob.class)
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void updateJobStatus(int jobId, String jobStatus) {
        try {
            if (jobStatus == null || jobStatus.trim().isEmpty()) return;

            Tbljob job = em.find(Tbljob.class, jobId);
            if (job != null) {
                job.setJobStatus(jobStatus);
                em.merge(job);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        try {
            if (jobTitle == null || jobTitle.trim().isEmpty()) {
                return new ArrayList<>();
            }

            return em.createNamedQuery("Tbljob.findByJobTitle", Tbljob.class)
                    .setParameter("jobTitle", jobTitle)
                    .getResultList();
            
//            return em.createQuery("SELECT j FROM Tbljob j WHERE j.jobTitle LIKE :title", Tbljob.class)
//                .setParameter("jobTitle", "%" + jobTitle + "%")
//                .getResultList();

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // ================= APPLICATION =================
    @Override
    public Collection<Tblapplication> getAllApplications() {
        try {
            return em.createNamedQuery("Tblapplication.findAll", Tblapplication.class)
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void updateApplicationStatus(int applicationId, String applicationStatus) {
        try {
            if (applicationStatus == null || applicationStatus.trim().isEmpty()) return;

            Tblapplication app = em.find(Tblapplication.class, applicationId);
            if (app != null) {
                app.setApplicationStatus(applicationStatus);
                app.setLastUpdatedDate(new Date());
                em.merge(app);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= NOTIFICATION =================
    @Override
    public void sendNotification(Tblnotification notification) {
        try {
            if (notification == null) return;

            notification.setCreatedDate(new Date());
            em.persist(notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= DASHBOARD =================
    @Override
    public int totalUsers() {
        try {
            return ((Long) em.createNamedQuery("Tblusers.count")
                    .getSingleResult()).intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int totalJobs() {
        try {
            return ((Long) em.createNamedQuery("Tbljob.count")
                    .getSingleResult()).intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int totalApplications() {
        try {
            return ((Long) em.createNamedQuery("Tblapplication.count")
                    .getSingleResult()).intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int totalCandidates() {
        try {
            return ((Long) em.createNamedQuery("Tblcandidates.count")
                    .getSingleResult()).intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int totalCompanies() {
        try {
            return ((Long) em.createNamedQuery("Tblcompany.count")
                    .getSingleResult()).intValue();
        } catch (Exception e) {
            return 0;
        }
    }
    
    // ================= REPORTS =================

    // Total applications for a specific job
    @Override
    public Collection<Tblapplication> applicationsPerJob(int jobId) {
        try {
//            return em.createNamedQuery("Tblapplication.countByJob", Long.class)
            return em.createNamedQuery("Tblapplication.FindApplicationsByJob", Tblapplication.class)
                    .setParameter("jobId", jobId)
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // Total jobs posted by a company
    @Override
    public Collection<Tbljob> jobsPerCompany(int companyId) {
        try {
//            return em.createNamedQuery("Tbljob.countByCompany", Long.class)
            return em.createNamedQuery("Tbljob.findJobsByCompany", Tbljob.class)
                    .setParameter("companyId", companyId)
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // Total selected candidates
    @Override
    public Collection<Tblapplication> selectedApplications() {
        try {
//            return em.createNamedQuery("Tblapplication.countSelected", Long.class)
            return em.createNamedQuery("Tblapplication.findSelectedApplication", Tblapplication.class)
                .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // Job-wise application report
    @Override
    public Collection<Object[]> jobWiseApplicationReport() {
        try {
            return em.createNamedQuery("Tbljob.jobWiseApplications", Object[].class)
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }  
}
