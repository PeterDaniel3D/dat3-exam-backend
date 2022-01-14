package security;

import entities.Owner;
import entities.User;
import entities.Role;

import errorhandling.API_Exception;
import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;

import java.net.URI;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import org.junit.jupiter.api.*;
import rest.ApplicationConfig;
import utils.EMF_Creator;

public class LoginEndpointTest {

    private static final int server_port = 7777;
    private static final String server_url = "http://localhost/api";
    static final URI BASE_URI = UriBuilder.fromUri(server_url).port(server_port).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    private static User admin, dev, user;
    private static Role adminRole, ownerRole;
    private static Owner ownerA, ownerB, ownerC;

    @BeforeAll
    public static void setUpClass() {
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        RestAssured.baseURI = server_url;
        RestAssured.port = server_port;
        RestAssured.defaultParser = Parser.JSON;
    }

    @BeforeEach
    public void setUp() throws API_Exception {
        EntityManager em = getEntityManager();

        // Create roles
        adminRole = new Role("admin");
        ownerRole = new Role("owner");

        // Create users
        admin = new User("admin", "admin");
        user = new User("owner", "owner");
        dev = new User("dev", "dev");

        // Create owners
        ownerA = new Owner("Daniel", "11112222", "daniel@daniel.dk");
        ownerB = new Owner("Jon", "33334444", "jon@jon.dk");
        ownerC = new Owner("Peter", "55556666", "peter@peter.dk");

        // Assign roles
        admin.addRole(adminRole);
        user.addRole(ownerRole);
        dev.addRole(adminRole);
        dev.addRole(ownerRole);

        // Assign owners to users
        admin.setOwner(ownerA);
        user.setOwner(ownerB);
        dev.setOwner(ownerC);

        // Persist
        try {
            em.getTransaction().begin();
            em.persist(adminRole);
            em.persist(ownerRole);
            em.persist(admin);
            em.persist(user);
            em.persist(dev);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new API_Exception("No connection to DB.");
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("roles.deleteAllRows").executeUpdate();
            em.createNamedQuery("users.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterAll
    public static void stopServer() {
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    private static String securityToken;

    private static void login(String role, String password) {
        String json = String.format("{username: \"%s\", password: \"%s\"}", role, password);

        securityToken = given()
                .contentType("application/json")
                .body(json)
                .when()
                .post("/login")
                .then()
                .extract().path("token");
    }

    private void logOut() {
        securityToken = null;
    }

    @Test
    public void serverIsRunning() {
        given()
                .when()
                .get("")
                .then()
                .statusCode(HttpStatus.OK_200.getStatusCode());
    }

    @Test
    public void users() {
        login("admin", "admin");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("userName", hasSize(3))
                .body("userName", hasItem(user.getUserName()));
    }

    @Test
    public void demo() {
        given()
                .contentType("application/json")
                .get("")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("message", equalTo("Hello Anonymous! Server is running..."));
    }

    @Test
    public void demoAdmin() {
        login("admin", "admin");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/admin")
                .then()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("message", equalTo("Hello to (admin) User: admin"));
    }

    @Test
    public void demoUser() {
        login("owner", "owner");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/user")
                .then()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("message", equalTo("Hello to User: owner"));
    }

    @Test
    public void demoMultiRoleA() {
        login("dev", "dev");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/admin")
                .then()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("message", equalTo("Hello to (admin) User: dev"));
    }

    @Test
    public void demoMultiRoleB() {
        login("dev", "dev");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/user")
                .then()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("message", equalTo("Hello to User: dev"));
    }

    @Test
    public void authorizedUserCannotAccessAdminPage() {
        login("owner", "owner");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/admin")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED_401.getStatusCode());
    }

    @Test
    public void authorizedAdminCannotAccessUserPage() {
        login("admin", "admin");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/user")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED_401.getStatusCode());
    }

    @Test
    public void userNotAuthenticated() {
        logOut();
        given()
                .contentType("application/json")
                .when()
                .get("/user").then()
                .statusCode(HttpStatus.FORBIDDEN_403.getStatusCode())
                .body("errorCode", equalTo(HttpStatus.FORBIDDEN_403.getStatusCode()))
                .body("message", equalTo("Not authenticated - Please login."));
    }

    @Test
    public void adminNotAuthenticated() {
        logOut();
        given()
                .contentType("application/json")
                .when()
                .get("/user").then()
                .statusCode(HttpStatus.FORBIDDEN_403.getStatusCode())
                .body("errorCode", equalTo(HttpStatus.FORBIDDEN_403.getStatusCode()))
                .body("message", equalTo("Not authenticated - Please login."));
    }

    @Test
    public void getOwner() {
        login("owner", "owner");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("owner/" + ownerA.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("name", equalTo(ownerA.getName()));
    }
}
