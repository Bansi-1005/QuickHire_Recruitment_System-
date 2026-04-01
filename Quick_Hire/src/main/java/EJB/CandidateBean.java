/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import jakarta.ejb.Stateless;
import Entity.*;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tejan
 */

@Stateless
public class CandidateBean implements CandidateBeanLocal {

    @PersistenceContext(unitName="jpu")
    EntityManager em;
    @Inject Pbkdf2PasswordHash hash;
    // ================= AUTH =================
//    @Override
//    public Tblusers candidateLogin(String email, String password, int roleId) {
//        try {
//            return em.createNamedQuery("Tblusers.loginByRole", Tblusers.class)
//                    .setParameter("email", email)
//                    .setParameter("password", password)
//                    .setParameter("roleId", roleId)
//                    .getSingleResult();
//        } catch (Exception e) {
//            return null;
//        }
//    }

    @Override
    public void registerCandidate(Tblusers user, Tblcandidates candidate) {
         try {
             if (user == null || candidate == null) return;

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

            candidate.setUserId(user);
            candidate.setResumeUploadDate(now);

            em.persist(candidate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= PROFILE =================
    @Override
    public Tblcandidates getCandidateProfile(int userId) {
        try {
            return em.createNamedQuery("Tblcandidates.findByUser", Tblcandidates.class)
                    .setParameter("userId", userId)
                    .getResultList()
                    .stream()
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void updateCandidateProfile(Tblcandidates candidate) {
        try {
            candidate.setResumeUploadDate(new Date());
            em.merge(candidate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= RESUME =================
    @Override
    public void uploadResume(int candidateId, String candidateResume) {
        try {
            Tblcandidates c = em.find(Tblcandidates.class, candidateId);

            if (c != null) {
                c.setCandidateResume(candidateResume);
                c.setResumeUploadDate(new Date());
                em.merge(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getCandidateResume(int candidateId) {
       try {
            Tblcandidates c = em.find(Tblcandidates.class, candidateId);
            return (c != null) ? c.getCandidateResume() : null;
        } catch (Exception e) {
            return null;
        }
    }

    // ================= SKILLS =================
    @Override
    public void addSkillToCandidate(int candidateId, int skillId) {
        try {
            Tblcandidates c = em.find(Tblcandidates.class, candidateId);
            Tblskills s = em.find(Tblskills.class, skillId);

            if (c != null && s != null) {

                if (c.getTblskillsCollection() == null) {
                    c.setTblskillsCollection(new ArrayList<>());
                }

                if (!c.getTblskillsCollection().contains(s)) {
                    c.getTblskillsCollection().add(s);
                }

                em.merge(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSkillToCandidate(int candidateId, Collection<Integer> skillIds) {
        try {
            Tblcandidates c = em.find(Tblcandidates.class, candidateId);

            if (c != null) {

                // Create NEW list (better than clear)
                Collection<Tblskills> newSkills = new ArrayList<>();

                for (Integer skillId : skillIds) {
                    Tblskills s = em.find(Tblskills.class, skillId);
                    if (s != null) {
                        newSkills.add(s);
                    }
                }

                // Replace entire collection
                c.setTblskillsCollection(newSkills);

                em.merge(c);
                em.flush(); // ensure DB update

                System.out.println("Skills Updated: " + newSkills.size());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeSkillFromCandidate(int candidateId, int skillId) {
        try {
            Tblcandidates c = em.find(Tblcandidates.class, candidateId);
            Tblskills s = em.find(Tblskills.class, skillId);

            if (c != null && s != null && c.getTblskillsCollection() != null) {
                c.getTblskillsCollection().remove(s);
                em.merge(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<Tblskills> getCandidateSkills(int candidateId) {
        try {
            Tblcandidates c = em.find(Tblcandidates.class, candidateId);
            return (c != null) ? c.getTblskillsCollection() : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // ================= JOBS =================
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
    public Collection<Tbljob> searchJobsByLocation(String jobLocation) {
        try {
            return em.createNamedQuery("Tbljob.findByLocation", Tbljob.class)
                    .setParameter("jobLocation", "%" + jobLocation + "%")
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    
    
    @Override
    public Collection<Tbljob> searchJobsBySkill(String skillName) {
        try {
            return em.createNamedQuery(
                    "Tbljob.findBySkill",
                    Tbljob.class)
                    .setParameter("skillName", "%" + skillName + "%")
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // ================= APPLICATION =================
    @Override
    public boolean alreadyApplied(int candidateId, int jobId) {
        try {
            Long count = em.createNamedQuery("Tblapplication.countByCandidateAndJob", Long.class)
                    .setParameter("candidateId", candidateId)
                    .setParameter("jobId", jobId)
                    .getSingleResult();

            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public String applyForJob(Tblapplication application) {
        try {
            if (application == null || application.getCandidateId() == null || application.getJobId() == null) {
                return "Invalid Data";
            }

            int candidateId = application.getCandidateId().getCandidateId();
            int jobId = application.getJobId().getJobId();

            if (alreadyApplied(candidateId, jobId)) {
                return "Already Applied";
            }

            Tblcandidates candidate = em.find(Tblcandidates.class, candidateId);
            Tbljob job = em.find(Tbljob.class, jobId);

            if (candidate == null || job == null) {
                return "Candidate or Job not found";
            }

            application.setCandidateId(candidate);
            application.setJobId(job);

            application.setApplicationAppliedDate(new Date());
            application.setLastUpdatedDate(new Date());
            application.setApplicationStatus("Applied");

            em.persist(application);
            em.flush();

            return "Applied Successfully";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @Override
    public Collection<Tblapplication> getCandidateApplications(int candidateId) {
        try {
            return em.createNamedQuery("Tblapplication.findByCandidate", Tblapplication.class)
                    .setParameter("candidateId", candidateId)
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteApplication(int applicationId) {
        try {
            Tblapplication app = em.find(Tblapplication.class, applicationId);

            if (app != null) {
                em.remove(app);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= APPLICATION STATUS =================
    @Override
    public Tblapplication getApplicationDetails(int applicationId) {
        try {
            return em.find(Tblapplication.class, applicationId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getApplicationStatus(int applicationId) {
         try {
            Tblapplication app = em.find(Tblapplication.class, applicationId);
            return (app != null && app.getApplicationStatus() != null)
                    ? app.getApplicationStatus()
                    : "";
        } catch (Exception e) {
            return "";
        }
    }
    
    @Override
    public void updateApplicationStatus(int applicationId, String status) {
        try {
            if (status == null || status.trim().isEmpty()) return;

            Tblapplication app = em.find(Tblapplication.class, applicationId);

            if (app != null) {
                app.setApplicationStatus(status);
                app.setLastUpdatedDate(new Date());
                em.merge(app);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= SCREENING =================
    @Override
    public Tblscreeningscore getScreeningScore(int applicationId) {
        try {
            return em.createNamedQuery("Tblscreeningscore.findByApplication", Tblscreeningscore.class)
                    .setParameter("applicationId", applicationId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    // ================= INTERVIEW =================
    @Override
    public Collection<Tblinterview> getCandidateInterviews(int applicationId) {
        try {
            return em.createNamedQuery("Tblinterview.findByApplication", Tblinterview.class)
                    .setParameter("applicationId", applicationId)
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // ================= NOTIFICATION =================
    @Override
    public Collection<Tblnotification> getCandidateNotifications(int userId) {
         try {
            return em.createNamedQuery("Tblnotification.findByUser", Tblnotification.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
    
    
    
   