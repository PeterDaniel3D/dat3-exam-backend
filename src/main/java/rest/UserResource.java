package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.UserDTO;
import errorhandling.API_Exception;
import facades.UserFacade;
import utils.EMF_Creator;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.List;

@Path("")
public class UserResource {
    private static final EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
    private static final UserFacade facade = UserFacade.getFacade(emf);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("users")
    @RolesAllowed("admin")
    public Response getUsers() throws API_Exception {
        List<UserDTO> result = facade.getUsers();
        return Response.ok().entity(gson.toJson(result)).build();
    }
}
