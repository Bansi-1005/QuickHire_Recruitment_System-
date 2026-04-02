/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package CDIBean;

import Client.JwtClientFilter;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.Serializable;
import org.json.JSONObject;

/**
 *
 * @author RINKAL
 */
@Named(value = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {
    
    // Stores user credentials from UI
    private String username;
    private String password;
    
    // Message to show on UI (error/success)
    private String message;
    
    // JWT Token received from backend
    private String token;
    
    // Role of logged-in user (Admin / Recruiter / Candidate)
    private String role;

    // ================= GETTERS & SETTERS =================

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    public LoginBean() {
    }
    
    
    public String login() {
    try {
        System.out.println("FRONTEND USERNAME: " + username);
        System.out.println("FRONTEND PASSWORD: " + password);
        
        // Create REST client
        Client client = ClientBuilder.newClient();
        
        // Target login API
        WebTarget target = client.target("http://localhost:8080/Quick_Hire/resources/auth/login");

        // Prepare form data
        Form form = new Form();
        form.param("userName", username);
        form.param("password", password);

        // Send POST request
        Response response = target.request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));

        // ================= SUCCESS =================
        if (response.getStatus() == 200) {

            //Get JWT token from response header
            //token = response.getHeaderString(HttpHeaders.AUTHORIZATION);
         
            String fullToken = response.getHeaderString(HttpHeaders.AUTHORIZATION);

            // Remove "Bearer " prefix
            if (fullToken != null && fullToken.startsWith("Bearer ")) {
                token = fullToken.substring(7).trim();  
            } else {
                token = null; 
            }
            // Read JSON response
            String jsonResponse = response.readEntity(String.class);
            JSONObject obj = new JSONObject(jsonResponse);
            
            // Extract token & role
            role = obj.getString("role");

            // Redirect based on role
            if (role.equalsIgnoreCase("Admin")) {
                return "/admin/adminDashboard.xhtml?faces-redirect=true";
            } else if (role.equalsIgnoreCase("Recruiter")) {
                return "/recruiter/recruiterDashboard.xhtml?faces-redirect=true";
            } else {
                return "/candidate/candidateDashboard.xhtml?faces-redirect=true";
            }

        } else if (response.getStatus() == 401) {
            message = "Invalid Username or Password";
        } else {
            message = "Login Failed (Error Code: " + response.getStatus() + ")";
        }

    } catch (Exception e) {
        e.printStackTrace();
        message = "Server Error";
    }

    return null;
}
    
    public void checkLogin() {
    if (token == null || role == null) {
        try {
            FacesContext.getCurrentInstance()
                .getExternalContext()
                .redirect("login.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
    
    // ================= LOGOUT =================
    public String logout() {
        token = null;
        username = null;
        password = null;
        role = null;

        return "/login.xhtml?faces-redirect=true";
    }
}



