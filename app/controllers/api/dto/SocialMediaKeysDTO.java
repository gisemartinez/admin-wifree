package controllers.api.dto;

public class SocialMediaKeysDTO extends WiFreeDTO {

    public SocialKeysDTO facebook;
    public SocialKeysDTO google;

    public SocialMediaKeysDTO() {}

    public SocialMediaKeysDTO(SocialKeysDTO facebook, SocialKeysDTO google) {
        this.facebook = facebook;
        this.google = google;
    }

}
