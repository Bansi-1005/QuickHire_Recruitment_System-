/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */

/**
 * UserDisplayAdminCDIBean
 * 
 * PURPOSE:
 * This CDI Bean is used by Admin UI to fetch and display all users
 * from backend REST API.
 * 
 * It uses JWT token (stored in LoginBean) to authenticate API requests.
 */
package CDIBean;

import Client.JwtClientFilter;
import Entity.Tblusers;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.Serializable;
import java.util.List;
import util.RestClientUtil;

/**
 *
 * @author RINKAL
 */
@Named(value = "userDisplayAdminCDIBean")
@SessionScoped
public class UserDisplayAdminCDIBean implements Serializable {

    /**
     * Creates a new instance of UserDisplayAdminCDIBean
     */
    public UserDisplayAdminCDIBean() {
    }
    // Inject LoginBean to access stored JWT token
    @Inject LoginBean loginBean;
   // List to store users received from backend API
    private List<Tblusers> users;

    public List<Tblusers> getUsers() {
        loadUsers();// Load data before returning
        return users;
    }

    public void loadUsers() {
        try {
             // Create client with JWT token attached
            Client client = RestClientUtil.getClient(loginBean.getToken());
            
            // Define target API endpoint
            WebTarget target = client.target("http://localhost:8080/Quick_Hire/resources/admin/getAllUsers");

            // Send GET request
            Response response = target.request(MediaType.APPLICATION_JSON).get();

            // If response is successful
            if (response.getStatus() == 200) {
                // Convert JSON response to Java List
                users = response.readEntity(new GenericType<List<Tblusers>>() {});
            } else {
                System.out.println("FAILED: " + response.getStatus());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

