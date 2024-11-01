package org.dailydone.mobile.android.rest;

import org.dailydone.mobile.android.model.User;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface IAuthenticationRestOperations {

    @PUT
    @Path("/auth")
    boolean authenticateUser(User user);

    @PUT
    @Path("/prepare")
    boolean prepare(User user);
}