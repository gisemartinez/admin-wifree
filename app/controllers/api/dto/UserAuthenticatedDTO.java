package controllers.api.dto;

import java.time.Instant;

public class UserAuthenticatedDTO extends WiFreeDTO {

    private Long id;
    private Long portalId;
    private String name;
    private String email;
    private Instant connectionTime;
    private String gender;
    private String socialNetwork;
    private Integer age;

    public UserAuthenticatedDTO() {
    }

    public UserAuthenticatedDTO(Long id, Long portalId, String name, String email, Instant connectionTime, String gender, String socialNetwork, Integer age) {
        this.id = id;
        this.portalId = portalId;
        this.name = name;
        this.email = email;
        this.connectionTime = connectionTime;
        this.gender = gender;
        this.socialNetwork = socialNetwork;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPortalId() {
        return portalId;
    }

    public void setPortalId(Long portalId) {
        this.portalId = portalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getConnectionTime() {
        return connectionTime;
    }

    public void setConnectionTime(Instant connectionTime) {
        this.connectionTime = connectionTime;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSocialNetwork() {
        return socialNetwork;
    }

    public void setSocialNetwork(String socialNetwork) {
        this.socialNetwork = socialNetwork;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
