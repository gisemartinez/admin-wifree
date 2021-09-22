package models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PortalAppConfig {

    private String leftColumn;
    private String middleColumn;
    private String rightColumn;

    private List<File> images;

    public PortalAppConfig() {}

    public PortalAppConfig(String leftColumn, String middleColumn, String rightColumn) {
        this.leftColumn = leftColumn;
        this.middleColumn = middleColumn;
        this.rightColumn = rightColumn;
    }

    public void fixImagesPaths() {
        images.forEach(img -> {
        });
    }

    public static PortalAppConfig templateOne() {
        return new PortalAppConfig("", "", "");
    }

    public static PortalAppConfig templateTwo() {
        return new PortalAppConfig();
    }

    public static PortalAppConfig carrousel() {
        return new PortalAppConfig(new ArrayList<>());
    }

    public PortalAppConfig(List<File> images) {
        this.images = images;
    }

    public String getLeftColumn() {
        return leftColumn;
    }

    public void setLeftColumn(String leftColumn) {
        this.leftColumn = leftColumn;
    }

    public String getMiddleColumn() {
        return middleColumn;
    }

    public void setMiddleColumn(String middleColumn) {
        this.middleColumn = middleColumn;
    }

    public String getRightColumn() {
        return rightColumn;
    }

    public void setRightColumn(String rightColumn) {
        this.rightColumn = rightColumn;
    }

    public List<File> getImages() {
        return images;
    }

    public void setImages(List<File> images) {
        this.images = images;
    }
}
