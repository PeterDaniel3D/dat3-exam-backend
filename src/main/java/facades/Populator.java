package facades;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import entities.*;
import errorhandling.API_Exception;
import utils.EMF_Creator;

import java.util.List;

public class Populator {

    private static final EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();

    public static void main(String[] args) throws API_Exception {
        populate();
        createUsers();
    }

    public static void populate() throws API_Exception {
        EntityManager em = emf.createEntityManager();

        // TODO: Create data to populate
    }

    public static void createUsers() throws API_Exception {
        EntityManager em = emf.createEntityManager();

        TypedQuery<User> query = em.createQuery("SELECT user FROM User user", User.class);
        List<User> users = query.getResultList();

        if (users.isEmpty()) {
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
        } else {
            throw new API_Exception("DB already contain users!");
        }
    }
}
