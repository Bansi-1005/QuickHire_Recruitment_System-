/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/JerseyClient.java to edit this template
 */
package Client;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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

    public Response updateJob(Object requestEntity) throws ClientErrorException {
        return webTarget.path("updateJob").request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).put(jakarta.ws.rs.client.Entity.entity(requestEntity, jakarta.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
    }

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

    public Response createJob(Object requestEntity) throws ClientErrorException {
        return webTarget.path("createJob")
                .request(MediaType.TEXT_PLAIN)
                .header("Authorization", "Bearer " + token)
                .post(
                        Entity.entity(requestEntity,
                                MediaType.APPLICATION_JSON),
                        Response.class
                );
    }

    public Response sendNotification(Object requestEntity) throws ClientErrorException {
        return webTarget.path("sendNotification").request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).post(jakarta.ws.rs.client.Entity.entity(requestEntity, jakarta.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
    }

    public Response deleteJob() throws ClientErrorException {
        return webTarget.path("deleteJob").request().delete(Response.class);
    }

//    public <T> T getJobs(Class<T> responseType, String recruiterId) throws ClientErrorException {
//        WebTarget resource = webTarget;
//        if (recruiterId != null) {
//            resource = resource.queryParam("recruiterId", recruiterId);
//        }
//        resource = resource.path("getJobs");
//        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
//    }
    public <T> T getJobs(Class<T> responseType, String recruiterId) {

        WebTarget resource = webTarget;

        if (recruiterId != null) {
            resource = resource.queryParam("recruiterId", recruiterId);
        }

        resource = resource.path("getJobs");

        return resource.request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .get(responseType);
    }

    public Response updateJobStatus() throws ClientErrorException {
        return webTarget.path("updateJobStatus").request().put(null, Response.class);
    }

    public Response updateApplicationStatus() throws ClientErrorException {
        return webTarget.path("updateApplicationStatus").request().put(null, Response.class);
    }

    public Response updateInterview(Object requestEntity) throws ClientErrorException {
        return webTarget.path("updateInterview").request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).put(jakarta.ws.rs.client.Entity.entity(requestEntity, jakarta.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
    }

    public <T> T getCompany(Class<T> responseType, String recruiterId) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (recruiterId != null) {
            resource = resource.queryParam("recruiterId", recruiterId);
        }
        resource = resource.path("getCompany");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response addApplicationHistory() throws ClientErrorException {
        return webTarget.path("addApplicationHistory").request().post(null, Response.class);
    }

    public Response scheduleInterview(Object requestEntity) throws ClientErrorException {
        return webTarget.path("scheduleInterview").request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).post(jakarta.ws.rs.client.Entity.entity(requestEntity, jakarta.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
    }

    public <T> T getJobSkills(Class<T> responseType, String jobId) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (jobId != null) {
            resource = resource.queryParam("jobId", jobId);
        }
        resource = resource.path("getJobSkills");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getApplications(Class<T> responseType, String jobId) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (jobId != null) {
            resource = resource.queryParam("jobId", jobId);
        }
        resource = resource.path("getApplications");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response addSkillToJob() throws ClientErrorException {
        return webTarget.path("addSkillToJob").request().post(null, Response.class);
    }

    public Response removeSkillFromJob() throws ClientErrorException {
        return webTarget.path("removeSkillFromJob").request().delete(Response.class);
    }

    public Response generateScore() throws ClientErrorException {
        return webTarget.path("generateScore").request().post(null, Response.class);
    }

    public <T> T getScore(Class<T> responseType, String applicationId) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (applicationId != null) {
            resource = resource.queryParam("applicationId", applicationId);
        }
        resource = resource.path("getScore");
        return resource.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response updateFeedback() throws ClientErrorException {
        return webTarget.path("updateFeedback").request().put(null, Response.class);
    }

    public void close() {
        client.close();
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

    public <T> T getDashboardTopCandidates(Class<T> responseType, String recruiterId) {
        return webTarget
                .path("dashboardTopCandidates")
                .queryParam("recruiterId", recruiterId)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .get(responseType);
    }

    public <T> T getDashboardUpcomingInterviews(Class<T> responseType, String recruiterId) {

        return webTarget
                .path("dashboardUpcomingInterviews")
                .queryParam("recruiterId", recruiterId)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .get(responseType);
    }

    public <T> T getRecentActivities(Class<T> responseType, String userId) {

        return webTarget
                .path("recentActivities")
                .queryParam("userId", userId)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization",
                        "Bearer " + token)
                .get(responseType);
    }

}
