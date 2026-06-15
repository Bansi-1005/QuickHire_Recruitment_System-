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


    // ================= JOB MANAGEMENT =================

    void createJob(Tbljob job, Collection<Integer> skillIds, Collection<Integer> educationIds);

    Collection<Tbljob> getJobs(int recruiterId);

    void updateJob(Tbljob job, Collection<Integer> skillIds, Collection<Integer> educationIds);

    void toggleJobStatus(int jobId, int recruiterId);

    void updateJobExpiryDate(int jobId, int recruiterId, Date expiryDate);

    public void closeExpiredJobs();

    Collection<Tblskills> getJobSkills(int jobId);

    public Collection<Tblskills> getAllSkills(Integer userId);

    public Collection<Tblskillcategory> getSkillCategories(Integer recruiterUserId);

    public Collection<Tblskills> getSkillsByCategory(Integer categoryId, Integer recruiterUserId);

    public void addSkillAndOrCategory(String categoryName, Collection<String> skillNames, Integer existingCategoryId, Integer recruiterUserId);

    long getJobApplicationCount(Integer jobId, String status);

    // ================= JOB Education =======================================================
    Collection<Tbleducation> getAllEducation();

    Collection<Tbleducation> getJobEducation(int jobId);

    // ================= CANDIDATE MANAGEMENT =================
    public String getCandidateSkillsTextByApplication(int applicationId);

    public String getCandidateEducationTextByApplication(int applicationId);

    public Collection<Tblapplication> getRecruiterApplications(int recruiterId);

    public double calculateAndSaveScreeningScore(int applicationId);

    public String getScreeningLevel(int applicationId);

    public Map<Integer, BigDecimal> getAllScreeningScores(int recruiterId);

    void shortlistApplication(int applicationId);

    public void rejectApplication(int applicationId);

    public long getRejectedApplicationCount(int recruiterId);

    // ================= INTERVIEW =================
    void scheduleInterview(Tblinterview interview);

    Collection<Tblinterview> getRecruiterInterviews(Integer recruiterId);

    Collection<Tblinterview> getInterviewHistoryByApplication(Integer applicationId);

    Long getScheduledInterviewCount(Integer recruiterId);

    Long getCompletedInterviewCount(Integer recruiterId);

    Long getSelectedCount(Integer recruiterId);

    Long getRejectedCount(Integer recruiterId);

    Long getTotalInterviewCount(Integer recruiterId);

    void conductInterview(Integer interviewId, String feedback, String result);

    void rescheduleInterview(Integer interviewId, Date interviewDate, String interviewerName, String interviewerMode);

    public void cancelInterview(Integer interviewId);

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

    // ================= NOTIFICATION =================
    Collection<Tblnotification> getNotifications(int userId);

    void markNotificationAsRead(int notificationId, int userId);

    void markAllNotificationsAsRead(int userId);

}
