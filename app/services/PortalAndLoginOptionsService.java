package services;

import daos.*;
import models.*;
import models.types.AccountType;
import models.types.LoginMethodType;
import models.types.PortalApplicationType;
import org.pac4j.core.profile.CommonProfile;
import views.dto.PortalOptionsView;
import views.dto.SocialKeysView;

import javax.inject.Inject;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static models.types.LoginMethodType.Facebook;
import static models.types.LoginMethodType.Google;

public class PortalAndLoginOptionsService {

    @Inject
    PortalLoginConfigurationDAO loginConfigDAO;

    @Inject
    PortalDAO portalDAO;

    @Inject
    PortalAppDAO portalAppDAO;

    @Inject
    PortalNetworkConfigurationDAO networkConfigDAO;

    public SocialKeysView getLoginOptions(Long portalId) {
        Map<LoginMethodType, PortalLoginConfiguration> loginConfigurations = loginConfigDAO.findForPortal(portalId);
        Optional<LoginMethodType> loginMethod = networkConfigDAO.getLoginMethod(portalId);

        return loginConfigurations.isEmpty()
                ? SocialKeysView.initialize()
                : new SocialKeysView(loginConfigurations.get(Google), loginConfigurations.get(Facebook));
    }

    public void saveLoginOptions(SocialKeysView socialKeysView, Long portalId) {
        Portal portal = portalDAO.get(portalId);
        PortalLoginConfiguration google = socialKeysView.getGoogle();
        google.setKeysProvider();
        PortalLoginConfiguration facebook = socialKeysView.getFacebook();
        facebook.setKeysProvider();
        google.setPortal(portal);
        facebook.setPortal(portal);
        loginConfigDAO.saveOrUpdate(google);
        loginConfigDAO.saveOrUpdate(facebook);
        portalDAO.save(portal);
    }

    public PortalOptionsView getPortalOptions(Long portalId) {
        Portal portal = portalDAO.get(portalId);

        Map<PortalApplicationType, PortalApp> applications = portal.getApplicationsByType();
        PortalApp templateOne = applications.get(PortalApplicationType.TemplateOne);
        PortalApp templateTwo = applications.get(PortalApplicationType.TemplateTwo);
        PortalApp template = Optional.ofNullable(templateOne).orElse(Optional.ofNullable(templateTwo).orElse(null));
        PortalApp carousel = applications.get(PortalApplicationType.Carousel);
        Set<PortalNetworkConfiguration> networkConfigurations = portal.getNetworkConfigurations();

        return template == null || carousel == null
                ? PortalOptionsView.initialize(portalId, portal.getHomeURL(), portal.getName(), portal.getDescription())
                : new PortalOptionsView(
                portalId,
                portal.getHomeURL(),
                template.getType(),
                template, carousel,
                portal.getName(), 
                portal.getDescription(),
                networkConfigurations.stream().map(PortalNetworkConfiguration::getLoginMethod).collect(Collectors.toList()));
    }

    public Portal savePortalOptions(CommonProfile commonProfile, PortalOptionsView portalOptions, Long portalId, List<File> files) {
        Portal portal;
        if (portalId != null) {
            portal = portalDAO.get(portalId);
        } else {
            portal = new Portal();
            AdminDAO adminDAO = new AdminDAO();
            Admin admin = adminDAO.getByEmail(commonProfile.getId());
            portal.setOwner(admin);
            portal.setAccountType(AccountType.Basic);
        }
        portal.setHomeURL(portalOptions.getHomeURL());
        portal.setName(portalOptions.getName());
        portal.setDescription(portalOptions.getDescription());

        portalOptions.getTemplate().setPortal(portal);

        PortalApp carousel = PortalApp.carousel();
        carousel.setPortal(portal);
        carousel.setConfig(new PortalAppConfig(files));

        HashSet<PortalApp> portalApps = new HashSet<>();
        portalApps.add(portalOptions.getTemplate());
        portalApps.add(carousel);

        portal.setApplications(portalApps);

        Portal finalPortal = portal;
        List<PortalNetworkConfiguration> networkConfigurations = portalOptions.getLoginMethods().stream()
                .map(loginMethodType -> {
                            PortalNetworkConfiguration p = new PortalNetworkConfiguration(finalPortal);
                            p.setId(UUID.randomUUID().getMostSignificantBits());
                            p.setLoginMethod(loginMethodType);
                            return p;
                        }).collect(Collectors.toList());
        
        portal.setNetworkConfigurations(new HashSet<>(networkConfigurations));
        
        portalDAO.saveOrUpdate(portal);
        networkConfigDAO.saveAll(networkConfigurations);
        return portal;
    }
}
