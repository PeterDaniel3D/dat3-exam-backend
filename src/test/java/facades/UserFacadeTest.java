package facades;

import dtos.UserDTO;
import entities.*;
import errorhandling.API_Exception;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserFacadeTest {

    private static EntityManagerFactory emf;
    private static UserFacade facade;
    private static Owner ownerA, ownerB, ownerC;
    private static Role adminRole, ownerRole;
    private static User admin, user, dev;

    public UserFacadeTest() {
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = UserFacade.getFacade(emf);
    }

    @BeforeEach
    void setUp() throws API_Exception {
        EntityManager em = getEntityManager();

        // Create owners
        ownerA = new Owner("Daniel", "11112222", "daniel@daniel.dk");
        ownerB = new Owner("Jon", "33334444", "jon@jon.dk");
        ownerC = new Owner("Peter", "55556666", "peter@peter.dk");

        try {
            em.getTransaction().begin();
            em.persist(ownerA);
            em.persist(ownerB);
            em.persist(ownerC);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new API_Exception("No connection to DB.");
        }

        // Create roles
        adminRole = new Role("admin");
        ownerRole = new Role("owner");

        // Create users
        admin = new User("admin", "admin");
        user = new User("owner", "owner");
        dev = new User("dev", "dev");

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
            em.persist(ownerA);
            em.persist(ownerB);
            em.persist(ownerC);
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
    void tearDown() {
        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            em.createNamedQuery("users.deleteAllRows").executeUpdate();
            em.createNamedQuery("roles.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

//    @Test
//    void registerNewUser() {
//    }
//
//    @Test
//    void getVerifiedUser() {
//    }

    @Test
    void getUsers() throws API_Exception {
        EntityManager em = getEntityManager();
        TypedQuery<User> query = em.createQuery("SELECT user FROM User user", User.class);
        List<User> users = query.getResultList();
        int expected = users.size();
        int actual = facade.getUsers().size();
        assertEquals(expected, actual);
    }

    @Test
    void getOwnerId() throws API_Exception {
        Long expected = ownerC.getId();
        Long actual = facade.getOwnerId(dev.getUserName()).getOwnerId();
        assertEquals(expected, actual);
    }

    @Test
    void addBoat() {

    }
}