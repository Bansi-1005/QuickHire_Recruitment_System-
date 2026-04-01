/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
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
import static java.util.Objects.hash;

/**
 *
 * @author RINKAL
 */
@Stateless
public class AuthBean implements AuthBeanLocal {

    @PersistenceContext(unitName = "jpu")
    EntityManager em;

    @Inject Pbkdf2PasswordHash hash;

    @Override
    public Tblusers login(String userName, String password) {
        try {

            // 🔥 REMOVE EXTRA SPACES
            userName = userName.trim();
            password = password.trim();

            Tblusers user = em.createNamedQuery("Tblusers.findByuserName", Tblusers.class)
                    .setParameter("userName", userName)
                    .getSingleResult();

            Map<String, String> params = new HashMap<>();
            params.put("Pbkdf2PasswordHash.Iterations", "3072");
            params.put("Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA256");
            hash.initialize(params);

            if (user != null && hash.verify(password.toCharArray(), user.getUserPassword())) {
                return user;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    };
    
}