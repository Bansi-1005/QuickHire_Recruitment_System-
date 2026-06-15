/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package EJB;

import Entity.*;
import jakarta.ejb.Local;
import java.util.Collection;

/**
 *
 * @author tejan
 */
@Local
public interface RegistrationBeanLocal {
    void registerUser(Tblusers user, Tblrolemaster role, Tblcandidates candidate, Tblrecruiters recruiter);
        public Collection<Tblcompany> getAllCompanies();

}
