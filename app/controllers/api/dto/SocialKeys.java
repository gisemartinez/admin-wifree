package controllers.api.dto;

public class SocialKeys extends WiFreeDTO {

    public String clientId;
    public String secret;

    public SocialKeys() {}

    public SocialKeys(String clientId, String secret) {
        this.clientId = clientId;
        this.secret = secret;
    }
}
