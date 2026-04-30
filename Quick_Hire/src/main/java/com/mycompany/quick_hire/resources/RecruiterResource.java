/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quick_hire.resources;
import EJB.RecruiterBeanLocal;
import Entity.*;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 *
 * @author RINKAL
 */

@Path("recruiter")
public class RecruiterResource {
    
    @EJB RecruiterBeanLocal ejb;
    // ================= AUTH =================
//    @GET
//    @Path("login")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response login(@QueryParam("email") String email,
//                          @QueryParam("password") String password,
//                          @QueryParam("roleId") int roleId) {
//        try {
//            Tblusers user = ejb.recruiterLogin(email, password, roleId);
//            if (user != null)
//                return Response.ok(user).build();
//            else
//                return Response.status(401).entity("Invalid Credentials").build();
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }



//    @POST
//    @Path("registerRecruiter")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response registerRecruiter(Tblrecruiters recruiter) {
//        try {
//
//            if (recruiter == null || recruiter.getUserId() == null) {
//                return Response.status(Response.Status.BAD_REQUEST)
//                        .entity("User data is missing").build();
//            }
//
//            Tblusers user = recruiter.getUserId();
//            
//
//            ejb.registerRecruiter(user, recruiter);
//
//            return Response.ok("Recruiter Registered Successfully").build();
//
//        } catch (Exception e) {
//            e.printStackTrace();   
//            return Response.status(500).entity(e.toString()).build();
//        }
//    }

        // ================= PROFILE =================
    @GET
    @Path("getProfile")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProfile(@QueryParam("userId") int userId) {
        try {
            Tblrecruiters recruiter = ejb.getProfile(userId);

            if (recruiter == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Profile not found").build();
            }

            return Response.ok(recruiter).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    
    @PUT
    @Path("updateProfile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateProfile(Tblrecruiters recruiter) {
        try {
            if (recruiter == null || recruiter.getRecruiterId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Recruiter ID required")
                        .build();
            }

            ejb.updateProfile(recruiter);

            return Response.ok("Updated Successfully").build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    // ================= COMPANY =================
    
    @GET
    @Path("getCompany")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCompany(@QueryParam("recruiterId") int recruiterId) {
        try {
            if (recruiterId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid recruiterId")
                        .build();
            }

            Tblcompany company = ejb.getCompanyDetails(recruiterId);

            if (company == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Company not found")
                        .build();
            }

            return Response.ok(company).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

// ================= JOB MANAGEMENT =================
    
    @POST
    @Path("createJob")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createJob(Tbljob job) {
        try {
            if (job == null || job.getRecruiterId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Job data or recruiter missing")
                        .build();
            }

            ejb.createJob(job);

            return Response.ok("Job Created Successfully").build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

//    @PUT
//    @Path("updateJob")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.TEXT_PLAIN)
//    public Response updateJob(Tbljob job) {
//        try {
//            if (job == null || job.getJobId() == null) {
//                return Response.status(Response.Status.BAD_REQUEST)
//                        .entity("Job ID required")
//                        .build();
//            }
//
//            ejb.updateJob(job);
//
//            return Response.ok("Job Updated Successfully").build();
//
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
    @PUT
    @Path("updateJob")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateJob(Tbljob job) {
        try {
            if (job == null || job.getJobId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Job ID is required")
                        .build();
            }
            ejb.updateJob(job);

            return Response.ok("Job Updated Successfully").build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating job: " + e.getMessage())
                    .build();
        }
    }
    @DELETE
    @Path("deleteJob")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteJob(@QueryParam("jobId") int jobId,
                              @QueryParam("recruiterId") int recruiterId) {
        try {
            if (jobId <= 0 || recruiterId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid IDs")
                        .build();
            }

            ejb.deleteJob(jobId, recruiterId);

            return Response.ok("Job Deleted Successfully").build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    //multiple,single and all job delete logic
//    @DELETE
//@Path("deleteJob")
//@Produces(MediaType.TEXT_PLAIN)
//public Response deleteJob(@QueryParam("jobId") Integer jobId,
//                          @QueryParam("jobIds") List<Integer> jobIds,
//                          @QueryParam("recruiterId") int recruiterId) {
//    try {
//
//        if (recruiterId <= 0) {
//            return Response.status(Response.Status.BAD_REQUEST)
//                    .entity("Recruiter ID required")
//                    .build();
//        }
//
//        ejb.deleteJob(jobId, jobIds, recruiterId);
//
//        // Response messages
//        if (jobId != null && jobId == 0) {
//            return Response.ok("All Jobs Deleted Successfully").build();
//        } else if (jobIds != null && !jobIds.isEmpty()) {
//            return Response.ok("Multiple Jobs Deleted Successfully").build();
//        } else if (jobId != null && jobId > 0) {
//            return Response.ok("Single Job Deleted Successfully").build();
//        } else {
//            return Response.status(Response.Status.BAD_REQUEST)
//                    .entity("Provide jobId or jobIds")
//                    .build();
//        }
//
//    } catch (Exception e) {
//        return Response.status(500).entity(e.getMessage()).build();
//    }
//}
    @PUT
    @Path("updateJobStatus")
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateJobStatus(@QueryParam("jobId") int jobId,
                                   @QueryParam("status") String status) {
        try {
            if (jobId <= 0 || status == null || status.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("JobId or Status missing")
                        .build();
            }

            ejb.updateJobStatus(jobId, status);

            return Response.ok("Status Updated Successfully").build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
    
    @GET
    @Path("getJobs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJobs(@QueryParam("recruiterId") int recruiterId) {
        try {
            if (recruiterId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Recruiter ID")
                        .build();
            }

            Collection<Tbljob> jobs = ejb.getJobs(recruiterId);

            if (jobs == null || jobs.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No jobs found")
                        .build();
            }

            return Response.ok(jobs).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    // ================= JOB SKILLS =================
    
    @POST
    @Path("addSkillToJob")
    @Produces(MediaType.TEXT_PLAIN)
    public Response addSkillToJob(@QueryParam("jobId") int jobId,
                             @QueryParam("skillId") int skillId) {
        try {
            if (jobId <= 0 || skillId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("JobId or SkillId missing")
                        .build();
            }

            ejb.addSkillToJob(jobId, skillId);

            return Response.ok("Skill Added Successfully").build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
    
    
    @DELETE
    @Path("removeSkillFromJob")
    @Produces(MediaType.TEXT_PLAIN)
    public Response removeSkillFromJob(@QueryParam("jobId") int jobId,
                                @QueryParam("skillId") int skillId) {
        try {
            if (jobId <= 0 || skillId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("JobId or SkillId missing")
                        .build();
            }

            ejb.removeSkillFromJob(jobId, skillId);

            return Response.ok("Skill Removed Successfully").build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
    
    @GET
    @Path("getJobSkills")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJobSkills(@QueryParam("jobId") int jobId) {
        try {
            if (jobId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Job ID")
                        .build();
            }

            Collection<Tblskills> skills = ejb.getJobSkills(jobId);

            if (skills == null || skills.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No skills found for this job")
                        .build();
            }

            return Response.ok(skills).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    // ================= APPLICATION =================
    
    @GET
    @Path("getApplications")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApplications(@QueryParam("jobId") int jobId) {
        try {
            if (jobId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Job ID")
                        .build();
            }

            Collection<Tblapplication> apps = ejb.getApplications(jobId);

            if (apps == null || apps.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No applications found")
                        .build();
            }

            return Response.ok(apps).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    
    @PUT
    @Path("updateApplicationStatus")
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateApplicationStatus(@QueryParam("applicationId") int id,
                                 @QueryParam("status") String status) {
        try {
            if (id <= 0 || status == null || status.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("ApplicationId or Status missing")
                        .build();
            }

            ejb.updateApplicationStatus(id, status);

            return Response.ok("Application Status Updated Successfully").build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    // ================= APPLICATION HISTORY =================
    
    @POST
    @Path("addApplicationHistory")
    @Produces(MediaType.TEXT_PLAIN)
    public Response addApplicationHistory(@QueryParam("applicationId") int applicationId,
                                          @QueryParam("oldStatus") String oldStatus,
                                          @QueryParam("newStatus") String newStatus) {
        try {

            if (applicationId <= 0 || oldStatus == null || newStatus == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Missing required fields")
                        .build();
            }

            ejb.addApplicationStatusHistory(applicationId, oldStatus, newStatus);

            return Response.ok("History Added Successfully").build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    // ================= SCREENING =================
    
    @POST
    @Path("generateScore")
    @Produces(MediaType.TEXT_PLAIN)
    public Response generateScore(@QueryParam("applicationId") int id) {
        try {
            if (id <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Application ID")
                        .build();
            }

            ejb.generateScreeningScore(id);

            return Response.ok("Score Generated Successfully").build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    
    @GET
    @Path("getScore")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getScore(@QueryParam("applicationId") int applicationId) {
        try {
            if (applicationId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Application ID")
                        .build();
            }

            Tblscreeningscore score = ejb.getScore(applicationId);

            if (score == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Score not found")
                        .build();
            }

            return Response.ok(score).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

//    // ================= SHORTLIST =================
    
    @GET
    @Path("topCandidates")
    @Produces(MediaType.APPLICATION_JSON)
    public Response topCandidates(@QueryParam("jobId") int jobId) {
        try {
            if (jobId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Job ID").build();
            }

            Collection<Tblapplication> list = ejb.getTopCandidates(jobId);

            if (list == null || list.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No candidates found").build();
            }

            return Response.ok(list).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("filterCandidates")
    @Produces(MediaType.APPLICATION_JSON)
    public Response filterCandidates(@QueryParam("jobId") int jobId,
                                     @QueryParam("score") double score) {
        try {
            if (jobId <= 0 || score < 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid input").build();
            }

            Collection<Tblapplication> list =
                    ejb.filterCandidatesByScore(jobId, score);

            if (list == null || list.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No candidates found").build();
            }

            return Response.ok(list).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
//    // ================= INTERVIEW =================
    
    @POST
    @Path("scheduleInterview")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response scheduleInterview(Tblinterview i) {
        try {
            if (i == null || i.getApplicationId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Application ID required").build();
            }

            ejb.scheduleInterview(i);

            return Response.ok("Interview Scheduled Successfully").build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    
    @PUT
    @Path("updateInterview")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateInterview(Tblinterview i) {
        try {
            if (i == null || i.getInterviewId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Interview ID required").build();
            }

            ejb.updateInterview(i);

            return Response.ok("Interview Updated Successfully").build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("updateFeedback")
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateFeedback(@QueryParam("interviewId") int id,
                             @QueryParam("feedback") String feedback,
                             @QueryParam("result") String result) {
        try {
            if (id <= 0 || feedback == null || result == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Missing data").build();
            }

            ejb.updateInterviewFeedback(id, feedback, result);

            return Response.ok("Feedback Updated Successfully").build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
   // ================= NOTIFICATION =================
    
    @POST
    @Path("sendNotification")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendNotification(Tblnotification n) {
        try {
            if (n == null || n.getUserId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("User ID required").build();
            }

            ejb.sendNotification(n);

            return Response.ok("Notification Sent").build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("getNotifications")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotifications(@QueryParam("userId") int userId) {
        try {
            if (userId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid User ID").build();
            }

            Collection<Tblnotification> list =
                    ejb.getRecruiterNotifications(userId);

            if (list == null || list.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No notifications found").build();
            }

            return Response.ok(list).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

}
