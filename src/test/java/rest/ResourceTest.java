package rest;

import entities.Auction;
import entities.Boat;
import entities.Owner;
import errorhandling.API_Exception;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

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

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    private static Owner ownerA, ownerB, ownerC;
    private static Boat boatA, boatB, boatC;
    private static Auction auctionA, auctionB;

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

        // Create
        ownerA = new Owner("Daniel", "11112222", "daniel@daniel.dk");
        ownerB = new Owner("Jon", "33334444", "jon@jon.dk");
        ownerC = new Owner("Peter", "55556666", "peter@peter.dk");

        boatA = new Boat("Modesty", "Ukendt", "BB-11", 1967, "https://motorbaadsnyt.dk/fileadmin/news_import/modstytopPICT0048.JPG");
        boatB = new Boat("Speedy", "Yamaha", "SS-Turbo", 1997, "https://www.proptalk.com/sites/default/files/inline-images/48576791432_9edee785c5_o.jpg");
        boatC = new Boat("Nimbus T8", "Nimbus", "Nimbus 8 Series", 1971, "https://nimbus.se/app/uploads/2020/10/Nimbus2020_T8-1200x800.jpg");

        auctionA = new Auction("Båd auktion i Torvehallerne", "2022/02/01", "09:00", "København");
        auctionB = new Auction("Køb en billig båd", "2022/06/01", "12:30", "Israels Plads");

        // Assign
        boatA.setAuction(auctionA);
        boatB.setAuction(auctionB);
        boatC.setAuction(auctionB);

        List<Boat> boatListA = ownerA.getBoats();
        boatListA.add(boatA);

        List<Boat> boatListB = ownerB.getBoats();
        boatListB.add(boatA);
        boatListB.add(boatB);
        boatListB.add(boatC);

        List<Boat> boatListC = ownerC.getBoats();
        boatListC.add(boatC);

        // Persist
        try {
            em.getTransaction().begin();
            em.persist(ownerA);
            em.persist(ownerB);
            em.persist(ownerC);
            em.persist(boatA);
            em.persist(boatB);
            em.persist(boatC);
            em.persist(auctionA);
            em.persist(auctionB);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new API_Exception("No connection to DB.");
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            em.createNamedQuery("boat.deleteAllRows").executeUpdate();
            em.createNamedQuery("auction.deleteAllRows").executeUpdate();
            em.createNamedQuery("owner.deleteAllRows").executeUpdate();
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

    @Test
    public void populate() {
        given()
                .contentType("application/json")
                .get("populate")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode())
                .body("errorCode", equalTo(HttpStatus.BAD_REQUEST_400.getStatusCode()))
                .body("message", equalTo("DB is already populated!"));
    }

    @Test
    public void getAuctions() {
        EntityManager em = getEntityManager();
        TypedQuery<Auction> query = em.createQuery("SELECT auction FROM Auction auction", Auction.class);
        List<Auction> auctions = query.getResultList();

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .get("auctions")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("auctions", hasSize(auctions.size()));
    }
}