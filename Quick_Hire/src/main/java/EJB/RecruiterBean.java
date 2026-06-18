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
import java.text.SimpleDateFormat;
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

    private String formatNotificationDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
        return sdf.format(date);
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

    @Override
    public void uploadProfilePhoto(Integer userId, String photo) {
        try {
            Tblusers user = em.find(Tblusers.class, userId);

            if (user == null) {
                throw new RuntimeException("User not found");
            }

            user.setProfilePhoto(photo);
            em.merge(user);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

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
            Tblnotification notification = new Tblnotification();

            notification.setSenderUserId(recruiter.getUserId());
            notification.setReceiverUserId(recruiter.getUserId());

            notification.setNotificationTitle("Job Published Successfully");
            notification.setNotificationMessage(
                    "Your job listing \""
                    + job.getJobTitle()
                    + "\" in "
                    + job.getJobLocation()
                    + " is now live on QuickHire. Candidates matching your requirements will be able to discover and apply to this role."
            );

            notification.setNotificationType("JOB_POSTED");

            notification.setIsRead(false);
            notification.setCreatedDate(new Date());
            em.persist(notification);

            // ================= SEND EMAIL =================
            Tblusers recruiterUser
                    = recruiter.getUserId();

            if (recruiterUser != null) {

                String email = recruiterUser.getUserEmail();
                String subject = "Job Published Successfully — QuickHire";
                String message
                        = "Hello " + recruiterUser.getUserName() + ",\n\n"
                        + "Your job listing has been published successfully on QuickHire.\n\n"
                        + "Job Details:\n"
                        + "  Title    : " + job.getJobTitle() + "\n"
                        + "  Location : " + job.getJobLocation() + "\n\n"
                        + "Candidates matching your requirements can now discover and apply to this role.\n"
                        + "You can manage this listing, review applications, and update job details from your recruiter dashboard.\n\n"
                        + "Warm regards,\n"
                        + "QuickHire Recruitment Team";

                emailService.sendEmail(email, subject, message);
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

            notification.setSenderUserId(recruiterUser);
            notification.setReceiverUserId(recruiterUser);
            notification.setNotificationTitle("Job Listing Updated");
            notification.setNotificationMessage(
                    "Your job listing \""
                    + existingJob.getJobTitle()
                    + "\" has been updated successfully. The changes are now live on QuickHire."
            );
            notification.setNotificationType("JOB_UPDATED");
            notification.setIsRead(false);
            notification.setCreatedDate(new Date());

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
            notification.setNotificationTitle("Job Status Updated");
            notification.setNotificationMessage(
                    "The status of your job listing \""
                    + job.getJobTitle()
                    + "\" has been changed from "
                    + oldStatus
                    + " to "
                    + newStatus
                    + "."
            );
            notification.setNotificationType("JOB_STATUS");
            notification.setIsRead(false);
            notification.setCreatedDate(new Date());

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
                notification.setNotificationTitle("Job Listing Closed — Expiry Reached");
                notification.setNotificationMessage(
                        "Your job listing \""
                        + job.getJobTitle()
                        + "\" has been automatically closed as it reached its expiry date. "
                        + "You can reopen it by updating the expiry date from your dashboard."
                );
                notification.setNotificationType("JOB_STATUS");
                notification.setIsRead(false);
                notification.setCreatedDate(new Date());

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

    // ================= JOB SKILLS ===========================================================================
    @Override
    public Collection<Tblskills> getAllSkills(Integer userId) {

        try {
            return em.createQuery(
                    "SELECT s FROM Tblskills s "
                    + "WHERE s.skillStatus = 'APPROVED' "
                    + "AND s.categoryId.categoryStatus = 'APPROVED' "
                    + "ORDER BY s.skillName",
                    Tblskills.class)
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

    @Override
    public Collection<Tblskills> getSkillsByCategory(Integer categoryId, Integer userId) {

        try {
            return em.createQuery(
                    "SELECT s FROM Tblskills s "
                    + "WHERE s.categoryId.categoryId = :categoryId "
                    + "AND s.skillStatus = 'APPROVED' "
                    + "AND s.categoryId.categoryStatus = 'APPROVED' "
                    + "ORDER BY s.skillName",
                    Tblskills.class)
                    .setParameter("categoryId", categoryId)
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
                throw new RuntimeException("Recruiter user not found");
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
                    throw new RuntimeException("This Category alredy exists.");
                }

                // CREATE CATEGORY
                category = new Tblskillcategory();

                category.setCategoryName(categoryName.trim());
                category.setCategoryStatus("PENDING");
                category.setCreatedByUserId(recruiterUserId);
                category.setCreatedDate(new Date());

                em.persist(category);
                em.flush();

                saveNotification(recruiterUser, recruiterUser,
                        "Category Request Submitted — Pending Approval",
                        category.getCategoryName() + " has been successfully sent for approval.",
                        "CATEGORY_REQUEST");

                notifyAdmins(recruiterUser,
                        "New Skill Category Approval Request",
                        recruiterUser.getUserName()
                        + " has requested the addition of a new skill category: \""
                        + category.getCategoryName()
                        + "\". Please review and approve or reject this request from the admin panel.",
                        "CATEGORY_PENDING");
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

                throw new RuntimeException("Please select category");
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

                    saveNotification(recruiterUser, recruiterUser,
                            "Skill Request Submitted — Pending Approval",
                            skill.getSkillName() + " has been successfully sent for approval.",
                            "SKILL_REQUEST");

                    notifyAdmins(recruiterUser,
                            "New Skill Approval Request",
                            recruiterUser.getUserName()
                            + " has requested the addition of a new skill: \""
                            + skill.getSkillName()
                            + "\". Please review and approve or reject this request from the admin panel.",
                            "SKILL_PENDING");
                }
            }
            em.flush();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public long getJobApplicationCount(Integer jobId, String status) {
        if (jobId == null || jobId <= 0) {
            return 0;
        }

        if (status == null || status.trim().isEmpty()) {
            return em.createQuery(
                    "SELECT COUNT(a) FROM Tblapplication a "
                    + "WHERE a.jobId.jobId = :jobId",
                    Long.class)
                    .setParameter("jobId", jobId)
                    .getSingleResult();
        }

        return em.createQuery(
                "SELECT COUNT(a) FROM Tblapplication a "
                + "WHERE a.jobId.jobId = :jobId "
                + "AND LOWER(TRIM(a.applicationStatus)) = LOWER(TRIM(:status))",
                Long.class)
                .setParameter("jobId", jobId)
                .setParameter("status", status)
                .getSingleResult();
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
    public String getCandidateSkillsTextByApplication(int applicationId) {
        try {
            Tblapplication app = em.find(Tblapplication.class, applicationId);
            if (app == null || app.getCandidateId() == null) {
                return "Skills not provided";
            }

            Collection<Tblskills> skills = app.getCandidateId().getTblskillsCollection();
            if (skills == null || skills.isEmpty()) {
                return "Skills not provided";
            }

            List<String> names = new ArrayList<>();
            for (Tblskills skill : skills) {
                if (skill != null && skill.getSkillName() != null && !skill.getSkillName().trim().isEmpty()) {
                    names.add(skill.getSkillName().trim());
                }
            }

            return names.isEmpty() ? "Skills not provided" : String.join(", ", names);

        } catch (Exception e) {
            e.printStackTrace();
            return "Skills not provided";
        }
    }

    @Override
    public String getCandidateEducationTextByApplication(int applicationId) {
        try {
            Tblapplication app = em.find(Tblapplication.class, applicationId);
            if (app == null || app.getCandidateId() == null) {
                return "Education not provided";
            }

            Collection<Tblcandidateeducation> educations = app.getCandidateId().getTblcandidateeducationCollection();
            if (educations == null || educations.isEmpty()) {
                return "Education not provided";
            }

            List<String> names = new ArrayList<>();
            for (Tblcandidateeducation edu : educations) {
                if (edu != null && edu.getEducationName() != null && !edu.getEducationName().trim().isEmpty()) {
                    names.add(edu.getEducationName().trim());
                }
            }

            return names.isEmpty() ? "Education not provided" : String.join(", ", names);

        } catch (Exception e) {
            e.printStackTrace();
            return "Education not provided";
        }
    }

    @Override
    public Collection<Tblapplication> getRecruiterApplications(int recruiterId) {

        try {

            return em.createQuery(
                    "SELECT DISTINCT a FROM Tblapplication a "
                    + "JOIN FETCH a.candidateId c "
                    + "JOIN FETCH c.userId "
                    + "JOIN FETCH a.jobId j "
                    + "LEFT JOIN FETCH a.resumeId r "
                    + "WHERE j.recruiterId.recruiterId = :rid "
                    + "ORDER BY a.applicationAppliedDate DESC",
                    Tblapplication.class)
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
            } else if (candidateSkills.isEmpty()) {
                skillScore = 0;
            } else {
                for (Tblskills jobSkill : jobSkills) {
                    for (Tblskills candidateSkill : candidateSkills) {
                        if (jobSkill.getSkillId() != null
                                && candidateSkill.getSkillId() != null
                                && jobSkill.getSkillId().equals(candidateSkill.getSkillId())) {

                            matchedSkills++;
                            break;
                        }
                    }
                }

                skillScore = ((double) matchedSkills / jobSkills.size()) * 50;
            }

            // =========================
            // 2. EXPERIENCE SCORE - 30
            // Experience is saved in MONTHS
            // =========================
            int requiredMonths = job.getExperienceRequired() != null ? job.getExperienceRequired() : 0;
            int candidateMonths = candidate.getCandidateExperience() != null ? candidate.getCandidateExperience() : 0;

            double expScore = 0;

            if (requiredMonths <= 0) {
                expScore = 0;
            } else if (candidateMonths <= 0) {
                expScore = 0;
            } else {
                double ratio = (double) candidateMonths / requiredMonths;

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

            // =========================
            // 3. EDUCATION SCORE - 20
            // Education is OPTIONAL
            // =========================
            double eduScore = 0;
            long matchedEducation = 0;
            boolean educationRequired = !jobEducations.isEmpty();

            if (!educationRequired) {
            } else if (candidateEducations.isEmpty()) {
                eduScore = 0;
            } else {
                for (Tbleducation jobEdu : jobEducations) {
                    for (Tblcandidateeducation candidateEdu : candidateEducations) {

                        String jobEduName = jobEdu.getEducationName();
                        String candidateEduName = candidateEdu.getEducationName();

                        if (jobEduName != null
                                && candidateEduName != null
                                && jobEduName.trim().equalsIgnoreCase(candidateEduName.trim())) {

                            matchedEducation++;
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
                "Application Shortlisted — QuickHire",
                "Congratulations! Your application has been shortlisted. "
                + "Our recruitment team is currently reviewing your profile in detail "
                + "and will be in touch shortly regarding the next steps in the hiring process."
        );
    }

    @Override
    public void rejectApplication(int applicationId) {
        updateApplicationStatusWithHistoryAndNotification(
                applicationId,
                "Rejected",
                "Application Update — QuickHire",
                "Thank you for your interest and the time you invested in applying. "
                + "After careful consideration of all applications, we regret to inform you "
                + "that we will not be moving forward with your application at this time. "
                + "We encourage you to keep your profile updated and explore other opportunities on QuickHire."
        );
    }

    @Override
    public long getRejectedApplicationCount(int recruiterId) {
        try {
            return em.createQuery(
                    "SELECT COUNT(a) FROM Tblapplication a "
                    + "WHERE a.jobId.recruiterId.recruiterId = :rid "
                    + "AND a.applicationStatus = 'Rejected'",
                    Long.class)
                    .setParameter("rid", recruiterId)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void updateApplicationStatusWithHistoryAndNotification(int applicationId, String newStatus, String notificationTitle, String candidateMessage) {
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
            em.flush();

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
                    "The application of "
                    + candidateUser.getUserName()
                    + " for \""
                    + app.getJobId().getJobTitle()
                    + "\" has been updated to: "
                    + newStatus + ".",
                    "APPLICATION_STATUS"
            );

            if (candidateUser.getUserEmail() != null) {

                // ================= BUILD EMAIL BODY BASED ON STATUS =================
                String emailBody;

                if ("Shortlisted".equalsIgnoreCase(newStatus)) {
                    emailBody = "Hello " + candidateUser.getUserName() + ",\n\n"
                            + "We are pleased to inform you that your application for the position of \""
                            + app.getJobId().getJobTitle()
                            + "\" at QuickHire has been shortlisted.\n\n"
                            + "Your profile stood out among many applicants, and our recruitment team "
                            + "is currently reviewing your details further.\n\n"
                            + "What happens next?\n"
                            + "  \u2022 Our team will review your profile in detail.\n"
                            + "  \u2022 You may be contacted for an interview or further assessment.\n"
                            + "  \u2022 Please ensure your contact details are up to date on your QuickHire profile.\n\n"
                            + "We appreciate your interest and will be in touch soon.\n\n"
                            + "Warm regards,\n"
                            + "QuickHire Recruitment Team";

                } else if ("Rejected".equalsIgnoreCase(newStatus)) {
                    emailBody = "Hello " + candidateUser.getUserName() + ",\n\n"
                            + "Thank you for taking the time to apply for the position of \""
                            + app.getJobId().getJobTitle()
                            + "\" at QuickHire.\n\n"
                            + "After a thorough review of all applications, we regret to inform you "
                            + "that we will not be moving forward with your application at this stage. "
                            + "This was a competitive process, and we appreciate the effort you put into your application.\n\n"
                            + "We encourage you to:\n"
                            + "  \u2022 Keep your profile and skills updated on QuickHire.\n"
                            + "  \u2022 Apply for other roles that match your expertise.\n"
                            + "  \u2022 Continue building your experience for future opportunities.\n\n"
                            + "We wish you the very best in your job search and future career endeavours.\n\n"
                            + "Kind regards,\n"
                            + "QuickHire Recruitment Team";

                } else {
                    emailBody = "Hello " + candidateUser.getUserName() + ",\n\n"
                            + candidateMessage + "\n\n"
                            + "Job: " + app.getJobId().getJobTitle() + "\n"
                            + "Status: " + newStatus + "\n\n"
                            + "Regards,\n"
                            + "QuickHire Recruitment Team";
                }

                emailService.sendEmail(
                        candidateUser.getUserEmail(),
                        notificationTitle,
                        emailBody
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void saveNotification(Tblusers senderUser, Tblusers receiverUser, String title, String message, String type) {

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

    private void notifyAdmins(Tblusers recruiterUser, String title, String message, String type) {
        Collection<Tblusers> admins = em.createQuery(
                "SELECT u FROM Tblusers u WHERE u.roleId.roleName = :roleName",
                Tblusers.class)
                .setParameter("roleName", "Admin")
                .getResultList();

        for (Tblusers admin : admins) {
            saveNotification(recruiterUser, admin, title, message, type);
        }
    }

    // ================= INTERVIEW =================
    @Override
    public void scheduleInterview(Tblinterview interview) {

        try {

            // ================= VALIDATION =================
            if (interview == null
                    || interview.getApplicationId() == null
                    || interview.getApplicationId().getApplicationId() == null) {
                if (interview.getInterviewDate() == null) {
                    throw new RuntimeException("Interview date is required");
                }

                if (interview.getInterviewerName() == null
                        || interview.getInterviewerName().trim().isEmpty()) {

                    throw new RuntimeException("Interviewer name is required");
                }

                if (interview.getInterviewerMode() == null
                        || interview.getInterviewerMode().trim().isEmpty()) {

                    throw new RuntimeException("Interview mode is required");
                }
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
            interview.setInterviewStatus("Scheduled");
            interview.setInterviewRound(1);
            interview.setInterviewRoundName("Interview Round");
            interview.setResult("Pending");
            interview.setFeedback(null);

            // ================= SAVE INTERVIEW =================
            em.persist(interview);

            // ================= UPDATE APPLICATION STATUS =================
            app.setApplicationStatus("Interview Scheduled");
            app.setLastUpdatedDate(new Date());

            em.merge(app);
            em.flush();

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
                    "Interview Scheduled — QuickHire",
                    "Your interview for the position of \""
                    + app.getJobId().getJobTitle()
                    + "\" has been scheduled. Please check your email for the complete interview details "
                    + "and ensure you are available at the scheduled time.",
                    "INTERVIEW"
            );

            // ================= SAVE RECRUITER ACTIVITY =================
            saveNotification(
                    recruiterUser,
                    recruiterUser,
                    "Interview Scheduled",
                    "An interview has been scheduled with "
                    + candidateUser.getUserName()
                    + " for the position of \""
                    + app.getJobId().getJobTitle()
                    + "\" on "
                    + formatNotificationDate(interview.getInterviewDate()) + ".",
                    "INTERVIEW"
            );

            // ================= EMAIL SECTION =================
            if (candidateUser.getUserEmail() != null) {

                String modeDetails;

                // ================= ONLINE =================
                if ("Online".equalsIgnoreCase(interview.getInterviewerMode())) {
                    modeDetails
                            = "  Mode    : Online\n"
                            + "  Details : Your meeting link and access credentials will be shared with you "
                            + "shortly via this email. Please ensure your device, camera, and microphone "
                            + "are working properly before the session.";

                } // ================= OFFLINE =================
                else if ("Offline".equalsIgnoreCase(interview.getInterviewerMode())) {
                    modeDetails
                            = "  Mode    : In-Person (Offline)\n"
                            + "  Details : The interview will be held at our office. The exact venue address "
                            + "and reporting instructions will be shared with you by the recruiter prior to the date. "
                            + "Please carry a printed copy of your resume and any relevant documents.";

                } // ================= PHONE =================
                else if ("Phone".equalsIgnoreCase(interview.getInterviewerMode())) {
                    modeDetails
                            = "  Mode    : Phone Interview\n"
                            + "  Details : The interviewer will call you on your registered phone number at the "
                            + "scheduled time. Please ensure you are in a quiet environment and reachable at that number.";

                } // ================= DEFAULT FALLBACK =================
                else {
                    modeDetails
                            = "  Mode    : " + interview.getInterviewerMode() + "\n"
                            + "  Details : Further information regarding the interview mode will be shared with you shortly.";
                }

                String subject = "Interview Scheduled — QuickHire";

                String message
                        = "Hello " + candidateUser.getUserName() + ",\n\n"
                        + "Congratulations on progressing to the interview stage!\n\n"
                        + "We are pleased to inform you that your interview for the position of \""
                        + app.getJobId().getJobTitle()
                        + "\" at QuickHire has been scheduled.\n\n"
                        + "Interview Details:\n"
                        + "  Date & Time  : " + formatNotificationDate(interview.getInterviewDate()) + "\n"
                        + "  Interviewer  : " + interview.getInterviewerName() + "\n"
                        + modeDetails + "\n\n"
                        + "Tips to prepare:\n"
                        + "  \u2022 Research the role and review the job description beforehand.\n"
                        + "  \u2022 Keep a copy of your resume and any supporting documents handy.\n"
                        + "  \u2022 Be available and ready 5\u201310 minutes before the scheduled time.\n\n"
                        + "If you have any questions or need to reschedule, please reach out through your QuickHire portal.\n\n"
                        + "We look forward to speaking with you!\n\n"
                        + "Warm regards,\n"
                        + "QuickHire Recruitment Team\n\n"
                        + "---\n"
                        + "Note: This interview invitation does not constitute an offer of employment. "
                        + "A formal offer, if made, will be communicated separately in writing upon "
                        + "the successful completion of the hiring process.";

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

    @Override
    public Collection<Tblinterview> getInterviewHistoryByApplication(Integer applicationId) {
        return em.createQuery(
                "SELECT i FROM Tblinterview i "
                + "WHERE i.applicationId.applicationId = :applicationId "
                + "ORDER BY i.interviewId DESC",
                Tblinterview.class)
                .setParameter("applicationId", applicationId)
                .getResultList();
    }

    @Override
    public Collection<Tblinterview> getRecruiterInterviews(Integer recruiterId) {
        return em.createQuery(
                "SELECT i FROM Tblinterview i "
                + "WHERE i.applicationId.jobId.recruiterId.recruiterId = :rid "
                + "AND i.interviewId = ("
                + "    SELECT MAX(i2.interviewId) "
                + "    FROM Tblinterview i2 "
                + "    WHERE i2.applicationId.applicationId = i.applicationId.applicationId"
                + ") "
                + "ORDER BY "
                + "CASE "
                + "WHEN i.interviewStatus IN ('Scheduled', 'Rescheduled') THEN 1 "
                + "WHEN i.interviewStatus = 'Completed' THEN 2 "
                + "WHEN i.interviewStatus = 'Cancelled' THEN 3 "
                + "ELSE 4 END, "
                + "i.interviewDate DESC",
                Tblinterview.class)
                .setParameter("rid", recruiterId)
                .getResultList();
    }

    @Override
    public Long getCompletedInterviewCount(Integer recruiterId) {
        return em.createQuery(
                "SELECT COUNT(i) FROM Tblinterview i "
                + "WHERE i.applicationId.jobId.recruiterId.recruiterId = :rid "
                + "AND i.interviewId = ("
                + "    SELECT MAX(i2.interviewId) FROM Tblinterview i2 "
                + "    WHERE i2.applicationId.applicationId = i.applicationId.applicationId"
                + ") "
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
                + "AND i.interviewId = ("
                + "    SELECT MAX(i2.interviewId) FROM Tblinterview i2 "
                + "    WHERE i2.applicationId.applicationId = i.applicationId.applicationId"
                + ") "
                + "AND i.interviewStatus IN :statuses",
                Long.class)
                .setParameter("rid", recruiterId)
                .setParameter("statuses", java.util.Arrays.asList("Scheduled", "Rescheduled"))
                .getSingleResult();
    }

    @Override
    public Long getSelectedCount(Integer recruiterId) {
        return em.createQuery(
                "SELECT COUNT(i) FROM Tblinterview i "
                + "WHERE i.applicationId.jobId.recruiterId.recruiterId = :rid "
                + "AND i.interviewId = ("
                + "    SELECT MAX(i2.interviewId) FROM Tblinterview i2 "
                + "    WHERE i2.applicationId.applicationId = i.applicationId.applicationId"
                + ") "
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
                + "AND i.interviewId = ("
                + "    SELECT MAX(i2.interviewId) FROM Tblinterview i2 "
                + "    WHERE i2.applicationId.applicationId = i.applicationId.applicationId"
                + ") "
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
                + "WHERE i.applicationId.jobId.recruiterId.recruiterId = :rid "
                + "AND i.interviewId = ("
                + "    SELECT MAX(i2.interviewId) FROM Tblinterview i2 "
                + "    WHERE i2.applicationId.applicationId = i.applicationId.applicationId"
                + ")",
                Long.class)
                .setParameter("rid", recruiterId)
                .getSingleResult();
    }

    @Override
    public void conductInterview(Integer interviewId, String feedback, String result) {
        try {
            Tblinterview currentInterview = em.find(Tblinterview.class, interviewId);

            if (currentInterview == null) {
                throw new RuntimeException("Interview not found");
            }

            Tblapplication app = currentInterview.getApplicationId();

            Tblinterview completedInterview = new Tblinterview();
            completedInterview.setApplicationId(app);
            completedInterview.setInterviewDate(currentInterview.getInterviewDate());
            completedInterview.setInterviewerName(currentInterview.getInterviewerName());
            completedInterview.setInterviewerMode(currentInterview.getInterviewerMode());
            completedInterview.setInterviewStatus("Completed");
            completedInterview.setFeedback(feedback);
            completedInterview.setResult(result);

            em.persist(completedInterview);

            app.setApplicationStatus(result);
            app.setLastUpdatedDate(new Date());
            em.merge(app);
            em.flush();

            Tblusers recruiterUser = app.getJobId().getRecruiterId().getUserId();
            Tblusers candidateUser = app.getCandidateId().getUserId();

            // ================= SELECTED =================
            if ("Selected".equalsIgnoreCase(result)) {

                saveNotification(recruiterUser, candidateUser,
                        "Congratulations — You Have Been Selected!",
                        "We are delighted to inform you that you have been selected for the position of \""
                        + app.getJobId().getJobTitle()
                        + "\" at QuickHire. Our HR team will be in touch shortly with further details regarding the formal offer and next steps.",
                        "APPLICATION_STATUS");

                saveNotification(recruiterUser, recruiterUser,
                        "Interview Completed — Candidate Selected",
                        candidateUser.getUserName()
                        + " has been marked as Selected for \""
                        + app.getJobId().getJobTitle()
                        + "\". The candidate will be notified and an offer can now be initiated.",
                        "APPLICATION_STATUS");

                if (candidateUser.getUserEmail() != null) {
                    emailService.sendEmail(
                            candidateUser.getUserEmail(),
                            "Congratulations — You Have Been Selected! — QuickHire",
                            "Hello " + candidateUser.getUserName() + ",\n\n"
                            + "We are delighted to inform you that you have been selected for the position of \""
                            + app.getJobId().getJobTitle()
                            + "\" at QuickHire!\n\n"
                            + "You impressed our recruitment panel throughout the process, and we are excited "
                            + "about the possibility of you joining our team.\n\n"
                            + "What happens next?\n"
                            + "  \u2022 A member of our HR team will contact you within 2\u20133 business days.\n"
                            + "  \u2022 You will receive a formal offer letter outlining your role, compensation, and joining details.\n"
                            + "  \u2022 Please review the offer carefully and revert with your acceptance or any queries.\n\n"
                            + "If you have any questions in the meantime, please reach out through your QuickHire portal.\n\n"
                            + "Once again, congratulations \u2014 we look forward to welcoming you aboard!\n\n"
                            + "Warm regards,\n"
                            + "QuickHire Recruitment Team\n\n"
                            + "---\n"
                            + "Note: This notification is a preliminary communication indicating selection intent. "
                            + "A formal, binding offer of employment will be issued separately via a written offer letter. "
                            + "Employment is subject to the successful completion of any background verification "
                            + "or documentation requirements."
                    );
                }

            } // ================= NOT SELECTED =================
            else {

                saveNotification(recruiterUser, candidateUser,
                        "Interview Outcome — QuickHire",
                        "Thank you for attending the interview for \""
                        + app.getJobId().getJobTitle()
                        + "\" at QuickHire. After thorough deliberation, we will not be proceeding "
                        + "with your application at this time. We wish you the very best in your career journey.",
                        "APPLICATION_STATUS");

                saveNotification(recruiterUser, recruiterUser,
                        "Interview Completed — Candidate Not Selected",
                        candidateUser.getUserName()
                        + " has been marked as "
                        + result
                        + " for \""
                        + app.getJobId().getJobTitle() + "\".",
                        "APPLICATION_STATUS");

                if (candidateUser.getUserEmail() != null) {
                    emailService.sendEmail(
                            candidateUser.getUserEmail(),
                            "Interview Outcome — QuickHire",
                            "Hello " + candidateUser.getUserName() + ",\n\n"
                            + "Thank you for taking the time to interview with us for the position of \""
                            + app.getJobId().getJobTitle()
                            + "\" at QuickHire. We genuinely appreciated the opportunity to learn more about your background and experience.\n\n"
                            + "After careful deliberation, we have decided to move forward with another candidate "
                            + "whose profile more closely matched our requirements at this time. "
                            + "This was not an easy decision given the calibre of applicants we received.\n\n"
                            + "We encourage you to:\n"
                            + "  \u2022 Keep your QuickHire profile updated with your latest skills and experience.\n"
                            + "  \u2022 Continue exploring other roles on our platform that may be a great fit.\n\n"
                            + "We will retain your profile and may reach out for future opportunities that align with your expertise.\n\n"
                            + "Thank you again for your interest in QuickHire, and we wish you every success in your career.\n\n"
                            + "Kind regards,\n"
                            + "QuickHire Recruitment Team"
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void rescheduleInterview(Integer interviewId, Date interviewDate, String interviewerName, String interviewerMode) {
        Tblinterview oldInterview = em.find(Tblinterview.class, interviewId);

        if (oldInterview == null) {
            throw new RuntimeException("Interview not found");
        }

        oldInterview.setInterviewStatus("Rescheduled");
        em.merge(oldInterview);

        Tblapplication app = oldInterview.getApplicationId();

        Tblinterview newInterview = new Tblinterview();
        newInterview.setApplicationId(app);
        newInterview.setInterviewDate(interviewDate);
        newInterview.setInterviewerName(interviewerName);
        newInterview.setInterviewerMode(interviewerMode);
        newInterview.setInterviewStatus("Scheduled");
        newInterview.setInterviewRound(1);
        newInterview.setInterviewRoundName("Interview Round");
        newInterview.setResult("Pending");
        newInterview.setFeedback(null);

        em.persist(newInterview);

        app.setApplicationStatus("Interview Scheduled");
        app.setLastUpdatedDate(new Date());
        em.merge(app);
        em.flush();

        Tblusers recruiterUser = app.getJobId().getRecruiterId().getUserId();
        Tblusers candidateUser = app.getCandidateId().getUserId();

        // ================= CANDIDATE NOTIFICATION =================
        saveNotification(
                recruiterUser,
                candidateUser,
                "Interview Rescheduled — QuickHire",
                "Your interview for \""
                + app.getJobId().getJobTitle()
                + "\" has been rescheduled. "
                + "Please check your email for the updated date and interview details. "
                + "Your application remains active and we look forward to speaking with you.",
                "INTERVIEW"
        );

        // ================= RECRUITER ACTIVITY =================
        saveNotification(
                recruiterUser,
                recruiterUser,
                "Interview Rescheduled",
                "The interview with "
                + candidateUser.getUserName()
                + " for \""
                + app.getJobId().getJobTitle()
                + "\" has been rescheduled to "
                + formatNotificationDate(interviewDate) + ".",
                "INTERVIEW"
        );

        // ================= EMAIL TO CANDIDATE =================
        if (candidateUser.getUserEmail() != null) {
            emailService.sendEmail(
                    candidateUser.getUserEmail(),
                    "Interview Rescheduled — QuickHire",
                    "Hello " + candidateUser.getUserName() + ",\n\n"
                    + "We would like to inform you that your interview for the position of \""
                    + app.getJobId().getJobTitle()
                    + "\" at QuickHire has been rescheduled.\n\n"
                    + "Updated Interview Details:\n"
                    + "  New Date & Time : " + formatNotificationDate(interviewDate) + "\n"
                    + "  Interviewer      : " + interviewerName + "\n"
                    + "  Mode             : " + interviewerMode + "\n\n"
                    + "We apologise for any inconvenience this change may have caused. "
                    + "Your application remains active, and we look forward to speaking with you at the rescheduled time.\n\n"
                    + "If this new date does not work for you, please reach out through your QuickHire portal at your earliest convenience.\n\n"
                    + "Kind regards,\n"
                    + "QuickHire Recruitment Team"
            );
        }
    }

    @Override
    public void cancelInterview(Integer interviewId) {
        Tblinterview currentInterview = em.find(Tblinterview.class, interviewId);

        if (currentInterview != null) {
            Tblapplication app = currentInterview.getApplicationId();

            Tblinterview cancelledInterview = new Tblinterview();
            cancelledInterview.setApplicationId(app);
            cancelledInterview.setInterviewDate(currentInterview.getInterviewDate());
            cancelledInterview.setInterviewerName(currentInterview.getInterviewerName());
            cancelledInterview.setInterviewerMode(currentInterview.getInterviewerMode());
            cancelledInterview.setInterviewStatus("Cancelled");
            cancelledInterview.setResult("Pending");
            cancelledInterview.setFeedback(null);
            em.persist(cancelledInterview);

            app.setApplicationStatus("Shortlisted");
            app.setLastUpdatedDate(new Date());
            em.merge(app);
            em.flush();

            Tblusers recruiterUser = app.getJobId().getRecruiterId().getUserId();
            Tblusers candidateUser = app.getCandidateId().getUserId();

            // ================= CANDIDATE NOTIFICATION =================
            saveNotification(recruiterUser, candidateUser,
                    "Interview Cancelled — QuickHire",
                    "Your scheduled interview for \""
                    + app.getJobId().getJobTitle()
                    + "\" at QuickHire has been cancelled. "
                    + "Please note that your application remains under active consideration. "
                    + "The recruiter will be in touch shortly to arrange a new interview date.",
                    "INTERVIEW");

            // ================= RECRUITER ACTIVITY =================
            saveNotification(recruiterUser, recruiterUser,
                    "Interview Cancelled",
                    "The scheduled interview with "
                    + candidateUser.getUserName()
                    + " for \""
                    + app.getJobId().getJobTitle()
                    + "\" has been cancelled. The candidate's application has been moved back to Shortlisted.",
                    "INTERVIEW");

            // ================= EMAIL TO CANDIDATE =================
            if (candidateUser.getUserEmail() != null) {
                emailService.sendEmail(
                        candidateUser.getUserEmail(),
                        "Interview Cancelled — QuickHire",
                        "Hello " + candidateUser.getUserName() + ",\n\n"
                        + "We regret to inform you that your scheduled interview for the position of \""
                        + app.getJobId().getJobTitle()
                        + "\" at QuickHire has been cancelled.\n\n"
                        + "Please note that your application continues to be under active consideration. "
                        + "The recruiter will be in touch shortly to arrange a new interview date that is convenient for both parties.\n\n"
                        + "We sincerely apologise for the inconvenience and appreciate your patience and understanding.\n\n"
                        + "Kind regards,\n"
                        + "QuickHire Recruitment Team"
                );
            }
        }
    }

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
                    + "AND n.senderUserId.userId = :uid "
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

    @Override
    public Collection<Tblnotification> getActivities(int userId) {
        try {
            return em.createQuery(
                    "SELECT n FROM Tblnotification n "
                    + "WHERE n.receiverUserId.userId = :uid "
                    + "AND n.senderUserId.userId = :uid "
                    + "ORDER BY n.createdDate DESC",
                    Tblnotification.class)
                    .setParameter("uid", userId)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void markAllNotificationsAsRead(int userId) {
        em.createQuery(
                "UPDATE Tblnotification n "
                + "SET n.isRead = TRUE, n.readDate = CURRENT_TIMESTAMP "
                + "WHERE n.receiverUserId.userId = :uid "
                + "AND (n.senderUserId IS NULL OR n.senderUserId.userId <> :uid) "
                + "AND (n.isRead = FALSE OR n.isRead IS NULL)")
                .setParameter("uid", userId)
                .executeUpdate();
    }

    // ================= NOTIFICATION =================
    @Override
    public Collection<Tblnotification> getNotifications(int userId) {
        try {
            return em.createQuery(
                    "SELECT n FROM Tblnotification n "
                    + "WHERE n.receiverUserId.userId = :uid "
                    + "AND (n.senderUserId IS NULL OR n.senderUserId.userId <> :uid) "
                    + "ORDER BY n.createdDate DESC",
                    Tblnotification.class)
                    .setParameter("uid", userId)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void markNotificationAsRead(int notificationId, int userId) {
        Tblnotification notification = em.createQuery(
                "SELECT n FROM Tblnotification n "
                + "WHERE n.notificationId = :nid "
                + "AND n.receiverUserId.userId = :uid",
                Tblnotification.class)
                .setParameter("nid", notificationId)
                .setParameter("uid", userId)
                .getSingleResult();

        notification.setIsRead(true);
        notification.setReadDate(new Date());
        em.merge(notification);
    }

    //company
}
