package controllers.api.dto;

public class LandingChoicesDTO extends WiFreeDTO {

    public String title;
    public String iframeURL;

    public LandingChoicesDTO() {}

    public LandingChoicesDTO(String title, String iframeURL) {
        this.title = title;
        this.iframeURL = iframeURL;
    }

}
