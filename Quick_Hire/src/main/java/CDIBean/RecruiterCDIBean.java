/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package CDIBean;

import Client.RecruiterJerseyClient;
import Entity.Tblrecruiters;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import java.io.Serializable;

/**
 *
 * @author RINKAL
 */
@Named(value = "recruiterCDIBean")
@SessionScoped
public class RecruiterCDIBean implements Serializable {

    private Tblrecruiters recruiter = new Tblrecruiters();
    private RecruiterJerseyClient client = new RecruiterJerseyClient();

    @Inject
    LoginCDIBean loginBean;   // ✅ GET LOGGED-IN USER

    public RecruiterCDIBean() {}

    // ================= LOAD PROFILE =================
    @PostConstruct
    public void init() {
        // DO NOTHING HERE (token not ready yet)
    }

    public void loadProfile() {
        try {
            // ✅ Set token EVERY TIME before API call
            client.setToken(loginBean.getToken());

            // ✅ Use correct userId (NOT username)
            int userId = loginBean.getUserId();   // MUST exist in login bean

            recruiter = client.getProfile(Tblrecruiters.class, String.valueOf(userId));
            System.out.println("Loaded Recruiter ID: " + recruiter.getRecruiterId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= UPDATE PROFILE =================
    public void updateProfile() {
        try {
            client.setToken(loginBean.getToken());

            System.out.println("Updating Recruiter ID: " + recruiter.getRecruiterId());

            Response res = client.updateProfile(recruiter);

            String responseMsg = res.readEntity(String.class);
            System.out.println("SERVER RESPONSE: " + responseMsg);

            FacesMessage message;

            if (res.getStatus() == 200) {
                // show nice text to user
                message = new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        "Profile updated successfully",
                        "Your changes have been saved."
                );
                loadProfile();    // refresh data in bean
            } else {
                message = new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Update failed",
                        responseMsg
                );
            }

            FacesContext.getCurrentInstance().addMessage(null, message);

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Update failed",
                            "Something went wrong while updating your profile.")
            );
        }
    }
    public Tblrecruiters getRecruiter() {
        return recruiter;
    }

    public void setRecruiter(Tblrecruiters recruiter) {
        this.recruiter = recruiter;
    }
}