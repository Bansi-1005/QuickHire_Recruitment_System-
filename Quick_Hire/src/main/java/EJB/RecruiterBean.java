/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.Tbljobskillweightage;
import Entity.*;
import Validation.RecruiterValidator;
import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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
//    @Override
//    public void createJob(Tbljob job) {
//
//        try {
//
//            // ================= VALIDATION =================
//            RecruiterValidator.validateJob(job);
//
//            // ================= SET POSTED DATE =================
//            job.setJobPostedDate(new Date());
//
//            // ================= FETCH RECRUITER =================
//            Tblrecruiters recruiter
//                    = em.find(
//                            Tblrecruiters.class,
//                            job.getRecruiterId()
//                                    .getRecruiterId()
//                    );
//
//            if (recruiter == null) {
//
//                throw new RuntimeException(
//                        "Recruiter not found"
//                );
//            }
//
//            // ================= SET RECRUITER =================
//            job.setRecruiterId(recruiter);
//
//            // ================= SAVE JOB =================
//            em.persist(job);
//
//            // ================= SAVE ACTIVITY =================
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
//
//            em.persist(activity);
//
//            // ================= SEND EMAIL =================
//            Tblusers recruiterUser
//                    = recruiter.getUserId();
//
//            if (recruiterUser != null) {
//
//                String email
//                        = recruiterUser.getUserEmail();
//
//                String subject
//                        = "Job Posted Successfully";
//
//                String message
//                        = "Your job has been posted successfully.\n\n"
//                        + "Job Title: "
//                        + job.getJobTitle()
//                        + "\n"
//                        + "Location: "
//                        + job.getJobLocation();
//
//                emailService.sendEmail(
//                        email,
//                        subject,
//                        message
//                );
//            }
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//
//            throw e;
//        }
//    }
    @Override
    public void createJob(Tbljob job, Collection<Integer> skillIds) {

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

            // ================= PERSIST JOB  =================
            em.persist(job);

            // ================= SAVE ACTIVITY =================
            Tblnotification activity
                    = new Tblnotification();

            activity.setUserId(
                    recruiter.getUserId()
            );

            activity.setMessage(
                    "New job posted: "
                    + job.getJobTitle()
            );

            activity.setCreatedDate(
                    new Date()
            );

            activity.setNotificationStatus(
                    "Read"
            );

            em.persist(activity);

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



//    @Override
//    public void updateJob(Tbljob job) {
//        try {
//            RecruiterValidator.validateJob(job);
//
//            Tbljob existingJob = em.find(Tbljob.class, job.getJobId());
//
//            if (existingJob != null) {
//
//                // Update ONLY if value is provided (NOT NULL)
//                if (job.getJobTitle() != null) {
//                    existingJob.setJobTitle(job.getJobTitle());
//                }
//
//                if (job.getJobDescription() != null) {
//                    existingJob.setJobDescription(job.getJobDescription());
//                }
//
//                if (job.getJobLocation() != null) {
//                    existingJob.setJobLocation(job.getJobLocation());
//                }
//
//                if (job.getJobStatus() != null) {
//                    existingJob.setJobStatus(job.getJobStatus());
//                }
//
//                if (job.getJobType() != null) {
//                    existingJob.setJobType(job.getJobType());
//                }
//
//                if (job.getExperienceRequired() != null) {
//                    existingJob.setExperienceRequired(job.getExperienceRequired());
//                }
//
//                if (job.getJobCompensationType() != null) {
//                    existingJob.setJobCompensationType(job.getJobCompensationType());
//                }
//
//                if (job.getJobExpiryDate() != null) {
//                    existingJob.setJobExpiryDate(job.getJobExpiryDate());
//                }
//
//                job.setJobPostedDate(existingJob.getJobPostedDate());
//
//                // recruiter update (important)
//                if (job.getRecruiterId() != null) {
//                    Tblrecruiters r = em.find(Tblrecruiters.class, job.getRecruiterId().getRecruiterId());
//                    existingJob.setRecruiterId(r);
//                }
//                em.merge(existingJob);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
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
    /// @param userId}
    /// @return 
//    @Override
//    public void updateJobStatus(int jobId, String status) {
//        try {
//            Tbljob job = em.find(Tbljob.class, jobId);
//            if (job != null) {
//                job.setJobStatus(status);
//                em.merge(job);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
    @Override
    public Collection<Tbljob> getJobs(int recruiterId) {

        try {

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
public Collection<Tblskills> getSkillsByCategory(Integer categoryId,Integer userId) {

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
public void addSkillAndOrCategory(String categoryName,Collection<String> skillNames,Integer existingCategoryId,Integer recruiterUserId){
    try{
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
        
        if(categoryName != null && !categoryName.trim().isEmpty()) 
        {
         
            String trimmedCategory = categoryName.trim();
            
            // CHECK CATEGORY DUPLICATE
            Long catagoryCount = em.createQuery("SELECT COUNT(c) FROM Tblskillcategory c  WHERE LOWER(TRIM(c.categoryName)) = :name",Long.class)
                                                .setParameter("name", trimmedCategory.toLowerCase())
                                                .getSingleResult();
            
            
            if(catagoryCount > 0)
            {
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
            Tblnotification categoryNotification = new Tblnotification();
            
            categoryNotification.setUserId(recruiterUser);
            categoryNotification.setMessage("New Category pending for approval:"+ trimmedCategory);
            categoryNotification.setCreatedDate(new Date());
            categoryNotification.setNotificationStatus("Unread");
            categoryNotification.setNotificationType("Important");
            
            em.persist(categoryNotification);
        }
        // =====================================================
        // USE EXISTING CATEGORY
        // =====================================================
        if(category == null && existingCategoryId != null && existingCategoryId > 0)
        {
            category = em.find(Tblskillcategory.class, existingCategoryId);
            
            if(category == null)
            {
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
        if(skillNames != null && !skillNames.isEmpty())
        {
            for(String skillName :skillNames)
            {
                if(skillName == null || skillName.trim().isEmpty())
                {
                    continue;
                }
                
                String trimmedSkill = skillName.trim();
              
                // GLOBAL SKILL DUPLICATE CHECK
                
                Long skillCount = em.createQuery("SELECT COUNT(S) FROM Tblskills s WHERE LOWER(TRIM(s.skillName)) = :name",Long.class)
                                  .setParameter("name", trimmedSkill.toLowerCase())
                                  .getSingleResult();
                
                if(skillCount > 0)
                {
                    throw new RuntimeException("This skill alredy exists:"+ trimmedSkill);
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
                
                //NOTIFICATION FOR SKILL
                
                Tblnotification skillNotification = new Tblnotification();
                
                skillNotification.setUserId(recruiterUser);
                skillNotification.setMessage("New skill pending for approval: "+ trimmedSkill);
                skillNotification.setCreatedDate(new Date());
                skillNotification.setNotificationStatus("Unread");
                skillNotification.setNotificationType("Important");
                
                em.persist(skillNotification);
            }
        }
        em.flush();
        
    }catch(Exception e)
    {
        e.printStackTrace();
        throw e;
    }
}


// ================= JOB Education =============================================================================
@Override
public Collection<Tbleducation> getAllEducation()
{
    try{

        return em.createNamedQuery("Tbleducation.findAll", Tbleducation.class).getResultList();

    }catch(Exception e)
    {
        e.printStackTrace();

        return new ArrayList<>();
    }
}












//    @Override
//    public void addSkillToJob(int jobId, int skillId) {
//        try {
//            Tbljob job = em.find(Tbljob.class, jobId);
//            Tblskills skill = em.find(Tblskills.class, skillId);
//
//            if (job == null || skill == null) {
//                return;
//            }
//
//            if (job.getTblskillsCollection() == null) {
//                job.setTblskillsCollection(new ArrayList<>());
//            }
//
//            // prevent duplicate
//            if (!job.getTblskillsCollection().contains(skill)) {
//                job.getTblskillsCollection().add(skill);
//            }
//
//            em.merge(job);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void removeSkillFromJob(int jobId, int skillId) {
//        try {
//            Tbljob job = em.find(Tbljob.class, jobId);
//            Tblskills skill = em.find(Tblskills.class, skillId);
//
//            if (job != null && skill != null) {
//                job.getTblskillsCollection().remove(skill);
//                em.merge(job);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
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
//
//    @Override
//    public void saveJobSkillWeightage(int jobId, Map<Integer, Integer> skillWeightMap) {
//        try {
//            Tbljob job = em.find(Tbljob.class, jobId);
//
//            if (job == null) {
//                throw new RuntimeException("Job not found");
//            }
//
//            for (Map.Entry<Integer, Integer> entry : skillWeightMap.entrySet()) {
//
//                Integer skillId = entry.getKey();
//                Integer weight = entry.getValue();
//
//                Tblskills skill = em.find(Tblskills.class, skillId);
//
//                if (skill == null) {
//                    continue;
//                }
//
//                // check if mapping already exists
//                Tbljobskillweightage existing = null;
//
//                try {
//                    existing = em.createQuery(
//                            "SELECT j FROM Tbljobskillweightage j WHERE j.jobId.jobId = :jobId AND j.skillId.skillId = :skillId",
//                            Tbljobskillweightage.class)
//                            .setParameter("jobId", jobId)
//                            .setParameter("skillId", skillId)
//                            .getSingleResult();
//                } catch (Exception e) {
//                    existing = null;
//                }
//
//                if (existing != null) {
//                    // UPDATE
//                    existing.setSkillWeightage(weight);
//                    em.merge(existing);
//                } else {
//                    // INSERT
//                    Tbljobskillweightage jw = new Tbljobskillweightage();
//                    jw.setJobId(job);
//                    jw.setSkillId(skill);
//                    jw.setSkillWeightage(weight);
//
//                    em.persist(jw);
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        }
//    }
//
//    @Override
//    public Collection<Tbljobskillweightage> getJobSkillWeightage(int jobId) {
//
//        return em.createQuery(
//                "SELECT j FROM Tbljobskillweightage j WHERE j.jobId.jobId = :jobId",
//                Tbljobskillweightage.class
//        )
//                .setParameter("jobId", jobId)
//                .getResultList();
//    }
//
//    @Override
//    public void updateJobSkillWeightage(
//            int jobId,
//            int skillId,
//            int weightage
//    ) {
//        try {
//
//            Tbljobskillweightage jw = em.createQuery(
//                    "SELECT j FROM Tbljobskillweightage j WHERE j.jobId.jobId = :jobId AND j.skillId.skillId = :skillId",
//                    Tbljobskillweightage.class
//            )
//                    .setParameter("jobId", jobId)
//                    .setParameter("skillId", skillId)
//                    .getSingleResult();
//
//            jw.setSkillWeightage(weightage);
//
//            em.merge(jw);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
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
                    + "WHERE n.userId.userId = :uid "
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
