/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package CDIBean;

import Client.RegistrationJerseyClient;
import EJB.RegistrationBeanLocal;
import Entity.*;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author tejan
 */
@Named(value = "registrationJSFManagedBean")
@RequestScoped
public class RegistrationJSFManagedBean {

   @EJB
    RegistrationBeanLocal ejb;

    Tblusers user = new Tblusers();
    Tblcandidates candidate = new Tblcandidates();
    Tblrecruiters recruiter = new Tblrecruiters();

    Integer roleId;
    
    @PostConstruct
    public void init() {
        recruiter.setCompanyId(new Tblcompany());
    }
    public RegistrationBeanLocal getEjb() {
        return ejb;
    }

    public void setEjb(RegistrationBeanLocal ejb) {
        this.ejb = ejb;
    }

    public Tblusers getUser() {
        return user;
    }

    public void setUser(Tblusers user) {
        this.user = user;
    }

    public Tblcandidates getCandidate() {
        return candidate;
    }

    public void setCandidate(Tblcandidates candidate) {
        this.candidate = candidate;
    }

    public Tblrecruiters getRecruiter() {
        return recruiter;
    }

    public void setRecruiter(Tblrecruiters recruiter) {
        this.recruiter = recruiter;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
    
    // ================= REGISTER =================
public String registerUser() {
    try {

        RegistrationJerseyClient client = new RegistrationJerseyClient();

        // Create request body (same as your REST API expects)
        Map<String, Object> body = new HashMap<>();

        body.put("userName", user.getUserName());
        body.put("userEmail", user.getUserEmail());
        body.put("userPassword", user.getUserPassword());
        body.put("userStatus", "Active");

        // Role
        if (roleId == null) {
            System.out.println("Please select a role!");
            return null; // Or show a message in JSF
        }
        
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("roleId", roleId);
        body.put("roleId", roleMap);

        // ================= ROLE BASED =================
            if (roleId == 2) { // Candidate

                Map<String, Object> candMap = new HashMap<>();

                candMap.put("candidatePhone", candidate.getCandidatePhone());
                candMap.put("candidateLocation", candidate.getCandidateLocation());
                candMap.put("candidateGender", candidate.getCandidateGender());
                candMap.put("candidateExperience", candidate.getCandidateExperience());
                candMap.put("candidateResume", "");

                //  DATE FIX (WITHOUT UTIL)
                if (candidate.getCandidateDOB() != null) {
                    String dob = new java.text.SimpleDateFormat("yyyy-MM-dd")
                            .format(candidate.getCandidateDOB());
                    candMap.put("candidateDOB", dob);
                }

                body.put("candidate", candMap);
            }

            else if (roleId == 3) { // Recruiter

                Map<String, Object> recMap = new HashMap<>();
                recMap.put("designation", recruiter.getDesignation());
                recMap.put("recruiterPhone", recruiter.getRecruiterPhone());
                recMap.put("recruiterStatus", "Active");
       
                if (recruiter.getCompanyId() != null &&
                    recruiter.getCompanyId().getCompanyId() != null) {

                    Map<String, Object> compMap = new HashMap<>();
                    compMap.put("companyId", recruiter.getCompanyId().getCompanyId());
                    recMap.put("companyId", compMap);
                }
                body.put("recruiter", recMap);
            }

            //  CALL API
            String response = client.registerUser(body);
            System.out.println("API RESPONSE: " + response);
            System.out.println("BODY JSON: " + body);

            client.close();

            return "Login?faces-redirect=true";

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
