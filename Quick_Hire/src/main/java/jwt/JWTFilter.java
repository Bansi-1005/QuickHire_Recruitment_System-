/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * JWTFilter.java
 * This filter intercepts every API request to validate JWT token and set user security context
 */
package jwt;

/**
 *
 * @author RINKAL
 */

import io.jsonwebtoken.Claims;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import java.io.IOException;

@Provider // Registers this class automatically as a JAX-RS filter(it works globally)
@Priority(Priorities.AUTHENTICATION) // Executes this filter at authentication phase (before resource methods)
public class JWTFilter implements ContainerRequestFilter {

    @Inject TokenProvider tokenProvider; // Inject TokenProvider to validate and parse JWT

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String path = requestContext.getUriInfo().getPath(); // Get requested API path
        System.out.println("REQUEST PATH: " + path);

        // Allow PUBLIC APIs without token (login and registration endpoints
        if (path.contains("auth/login") 
            || path.contains("registration/registerUser")
            || path.contains("recruiter/registerRecruiter")
            || path.contains("candidate/registerCandidate")) {
            return;// Skip token validation for these APIs
            }

        // Read Authorization header from request
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // If header is missing or not starting with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("NO TOKEN");
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)// Return 401 Unauthorized
                            .entity("Token Required")
                            .build()
            );
            return; // Stop request processing
        }

        // Extract actual token string by removing "Bearer " prefix
        String token = authHeader.substring("Bearer ".length());

        // Validate token (signature, expiry, format)
        if (!tokenProvider.validateToken(token)) {
            System.out.println("INVALID TOKEN");
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)// Return 401 Unauthorized
                            .entity("Invalid Token")
                            .build()
            );
            return;// Stop request processing
        }

        // Extract claims (data like username and role) from token
        Claims claims = tokenProvider.getCredential(token);
        
        String userName = claims.getSubject();// Get username from token
        String role = claims.get("role", String.class);// Get role from token

        System.out.println("userName: " + userName);
        System.out.println("ROLE: " + role);

        // Create custom SecurityContext with user details
        MySecurityContext securityContext = new MySecurityContext(userName, role);
        // Attach SecurityContext to request (used for @RolesAllowed and security checks)
        requestContext.setSecurityContext(securityContext);
    }
}