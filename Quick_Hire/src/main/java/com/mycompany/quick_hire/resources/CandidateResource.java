/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quick_hire.resources;

import EJB.CandidateBeanLocal;
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
import java.util.Collection;

/**
 *
 * @author tejan
 */

@Path("candidate")
public class CandidateResource {
    @EJB
    CandidateBeanLocal ejb;
    
     // ================= AUTH =================
//    @GET
//    @Path("login")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response login(@QueryParam("email") String email,
//                          @QueryParam("password") String password,
//                          @QueryParam("roleId") int roleId) {
//        try {
//            Tblusers user = ejb.candidateLogin(email, password, roleId);
//            if (user != null) {
//                return Response.ok(user).build();
//            } else {
//                return Response.status(Response.Status.UNAUTHORIZED)
//                        .entity("Invalid Credentials").build();
//            }
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
    
    // ================= REGISTER =================
//    @POST
//    @Path("registerCandidate")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response registerCandidate(Tblcandidates candidate) {
//        try {
//            if (candidate == null || candidate.getUserId() == null) {
//                return Response.status(Response.Status.BAD_REQUEST)
//                        .entity("User data is missing").build();
//            }
//
//            Tblusers user = candidate.getUserId();
//
//            ejb.registerCandidate(user, candidate);
//
//            return Response.ok("Candidate Registered Successfully").build();
//
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
    
    // ================= PROFILE =================
    @GET
    @Path("getCandidateProfile")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCandidateProfile(@QueryParam("userId") int userId) {
        try {
            Tblcandidates c = ejb.getCandidateProfile(userId);

            if (c == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Candidate not found").build();
            }

            return Response.ok(c).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.toString()).build();
        }
    }
    
    
    // ================= UPDATE PROFILE =================
    @PUT
    @Path("updateCandidateProfile")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCandidateProfile(Tblcandidates candidate) {
        try {
            if (candidate == null || candidate.getCandidateId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Candidate ID is required").build();
            }

            ejb.updateCandidateProfile(candidate);
            return Response.ok("Profile Updated Successfully").build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    
    @PUT
    @Path("uploadProfilePhoto")
    public Response uploadProfilePhoto(
            @QueryParam("userId") int userId,
            @QueryParam("photo") String photo) {

        try {

            ejb.uploadProfilePhoto(userId, photo);

            return Response.ok("Profile photo updated").build();

        } catch (Exception e) {

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }




    // ================= RESUME =================
    
    @GET
    @Path("getCandidateResumes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCandidateResumes(
            @QueryParam("candidateId") int candidateId) {

        try {
            return Response.ok(
                    ejb.getCandidateResumes(candidateId)
            ).build();

        } catch (Exception e) {

            return Response.status(500)
                    .entity(e.getMessage())
                    .build();
        }
    }
    
    @POST
    @Path("uploadResume")
    public Response uploadResume(
            @QueryParam("candidateId") int candidateId,
            @QueryParam("resumeFile") String resumeFile) {

        try {

            ejb.uploadResume(candidateId, resumeFile);

            return Response.ok("Resume Uploaded").build();

        } catch (Exception e) {

            return Response.status(500)
                    .entity(e.getMessage())
                    .build();
        }
    }
    
    @DELETE
    @Path("deleteResume")
    public Response deleteResume(
            @QueryParam("resumeId") int resumeId) {

        try {

            ejb.deleteResume(resumeId);

            return Response.ok("Resume Deleted").build();

        } catch (Exception e) {

            return Response.status(500)
                    .entity(e.getMessage())
                    .build();
        }
    }
    
    // ================= TOGGLE RESUME =================
    @PUT
    @Path("toggleResumeStatus")
    public Response toggleResumeStatus(
            @QueryParam("resumeId") int resumeId,  @QueryParam("status") boolean status) {

        try {

            ejb.toggleResumeStatus(resumeId, status);

            return Response.ok("Resume Updated").build();

        } catch (Exception e) {

            return Response.status(500)
                    .entity(e.getMessage())
                    .build();
        }
    }
    
//    @POST
//    @Path("uploadResume")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response uploadResume(@QueryParam("candidateId") int candidateId,
//                                 @QueryParam("resume") String resume) {
//        try {
//            if (resume == null || resume.trim().isEmpty()) {
//                return Response.status(Response.Status.BAD_REQUEST)
//                        .entity("Resume cannot be empty").build();
//            }
//
//            ejb.uploadResume(candidateId, resume);
//            return Response.ok("Resume Uploaded").build();
//
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//    @GET
//    @Path("getCandidateResume")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getCandidateResume(@QueryParam("candidateId") int candidateId) {
//        try {
//            String res = ejb.getCandidateResume(candidateId);
//
//            if (res == null) {
//                return Response.status(404).entity("Resume not found").build();
//            }
//
//            return Response.ok(res).build();
//
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }

    // ================= SKILLS =================
    @GET
    @Path("/getAllSkills")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSkills() {

        try {

            Collection<Tblskills> skills = ejb.getAllSkills();

            return Response
                    .status(Response.Status.OK)
                    .entity(skills)
                    .build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to load skills")
                    .build();
        }
    }
    
    
    @GET
    @Path("getAllSkillCategories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSkillCategories() {

        try {

            return Response.ok(
                    ejb.getAllSkillCategories()
            ).build();

        } catch (Exception e) {

            return Response.status(500)
                    .entity(e.getMessage())
                    .build();
        }
    }
    
    @GET
@Path("getSkillsByCategory")
@Produces(MediaType.APPLICATION_JSON)
    public Response getSkillsByCategory(
            @QueryParam("categoryId") int categoryId) {

        try {

            return Response.ok(
                    ejb.getSkillsByCategory(categoryId)
            ).build();

        } catch (Exception e) {

            return Response.status(500)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("addSkillToCandidate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSkillToCandidate(
        @QueryParam("candidateId") int candidateId,
        @QueryParam("skillId") int skillId) {

        try {

            String msg = ejb.addSkillToCandidate(candidateId, skillId);

            if ("Skill already added".equals(msg)) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(msg)
                        .build();
            }

            return Response.ok(msg).build();

        } catch (Exception e) {

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }
    
//    @PUT
//    @Path("updateSkillToCandidate")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response updateSkillToCandidate(@QueryParam("candidateId") int candidateId,
//                                           @QueryParam("skillId") String skillIdsStr) {
//       try {
//            // 🔥 Convert "3,1" → List<Integer>
//            Collection<Integer> skillIds = new ArrayList<>();
//
//            if (skillIdsStr != null && !skillIdsStr.isEmpty()) {
//                String[] parts = skillIdsStr.split(",");
//                for (String p : parts) {
//                    skillIds.add(Integer.parseInt(p.trim()));
//                }
//            }
//
//            ejb.updateSkillToCandidate(candidateId, skillIds);
//
//            return Response.ok("Skills Updated").build();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }

    @DELETE
    @Path("removeSkillFromCandidate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeSkillFromCandidate(@QueryParam("candidateId") int candidateId,
                               @QueryParam("skillId") int skillId) {
        try {
            ejb.removeSkillFromCandidate(candidateId, skillId);
            return Response.ok("Skill Removed").build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("getCandidateSkills")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCandidateSkills(@QueryParam("candidateId") int candidateId) {
        try {
            Collection<Tblskills> skills = ejb.getCandidateSkills(candidateId);
            
            if (skills == null || skills.isEmpty()) {
                return Response.status(404).entity("No skills found").build();
            }
            
            return Response.ok(skills).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    
    
    
    
    // ================= EDUCATION =================
    @GET
    @Path("/getAllEducations")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Tbleducation> getAllEducations() {

        return ejb.getAllEducations();
    }
    
    
    @GET
    @Path("getCandidateEducation")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCandidateEducation(@QueryParam("candidateId") Integer candidateId) {

        try {
            if (candidateId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("candidateId required")
                        .build();
            }

            Collection<Tblcandidateeducation> list =
                    ejb.getCandidateEducation(candidateId);

            return Response.ok(list).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error loading education")
                    .build();
        }
    }
    
    @POST
    @Path("addCandidateEducation/{candidateId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCandidateEducation(
            Tblcandidateeducation edu,
            @PathParam("candidateId") Integer candidateId) {

        try {

            if (candidateId == null || candidateId <= 0 || edu == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid input")
                        .build();
            }

            Tblcandidateeducation created =
                    ejb.addCandidateEducation(edu, candidateId);

            if (created == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Failed to create education")
                        .build();
            }

            // ✅ return OBJECT (important fix)
            return Response.status(Response.Status.CREATED)
                    .entity(created)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("updateCandidateEducation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCandidateEducation(Tblcandidateeducation edu) {

        try {

            if (edu == null || edu.getCandidateEducationId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Education ID required")
                        .build();
            }

            Tblcandidateeducation updated =
                    ejb.updateCandidateEducation(edu);

            if (updated == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Education not found")
                        .build();
            }

            return Response.ok(updated).build(); // ✅ return object

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("removeCandidateEducation/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeCandidateEducation(@PathParam("id") Integer id) {

        try {
            ejb.removeCandidateEducation(id);

            return Response.ok("Deleted successfully").build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage())
                    .build();
        }
    }
    
    
    
    // ================= JOBS =================
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

//    @GET
//    @Path("searchJobsByLocation")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response searchJobsByLocation(@QueryParam("jobLocation") String jobLocation) {
//        try {
//            return Response.ok(ejb.searchJobsByLocation(jobLocation)).build();
//
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//    @GET
//    @Path("searchJobsBySkill")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response searchJobsBySkill(@QueryParam("skill") String skill) {
//        try {
//            return Response.ok(ejb.searchJobsBySkill(skill)).build();
//
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }

    // ================= APPLICATION =================
    @POST
    @Path("applyForJob")
    @Produces(MediaType.TEXT_PLAIN)
    public Response applyForJob(
            @QueryParam("candidateId") int candidateId,
            @QueryParam("jobId") int jobId,
            @QueryParam("resumeId") int resumeId) {

        try {

            String result =
                    ejb.applyForJob(
                            candidateId,
                            jobId,
                            resumeId);

            if ("Already Applied".equals(result)) {

                return Response.status(Response.Status.CONFLICT)
                        .entity(result)
                        .build();
            }

            if ("Applied Successfully".equals(result)) {

                return Response.ok(result)
                        .build();
            }

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(result)
                    .build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error : " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("getCandidateApplications")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCandidateApplications(@QueryParam("candidateId") int candidateId) {
        try {
            
            return Response.ok(ejb.getCandidateApplications(candidateId)).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
    
    @DELETE
    @Path("deleteApplication")
    public Response deleteApplication(@QueryParam("applicationId") int applicationId) {
        try {
            ejb.deleteApplication(applicationId);
            return Response.ok("Application Deleted").build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    // ================= STATUS =================
    
//    @GET
//    @Path("getApplicationDetails")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getApplicationDetails(@QueryParam("applicationId") int applicationId) {
//        try {
//            Tblapplication app = ejb.getApplicationDetails(applicationId);
//
//            if (app == null) {
//                return Response.status(404).entity("Application not found").build();
//            }
//
//            return Response.ok(app).build();
//
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
    
    @GET
    @Path("getApplicationStatus")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApplicationStatus(@QueryParam("applicationId") int applicationId) {
        try {
            String status = ejb.getApplicationStatus(applicationId);

            if (status == null || status.isEmpty()) {
                return Response.status(404).entity("Status not found").build();
            }

            return Response.ok(status).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
    
    @PUT
    @Path("updateApplicationStatus")
    public Response updateApplicationStatus(@QueryParam("applicationId") int applicationId,
                                 @QueryParam("status") String status) {
        try {
            ejb.updateApplicationStatus(applicationId, status);
            return Response.ok("Status Updated").build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }


//    // ================= SCREENING =================
//    @GET
//    @Path("getScreeningScore")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getScreeningScore(@QueryParam("applicationId") int applicationId) {
//        try {
//            Tblscreeningscore score = ejb.getScreeningScore(applicationId);
//
//            if (score == null) {
//                return Response.status(404).entity("Score not found").build();
//            }
//
//            return Response.ok(score).build();
//
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }

    // ================= INTERVIEW =================
    @GET
    @Path("getCandidateInterviews")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCandidateInterviews(@QueryParam("applicationId") int applicationId) {
        try {
            return Response.ok(ejb.getCandidateInterviews(applicationId)).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    // ================= NOTIFICATION =================

    @GET
    @Path("getCandidateNotifications")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCandidateNotifications(@QueryParam("userId") int userId) {
        try {
            return Response.ok(ejb.getCandidateNotifications(userId)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("getUnreadNotifications")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUnreadNotifications(@QueryParam("userId") int userId) {
        try {
            return Response.ok(ejb.getUnreadNotifications(userId)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }
    
     @GET
    @Path("/getApplicationNotifications")
    public Collection<Tblnotification> getApplicationNotifications(
            @QueryParam("userId") int userId) {

        return ejb.getApplicationNotifications(userId);
    }

    @GET
    @Path("/getInterviewNotifications")
    public Collection<Tblnotification> getInterviewNotifications(
            @QueryParam("userId") int userId) {

        return ejb.getInterviewNotifications(userId);
    }

    @GET
    @Path("/getProfileNotifications")
    public Collection<Tblnotification> getProfileNotifications(
            @QueryParam("userId") int userId) {

        return ejb.getProfileNotifications(userId);
    }

    @PUT
    @Path("markNotificationAsRead")
    @Produces(MediaType.TEXT_PLAIN)
    public Response markNotificationAsRead(@QueryParam("notificationId") int notificationId) {
        try {
            ejb.markNotificationAsRead(notificationId);
            return Response.ok("Notification marked as read").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }
    
}
