/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package CDIBean;

import Client.JwtClientFilter;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.Serializable;

/**
 *
 * @author RINKAL
 */
@Named(value = "dashboardBean")
@SessionScoped
public class DashboardCDIBean implements Serializable {
    @Inject LoginCDIBean loginBean;

    private String result;

    public String getResult() {
        return result;
    }

    // ================= GET USERS (ADMIN API) =================
    public void getUsers() {
        try {
            Client client = ClientBuilder.newClient();

            // 🔥 REGISTER FILTER (AUTO TOKEN ATTACH)
            client.register(new JwtClientFilter(loginBean.getToken()));

            WebTarget target = client.target("http://localhost:8080/Quick_Hire/resources/admin/users");

            Response response = target.request(MediaType.APPLICATION_JSON).get();

            result = response.readEntity(String.class);

        } catch (Exception e) {
            e.printStackTrace();
            result = "Error calling API";
        }
    }

    // ================= GET JOBS =================
    public void getJobs() {
        try {
            Client client = ClientBuilder.newClient();
            client.register(new JwtClientFilter(loginBean.getToken()));

            WebTarget target = client.target("http://localhost:8080/Quick_Hire/resources/jobs");

            Response response = target.request(MediaType.APPLICATION_JSON).get();

            result = response.readEntity(String.class);

        } catch (Exception e) {
            e.printStackTrace();
            result = "Error fetching jobs";
        }
    }
}