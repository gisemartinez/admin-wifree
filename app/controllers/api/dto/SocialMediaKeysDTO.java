package controllers.api.dto;

public class SocialMediaKeysDTO extends WiFreeDTO {

    public SocialKeys facebook;
    public SocialKeys google;

    public SocialMediaKeysDTO() {}

    public SocialMediaKeysDTO(SocialKeys facebook, SocialKeys google) {
        this.facebook = facebook;
        this.google = google;
    }

}
