/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Client;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import java.io.IOException;

/**
 *
 * @author RINKAL
 */
public class JwtClientFilter implements ClientRequestFilter {

    private final String token;

    public JwtClientFilter(String token) {
        this.token = token;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        
        // If token exists → attach it in header
        if (token != null && !token.isEmpty()) {
            requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
        System.out.println("JWT CLIENT FILTER CALLED");
        System.out.println("TOKEN SENT: " + token);
    }
}