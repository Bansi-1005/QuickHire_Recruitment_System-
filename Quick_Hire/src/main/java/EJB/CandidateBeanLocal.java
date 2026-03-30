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
    void registerCandidate(Tblusers user, Tblcandidates candidate);

    // PROFILE
    Tblcandidates getCandidateProfile(int userId);
    void updateCandidateProfile(Tblcandidates candidate);

    // RESUME
    void uploadResume(int candidateId, String candidateResume);
    String getCandidateResume(int candidateId);

    // ---------- Candidate Skills ----------
    void addSkillToCandidate(int candidateId, int skillId);
    void updateSkillToCandidate(int candidateId, Collection<Integer> skillIds);
    void removeSkillFromCandidate(int candidateId, int skillId);
    Collection<Tblskills> getCandidateSkills(int candidateId);

    // JOBS
    Collection<Tbljob> getAllJobs();
    Collection<Tbljob> searchJobsByLocation(String location);
    Collection<Tbljob> searchJobsBySkill(String skill);

    // Job APPLICATION
    String applyForJob(Tblapplication application);
    Collection<Tblapplication> getCandidateApplications(int candidateId);
    public boolean alreadyApplied(int candidateId, int jobId);
    public void deleteApplication(int applicationId);
            
    //  Application Status 
    Tblapplication getApplicationDetails(int applicationId);
    String getApplicationStatus(int applicationId);
    public void updateApplicationStatus(int applicationId, String status);

    // SCREENING
    Tblscreeningscore getScreeningScore(int applicationId);

    // INTERVIEW
    Collection<Tblinterview> getCandidateInterviews(int applicationId);

    // NOTIFICATION
    Collection<Tblnotification> getCandidateNotifications(int userId);
    
    
  
}
