/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import jakarta.ejb.Stateless;
import Entity.*;
import jakarta.annotation.security.DeclareRoles;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import util.EmailServiceLocal;

/**
 *
 * @author tejan
 */

@Stateless
@DeclareRoles({"Admin","Recruiter","Candidate"})
public class CandidateBean implements CandidateBeanLocal {

    @PersistenceContext(unitName="jpu")
    EntityManager em;
    
    @EJB EmailServiceLocal emailService;
    
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

//    @Override
//    public void registerCandidate(Tblusers user, Tblcandidates candidate) {
//         try {
//             if (user == null || candidate == null) return;
//
//            Date now = new Date();
//            //  STEP 1: Initialize hash (IMPORTANT)
//            Map<String, String> params = new HashMap<>();
//            params.put("Pbkdf2PasswordHash.Iterations", "3072");
//            params.put("Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA256");
//
//            hash.initialize(params);
//
//            //  STEP 2: Hash password
//            String hashedPassword = hash.generate(user.getUserPassword().toCharArray());
//
//            //  STEP 3: Set hashed password
//            user.setUserPassword(hashedPassword);
//            user.setCreatedDate(now);
//            user.setUpdatedDate(now);
//            user.setLastLoginDate(now);
//
//            em.persist(user);
//
//            candidate.setUserId(user);
//            candidate.setResumeUploadDate(now);
//
//            em.persist(candidate);
//            
//            // send email to candidate 
//            String email = user.getUserEmail();
//
//            String subject = "Welcome to QuickHire";
//
//            String message = "Hello " + user.getUserName() + ",\n\n"
//                    + "Your account has been successfully created.\n"
//                    + "You can now apply for jobs.\n\n"
//                    + "Thank you!";
//
//            emailService.sendEmail(email, subject, message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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
            Tblcandidates existing = em.find(Tblcandidates.class, candidate.getCandidateId());

            if (existing != null) {

                // update candidate fields
                existing.setCandidatePhone(candidate.getCandidatePhone());
                existing.setCandidateCity(candidate.getCandidateCity());
                existing.setCandidateState(candidate.getCandidateState());
                existing.setCandidateArea(candidate.getCandidateArea());
                existing.setCandidateDOB(candidate.getCandidateDOB());
                existing.setCandidateExperience(candidate.getCandidateExperience());
                existing.setCandidateGender(candidate.getCandidateGender());

                // IMPORTANT: update USER separately
                if (candidate.getUserId() != null) {

                    Tblusers user = em.find(Tblusers.class,
                            candidate.getUserId().getUserId());

                    if (user != null) {
                        user.setUserName(candidate.getUserId().getUserName());
                        user.setUserEmail(candidate.getUserId().getUserEmail());

                        em.merge(user);
                    }
                }

                em.merge(existing);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    @Override
    public void uploadProfilePhoto(int userId, String profilePhoto) {

        try {

            Tblusers user = em.find(Tblusers.class, userId);

            if (user != null) {

                user.setProfilePhoto(profilePhoto);

                em.merge(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    

    // ================= RESUME =================
    
    @Override
    public Collection<Tblresume> getCandidateResumes(int candidateId) {

        return em.createQuery(
                "SELECT r FROM Tblresume r WHERE r.candidateId.candidateId = :cid ORDER BY r.uploadDate DESC",
                Tblresume.class
        )
        .setParameter("cid", candidateId)
        .getResultList();
    }
    
    @Override
    public void uploadResume(int candidateId, String resumeFile) {

        Tblresume r = new Tblresume();

        r.setResumeFile(resumeFile);

        r.setUploadDate(new Date());

        r.setIsActive(true);

        Tblcandidates c = em.find(Tblcandidates.class, candidateId);

        r.setCandidateId(c);

        em.persist(r);
    }
    
    @Override
    public void deleteResume(int resumeId) {

        Tblresume r = em.find(Tblresume.class, resumeId);

        if (r != null) {
            em.remove(r);
        }
    }
    
    @Override
    public void toggleResumeStatus(int resumeId, boolean status) {

        Tblresume r = em.find(Tblresume.class, resumeId);

        if (r != null) {

            r.setIsActive(status);

            em.merge(r);
        }
    }
    
//    @Override
//    public void uploadResume(int candidateId, String candidateResume) {
//        try {
//            Tblcandidates c = em.find(Tblcandidates.class, candidateId);
//
//            if (c != null) {
//                c.setCandidateResume(candidateResume);
//                c.setResumeUploadDate(new Date());
//                em.merge(c);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public String getCandidateResume(int candidateId) {
//       try {
//            Tblcandidates c = em.find(Tblcandidates.class, candidateId);
//            return (c != null) ? c.getCandidateResume() : null;
//        } catch (Exception e) {
//            return null;
//        }
//    }

    // ================= SKILLS =================
    
    @Override
    public Collection<Tblskillcategory> getAllSkillCategories() {

        try {

            return em.createNamedQuery(
                    "Tblskillcategory.findApprovedCategory",
                    Tblskillcategory.class
            ).getResultList();

        } catch (Exception e) {

            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public Collection<Tblskills> getSkillsByCategory(int categoryId) {

        try {

            return em.createNamedQuery(
                    "Tblskills.findApprovedSkillsByCategory",
                    Tblskills.class
            )
            .setParameter("categoryId", categoryId)
            .getResultList();

        } catch (Exception e) {

            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
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

//    @Override
//    public void updateSkillToCandidate(int candidateId, Collection<Integer> skillIds) {
//        try {
//            Tblcandidates c = em.find(Tblcandidates.class, candidateId);
//
//            if (c != null) {
//
//                // Create NEW list (better than clear)
//                Collection<Tblskills> newSkills = new ArrayList<>();
//
//                for (Integer skillId : skillIds) {
//                    Tblskills s = em.find(Tblskills.class, skillId);
//                    if (s != null) {
//                        newSkills.add(s);
//                    }
//                }
//
//                // Replace entire collection
//                c.setTblskillsCollection(newSkills);
//
//                em.merge(c);
//                em.flush(); // ensure DB update
//
//                System.out.println("Skills Updated: " + newSkills.size());
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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
    
    


    // ================= EDUCATION =================
    @Override
    public Collection<Tblcandidateeducation> getCandidateEducation(Integer candidateId) {

        if (candidateId == null) {
            return new ArrayList<>();
        }

        try {
            return em.createQuery(
                    "SELECT e FROM Tblcandidateeducation e WHERE e.candidateId.candidateId = :cid",
                    Tblcandidateeducation.class
            )
            .setParameter("cid", candidateId)
            .getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public Tblcandidateeducation addCandidateEducation(Tblcandidateeducation edu, Integer candidateId) {

        if (edu == null || candidateId == null) {
            return null;
        }

        Tblcandidates candidate = em.find(Tblcandidates.class, candidateId);

        if (candidate == null) {
            return null;
        }

        edu.setCandidateId(candidate);

        em.persist(edu);
        return edu;
    }

    @Override
    public Tblcandidateeducation updateCandidateEducation(Tblcandidateeducation edu) {

        if (edu == null || edu.getCandidateEducationId() == null) {
            return null;
        }

        Tblcandidateeducation existing =
                em.find(Tblcandidateeducation.class, edu.getCandidateEducationId());

        if (existing == null) {
            return null;
        }

        existing.setEducationName(edu.getEducationName());
        existing.setInstituteName(edu.getInstituteName());
        existing.setSpecialization(edu.getSpecialization());
        existing.setStartYear(edu.getStartYear());
        existing.setEndYear(edu.getEndYear());
        existing.setPercentage(edu.getPercentage());
        existing.setCgpa(edu.getCgpa());
        existing.setGrade(edu.getGrade());
        existing.setEducationDescription(edu.getEducationDescription());

        return em.merge(existing);
    }
    
    @Override
    public void removeCandidateEducation(Integer candidateEducationId) {

        if (candidateEducationId == null) return;

        Tblcandidateeducation edu =
                em.find(Tblcandidateeducation.class, candidateEducationId);

        if (edu != null) {
            em.remove(edu);
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

//    @Override
//    public Collection<Tbljob> searchJobsByLocation(String jobLocation) {
//        try {
//            return em.createNamedQuery("Tbljob.findByLocation", Tbljob.class)
//                    .setParameter("jobLocation", "%" + jobLocation + "%")
//                    .getResultList();
//        } catch (Exception e) {
//            return new ArrayList<>();
//        }
//    }
//
//    
//    
//    @Override
//    public Collection<Tbljob> searchJobsBySkill(String skillName) {
//        try {
//            return em.createNamedQuery(
//                    "Tbljob.findBySkill",
//                    Tbljob.class)
//                    .setParameter("skillName", "%" + skillName + "%")
//                    .getResultList();
//        } catch (Exception e) {
//            return new ArrayList<>();
//        }
//    }

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
    public String applyForJob(int candidateId, int jobId, int resumeId) {
        try {

            if (alreadyApplied(candidateId, jobId)) {
                return "Already Applied";
            }

            Tblcandidates candidate =
                    em.find(Tblcandidates.class, candidateId);

            Tbljob job =
                    em.find(Tbljob.class, jobId);

            Tblresume resume =
                    em.find(Tblresume.class, resumeId);

            if (candidate == null) {
                return "Candidate Not Found";
            }

            if (job == null) {
                return "Job Not Found";
            }

            if (resume == null) {
                return "Resume Not Found";
            }

            if (resume.getCandidateId() == null
                    || resume.getCandidateId().getCandidateId() == null
                    || !resume.getCandidateId()
                            .getCandidateId()
                            .equals(candidateId)) {

                return "Invalid Resume";
            }

            if (!Boolean.TRUE.equals(resume.getIsActive())) {
                return "Resume Not Active";
            }

            Tblapplication application = new Tblapplication();

            application.setCandidateId(candidate);
            application.setJobId(job);
            application.setResumeId(resume);

            application.setApplicationStatus("Applied");
            application.setApplicationAppliedDate(new Date());
            application.setLastUpdatedDate(new Date());

            em.persist(application);
            em.flush();

            Tblrecruiters recruiter = job.getRecruiterId();

            if (recruiter != null
                    && recruiter.getUserId() != null) {

                Tblusers recruiterUser =
                        recruiter.getUserId();

                Tblusers candidateUser =
                        candidate.getUserId();

                Tblnotification notification =
                        new Tblnotification();

                notification.setUserId(recruiterUser);

                notification.setMessage(
                        candidateUser.getUserName()
                        + " applied for "
                        + job.getJobTitle());

                notification.setNotificationType(
                        "Application");

                notification.setNotificationStatus(
                        "Unread");

                notification.setCreatedDate(
                        new Date());

                em.persist(notification);

                try {

                    String recruiterEmail =
                            recruiterUser.getUserEmail();

                    String subject =
                            "New Job Application";

                    String message =
                            "Dear "
                            + recruiterUser.getUserName()
                            + ",\n\n"
                            + candidateUser.getUserName()
                            + " has applied for your job.\n\n"
                            + "Job Title : "
                            + job.getJobTitle()
                            + "\n"
                            + "Resume : "
                            + resume.getResumeFile()
                            + "\n\n"
                            + "Please login and review the application.\n\n"
                            + "Regards,\nQuickHire";

                    emailService.sendEmail(
                            recruiterEmail,
                            subject,
                            message);

                } catch (Exception mailEx) {

                    mailEx.printStackTrace();
                }
            }

            return "Applied Successfully";

        } catch (Exception e) {

            e.printStackTrace();

            return "Error";
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
//    @Override
//    public Tblapplication getApplicationDetails(int applicationId) {
//        try {
//            return em.find(Tblapplication.class, applicationId);
//        } catch (Exception e) {
//            return null;
//        }
//    }

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
    
    
    
   