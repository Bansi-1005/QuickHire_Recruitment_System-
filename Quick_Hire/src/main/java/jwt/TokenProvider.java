/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import java.io.Serializable;
import java.security.*;
import java.util.Date;
import javax.crypto.SecretKey;



@Named
public class TokenProvider implements Serializable {

    // ✅ Use BASE64 encoded 256-bit key (VERY IMPORTANT)
    private static final String SECRET = "bXlzZWNyZXRrZXkxMjM0NTY3ODkwMTIzNDU2Nzg5MDEy"; // example (32+ bytes)

    private final Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));

    // 🔐 CREATE TOKEN (NEW API)
    public String createToken(String userName, String role) {

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(userName)                  // ✅ NEW (replaces setSubject)
                .claim("role", role)
                .issuer("quickhire")             // ✅ NEW
                .issuedAt(new Date(now))         // ✅ NEW
                .expiration(new Date(now + 86400000))
                .signWith(key)                   // ✅ OK
                .compact();
    }

    // 🔍 GET CLAIMS (NEW API)
    public Claims getCredential(String token) {
        return Jwts.parser()                     // ✅ NEW (replaces parserBuilder)
                .verifyWith((javax.crypto.SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ✅ VALIDATE TOKEN
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((javax.crypto.SecretKey) key)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}