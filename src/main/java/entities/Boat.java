package entities;

import javax.persistence.*;
import java.util.List;

@Entity
@NamedQuery(name = "boat.deleteAllRows", query = "DELETE FROM Boat")
@Table(name = "boat")
public class Boat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "brand")
    private String brand;

    @Column(name = "make")
    private String make;

    @Column(name = "year")
    private Integer year;

    @Column(name = "imageURL")
    private String imageURL;

    @ManyToMany(mappedBy = "boats")
    private List<Owner> owners;

    @ManyToOne
    @JoinColumn(name = "harbour_id")
    private Auction auction;

    public Boat() {
    }

    public Boat(String name, String brand, String make, Integer year, String imageURL) {
        this.name = name;
        this.brand = brand;
        this.make = make;
        this.year = year;
        this.imageURL = imageURL;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public List<Owner> getOwners() {
        return owners;
    }

    public void setOwners(List<Owner> owners) {
        this.owners = owners;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }
}
