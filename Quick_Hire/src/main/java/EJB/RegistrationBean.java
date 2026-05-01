/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.*;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import util.EmailServiceLocal;

/**
 *
 * @author tejan
 */
@Stateless
public class RegistrationBean implements RegistrationBeanLocal {

    @PersistenceContext(unitName = "jpu")
    EntityManager em;

    @EJB
    private EmailServiceLocal emailService;

    @Inject
    Pbkdf2PasswordHash hash;

    @Override
    public void registerUser(Tblusers user, Tblrolemaster role, Tblcandidates candidate, Tblrecruiters recruiter) {
        try {
            if (user == null || role == null) return;

            Date now = new Date();

            //  STEP 1: Initialize hash
            Map<String, String> params = new HashMap<>();
            params.put("Pbkdf2PasswordHash.Iterations", "3072");
            params.put("Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA256");

            hash.initialize(params);

            //  STEP 2: Hash password
            String hashedPassword = hash.generate(user.getUserPassword().toCharArray());

            //  STEP 3: Set user data
            user.setUserPassword(hashedPassword);
            user.setCreatedDate(now);
            user.setUpdatedDate(now);
            user.setLastLoginDate(now);

            // SET ROLE
            Tblrolemaster managedRole = em.find(Tblrolemaster.class, role.getRoleId());
            user.setRoleId(managedRole);            

            em.persist(user);
            em.flush();

            // ============================
            // ROLE BASED LOGIC
            // ============================

            // Candidate (Assume roleId = 2)

            if (managedRole.getRoleId() == 2 && candidate != null) {

                candidate.setUserId(user);
                if (candidate.getCandidateResume() != null && 
                    !candidate.getCandidateResume().trim().equals("") &&
                    !candidate.getCandidateResume().equalsIgnoreCase("null")) {

                    candidate.setResumeUploadDate(now);

                } else {
                    candidate.setCandidateResume(null);
                    candidate.setResumeUploadDate(null);
}

                em.persist(candidate);

                //  Email
                if (user.getUserEmail() != null) {
                    String subject = "Welcome to QuickHire";
                    String message = "Hello " + user.getUserName() + ",\n\n"
                            + "Your account has been successfully created.\n"
                            + "You can now apply for jobs.\n\n"
                            + "Thank you!";

                    emailService.sendEmail(user.getUserEmail(), subject, message);
                }
            }

            // Recruiter (Assume roleId = 3)
            else if (managedRole.getRoleId() == 3 && recruiter != null) {

                recruiter.setUserId(user);
                recruiter.setCreatedDate(now);
                
                if (recruiter.getCompanyId() != null) {
                    Tblcompany managedCompany = em.find(Tblcompany.class,recruiter.getCompanyId().getCompanyId());
                    recruiter.setCompanyId(managedCompany);
                }

              
                em.persist(recruiter);

                // Email
                if (user.getUserEmail() != null) {
                    String subject = "Welcome to QuickHire";
                    String message = "Hello " + user.getUserName() + ",\n\n"
                            + "Your recruiter account has been created successfully.\n"
                            + "You can now post jobs and manage candidates.\n\n"
                            + "Regards,\nQuickHire Team";

                    emailService.sendEmail(user.getUserEmail(), subject, message);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
     }
}
