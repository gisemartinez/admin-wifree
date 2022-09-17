package models;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Created by jesu on 08/06/17.
 */
@Entity
public class Address extends BaseModel {

    @OneToOne(optional = false)
    private NetworkUser owner;

    private String street;

    private Integer number;

    private String other;

    private String city;

    private String state;

    private String country;

    private String zipCode;


    public Address() {
    }

    public Address(NetworkUser owner, String street, Integer number, String other, String city, String state, String country, String zipCode) {
        this.owner = owner;
        this.street = street;
        this.number = number;
        this.other = other;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zipCode = zipCode;
    }


    @Override
    public String toLogString() {
        return toLogString("id: " + id, "owner: " + owner, "zipCode: " + zipCode);
    }


    public NetworkUser getOwner() {
        return owner;
    }

    public void setOwner(NetworkUser owner) {
        this.owner = owner;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

}
