package security;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import errorhandling.API_Exception;
import facades.Facade;
import facades.Populator;
import facades.UserFacade;
import utils.EMF_Creator;
import utils.Message;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("")
public class RegisterEndpoint {

    private static final EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
    private static final UserFacade facade = UserFacade.getFacade(emf);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("createUsers")
    public Response createUsers() throws API_Exception {
        Populator.createUsers();
        Message result = new Message("Users created!");
        return Response.ok().entity(gson.toJson(result)).build();
    }

    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(String jsonString) throws API_Exception {
        String username;
        String password;
        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            username = json.get("username").getAsString();
            password = json.get("password").getAsString();
        } catch (Exception e) {
            throw new API_Exception("Malformed JSON supplied", 400, e);
        }
        return Response.ok().entity(gson.toJson(facade.registerNewUser(username, password))).build();
    }
}
