package facades;

import dtos.BoatDTO;
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
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FacadeTest {

    private static EntityManagerFactory emf;
    private static Facade facade;
    private static Owner ownerA, ownerB, ownerC;
    private static Boat boatA, boatB, boatC;
    private static Auction auctionA, auctionB;

    public FacadeTest() {
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = Facade.getFacade(emf);
    }

    @BeforeEach
    void setUp() throws API_Exception {
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
    void tearDown() {
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

    @Test
    void getAuctions() throws API_Exception {
        EntityManager em = getEntityManager();
        TypedQuery<Auction> query = em.createQuery("SELECT auction FROM Auction auction", Auction.class);
        List<Auction> auctions = query.getResultList();
        int expected = auctions.size();
        int actual = facade.getAuctions().size();
        assertEquals(expected, actual);
    }

    @Test
    void getOwner() throws API_Exception {
        String expected = ownerA.getName();
        String actual = facade.getOwner(ownerA.getId()).getName();
        assertEquals(expected, actual);
    }

    @Test
    void getBoatsByOwner() throws API_Exception {
        EntityManager em = getEntityManager();
        TypedQuery<Boat> query = em.createQuery("SELECT boat FROM Boat boat INNER JOIN boat.owners owners WHERE owners.id = :id", Boat.class);
        query.setParameter("id", ownerC.getId());
        List<Boat> boats = query.getResultList();
        int expected = boats.size();
        int actual = facade.getBoatsByOwner(ownerC.getId()).size();
        assertEquals(expected, actual);
    }

    @Test
    void addBoat() throws API_Exception {
        BoatDTO expected = new BoatDTO(boatA.getName(), boatA.getBrand(), boatA.getMake(), boatA.getYear(), boatA.getImageURL(), ownerA.getId());
        BoatDTO actual = facade.addBoat(expected);
        assertTrue(Objects.equals(expected.getName(), actual.getName()) &&
                Objects.equals(expected.getBrand(), actual.getBrand()) &&
                Objects.equals(expected.getMake(), actual.getMake()) &&
                Objects.equals(expected.getYear(), actual.getYear()) &&
                Objects.equals(expected.getImageURL(), actual.getImageURL())
        );
    }
}