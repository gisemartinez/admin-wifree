package models;


import utils.StringHelper;

public class Option {

    private Integer index;
    private String key;

    public Option() {
    }

    public Option(Integer index, String key) {
        this.index = index;
        this.key = key;
    }

    public String toLogString() {
        return StringHelper.toLogString("index=" + index, "key=" + key);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Option copy() {
        return new Option(index, key);
    }
}
