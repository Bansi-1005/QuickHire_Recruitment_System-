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
import jakarta.ws.rs.core.Response;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tejan
 */
@Named(value = "registrationJSFManagedBean")
@RequestScoped
public class RegistrationCDIBean {

    @EJB
    RegistrationBeanLocal ejb;

    Tblusers user = new Tblusers();
    Tblcandidates candidate = new Tblcandidates();
    Tblrecruiters recruiter = new Tblrecruiters();
    private Collection<Tblcompany> companies;
    private Integer selectedCompanyId;
    Integer roleId;

//    @PostConstruct
//    public void init() {
//        recruiter.setCompanyId(new Tblcompany());
//
//        // Default Active
//        user.setUserIsActive(true);
//    }
    @PostConstruct
    public void init() {
        try {
            user.setUserIsActive(true);
            companies = ejb.getAllCompanies();

            System.out.println("Companies loaded = "
                    + (companies == null ? "NULL" : companies.size()));

        } catch (Exception e) {
            e.printStackTrace();
            companies = new java.util.ArrayList<>();
        }
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

    public Collection<Tblcompany> getCompanies() {
        return companies;
    }

    public void setCompanies(Collection<Tblcompany> companies) {
        this.companies = companies;
    }

    public Integer getSelectedCompanyId() {
        return selectedCompanyId;
    }

    public void setSelectedCompanyId(Integer selectedCompanyId) {
        this.selectedCompanyId = selectedCompanyId;
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
            body.put("userIsActive", user.getUserIsActive());

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
                candMap.put("candidateArea", candidate.getCandidateArea());
                candMap.put("candidateCity", candidate.getCandidateCity());
                candMap.put("candidateState", candidate.getCandidateState());
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
            } else if (roleId == 3) { // Recruiter

                Map<String, Object> recMap = new HashMap<>();
                recMap.put("designation", recruiter.getDesignation());
                recMap.put("recruiterPhone", recruiter.getRecruiterPhone());
                recMap.put("recruiterStatus", "Active");

//                if (recruiter.getCompanyId() != null
//                        && recruiter.getCompanyId().getCompanyId() != null) {
//
//                    Map<String, Object> compMap = new HashMap<>();
//                    compMap.put("companyId", recruiter.getCompanyId().getCompanyId());
//                    recMap.put("companyId", compMap);
//                }
                if (selectedCompanyId != null && selectedCompanyId > 0) {
                    Map<String, Object> compMap = new HashMap<>();
                    compMap.put("companyId", selectedCompanyId);
                    recMap.put("companyId", compMap);
                }
                body.put("recruiter", recMap);
            }

            //  CALL API
            Response response = client.registerUser(body);
            String msg = response.readEntity(String.class);

            System.out.println("API STATUS: " + response.getStatus());
            System.out.println("API RESPONSE: " + msg);
            System.out.println("BODY JSON: " + body);

            client.close();

            if (response.getStatus() == 200) {
                return "/Login.xhtml?faces-redirect=true";
            }

            jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(
                    null,
                    new jakarta.faces.application.FacesMessage(
                            jakarta.faces.application.FacesMessage.SEVERITY_ERROR,
                            "Registration Failed",
                            msg
                    )
            );

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
