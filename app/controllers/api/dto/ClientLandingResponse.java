package controllers.api.dto;

public class ClientLandingResponse extends WiFreeDTO {

    public LandingChoicesDTO landingChoices;
    public String client_id;
    public String template_id;

    public ClientLandingResponse() {
    }

    public ClientLandingResponse(LandingChoicesDTO landingChoices, String client_id, String template_id) {
        this.landingChoices = landingChoices;
        this.client_id = client_id;
        this.template_id = template_id;
    }
}
