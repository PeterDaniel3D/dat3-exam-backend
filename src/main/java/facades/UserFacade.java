package facades;

import dtos.UserDTO;
import entities.Role;
import entities.User;
import errorhandling.API_Exception;
import security.errorhandling.AuthenticationException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;

    private UserFacade() {
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static UserFacade getFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

    public User registerNewUser(String username, String password) {
        EntityManager em = emf.createEntityManager();
        Role role = new Role("owner");
        User user = new User(username, password);
        user.addRole(role);
        try {
            if (em.find(User.class, username) == null) {
                em.getTransaction().begin();
                em.persist(user);
                em.getTransaction().commit();
            } else throw new Exception("Owner with username (" + username + ") already exists!");
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
        users.forEach(user -> userDTOS.add(new UserDTO(user.getUserName(), user.getOwner().getId())));
        return userDTOS;
    }

    public UserDTO getOwnerId(String username) throws API_Exception {
        EntityManager em = getEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user == null) {
                throw new API_Exception("No owner with username (" + username + ") found.");
            }
        } finally {
            em.close();
        }
        return new UserDTO(user.getOwner().getId());
    }
}
