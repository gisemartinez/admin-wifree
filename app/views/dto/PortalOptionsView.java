package views.dto;

import models.PortalApp;
import models.types.PortalApplicationType;

public class PortalOptionsView {

    private Long portalId;
    private String homeURL;
    private PortalApplicationType templateType;
    private PortalApp template;
    private PortalApp carrousel;
    private String name;
    private String description;

    public PortalOptionsView() {}

    public PortalOptionsView(Long portalId, String homeURL, PortalApplicationType templateType, PortalApp template,
                             PortalApp carrousel, String name, String description) {
        this.portalId = portalId;
        this.homeURL = homeURL;
        this.templateType = templateType;
        this.template = template;
        this.carrousel = carrousel;
        this.name = name;
        this.description = description;
    }

    public static PortalOptionsView initialize(Long portalId, String homeURL, String name, String description) {
        return new PortalOptionsView(portalId, homeURL, PortalApplicationType.TemplateOne, PortalApp.templateOne(),
                PortalApp.carrousel(), name, description);
    }

    public Long getPortalId() {
        return portalId;
    }

    public void setPortalId(Long portalId) {
        this.portalId = portalId;
    }

    public String getHomeURL() {
        return homeURL;
    }

    public void setHomeURL(String homeURL) {
        this.homeURL = homeURL;
    }

    public PortalApp getTemplate() {
        return template;
    }

    public void setTemplate(PortalApp template) {
        this.template = template;
    }

    public PortalApp getCarrousel() {
        return carrousel;
    }

    public void setCarrousel(PortalApp carrousel) {
        this.carrousel = carrousel;
    }

    public PortalApplicationType getTemplateType() {
        return templateType;
    }

    public void setTemplateType(PortalApplicationType templateType) {
        this.templateType = templateType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
