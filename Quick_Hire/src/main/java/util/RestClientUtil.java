/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * RestClientUtil
 * 
 * PURPOSE:
 * This utility class is used to create a REST client
 * and automatically attach JWT token to every API request.
 * 
 * This avoids writing the same code (client.register...) everywhere.
 */

package util;

import Client.JwtClientFilter;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

/**
 *
 * @author RINKAL
 */
public class RestClientUtil {
    
    public static Client getClient(String token) {
        // Create new REST client
        Client client = ClientBuilder.newClient();

        // If token is available → attach it using JwtClientFilter
        if (token != null && !token.isEmpty()) {
            client.register(new JwtClientFilter(token));
        }

        // Return configured client
        return client;
    }
}
