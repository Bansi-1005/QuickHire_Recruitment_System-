/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import jakarta.ejb.Stateless;
import Entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author tejan
 */

@Stateless
public class CandidateBean implements CandidateBeanLocal {

    @PersistenceContext(unitName="jpu")
    EntityManager em;

    // ================= AUTH =================
//    @Override
//    public Tblusers candidateLogin(String email, String password, int roleId) {
//        try {
//            return em.createNamedQuery("Tblusers.loginByRole", Tblusers.class)
//                    .setParameter("email", email)
//                    .setParameter("password", password)
//                    .setParameter("roleId", roleId)
//                    .getSingleResult();
//        } catch (Exception e) {
//            return null;
//        }
//    }

    @Override
    public void registerCandidate(Tblusers user, Tblcandidates candidate) {
        em.persist(user);
        candidate.setUserId(user);
        candidate.setResumeUploadDate(new Date());
        em.persist(candidate);
    }

    // ================= PROFILE =================
    @Override
    public Tblcandidates getCandidateProfile(int userId) {
        try {
            return em.createNamedQuery("Tblcandidates.findByUser", Tblcandidates.class)
                    .setParameter("userId", userId)
                    .getResultList()
                    .stream()
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void updateCandidateProfile(Tblcandidates candidate) {
        em.merge(candidate);
    }

    // ================= RESUME =================
    @Override
    public void uploadResume(int candidateId, String candidateResume) {
        Tblcandidates c = em.find(Tblcandidates.class, candidateId);
        if (c != null) {
            c.setCandidateResume(candidateResume);
            c.setResumeUploadDate(new Date());
            em.merge(c);
        }
    }

    @Override
    public String getCandidateResume(int candidateId) {
        Tblcandidates c = em.find(Tblcandidates.class, candidateId);
        return (c != null) ? c.getCandidateResume() : null;
    }

    // ================= SKILLS =================
    @Override
    public void addSkillToCandidate(int candidateId, int skillId) {
        Tblcandidates c = em.find(Tblcandidates.class, candidateId);
        Tblskills s = em.find(Tblskills.class, skillId);

        if (c != null && s != null) {
            c.getTblskillsCollection().add(s);
            em.merge(c);
        }
    }

    @Override
    public void updateSkillToCandidate(int candidateId, int skillId) {
        addSkillToCandidate(candidateId, skillId); // simple reuse
    }

    @Override
    public void removeSkillFromCandidate(int candidateId, int skillId) {
        Tblcandidates c = em.find(Tblcandidates.class, candidateId);
        Tblskills s = em.find(Tblskills.class, skillId);

        if (c != null && s != null) {
            c.getTblskillsCollection().remove(s);
            em.merge(c);
        }
    }

    @Override
    public Collection<Tblskills> getCandidateSkills(int candidateId) {
        Tblcandidates c = em.find(Tblcandidates.class, candidateId);
        return (c != null) ? c.getTblskillsCollection() : null;
    }

    // ================= JOBS =================
    @Override
    public Collection<Tbljob> getAllJobs() {
        return em.createNamedQuery("Tbljob.findAll", Tbljob.class)
                .getResultList();
    }

    @Override
    public Collection<Tbljob> searchJobsByLocation(String jobLocation) {
        return em.createQuery(
                "Tbljob.findByLocation", Tbljob.class)
                .setParameter("jobLocation", "%" + jobLocation + "%")
                .getResultList();
    }

    @Override
    public Collection<Tbljob> searchJobsBySkill(String skillName) {
        return em.createQuery(
                "Tbljob.findBySkill",
                Tbljob.class)
                .setParameter("skillName", "%" + skillName + "%")
                .getResultList();
    }

    // ================= APPLICATION =================
    @Override
    public void applyForJob(Tblapplication application) {
        application.setApplicationAppliedDate(new Date());
        application.setLastUpdatedDate(new Date());
        application.setApplicationStatus("Applied");
        em.persist(application);
    }

    @Override
    public Collection<Tblapplication> getCandidateApplications(int candidateId) {
        return em.createNamedQuery("Tblapplication.findByCandidate", Tblapplication.class)
                .setParameter("candidateId", candidateId)
                .getResultList();
    }

    // ================= APPLICATION STATUS =================
    @Override
    public Tblapplication getApplicationDetails(int applicationId) {
        return em.find(Tblapplication.class, applicationId);
    }

    @Override
    public String getApplicationStatus(int applicationId) {
        Tblapplication app = em.find(Tblapplication.class, applicationId);
        return (app != null) ? app.getApplicationStatus() : null;
    }

    // ================= SCREENING =================
    @Override
    public Tblscreeningscore getScreeningScore(int applicationId) {
        try {
            return em.createQuery(
                    "Tblscreeningscore.findByApplication",
                    Tblscreeningscore.class)
                    .setParameter("applicationId", applicationId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    // ================= INTERVIEW =================
    @Override
    public Collection<Tblinterview> getCandidateInterviews(int applicationId) {
        return em.createNamedQuery("Tblinterview.findByApplication", Tblinterview.class)
                .setParameter("applicationId", applicationId)
                .getResultList();
    }

    // ================= NOTIFICATION =================
    @Override
    public Collection<Tblnotification> getCandidateNotifications(int userId) {
        return em.createQuery(
                "Tblnotification.findByUser",
                Tblnotification.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
