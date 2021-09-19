package controllers.api.dto;

public class ClientLandingResponse extends WiFreeDTO {

    public String uniqueId;
    public LandingChoicesDTO landingChoices;
    public String client_id;
    public String template_id;

    public ClientLandingResponse() {}

    public ClientLandingResponse(String uniqueId, LandingChoicesDTO landingChoices, String client_id, String template_id) {
        this.uniqueId = uniqueId;
        this.landingChoices = landingChoices;
        this.client_id = client_id;
        this.template_id = template_id;
    }
}
