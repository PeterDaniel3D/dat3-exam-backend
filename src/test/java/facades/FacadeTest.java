package facades;

import entities.Auction;
import entities.Boat;
import entities.Owner;
import errorhandling.API_Exception;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FacadeTest {

    private static EntityManagerFactory emf;
    private static Facade facade;
    private static Owner ownerA, ownerB, ownerC;
    private static Boat boatA, boatB, boatC;
    private static Auction auctionA, auctionB;

    public FacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = Facade.getFacade(emf);
    }

    @BeforeEach
    void setUp() throws API_Exception {
        EntityManager em = emf.createEntityManager();

        // Create
        ownerC = new Owner("Daniel", "11112222", "daniel@daniel.dk");
        ownerB = new Owner("Jon", "33334444", "jon@jon.dk");
        ownerA = new Owner("Peter", "55556666", "peter@peter.dk");

        boatA = new Boat("Modesty", "Ukendt", "BB-11", 1967, "https://motorbaadsnyt.dk/fileadmin/news_import/modstytopPICT0048.JPG");
        boatB = new Boat("Speedy", "Yamaha", "SS-Turbo", 1997, "https://www.proptalk.com/sites/default/files/inline-images/48576791432_9edee785c5_o.jpg");
        boatC = new Boat("Nimbus T8", "Nimbus", "Nimbus 8 Series", 1971, "https://nimbus.se/app/uploads/2020/10/Nimbus2020_T8-1200x800.jpg");

        auctionA = new Auction("Båd auktion i Torvehallerne", "2022/02/01", "12:00", "København");
        auctionB = new Auction("Køb en billig båd", "2022/06/01", "12:00", "Israels Plads");

        // Assign
        boatA.setAuction(auctionA);
        boatB.setAuction(auctionB);
        boatC.setAuction(auctionB);

        List<Boat> boatListA = ownerA.getBoats();
        boatListA.add(boatA);

        List<Boat> boatListB = ownerB.getBoats();
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
    void tearDown() {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.createNamedQuery("owner.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    void getAuctions() {
        assertEquals(2, facade.getAuctions().size());
    }
}