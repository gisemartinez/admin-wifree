package models;

import models.types.SocialNetworkType;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by jesu on 10/06/17.
 */
@Entity
public class NetworkUserSocialNetworkAccount extends BaseModel {

    @ManyToOne(optional = false)
    private NetworkUser networkUser;

    private SocialNetworkType socialNetwork;

    private String accountId;


    public NetworkUserSocialNetworkAccount() {
    }

    public NetworkUserSocialNetworkAccount(NetworkUser networkUser, SocialNetworkType socialNetwork, String accountId) {
        this.networkUser = networkUser;
        this.socialNetwork = socialNetwork;
        this.accountId = accountId;
    }


    @Override
    public String toLogString() {
        return toLogString("id: " + id, "networkUser: " + networkUser, "socialNetwork: " + socialNetwork, "accountId: " + accountId);
    }


    public NetworkUser getNetworkUser() {
        return networkUser;
    }

    public void setNetworkUser(NetworkUser networkUser) {
        this.networkUser = networkUser;
    }

    public SocialNetworkType getSocialNetwork() {
        return socialNetwork;
    }

    public void setSocialNetwork(SocialNetworkType socialNetwork) {
        this.socialNetwork = socialNetwork;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
