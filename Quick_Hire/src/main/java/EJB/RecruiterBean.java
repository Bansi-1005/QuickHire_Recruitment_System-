/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author RINKAL
 */

@Stateless
public class RecruiterBean implements RecruiterBeanLocal {

    @PersistenceContext(unitName = "jpu")
    EntityManager em;

    // ================= AUTH =================
//    @Override
//    public Tblusers recruiterLogin(String email, String password, int roleId) {
//        try {
//            return em.createNamedQuery("Tblusers.loginByRole", Tblusers.class)
//                    .setParameter("email", email)
//                    .setParameter("password", password)
//                    .setParameter("roleId", roleId)
//                    .getSingleResult();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    @Override
    public void registerRecruiter(Tblusers user, Tblrecruiters recruiter) {
        try {
            Date now = new Date();

            user.setCreatedDate(now);
            user.setUpdatedDate(now);
            user.setLastLoginDate(now);

            em.persist(user);

            recruiter.setUserId(user);
            recruiter.setCreatedDate(now);

            em.persist(recruiter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= PROFILE =================
    @Override
    public Tblrecruiters getProfile(int userId) {
        try {
            return em.createNamedQuery("Tblrecruiters.findByUser", Tblrecruiters.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void updateProfile(Tblrecruiters recruiter) {
        try {
            em.merge(recruiter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= COMPANY =================
    @Override
    public Tblcompany getCompanyDetails(int recruiterId) {
        try {
        return em.createNamedQuery("Tblrecruiters.findCompanyByRecruiterId", Tblcompany.class)
                .setParameter("recruiterId", recruiterId)
                .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ================= JOB MANAGEMENT =================
    @Override
    public void createJob(Tbljob job) {
        try {
            job.setJobPostedDate(new Date());
            em.persist(job);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void updateJob(Tbljob job) {
//        try {
//            em.merge(job);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    @Override
public void updateJob(Tbljob job) {
    try {
        Tbljob existingJob = em.find(Tbljob.class, job.getJobId());

        if (existingJob != null) {

            // Preserve posted date
            job.setJobPostedDate(existingJob.getJobPostedDate());

            em.merge(job);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
//    @Override
//    public void updateJob(Tbljob job) {
//        try {
//            Tbljob existingJob = em.find(Tbljob.class, job.getJobId());
//
//            if (existingJob != null) {
//
//                existingJob.setJobTitle(job.getJobTitle());
//                existingJob.setJobDescription(job.getJobDescription());
//                existingJob.setJobLocation(job.getJobLocation());
//                existingJob.setJobStatus(job.getJobStatus());
//                existingJob.setJobVacancies(job.getJobVacancies());
//
//                // handle recruiter safely
//                if (job.getRecruiterId() != null) {
//                    existingJob.setRecruiterId(job.getRecruiterId());
//                }
//
//                em.merge(existingJob);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e; // rethrow so REST can catch properly
//        }
//    }

//    @Override
//    public void deleteJob(int jobId) {
//        try {
//            Tbljob job = em.find(Tbljob.class, jobId);
//            if (job != null) em.remove(job);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
@Override
public void deleteJob(int jobId) {
    try {
        Tbljob job = em.find(Tbljob.class, jobId);

        if (job != null) {

            // Step 1: Delete applications linked to this job
            em.createQuery("DELETE FROM Tblapplication a WHERE a.jobId.jobId = :jobId")
              .setParameter("jobId", jobId)
              .executeUpdate();

            // Step 2: Now delete job
            em.remove(job);
        }

    } catch (Exception e) {
        e.printStackTrace();
        throw e;
    }
}
    @Override
    public void updateJobStatus(int jobId, String status) {
        try {
            Tbljob job = em.find(Tbljob.class, jobId);
            if (job != null) {
                job.setJobStatus(status);
                em.merge(job);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<Tbljob> getJobs(int recruiterId) {
        try {
            return em.createNamedQuery("Tbljob.findByRecruiter", Tbljob.class)
                    .setParameter("recruiterId", recruiterId)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ================= JOB SKILLS =================
    @Override
    public void addSkillToJob(int jobId, int skillId) {
        try {
            Tbljob job = em.find(Tbljob.class, jobId);
            Tblskills skill = em.find(Tblskills.class, skillId);

            if (job != null && skill != null) {
                job.getTblskillsCollection().add(skill);
                em.merge(job);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeSkillFromJob(int jobId, int skillId) {
        try {
            Tbljob job = em.find(Tbljob.class, jobId);
            Tblskills skill = em.find(Tblskills.class, skillId);

            if (job != null && skill != null) {
                job.getTblskillsCollection().remove(skill);
                em.merge(job);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<Tblskills> getJobSkills(int jobId) {
        try {
            Tbljob job = em.find(Tbljob.class, jobId);
            return (job != null) ? job.getTblskillsCollection() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ================= APPLICATION =================
    @Override
    public Collection<Tblapplication> getApplications(int jobId) {
        try {
            return em.createNamedQuery("Tblapplication.findByJob", Tblapplication.class)
                    .setParameter("jobId", jobId)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void updateApplicationStatus(int applicationId, String newStatus) {
        try {
            Tblapplication app = em.find(Tblapplication.class, applicationId);

            if (app != null) {
                String oldStatus = app.getApplicationStatus();

                app.setApplicationStatus(newStatus);
                app.setLastUpdatedDate(new Date());
                em.merge(app);

                addApplicationStatusHistory(applicationId, oldStatus, newStatus);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= APPLICATION HISTORY =================
    @Override
    public void addApplicationStatusHistory(int applicationId, String oldStatus, String newStatus) {
        try {
            Tblapplication app = em.find(Tblapplication.class, applicationId);

            if (app != null) {
                Tblapplicationstatushistory history = new Tblapplicationstatushistory();
                history.setApplicationId(app);
                history.setOldStatus(oldStatus);
                history.setNewStatus(newStatus);
                history.setStatusUpdatedDate(new Date());

                em.persist(history);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= SCREENING =================
    @Override
    public void generateScreeningScore(int applicationId) {
        try {
            Tblapplication app = em.find(Tblapplication.class, applicationId);
            if (app == null) return;

            Collection<Tblskills> candidateSkills = app.getCandidateId().getTblskillsCollection();
            Collection<Tblskills> jobSkills = app.getJobId().getTblskillsCollection();

            int match = 0;
            for (Tblskills js : jobSkills) {
                for (Tblskills cs : candidateSkills) {
                    if (js.getSkillId().equals(cs.getSkillId())) {
                        match++;
                    }
                }
            }

            double score = ((double) match / jobSkills.size()) * 100;

            Tblscreeningscore sc = new Tblscreeningscore();
            sc.setApplicationId(app);
            sc.setMatchingScore(BigDecimal.valueOf(score));
            sc.setScreeningLevel(score >= 70 ? "High" : score >= 40 ? "Medium" : "Low");
            sc.setScoreDate(new Date());

            em.persist(sc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Tblscreeningscore getScore(int applicationId) {
        try {
            return em.createNamedQuery("Tblscreeningscore.findByApplication", Tblscreeningscore.class)
                    .setParameter("applicationId", applicationId)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ================= SHORTLIST =================
    @Override
    public Collection<Tblapplication> getTopCandidates(int jobId) {
        try {
            return em.createNamedQuery("Tblapplication.findTopCandidates", Tblapplication.class)
                    .setParameter("jobId", jobId)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Collection<Tblapplication> filterCandidatesByScore(int jobId, double minScore) {
        try {
            return em.createNamedQuery("Tblapplication.filterByScore", Tblapplication.class)
                    .setParameter("jobId", jobId)
                    .setParameter("score", minScore)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ================= INTERVIEW =================
    @Override
    public void scheduleInterview(Tblinterview interview) {
        try {
            
            em.persist(interview);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateInterview(Tblinterview interview) {
        try {
            em.merge(interview);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateInterviewFeedback(int interviewId, String feedback, String result) {
        try {
            Tblinterview interview = em.find(Tblinterview.class, interviewId);
            if (interview != null) {
                interview.setFeedback(feedback);
                interview.setResult(result);
                em.merge(interview);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= NOTIFICATION =================
    @Override
    public void sendNotification(Tblnotification notification) {
        try {
            notification.setCreatedDate(new Date());
            em.persist(notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<Tblnotification> getRecruiterNotifications(int userId) {
        try {
            return em.createNamedQuery("Tblnotification.findByUser", Tblnotification.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}