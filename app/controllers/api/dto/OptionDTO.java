package controllers.api.dto;

import models.FieldConfig;
import models.Option;

public class OptionDTO {

    public Integer index;
    public String key;
    public Integer maximum;

    public OptionDTO() {}

    public OptionDTO(Integer index, String key) {
        this.index = index;
        this.key = key;
    }

    public OptionDTO(Integer maximum) {
        this.maximum = maximum;
    }

    public static OptionDTO fromDomain(Option option) {
        return new OptionDTO(option.getIndex(), option.getKey());
    }

    public static OptionDTO fromRating(FieldConfig maximumConfig) {
        return new OptionDTO(maximumConfig.getMaximum());
    }
}
