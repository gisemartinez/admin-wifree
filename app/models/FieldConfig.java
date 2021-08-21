package models;


import utils.StringHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FieldConfig {

    protected String key;
    protected String label;
    protected Integer order;

    // Textbox
    @Nullable
    private Boolean required;
    @Nullable
    private String value;

    // Rating
    @Nullable
    private Integer maximum;

    // Radio Selector Checkbox
    @Nullable
    private List<Option> otherOptions;

    // TODO Checkbox
    // TODO Selector


    public FieldConfig(String key, String label, Integer order, @Nullable Boolean required, @Nullable String value, @Nullable Integer maximum, @Nullable List<Option> otherOptions) {
        this.key = key;
        this.label = label;
        this.order = order;
        this.required = required;
        this.value = value;
        this.maximum = maximum;
        this.otherOptions = otherOptions;
    }

    public FieldConfig(){}

    protected FieldConfig(String key, String label, Integer order) {
        this.key = key;
        this.label = label;
        this.order = order;
    }

    public FieldConfig copy() {
        return new FieldConfig(key, label, order, required, value, maximum,
                Objects.requireNonNull(otherOptions).stream().map(Option::copy).collect(Collectors.toList()));
    }

    public String toLogString() {
        return StringHelper.toLogString("key=" + key, "label=" + label, "order=" + order, "required=" + required,
                "value=" + value, "maximum=" + maximum,
                "otherOptions=" + (otherOptions != null ? otherOptions.stream().map(Option::toLogString).collect(Collectors.joining("\t\n")) : ""));
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getMaximum() {
        return maximum;
    }

    public void setMaximum(Integer maximum) {
        this.maximum = maximum;
    }

    public List<Option> getOtherOptions() {
        return otherOptions;
    }

    public void setOtherOptions(List<Option> otherOptions) {
        this.otherOptions = otherOptions;
    }

    public static class FieldConfigTypes {
        public static final String Textbox = "textbox";
        public static final String Rating = "rating";
        public static final String Radio = "radio";
    }

}
