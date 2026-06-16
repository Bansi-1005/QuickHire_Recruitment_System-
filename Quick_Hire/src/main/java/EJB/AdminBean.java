/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.*;
import jakarta.annotation.security.DeclareRoles;
import jakarta.ejb.Stateless;
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
@DeclareRoles({"Admin", "Recruiter", "Candidate"})
public class AdminBean implements AdminBeanLocal {

    @PersistenceContext(unitName = "jpu")
    EntityManager em;

    @Inject
    private Pbkdf2PasswordHash hash;

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
//    @Override
//    public void addRole(Tblrolemaster role) {
//        try {
//            if (role == null) return;
//            role.setCreatedDate(new Date());
//            em.persist(role);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public Collection<Tblrolemaster> getRoles() {
//        try {
//            return em.createNamedQuery("Tblrolemaster.findAll", Tblrolemaster.class)
//                    .getResultList();
//        } catch (Exception e) {
//            return new ArrayList<>();
//        }
//    }
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
            if (userStatus == null || userStatus.trim().isEmpty()) {
                return;
            }

            Tblusers user = em.find(Tblusers.class, userId);
            if (user != null) {
//                user.setUserStatus(userStatus);
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

    @Override
    public void toggleUserStatus(int userId, boolean status) {

        Tblusers user = em.find(Tblusers.class, userId);

        if (user != null) {

            user.setUserIsActive(status);

            em.merge(user);
        }
    }

    // ================= RECRUITER =================
    @Override
    public Collection<Tblrecruiters> getAllRecruiters() {
        try {
            return em.createNamedQuery("Tblrecruiters.findAll", Tblrecruiters.class)
                    .getResultList();
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

    // ================= Manage Skills =================
    @Override
    public Collection<Tblskillcategory> getAllSkillCategories() {

        try {

            return em.createNamedQuery(
                    "Tblskillcategory.findAll",
                    Tblskillcategory.class)
                    .getResultList();

        } catch (Exception e) {

            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    @Override
    public void addSkillCategory(Tblskillcategory category) {

        try {

            category.setCreatedDate(new Date());

            if (category.getCategoryStatus() == null) {
                category.setCategoryStatus("APPROVED");
            }

            em.persist(category);

        } catch (Exception e) {

            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void updateSkillCategory(Tblskillcategory category) {

        try {

            Tblskillcategory dbCategory
                    = em.find(Tblskillcategory.class,
                            category.getCategoryId());

            if (dbCategory != null) {

                dbCategory.setCategoryName(
                        category.getCategoryName());

                dbCategory.setCategoryStatus(
                        category.getCategoryStatus());

                em.merge(dbCategory);
            }

        } catch (Exception e) {

            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void deleteSkillCategory(Integer categoryId) {

        try {

            Tblskillcategory category
                    = em.find(Tblskillcategory.class,
                            categoryId);

            if (category != null) {

                em.remove(category);
            }

        } catch (Exception e) {

            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Collection<Tblskills> getAllSkills() {

        try {

            return em.createNamedQuery(
                    "Tblskills.findAll",
                    Tblskills.class)
                    .getResultList();

        } catch (Exception e) {

            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    @Override
    public void addSkill(Tblskills skill) {

        try {

            Tblskillcategory category
                    = em.find(
                            Tblskillcategory.class,
                            skill.getCategoryId().getCategoryId());

            skill.setCategoryId(category);

            skill.setCreatedDate(new Date());

            if (skill.getSkillStatus() == null) {
                skill.setSkillStatus("APPROVED");
            }

            em.persist(skill);

        } catch (Exception e) {

            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void updateSkill(Tblskills skill) {

        try {

            Tblskillcategory category = em.find(
                    Tblskillcategory.class,
                    skill.getCategoryId().getCategoryId()
            );

            skill.setCategoryId(category);

            em.merge(skill);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Override
    public void deleteSkill(Integer skillId) {

        try {

            Tblskills skill
                    = em.find(Tblskills.class,
                            skillId);

            if (skill != null) {

                em.remove(skill);
            }

        } catch (Exception e) {

            e.printStackTrace();
            throw e;
        }
    }

    // ================= COMPANY =================
//    @Override
//    public void addCompany(Tblcompany company) {
//        try {
//            if (company == null) return;
//
//            company.setCreatedDate(new Date());
//            company.setCompanyStatus("Pending");
//
//            em.persist(company);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public Collection<Tblcompany> getAllCompanies() {
//        try {
//            return em.createNamedQuery("Tblcompany.findAll", Tblcompany.class)
//                    .getResultList();
//        } catch (Exception e) {
//            return new ArrayList<>();
//        }
//    }
    @Override
    public void addCompany(Tblcompany company) {

        try {

            company.setCreatedDate(new Date());
            em.persist(company);

        } catch (Exception e) {

            System.out.println("Error in addCompany()");
            e.printStackTrace();

            throw new RuntimeException("Unable to add company");
        }
    }

    @Override
    public void updateCompany(Tblcompany company) {

        try {

            em.merge(company);

        } catch (Exception e) {

            System.out.println("Error in updateCompany()");
            e.printStackTrace();

            throw new RuntimeException("Unable to update company");
        }
    }

    @Override
    public void deleteCompany(Integer companyId) {

        try {

            Tblcompany company
                    = em.find(Tblcompany.class, companyId);

            if (company != null) {

                em.remove(company);
            }

        } catch (Exception e) {

            System.out.println("Error in deleteCompany()");
            e.printStackTrace();

            throw new RuntimeException("Unable to delete company");
        }
    }

    @Override
    public Tblcompany findCompanyById(Integer companyId) {

        try {

            return em.find(
                    Tblcompany.class,
                    companyId);

        } catch (Exception e) {

            System.out.println("Error in findCompanyById()");
            e.printStackTrace();

            return null;
        }
    }

    @Override
    public Collection<Tblcompany> getAllCompanies() {

        try {

            return em.createNamedQuery(
                    "Tblcompany.findAll",
                    Tblcompany.class)
                    .getResultList();

        } catch (Exception e) {

            System.out.println("Error in getAllCompanies()");
            e.printStackTrace();

            return new ArrayList<>();
        }
    }

    @Override
    public void toggleCompanyStatus(Integer companyId, boolean status) {

        try {

            Tblcompany company
                    = em.find(Tblcompany.class, companyId);

            if (company != null) {

                company.setIsActive(status);

                em.merge(company);
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
    public Tbljob getJobByJobId(Integer jobId) {
        try {
            return em.createNamedQuery("Tbljob.findByJobId", Tbljob.class)
                    .setParameter("jobId", jobId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

//    @Override
//    public void updateJobStatus(int jobId, String jobStatus) {
//        try {
//            if (jobStatus == null || jobStatus.trim().isEmpty()) return;
//
//            Tbljob job = em.find(Tbljob.class, jobId);
//            if (job != null) {
//                job.setJobStatus(jobStatus);
//                em.merge(job);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void approveJob(int jobId) {
//        updateJobStatus(jobId, "Approved");
//    }
//
//    @Override
//    public void rejectJob(int jobId) {
//        updateJobStatus(jobId, "Rejected");
//    }
//
//    @Override
//    public Collection<Tbljob> searchJobsByTitle(String jobTitle) {
//        try {
//            if (jobTitle == null || jobTitle.trim().isEmpty()) {
//                return new ArrayList<>();
//            }
//
//            return em.createNamedQuery("Tbljob.findByJobTitle", Tbljob.class)
//                    .setParameter("jobTitle", jobTitle)
//                    .getResultList();
//            
    ////            return em.createQuery("SELECT j FROM Tbljob j WHERE j.jobTitle LIKE :title", Tbljob.class)
////                .setParameter("jobTitle", "%" + jobTitle + "%")
////                .getResultList();
//
//        } catch (Exception e) {
//            return new ArrayList<>();
//        }
//    }
    
    // ========================Profile=======================

    @Override
    public Tblusers getAdminProfile(Integer userId) {

        return em.find(Tblusers.class, userId);
    }

    @Override
    public void updateAdminProfile(Tblusers user) {

        Tblusers dbUser
                = em.find(
                        Tblusers.class,
                        user.getUserId());

        dbUser.setUserName(
                user.getUserName());

        dbUser.setUserEmail(user.getUserEmail());

        dbUser.setUserIsActive(
                user.getUserIsActive());

        em.merge(dbUser);

        // profile notification
        Tblnotification notification = new Tblnotification();

        notification.setNotificationTitle("Profile Updated");

        notification.setNotificationMessage(
                "Your admin profile information has been updated successfully.");

        notification.setNotificationType("PROFILE");

        notification.setIsRead(false);

        notification.setCreatedDate(new Date());

        notification.setReceiverUserId(dbUser);

        notification.setSenderUserId(dbUser);

        em.persist(notification);
    }

    @Override
    public String changeAdminPassword(Integer userId, String currentPassword, String newPassword) {

        try {

            Tblusers user = em.find(Tblusers.class, userId);

            if (user == null) {
                return "User not found";
            }

            // 🔥 RESET HASH PROPERLY (IMPORTANT FIX)
            Map<String, String> params = new HashMap<>();
            params.put("Pbkdf2PasswordHash.Iterations", "3072");
            params.put("Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA256");

            hash.initialize(params);

            System.out.println("DB Password: " + user.getUserPassword());

            // VERIFY CURRENT PASSWORD
            boolean valid = hash.verify(
                    currentPassword.toCharArray(),
                    user.getUserPassword()
            );

            if (!valid) {
                return "Current password is incorrect";
            }

            // 🔥 RE-INITIALIZE BEFORE GENERATE (VERY IMPORTANT FIX)
            hash.initialize(params);

            String encryptedPassword = hash.generate(newPassword.toCharArray());

            user.setUserPassword(encryptedPassword);

            // password change notification
            Tblnotification notification = new Tblnotification();

            notification.setNotificationTitle("Password Changed");
            notification.setNotificationMessage(
                    "Your account password was changed successfully.");
            notification.setNotificationType("PROFILE");
            notification.setIsRead(false);
            notification.setCreatedDate(new Date());
            notification.setReceiverUserId(user);
            notification.setSenderUserId(user);

            em.persist(notification);

            em.merge(user);

            return "Password updated successfully";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @Override
    public void uploadProfilePhoto(Integer userId, String photo) {

        Tblusers user
                = em.find(
                        Tblusers.class,
                        userId);

        user.setProfilePhoto(photo);

        em.merge(user);
    }

    // ================= ADMIN NOTIFICATIONS =================
    @Override
    public Collection<Tblnotification> getAdminNotifications(int adminId) {

        try {

            return em.createNamedQuery(
                    "Tblnotification.findByReceiver",
                    Tblnotification.class)
                    .setParameter("userId", adminId)
                    .getResultList();

        } catch (Exception e) {

            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Collection<Tblnotification> getAdminUnreadNotifications(int adminId) {

        try {

            return em.createNamedQuery(
                    "Tblnotification.findUnreadByReceiver",
                    Tblnotification.class)
                    .setParameter("userId", adminId)
                    .getResultList();

        } catch (Exception e) {

            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void markNotificationAsRead(int notificationId) {

        try {

            Tblnotification notification
                    = em.find(Tblnotification.class, notificationId);

            if (notification != null) {

                notification.setIsRead(true);
                notification.setReadDate(new Date());

                em.merge(notification);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // ================= APPLICATION =================
//    @Override
//    public Collection<Tblapplication> getAllApplications() {
//        try {
//            return em.createNamedQuery("Tblapplication.findAll", Tblapplication.class)
//                    .getResultList();
//        } catch (Exception e) {
//            return new ArrayList<>();
//        }
//    }
//
//    @Override
//    public void updateApplicationStatus(int applicationId, String applicationStatus) {
//        try {
//            if (applicationStatus == null || applicationStatus.trim().isEmpty()) return;
//
//            Tblapplication app = em.find(Tblapplication.class, applicationId);
//            if (app != null) {
//                app.setApplicationStatus(applicationStatus);
//                app.setLastUpdatedDate(new Date());
//                em.merge(app);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    // ================= NOTIFICATION =================
//    @Override
//    public void sendNotification(Tblnotification notification) {
//        try {
//            if (notification == null) return;
//
//            notification.setCreatedDate(new Date());
//            em.persist(notification);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    // ================= DASHBOARD =================
//    @Override
//    public int totalUsers() {
//        try {
//            return ((Long) em.createNamedQuery("Tblusers.count")
//                    .getSingleResult()).intValue();
//        } catch (Exception e) {
//            return 0;
//        }
//    }
//
//    @Override
//    public int totalJobs() {
//        try {
//            return ((Long) em.createNamedQuery("Tbljob.count")
//                    .getSingleResult()).intValue();
//        } catch (Exception e) {
//            return 0;
//        }
//    }
//
//    @Override
//    public int totalApplications() {
//        try {
//            return ((Long) em.createNamedQuery("Tblapplication.count")
//                    .getSingleResult()).intValue();
//        } catch (Exception e) {
//            return 0;
//        }
//    }
//
//    @Override
//    public int totalCandidates() {
//        try {
//            return ((Long) em.createNamedQuery("Tblcandidates.count")
//                    .getSingleResult()).intValue();
//        } catch (Exception e) {
//            return 0;
//        }
//    }
//
//    @Override
//    public int totalCompanies() {
//        try {
//            return ((Long) em.createNamedQuery("Tblcompany.count")
//                    .getSingleResult()).intValue();
//        } catch (Exception e) {
//            return 0;
//        }
//    }
    // ================= REPORTS =================
    // Total applications for a specific job
//    @Override
//    public Collection<Tblapplication> applicationsPerJob(int jobId) {
//        try {
    ////            return em.createNamedQuery("Tblapplication.countByJob", Long.class)
//            return em.createNamedQuery("Tblapplication.FindApplicationsByJob", Tblapplication.class)
//                    .setParameter("jobId", jobId)
//                    .getResultList();
//        } catch (Exception e) {
//            return new ArrayList<>();
//        }
//    }
//
//    // Total jobs posted by a company
//    @Override
//    public Collection<Tbljob> jobsPerCompany(int companyId) {
//        try {
////            return em.createNamedQuery("Tbljob.countByCompany", Long.class)
//            return em.createNamedQuery("Tbljob.findJobsByCompany", Tbljob.class)
//                    .setParameter("companyId", companyId)
//                    .getResultList();
//        } catch (Exception e) {
//            return new ArrayList<>();
//        }
//    }
//
//    // Total selected candidates
//    @Override
//    public Collection<Tblapplication> selectedApplications() {
//        try {
////            return em.createNamedQuery("Tblapplication.countSelected", Long.class
    /// @return )
//            return em.createNamedQuery("Tblapplication.findSelectedApplication", Tblapplication.class)
//                .getResultList();
//        } catch (Exception e) {
//            return new ArrayList<>();
//        }
//    }
//
//    // Job-wise application report
//    @Override
//    public Collection<Object[]> jobWiseApplicationReport() {
//        try {
//            return em.createNamedQuery("Tbljob.jobWiseApplications", Object[].class)
//                    .getResultList();
//        } catch (Exception e) {
//            return new ArrayList<>();
//        }
//    }  
    @Override
    public Collection<Tblskills> getPendingSkills() {
        return em.createQuery(
                "SELECT s FROM Tblskills s "
                + "WHERE s.skillStatus = 'PENDING' "
                + "ORDER BY s.createdDate DESC",
                Tblskills.class)
                .getResultList();
    }

    @Override

    public Collection<Tblskillcategory> getPendingCategories() {
        return em.createQuery(
                "SELECT c FROM Tblskillcategory c "
                + "WHERE c.categoryStatus = 'PENDING' "
                + "ORDER BY c.createdDate DESC",
                Tblskillcategory.class)
                .getResultList();
    }

    @Override

    public void approveSkill(Integer skillId, Integer adminUserId) {
        Tblskills skill = em.find(Tblskills.class, skillId);
        Tblusers admin = em.find(Tblusers.class, adminUserId);

        if (skill == null) {
            throw new RuntimeException("Skill not found");
        }

        skill.setSkillStatus("APPROVED");
        skill.setApprovedByUserId(adminUserId);
        skill.setApprovedDate(new Date());
        em.merge(skill);

        notifyCreator(skill.getCreatedByUserId(), admin,
                "Skill Approved",
                "Your skill has been approved: " + skill.getSkillName(),
                "SKILL_APPROVED");
    }

    @Override

    public void rejectSkill(Integer skillId, Integer adminUserId) {
        Tblskills skill = em.find(Tblskills.class, skillId);
        Tblusers admin = em.find(Tblusers.class, adminUserId);

        if (skill == null) {
            throw new RuntimeException("Skill not found");
        }

        skill.setSkillStatus("DISAPPROVED");
        skill.setApprovedByUserId(adminUserId);
        skill.setApprovedDate(new Date());
        em.merge(skill);

        notifyCreator(skill.getCreatedByUserId(), admin,
                "Skill Disapproved",
                "Your skill request was disapproved: " + skill.getSkillName(),
                "SKILL_DISAPPROVED");
    }

    @Override

    public void approveCategory(Integer categoryId, Integer adminUserId) {
        Tblskillcategory category = em.find(Tblskillcategory.class, categoryId);
        Tblusers admin = em.find(Tblusers.class, adminUserId);

        if (category == null) {
            throw new RuntimeException("Category not found");
        }

        category.setCategoryStatus("APPROVED");
        em.merge(category);

        notifyCreator(category.getCreatedByUserId(), admin,
                "Category Approved",
                "Your category has been approved: " + category.getCategoryName(),
                "CATEGORY_APPROVED");
    }

    @Override
    public void rejectCategory(Integer categoryId, Integer adminUserId) {

        Tblskillcategory category = em.find(Tblskillcategory.class, categoryId);
        Tblusers admin = em.find(Tblusers.class, adminUserId);

        if (category == null) {
            throw new RuntimeException("Category not found");
        }

        category.setCategoryStatus("DISAPPROVED");
        em.merge(category);

        Collection<Tblskills> categorySkills = em.createQuery(
                "SELECT s FROM Tblskills s "
                + "WHERE s.categoryId.categoryId = :categoryId "
                + "AND s.skillStatus = 'PENDING'",
                Tblskills.class)
                .setParameter("categoryId", categoryId)
                .getResultList();

        for (Tblskills skill : categorySkills) {
            skill.setSkillStatus("DISAPPROVED");
            skill.setApprovedByUserId(adminUserId);
            skill.setApprovedDate(new Date());
            em.merge(skill);

            notifyCreator(skill.getCreatedByUserId(), admin,
                    "Skill Disapproved",
                    "Your skill request was disapproved because its category was disapproved: "
                    + skill.getSkillName(),
                    "SKILL_DISAPPROVED");
        }

        notifyCreator(category.getCreatedByUserId(), admin,
                "Category Disapproved",
                "Your category request was disapproved: " + category.getCategoryName(),
                "CATEGORY_DISAPPROVED");
    }

    @Override

    public Collection<Tblnotification> getAdminNotifications(Integer adminUserId) {
        return em.createQuery(
                "SELECT n FROM Tblnotification n "
                + "WHERE n.receiverUserId.userId = :uid "
                + "ORDER BY n.createdDate DESC",
                Tblnotification.class)
                .setParameter("uid", adminUserId)
                .getResultList();
    }

    @Override
    public Collection<Tblskills> getApprovedSkills() {
        return em.createQuery(
                "SELECT s FROM Tblskills s "
                + "WHERE s.skillStatus = 'APPROVED' "
                + "ORDER BY s.skillName",
                Tblskills.class)
                .getResultList();
    }

    @Override
    public Collection<Tblskillcategory> getApprovedCategories() {
        return em.createQuery(
                "SELECT c FROM Tblskillcategory c "
                + "WHERE c.categoryStatus = 'APPROVED' "
                + "ORDER BY c.categoryName",
                Tblskillcategory.class)
                .getResultList();
    }

    private void notifyCreator(Integer creatorUserId, Tblusers admin, String title, String message, String type) {
        if (creatorUserId == null) {
            return;
        }

        Tblusers creator = em.find(Tblusers.class, creatorUserId);
        if (creator == null) {
            return;
        }

        Tblnotification notification = new Tblnotification();
        notification.setSenderUserId(admin);
        notification.setReceiverUserId(creator);
        notification.setNotificationTitle(title);
        notification.setNotificationMessage(message);
        notification.setNotificationType(type);
        notification.setIsRead(false);
        notification.setCreatedDate(new Date());
        em.persist(notification);
    }

    // Admin dashboard
    @Override
    public Collection<Tblapplication> getAllApplications() {
        try {
            return em.createNamedQuery("Tblapplication.findAll", Tblapplication.class)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Collection<Tblinterview> getAllInterviews() {
        try {
            return em.createNamedQuery("Tblinterview.findAll", Tblinterview.class)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}
