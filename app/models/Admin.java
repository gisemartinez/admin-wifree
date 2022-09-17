package models;

import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.time.Instant;

/**
 * Created by jesu on 09/06/17.
 */
@Entity
public class Admin extends BaseModel {

    @OneToOne(mappedBy = "owner")
    private Portal portal;

    private String name;

    @NotNull
    @Constraints.Email
    private String email;

    private String password;

    private Instant lastConnection;

    private boolean online;


    public Admin() {
    }

    public Admin(Long id) {
        this.id = id;
    }

    public Admin(Portal portal, String name, String email, String password, Instant lastConnection, boolean online) {
        this.portal = portal;
        this.name = name;
        this.email = email;
        this.password = password;
        this.lastConnection = lastConnection;
        this.online = online;
    }


    @Override
    public String toLogString() {
        return toLogString("id: " + id, "portal: " + portal, "email: " + email);
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Instant getLastConnection() {
        return lastConnection;
    }

    public void setLastConnection(Instant lastConnection) {
        this.lastConnection = lastConnection;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

}
