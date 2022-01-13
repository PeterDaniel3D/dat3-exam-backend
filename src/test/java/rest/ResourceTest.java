package rest;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class ResourceTest {
    private static final int server_port = 7777;
    private static final String server_url = "http://localhost/api";
    static final URI BASE_URI = UriBuilder.fromUri(server_url).port(server_port).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

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
    public void setUp() {
        EntityManager em = emf.createEntityManager();

    }

    @AfterEach
    public void tearDown() {
        EntityManager em = emf.createEntityManager();

    }

    @AfterAll
    public static void stopServer() {
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
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
    public void demo() {
        given()
                .contentType("application/json")
                .get("")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("message", equalTo("Hello Anonymous! Server is running..."));
    }
}