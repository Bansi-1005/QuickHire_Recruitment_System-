/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package EJB;

import Entity.Tblusers;
import jakarta.ejb.Local;

/**
 *
 * @author RINKAL
 */
@Local
public interface AuthBeanLocal {
      Tblusers login(String userName, String password);

}
