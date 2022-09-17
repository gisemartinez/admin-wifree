package models.types;

public class SocialKeys {

    private String provider;
    private String clientId;
    private String secret;

    public SocialKeys() {
    }

    public SocialKeys(String provider, String clientId, String secret) {
        this.provider = provider;
        this.clientId = clientId;
        this.secret = secret;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
