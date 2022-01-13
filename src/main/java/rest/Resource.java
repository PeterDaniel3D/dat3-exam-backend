package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import dtos.UserDTO;
import errorhandling.API_Exception;
import facades.Facade;
import utils.EMF_Creator;
import utils.Message;

import java.util.List;

@Path("")
public class Resource {
    private static final EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
    private static final Facade facade = Facade.getFacade(emf);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response demo() {
        Message result = new Message("Hello Anonymous! Server is running...");
        return Response.ok().entity(gson.toJson(result)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("admin")
    @RolesAllowed("admin")
    public Response demoAdmin() {
        String user = securityContext.getUserPrincipal().getName();
        Message result = new Message("Hello to (admin) User: " + user);
        return Response.ok().entity(gson.toJson(result)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user")
    @RolesAllowed("user")
    public Response demoUser() {
        String user = securityContext.getUserPrincipal().getName();
        Message result = new Message("Hello to User: " + user);
        return Response.ok().entity(gson.toJson(result)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("users")
    @RolesAllowed("admin")
    public Response getUsers() throws API_Exception {
        List<UserDTO> result = facade.getUsers();
        return Response.ok().entity(gson.toJson(result)).build();
    }
}