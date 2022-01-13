package facades;

import dtos.AuctionDTO;
import dtos.BoatDTO;
import dtos.OwnerDTO;
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

    public List<AuctionDTO> getAuctions() throws API_Exception {
        EntityManager em = getEntityManager();
        TypedQuery<Auction> query = em.createQuery("SELECT auction FROM Auction auction", Auction.class);
        List<Auction> auctions = query.getResultList();
        if (auctions.isEmpty()) {
            throw new API_Exception("No auctions found. DB table is empty.");
        }
        List<AuctionDTO> auctionDTOS = new ArrayList<>();
        auctions.forEach(auction -> auctionDTOS.add(new AuctionDTO(auction.getId(), auction.getName(), auction.getDate(), auction.getTime(), auction.getLocation())));
        return auctionDTOS;
    }

    public List<BoatDTO> getBoatsByOwner(Long id) throws API_Exception {
        EntityManager em = getEntityManager();
        TypedQuery<Boat> query = em.createQuery("SELECT boat FROM Boat boat INNER JOIN boat.owners owners WHERE owners.id = :id", Boat.class);
        query.setParameter("id", id);
        List<Boat> boats = query.getResultList();
        if (boats.isEmpty()) {
            throw new API_Exception("No boats found. DB table is empty.");
        }
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

    public BoatDTO addBoat(BoatDTO boatDTO) throws API_Exception {
        EntityManager em = getEntityManager();
        Boat boat = new Boat(boatDTO.getName(), boatDTO.getBrand(), boatDTO.getMake(), boatDTO.getYear(), boatDTO.getImageURL());
        Owner owner = em.find(Owner.class, boatDTO.getOwnerId());
        if (owner == null) {
            throw new API_Exception("No owner with id (" + boatDTO.getOwnerId() + ") found.");
        }
        List<Boat> boatList = owner.getBoats();
        boatList.add(boat);
        try {
            em.getTransaction().begin();
            em.persist(boat);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new BoatDTO(boat.getId(),  boat.getName(), boat.getBrand(), boat.getMake(), boat.getYear(), boat.getImageURL());
    }
}
