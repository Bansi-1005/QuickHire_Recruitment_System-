/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quick_hire.resources;

import EJB.CandidateBeanLocal;
import Entity.Tblapplication;
import Entity.Tblcandidates;
import Entity.Tblscreeningscore;
import Entity.Tblskills;
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
import java.util.ArrayList;
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

    // ================= RESUME =================
    @POST
    @Path("uploadResume")
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadResume(@QueryParam("candidateId") int candidateId,
                                 @QueryParam("resume") String resume) {
        try {
            if (resume == null || resume.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Resume cannot be empty").build();
            }

            ejb.uploadResume(candidateId, resume);
            return Response.ok("Resume Uploaded").build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("getCandidateResume")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCandidateResume(@QueryParam("candidateId") int candidateId) {
        try {
            String res = ejb.getCandidateResume(candidateId);

            if (res == null) {
                return Response.status(404).entity("Resume not found").build();
            }

            return Response.ok(res).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    // ================= SKILLS =================
    @POST
    @Path("addSkillToCandidate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSkillToCandidate(@QueryParam("candidateId") int candidateId,
                            @QueryParam("skillId") int skillId) {
        try {
            ejb.addSkillToCandidate(candidateId, skillId);
            return Response.ok("Skill Added").build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
    
    @PUT
    @Path("updateSkillToCandidate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSkillToCandidate(@QueryParam("candidateId") int candidateId,
                                           @QueryParam("skillId") String skillIdsStr) {
       try {
            // 🔥 Convert "3,1" → List<Integer>
            Collection<Integer> skillIds = new ArrayList<>();

            if (skillIdsStr != null && !skillIdsStr.isEmpty()) {
                String[] parts = skillIdsStr.split(",");
                for (String p : parts) {
                    skillIds.add(Integer.parseInt(p.trim()));
                }
            }

            ejb.updateSkillToCandidate(candidateId, skillIds);

            return Response.ok("Skills Updated").build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

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
    @Path("searchJobsByLocation")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchJobsByLocation(@QueryParam("jobLocation") String jobLocation) {
        try {
            return Response.ok(ejb.searchJobsByLocation(jobLocation)).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("searchJobsBySkill")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchJobsBySkill(@QueryParam("skill") String skill) {
        try {
            return Response.ok(ejb.searchJobsBySkill(skill)).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    // ================= APPLICATION =================
    @POST
    @Path("applyForJob")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response applyForJob(Tblapplication application) {
        try {
            if (application == null 
                || application.getCandidateId() == null 
                || application.getJobId() == null) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Application missing").build();
            }

            String result = ejb.applyForJob(application);

            // Handle responses properly
            if (result.equals("Already Applied")) {
                return Response.status(Response.Status.CONFLICT) // 409
                        .entity(result).build();
            }

            if (result.equals("Applied Successfully")) {
                return Response.ok(result).build();
            }

            // Other cases
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(result).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
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
    
    @GET
    @Path("getApplicationDetails")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApplicationDetails(@QueryParam("applicationId") int applicationId) {
        try {
            Tblapplication app = ejb.getApplicationDetails(applicationId);

            if (app == null) {
                return Response.status(404).entity("Application not found").build();
            }

            return Response.ok(app).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
    
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


    // ================= SCREENING =================
    @GET
    @Path("getScreeningScore")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getScreeningScore(@QueryParam("applicationId") int applicationId) {
        try {
            Tblscreeningscore score = ejb.getScreeningScore(applicationId);

            if (score == null) {
                return Response.status(404).entity("Score not found").build();
            }

            return Response.ok(score).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

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
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    

}
