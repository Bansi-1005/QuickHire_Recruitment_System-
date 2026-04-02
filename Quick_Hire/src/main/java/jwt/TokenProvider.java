/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/*
 * TokenProvider.java
 * This class handles JWT token creation, validation, and data extraction
 */
package jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import jakarta.inject.Named;
import java.io.Serializable;
import java.security.*;
import java.util.Date;



@Named // Makes this class injectable (CDI bean)
public class TokenProvider implements Serializable {

    // Secret key (Base64 encoded) used to sign and verify JWT tokens
    private static final String SECRET = "bXlzZWNyZXRrZXkxMjM0NTY3ODkwMTIzNDU2Nzg5MDEy"; // example (32+ bytes)

     // Convert Base64 secret into secure Key object for signing
    private final Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));

    // Method to CREATE JWT token using username and role
    public String createToken(String userName, String role) {

        long now = System.currentTimeMillis(); // Get current time

        return Jwts.builder()
                .subject(userName) // Set username as subject of token                 // ✅ NEW (replaces setSubject)
                .claim("role", role) // Add user role as custom claim
                .issuer("quickhire") // Set token issuer (your application name)
                .issuedAt(new Date(now))// Set token creation time
                .expiration(new Date(now + 86400000)) // Set expiry time (1 day)
                .signWith(key) // Sign token with secret key
                .compact();// Generate final JWT string
    }

    // Method to EXTRACT claims (data) from token
    public Claims getCredential(String token) {
        return Jwts.parser()// Create JWT parser                    
                .verifyWith((javax.crypto.SecretKey) key)// Verify token using same secret key
                .build()// Build parser
                .parseSignedClaims(token)// Parse token and validate signature
                .getPayload();// Return claims (data inside token)
    }

     // Method to VALIDATE token (check if token is valid or not)
    public boolean validateToken(String token) {
        try {
            Jwts.parser()// Create parser
                    .verifyWith((javax.crypto.SecretKey) key)// Verify using secret key
                    .build()// Build parser
                    .parseSignedClaims(token);// Parse token (throws error if invalid)

            return true;// Token is valid

        } catch (Exception e) {
            e.printStackTrace();
            return false;// Token is invalid
        }
    }
}