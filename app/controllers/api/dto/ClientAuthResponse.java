package controllers.api.dto;

public class ClientAuthResponse extends WiFreeDTO {

    public AuthDataDTO authData;
    public ClientDataDTO clientData;
    public CarouselDataDTO carouselData;

    public ClientAuthResponse() {
    }

    public ClientAuthResponse(AuthDataDTO authData, ClientDataDTO clientData, CarouselDataDTO carouselData) {
        this.authData = authData;
        this.clientData = clientData;
        this.carouselData = carouselData;
    }

}
