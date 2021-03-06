package views.dto;

import models.PortalApp;
import models.types.LoginMethodType;
import models.types.PortalApplicationType;

import java.util.ArrayList;
import java.util.List;

public class PortalOptionsView {

    private Long portalId;
    private String homeURL;
    private PortalApplicationType templateType;
    private PortalApp template;
    private PortalApp carousel;
    private String name;
    private String description;
    private List<LoginMethodType> loginMethods;

    public PortalOptionsView() {
        this.loginMethods = new ArrayList<>();
    }

    public PortalOptionsView(Long portalId, String homeURL, PortalApplicationType templateType, PortalApp template,
                             PortalApp carousel, String name, String description, List<LoginMethodType> loginMethods) {
        this.portalId = portalId;
        this.homeURL = homeURL;
        this.templateType = templateType;
        this.template = template;
        this.carousel = carousel;
        this.name = name;
        this.description = description;
        this.loginMethods = loginMethods;
    }

    public static PortalOptionsView initialize(Long portalId, String homeURL, String name, String description) {
        return new PortalOptionsView(portalId, homeURL, PortalApplicationType.TemplateOne, PortalApp.templateOne(),
                PortalApp.carousel(), name, description, new ArrayList<>());
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

    public PortalApp getCarousel() {
        return carousel;
    }

    public void setCarousel(PortalApp carousel) {
        this.carousel = carousel;
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

    public List<LoginMethodType> getLoginMethods() {
        return loginMethods;
    }

    public void setLoginMethods(List<LoginMethodType> loginMethods) {
        this.loginMethods = loginMethods;
    }
}
