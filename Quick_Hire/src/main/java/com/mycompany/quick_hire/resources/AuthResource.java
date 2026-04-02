/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quick_hire.resources;

import EJB.AuthBeanLocal;
import Entity.Tblusers;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jwt.TokenProvider;
import org.json.JSONObject;

/**
 *
 * @author RINKAL
 */

@Path("auth")
public class AuthResource {

    @EJB
    AuthBeanLocal authBean;

    @Inject
    TokenProvider tokenProvider;

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    //@Produces(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)   
    public Response login(@FormParam("userName") String userName,
                          @FormParam("password") String password) {

        System.out.println("LOGIN API HIT");
        System.out.println("userName: " + userName);

        Tblusers user = authBean.login(userName, password);

        if (user == null) {
            System.out.println("LOGIN FAILED");
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        String role = user.getRoleId().getRoleName();

        String token = tokenProvider.createToken(userName, role);

        System.out.println("TOKEN GENERATED: " + token);

        String json = new JSONObject()
        .put("token", token)
        .put("role", role)
        .toString();

        return Response.ok(json)
        .header("Authorization", "Bearer " + token)
        .build();
    }
}