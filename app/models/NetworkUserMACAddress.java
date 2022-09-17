package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 * Created by jesu on 20/06/17.
 */
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"network_user_id", "mac_address"})
)
@Entity
public class NetworkUserMACAddress extends BaseModel {

    @ManyToOne(optional = false)
    private NetworkUser networkUser;

    @NotNull
    private String macAddress;


    public NetworkUserMACAddress() {
    }

    public NetworkUserMACAddress(NetworkUser networkUser, String macAddress) {
        this.networkUser = networkUser;
        this.macAddress = macAddress;
    }

    @Override
    public String toLogString() {
        return null;
    }

    public NetworkUser getNetworkUser() {
        return networkUser;
    }

    public void setNetworkUser(NetworkUser networkUser) {
        this.networkUser = networkUser;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}
