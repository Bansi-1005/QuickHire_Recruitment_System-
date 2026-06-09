/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package EJB;

import Entity.*;
import jakarta.ejb.Local;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author RINKAL
 */
@Local
public interface RecruiterBeanLocal {

    // ================= PROFILE =================
    Tblrecruiters getProfile(int userId);

    void updateProfile(Tblrecruiters recruiter);

    // ================= COMPANY =================
//    Tblcompany getCompanyDetails(int recruiterId);
    // ================= JOB MANAGEMENT =================
    void createJob(Tbljob job, Collection<Integer> skillIds, Collection<Integer> educationIds);

    Collection<Tbljob> getJobs(int recruiterId);

    void updateJob(Tbljob job, Collection<Integer> skillIds, Collection<Integer> educationIds);

    void toggleJobStatus(int jobId, int recruiterId);

    void updateJobExpiryDate(int jobId, int recruiterId, Date expiryDate);

    public void closeExpiredJobs();

    // ================= JOB SKILLS =========================================================
    Collection<Tblskills> getJobSkills(int jobId);

    public Collection<Tblskills> getAllSkills(Integer userId);

    public Collection<Tblskillcategory> getSkillCategories(Integer recruiterUserId);

    public Collection<Tblskills> getSkillsByCategory(Integer categoryId, Integer recruiterUserId);

    public void addSkillAndOrCategory(String categoryName, Collection<String> skillNames, Integer existingCategoryId, Integer recruiterUserId);

    // ================= JOB Education =======================================================
    Collection<Tbleducation> getAllEducation();

    Collection<Tbleducation> getJobEducation(int jobId);

    // ================= CANDIDATE MANAGEMENT =================
    public Collection<Tblapplication> getRecruiterApplications(int recruiterId);

    public double calculateAndSaveScreeningScore(int applicationId);

    public String getScreeningLevel(int applicationId);

    public Map<Integer, BigDecimal> getAllScreeningScores(int recruiterId);

    void shortlistApplication(int applicationId);

//    void selectApplication(int applicationId);
    void scheduleInterview(Tblinterview interview);

    // ================= INTERVIEW =================
    Collection<Tblinterview> getRecruiterInterviews(Integer recruiterId);

    Long getScheduledInterviewCount(Integer recruiterId);

    Long getCompletedInterviewCount(Integer recruiterId);

    Long getSelectedCount(Integer recruiterId);

    Long getRejectedCount(Integer recruiterId);

    Long getTotalInterviewCount(Integer recruiterId);

    void conductInterview(Integer interviewId,
            String feedback,
            String result);

    void rescheduleInterview(Integer interviewId,
            Date interviewDate,
            String interviewerName,
            String interviewerMode);

//    // ================= APPLICATION =================
//    Collection<Tblapplication> getApplications(int jobId);
//    void updateApplicationStatus(int applicationId, String newStatus);
//
//    // ================= APPLICATION HISTORY =================
//    void addApplicationStatusHistory(int applicationId, String oldStatus, String newStatus);
//
//    // ================= SCREENING =================
//    void generateScreeningScore(int applicationId);
//    Tblscreeningscore getScore(int applicationId);
//
//    // ================= SHORTLIST =================
//    Collection<Tblapplication> getTopCandidates(int jobId);
//    Collection<Tblapplication> filterCandidatesByScore(int jobId, double minScore);
//
//    // ================= INTERVIEW =================
//    void scheduleInterview(Tblinterview interview);
//    void updateInterview(Tblinterview interview);
//    void updateInterviewFeedback(int interviewId, String feedback, String result);
//
//    // ================= NOTIFICATION =================
//    void sendNotification(Tblnotification notification);
//    Collection<Tblnotification> getRecruiterNotifications(int userId);
//    
    // ================= DASHBOARD =================
    public long getTodayInterviewsCount(int recruiterId);

    public long getNewApplicantsCount(int recruiterId);

    public long getShortlistedCount(int recruiterId);

    public double getHiringRate(int recruiterId);

    public long getActiveJobsCount(int recruiterId);

    public long getTotalApplicantsCount(int recruiterId);

    public long getUpcomingInterviewsCount(int recruiterId);

    public double getAvgTimeToHire(int recruiterId);

    Collection<Tblscreeningscore> getDashboardTopCandidates(int recruiterId);

    Collection<Tblinterview> getDashboardUpcomingInterviews(int recruiterId);

    Collection<Tblnotification> getRecentActivities(int userId);

}
