package controllers.api.dto;

public class SocialKeysDTO extends WiFreeDTO {

    public String clientId;
    public String secret;

    public SocialKeysDTO() {}

    public SocialKeysDTO(String clientId, String secret) {
        this.clientId = clientId;
        this.secret = secret;
    }
}
