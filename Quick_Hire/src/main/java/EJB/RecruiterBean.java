/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.*;
import Validation.RecruiterValidator;
import jakarta.annotation.security.DeclareRoles;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.EmailServiceLocal;

/**
 *
 * @author RINKAL
 */
@Stateless
@DeclareRoles({"Admin", "Recruiter", "Candidate"})
public class RecruiterBean implements RecruiterBeanLocal {

    @PersistenceContext(unitName = "jpu")
    EntityManager em;

    @EJB
    private EmailServiceLocal emailService;

    @Inject
    Pbkdf2PasswordHash hash;

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
            Tblrecruiters existing = em.find(Tblrecruiters.class, recruiter.getRecruiterId());

            if (existing != null) {

                if (recruiter.getDesignation() != null) {
                    existing.setDesignation(recruiter.getDesignation());
                }

                if (recruiter.getRecruiterPhone() != null) {
                    existing.setRecruiterPhone(recruiter.getRecruiterPhone());
                }

                em.merge(existing);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= COMPANY =================
//    @Override
//    public Tblcompany getCompanyDetails(int recruiterId) {
//        try {
//            return em.createNamedQuery("Tblrecruiters.findCompanyByRecruiterId", Tblcompany.class)
//                    .setParameter("recruiterId", recruiterId)
//                    .getSingleResult();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
    // ================= JOB MANAGEMENT =================
    @Override
    public void createJob(Tbljob job, Collection<Integer> skillIds, Collection<Integer> educationIds) {

        try {

            RecruiterValidator.validateJob(job);

            job.setJobPostedDate(new Date());

            // ================= RECRUITER FETCH  =================
            Tblrecruiters recruiter = em.find(
                    Tblrecruiters.class,
                    job.getRecruiterId().getRecruiterId()
            );

            if (recruiter == null) {
                throw new RuntimeException("Recruiter not found");
            }

            job.setRecruiterId(recruiter);

            // ================= SKILLS =================
            Collection<Tblskills> managedSkills = new ArrayList<>();

            if (skillIds != null && !skillIds.isEmpty()) {

                for (Integer skillId : skillIds) {

                    Tblskills skill = em.getReference(Tblskills.class, skillId);

                    managedSkills.add(skill);
                }
            }

            job.setTblskillsCollection(managedSkills);

            // ================= EDUCATION =================
            Collection<Tbleducation> managedEducation
                    = new ArrayList<>();

            if (educationIds != null && !educationIds.isEmpty()) {

                for (Integer educationId : educationIds) {

                    Tbleducation education
                            = em.getReference(
                                    Tbleducation.class,
                                    educationId
                            );

                    managedEducation.add(
                            education
                    );
                }
            }

            job.setTbleducationCollection(
                    managedEducation
            );

            // ================= PERSIST JOB  =================
            em.persist(job);

            // ================= SAVE ACTIVITY =================
//            Tblnotification activity
//                    = new Tblnotification();
//
//            activity.setUserId(
//                    recruiter.getUserId()
//            );
//
//            activity.setMessage(
//                    "New job posted: "
//                    + job.getJobTitle()
//            );
//
//            activity.setCreatedDate(
//                    new Date()
//            );
//
//            activity.setNotificationStatus(
//                    "Read"
//            );
            // ================= SAVE ACTIVITY =================
            Tblnotification notification = new Tblnotification();

            notification.setSenderUserId(recruiter.getUserId());
            notification.setReceiverUserId(recruiter.getUserId());

            notification.setNotificationTitle("Job Posted");
            notification.setNotificationMessage(
                    "New job posted: " + job.getJobTitle()
            );

            notification.setNotificationType("JOB_POSTED");

            notification.setIsRead(false);
            notification.setCreatedDate(new Date());
            em.persist(notification);
//            em.persist(activity);

            // ================= SEND EMAIL =================
            Tblusers recruiterUser
                    = recruiter.getUserId();

            if (recruiterUser != null) {

                String email
                        = recruiterUser.getUserEmail();

                String subject
                        = "Job Posted Successfully";

                String message
                        = "Your job has been posted successfully.\n\n"
                        + "Job Title: "
                        + job.getJobTitle()
                        + "\n"
                        + "Location: "
                        + job.getJobLocation();

                emailService.sendEmail(
                        email,
                        subject,
                        message
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            throw e;
        }
    }

    @Override
    public void updateJob(Tbljob job, Collection<Integer> skillIds, Collection<Integer> educationIds) {

        try {
            if (job == null || job.getJobId() == null) {
                throw new RuntimeException("Job Not Found for Update");
            }
            RecruiterValidator.validateJob(job);

            Tbljob existingJob = em.find(Tbljob.class, job.getJobId());

            if (existingJob == null) {
                throw new RuntimeException("Job not found");
            }

            if (job.getRecruiterId() == null
                    || job.getRecruiterId().getRecruiterId() == null
                    || existingJob.getRecruiterId() == null
                    || !existingJob.getRecruiterId().getRecruiterId()
                            .equals(job.getRecruiterId().getRecruiterId())) {

                throw new RuntimeException("You are not allowed to edit this job");
            }

            existingJob.setJobTitle(job.getJobTitle());
            existingJob.setJobDescription(job.getJobDescription());
            existingJob.setJobLocation(job.getJobLocation());
            existingJob.setWorkMode(job.getWorkMode());
            existingJob.setJobCity(job.getJobCity());
            existingJob.setJobState(job.getJobState());
            existingJob.setExperienceRequired(job.getExperienceRequired());
            existingJob.setJobType(job.getJobType());
            existingJob.setJobCompensationType(job.getJobCompensationType());
            existingJob.setJobCompensationMin(job.getJobCompensationMin());
            existingJob.setJobCompensationMax(job.getJobCompensationMax());
            existingJob.setJobCompensationPeriod(job.getJobCompensationPeriod());
            existingJob.setJobVacancies(job.getJobVacancies());
            existingJob.setJobStatus(job.getJobStatus());
            existingJob.setJobExpiryDate(job.getJobExpiryDate());
            if (existingJob.getJobExpiryDate() != null
                    && existingJob.getJobExpiryDate().before(startOfToday())) {

                existingJob.setJobStatus("Closed");
            }
            Collection<Tblskills> managedSkills = new ArrayList<>();

            if (skillIds != null) {

                for (Integer skillId : skillIds) {

                    if (skillId == null) {
                        continue;
                    }

                    Tblskills skill = em.find(Tblskills.class, skillId);

                    if (skill == null) {
                        throw new RuntimeException(
                                "Selected skill not found: " + skillId
                        );
                    }

                    managedSkills.add(skill);
                }

                existingJob.setTblskillsCollection(managedSkills);
            }

// ================= EDUCATION =================
            Collection<Tbleducation> managedEducation = new ArrayList<>();

            if (educationIds != null) {

                for (Integer educationId : educationIds) {

                    if (educationId == null) {
                        continue;
                    }

                    Tbleducation education
                            = em.find(
                                    Tbleducation.class,
                                    educationId
                            );

                    if (education == null) {
                        throw new RuntimeException(
                                "Selected education not found: "
                                + educationId
                        );
                    }

                    managedEducation.add(education);
                }

                existingJob.setTbleducationCollection(
                        managedEducation
                );
            }

// ================= SAVE =================
            em.merge(existingJob);
            em.flush();

// ================= NOTIFICATION =================
            Tblusers recruiterUser
                    = existingJob.getRecruiterId().getUserId();

            Tblnotification notification
                    = new Tblnotification();

            notification.setSenderUserId(
                    recruiterUser
            );

            notification.setReceiverUserId(
                    recruiterUser
            );

            notification.setNotificationTitle(
                    "Job Updated"
            );

            notification.setNotificationMessage(
                    "Job updated: "
                    + existingJob.getJobTitle()
            );

            notification.setNotificationType(
                    "JOB_UPDATED"
            );

            notification.setIsRead(
                    false
            );

            notification.setCreatedDate(
                    new Date()
            );

            em.persist(notification);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void toggleJobStatus(int jobId, int recruiterId) {

        try {

            Tbljob job = em.createQuery(
                    "SELECT j FROM Tbljob j "
                    + "WHERE j.jobId = :jobId "
                    + "AND j.recruiterId.recruiterId = :recruiterId",
                    Tbljob.class
            )
                    .setParameter("jobId", jobId)
                    .setParameter("recruiterId", recruiterId)
                    .getSingleResult();

            String oldStatus = job.getJobStatus();
            String newStatus;

            if (job.getJobExpiryDate() == null) {

                throw new RuntimeException(
                        "Please update the expiry date before changing this job status."
                );
            }

            if (job.getJobExpiryDate().before(startOfToday())) {

                throw new RuntimeException(
                        "This job has expired. Please update the expiry date before changing its status."
                );
            }

            if ("Closed".equalsIgnoreCase(oldStatus)) {

                newStatus = "Open";

            } else if ("Open".equalsIgnoreCase(oldStatus)) {

                newStatus = "Closed";

            } else {

                throw new RuntimeException(
                        "Only Open and Closed jobs can be changed."
                );
            }

            job.setJobStatus(newStatus);
            em.merge(job);

            Tblusers recruiterUser
                    = job.getRecruiterId().getUserId();

            Tblnotification notification
                    = new Tblnotification();

            notification.setSenderUserId(recruiterUser);
            notification.setReceiverUserId(recruiterUser);

            notification.setNotificationTitle(
                    "Job Status Updated"
            );

            notification.setNotificationMessage(
                    "Job status changed from "
                    + oldStatus
                    + " to "
                    + newStatus
                    + ": "
                    + job.getJobTitle()
            );

            notification.setNotificationType(
                    "JOB_STATUS"
            );

            notification.setIsRead(false);

            notification.setCreatedDate(
                    new Date()
            );

            em.persist(notification);

        } catch (Exception e) {

            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void updateJobExpiryDate(int jobId, int recruiterId, Date expiryDate) {

        try {

            if (expiryDate == null) {
                throw new RuntimeException("Please select a valid expiry date.");
            }

            if (expiryDate.before(startOfToday())) {
                throw new RuntimeException("Expiry date cannot be in the past.");
            }

            Tbljob job = em.createQuery(
                    "SELECT j FROM Tbljob j "
                    + "WHERE j.jobId = :jobId "
                    + "AND j.recruiterId.recruiterId = :recruiterId",
                    Tbljob.class
            )
                    .setParameter("jobId", jobId)
                    .setParameter("recruiterId", recruiterId)
                    .getSingleResult();

            job.setJobExpiryDate(expiryDate);

            job.setJobStatus("Open");

            em.merge(job);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void closeExpiredJobs() {

        try {

            Collection<Tbljob> expiredJobs
                    = em.createQuery(
                            "SELECT j FROM Tbljob j "
                            + "WHERE j.jobStatus = 'Open' "
                            + "AND j.jobExpiryDate < :today",
                            Tbljob.class
                    )
                            .setParameter("today", startOfToday())
                            .getResultList();

            for (Tbljob job : expiredJobs) {

                job.setJobStatus("Closed");
                em.merge(job);

                Tblusers recruiterUser
                        = job.getRecruiterId().getUserId();

                Tblnotification notification
                        = new Tblnotification();

                notification.setSenderUserId(recruiterUser);
                notification.setReceiverUserId(recruiterUser);

                notification.setNotificationTitle(
                        "Job Auto Closed"
                );

                notification.setNotificationMessage(
                        "Job auto-closed after expiry: "
                        + job.getJobTitle()
                );

                notification.setNotificationType(
                        "JOB_STATUS"
                );

                notification.setIsRead(false);

                notification.setCreatedDate(
                        new Date()
                );

                em.persist(notification);
            }

        } catch (Exception e) {

            e.printStackTrace();
            throw e;
        }
    }

    private Date startOfToday() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }
//
//    @Override
//    public void deleteJob(int jobId, int recruiterId) {
//        try {
//            Tbljob j = em.find(Tbljob.class, jobId);
//            Tblrecruiters r = em.find(Tblrecruiters.class, recruiterId);
//
//            if (j != null && r != null && r.getTbljobCollection() != null) {
//
//                // Step 1: Remove Job ↔ Skills (tbljob_skills)
//                if (j.getTblskillsCollection() != null) {
//                    j.getTblskillsCollection().clear();
//                }
//
//                // Step 2: Remove Job ↔ Candidates (candidate_job)
//                if (j.getTblcandidatesCollection() != null) {
//                    j.getTblcandidatesCollection().clear();
//                }
//
//                // Step 3: Delete Applications (VERY IMPORTANT)
//                if (j.getTblapplicationCollection() != null) {
//                    for (Tblapplication app : j.getTblapplicationCollection()) {
//                        em.remove(em.contains(app) ? app : em.merge(app));
//                    }
//                }
//
//                // DELETE WEIGHTAGE MAPPING
//                em.createQuery(
//                        "DELETE FROM Tbljobskillweightage j WHERE j.jobId.jobId = :jobId"
//                )
//                        .setParameter("jobId", jobId)
//                        .executeUpdate();
//
//                // Step 4: Remove from recruiter
//                r.getTbljobCollection().remove(j);
//                em.merge(r);
//
//                // Step 5: Finally delete job
//                em.remove(em.contains(j) ? j : em.merge(j));
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        }
//    }
//

    ////single,multiple,all job delete logic:
////@Override
////public void deleteJob(Integer jobId, Collection<Integer> jobIds, int recruiterId) {
////    try {
////        Tblrecruiters r = em.find(Tblrecruiters.class, recruiterId);
////
////        if (r == null || r.getTbljobCollection() == null) return;
////
////        // 🔥 CASE 1: DELETE ALL JOBS
////        if (jobId != null && jobId == 0) {
////
////            Collection<Tbljob> jobs = new ArrayList<>(r.getTbljobCollection());
////
////            for (Tbljob j : jobs) {
////
////                // Remove Job ↔ Skills
////                if (j.getTblskillsCollection() != null) {
////                    j.getTblskillsCollection().clear();
////                }
////
////                // Remove Job ↔ Candidates
////                if (j.getTblcandidatesCollection() != null) {
////                    j.getTblcandidatesCollection().clear();
////                }
////
////                // Remove Applications
////                if (j.getTblapplicationCollection() != null) {
////                    for (Tblapplication app : j.getTblapplicationCollection()) {
////                        em.remove(em.contains(app) ? app : em.merge(app));
////                    }
////                }
////
////                // Remove from recruiter
////                r.getTbljobCollection().remove(j);
////
////                // Delete job
////                em.remove(em.contains(j) ? j : em.merge(j));
////            }
////
////            em.merge(r);
////        }
////
////        // 🔥 CASE 2: DELETE MULTIPLE JOBS
////        else if (jobIds != null && !jobIds.isEmpty()) {
////
////            for (Integer id : jobIds) {
////                Tbljob j = em.find(Tbljob.class, id);
////
////                if (j != null) {
////
////                    if (j.getTblskillsCollection() != null) {
////                        j.getTblskillsCollection().clear();
////                    }
////
////                    if (j.getTblcandidatesCollection() != null) {
////                        j.getTblcandidatesCollection().clear();
////                    }
////
////                    if (j.getTblapplicationCollection() != null) {
////                        for (Tblapplication app : j.getTblapplicationCollection()) {
////                            em.remove(em.contains(app) ? app : em.merge(app));
////                        }
////                    }
////
////                    r.getTbljobCollection().remove(j);
////
////                    em.remove(em.contains(j) ? j : em.merge(j));
////                }
////            }
////
////            em.merge(r);
////        }
////
////        // 🔥 CASE 3: DELETE SINGLE JOB
////        else if (jobId != null && jobId > 0) {
////
////            Tbljob j = em.find(Tbljob.class, jobId);
////
////            if (j != null) {
////
////                if (j.getTblskillsCollection() != null) {
////                    j.getTblskillsCollection().clear();
////                }
////
////                if (j.getTblcandidatesCollection() != null) {
////                    j.getTblcandidatesCollection().clear();
////                }
////
////                if (j.getTblapplicationCollection() != null) {
////                    for (Tblapplication app : j.getTblapplicationCollection()) {
////                        em.remove(em.contains(app) ? app : em.merge(app));
////                    }
////                }
////
////                r.getTbljobCollection().remove(j);
////                em.merge(r);
////
////                em.remove(em.contains(j) ? j : em.merge(j));
////            }
////        }
////
////    } catch (Exception e) {
////        e.printStackTrace();
////        throw e;
////    }
////

    @Override
    public Collection<Tbljob> getJobs(int recruiterId) {

        try {
            closeExpiredJobs();
            return em.createNamedQuery(
                    "Tbljob.findByRecruiter",
                    Tbljob.class
            )
                    .setParameter("recruiterId", recruiterId)
                    .getResultList();

        } catch (Exception e) {

            e.printStackTrace();

            return new ArrayList<>();
        }
    }
//
    // ================= JOB SKILLS ===========================================================================

    @Override
    public Collection<Tblskills> getAllSkills(Integer userId) {

        try {

            return em.createQuery(
                    "SELECT s FROM Tblskills s "
                    + "WHERE ("
                    + "s.skillStatus = 'APPROVED' "
                    + "OR "
                    + "(s.skillStatus = 'PENDING' "
                    + "AND s.createdByUserId = :userId)"
                    + ") "
                    + "ORDER BY s.skillName",
                    Tblskills.class
            )
                    .setParameter("userId", userId)
                    .getResultList();

        } catch (Exception e) {

            e.printStackTrace();

            return new ArrayList<>();
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

    // ================= SKILL CATEGORIES =================
    @Override
    public Collection<Tblskillcategory> getSkillCategories(Integer recruiterUserId) {

        try {

            return em.createQuery(
                    "SELECT c FROM Tblskillcategory c "
                    + "WHERE ("
                    + "c.categoryStatus = 'APPROVED' "
                    + "OR "
                    + "(c.categoryStatus = 'PENDING' "
                    + "AND c.createdByUserId = :userId)"
                    + ") "
                    + "ORDER BY c.categoryName",
                    Tblskillcategory.class
            )
                    .setParameter("userId", recruiterUserId)
                    .getResultList();

        } catch (Exception e) {

            e.printStackTrace();

            return new ArrayList<>();
        }
    }

// ================= SKILLS BY CATEGORY =================
    @Override
    public Collection<Tblskills> getSkillsByCategory(Integer categoryId, Integer userId) {

        try {

            return em.createQuery(
                    "SELECT s FROM Tblskills s "
                    + "WHERE s.categoryId.categoryId = :categoryId "
                    + "AND ("
                    + "s.skillStatus = 'APPROVED' "
                    + "OR "
                    + "(s.skillStatus = 'PENDING' "
                    + "AND s.createdByUserId = :userId)"
                    + ") "
                    + "ORDER BY s.skillName",
                    Tblskills.class
            )
                    .setParameter("categoryId", categoryId)
                    .setParameter("userId", userId)
                    .getResultList();

        } catch (Exception e) {

            e.printStackTrace();

            return new ArrayList<>();
        }
    }

    @Override
    public void addSkillAndOrCategory(String categoryName, Collection<String> skillNames, Integer existingCategoryId, Integer recruiterUserId) {
        try {
            Tblskillcategory category = null;

            Tblusers recruiterUser = em.find(Tblusers.class, recruiterUserId);

            if (recruiterUser == null) {

                throw new RuntimeException(
                        "Recruiter user not found"
                );
            }
            // =====================================================
            // CREATE NEW CATEGORY
            // =====================================================

            if (categoryName != null && !categoryName.trim().isEmpty()) {

                String trimmedCategory = categoryName.trim();

                // CHECK CATEGORY DUPLICATE
                Long catagoryCount = em.createQuery("SELECT COUNT(c) FROM Tblskillcategory c  WHERE LOWER(TRIM(c.categoryName)) = :name", Long.class)
                        .setParameter("name", trimmedCategory.toLowerCase())
                        .getSingleResult();

                if (catagoryCount > 0) {
                    throw new RuntimeException(
                            "This Category alredy exists."
                    );
                }

                // CREATE CATEGORY
                category = new Tblskillcategory();

                category.setCategoryName(trimmedCategory);
                category.setCategoryStatus("PENDING");
                category.setCreatedByUserId(recruiterUserId);
                category.setCreatedDate(new Date());

                em.persist(category);
                em.flush();

                //NOTIFICATION FOR CATEGORY
//                Tblnotification categoryNotification = new Tblnotification();
//
//                categoryNotification.setUserId(recruiterUser);
//                categoryNotification.setMessage("New Category pending for approval:" + trimmedCategory);
//                categoryNotification.setCreatedDate(new Date());
//                categoryNotification.setNotificationStatus("Unread");
//                categoryNotification.setNotificationType("Important");
//
//                em.persist(categoryNotification);
//                
                // NOTIFICATION FOR CATEGORY
                Tblnotification categoryNotification = new Tblnotification();

                categoryNotification.setSenderUserId(recruiterUser);
                categoryNotification.setReceiverUserId(recruiterUser);

                categoryNotification.setNotificationTitle(
                        "Category Approval Request"
                );

                categoryNotification.setNotificationMessage(
                        "New category pending approval: "
                        + trimmedCategory
                );

                categoryNotification.setNotificationType(
                        "CATEGORY_REQUEST"
                );

                categoryNotification.setIsRead(false);

                categoryNotification.setCreatedDate(
                        new Date()
                );

                em.persist(categoryNotification);
            }
            // =====================================================
            // USE EXISTING CATEGORY
            // =====================================================
            if (category == null && existingCategoryId != null && existingCategoryId > 0) {
                category = em.find(Tblskillcategory.class, existingCategoryId);

                if (category == null) {
                    throw new RuntimeException("Selected category not found");
                }
            }

            // =====================================================
            // CATEGORY REQUIRED FOR SKILLS
            // =====================================================
            if ((skillNames != null
                    && !skillNames.isEmpty())
                    && category == null) {

                throw new RuntimeException(
                        "Please select category"
                );
            }

            // =====================================================
            // ADD SKILLS
            // =====================================================
            if (skillNames != null && !skillNames.isEmpty()) {
                for (String skillName : skillNames) {
                    if (skillName == null || skillName.trim().isEmpty()) {
                        continue;
                    }

                    String trimmedSkill = skillName.trim();

                    // GLOBAL SKILL DUPLICATE CHECK
                    Long skillCount = em.createQuery("SELECT COUNT(S) FROM Tblskills s WHERE LOWER(TRIM(s.skillName)) = :name", Long.class)
                            .setParameter("name", trimmedSkill.toLowerCase())
                            .getSingleResult();

                    if (skillCount > 0) {
                        throw new RuntimeException("This skill alredy exists:" + trimmedSkill);
                    }

                    // =============================================
                    // CREATE SKILL
                    // =============================================
                    Tblskills skill = new Tblskills();

                    skill.setSkillName(trimmedSkill);
                    skill.setCategoryId(category);
                    skill.setSkillStatus("PENDING");
                    skill.setCreatedByUserId(recruiterUserId);
                    skill.setCreatedDate(new Date());

                    em.persist(skill);

//                    //NOTIFICATION FOR SKILL
//                    Tblnotification skillNotification = new Tblnotification();
//
//                    skillNotification.setUserId(recruiterUser);
//                    skillNotification.setMessage("New skill pending for approval: " + trimmedSkill);
//                    skillNotification.setCreatedDate(new Date());
//                    skillNotification.setNotificationStatus("Unread");
//                    skillNotification.setNotificationType("Important");
//
//                    em.persist(skillNotification);
                    Tblnotification skillNotification = new Tblnotification();

                    skillNotification.setSenderUserId(recruiterUser);
                    skillNotification.setReceiverUserId(recruiterUser);

                    skillNotification.setNotificationTitle(
                            "Skill Approval Request"
                    );

                    skillNotification.setNotificationMessage(
                            "New skill pending approval: "
                            + trimmedSkill
                    );

                    skillNotification.setNotificationType(
                            "SKILL_REQUEST"
                    );

                    skillNotification.setIsRead(false);

                    skillNotification.setCreatedDate(
                            new Date()
                    );

                    em.persist(skillNotification);
                }
            }
            em.flush();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

// ================= JOB Education =============================================================================
    @Override
    public Collection<Tbleducation> getAllEducation() {
        try {

            return em.createNamedQuery("Tbleducation.findAll", Tbleducation.class).getResultList();

        } catch (Exception e) {
            e.printStackTrace();

            return new ArrayList<>();
        }
    }

    @Override
    public Collection<Tbleducation> getJobEducation(int jobId) {
        try {
            Tbljob job = em.find(Tbljob.class, jobId);
            return (job != null) ? job.getTbleducationCollection() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ================= CANDIDATE MANAGEMENT =================
    @Override
    public Collection<Tblapplication> getRecruiterApplications(int recruiterId) {

        try {

            return em.createQuery(
                    "SELECT a FROM Tblapplication a "
                    + "WHERE a.jobId.recruiterId.recruiterId = :rid "
                    + "ORDER BY a.applicationAppliedDate DESC",
                    Tblapplication.class
            )
                    .setParameter("rid", recruiterId)
                    .getResultList();

        } catch (Exception e) {

            e.printStackTrace();

            return new ArrayList<>();
        }
    }

//    @Override
//    public double calculateAndSaveScreeningScore(int applicationId) {
//
//        try {
//
//            Tblapplication app = em.find(Tblapplication.class, applicationId);
//
//            if (app == null) {
//                System.out.println("APP NOT FOUND");
//                return 0;
//            }
//
//            Tbljob job = app.getJobId();
//            Tblcandidates candidate = app.getCandidateId();
//
//            System.out.println("\n====================================");
//            System.out.println("APPLICATION : " + applicationId);
//
//            // =====================================================
//            // SAFE COLLECTIONS
//            // =====================================================
//            Collection<Tblskills> jobSkills
//                    = job.getTblskillsCollection() != null ? job.getTblskillsCollection() : new ArrayList<>();
//
//            Collection<Tblskills> candidateSkills
//                    = candidate.getTblskillsCollection() != null ? candidate.getTblskillsCollection() : new ArrayList<>();
//
//            Collection<Tbleducation> jobEducations
//                    = job.getTbleducationCollection();
//
//            Collection<Tblcandidateeducation> candidateEducations
//                    = candidate.getTblcandidateeducationCollection() != null
//                    ? candidate.getTblcandidateeducationCollection()
//                    : new ArrayList<>();
//
//            // =====================================================
//            // 1. SKILL SCORE (0–50)
//            // =====================================================
//            long matchedSkills = 0;
//
//            for (Tblskills js : jobSkills) {
//
//                boolean found = candidateSkills.stream()
//                        .anyMatch(cs -> cs.getSkillId().equals(js.getSkillId()));
//
//                if (found) {
//                    matchedSkills++;
//                }
//            }
//
//            double skillScore = jobSkills.isEmpty()
//                    ? 0
//                    : ((double) matchedSkills / jobSkills.size()) * 50.0;
//
//            // =====================================================
//            // 2. EXPERIENCE SCORE (0–30)
//            // =====================================================
//            int requiredMonths = job.getExperienceRequired() == null ? 0 : job.getExperienceRequired();
//            int candidateMonths = candidate.getCandidateExperience() == null ? 0 : candidate.getCandidateExperience();
//
//            double expScore;
//
//            if (requiredMonths <= 0) {
//                expScore = 20; // neutral baseline (important fix)
//            } else {
//
//                double ratio = (double) candidateMonths / requiredMonths;
//
//                if (ratio >= 1.50) {
//                    expScore = 30;
//                } else if (ratio >= 1.00) {
//                    expScore = 25;
//                } else if (ratio >= 0.75) {
//                    expScore = 20;
//                } else if (ratio >= 0.50) {
//                    expScore = 12;
//                } else {
//                    expScore = 5;
//                }
//            }
//
//            // =====================================================
//            // 3. EDUCATION SCORE (0–20) — LIBERAL FIX
//            // =====================================================
//            double eduScore;
//
//            if (jobEducations == null || jobEducations.isEmpty()) {
//
//                // 🔥 IMPORTANT FIX:
//                // If job does NOT specify education → don't punish candidate
//                eduScore = 15;
//
//            } else {
//
//                long matchedEducation = 0;
//
//                for (Tbleducation je : jobEducations) {
//
//                    boolean found = candidateEducations.stream()
//                            .anyMatch(ce
//                                    -> ce.getEducationName() != null
//                            && je.getEducationName() != null
//                            && ce.getEducationName().trim()
//                                    .equalsIgnoreCase(je.getEducationName().trim())
//                            );
//
//                    if (found) {
//                        matchedEducation++;
//                    }
//                }
//
//                eduScore = ((double) matchedEducation / jobEducations.size()) * 20.0;
//            }
//
//            // =====================================================
//            // FINAL SCORE
//            // =====================================================
//            double finalScore = skillScore + expScore + eduScore;
//
//            if (finalScore > 100) {
//                finalScore = 100;
//            }
//
//            finalScore = Math.round(finalScore);
//
//            System.out.println("FINAL SCORE : " + finalScore);
//
//            // =====================================================
//            // SAVE
//            // =====================================================
//            List<Tblscreeningscore> existing
//                    = em.createQuery(
//                            "SELECT s FROM Tblscreeningscore s WHERE s.applicationId.applicationId = :aid",
//                            Tblscreeningscore.class
//                    )
//                            .setParameter("aid", applicationId)
//                            .getResultList();
//
//            Tblscreeningscore score;
//
//            if (existing.isEmpty()) {
//                score = new Tblscreeningscore();
//                score.setApplicationId(app);
//            } else {
//                score = existing.get(0);
//            }
//
//            score.setMatchingScore(BigDecimal.valueOf(finalScore));
//
//            score.setScreeningLevel(
//                    finalScore >= 80 ? "EXCELLENT"
//                            : finalScore >= 65 ? "HIGH"
//                                    : finalScore >= 45 ? "MEDIUM"
//                                            : "LOW"
//            );
//
//            score.setScoreDate(new Date());
//
//            if (existing.isEmpty()) {
//                em.persist(score);
//            } else {
//                em.merge(score);
//            }
//
//            return finalScore;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }
    @Override
    public double calculateAndSaveScreeningScore(int applicationId) {

        try {

            System.out.println("\n====================================");
            System.out.println("START SCREENING SCORE CALCULATION");
            System.out.println("APPLICATION ID : " + applicationId);
            System.out.println("====================================");

            Tblapplication app = em.find(Tblapplication.class, applicationId);

            if (app == null) {
                System.out.println("ERROR : Application not found.");
                return 0;
            }

            Tbljob job = app.getJobId();
            Tblcandidates candidate = app.getCandidateId();

            if (job == null || candidate == null) {
                System.out.println("ERROR : Job or Candidate is missing.");
                return 0;
            }

            Collection<Tblskills> jobSkills
                    = job.getTblskillsCollection() != null ? job.getTblskillsCollection() : new ArrayList<>();

            Collection<Tblskills> candidateSkills
                    = candidate.getTblskillsCollection() != null ? candidate.getTblskillsCollection() : new ArrayList<>();

            Collection<Tbleducation> jobEducations
                    = job.getTbleducationCollection() != null ? job.getTbleducationCollection() : new ArrayList<>();

            Collection<Tblcandidateeducation> candidateEducations
                    = candidate.getTblcandidateeducationCollection() != null ? candidate.getTblcandidateeducationCollection() : new ArrayList<>();

            // =========================
            // 1. SKILL SCORE - 50
            // =========================
            double skillScore = 0;
            long matchedSkills = 0;

            if (jobSkills.isEmpty()) {
                skillScore = 0;
                System.out.println("SKILL DEBUG : Job skills are required, but no job skills found.");
            } else if (candidateSkills.isEmpty()) {
                skillScore = 0;
                System.out.println("SKILL DEBUG : Candidate has no skills.");
            } else {
                for (Tblskills jobSkill : jobSkills) {
                    for (Tblskills candidateSkill : candidateSkills) {
                        if (jobSkill.getSkillId() != null
                                && candidateSkill.getSkillId() != null
                                && jobSkill.getSkillId().equals(candidateSkill.getSkillId())) {

                            matchedSkills++;
                            System.out.println("SKILL MATCHED : " + jobSkill.getSkillName());
                            break;
                        }
                    }
                }

                skillScore = ((double) matchedSkills / jobSkills.size()) * 50;
            }

            System.out.println("SKILL SCORE : " + skillScore + " / 50");

            // =========================
            // 2. EXPERIENCE SCORE - 30
            // Experience is saved in MONTHS
            // =========================
            int requiredMonths = job.getExperienceRequired() != null ? job.getExperienceRequired() : 0;
            int candidateMonths = candidate.getCandidateExperience() != null ? candidate.getCandidateExperience() : 0;

            double expScore = 0;

            System.out.println("REQUIRED EXPERIENCE  : " + requiredMonths + " months");
            System.out.println("CANDIDATE EXPERIENCE : " + candidateMonths + " months");

            if (requiredMonths <= 0) {
                expScore = 0;
                System.out.println("EXP DEBUG : Experience is required, but job required months is missing/zero.");
            } else if (candidateMonths <= 0) {
                expScore = 0;
                System.out.println("EXP DEBUG : Candidate has 0 months experience.");
            } else {
                double ratio = (double) candidateMonths / requiredMonths;

                System.out.println("EXP DEBUG : Ratio = " + ratio);

                if (ratio >= 2.00) {
                    expScore = 30;
                } else if (ratio >= 1.50) {
                    expScore = 28;
                } else if (ratio >= 1.00) {
                    expScore = 25;
                } else if (ratio >= 0.75) {
                    expScore = 18;
                } else if (ratio >= 0.50) {
                    expScore = 10;
                } else {
                    expScore = 3;
                }
            }

            System.out.println("EXPERIENCE SCORE : " + expScore + " / 30");

            // =========================
            // 3. EDUCATION SCORE - 20
            // Education is OPTIONAL
            // =========================
            double eduScore = 0;
            long matchedEducation = 0;
            boolean educationRequired = !jobEducations.isEmpty();

            if (!educationRequired) {
                System.out.println("EDU DEBUG : Education is optional/not selected. Education will not affect score.");
            } else if (candidateEducations.isEmpty()) {
                eduScore = 0;
                System.out.println("EDU DEBUG : Job requires education but candidate has no education.");
            } else {
                for (Tbleducation jobEdu : jobEducations) {
                    for (Tblcandidateeducation candidateEdu : candidateEducations) {

                        String jobEduName = jobEdu.getEducationName();
                        String candidateEduName = candidateEdu.getEducationName();

                        if (jobEduName != null
                                && candidateEduName != null
                                && jobEduName.trim().equalsIgnoreCase(candidateEduName.trim())) {

                            matchedEducation++;
                            System.out.println("EDUCATION MATCHED : " + jobEduName);
                            break;
                        }
                    }
                }

                if (matchedEducation == 0) {
                    eduScore = 4;
                } else {
                    eduScore = ((double) matchedEducation / jobEducations.size()) * 20;
                }
            }

            System.out.println("EDUCATION SCORE : " + eduScore + (educationRequired ? " / 20" : " ignored"));

            // =========================
            // FINAL SCORE
            // =========================
            double totalScore;
            double totalPossibleMarks;

            if (educationRequired) {
                totalScore = skillScore + expScore + eduScore;
                totalPossibleMarks = 100;
            } else {
                totalScore = skillScore + expScore;
                totalPossibleMarks = 80;
            }

            double finalScore = (totalScore / totalPossibleMarks) * 100;

            if (finalScore > 100) {
                finalScore = 100;
            }

            if (finalScore < 0) {
                finalScore = 0;
            }

            finalScore = Math.round(finalScore);

            String level;

            if (finalScore >= 80) {
                level = "EXCELLENT";
            } else if (finalScore >= 65) {
                level = "HIGH";
            } else if (finalScore >= 45) {
                level = "MEDIUM";
            } else {
                level = "LOW";
            }

            String remarks
                    = "Skills " + matchedSkills + "/" + jobSkills.size()
                    + ", Experience " + candidateMonths + "/" + requiredMonths + " months";

            if (educationRequired) {
                remarks += ", Education " + matchedEducation + "/" + jobEducations.size();
            } else {
                remarks += ", Education optional";
            }

            System.out.println("TOTAL SCORE BEFORE NORMALIZE : " + totalScore + " / " + totalPossibleMarks);
            System.out.println("FINAL SCORE : " + finalScore + " / 100");
            System.out.println("SCREENING LEVEL : " + level);
            System.out.println("REMARKS : " + remarks);

            List<Tblscreeningscore> existingScores = em.createQuery(
                    "SELECT s FROM Tblscreeningscore s WHERE s.applicationId.applicationId = :aid",
                    Tblscreeningscore.class
            )
                    .setParameter("aid", applicationId)
                    .getResultList();

            Tblscreeningscore screeningScore;

            if (existingScores.isEmpty()) {
                screeningScore = new Tblscreeningscore();
                screeningScore.setApplicationId(app);
                System.out.println("DB DEBUG : New screening score record will be inserted.");
            } else {
                screeningScore = existingScores.get(0);
                System.out.println("DB DEBUG : Existing screening score record will be updated.");
            }

            screeningScore.setMatchingScore(BigDecimal.valueOf(finalScore).setScale(2, RoundingMode.HALF_UP));
            screeningScore.setScreeningLevel(level);
            screeningScore.setRemarks(remarks);
            screeningScore.setScoreDate(new Date());

            if (existingScores.isEmpty()) {
                em.persist(screeningScore);
            } else {
                em.merge(screeningScore);
            }

            System.out.println("DB DEBUG : Screening score saved successfully.");
            System.out.println("====================================\n");

            return finalScore;

        } catch (Exception e) {
            System.out.println("ERROR : Failed to calculate screening score for application ID " + applicationId);
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Map<Integer, BigDecimal> getAllScreeningScores(int recruiterId) {
        try {
            List<Object[]> results = em.createQuery(
                    "SELECT s.applicationId.applicationId, s.matchingScore "
                    + "FROM Tblscreeningscore s "
                    + "WHERE s.applicationId.jobId.recruiterId.recruiterId = :rid "
                    + "ORDER BY s.scoreDate DESC",
                    Object[].class)
                    .setParameter("rid", recruiterId)
                    .getResultList();

            Map<Integer, BigDecimal> scoreMap = new HashMap<>();
            for (Object[] row : results) {
                Integer appId = (Integer) row[0];
                BigDecimal score = (BigDecimal) row[1];
                // Only keep the latest score per application
                if (!scoreMap.containsKey(appId)) {
                    scoreMap.put(appId, score);
                }
            }
            return scoreMap;

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    @Override
    public String getScreeningLevel(int applicationId) {
        try {
            List<String> results = em.createQuery(
                    "SELECT s.screeningLevel FROM Tblscreeningscore s "
                    + "WHERE s.applicationId.applicationId = :aid "
                    + "ORDER BY s.scoreDate DESC",
                    String.class)
                    .setParameter("aid", applicationId)
                    .setMaxResults(1)
                    .getResultList();

            return results.isEmpty() ? "NOT_SCORED" : results.get(0);

        } catch (Exception e) {
            return "NOT_SCORED";
        }
    }

    @Override
    public void shortlistApplication(int applicationId) {
        updateApplicationStatusWithHistoryAndNotification(
                applicationId,
                "Shortlisted",
                "Application Under Review",
                "Your application is under review."
        );
    }

//    @Override
//    public void selectApplication(int applicationId) {
//        updateApplicationStatusWithHistoryAndNotification(
//                applicationId,
//                "Selected",
//                "Application Selected",
//                "Congratulations. You are selected for this application."
//        );
//    }
    private void updateApplicationStatusWithHistoryAndNotification(
            int applicationId,
            String newStatus,
            String notificationTitle,
            String candidateMessage) {

        try {
            if (applicationId <= 0) {
                throw new RuntimeException("Invalid application id");
            }

            Tblapplication app = em.find(Tblapplication.class, applicationId);

            if (app == null) {
                throw new RuntimeException("Application not found");
            }

            String oldStatus = app.getApplicationStatus();

            if (oldStatus != null && oldStatus.equalsIgnoreCase(newStatus)) {
                return;
            }

            app.setApplicationStatus(newStatus);
            app.setLastUpdatedDate(new Date());

            em.merge(app);

//            addApplicationStatusHistory(app, oldStatus, newStatus);
            Tblusers recruiterUser = app.getJobId()
                    .getRecruiterId()
                    .getUserId();

            Tblusers candidateUser = app.getCandidateId()
                    .getUserId();

            saveNotification(
                    recruiterUser,
                    candidateUser,
                    notificationTitle,
                    candidateMessage,
                    "APPLICATION_STATUS"
            );

            saveNotification(
                    recruiterUser,
                    recruiterUser,
                    "Application Status Updated",
                    candidateUser.getUserName()
                    + " status changed to "
                    + newStatus
                    + " for "
                    + app.getJobId().getJobTitle(),
                    "APPLICATION_STATUS"
            );

            if (candidateUser.getUserEmail() != null) {
                emailService.sendEmail(
                        candidateUser.getUserEmail(),
                        notificationTitle,
                        "Hello " + candidateUser.getUserName() + ",\n\n"
                        + candidateMessage + "\n\n"
                        + "Job: " + app.getJobId().getJobTitle() + "\n"
                        + "Status: " + newStatus + "\n\n"
                        + "Regards,\nQuickHire Team"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void saveNotification(
            Tblusers senderUser,
            Tblusers receiverUser,
            String title,
            String message,
            String type) {

        Tblnotification notification = new Tblnotification();

        notification.setSenderUserId(senderUser);
        notification.setReceiverUserId(receiverUser);
        notification.setNotificationTitle(title);
        notification.setNotificationMessage(message);
        notification.setNotificationType(type);
        notification.setIsRead(false);
        notification.setCreatedDate(new Date());

        em.persist(notification);
    }

    @Override
    public void scheduleInterview(Tblinterview interview) {

        try {

            // ================= VALIDATION =================
            if (interview == null
                    || interview.getApplicationId() == null
                    || interview.getApplicationId().getApplicationId() == null) {

                throw new RuntimeException("Application id is required");
            }

            // ================= FETCH APPLICATION =================
            Tblapplication app = em.find(
                    Tblapplication.class,
                    interview.getApplicationId().getApplicationId()
            );

            if (app == null) {

                throw new RuntimeException("Application not found");
            }

            // ================= STATUS CHECK =================
            if (!"Shortlisted".equalsIgnoreCase(app.getApplicationStatus())) {

                throw new RuntimeException(
                        "Interview can only be scheduled for shortlisted candidates"
                );
            }

            // ================= SET APPLICATION =================
            interview.setApplicationId(app);

            // ================= DEFAULT VALUES =================
            // Interview just scheduled
            interview.setInterviewStatus("Scheduled");

            // Result will be updated after interview
            interview.setResult("Pending");

            // Feedback will be added later
            interview.setFeedback(null);

            // ================= SAVE INTERVIEW =================
            em.persist(interview);

            // ================= UPDATE APPLICATION STATUS =================
            app.setApplicationStatus("Interview Scheduled");

            app.setLastUpdatedDate(new Date());

            em.merge(app);

            // ================= GET USERS =================
            Tblusers recruiterUser = app.getJobId()
                    .getRecruiterId()
                    .getUserId();

            Tblusers candidateUser = app.getCandidateId()
                    .getUserId();

            // ================= SAVE CANDIDATE NOTIFICATION =================
            saveNotification(
                    recruiterUser,
                    candidateUser,
                    "Interview Scheduled",
                    "Your interview has been scheduled for "
                    + app.getJobId().getJobTitle(),
                    "INTERVIEW"
            );

            // ================= SAVE RECRUITER ACTIVITY =================
            saveNotification(
                    recruiterUser,
                    recruiterUser,
                    "Interview Scheduled",
                    "Interview scheduled with "
                    + candidateUser.getUserName()
                    + " for "
                    + app.getJobId().getJobTitle(),
                    "INTERVIEW"
            );

            // ================= EMAIL SECTION =================
            if (candidateUser.getUserEmail() != null) {

                String interviewDetails = "";

                // ================= ONLINE =================
                if ("Online".equalsIgnoreCase(interview.getInterviewerMode())) {

                    interviewDetails
                            = "Mode: Online\n"
                            + "Meeting details will be shared soon.\n";
                } // ================= OFFLINE =================
                else if ("Offline".equalsIgnoreCase(interview.getInterviewerMode())) {

                    interviewDetails
                            = "Mode: Offline\n"
                            + "Interview location will be shared later by the recruiter.\n";
                } // ================= PHONE =================
                else if ("Phone".equalsIgnoreCase(interview.getInterviewerMode())) {

                    interviewDetails
                            = "Mode: Phone Interview\n"
                            + "The recruiter will contact you on your registered phone number.\n";
                } // ================= DEFAULT FALLBACK =================
                else {

                    interviewDetails
                            = "Interview mode details will be shared soon.\n";
                }

                String subject = "Interview Scheduled";

                String message
                        = "Hello "
                        + candidateUser.getUserName()
                        + ",\n\n"
                        + "Congratulations! Your interview has been scheduled "
                        + "for the position of "
                        + app.getJobId().getJobTitle()
                        + ".\n\n"
                        + "Interview Details:\n"
                        + "Date: "
                        + interview.getInterviewDate()
                        + "\n"
                        + "Interviewer: "
                        + interview.getInterviewerName()
                        + "\n"
                        + interviewDetails
                        + "\n"
                        + "Please be available on time and prepare well for your interview.\n\n"
                        + "Best Regards,\n"
                        + "QuickHire Team";

                // ================= SEND EMAIL =================
                emailService.sendEmail(
                        candidateUser.getUserEmail(),
                        subject,
                        message
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Error while scheduling interview: "
                    + e.getMessage()
            );
        }
    }

    // ================= INTERVIEW =================
    @Override
    public Collection<Tblinterview> getRecruiterInterviews(Integer recruiterId) {

        return em.createQuery(
                "SELECT i FROM Tblinterview i "
                + "WHERE i.applicationId.jobId.recruiterId.recruiterId = :rid "
                + "ORDER BY i.interviewDate DESC",
                Tblinterview.class)
                .setParameter("rid", recruiterId)
                .getResultList();
    }

    @Override
    public Long getCompletedInterviewCount(Integer recruiterId) {

        return em.createQuery(
                "SELECT COUNT(i) FROM Tblinterview i "
                + "WHERE i.applicationId.jobId.recruiterId.recruiterId = :rid "
                + "AND i.interviewStatus = :status",
                Long.class)
                .setParameter("rid", recruiterId)
                .setParameter("status", "Completed")
                .getSingleResult();
    }

    @Override
    public Long getScheduledInterviewCount(Integer recruiterId) {

        return em.createQuery(
                "SELECT COUNT(i) FROM Tblinterview i "
                + "WHERE i.applicationId.jobId.recruiterId.recruiterId = :rid "
                + "AND i.interviewStatus = :status",
                Long.class)
                .setParameter("rid", recruiterId)
                .setParameter("status", "Scheduled")
                .getSingleResult();
    }

    @Override
    public Long getSelectedCount(Integer recruiterId) {

        return em.createQuery(
                "SELECT COUNT(i) FROM Tblinterview i "
                + "WHERE i.applicationId.jobId.recruiterId.recruiterId = :rid "
                + "AND i.result = :result",
                Long.class)
                .setParameter("rid", recruiterId)
                .setParameter("result", "Selected")
                .getSingleResult();
    }

    @Override
    public Long getRejectedCount(Integer recruiterId) {

        return em.createQuery(
                "SELECT COUNT(i) FROM Tblinterview i "
                + "WHERE i.applicationId.jobId.recruiterId.recruiterId = :rid "
                + "AND i.result = :result",
                Long.class)
                .setParameter("rid", recruiterId)
                .setParameter("result", "Rejected")
                .getSingleResult();
    }

    @Override
    public Long getTotalInterviewCount(Integer recruiterId) {

        return em.createQuery(
                "SELECT COUNT(i) FROM Tblinterview i "
                + "WHERE i.applicationId.jobId.recruiterId.recruiterId = :rid",
                Long.class)
                .setParameter("rid", recruiterId)
                .getSingleResult();
    }

    @Override
    public void conductInterview(Integer interviewId,
            String feedback,
            String result) {

        Tblinterview interview
                = em.find(Tblinterview.class, interviewId);

        if (interview != null) {

            interview.setFeedback(feedback);
            interview.setResult(result);
            interview.setInterviewStatus("Completed");

            em.merge(interview);

            Tblapplication application
                    = interview.getApplicationId();

            application.setApplicationStatus(result);

            em.merge(application);
        }
    }

    @Override
    public void rescheduleInterview(Integer interviewId,
            Date interviewDate,
            String interviewerName,
            String interviewerMode) {

        Tblinterview interview
                = em.find(Tblinterview.class, interviewId);

        if (interview != null) {

            interview.setInterviewDate(interviewDate);
            interview.setInterviewerName(interviewerName);
            interview.setInterviewerMode(interviewerMode);
            interview.setInterviewStatus("Scheduled");

            em.merge(interview);
        }
    }

//    // ================= APPLICATION =================
//    @Override
//    public Collection<Tblapplication> getApplications(int jobId) {
//        try {
//            return em.createNamedQuery("Tblapplication.findByJob", Tblapplication.class)
//                    .setParameter("jobId", jobId)
//                    .getResultList();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    @Override
//    public void updateApplicationStatus(int applicationId, String newStatus) {
//        try {
//            Tblapplication app = em.find(Tblapplication.class, applicationId);
//
//            if (app != null) {
//                String oldStatus = app.getApplicationStatus();
//
//                app.setApplicationStatus(newStatus);
//                app.setLastUpdatedDate(new Date());
//                em.merge(app);
//
//                // ================= RECENT ACTIVITY =================
//                if ("Shortlisted".equalsIgnoreCase(newStatus)) {
//
//                    Tblnotification activity
//                            = new Tblnotification();
//
//                    activity.setUserId(
//                            app.getJobId()
//                                    .getRecruiterId()
//                                    .getUserId()
//                    );
//
//                    activity.setMessage(
//                            app.getCandidateId()
//                                    .getUserId()
//                                    .getUserName()
//                            + " shortlisted for "
//                            + app.getJobId()
//                                    .getJobTitle()
//                    );
//
//                    activity.setCreatedDate(
//                            new Date()
//                    );
//
//                    activity.setNotificationStatus(
//                            "Read"
//                    );
//
//                    em.persist(activity);
//                }
//
//                if ("Selected".equalsIgnoreCase(newStatus)) {
//
//                    Tblnotification activity
//                            = new Tblnotification();
//
//                    activity.setUserId(
//                            app.getJobId()
//                                    .getRecruiterId()
//                                    .getUserId()
//                    );
//
//                    activity.setMessage(
//                            app.getCandidateId()
//                                    .getUserId()
//                                    .getUserName()
//                            + " selected for "
//                            + app.getJobId()
//                                    .getJobTitle()
//                    );
//
//                    activity.setCreatedDate(
//                            new Date()
//                    );
//
//                    activity.setNotificationStatus(
//                            "Read"
//                    );
//
//                    em.persist(activity);
//                }
//
//                addApplicationStatusHistory(applicationId, oldStatus, newStatus);
//
//                // Notify Candidate
//                // Fetch fresh from DB (safe)
//                Tblapplication freshApp = em.find(Tblapplication.class, applicationId);
//
//                if (freshApp.getCandidateId() != null && freshApp.getCandidateId().getUserId() != null) {
//                    Tblusers candidateUser = freshApp.getCandidateId().getUserId();
//
//                    String subject = "Application Status Updated";
//
//                    String message = "Hello " + candidateUser.getUserName() + ",\n\n"
//                            + "Your application status has been updated.\n\n"
//                            + "Job: " + freshApp.getJobId().getJobTitle() + "\n"
//                            + "Old Status: " + oldStatus + "\n"
//                            + "New Status: " + newStatus + "\n\n"
//                            + "Regards,\nQuickHire Team";
//
//                    emailService.sendEmail(candidateUser.getUserEmail(), subject, message);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    // ================= APPLICATION HISTORY =================
//    @Override
//    public void addApplicationStatusHistory(int applicationId, String oldStatus, String newStatus) {
//        try {
//            Tblapplication app = em.find(Tblapplication.class, applicationId);
//
//            if (app != null) {
//                Tblapplicationstatushistory history = new Tblapplicationstatushistory();
//                history.setApplicationId(app);
//                history.setOldStatus(oldStatus);
//                history.setNewStatus(newStatus);
//                history.setStatusUpdatedDate(new Date());
//
//                em.persist(history);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    // ================= SCREENING =================
//    @Override
//    public void generateScreeningScore(int applicationId) {
//        try {
//            Tblapplication app = em.find(Tblapplication.class, applicationId);
//            if (app == null) {
//                return;
//            }
//
//            Collection<Tblskills> candidateSkills = app.getCandidateId().getTblskillsCollection();
//            Collection<Tblskills> jobSkills = app.getJobId().getTblskillsCollection();
//
//            int match = 0;
//            for (Tblskills js : jobSkills) {
//                for (Tblskills cs : candidateSkills) {
//                    if (js.getSkillId().equals(cs.getSkillId())) {
//                        match++;
//                    }
//                }
//            }
//
//            double score = ((double) match / jobSkills.size()) * 100;
//
//            Tblscreeningscore sc = new Tblscreeningscore();
//            sc.setApplicationId(app);
//            sc.setMatchingScore(BigDecimal.valueOf(score));
//            sc.setScreeningLevel(score >= 70 ? "High" : score >= 40 ? "Medium" : "Low");
//            sc.setScoreDate(new Date());
//
//            em.persist(sc);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public Tblscreeningscore getScore(int applicationId) {
//        try {
//            return em.createNamedQuery("Tblscreeningscore.findByApplication", Tblscreeningscore.class)
//                    .setParameter("applicationId", applicationId)
//                    .getSingleResult();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    // ================= SHORTLIST =================
//    @Override
//    public Collection<Tblapplication> getTopCandidates(int jobId) {
//        try {
//            return em.createNamedQuery("Tblapplication.findTopCandidates", Tblapplication.class)
//                    .setParameter("jobId", jobId)
//                    .getResultList();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    @Override
//    public Collection<Tblapplication> filterCandidatesByScore(int jobId, double minScore) {
//        try {
//            return em.createNamedQuery("Tblapplication.filterByScore", Tblapplication.class)
//                    .setParameter("jobId", jobId)
//                    .setParameter("score", minScore)
//                    .getResultList();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    // ================= INTERVIEW =================
//    @Override
//    public void scheduleInterview(
//            Tblinterview interview) {
//
//        try {
//
//            Tblapplication app
//                    = em.find(
//                            Tblapplication.class,
//                            interview.getApplicationId()
//                                    .getApplicationId()
//                    );
//
//            if (app == null) {
//                return;
//            }
//
//            interview.setApplicationId(app);
//
//            // SAVE INTERVIEW
//            em.persist(interview);
//
//            // =========================
//            // SAVE RECENT ACTIVITY
//            // =========================
//            Tblnotification activity
//                    = new Tblnotification();
//
//            activity.setUserId(
//                    app.getJobId()
//                            .getRecruiterId()
//                            .getUserId()
//            );
//
//            activity.setMessage(
//                    "Interview scheduled with "
//                    + app.getCandidateId()
//                            .getUserId()
//                            .getUserName()
//            );
//
//            activity.setCreatedDate(
//                    new Date()
//            );
//
//            activity.setNotificationStatus(
//                    "Read"
//            );
//
//            em.persist(activity);
//
//            // =========================
//            // SEND EMAIL TO CANDIDATE
//            // =========================
//            Tblusers candidateUser
//                    = app.getCandidateId()
//                            .getUserId();
//
//            if (candidateUser != null) {
//
//                String subject
//                        = "Interview Scheduled";
//
//                String message
//                        = "Hello "
//                        + candidateUser.getUserName()
//                        + ",\n\n"
//                        + "Your interview has been scheduled.\n\n"
//                        + "Job: "
//                        + app.getJobId()
//                                .getJobTitle()
//                        + "\n"
//                        + "Date: "
//                        + interview.getInterviewDate()
//                        + "\n\n"
//                        + "Regards,\nQuickHire Team";
//
//                emailService.sendEmail(
//                        candidateUser.getUserEmail(),
//                        subject,
//                        message
//                );
//            }
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void updateInterview(Tblinterview interview) {
//        try {
//            em.merge(interview);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void updateInterviewFeedback(int interviewId, String feedback, String result) {
//        try {
//            Tblinterview interview = em.find(Tblinterview.class, interviewId);
//            if (interview != null) {
//                interview.setFeedback(feedback);
//                interview.setResult(result);
//                em.merge(interview);
//
//                // Notify Candidate
//                // Fetch fresh application safely
//                Tblapplication app = em.find(Tblapplication.class, interview.getApplicationId().getApplicationId());
//
//                if (app != null && app.getCandidateId() != null && app.getCandidateId().getUserId() != null) {
//                    Tblusers candidateUser = app.getCandidateId().getUserId();
//
//                    String subject = "Interview Result";
//
//                    String message = "Hello " + candidateUser.getUserName() + ",\n\n"
//                            + "Your interview result is now available.\n\n"
//                            + "Result: " + result + "\n"
//                            + "Feedback: " + feedback + "\n\n"
//                            + "Regards,\nQuickHire Team";
//
//                    emailService.sendEmail(candidateUser.getUserEmail(), subject, message);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    // ================= NOTIFICATION =================
//    @Override
//    public void sendNotification(Tblnotification notification) {
//        try {
//            notification.setCreatedDate(new Date());
//            em.persist(notification);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public Collection<Tblnotification> getRecruiterNotifications(int userId) {
//        try {
//            return em.createNamedQuery("Tblnotification.findByUser", Tblnotification.class)
//                    .setParameter("userId", userId)
//                    .getResultList();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
    @Override
    public long getTodayInterviewsCount(int recruiterId) {

        try {

            return em.createQuery(
                    "SELECT COUNT(i) FROM Tblinterview i "
                    + "WHERE i.applicationId.jobId.recruiterId.recruiterId = :rid "
                    + "AND FUNCTION('DATE', i.interviewDate) = CURRENT_DATE "
                    + "AND i.interviewDate >= CURRENT_TIMESTAMP "
                    + "AND i.interviewStatus = 'Scheduled'",
                    Long.class)
                    .setParameter("rid", recruiterId)
                    .getSingleResult();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public long getNewApplicantsCount(int recruiterId) {
        try {

            return em.createQuery(
                    "SELECT COUNT(a) FROM Tblapplication a "
                    + "WHERE a.jobId.recruiterId.recruiterId = :rid "
                    + "AND FUNCTION('DATE', a.applicationAppliedDate) = CURRENT_DATE",
                    Long.class)
                    .setParameter("rid", recruiterId)
                    .getSingleResult();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public long getShortlistedCount(int recruiterId) {
        try {

            return em.createQuery(
                    "SELECT COUNT(a) FROM Tblapplication a "
                    + "WHERE a.jobId.recruiterId.recruiterId = :rid "
                    + "AND a.applicationStatus = 'Shortlisted'",
                    Long.class)
                    .setParameter("rid", recruiterId)
                    .getSingleResult();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public double getHiringRate(int recruiterId) {

        try {

            Long selected = em.createQuery(
                    "SELECT COUNT(a) FROM Tblapplication a "
                    + "WHERE a.jobId.recruiterId.recruiterId = :rid "
                    + "AND a.applicationStatus = 'Selected'",
                    Long.class)
                    .setParameter("rid", recruiterId)
                    .getSingleResult();

            Long total = em.createQuery(
                    "SELECT COUNT(a) FROM Tblapplication a "
                    + "WHERE a.jobId.recruiterId.recruiterId = :rid",
                    Long.class)
                    .setParameter("rid", recruiterId)
                    .getSingleResult();

            if (total == 0) {
                return 0;
            }

            return (selected * 100.0) / total;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public long getActiveJobsCount(int recruiterId) {
        closeExpiredJobs();
        try {

            return em.createQuery(
                    "SELECT COUNT(j) FROM Tbljob j "
                    + "WHERE j.recruiterId.recruiterId = :rid "
                    + "AND j.jobStatus = 'Open'",
                    Long.class)
                    .setParameter("rid", recruiterId)
                    .getSingleResult();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public long getTotalApplicantsCount(int recruiterId) {

        try {

            return em.createQuery(
                    "SELECT COUNT(a) FROM Tblapplication a "
                    + "WHERE a.jobId.recruiterId.recruiterId = :rid",
                    Long.class)
                    .setParameter("rid", recruiterId)
                    .getSingleResult();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public long getUpcomingInterviewsCount(int recruiterId) {

        try {

            return em.createQuery(
                    "SELECT COUNT(i) FROM Tblinterview i "
                    + "WHERE i.applicationId.jobId.recruiterId.recruiterId = :rid "
                    + "AND i.interviewDate >= CURRENT_TIMESTAMP "
                    + "AND i.interviewStatus = 'Scheduled'",
                    Long.class)
                    .setParameter("rid", recruiterId)
                    .getSingleResult();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public double getAvgTimeToHire(int recruiterId) {

        try {

            Double avgDays = em.createQuery(
                    "SELECT AVG(FUNCTION('DATEDIFF', a.lastUpdatedDate, a.applicationAppliedDate)) "
                    + "FROM Tblapplication a "
                    + "WHERE a.jobId.recruiterId.recruiterId = :rid "
                    + "AND a.applicationStatus = 'Selected'",
                    Double.class)
                    .setParameter("rid", recruiterId)
                    .getSingleResult();

            return avgDays != null ? avgDays : 0;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Collection<Tblscreeningscore> getDashboardTopCandidates(int recruiterId) {

        try {

            return em.createQuery(
                    "SELECT s FROM Tblscreeningscore s "
                    + "WHERE s.applicationId.jobId.recruiterId.recruiterId = :rid "
                    + "ORDER BY s.matchingScore DESC",
                    Tblscreeningscore.class)
                    .setParameter("rid", recruiterId)
                    .setMaxResults(3)
                    .getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Collection<Tblinterview> getDashboardUpcomingInterviews(int recruiterId) {

        try {

            Date now = new Date();

            System.out.println("Current Java Time = " + now);

            return em.createQuery(
                    "SELECT i FROM Tblinterview i "
                    + "WHERE i.applicationId.jobId.recruiterId.recruiterId = :rid "
                    + "AND i.interviewDate >= :now "
                    + "AND i.interviewStatus = 'Scheduled' "
                    + "ORDER BY i.interviewDate ASC",
                    Tblinterview.class)
                    .setParameter("rid", recruiterId)
                    .setParameter("now", now)
                    .setMaxResults(3)
                    .getResultList();

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Collection<Tblnotification> getRecentActivities(int userId) {

        try {

            return em.createQuery(
                    "SELECT n FROM Tblnotification n "
                    + "WHERE n.receiverUserId.userId = :uid "
                    + "ORDER BY n.createdDate DESC",
                    Tblnotification.class)
                    .setParameter("uid", userId)
                    .setMaxResults(6)
                    .getResultList();

        } catch (Exception e) {

            e.printStackTrace();

            return new ArrayList<>();
        }
    }

}
