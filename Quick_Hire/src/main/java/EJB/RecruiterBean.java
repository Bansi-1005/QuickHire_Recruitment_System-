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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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

    @Override
    public double calculateAndSaveScreeningScore(int applicationId) {

        try {

            Tblapplication app = em.find(Tblapplication.class, applicationId);

            if (app == null) {
                return 0;
            }

            int candidateId = app.getCandidateId().getCandidateId();
            int jobId = app.getJobId().getJobId();

            // ==================================================
            // 1. JOB SKILLS
            // ==================================================
            List<Integer> jobSkills = em.createQuery(
                    "SELECT js.skillId.skillId FROM TblJobSkills js WHERE js.jobId.jobId = :jobId",
                    Integer.class
            )
                    .setParameter("jobId", jobId)
                    .getResultList();

            // ==================================================
            // 2. CANDIDATE SKILLS
            // ==================================================
            List<Integer> candidateSkills = em.createQuery(
                    "SELECT cs.skillId.skillId FROM TblCandidateSkills cs WHERE cs.candidateId.candidateId = :cid",
                    Integer.class
            )
                    .setParameter("cid", candidateId)
                    .getResultList();

            // =========================
            // SKILL SCORE (0–50)
            // =========================
            double skillScore = 0;

            if (!jobSkills.isEmpty()) {

                long matched = jobSkills.stream()
                        .filter(candidateSkills::contains)
                        .count();

                double matchRatio = (double) matched / jobSkills.size();

                skillScore = matchRatio * 50;
            }

            // ==================================================
            // 3. EXPERIENCE SCORE (0–30)
            // ==================================================
            int candidateExp = app.getCandidateId().getCandidateExperience();
            int requiredExp = app.getJobId().getExperienceRequired();

            double expScore;

            if (requiredExp > 0) {

                double ratio = (double) candidateExp / requiredExp;

                if (ratio >= 1.2) {
                    expScore = 30;
                } else if (ratio >= 1.0) {
                    expScore = 27;
                } else if (ratio >= 0.8) {
                    expScore = 22;
                } else if (ratio >= 0.5) {
                    expScore = 15;
                } else {
                    expScore = 8;
                }

            } else {
                expScore = 20; // neutral score if no requirement
            }

            // ==================================================
            // 4. EDUCATION SCORE (0–20)
            // ==================================================
            double eduScore = 0;

            List<Integer> jobEduList = em.createQuery(
                    "SELECT je.educationId.educationId FROM TblJobEducation je WHERE je.jobId.jobId = :jobId",
                    Integer.class
            )
                    .setParameter("jobId", jobId)
                    .getResultList();

            List<String> candidateEduList = em.createQuery(
                    "SELECT ce.educationName FROM TblCandidateEducation ce WHERE ce.candidateId.candidateId = :cid",
                    String.class
            )
                    .setParameter("cid", candidateId)
                    .getResultList();

            if (!jobEduList.isEmpty()) {

                List<String> jobEduNames = em.createQuery(
                        "SELECT e.educationName FROM TblEducation e WHERE e.educationId IN :ids",
                        String.class
                )
                        .setParameter("ids", jobEduList)
                        .getResultList();

                long matchCount = jobEduNames.stream()
                        .filter(candidateEduList::contains)
                        .count();

                double eduRatio = (double) matchCount / jobEduNames.size();

                eduScore = eduRatio * 20;

            } else {
                eduScore = 10; // neutral when no requirement
            }

            // ==================================================
            // 5. FINAL SCORE (0–100)
            // ==================================================
            double finalScore = skillScore + expScore + eduScore;

            finalScore = Math.min(100, finalScore);

            // ==================================================
            // 6. SAVE SCORE
            // ==================================================
            Tblscreeningscore score = new Tblscreeningscore();

            score.setApplicationId(app);
            score.setMatchingScore(BigDecimal.valueOf(finalScore));
            score.setScreeningLevel(
                    finalScore >= 80 ? "EXCELLENT"
                            : finalScore >= 65 ? "HIGH"
                                    : finalScore >= 45 ? "MEDIUM"
                                            : "LOW"
            );
            score.setScoreDate(new Date());

            em.persist(score);

            return finalScore;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public BigDecimal getScreeningScore(int applicationId) {

        try {

            return em.createQuery(
                    "SELECT s.matchingScore FROM Tblscreeningscore s "
                    + "WHERE s.applicationId.applicationId = :aid "
                    + "ORDER BY s.scoreDate DESC",
                    BigDecimal.class
            )
                    .setParameter("aid", applicationId)
                    .setMaxResults(1)
                    .getSingleResult();

        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    @Override
    public String getScreeningLevel(int applicationId) {

        try {

            return em.createQuery(
                    "SELECT s.screeningLevel FROM Tblscreeningscore s "
                    + "WHERE s.applicationId.applicationId = :aid "
                    + "ORDER BY s.scoreDate DESC",
                    String.class
            )
                    .setParameter("aid", applicationId)
                    .setMaxResults(1)
                    .getSingleResult();

        } catch (Exception e) {
            return "NOT_SCORED";
        }
    }

    @Override
    public Collection<Tblapplication> getApplicationsByStatus(int recruiterId, String status) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Collection<Tblapplication> searchRecruiterCandidates(int recruiterId, String keyword) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Tblapplication getApplicationDetails(int applicationId, int recruiterId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateApplicationStatus(int applicationId, int recruiterId, String newStatus) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
