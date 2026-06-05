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
 * @author tejan
 */
@Local
public interface CandidateBeanLocal {

    // AUTH
   // Tblusers candidateLogin(String email,String password,int roleId);
//    void registerCandidate(Tblusers user, Tblcandidates candidate);

    // PROFILE
    Tblcandidates getCandidateProfile(int userId);
    void updateCandidateProfile(Tblcandidates candidate);
    public void uploadProfilePhoto(int userId, String profilePhoto);
    
    // RESUME
    Collection<Tblresume> getCandidateResumes(int candidateId);
    void uploadResume(int candidateId, String resumeFile);
    void deleteResume(int resumeId);
    void toggleResumeStatus(int resumeId, boolean status);
//    void uploadResume(int candidateId, String candidateResume);
//    String getCandidateResume(int candidateId);

    // ---------- Candidate Skills ----------
    Collection<Tblskills> getAllSkills();
    Collection<Tblskillcategory> getAllSkillCategories();
    Collection<Tblskills> getSkillsByCategory(int categoryId);
    
    void addSkillToCandidate(int candidateId, int skillId);
//    void updateSkillToCandidate(int candidateId, Collection<Integer> skillIds);
    void removeSkillFromCandidate(int candidateId, int skillId);
    Collection<Tblskills> getCandidateSkills(int candidateId);

    
    // ---------- Candidate Aducation ----------
    public Collection<Tblcandidateeducation> getCandidateEducation(Integer candidateId);
    public Tblcandidateeducation addCandidateEducation(Tblcandidateeducation edu, Integer candidateId);
    public Tblcandidateeducation updateCandidateEducation(Tblcandidateeducation edu);
    public void removeCandidateEducation(Integer candidateEducationId);
            
            
    // JOBS
    Collection<Tbljob> getAllJobs();
    public Tbljob getJobByJobId(Integer jobId);
//    Collection<Tbljob> searchJobsByLocation(String location);
//    Collection<Tbljob> searchJobsBySkill(String skill);

    // Job APPLICATION
    public String applyForJob(int candidateId, int jobId, int resumeId);
    Collection<Tblapplication> getCandidateApplications(int candidateId);
    public boolean alreadyApplied(int candidateId, int jobId);
    public void deleteApplication(int applicationId);
            
    //  Application Status 
//    Tblapplication getApplicationDetails(int applicationId);
    String getApplicationStatus(int applicationId);
    public void updateApplicationStatus(int applicationId, String status);

    // SCREENING

    // INTERVIEW
    Collection<Tblinterview> getCandidateInterviews(int applicationId);

    // NOTIFICATION
    Collection<Tblnotification> getCandidateNotifications(int userId); 
    public Collection<Tblnotification> getUnreadNotifications(int userId);
    public Collection<Tblnotification> getApplicationNotifications(int userId);
    public Collection<Tblnotification> getInterviewNotifications(int userId);
    public Collection<Tblnotification> getProfileNotifications(int userId);
    public void markNotificationAsRead(int notificationId);

}
