package controllers.api.dto;

import models.Option;

public class OptionDTO {

    public Integer index;
    public String key;

    public OptionDTO() {}

    public OptionDTO(Integer index, String key) {
        this.index = index;
        this.key = key;
    }

    public static OptionDTO fromDomain(Option option) {
        return new OptionDTO(option.getIndex(), option.getKey());
    }
}
