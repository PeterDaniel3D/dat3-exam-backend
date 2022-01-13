package facades;

import entities.*;
import errorhandling.API_Exception;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    public UserFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = UserFacade.getFacade(emf);
    }

    @BeforeEach
    void setUp() throws API_Exception {
        EntityManager em = emf.createEntityManager();

        // Create users
        ownerA = new Owner("Daniel", "11112222", "daniel@daniel.dk");
        ownerB = new Owner("Jon", "33334444", "jon@jon.dk");
        ownerC = new Owner("Peter", "55556666", "peter@peter.dk");

        // Create roles
        Role adminRole = new Role("admin");
        Role ownerRole = new Role("owner");

        // Create users
        User admin = new User("admin", "admin");
        User user = new User("owner", "owner");
        User dev = new User("dev", "dev");

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

//    @Test
//    void registerNewUser() {
//    }
//
//    @Test
//    void getVerifiedUser() {
//    }

    @Test
    void getUsers() throws API_Exception {
        assertEquals(3, facade.getUsers().size());
    }
}