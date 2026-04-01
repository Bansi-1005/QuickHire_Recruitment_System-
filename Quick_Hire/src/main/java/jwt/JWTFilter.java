/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JWTFilter implements ContainerRequestFilter {

    @Inject
    TokenProvider tokenProvider;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String path = requestContext.getUriInfo().getPath();
        System.out.println("REQUEST PATH: " + path);

        // ✅ PUBLIC APIs (NO TOKEN REQUIRED)
        if (path.contains("auth/login") 
            || path.contains("recruiter/registerRecruiter")
            || path.contains("candidate/registerCandidate")
            || path.contains("admin/registerAdmin")) {
            return;
            }

        // ✅ GET AUTH HEADER
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ NO TOKEN");
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("Token Required")
                            .build()
            );
            return;
        }

        // ✅ EXTRACT TOKEN
        String token = authHeader.substring("Bearer ".length());

        // ✅ VALIDATE TOKEN
        if (!tokenProvider.validateToken(token)) {
            System.out.println("❌ INVALID TOKEN");
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("Invalid Token")
                            .build()
            );
            return;
        }

        // ✅ EXTRACT CLAIMS
        Claims claims = tokenProvider.getCredential(token);
        String userName = claims.getSubject();
        String role = claims.get("role", String.class);

        System.out.println("✅ userName: " + userName);
        System.out.println("✅ ROLE: " + role);

        // ✅ SET SECURITY CONTEXT
        MySecurityContext securityContext = new MySecurityContext(userName, role);
        requestContext.setSecurityContext(securityContext);
    }
}