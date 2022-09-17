package models;

import models.types.Gender;
import play.data.validation.Constraints;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jesu on 08/06/17.
 */
@Entity
public class NetworkUser extends BaseModel {

    @ManyToOne(optional = false)
    private Portal portal;

    private String name;

    @Constraints.Email
    @NotNull
    private String email;

    @Nullable
    private String lastUsedMACAddress;

    @Nullable
    private Instant lastConnection;

    private boolean online;

    private String password;

    private Gender gender;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "owner")
    private Address address;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "networkUser")
    private Set<NetworkUserMACAddress> macAddresses = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    private Set<NetworkUserSocialNetworkAccount> socialNetworkAccounts = new HashSet<>();

    private int age;


    public NetworkUser() {
    }

    public NetworkUser(Long id) {
        this.id = id;
    }

    public NetworkUser(Portal portal, String name, String email, String lastUsedMACAddress, Instant lastConnection,
                       boolean online, String password, Gender gender, Address address, Set<NetworkUserMACAddress> macAddresses,
                       Set<NetworkUserSocialNetworkAccount> socialNetworkAccounts, int age) {
        this.portal = portal;
        this.name = name;
        this.email = email;
        this.lastUsedMACAddress = lastUsedMACAddress;
        this.lastConnection = lastConnection;
        this.online = online;
        this.password = password;
        this.gender = gender;
        this.address = address;
        this.macAddresses = macAddresses;
        this.socialNetworkAccounts = socialNetworkAccounts;
        this.age = age;
    }


    @Override
    public String toLogString() {
        return toLogString("id: " + id, "portal: " + portal, "name: " + name, "email: " + email);
    }


    public Portal getPortal() {
        return portal;
    }

    public void setPortal(Portal portal) {
        this.portal = portal;
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

    public String getLastUsedMACAddress() {
        return lastUsedMACAddress;
    }

    public void setLastUsedMACAddress(String lastUsedMACAddress) {
        this.lastUsedMACAddress = lastUsedMACAddress;
    }

    public Instant getLastConnection() {
        return lastConnection;
    }

    public void setLastConnection(Instant last_connection) {
        this.lastConnection = last_connection;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Set<NetworkUserMACAddress> getMacAddresses() {
        return macAddresses;
    }

    public void setMacAddresses(Set<NetworkUserMACAddress> macAddresses) {
        this.macAddresses = macAddresses;
    }

    public Set<NetworkUserSocialNetworkAccount> getSocialNetworkAccounts() {
        return socialNetworkAccounts;
    }

    public void setSocialNetworkAccounts(Set<NetworkUserSocialNetworkAccount> socialNetworkAccounts) {
        this.socialNetworkAccounts = socialNetworkAccounts;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
