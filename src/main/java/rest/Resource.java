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
import errorhandling.API_Exception;
import facades.Facade;
import utils.Populator;
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
    @RolesAllowed("owner")
    public Response demoUser() {
        String user = securityContext.getUserPrincipal().getName();
        Message result = new Message("Hello to User: " + user);
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
    @Produces(MediaType.APPLICATION_JSON)
    @Path("auctions")
    public Response getAuctions() throws API_Exception {
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
    public Response getBoatsByOwner(@PathParam("id") Long id) throws API_Exception {
        List<BoatDTO> result = facade.getBoatsByOwner(id);
        return Response.ok().entity(gson.toJson(result)).build();
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("boat")
    @RolesAllowed("owner")
    public Response addBoat(String str) throws API_Exception {
        BoatDTO boatDTO = gson.fromJson(str, BoatDTO.class);
        BoatDTO result = facade.addBoat(boatDTO);
        return Response.ok().entity(gson.toJson(result)).build();
    }

    @PUT
    @Path("boat/{id}")
    @RolesAllowed("owner")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response updateBoat(@PathParam("id") Long id, String str) throws API_Exception {
        BoatDTO boatDTO = gson.fromJson(str, BoatDTO.class);
        boatDTO.setId(id);
        BoatDTO result = facade.updateBoat(boatDTO);
        return Response.ok().entity(gson.toJson(result)).build();
    }

    @DELETE
    @Path("auction/{id}")
    @RolesAllowed("admin")
    @Produces({MediaType.APPLICATION_JSON})
    public Response deleteAuction(@PathParam("id") Long id) throws API_Exception {
        AuctionDTO result = facade.deleteAuction(id);
        return Response.ok().entity(gson.toJson(result)).build();
    }
}