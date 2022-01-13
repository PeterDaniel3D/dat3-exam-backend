package dtos;

public class UserDTO {
    private String userName;
    private Long ownerId;

    public UserDTO(String userName) {
        this.userName = userName;
    }

    public UserDTO(String userName, Long ownerId) {
        this.userName = userName;
        this.ownerId = ownerId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
