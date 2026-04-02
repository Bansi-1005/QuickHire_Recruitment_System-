/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * MySecurityContext.java
 * This class provides user identity and role information to JAX-RS security system
 */
package jwt;

/**
 *
 * @author RINKAL
 */


import jakarta.ws.rs.core.SecurityContext;
import java.security.Principal;

public class MySecurityContext implements SecurityContext {

    private String userName; // Stores username extracted from JWT
    private String role;// Stores role extracted from JWT

    // Constructor to initialize username and role
    public MySecurityContext(String userName, String role) {
        this.userName = userName;
        this.role = role;
    }

    // Returns the logged-in user as Principal object
    @Override
    public Principal getUserPrincipal() {
        return () -> userName;
    }

    // Checks if current user has the required role
    @Override
    public boolean isUserInRole(String role) {
        return this.role != null && this.role.equals(role);// Compare roles
    }

    // Indicates whether the request is secure (HTTPS)
    @Override
    public boolean isSecure() {
        return false; // Change to true if using HTTPS
    }

    // Returns authentication scheme used (JWT uses Bearer token)
    @Override
    public String getAuthenticationScheme() {
        return "Bearer";
    }
}