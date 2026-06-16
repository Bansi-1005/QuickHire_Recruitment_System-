/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package CDIBean;

import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.Serializable;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.Cookie;
import jwt.TokenProvider;
import util.EmailServiceLocal;

/**
 *
 * @author RINKAL
 */
@Named(value = "loginBean")
@SessionScoped
public class LoginCDIBean implements Serializable {

    @Inject
    RecruiterCDIBean recruiterCDIBean;

    @Inject
    CandidateCDIBean candidateCDIBean;

    @Inject
    EmailServiceLocal emailService;

    @Inject
    private TokenProvider tokenProvider;
    // Stores user credentials from UI
    private String username;
    private String password;

    // Message to show on UI (error/success)
    private String message;

    // JWT Token received from backend
    private String token;

    // Role of logged-in user (Admin / Recruiter / Candidate)
    private String role;

    private int userId;

    private boolean rememberMe;

    private String forgotEmail;
    private String otp;
    private String enteredOtp;
    private String newPassword;

    private boolean successMessage;

    // ================= GETTERS & SETTERS =================
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public String getForgotEmail() {
        return forgotEmail;
    }

    public void setForgotEmail(String forgotEmail) {
        this.forgotEmail = forgotEmail;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getEnteredOtp() {
        return enteredOtp;
    }

    public void setEnteredOtp(String enteredOtp) {
        this.enteredOtp = enteredOtp;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public boolean isSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(boolean successMessage) {
        this.successMessage = successMessage;
    }

    public LoginCDIBean() {
    }

    public String login() {

        message = null;
        successMessage = false;

        try {

            Client client = ClientBuilder.newClient();

            WebTarget target = client.target(
                    "http://localhost:8080/Quick_Hire/resources/auth/login");

            Form form = new Form();
            form.param("userName", username);
            form.param("password", password);

            Response response = target.request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(form,
                            MediaType.APPLICATION_FORM_URLENCODED));

            if (response.getStatus() == 200) {

                String fullToken
                        = response.getHeaderString(HttpHeaders.AUTHORIZATION);

                if (fullToken != null
                        && fullToken.startsWith("Bearer ")) {

                    token = fullToken.substring(7).trim();

                    System.out.println("NEW LOGIN TOKEN:");
                    System.out.println(token);
                }

                String jsonResponse = response.readEntity(String.class);

                JSONObject obj = new JSONObject(jsonResponse);

                role = obj.getString("role");
                userId = obj.getInt("userId");

                if (rememberMe) {

                    Map<String, Object> cookieProps = new HashMap<>();

                    cookieProps.put("maxAge", 60 * 60 * 24 * 30); // 30 days
                    cookieProps.put("path", "/");

                    FacesContext.getCurrentInstance()
                            .getExternalContext()
                            .addResponseCookie(
                                    "quickhire_token",
                                    token,
                                    cookieProps);

                    FacesContext.getCurrentInstance()
                            .getExternalContext()
                            .addResponseCookie(
                                    "quickhire_role",
                                    role,
                                    cookieProps);

                    FacesContext.getCurrentInstance()
                            .getExternalContext()
                            .addResponseCookie(
                                    "quickhire_userId",
                                    String.valueOf(userId),
                                    cookieProps);

                    FacesContext.getCurrentInstance()
                            .getExternalContext()
                            .addResponseCookie(
                                    "quickhire_username",
                                    username,
                                    cookieProps);

                }

                FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .getSessionMap()
                        .put("userId", userId);

                if ("Admin".equalsIgnoreCase(role)) {

                    return "/admin/adminDashboard.xhtml?faces-redirect=true";

                } else if ("Recruiter".equalsIgnoreCase(role)) {

                    recruiterCDIBean.loadProfile();
                    recruiterCDIBean.loadDashboardData();

                    return "/recruiter/recruiterDashboard.xhtml?faces-redirect=true";

                } else {
                    candidateCDIBean.reloadAfterRememberMe();
                    return "/candidate/candidateDashboard.xhtml?faces-redirect=true";
                }

            } else if (response.getStatus() == 401) {

                message = "Invalid Username or Password";
                return null;

            } else {

                message = "Login Failed. Please try again.";
                return null;
            }

        } catch (Exception e) {

            e.printStackTrace();
            message = "Server Error. Please try again later.";
            return null;
        }
    }

    public void checkLogin() {
        if (token == null || role == null) {
            try {
                FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .redirect(
                                FacesContext.getCurrentInstance()
                                        .getExternalContext()
                                        .getRequestContextPath()
                                + "/Login.xhtml"
                        );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadRememberMe() {

        token = null;
        role = null;
        userId = 0;
        username = null;

        Map<String, Object> cookies
                = FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .getRequestCookieMap();

        Cookie tokenCookie
                = (Cookie) cookies.get("quickhire_token");

        Cookie roleCookie
                = (Cookie) cookies.get("quickhire_role");

        Cookie userIdCookie
                = (Cookie) cookies.get("quickhire_userId");

        Cookie usernameCookie
                = (Cookie) cookies.get("quickhire_username");

        if (tokenCookie != null
                && roleCookie != null
                && userIdCookie != null
                && usernameCookie != null) {

            token = tokenCookie.getValue();
            role = roleCookie.getValue();
            userId = Integer.parseInt(userIdCookie.getValue());
            username = usernameCookie.getValue();

            System.out.println("COOKIE TOKEN = " + token);
            System.out.println("COOKIE ROLE = " + role);
            System.out.println("COOKIE USERID = " + userId);

        }
    }

    public String autoLogin() {

        loadRememberMe();

        if (token == null || role == null) {
            return null;
        }

        if (!tokenProvider.validateToken(token)) {

            logout();

            return null;
        }

        FacesContext.getCurrentInstance()
                .getExternalContext()
                .getSessionMap()
                .put("userId", userId);

        if ("Admin".equalsIgnoreCase(role)) {

            return "/admin/adminDashboard.xhtml?faces-redirect=true";

        } else if ("Recruiter".equalsIgnoreCase(role)) {

            recruiterCDIBean.loadProfile();
            recruiterCDIBean.loadDashboardData();

            return "/recruiter/recruiterDashboard.xhtml?faces-redirect=true";

        } else {

            candidateCDIBean.reloadAfterRememberMe();

            return "/candidate/candidateDashboard.xhtml?faces-redirect=true";
        }
    }

    // ================= LOGOUT =================
    public String logout() {

        Map<String, Object> cookieProps = new HashMap<>();

        cookieProps.put("maxAge", 0);
        cookieProps.put("path", "/");

        FacesContext.getCurrentInstance()
                .getExternalContext()
                .addResponseCookie(
                        "quickhire_token",
                        "",
                        cookieProps);

        FacesContext.getCurrentInstance()
                .getExternalContext()
                .addResponseCookie(
                        "quickhire_role",
                        "",
                        cookieProps);

        FacesContext.getCurrentInstance()
                .getExternalContext()
                .addResponseCookie(
                        "quickhire_userId",
                        "",
                        cookieProps);

        FacesContext.getCurrentInstance()
                .getExternalContext()
                .addResponseCookie(
                        "quickhire_username",
                        "",
                        cookieProps);

        FacesContext.getCurrentInstance()
                .getExternalContext()
                .invalidateSession();

        return "/Login.xhtml?faces-redirect=true";
    }

    public void sendOtp() {

        try {

            otp = String.valueOf(
                    (int) ((Math.random() * 900000) + 100000)
            );

            String subject = "QuickHire Password Reset OTP";

            String body
                    = "Dear User,\n\n"
                    + "Your OTP for password reset is: "
                    + otp
                    + "\n\nThis OTP is valid for one use only.\n\n"
                    + "QuickHire Team";

            emailService.sendEmail(
                    forgotEmail,
                    subject,
                    body
            );

//            FacesContext.getCurrentInstance()
//                    .getExternalContext()
//                    .getFlash()
//                    .setKeepMessages(true);
// change
            if (FacesContext.getCurrentInstance() != null) {
                var flash = FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .getFlash();

                if (flash != null) {
                    flash.setKeepMessages(true);
                }
            }

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new jakarta.faces.application.FacesMessage(
                            jakarta.faces.application.FacesMessage.SEVERITY_INFO,
                            "OTP send successfully to your email",
                            null
                    )
            );
        } catch (Exception e) {

            e.printStackTrace();

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new jakarta.faces.application.FacesMessage(
                            "Failed to send OTP"
                    )
            );
        }
    }

    public boolean verifyOtp() {

        if (otp != null
                && otp.equals(enteredOtp)) {

            return true;
        }

        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Invalid OTP",
                        null
                )
        );

        return false;
    }

    public String resetPassword() {

        try {

            if (!verifyOtp()) {
                return null;
            }

            Client client = ClientBuilder.newClient();

            WebTarget target = client.target(
                    "http://localhost:8080/Quick_Hire/resources/auth/resetPassword"
            );

            Response response = target
                    .queryParam("email", forgotEmail)
                    .queryParam("password", newPassword)
                    .request()
                    .put(Entity.text(""));

            System.out.println("EMAIL = " + forgotEmail);
            System.out.println("PASSWORD = " + newPassword);
            System.out.println("STATUS = " + response.getStatus());

            String result = response.readEntity(String.class);
            System.out.println("RESPONSE = " + result);

            if (response.getStatus() == 200) {

                FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .getFlash()
                        .setKeepMessages(true);
// change
//                FacesContext.getCurrentInstance().addMessage(
//                        null,
//                        new FacesMessage(
//                                FacesMessage.SEVERITY_INFO,
//                                "OTP send successfully to your email",
//                                null
//                        )
//                );

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new jakarta.faces.application.FacesMessage(
                                jakarta.faces.application.FacesMessage.SEVERITY_INFO,
                                "Password updated successfully",
                                null
                        )
                );

                return "/Login.xhtml?faces-redirect=true";

            } else {

//                FacesContext.getCurrentInstance()
//                        .getExternalContext()
//                        .getFlash()
//                        .setKeepMessages(true);
// change
                if (FacesContext.getCurrentInstance() != null) {
                    var flash = FacesContext.getCurrentInstance()
                            .getExternalContext()
                            .getFlash();

                    if (flash != null) {
                        flash.setKeepMessages(true);
                    }
                }
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new jakarta.faces.application.FacesMessage(
                                jakarta.faces.application.FacesMessage.SEVERITY_ERROR,
                                "Password update failed",
                                null
                        )
                );

                return null;
            }
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }
}
