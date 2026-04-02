/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */

/*
 * AuthBean.java
 * This EJB is responsible for handling user authentication (LOGIN)
 */
package EJB;

import Entity.Tblusers;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author RINKAL
 */
@Stateless
public class AuthBean implements AuthBeanLocal {

    @PersistenceContext(unitName = "jpu")
    EntityManager em;
    
    /**
     * Pbkdf2PasswordHash is used for password hashing & verification
     * It ensures secure password handling (no plain text comparison)
     */
    
    @Inject Pbkdf2PasswordHash hash;

    
    /**
     * LOGIN METHOD
     * 
     * @param userName → username entered by user
     * @param password → password entered by user
     * @return Tblusers object if login successful, otherwise null
     */
    
    @Override
    public Tblusers login(String userName, String password) {
        try {

            // Remove extra spaces
            userName = userName.trim();
            password = password.trim();

            //Fetch user from database using username , NamedQuery "Tblusers.findByuserName" must be defined in Tblusers entity
            Tblusers user = em.createNamedQuery("Tblusers.findByuserName", Tblusers.class)
                    .setParameter("userName", userName)
                    .getSingleResult();

            //Configure hashing parameters. These MUST match the parameters used during registration
            Map<String, String> params = new HashMap<>();
            params.put("Pbkdf2PasswordHash.Iterations", "3072"); // Number of hashing iterations
            params.put("Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA256"); // Algorithm used
            hash.initialize(params);// Initialize hash with parameters

            // Verify password , password entered by user → converted to char[] ,- stored password → already hashed in DB ,verify() compares both securely
            if (user != null && hash.verify(password.toCharArray(), user.getUserPassword())) {
                // LOGIN SUCCESS → return user object
                return user;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        // LOGIN FAILED → return null
        return null;
    };
    
}