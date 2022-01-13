package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import dtos.AuctionDTO;
import dtos.BoatDTO;
import dtos.OwnerDTO;
import dtos.UserDTO;
import errorhandling.API_Exception;
import facades.Facade;
import facades.Populator;
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
    @Path("populate")
    public Response populate() throws API_Exception {
        Populator.populate();
        Message result = new Message("Populated DB!");
        return Response.ok().entity(gson.toJson(result)).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("ownerId/{id}")
    @RolesAllowed("owner")
    public Response getOwnerId(@PathParam("id") String username) throws API_Exception {
        UserDTO result = facade.getOwnerId(username);
        return Response.ok().entity(gson.toJson(result)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("auctions")
    public Response getAuctions() {
        List<AuctionDTO> result = facade.getAuctions();
        return Response.ok().entity(gson.toJson(result)).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("owner/{id}")
    @RolesAllowed("owner")
    public Response getOwner(@PathParam("id") Long id) throws API_Exception {
        OwnerDTO result = facade.getOwner(id);
        return Response.ok().entity(gson.toJson(result)).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("boatsByOwner/{id}")
    @RolesAllowed("owner")
    public Response getBoatsByOwner(@PathParam("id") Long id) {
        List<BoatDTO> result = facade.getBoatsByOwner(id);
        return Response.ok().entity(gson.toJson(result)).build();
    }
}