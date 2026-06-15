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
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author RINKAL
 */
@Path("recruiter")
public class RecruiterResource {

    @EJB
    RecruiterBeanLocal ejb;
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
    @Produces(MediaType.APPLICATION_JSON)
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


// ================= JOB MANAGEMENT =================

    @POST
    @Path("createJob")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createJob(
            Tbljob job,
            @QueryParam("skillIds") String skillIdsStr,
            @QueryParam("educationIds") String educationIdsStr) {

        try {

            // ================= SKILLS =================
            Collection<Integer> skillIds = new ArrayList<>();

            if (skillIdsStr != null && !skillIdsStr.trim().isEmpty()) {

                String[] arr = skillIdsStr.split(",");

                for (String s : arr) {

                    if (!s.trim().isEmpty()) {

                        skillIds.add(
                                Integer.valueOf(s.trim())
                        );
                    }
                }
            }

            // ================= EDUCATION =================
            Collection<Integer> educationIds = new ArrayList<>();

            if (educationIdsStr != null
                    && !educationIdsStr.trim().isEmpty()) {

                String[] arr = educationIdsStr.split(",");

                for (String s : arr) {

                    if (!s.trim().isEmpty()) {

                        educationIds.add(
                                Integer.valueOf(s.trim())
                        );
                    }
                }
            }

            // ================= CREATE JOB =================
            ejb.createJob(
                    job,
                    skillIds,
                    educationIds
            );

            return Response.ok(
                    "Job Created Successfully"
            ).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR
            )
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("updateJob")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateJob(
            Tbljob job,
            @QueryParam("skillIds") String skillIdsStr,
            @QueryParam("educationIds") String educationIdsStr) {

        try {

            // ================= BASIC VALIDATION =================
            if (job == null || job.getJobId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Job ID is required")
                        .build();
            }

            if (job.getRecruiterId() == null
                    || job.getRecruiterId().getRecruiterId() == null) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Recruiter ID is required")
                        .build();
            }

            // ================= SKILLS =================
            Collection<Integer> skillIds = new ArrayList<>();

            if (skillIdsStr != null && !skillIdsStr.trim().isEmpty()) {

                String[] arr = skillIdsStr.split(",");

                for (String s : arr) {

                    if (s != null && !s.trim().isEmpty()) {
                        skillIds.add(Integer.valueOf(s.trim()));
                    }
                }
            }

            // ================= EDUCATION =================
            Collection<Integer> educationIds = new ArrayList<>();

            if (educationIdsStr != null && !educationIdsStr.trim().isEmpty()) {

                String[] arr = educationIdsStr.split(",");

                for (String s : arr) {

                    if (s != null && !s.trim().isEmpty()) {
                        educationIds.add(Integer.valueOf(s.trim()));
                    }
                }
            }

            // ================= UPDATE JOB =================
            ejb.updateJob(job, skillIds, educationIds);

            return Response.ok("Job Updated Successfully").build();

        } catch (NumberFormatException e) {

            e.printStackTrace();

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Skill IDs and Education IDs must be valid numbers")
                    .build();

        } catch (RuntimeException e) {

            e.printStackTrace();

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating job: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("toggleJobStatus")
    @Produces(MediaType.TEXT_PLAIN)
    public Response toggleJobStatus(
            @QueryParam("jobId") int jobId,
            @QueryParam("recruiterId") int recruiterId) {

        try {

            // ================= BASIC VALIDATION =================
            if (jobId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Job ID")
                        .build();
            }

            if (recruiterId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Recruiter ID")
                        .build();
            }

            // ================= TOGGLE STATUS =================
            ejb.toggleJobStatus(jobId, recruiterId);

            return Response.ok("Job Status Updated Successfully").build();

        } catch (RuntimeException e) {

            e.printStackTrace();

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating job status: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("updateJobExpiryDate")
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateJobExpiryDate(
            @QueryParam("jobId") int jobId,
            @QueryParam("recruiterId") int recruiterId,
            @QueryParam("expiryDate") String expiryDate) {

        try {

            if (jobId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Job ID")
                        .build();
            }

            if (recruiterId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Recruiter ID")
                        .build();
            }

            if (expiryDate == null || expiryDate.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Please select an expiry date.")
                        .build();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);

            Date parsedDate = sdf.parse(expiryDate.trim());

            ejb.updateJobExpiryDate(jobId, recruiterId, parsedDate);

            return Response.ok(
                    "Expiry date updated successfully. Job status is now Open."
            ).build();

        } catch (RuntimeException e) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Unable to update expiry date.")
                    .build();
        }
    }

//
//    @DELETE
//    @Path("deleteJob")
//    @Produces(MediaType.TEXT_PLAIN)
//    public Response deleteJob(@QueryParam("jobId") int jobId,
//            @QueryParam("recruiterId") int recruiterId) {
//        try {
//            if (jobId <= 0 || recruiterId <= 0) {
//                return Response.status(Response.Status.BAD_REQUEST)
//                        .entity("Invalid IDs")
//                        .build();
//            }
//
//            ejb.deleteJob(jobId, recruiterId);
//
//            return Response.ok("Job Deleted Successfully").build();
//
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//    //multiple,single and all job delete logic
    ////    @DELETE
////@Path("deleteJob")
////@Produces(MediaType.TEXT_PLAIN)
////public Response deleteJob(@QueryParam("jobId") Integer jobId,
////                          @QueryParam("jobIds") List<Integer> jobIds,
////                          @QueryParam("recruiterId") int recruiterId) {
////    try {
////
////        if (recruiterId <= 0) {
////            return Response.status(Response.Status.BAD_REQUEST)
////                    .entity("Recruiter ID required")
////                    .build();
////        }
////
////        ejb.deleteJob(jobId, jobIds, recruiterId);
////
////        // Response messages
////        if (jobId != null && jobId == 0) {
////            return Response.ok("All Jobs Deleted Successfully").build();
////        } else if (jobIds != null && !jobIds.isEmpty()) {
////            return Response.ok("Multiple Jobs Deleted Successfully").build();
////        } else if (jobId != null && jobId > 0) {
////            return Response.ok("Single Job Deleted Successfully").build();
////        } else {
////            return Response.status(Response.Status.BAD_REQUEST)
////                    .entity("Provide jobId or jobIds")
////                    .build();
////        }
////
////    } catch (Exception e) {
////        return Response.status(500).entity(e.getMessage()).build();
////    }
////
    /// @param userId}
//    @PUT
//    @Path("updateJobStatus")
//    @Produces(MediaType.TEXT_PLAIN)
//    public Response updateJobStatus(@QueryParam("jobId") int jobId,
//            @QueryParam("status") String status) {
//        try {
//            if (jobId <= 0 || status == null || status.isEmpty()) {
//                return Response.status(Response.Status.BAD_REQUEST)
//                        .entity("JobId or Status missing")
//                        .build();
//            }
//
//            ejb.updateJobStatus(jobId, status);
//
//            return Response.ok("Status Updated Successfully").build();
//
//        } catch (Exception e) {
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
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

            if (jobs == null) {
                jobs = new ArrayList<>();
            }

            return Response.ok(jobs).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    // ================= JOB SKILLS =================
    @GET
    @Path("getAllSkills")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSkills(
            @QueryParam("userId") Integer userId) {

        try {

            return Response.ok(
                    ejb.getAllSkills(userId)
            ).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error fetching skills")
                    .build();
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

            if (skills == null) {
                skills = new ArrayList<>();
            }

            return Response.ok(skills).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("getSkillCategories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSkillCategories(
            @QueryParam("userId") Integer userId) {

        try {

            Collection<Tblskillcategory> categories
                    = ejb.getSkillCategories(userId);

            if (categories == null) {
                categories = new ArrayList<>();
            }

            return Response.ok(categories).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error fetching skill categories")
                    .build();
        }
    }

    @GET
    @Path("getSkillsByCategory")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSkillsByCategory(
            @QueryParam("categoryId") Integer categoryId,
            @QueryParam("userId") Integer userId) {

        try {

            Collection<Tblskills> skills
                    = ejb.getSkillsByCategory(categoryId, userId);

            if (skills == null) {
                skills = new ArrayList<>();
            }

            return Response.ok(skills).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error fetching skills")
                    .build();
        }
    }

    @POST
    @Path("addSkillAndOrCategory")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addSkillAndOrCategory(
            @QueryParam("categoryName") String categoryName,
            @QueryParam("skillNames") String skillNamesStr,
            @QueryParam("existingCategoryId") Integer existingCategoryId,
            @QueryParam("userId") Integer recruiterUserId
    ) {

        try {

            // =====================================================
            // USER VALIDATION
            // =====================================================
            if (recruiterUserId == null
                    || recruiterUserId <= 0) {

                return Response.status(
                        Response.Status.BAD_REQUEST
                )
                        .entity("Invalid recruiter user")
                        .build();
            }

            // =====================================================
            // CONVERT SKILL STRING TO LIST
            // =====================================================
            Collection<String> skillNames
                    = new ArrayList<>();

            if (skillNamesStr != null
                    && !skillNamesStr.trim().isEmpty()) {

                String[] arr = skillNamesStr.split(",");

                for (String s : arr) {

                    if (s != null
                            && !s.trim().isEmpty()) {

                        skillNames.add(s.trim());
                    }
                }
            }

            // =====================================================
            // CALL EJB
            // =====================================================
            ejb.addSkillAndOrCategory(
                    categoryName,
                    skillNames,
                    existingCategoryId,
                    recruiterUserId
            );

            // =====================================================
            // SUCCESS MESSAGE
            // =====================================================
            String message;

            boolean hasCategory
                    = categoryName != null
                    && !categoryName.trim().isEmpty();

            boolean hasSkills
                    = !skillNames.isEmpty();

            if (hasCategory && !hasSkills) {

                message
                        = "Category submitted for approval";
            } else if (hasSkills) {

                message
                        = "Skill submitted for approval";
            } else {

                message
                        = "Submitted successfully";
            }

            return Response.ok(message).build();

        } catch (RuntimeException e) {

            e.printStackTrace();

            return Response.status(
                    Response.Status.BAD_REQUEST
            )
                    .entity(e.getMessage())
                    .build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR
            )
                    .entity("Error while adding skill/category")
                    .build();
        }
    }

    @GET
    @Path("jobApplicationCount")
    @Produces(MediaType.TEXT_PLAIN)
    public Response jobApplicationCount(
            @QueryParam("jobId") Integer jobId,
            @QueryParam("status") String status) {

        try {
            if (jobId == null || jobId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Job ID")
                        .build();
            }

            long count = ejb.getJobApplicationCount(jobId, status);
            return Response.ok(String.valueOf(count)).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    //===========================================Job Education ==========================================
    @GET
    @Path("getAllEducation")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEducation() {

        try {

            Collection<Tbleducation> educationList
                    = ejb.getAllEducation();

            if (educationList == null) {
                educationList = new ArrayList<>();
            }

            return Response.ok(educationList).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR
            )
                    .entity("Error fetching education list")
                    .build();
        }
    }

    @GET
    @Path("getJobEducation")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJobEducation(@QueryParam("jobId") int jobId) {

        try {

            if (jobId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Job ID")
                        .build();
            }

            Collection<Tbleducation> educations
                    = ejb.getJobEducation(jobId);

            if (educations == null) {
                educations = new ArrayList<>();
            }

            return Response.ok(educations).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    // ================= CANDIDATE MANAGEMENT =================
    @GET
    @Path("candidateSkillsText")
    @Produces(MediaType.TEXT_PLAIN)
    public Response candidateSkillsText(@QueryParam("applicationId") Integer applicationId) {
        if (applicationId == null || applicationId <= 0) {
            return Response.ok("Skills not provided").build();
        }

        return Response.ok(
                ejb.getCandidateSkillsTextByApplication(applicationId)
        ).build();
    }

    @GET
    @Path("candidateEducationText")
    @Produces(MediaType.TEXT_PLAIN)
    public Response candidateEducationText(@QueryParam("applicationId") Integer applicationId) {
        if (applicationId == null || applicationId <= 0) {
            return Response.ok("Education not provided").build();
        }

        return Response.ok(
                ejb.getCandidateEducationTextByApplication(applicationId)
        ).build();
    }

    @GET
    @Path("getRecruiterApplications")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecruiterApplications(
            @QueryParam("recruiterId") int recruiterId) {

        try {

            // ================= VALIDATION =================
            if (recruiterId <= 0) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Recruiter ID")
                        .build();
            }

            // ================= FETCH APPLICATIONS =================
            Collection<Tblapplication> applications
                    = ejb.getRecruiterApplications(recruiterId);

            // ================= NULL SAFETY =================
            if (applications == null) {

                applications = new ArrayList<>();
            }

            return Response.ok(applications).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR
            )
                    .entity(e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("generateScreeningScore")
    @Produces(MediaType.TEXT_PLAIN)
    public Response generateScreeningScore(
            @QueryParam("applicationId") int applicationId) {

        try {

            if (applicationId <= 0) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Application ID")
                        .build();
            }

            double score = ejb.calculateAndSaveScreeningScore(applicationId);

            return Response.ok(
                    "Screening Score Generated : " + score
            ).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR
            )
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("shortlistApplication")
    @Produces(MediaType.TEXT_PLAIN)
    public Response shortlistApplication(
            @QueryParam("applicationId") int applicationId) {

        try {

            if (applicationId <= 0) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Application ID")
                        .build();
            }

            ejb.shortlistApplication(applicationId);

            return Response.ok(
                    "Applicant Shortlisted Successfully"
            ).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR
            )
                    .entity(e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("scheduleInterview")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response scheduleInterview(Tblinterview interview) {

        try {

            // ================= VALIDATION =================
            if (interview == null) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Interview data is required")
                        .build();
            }

            if (interview.getApplicationId() == null
                    || interview.getApplicationId().getApplicationId() == null) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Application ID is required")
                        .build();
            }

            if (interview.getInterviewDate() == null) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Interview date is required")
                        .build();
            }

            if (interview.getInterviewerName() == null
                    || interview.getInterviewerName().trim().isEmpty()) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Interviewer name is required")
                        .build();
            }

            if (interview.getInterviewerMode() == null
                    || interview.getInterviewerMode().trim().isEmpty()) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Interview mode is required")
                        .build();
            }

            // ================= VALID MODES =================
            String mode = interview.getInterviewerMode().trim();

            if (!mode.equalsIgnoreCase("Online")
                    && !mode.equalsIgnoreCase("Offline")
                    && !mode.equalsIgnoreCase("Phone")) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Interview mode must be Online, Offline, or Phone")
                        .build();
            }

            // ================= CALL EJB =================
            ejb.scheduleInterview(interview);

            // ================= SUCCESS =================
            return Response.ok(
                    "Interview Scheduled Successfully"
            ).build();

        } catch (RuntimeException e) {

            e.printStackTrace();

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error scheduling interview")
                    .build();
        }
    }

    @GET
    @Path("interviewHistoryByApplication")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInterviewHistoryByApplication(
            @QueryParam("applicationId") Integer applicationId) {

        try {
            if (applicationId == null || applicationId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Application ID")
                        .build();
            }

            Collection<Tblinterview> history
                    = ejb.getInterviewHistoryByApplication(applicationId);

            if (history == null) {
                history = new ArrayList<>();
            }

            return Response.ok(history).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("getAllScreeningScores")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllScreeningScores(
            @QueryParam("recruiterId") int recruiterId) {
        try {
            if (recruiterId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Recruiter ID").build();
            }
            return Response.ok(ejb.getAllScreeningScores(recruiterId)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    // ================= REJECT APPLICATION =================
    @PUT
    @Path("rejectApplication")
    @Produces(MediaType.TEXT_PLAIN)
    public Response rejectApplication(
            @QueryParam("applicationId") int applicationId) {
        try {
            if (applicationId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Application ID")
                        .build();
            }
            ejb.rejectApplication(applicationId);
            return Response.ok("Application Rejected Successfully").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build();
        }
    }

// ================= CANCEL INTERVIEW =================
    @PUT
    @Path("cancelInterview")
    @Produces(MediaType.TEXT_PLAIN)
    public Response cancelInterview(
            @QueryParam("interviewId") Integer interviewId) {
        try {
            if (interviewId == null || interviewId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Interview ID")
                        .build();
            }
            ejb.cancelInterview(interviewId);
            return Response.ok("Interview Cancelled Successfully").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build();
        }
    }

// ================= REJECTED APPLICATION COUNT =================
    @GET
    @Path("rejectedApplicationCount")
    @Produces(MediaType.TEXT_PLAIN)
    public Response rejectedApplicationCount(
            @QueryParam("recruiterId") int recruiterId) {
        try {
            if (recruiterId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Recruiter ID")
                        .build();
            }
            long count = ejb.getRejectedApplicationCount(recruiterId);
            return Response.ok(String.valueOf(count)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    // ================= INTERVIEW MANAGEMENT =================
    @GET
    @Path("getRecruiterInterviews")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecruiterInterviews(
            @QueryParam("recruiterId") Integer recruiterId) {

        try {

            if (recruiterId == null || recruiterId <= 0) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Recruiter ID")
                        .build();
            }

            Collection<Tblinterview> interviews
                    = ejb.getRecruiterInterviews(recruiterId);

            if (interviews == null) {
                interviews = new ArrayList<>();
            }

            return Response.ok(interviews).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("scheduledInterviewCount")
    @Produces(MediaType.TEXT_PLAIN)
    public Response scheduledInterviewCount(
            @QueryParam("recruiterId") Integer recruiterId) {

        try {

            if (recruiterId == null || recruiterId <= 0) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Recruiter ID")
                        .build();
            }

            Long count
                    = ejb.getScheduledInterviewCount(recruiterId);

            return Response.ok(String.valueOf(count)).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(500)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("completedInterviewCount")
    @Produces(MediaType.TEXT_PLAIN)
    public Response completedInterviewCount(
            @QueryParam("recruiterId") Integer recruiterId) {

        try {

            if (recruiterId == null || recruiterId <= 0) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Recruiter ID")
                        .build();
            }

            Long count
                    = ejb.getCompletedInterviewCount(recruiterId);

            return Response.ok(String.valueOf(count)).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(500)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("selectedCount")
    @Produces(MediaType.TEXT_PLAIN)
    public Response selectedCount(
            @QueryParam("recruiterId") Integer recruiterId) {

        try {

            if (recruiterId == null || recruiterId <= 0) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Recruiter ID")
                        .build();
            }

            Long count
                    = ejb.getSelectedCount(recruiterId);

            return Response.ok(String.valueOf(count)).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(500)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("rejectedCount")
    @Produces(MediaType.TEXT_PLAIN)
    public Response rejectedCount(
            @QueryParam("recruiterId") Integer recruiterId) {

        try {

            if (recruiterId == null || recruiterId <= 0) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Recruiter ID")
                        .build();
            }

            Long count
                    = ejb.getRejectedCount(recruiterId);

            return Response.ok(String.valueOf(count)).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(500)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("totalInterviewCount")
    @Produces(MediaType.TEXT_PLAIN)
    public Response totalInterviewCount(
            @QueryParam("recruiterId") Integer recruiterId) {

        try {

            if (recruiterId == null || recruiterId <= 0) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Recruiter ID")
                        .build();
            }

            Long count
                    = ejb.getTotalInterviewCount(recruiterId);

            return Response.ok(String.valueOf(count)).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(500)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("conductInterview")
    @Produces(MediaType.TEXT_PLAIN)
    public Response conductInterview(
            @QueryParam("interviewId") Integer interviewId,
            @QueryParam("feedback") String feedback,
            @QueryParam("result") String result) {

        try {

            if (interviewId == null || interviewId <= 0) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Interview ID")
                        .build();
            }

            if (result == null || result.trim().isEmpty()) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Result is required")
                        .build();
            }

            ejb.conductInterview(
                    interviewId,
                    feedback,
                    result
            );

            return Response.ok(
                    "Interview completed successfully"
            ).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(500)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("rescheduleInterview")
    @Produces(MediaType.TEXT_PLAIN)
    public Response rescheduleInterview(
            @QueryParam("interviewId") Integer interviewId,
            @QueryParam("interviewerName") String interviewerName,
            @QueryParam("interviewerMode") String interviewerMode,
            @QueryParam("interviewDate") String interviewDateStr) {

        try {

            if (interviewId == null || interviewId <= 0) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Interview ID")
                        .build();
            }

            SimpleDateFormat sdf
                    = new SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm"
                    );

            Date interviewDate
                    = sdf.parse(interviewDateStr);

            ejb.rescheduleInterview(
                    interviewId,
                    interviewDate,
                    interviewerName,
                    interviewerMode
            );

            return Response.ok(
                    "Interview rescheduled successfully"
            ).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(500)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("todayInterviews")
    @Produces(MediaType.TEXT_PLAIN)
    public Response todayInterviews(@QueryParam("recruiterId") int recruiterId) {

        try {

            long count = ejb.getTodayInterviewsCount(recruiterId);

            return Response.ok(String.valueOf(count)).build();

        } catch (Exception e) {

            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("newApplicants")
    @Produces(MediaType.TEXT_PLAIN)
    public Response newApplicants(@QueryParam("recruiterId") int recruiterId) {

        try {

            long count = ejb.getNewApplicantsCount(recruiterId);

            return Response.ok(String.valueOf(count)).build();

        } catch (Exception e) {

            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("shortlisted")
    @Produces(MediaType.TEXT_PLAIN)
    public Response shortlisted(@QueryParam("recruiterId") int recruiterId) {

        try {

            long count = ejb.getShortlistedCount(recruiterId);

            return Response.ok(String.valueOf(count)).build();

        } catch (Exception e) {

            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("hiringRate")
    @Produces(MediaType.TEXT_PLAIN)
    public Response hiringRate(@QueryParam("recruiterId") int recruiterId) {

        try {

            double rate = ejb.getHiringRate(recruiterId);

            return Response.ok(String.format("%.0f", rate)).build();

        } catch (Exception e) {

            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("activeJobs")
    @Produces(MediaType.TEXT_PLAIN)
    public Response activeJobs(@QueryParam("recruiterId") int recruiterId) {

        try {

            long count = ejb.getActiveJobsCount(recruiterId);

            return Response.ok(String.valueOf(count)).build();

        } catch (Exception e) {

            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("totalApplicants")
    @Produces(MediaType.TEXT_PLAIN)
    public Response totalApplicants(@QueryParam("recruiterId") int recruiterId) {

        try {

            long count = ejb.getTotalApplicantsCount(recruiterId);

            return Response.ok(String.valueOf(count)).build();

        } catch (Exception e) {

            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("upcomingInterviews")
    @Produces(MediaType.TEXT_PLAIN)
    public Response upcomingInterviews(@QueryParam("recruiterId") int recruiterId) {

        try {

            long count = ejb.getUpcomingInterviewsCount(recruiterId);

            return Response.ok(String.valueOf(count)).build();

        } catch (Exception e) {

            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("avgTimeToHire")
    @Produces(MediaType.TEXT_PLAIN)
    public Response avgTimeToHire(@QueryParam("recruiterId") int recruiterId) {

        try {

            double days = ejb.getAvgTimeToHire(recruiterId);

            return Response.ok(String.format("%.0f", days)).build();

        } catch (Exception e) {

            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("dashboardTopCandidates")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dashboardTopCandidates(
            @QueryParam("recruiterId") int recruiterId) {

        try {

            if (recruiterId <= 0) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Recruiter ID")
                        .build();
            }

            Collection<Tblscreeningscore> list
                    = ejb.getDashboardTopCandidates(recruiterId);

            // IMPORTANT FIX
            if (list == null) {
                list = new ArrayList<>();
            }

            return Response.ok(list).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(500)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("dashboardUpcomingInterviews")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dashboardUpcomingInterviews(
            @QueryParam("recruiterId") int recruiterId) {

        try {

            if (recruiterId <= 0) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Recruiter ID")
                        .build();
            }

            Collection<Tblinterview> list
                    = ejb.getDashboardUpcomingInterviews(recruiterId);

            // IMPORTANT FIX
            if (list == null) {
                list = new ArrayList<>();
            }

            return Response.ok(list).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(500)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("recentActivities")
    @Produces(MediaType.APPLICATION_JSON)
    public Response recentActivities(
            @QueryParam("userId") int userId) {

        try {

            if (userId <= 0) {

                return Response.status(
                        Response.Status.BAD_REQUEST)
                        .entity("Invalid User ID")
                        .build();
            }

            Collection<Tblnotification> list
                    = ejb.getRecentActivities(userId);

            // IMPORTANT FIX
            if (list == null) {
                list = new ArrayList<>();
            }

            return Response.ok(list).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(500)
                    .entity(e.getMessage())
                    .build();
        }
    }
//=========================NOTIFICATION========================

    @GET
    @Path("getNotifications")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotifications(@QueryParam("userId") int userId) {
        try {
            if (userId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid User ID")
                        .build();
            }
            Collection<Tblnotification> list = ejb.getNotifications(userId);
            return Response.ok(list != null ? list : new ArrayList<>()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("markNotificationRead")
    @Produces(MediaType.TEXT_PLAIN)
    public Response markNotificationRead(
            @QueryParam("notificationId") int notificationId,
            @QueryParam("userId") int userId) {
        try {
            if (notificationId <= 0 || userId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid notification or user.")
                        .build();
            }
            ejb.markNotificationAsRead(notificationId, userId);
            return Response.ok("Notification marked as read").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("markAllNotificationsRead")
    @Produces(MediaType.TEXT_PLAIN)
    public Response markAllNotificationsRead(@QueryParam("userId") int userId) {
        try {
            if (userId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid User ID")
                        .build();
            }
            ejb.markAllNotificationsAsRead(userId);
            return Response.ok("All notifications marked as read").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
}
