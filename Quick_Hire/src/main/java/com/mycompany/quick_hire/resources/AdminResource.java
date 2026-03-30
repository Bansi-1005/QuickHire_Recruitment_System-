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

/**
 *
 * @author tejan
 */

@Path("admin")
public class AdminResource {
    @EJB
    AdminBeanLocal ejb;
    
     // ================= ROLE =================
    @POST
    @Path("addRole")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addRole(Tblrolemaster role) {
        try {
            ejb.addRole(role);
            return Response.ok("Role Added").build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("getRoles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoles() {
        try {
            return Response.ok(ejb.getRoles()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

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

    @DELETE
    @Path("deleteCandidate/{candidateId}")
    public Response deleteCandidate(@PathParam("candidateId") int candidateId) {
        try {
            if (candidateId <= 0) {
                return Response.status(400).entity("Invalid ID").build();
            }
             
            ejb.deleteCandidate(candidateId);
            return Response.ok("Candidate Deleted").build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    // ================= COMPANY =================
    @POST
    @Path("addCompany")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCompany(Tblcompany company) {
        try {
            if (company == null) {
                return Response.status(400).entity("Company data required").build();
            }
            
            ejb.addCompany(company);
            return Response.ok("Company Added").build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("getAllCompanies")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCompanies() {
        try {
            return Response.ok(ejb.getAllCompanies()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("approveCompany/{companyId}")
    public Response approveCompany(@PathParam("companyId") int companyId) {
        try {
            ejb.approveCompany(companyId);
            return Response.ok("Company Approved").build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("rejectCompany/{companyId}")
    public Response rejectCompany(@PathParam("companyId") int companyId) {
        try {
            ejb.rejectCompany(companyId);
            return Response.ok("Company Rejected").build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
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
    
    @PUT
    @Path("updateJobStatus/{jobId}/{jobStatus}")
    public Response updateJobStatus(@PathParam("jobId") int jobId,
                                   @PathParam("jobStatus") String jobStatus) {
        try {
            ejb.updateJobStatus(jobId, jobStatus);
            return Response.ok("Job Status Updated").build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("approveJob/{jobId}")
    public Response approveJob(@PathParam("jobId") int jobId) {
        try {
            ejb.approveJob(jobId);
            return Response.ok("Job Approved").build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("rejectJob/{jobId}")
    public Response rejectJob(@PathParam("jobId") int jobId) {
        try {
            ejb.rejectJob(jobId);
            return Response.ok("Job Rejected").build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("searchJobsByTitle")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchJobsByTitle(@QueryParam("jobTitle") String jobTitle) {
        try {
            if (jobTitle == null || jobTitle.trim().isEmpty()) {
                return Response.status(400).entity("Job title required").build();
            }
            
            return Response.ok(ejb.searchJobsByTitle(jobTitle)).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    // ================= APPLICATION =================
    @GET
    @Path("getAllApplications")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllApplications() {
        try {
            return Response.ok(ejb.getAllApplications()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("updateApplicationStatus/{applicationId}/{applicationStatus}")
    public Response updateApplicationStatus(@PathParam("applicationId") int applicationId,
                                            @PathParam("applicationStatus") String applicationStatus) {
        try {
            ejb.updateApplicationStatus(applicationId, applicationStatus);
            return Response.ok("Application Updated").build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    // ================= NOTIFICATION =================
    @POST
    @Path("sendNotification")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendNotification(Tblnotification notification) {
        try {
            ejb.sendNotification(notification);
            return Response.ok("Notification Sent").build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
    
    // ================= DASHBOARD =================
    @GET
    @Path("dashboard/totalUsers")
    public Response totalUsers() {
        try {
            return Response.ok(ejb.totalUsers()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("dashboard/totalJobs")
    public Response totalJobs() {
        try {
            return Response.ok(ejb.totalJobs()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("dashboard/totalApplications")
    public Response totalApplications() {
        try {
            return Response.ok(ejb.totalApplications()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("dashboard/totalCandidates")
    public Response totalCandidates() {
        try {
            return Response.ok(ejb.totalCandidates()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("dashboard/totalCompanies")
    public Response totalCompanies() {
        try {
            return Response.ok(ejb.totalCompanies()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    // ================= REPORT =================
    @GET
    @Path("report/applicationsPerJob/{jobId}")
    public Response applicationsPerJob(@PathParam("jobId") int jobId) {
        try {
            if (jobId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Job ID").build();
            }

            return Response.ok(ejb.applicationsPerJob(jobId)).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("report/jobsPerCompany/{companyId}")
    public Response jobsPerCompany(@PathParam("companyId") int companyId) {
        try {
            if (companyId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Company ID").build();
            }

            return Response.ok(ejb.jobsPerCompany(companyId)).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("report/selectedApplications")
    public Response selectedApplications() {
        try {
            return Response.ok(ejb.selectedApplications()).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("report/jobWiseApplicationReport")
    @Produces(MediaType.APPLICATION_JSON)
    public Response jobWiseApplicationReport() {
        try {
            return Response.ok(ejb.jobWiseApplicationReport()).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
}
