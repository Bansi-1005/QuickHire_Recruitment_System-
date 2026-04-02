/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/JerseyClient.java to edit this template
 */
package Client;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

/**
 * Jersey REST client generated for REST resource:RegistrationResource
 * [registration]<br>
 * USAGE:
 * <pre>
 *        RegistrationJerseyClient client = new RegistrationJerseyClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author tejan
 */
public class RegistrationJerseyClient {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8080/Quick_Hire/resources";

    public RegistrationJerseyClient() {
        client = jakarta.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("registration");
    }
//
//    public Response registerUser(Object requestEntity) throws ClientErrorException {
//        return webTarget.path("registerUser").request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(jakarta.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
//    }

    public void close() {
        client.close();
    }
    
    public String registerUser(Object requestEntity) throws ClientErrorException {
        return webTarget
                .path("registerUser")
                .request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
                .post(jakarta.ws.rs.client.Entity.entity(requestEntity, jakarta.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
    }

}
