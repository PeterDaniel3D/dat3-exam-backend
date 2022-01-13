package facades;

import dtos.AuctionDTO;
import dtos.BoatDTO;
import dtos.OwnerDTO;
import dtos.UserDTO;
import entities.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import errorhandling.API_Exception;

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

    public List<AuctionDTO> getAuctions() {
        EntityManager em = getEntityManager();
        TypedQuery<Auction> query = em.createQuery("SELECT auction FROM Auction auction", Auction.class);
        List<Auction> auctions = query.getResultList();
        List<AuctionDTO> auctionDTOS = new ArrayList<>();
        auctions.forEach(auction -> auctionDTOS.add(new AuctionDTO(auction.getId(), auction.getName(), auction.getDate(), auction.getTime(), auction.getLocation())));
        return auctionDTOS;
    }

    public List<BoatDTO> getBoatsByOwner(Long id) {
        EntityManager em = getEntityManager();
        TypedQuery<Boat> query = em.createQuery("SELECT boat FROM Boat boat INNER JOIN boat.owners owners WHERE owners.id = :id", Boat.class);
        query.setParameter("id", id);
        List<Boat> boats = query.getResultList();
        List<BoatDTO> boatDTOS = new ArrayList<>();
        boats.forEach(boat -> boatDTOS.add(new BoatDTO(boat.getId(), boat.getName(), boat.getBrand(), boat.getMake(), boat.getYear(), boat.getImageURL())));
        return boatDTOS;
    }

    public OwnerDTO getOwner(Long id) throws API_Exception {
        EntityManager em = getEntityManager();
        Owner owner = em.find(Owner.class, id);
        if (owner == null) {
            throw new API_Exception("No owner with id (" + id + ") found.");
        }
        return new OwnerDTO(owner.getId(), owner.getName(), owner.getPhone(), owner.getEmail());
    }
}
