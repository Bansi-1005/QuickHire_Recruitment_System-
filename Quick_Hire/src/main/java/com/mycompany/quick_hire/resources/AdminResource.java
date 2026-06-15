/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quick_hire.resources;

import EJB.AdminBeanLocal;
import Entity.*;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author tejan
 */

@Path("admin")
public class AdminResource {
    @EJB
    AdminBeanLocal ejb;
    
     // ================= ROLE =================
//    @POST
//    @Path("addRole")
//    @Consumes(MediaType.APPLICATION_JSON)
//    public Response addRole(Tblrolemaster role) {
//        try {
//            ejb.addRole(role);
//            return Response.ok("Role Added").build();
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//    @GET
//    @Path("getRoles")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getRoles() {
//        try {
//            return Response.ok(ejb.getRoles()).build();
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }

    // ================= USER =================
    @GET
    @Path("getAllUsers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        try {
            return Response.ok(ejb.getAllUsers()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("updateUserStatus")
    public Response updateUserStatus(@QueryParam("userId") int userId,
                                     @QueryParam("userStatus") String userStatus) {
        try {
            if (userStatus == null || userStatus.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Status is required").build();
            }

            ejb.updateUserStatus(userId, userStatus);
            return Response.ok("User Updated").build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("searchUsersByEmail")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchUsersByEmail(@QueryParam("userEmail") String userEmail) {
        try {
            return Response.ok(ejb.searchUsersByEmail(userEmail)).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
    
    @PUT
    @Path("toggleUserStatus")
    public Response toggleUserStatus(
            @QueryParam("userId") int userId,
            @QueryParam("status") boolean status) {

        try {

            ejb.toggleUserStatus(userId, status);

            return Response.ok("User Updated").build();

        } catch (Exception e) {

            return Response.status(500)
                    .entity(e.getMessage())
                    .build();
        }
    }
    
    
    // ================= RECRUITER =================
    @GET
    @Path("getAllRecruiters")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRecruiters() {
        try {
            return Response.ok(ejb.getAllRecruiters()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    // ================= CANDIDATE =================
    @GET
    @Path("getAllCandidates")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCandidates() {
        try {
            return Response.ok(ejb.getAllCandidates()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    
    
    
    
    // ================= Manage Skills =================

    @GET
    @Path("getAllSkillCategories")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Tblskillcategory> getAllSkillCategories() {

        try {

            return ejb.getAllSkillCategories();

        } catch (Exception e) {

            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    @POST
    @Path("addSkillCategory")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addSkillCategory(Tblskillcategory category) {

        try {

            ejb.addSkillCategory(category);

            return Response.ok("Category Added Successfully").build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed To Add Category")
                    .build();
        }
    }

    @PUT
    @Path("updateSkillCategory")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateSkillCategory(Tblskillcategory category) {

        try {

            ejb.updateSkillCategory(category);

            return Response.ok("Category Updated Successfully").build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed To Update Category")
                    .build();
        }
    }

    @DELETE
    @Path("deleteSkillCategory/{id}")
    public Response deleteSkillCategory(
            @PathParam("id") Integer id) {

        try {

            ejb.deleteSkillCategory(id);

            return Response.ok("Category Deleted Successfully").build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed To Delete Category")
                    .build();
        }
    }

    @GET
    @Path("getAllSkills")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Tblskills> getAllSkills() {

        try {

            return ejb.getAllSkills();

        } catch (Exception e) {

            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    @POST
    @Path("addSkill")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addSkill(Tblskills skill) {

        try {

            ejb.addSkill(skill);

            return Response.ok("Skill Added Successfully").build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed To Add Skill")
                    .build();
        }
    }

    @PUT
    @Path("updateSkill")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateSkill(Tblskills skill) {

        try {

            ejb.updateSkill(skill);

            return Response.ok("Skill Updated Successfully").build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed To Update Skill")
                    .build();
        }
    }

    @DELETE
    @Path("deleteSkill/{id}")
    public Response deleteSkill(
            @PathParam("id") Integer id) {

        try {

            ejb.deleteSkill(id);

            return Response.ok("Skill Deleted Successfully").build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed To Delete Skill")
                    .build();
        }
    }
    
    
    
    

    // ================= COMPANY =================
//    @POST
//    @Path("addCompany")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response addCompany(Tblcompany company) {
//        try {
//            if (company == null) {
//                return Response.status(400).entity("Company data required").build();
//            }
//            
//            ejb.addCompany(company);
//            return Response.ok("Company Added").build();
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//    @GET
//    @Path("getAllCompanies")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getAllCompanies() {
//        try {
//            return Response.ok(ejb.getAllCompanies()).build();
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }

    @POST
    @Path("addCompany")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addCompany(Tblcompany company) {

        try {

            ejb.addCompany(company);

            return Response.ok(
                    "Company added successfully")
                    .build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Unable to add company")
                    .build();
        }
    }

    @PUT
    @Path("updateCompany")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateCompany(Tblcompany company) {

        try {

            ejb.updateCompany(company);

            return Response.ok(
                    "Company updated successfully")
                    .build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Unable to update company")
                    .build();
        }
    }

    @DELETE
    @Path("deleteCompany/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteCompany(
            @PathParam("id") Integer companyId) {

        try {

            ejb.deleteCompany(companyId);

            return Response.ok(
                    "Company deleted successfully")
                    .build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Unable to delete company")
                    .build();
        }
    }

    @GET
    @Path("getCompany/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCompany(
            @PathParam("id") Integer companyId) {

        try {

            Tblcompany company =
                    ejb.findCompanyById(companyId);

            if (company == null) {

                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Company not found")
                        .build();
            }

            return Response.ok(company)
                    .build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.BAD_REQUEST)
                    .build();
        }
    }

    @GET
    @Path("getAllCompanies")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCompanies() {

        try {

            Collection<Tblcompany> companies =
                    ejb.getAllCompanies();

            return Response.ok(companies)
                    .build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.BAD_REQUEST)
                    .build();
        }
    }

    @PUT
    @Path("toggleCompanyStatus")
    @Produces(MediaType.TEXT_PLAIN)
    public Response toggleCompanyStatus(
            @QueryParam("companyId") Integer companyId,
            @QueryParam("status") Boolean status) {

        try {

            ejb.toggleCompanyStatus(companyId, status);

            return Response.ok(
                    "Company status updated successfully")
                    .build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Unable to update company status")
                    .build();
        }
    }

    // ================= JOB =================
     @GET
    @Path("getAllJobs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllJobs() {
        try {
            return Response.ok(ejb.getAllJobs()).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
    
    @GET
    @Path("/getJobByJobId/{jobId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJobByJobId(@PathParam("jobId") Integer jobId)
    {
        Tbljob job = ejb.getJobByJobId(jobId);

        if(job == null)
        {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Job not found")
                           .build();
        }

        return Response.ok(job).build();
    }
    
//    @PUT
//    @Path("updateJobStatus/{jobId}/{jobStatus}")
//    public Response updateJobStatus(@PathParam("jobId") int jobId,
//                                   @PathParam("jobStatus") String jobStatus) {
//        try {
//            ejb.updateJobStatus(jobId, jobStatus);
//            return Response.ok("Job Status Updated").build();
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//    @PUT
//    @Path("approveJob/{jobId}")
//    public Response approveJob(@PathParam("jobId") int jobId) {
//        try {
//            ejb.approveJob(jobId);
//            return Response.ok("Job Approved").build();
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//    @PUT
//    @Path("rejectJob/{jobId}")
//    public Response rejectJob(@PathParam("jobId") int jobId) {
//        try {
//            ejb.rejectJob(jobId);
//            return Response.ok("Job Rejected").build();
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//    @GET
//    @Path("searchJobsByTitle")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response searchJobsByTitle(@QueryParam("jobTitle") String jobTitle) {
//        try {
//            if (jobTitle == null || jobTitle.trim().isEmpty()) {
//                return Response.status(400).entity("Job title required").build();
//            }
//            
//            return Response.ok(ejb.searchJobsByTitle(jobTitle)).build();
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
    
    // ========================Profile=======================
        
    @GET
    @Path("getAdminProfile/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Tblusers getAdminProfile(
            @PathParam("userId") Integer userId) {

        return ejb.getAdminProfile(userId);
    }
    
    @PUT
    @Path("updateAdminProfile")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAdminProfile(
            Tblusers user) {

        ejb.updateAdminProfile(user);

        return Response.ok().build();
    }
    
    @PUT
    @Path("changeAdminPassword")
    @Produces(MediaType.TEXT_PLAIN)
    public Response changeAdminPassword(
            @QueryParam("userId") Integer userId,
            @QueryParam("currentPassword") String currentPassword,
            @QueryParam("newPassword") String newPassword) {

        System.out.println("REST CALLED: changeAdminPassword");
        String result = ejb.changeAdminPassword(userId, currentPassword, newPassword);

        if (result.equals("Password updated successfully")) {
            return Response.ok(result).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(result)
                    .build();
        }
    }
    
    @PUT
    @Path("uploadProfilePhoto")
    public Response uploadProfilePhoto(
            @QueryParam("userId") Integer userId,
            @QueryParam("photo") String photo) {

        ejb.uploadProfilePhoto(
                userId,
                photo);

        return Response.ok().build();
    }

    
    
    
    
    
    
    
    // ================= ADMIN NOTIFICATIONS =================

    @GET
    @Path("getAdminNotifications")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAdminNotifications(
            @QueryParam("adminId") int adminId) {

        try {

            return Response.ok(
                    ejb.getAdminNotifications(adminId))
                    .build();

        } catch (Exception e) {

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("getAdminUnreadNotifications")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAdminUnreadNotifications(
            @QueryParam("adminId") int adminId) {

        try {

            return Response.ok(
                    ejb.getAdminUnreadNotifications(adminId))
                    .build();

        } catch (Exception e) {

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("markNotificationAsRead")
    @Produces(MediaType.TEXT_PLAIN)
    public Response markNotificationAsRead(
            @QueryParam("notificationId") int notificationId) {

        try {

            ejb.markNotificationAsRead(notificationId);

            return Response.ok(
                    "Notification marked as read")
                    .build();

        } catch (Exception e) {

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }
    
    
    
    
    
    
    // ================= APPLICATION =================
//    @GET
//    @Path("getAllApplications")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getAllApplications() {
//        try {
//            return Response.ok(ejb.getAllApplications()).build();
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//    @PUT
//    @Path("updateApplicationStatus/{applicationId}/{applicationStatus}")
//    public Response updateApplicationStatus(@PathParam("applicationId") int applicationId,
//                                            @PathParam("applicationStatus") String applicationStatus) {
//        try {
//            ejb.updateApplicationStatus(applicationId, applicationStatus);
//            return Response.ok("Application Updated").build();
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//    // ================= NOTIFICATION =================
//    @POST
//    @Path("sendNotification")
//    @Consumes(MediaType.APPLICATION_JSON)
//    public Response sendNotification(Tblnotification notification) {
//        try {
//            ejb.sendNotification(notification);
//            return Response.ok("Notification Sent").build();
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//    
//    // ================= DASHBOARD =================
//    @GET
//    @Path("dashboard/totalUsers")
//    public Response totalUsers() {
//        try {
//            return Response.ok(ejb.totalUsers()).build();
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//    @GET
//    @Path("dashboard/totalJobs")
//    public Response totalJobs() {
//        try {
//            return Response.ok(ejb.totalJobs()).build();
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//    @GET
//    @Path("dashboard/totalApplications")
//    public Response totalApplications() {
//        try {
//            return Response.ok(ejb.totalApplications()).build();
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//    @GET
//    @Path("dashboard/totalCandidates")
//    public Response totalCandidates() {
//        try {
//            return Response.ok(ejb.totalCandidates()).build();
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//    @GET
//    @Path("dashboard/totalCompanies")
//    public Response totalCompanies() {
//        try {
//            return Response.ok(ejb.totalCompanies()).build();
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//    // ================= REPORT =================
//    @GET
//    @Path("report/applicationsPerJob/{jobId}")
//    public Response applicationsPerJob(@PathParam("jobId") int jobId) {
//        try {
//            if (jobId <= 0) {
//                return Response.status(Response.Status.BAD_REQUEST)
//                        .entity("Invalid Job ID").build();
//            }
//
//            return Response.ok(ejb.applicationsPerJob(jobId)).build();
//
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//    @GET
//    @Path("report/jobsPerCompany/{companyId}")
//    public Response jobsPerCompany(@PathParam("companyId") int companyId) {
//        try {
//            if (companyId <= 0) {
//                return Response.status(Response.Status.BAD_REQUEST)
//                        .entity("Invalid Company ID").build();
//            }
//
//            return Response.ok(ejb.jobsPerCompany(companyId)).build();
//
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//    @GET
//    @Path("report/selectedApplications")
//    public Response selectedApplications() {
//        try {
//            return Response.ok(ejb.selectedApplications()).build();
//
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//    @GET
//    @Path("report/jobWiseApplicationReport")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response jobWiseApplicationReport() {
//        try {
//            return Response.ok(ejb.jobWiseApplicationReport()).build();
//
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
}
