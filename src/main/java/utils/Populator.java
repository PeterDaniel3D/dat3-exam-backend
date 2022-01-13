package utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import entities.*;
import errorhandling.API_Exception;

import java.util.List;

public class Populator {

    private static final EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();

    private static Owner ownerA, ownerB, ownerC;
    private static Boat boatA, boatB, boatC;
    private static Auction auctionA, auctionB;

    public static void main(String[] args) throws API_Exception {
        populate();
        createUsers();
    }

    public static void populate() throws API_Exception {
        EntityManager em = emf.createEntityManager();

        TypedQuery<Owner> query = em.createQuery("SELECT owner FROM Owner owner", Owner.class);
        List<Owner> owners = query.getResultList();

        if (owners.isEmpty()) {
            // Create
            ownerA = new Owner("Daniel", "11112222", "daniel@daniel.dk");
            ownerB = new Owner("Jon", "33334444", "jon@jon.dk");
            ownerC = new Owner("Peter", "55556666", "peter@peter.dk");

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
                System.out.println("Users created!");
            }
        } else {
            throw new API_Exception("DB is already populated!");
        }
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
                System.out.println("Populated DB!");
            }
        } else {
            throw new API_Exception("DB already contain users!");
        }
    }
}
