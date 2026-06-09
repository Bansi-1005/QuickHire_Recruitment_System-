/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/JerseyClient.java to edit this template
 */
package Client;

import Entity.Tblapplication;
import Entity.Tbleducation;
import Entity.Tblinterview;
import Entity.Tbljob;
import Entity.Tblskillcategory;
import Entity.Tblskills;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Jersey REST client generated for REST resource:RecruiterResource
 * [recruiter]<br>
 * USAGE:
 * <pre>
 *        RecruiterJerseyClient client = new RecruiterJerseyClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author RINKAL
 */
public class RecruiterJerseyClient {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8080/Quick_Hire/resources";

    public RecruiterJerseyClient() {
        client = jakarta.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("recruiter");
    }
    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    public <T> T topCandidates(Class<T> responseType, String jobId) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (jobId != null) {
            resource = resource.queryParam("jobId", jobId);
        }
        resource = resource.path("topCandidates");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response updateProfile(Object requestEntity) {
        return webTarget.path("updateProfile")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .put(Entity.entity(requestEntity, MediaType.APPLICATION_JSON));
    }

    public <T> T getNotifications(Class<T> responseType, String userId) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (userId != null) {
            resource = resource.queryParam("userId", userId);
        }
        resource = resource.path("getNotifications");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

//    public Response updateJob(Object requestEntity) throws ClientErrorException {
//        return webTarget.path("updateJob").request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).put(jakarta.ws.rs.client.Entity.entity(requestEntity, jakarta.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
//    }
    public <T> T filterCandidates(Class<T> responseType, String jobId, String score) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (jobId != null) {
            resource = resource.queryParam("jobId", jobId);
        }
        if (score != null) {
            resource = resource.queryParam("score", score);
        }
        resource = resource.path("filterCandidates");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getProfile(Class<T> responseType, String userId) {

        WebTarget resource = webTarget
                .path("getProfile")
                .queryParam("userId", Integer.parseInt(userId)); // ✅ FIX

        return resource.request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .get(responseType);
    }

    public Response createJob(
            Tbljob requestEntity,
            Collection<Integer> skillIds,
            Collection<Integer> educationIds
    )
            throws ClientErrorException {

        String skillParam = "";

        if (skillIds != null
                && !skillIds.isEmpty()) {

            StringBuilder sb
                    = new StringBuilder();

            for (Integer id : skillIds) {

                if (sb.length() > 0) {
                    sb.append(",");
                }

                sb.append(id);
            }

            skillParam = sb.toString();
        }

        String educationParam = "";

        if (educationIds != null
                && !educationIds.isEmpty()) {

            StringBuilder sb
                    = new StringBuilder();

            for (Integer id : educationIds) {

                if (sb.length() > 0) {
                    sb.append(",");
                }

                sb.append(id);
            }

            educationParam = sb.toString();
        }

        return webTarget
                .path("createJob")
                .queryParam(
                        "skillIds",
                        skillParam
                )
                .queryParam(
                        "educationIds",
                        educationParam
                )
                .request(MediaType.TEXT_PLAIN)
                .header(
                        "Authorization",
                        "Bearer " + token
                )
                .post(
                        Entity.entity(
                                requestEntity,
                                MediaType.APPLICATION_JSON
                        ),
                        Response.class
                );
    }

    public Response updateJob(
            Tbljob requestEntity,
            Collection<Integer> skillIds,
            Collection<Integer> educationIds
    ) throws ClientErrorException {

        String skillParam = "";

        if (skillIds != null && !skillIds.isEmpty()) {

            StringBuilder sb = new StringBuilder();

            for (Integer id : skillIds) {

                if (sb.length() > 0) {
                    sb.append(",");
                }

                sb.append(id);
            }

            skillParam = sb.toString();
        }

        String educationParam = "";

        if (educationIds != null && !educationIds.isEmpty()) {

            StringBuilder sb = new StringBuilder();

            for (Integer id : educationIds) {

                if (sb.length() > 0) {
                    sb.append(",");
                }

                sb.append(id);
            }

            educationParam = sb.toString();
        }

        return webTarget
                .path("updateJob")
                .queryParam(
                        "skillIds",
                        skillParam
                )
                .queryParam(
                        "educationIds",
                        educationParam
                )
                .request(MediaType.TEXT_PLAIN)
                .header(
                        "Authorization",
                        "Bearer " + token
                )
                .put(
                        Entity.entity(
                                requestEntity,
                                MediaType.APPLICATION_JSON
                        ),
                        Response.class
                );
    }

    public Response toggleJobStatus(
            Integer jobId,
            Integer recruiterId
    ) throws ClientErrorException {

        WebTarget resource = webTarget
                .path("toggleJobStatus");

        if (jobId != null) {
            resource = resource.queryParam(
                    "jobId",
                    jobId
            );
        }

        if (recruiterId != null) {
            resource = resource.queryParam(
                    "recruiterId",
                    recruiterId
            );
        }

        return resource
                .request(MediaType.TEXT_PLAIN)
                .header(
                        "Authorization",
                        "Bearer " + token
                )
                .put(
                        Entity.text("")
                );
    }

    public Response updateJobExpiryDate(
            Integer jobId,
            Integer recruiterId,
            String expiryDate) {

        WebTarget resource = webTarget
                .path("updateJobExpiryDate")
                .queryParam("jobId", jobId)
                .queryParam("recruiterId", recruiterId)
                .queryParam("expiryDate", expiryDate);

        return resource
                .request(MediaType.TEXT_PLAIN)
                .header("Authorization", "Bearer " + token)
                .put(Entity.text(""));
    }

//    public Response sendNotification(Object requestEntity) throws ClientErrorException {
//        return webTarget.path("sendNotification").request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).post(jakarta.ws.rs.client.Entity.entity(requestEntity, jakarta.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
//    }
//
//    public Response deleteJob() throws ClientErrorException {
//        return webTarget.path("deleteJob").request().delete(Response.class);
//    }
//    public <T> T getJobs(Class<T> responseType, String recruiterId) throws ClientErrorException {
//        WebTarget resource = webTarget;
//        if (recruiterId != null) {
//            resource = resource.queryParam("recruiterId", recruiterId);
//        }
//        resource = resource.path("getJobs");
//        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
//    }
    public <T> T getJobs(Class<T> responseType, String recruiterId) {

        WebTarget resource = webTarget.path("getJobs");

        if (recruiterId != null && !recruiterId.isEmpty()) {
            resource = resource.queryParam("recruiterId", recruiterId);
        }

        return resource.request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .get(responseType);
    }

    public <T> T getJobs(GenericType<T> responseType,
            String recruiterId) {

        WebTarget resource = webTarget.path("getJobs");

        if (recruiterId != null
                && !recruiterId.isEmpty()) {

            resource = resource.queryParam(
                    "recruiterId",
                    recruiterId
            );
        }

        return resource.request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .get(responseType);
    }

//    public Response updateJobStatus() throws ClientErrorException {
//        return webTarget.path("updateJobStatus").request().put(null, Response.class);
//    }
//
//    public Response updateApplicationStatus() throws ClientErrorException {
//        return webTarget.path("updateApplicationStatus").request().put(null, Response.class);
//    }
//    public Response updateInterview(Object requestEntity) throws ClientErrorException {
//        return webTarget.path("updateInterview").request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).put(jakarta.ws.rs.client.Entity.entity(requestEntity, jakarta.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
//    }
//
//    public <T> T getCompany(Class<T> responseType, String recruiterId) throws ClientErrorException {
//        WebTarget resource = webTarget;
//        if (recruiterId != null) {
//            resource = resource.queryParam("recruiterId", recruiterId);
//        }
//        resource = resource.path("getCompany");
//        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
//    }
//
//    public Response addApplicationHistory() throws ClientErrorException {
//        return webTarget.path("addApplicationHistory").request().post(null, Response.class);
//    }
//
//    public Response scheduleInterview(Object requestEntity) throws ClientErrorException {
//        return webTarget.path("scheduleInterview").request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).post(jakarta.ws.rs.client.Entity.entity(requestEntity, jakarta.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
//    }
//    public <T> T getJobSkills(Class<T> responseType, String jobId) throws ClientErrorException {
//        WebTarget resource = webTarget;
//        if (jobId != null) {
//            resource = resource.queryParam("jobId", jobId);
//        }
//        resource = resource.path("getJobSkills");
//        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
//    }
    public Collection<Tblskills> getJobSkills(String jobId)
            throws ClientErrorException {

        WebTarget resource = webTarget
                .path("getJobSkills");

        if (jobId != null && !jobId.isEmpty()) {
            resource = resource.queryParam("jobId", jobId);
        }

        return resource
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .get(new GenericType<Collection<Tblskills>>() {
                });
    }

    public <T> T getApplications(Class<T> responseType, String jobId) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (jobId != null) {
            resource = resource.queryParam("jobId", jobId);
        }
        resource = resource.path("getApplications");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Collection<Tblskills> getAllSkills(Integer userId)
            throws ClientErrorException {

        WebTarget resource = webTarget
                .path("getAllSkills");

        if (userId != null) {

            resource = resource.queryParam(
                    "userId",
                    userId
            );
        }

        return resource
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .get(new GenericType<Collection<Tblskills>>() {
                });
    }

    public Collection<Tblskillcategory> getSkillCategories(Integer userId)
            throws ClientErrorException {

        WebTarget resource = webTarget
                .path("getSkillCategories");

        if (userId != null) {

            resource = resource.queryParam(
                    "userId",
                    userId
            );
        }

        return resource
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .get(new GenericType<Collection<Tblskillcategory>>() {
                });
    }

    public Collection<Tblskills> getSkillsByCategory(
            Integer categoryId,
            Integer userId)
            throws ClientErrorException {

        WebTarget resource = webTarget
                .path("getSkillsByCategory");

        if (categoryId != null) {

            resource = resource.queryParam(
                    "categoryId",
                    categoryId
            );
        }

        if (userId != null) {

            resource = resource.queryParam(
                    "userId",
                    userId
            );
        }

        return resource
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .get(new GenericType<Collection<Tblskills>>() {
                });
    }

    public Response addSkillAndOrCategory(
            String categoryName,
            String skillNames,
            Integer existingCategoryId,
            Integer userId
    ) {

        WebTarget resource = webTarget
                .path("addSkillAndOrCategory");

        // CATEGORY NAME
        if (categoryName != null
                && !categoryName.trim().isEmpty()) {

            resource = resource.queryParam(
                    "categoryName",
                    categoryName.trim()
            );
        }

        // SKILL NAMES
        if (skillNames != null
                && !skillNames.trim().isEmpty()) {

            resource = resource.queryParam(
                    "skillNames",
                    skillNames.trim()
            );
        }

        // EXISTING CATEGORY
        if (existingCategoryId != null
                && existingCategoryId > 0) {

            resource = resource.queryParam(
                    "existingCategoryId",
                    existingCategoryId
            );
        }

        // USER ID
        if (userId != null
                && userId > 0) {

            resource = resource.queryParam(
                    "userId",
                    userId
            );
        }

        return resource
                .request(MediaType.TEXT_PLAIN)
                .header(
                        "Authorization",
                        "Bearer " + token
                )
                .post(null);
    }

//==============Job Education ====================
    public Collection<Tbleducation> getAllEducation()
            throws ClientErrorException {

        return webTarget
                .path("getAllEducation")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .get(new GenericType<Collection<Tbleducation>>() {
                });
    }

    public Collection<Tbleducation> getJobEducation(String jobId)
            throws ClientErrorException {

        WebTarget resource = webTarget
                .path("getJobEducation");

        if (jobId != null && !jobId.isEmpty()) {
            resource = resource.queryParam("jobId", jobId);
        }

        return resource
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .get(new GenericType<Collection<Tbleducation>>() {
                });
    }

    //==============CANDIDATE MANAGEMENT ====================
    public Collection<Tblapplication> getRecruiterApplications(
            Integer recruiterId)
            throws ClientErrorException {

        WebTarget resource = webTarget
                .path("getRecruiterApplications");

        // ================= QUERY PARAM =================
        if (recruiterId != null && recruiterId > 0) {

            resource = resource.queryParam(
                    "recruiterId",
                    recruiterId
            );
        }

        // ================= API CALL =================
        return resource
                .request(MediaType.APPLICATION_JSON)
                .header(
                        "Authorization",
                        "Bearer " + token
                )
                .get(new GenericType<Collection<Tblapplication>>() {
                });
    }

    public Response generateScreeningScore(
            Integer applicationId)
            throws ClientErrorException {

        WebTarget resource = webTarget
                .path("generateScreeningScore");

        if (applicationId != null
                && applicationId > 0) {

            resource = resource.queryParam(
                    "applicationId",
                    applicationId
            );
        }

        return resource
                .request(MediaType.TEXT_PLAIN)
                .header(
                        "Authorization",
                        "Bearer " + token
                )
                .post(
                        Entity.text("")
                );
    }

    public Map<Integer, BigDecimal> getAllScreeningScores(Integer recruiterId) {
        try {
            return webTarget
                    .path("getAllScreeningScores")
                    .queryParam("recruiterId", recruiterId)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .get(new GenericType<Map<Integer, BigDecimal>>() {
                    });
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public Response shortlistApplication(
            Integer applicationId)
            throws ClientErrorException {

        WebTarget resource = webTarget
                .path("shortlistApplication");

        if (applicationId != null
                && applicationId > 0) {

            resource = resource.queryParam(
                    "applicationId",
                    applicationId
            );
        }

        return resource
                .request(MediaType.TEXT_PLAIN)
                .header(
                        "Authorization",
                        "Bearer " + token
                )
                .put(
                        Entity.text("")
                );
    }
//
//    public Response selectApplication(
//            Integer applicationId)
//            throws ClientErrorException {
//
//        WebTarget resource = webTarget
//                .path("selectApplication");
//
//        if (applicationId != null
//                && applicationId > 0) {
//
//            resource = resource.queryParam(
//                    "applicationId",
//                    applicationId
//            );
//        }
//
//        return resource
//                .request(MediaType.TEXT_PLAIN)
//                .header(
//                        "Authorization",
//                        "Bearer " + token
//                )
//                .put(
//                        Entity.text("")
//                );
//    }

    public Response scheduleInterview(
            Tblinterview interview)
            throws ClientErrorException {

        // ================= VALIDATION =================
        if (interview == null) {

            throw new ClientErrorException(
                    "Interview data is required",
                    Response.Status.BAD_REQUEST
            );
        }

        if (interview.getApplicationId() == null
                || interview.getApplicationId().getApplicationId() == null) {

            throw new ClientErrorException(
                    "Application ID is required",
                    Response.Status.BAD_REQUEST
            );
        }

        if (interview.getInterviewDate() == null) {

            throw new ClientErrorException(
                    "Interview date is required",
                    Response.Status.BAD_REQUEST
            );
        }

        if (interview.getInterviewerName() == null
                || interview.getInterviewerName().trim().isEmpty()) {

            throw new ClientErrorException(
                    "Interviewer name is required",
                    Response.Status.BAD_REQUEST
            );
        }

        if (interview.getInterviewerMode() == null
                || interview.getInterviewerMode().trim().isEmpty()) {

            throw new ClientErrorException(
                    "Interview mode is required",
                    Response.Status.BAD_REQUEST
            );
        }

        // ================= VALIDATE MODE =================
        String mode = interview.getInterviewerMode().trim();

        if (!mode.equalsIgnoreCase("Online")
                && !mode.equalsIgnoreCase("Offline")
                && !mode.equalsIgnoreCase("Phone")) {

            throw new ClientErrorException(
                    "Interview mode must be Online, Offline, or Phone",
                    Response.Status.BAD_REQUEST
            );
        }

        // ================= API CALL =================
        return webTarget
                .path("scheduleInterview")
                .request(MediaType.TEXT_PLAIN)
                .header(
                        "Authorization",
                        "Bearer " + token
                )
                .post(
                        Entity.entity(
                                interview,
                                MediaType.APPLICATION_JSON
                        ),
                        Response.class
                );
    }

    // ================= INTERVIEW MANAGEMENT =================
// Get all recruiter interviews
    public Collection<Tblinterview> getRecruiterInterviews(
            Integer recruiterId)
            throws ClientErrorException {

        WebTarget resource = webTarget
                .path("getRecruiterInterviews");

        if (recruiterId != null && recruiterId > 0) {
            resource = resource.queryParam(
                    "recruiterId",
                    recruiterId
            );
        }

        return resource
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization",
                        "Bearer " + token)
                .get(new GenericType<Collection<Tblinterview>>() {
                });
    }

// Scheduled Interview Count
    public String getScheduledInterviewCount(
            Integer recruiterId)
            throws ClientErrorException {

        WebTarget resource = webTarget
                .path("scheduledInterviewCount");

        if (recruiterId != null && recruiterId > 0) {
            resource = resource.queryParam(
                    "recruiterId",
                    recruiterId
            );
        }

        return resource
                .request(MediaType.TEXT_PLAIN)
                .header("Authorization",
                        "Bearer " + token)
                .get(String.class);
    }

// Completed Interview Count
    public String getCompletedInterviewCount(
            Integer recruiterId)
            throws ClientErrorException {

        WebTarget resource = webTarget
                .path("completedInterviewCount");

        if (recruiterId != null && recruiterId > 0) {
            resource = resource.queryParam(
                    "recruiterId",
                    recruiterId
            );
        }

        return resource
                .request(MediaType.TEXT_PLAIN)
                .header("Authorization",
                        "Bearer " + token)
                .get(String.class);
    }

// Selected Count
    public String getSelectedCount(
            Integer recruiterId)
            throws ClientErrorException {

        WebTarget resource = webTarget
                .path("selectedCount");

        if (recruiterId != null && recruiterId > 0) {
            resource = resource.queryParam(
                    "recruiterId",
                    recruiterId
            );
        }

        return resource
                .request(MediaType.TEXT_PLAIN)
                .header("Authorization",
                        "Bearer " + token)
                .get(String.class);
    }

// Rejected Count
    public String getRejectedCount(
            Integer recruiterId)
            throws ClientErrorException {

        WebTarget resource = webTarget
                .path("rejectedCount");

        if (recruiterId != null && recruiterId > 0) {
            resource = resource.queryParam(
                    "recruiterId",
                    recruiterId
            );
        }

        return resource
                .request(MediaType.TEXT_PLAIN)
                .header("Authorization",
                        "Bearer " + token)
                .get(String.class);
    }

// Total Interview Count
    public String getTotalInterviewCount(
            Integer recruiterId)
            throws ClientErrorException {

        WebTarget resource = webTarget
                .path("totalInterviewCount");

        if (recruiterId != null && recruiterId > 0) {
            resource = resource.queryParam(
                    "recruiterId",
                    recruiterId
            );
        }

        return resource
                .request(MediaType.TEXT_PLAIN)
                .header("Authorization",
                        "Bearer " + token)
                .get(String.class);
    }

// Conduct Interview
    public Response conductInterview(
            Integer interviewId,
            String feedback,
            String result)
            throws ClientErrorException {

        WebTarget resource = webTarget
                .path("conductInterview");

        if (interviewId != null) {
            resource = resource.queryParam(
                    "interviewId",
                    interviewId
            );
        }

        if (feedback != null) {
            resource = resource.queryParam(
                    "feedback",
                    feedback
            );
        }

        if (result != null) {
            resource = resource.queryParam(
                    "result",
                    result
            );
        }

        return resource
                .request(MediaType.TEXT_PLAIN)
                .header("Authorization",
                        "Bearer " + token)
                .put(
                        Entity.text("")
                );
    }

// Reschedule Interview
    public Response rescheduleInterview(
            Integer interviewId,
            String interviewerName,
            String interviewerMode,
            String interviewDate)
            throws ClientErrorException {

        WebTarget resource = webTarget
                .path("rescheduleInterview");

        if (interviewId != null) {
            resource = resource.queryParam(
                    "interviewId",
                    interviewId
            );
        }

        if (interviewerName != null) {
            resource = resource.queryParam(
                    "interviewerName",
                    interviewerName
            );
        }

        if (interviewerMode != null) {
            resource = resource.queryParam(
                    "interviewerMode",
                    interviewerMode
            );
        }

        if (interviewDate != null) {
            resource = resource.queryParam(
                    "interviewDate",
                    interviewDate
            );
        }

        return resource
                .request(MediaType.TEXT_PLAIN)
                .header("Authorization",
                        "Bearer " + token)
                .put(
                        Entity.text("")
                );
    }

    public String getTodayInterviews(String recruiterId) {

        return webTarget.path("todayInterviews")
                .queryParam("recruiterId", recruiterId)
                .request(MediaType.TEXT_PLAIN)
                .header("Authorization", "Bearer " + token)
                .get(String.class);
    }

    public String getNewApplicants(String recruiterId) {

        return webTarget.path("newApplicants")
                .queryParam("recruiterId", recruiterId)
                .request(MediaType.TEXT_PLAIN)
                .header("Authorization", "Bearer " + token)
                .get(String.class);
    }

    public String getShortlisted(String recruiterId) {

        return webTarget.path("shortlisted")
                .queryParam("recruiterId", recruiterId)
                .request(MediaType.TEXT_PLAIN)
                .header("Authorization", "Bearer " + token)
                .get(String.class);
    }

    public String getHiringRate(String recruiterId) {

        return webTarget.path("hiringRate")
                .queryParam("recruiterId", recruiterId)
                .request(MediaType.TEXT_PLAIN)
                .header("Authorization", "Bearer " + token)
                .get(String.class);
    }

    public String getActiveJobs(String recruiterId) {

        return webTarget.path("activeJobs")
                .queryParam("recruiterId", recruiterId)
                .request(MediaType.TEXT_PLAIN)
                .header("Authorization", "Bearer " + token)
                .get(String.class);
    }

    public String getTotalApplicants(String recruiterId) {

        return webTarget.path("totalApplicants")
                .queryParam("recruiterId", recruiterId)
                .request(MediaType.TEXT_PLAIN)
                .header("Authorization", "Bearer " + token)
                .get(String.class);
    }

    public String getUpcomingInterviews(String recruiterId) {

        return webTarget.path("upcomingInterviews")
                .queryParam("recruiterId", recruiterId)
                .request(MediaType.TEXT_PLAIN)
                .header("Authorization", "Bearer " + token)
                .get(String.class);
    }

    public String getAvgTimeToHire(String recruiterId) {

        return webTarget.path("avgTimeToHire")
                .queryParam("recruiterId", recruiterId)
                .request(MediaType.TEXT_PLAIN)
                .header("Authorization", "Bearer " + token)
                .get(String.class);
    }

    public <T> T getDashboardTopCandidates(
            Class<T> responseType,
            String recruiterId) {

        try {

            return webTarget
                    .path("dashboardTopCandidates")
                    .queryParam("recruiterId", recruiterId)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization",
                            "Bearer " + token)
                    .get(responseType);

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

    public <T> T getDashboardUpcomingInterviews(
            Class<T> responseType,
            String recruiterId) {

        try {

            return webTarget
                    .path("dashboardUpcomingInterviews")
                    .queryParam("recruiterId", recruiterId)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization",
                            "Bearer " + token)
                    .get(responseType);

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

    public <T> T getRecentActivities(
            Class<T> responseType,
            String userId) {

        try {

            return webTarget
                    .path("recentActivities")
                    .queryParam("userId", userId)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization",
                            "Bearer " + token)
                    .get(responseType);

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

    public void close() {
        client.close();
    }

}
