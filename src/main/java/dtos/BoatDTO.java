package dtos;

public class BoatDTO {
    private Long id;
    private String name;
    private String brand;
    private String make;
    private int year;
    private String imageURL;

    public BoatDTO(Long id, String name, String brand, String make, int year, String imageURL) {
        this.id = id;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
