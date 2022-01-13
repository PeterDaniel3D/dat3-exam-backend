package facades;

import dtos.AuctionDTO;
import dtos.UserDTO;
import entities.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import errorhandling.API_Exception;
import security.errorhandling.AuthenticationException;


import java.util.ArrayList;
import java.util.List;

public class Facade {

    private static EntityManagerFactory emf;
    private static Facade instance;

    private Facade() {
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static Facade getFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new Facade();
        }
        return instance;
    }

    public User registerNewUser(String username, String password) {
        EntityManager em = emf.createEntityManager();
        Role role = new Role("user");
        User user = new User(username, password);
        user.addRole(role);
        try {
            if (em.find(User.class, username) == null) {
                em.getTransaction().begin();
                em.persist(user);
                em.getTransaction().commit();
            } else throw new Exception("User with username (" + username + ") already exists!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return user;
    }

    public User getVerifiedUser(String username, String password) throws AuthenticationException {
        EntityManager em = getEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user == null || !user.verifyPassword(password)) {
                throw new AuthenticationException("Invalid username and/or password.");
            }
        } finally {
            em.close();
        }
        return user;
    }

    public List<UserDTO> getUsers() throws API_Exception {
        EntityManager em = getEntityManager();
        TypedQuery<User> query = em.createQuery("SELECT user FROM User user", User.class);
        List<User> users = query.getResultList();
        if (users.isEmpty()) {
            throw new API_Exception("No users found. DB table is empty.");
        }
        List<UserDTO> userDTOS = new ArrayList<>();
        users.forEach(user -> userDTOS.add(new UserDTO(user.getUserName())));
        return userDTOS;
    }

    public List<AuctionDTO> getAuctions() {
        EntityManager em = getEntityManager();
        TypedQuery<Auction> query = em.createQuery("SELECT auction FROM Auction auction", Auction.class);
        List<Auction> auctions = query.getResultList();
        List<AuctionDTO> auctionDTOS = new ArrayList<>();
        auctions.forEach(auction -> auctionDTOS.add(new AuctionDTO(auction.getId(), auction.getName(), auction.getDate(), auction.getTime(), auction.getLocation())));
        return auctionDTOS;
    }
}
