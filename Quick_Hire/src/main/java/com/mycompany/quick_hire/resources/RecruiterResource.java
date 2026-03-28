/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quick_hire.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 *
 * @author tejan
 */

@Path("recruiter")
public class RecruiterResource {
     @GET
    public Response ping(){
        return Response
                .ok("ping Jakarta EE")
                .build();
    }
}
