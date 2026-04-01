/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jwt;

/**
 *
 * @author RINKAL
 */


import jakarta.ws.rs.core.SecurityContext;
import java.security.Principal;

public class MySecurityContext implements SecurityContext {

    private String userName;
    private String role;

    public MySecurityContext(String userName, String role) {
        this.userName = userName;
        this.role = role;
    }

    @Override
    public Principal getUserPrincipal() {
        return () -> userName;
    }

    @Override
    public boolean isUserInRole(String role) {
        return this.role != null && this.role.equals(role);
    }

    @Override
    public boolean isSecure() {
        return false; // change if using HTTPS
    }

    @Override
    public String getAuthenticationScheme() {
        return "Bearer";
    }
}