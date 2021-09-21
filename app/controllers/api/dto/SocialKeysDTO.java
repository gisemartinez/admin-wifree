package controllers.api.dto;

import models.types.SocialKeys;

public class SocialKeysDTO extends WiFreeDTO {

    public String clientId;
    public String secret;

    public SocialKeysDTO() {}

    public SocialKeysDTO(String clientId, String secret) {
        this.clientId = clientId;
        this.secret = secret;
    }

    public static SocialKeysDTO fromDomain(SocialKeys socialKeys) {
        return new SocialKeysDTO(socialKeys.getClientId(), socialKeys.getSecret());
    }
}
