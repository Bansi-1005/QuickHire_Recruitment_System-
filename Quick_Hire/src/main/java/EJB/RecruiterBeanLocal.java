/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package EJB;

import Entity.*;
import jakarta.ejb.Local;
import java.util.Collection;

/**
 *
 * @author RINKAL
 */
@Local
public interface RecruiterBeanLocal {

    // ================= AUTH =================
    void registerRecruiter(Tblusers user, Tblrecruiters recruiter);

    // ================= PROFILE =================
    Tblrecruiters getProfile(int userId);
    void updateProfile(Tblrecruiters recruiter);

    // ================= COMPANY =================
    Tblcompany getCompanyDetails(int recruiterId);

    // ================= JOB MANAGEMENT =================
    void createJob(Tbljob job);
    void updateJob(Tbljob job);
    void deleteJob(int jobId,int recruiterId);
     //single,multiple,all job delete logic:
    //public void deleteJob(Integer jobId, Collection<Integer> jobIds, int recruiterId);
    void updateJobStatus(int jobId, String status);
    Collection<Tbljob> getJobs(int recruiterId);

    // ================= JOB SKILLS =================
    void addSkillToJob(int jobId, int skillId);
    void removeSkillFromJob(int jobId, int skillId);
    Collection<Tblskills> getJobSkills(int jobId);

    // ================= APPLICATION =================
    Collection<Tblapplication> getApplications(int jobId);
    void updateApplicationStatus(int applicationId, String newStatus);

    // ================= APPLICATION HISTORY =================
    void addApplicationStatusHistory(int applicationId, String oldStatus, String newStatus);

    // ================= SCREENING =================
    void generateScreeningScore(int applicationId);
    Tblscreeningscore getScore(int applicationId);

    // ================= SHORTLIST =================
    Collection<Tblapplication> getTopCandidates(int jobId);
    Collection<Tblapplication> filterCandidatesByScore(int jobId, double minScore);

    // ================= INTERVIEW =================
    void scheduleInterview(Tblinterview interview);
    void updateInterview(Tblinterview interview);
    void updateInterviewFeedback(int interviewId, String feedback, String result);

    // ================= NOTIFICATION =================
    void sendNotification(Tblnotification notification);
    Collection<Tblnotification> getRecruiterNotifications(int userId);
}