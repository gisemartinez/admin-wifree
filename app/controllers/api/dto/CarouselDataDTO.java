package controllers.api.dto;

import java.util.List;

public class CarouselDataDTO extends WiFreeDTO {
    public List<String> images;

    public CarouselDataDTO() {
    }

    public CarouselDataDTO(List<String> images) {
        this.images = images;
    }
}
