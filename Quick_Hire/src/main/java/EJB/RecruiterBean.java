/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.*;
import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author RINKAL
 */

@Stateless
@DeclareRoles({"Admin","Recruiter","Candidate"})
public class RecruiterBean implements RecruiterBeanLocal {

    @PersistenceContext(unitName = "jpu")
    EntityManager em;
    @Inject Pbkdf2PasswordHash hash;
    @Override
    public void registerRecruiter(Tblusers user, Tblrecruiters recruiter) {
        try {
            Date now = new Date();
            
            // 🔐 STEP 1: Initialize hash (IMPORTANT)
            Map<String, String> params = new HashMap<>();
            params.put("Pbkdf2PasswordHash.Iterations", "3072");
            params.put("Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA256");

            hash.initialize(params);

            // 🔐 STEP 2: Hash password
            String hashedPassword = hash.generate(user.getUserPassword().toCharArray());

            // 🔐 STEP 3: Set hashed password
            user.setUserPassword(hashedPassword);
            
            
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
    @RolesAllowed("Recruiter")
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
//    @Override
//    public void updateJob(Tbljob job) {
//        try {
//            Tbljob existingJob = em.find(Tbljob.class, job.getJobId());
//
//            if (existingJob != null) {
//
//                // Preserve posted date
//                job.setJobPostedDate(existingJob.getJobPostedDate());
//
//                em.merge(job);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    
    @Override
    public void updateJob(Tbljob job) {
        try {
            Tbljob existingJob = em.find(Tbljob.class, job.getJobId());

            if (existingJob != null) {

                // Update ONLY if value is provided (NOT NULL)

                if (job.getJobTitle() != null)
                    existingJob.setJobTitle(job.getJobTitle());

                if (job.getJobDescription() != null)
                    existingJob.setJobDescription(job.getJobDescription());

                if (job.getJobLocation() != null)
                    existingJob.setJobLocation(job.getJobLocation());

                if (job.getJobStatus() != null)
                    existingJob.setJobStatus(job.getJobStatus());

                if (job.getJobType() != null)
                    existingJob.setJobType(job.getJobType());

                if (job.getExperienceRequired() != null)
                    existingJob.setExperienceRequired(job.getExperienceRequired());

                if (job.getJobCompensationType() != null)
                    existingJob.setJobCompensationType(job.getJobCompensationType());

                if (job.getJobExpiryDate() != null)
                    existingJob.setJobExpiryDate(job.getJobExpiryDate());

                job.setJobPostedDate(existingJob.getJobPostedDate());

                // recruiter update (important)
                if (job.getRecruiterId() != null) {
                    Tblrecruiters r = em.find(Tblrecruiters.class,job.getRecruiterId().getRecruiterId());
                    existingJob.setRecruiterId(r);
                } 
                em.merge(existingJob);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void deleteJob(int jobId) {
//        try {
//            Tbljob job = em.find(Tbljob.class, jobId);
//
//            if (job != null) {
//
//                // Step 1: Delete applications linked to this job
//                em.createQuery("DELETE FROM Tblapplication a WHERE a.jobId.jobId = :jobId")
//                  .setParameter("jobId", jobId)
//                  .executeUpdate();
//
//                // Step 2: Now delete job
//                em.remove(job);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        }
//    }
    
    
//    @Override
//    public void deleteJob(int jobId, int recruiterId) {
//        try {
//            Tbljob j = em.find(Tbljob.class, jobId);
//            Tblrecruiters r = em.find(Tblrecruiters.class, recruiterId);
//
//            if (j != null && r != null && r.getTbljobCollection() != null) {
//
//                // Step 1: Remove job from recruiter 
//                r.getTbljobCollection().remove(j);
//
//                // Step 2: Merge recruiter
//                em.merge(r);
//
//                // Step 3: Delete job
//                em.remove(em.merge(j));
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    @Override
    public void deleteJob(int jobId, int recruiterId) {
        try {
            Tbljob j = em.find(Tbljob.class, jobId);
            Tblrecruiters r = em.find(Tblrecruiters.class, recruiterId);

            if (j != null && r != null && r.getTbljobCollection() != null) {

                // ✅ Step 1: Remove Job ↔ Skills (tbljob_skills)
                if (j.getTblskillsCollection() != null) {
                    j.getTblskillsCollection().clear();
                }

                // ✅ Step 2: Remove Job ↔ Candidates (candidate_job)
                if (j.getTblcandidatesCollection() != null) {
                    j.getTblcandidatesCollection().clear();
                }

                // ✅ Step 3: Delete Applications (VERY IMPORTANT)
                if (j.getTblapplicationCollection() != null) {
                    for (Tblapplication app : j.getTblapplicationCollection()) {
                        em.remove(em.contains(app) ? app : em.merge(app));
                    }
                }

                // ✅ Step 4: Remove from recruiter
                r.getTbljobCollection().remove(j);
                em.merge(r);

                // ✅ Step 5: Finally delete job
                em.remove(em.contains(j) ? j : em.merge(j));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    
//single,multiple,all job delete logic:
    
//@Override
//public void deleteJob(Integer jobId, Collection<Integer> jobIds, int recruiterId) {
//    try {
//        Tblrecruiters r = em.find(Tblrecruiters.class, recruiterId);
//
//        if (r == null || r.getTbljobCollection() == null) return;
//
//        // 🔥 CASE 1: DELETE ALL JOBS
//        if (jobId != null && jobId == 0) {
//
//            Collection<Tbljob> jobs = new ArrayList<>(r.getTbljobCollection());
//
//            for (Tbljob j : jobs) {
//
//                // Remove Job ↔ Skills
//                if (j.getTblskillsCollection() != null) {
//                    j.getTblskillsCollection().clear();
//                }
//
//                // Remove Job ↔ Candidates
//                if (j.getTblcandidatesCollection() != null) {
//                    j.getTblcandidatesCollection().clear();
//                }
//
//                // Remove Applications
//                if (j.getTblapplicationCollection() != null) {
//                    for (Tblapplication app : j.getTblapplicationCollection()) {
//                        em.remove(em.contains(app) ? app : em.merge(app));
//                    }
//                }
//
//                // Remove from recruiter
//                r.getTbljobCollection().remove(j);
//
//                // Delete job
//                em.remove(em.contains(j) ? j : em.merge(j));
//            }
//
//            em.merge(r);
//        }
//
//        // 🔥 CASE 2: DELETE MULTIPLE JOBS
//        else if (jobIds != null && !jobIds.isEmpty()) {
//
//            for (Integer id : jobIds) {
//                Tbljob j = em.find(Tbljob.class, id);
//
//                if (j != null) {
//
//                    if (j.getTblskillsCollection() != null) {
//                        j.getTblskillsCollection().clear();
//                    }
//
//                    if (j.getTblcandidatesCollection() != null) {
//                        j.getTblcandidatesCollection().clear();
//                    }
//
//                    if (j.getTblapplicationCollection() != null) {
//                        for (Tblapplication app : j.getTblapplicationCollection()) {
//                            em.remove(em.contains(app) ? app : em.merge(app));
//                        }
//                    }
//
//                    r.getTbljobCollection().remove(j);
//
//                    em.remove(em.contains(j) ? j : em.merge(j));
//                }
//            }
//
//            em.merge(r);
//        }
//
//        // 🔥 CASE 3: DELETE SINGLE JOB
//        else if (jobId != null && jobId > 0) {
//
//            Tbljob j = em.find(Tbljob.class, jobId);
//
//            if (j != null) {
//
//                if (j.getTblskillsCollection() != null) {
//                    j.getTblskillsCollection().clear();
//                }
//
//                if (j.getTblcandidatesCollection() != null) {
//                    j.getTblcandidatesCollection().clear();
//                }
//
//                if (j.getTblapplicationCollection() != null) {
//                    for (Tblapplication app : j.getTblapplicationCollection()) {
//                        em.remove(em.contains(app) ? app : em.merge(app));
//                    }
//                }
//
//                r.getTbljobCollection().remove(j);
//                em.merge(r);
//
//                em.remove(em.contains(j) ? j : em.merge(j));
//            }
//        }
//
//    } catch (Exception e) {
//        e.printStackTrace();
//        throw e;
//    }
//}
    
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