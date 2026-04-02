/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quick_hire.resources;

import EJB.RegistrationBeanLocal;
import Entity.*;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

/**
 *
 * @author tejan
 */
@Path("registration")
public class RegistrationResource {
    @EJB
    RegistrationBeanLocal ejb;
    
//    @POST
//    @Path("registerUser")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response registerUser(Tblusers user) {
//        try {
//
//            if (user == null || user.getRoleId() == null) {
//                return Response.status(Response.Status.BAD_REQUEST)
//                        .entity("User or Role is missing").build();
//            }
//
//            Tblrolemaster role = user.getRoleId();
//
//            Tblcandidates candidate = null;
//            Tblrecruiters recruiter = null;
//
//            // If Candidate
//            if ("Candidate".equalsIgnoreCase(role.getRoleName())) {
//
//                if (user.getTblcandidatesCollection() != null &&
//                    !user.getTblcandidatesCollection().isEmpty()) {
//
//                    candidate = user.getTblcandidatesCollection().iterator().next();
//                }
//            }
//
//            // If Recruiter
//            else if ("Recruiter".equalsIgnoreCase(role.getRoleName())) {
//
//                if (user.getTblrecruitersCollection() != null &&
//                    !user.getTblrecruitersCollection().isEmpty()) {
//
//                    recruiter = user.getTblrecruitersCollection().iterator().next();
//                }
//            }
//
//            // Call your EJB
//            ejb.registerUser(user, role, candidate, recruiter);
//
//            return Response.ok("User Registered Successfully").build();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
    
    @POST
    @Path("registerUser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(Map<String, Object> body) {
        try {

            // ================= USER =================
            Tblusers user = new Tblusers();
            user.setUserName((String) body.get("userName"));
            user.setUserEmail((String) body.get("userEmail"));
            user.setUserPassword((String) body.get("userPassword"));
            user.setUserStatus((String) body.get("userStatus"));

            // ================= ROLE =================
            Map<String, Object> roleMap = (Map<String, Object>) body.get("roleId");
            
            if (roleMap == null || roleMap.get("roleId") == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                               .entity("Role is missing").build();
            }
            
            Tblrolemaster role = new Tblrolemaster();
            role.setRoleId(((Number) roleMap.get("roleId")).intValue());

            user.setRoleId(role);
            System.out.println("Received roleMap: " + roleMap);

            // ================= CANDIDATE =================
            Tblcandidates candidate = null;
            Tblrecruiters recruiter = null;

            if (role.getRoleId() == 2) {

                Map<String, Object> candMap = (Map<String, Object>) body.get("candidate");

                if (candMap != null) {
                    candidate = new Tblcandidates();

                    candidate.setCandidatePhone((String) candMap.get("candidatePhone"));
                    candidate.setCandidateLocation((String) candMap.get("candidateLocation"));
                    candidate.setCandidateGender((String) candMap.get("candidateGender"));
                    candidate.setCandidateExperience(((Number) candMap.get("candidateExperience")).intValue());
                    candidate.setCandidateResume((String) candMap.get("candidateResume"));

                    // Date conversion
                    candidate.setCandidateDOB(java.sql.Date.valueOf((String) candMap.get("candidateDOB")));
                }
            }
            else if(role.getRoleId() == 3){
                
                Map<String, Object> recMap = (Map<String, Object>) body.get("recruiter");

                recruiter = new Tblrecruiters();

                recruiter.setDesignation((String) recMap.get("designation"));
                recruiter.setRecruiterPhone((String) recMap.get("recruiterPhone"));
                recruiter.setRecruiterStatus((String) recMap.get("recruiterStatus"));

                // COMPANY FIX
                if (recMap.get("companyId") != null) {
                    Map<String, Object> compMap = (Map<String, Object>) recMap.get("companyId");

                    Tblcompany company = new Tblcompany();
                    company.setCompanyId(((Number) compMap.get("companyId")).intValue());

                    recruiter.setCompanyId(company);
                }
            }

            // ================= CALL EJB =================
            ejb.registerUser(user, role, candidate, recruiter);

            return Response.ok("User Registered Successfully").build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
}
 