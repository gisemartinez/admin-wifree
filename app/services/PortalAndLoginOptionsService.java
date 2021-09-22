package services;

import daos.PortalAppDAO;
import daos.PortalDAO;
import daos.PortalLoginConfigurationDAO;
import models.Portal;
import models.PortalApp;
import models.PortalAppConfig;
import models.PortalLoginConfiguration;
import models.types.LoginMethodType;
import models.types.PortalApplicationType;
import views.dto.PortalOptionsView;
import views.dto.SocialKeysView;

import javax.inject.Inject;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static models.types.LoginMethodType.Facebook;
import static models.types.LoginMethodType.Google;

public class PortalAndLoginOptionsService {

    @Inject
    PortalLoginConfigurationDAO loginConfigDAO;

    @Inject
    PortalDAO portalDAO;

    @Inject
    PortalAppDAO portalAppDAO;

    public SocialKeysView getLoginOptions(Long portalId) {
        Map<LoginMethodType, PortalLoginConfiguration> loginConfigurations = loginConfigDAO.findForPortal(portalId);

        return loginConfigurations.isEmpty()
                ? SocialKeysView.initialize()
                : new SocialKeysView(loginConfigurations.get(Google), loginConfigurations.get(Facebook));
    }

    public void saveLoginOptions(SocialKeysView socialKeysView, Long portalId) {
        Portal portal = portalDAO.get(portalId);
        PortalLoginConfiguration google = socialKeysView.getGoogle();
        PortalLoginConfiguration facebook = socialKeysView.getFacebook();
        google.setPortal(portal);
        facebook.setPortal(portal);
        loginConfigDAO.saveOrUpdate(google);
        loginConfigDAO.saveOrUpdate(facebook);
    }

    public PortalOptionsView getPortalOptions(Long portalId) {
        Portal portal = portalDAO.get(portalId);

        Map<PortalApplicationType, PortalApp> applications = portal.getApplicationsByType();
        PortalApp templateOne = applications.get(PortalApplicationType.TemplateOne);
        PortalApp templateTwo = applications.get(PortalApplicationType.TemplateTwo);
        PortalApp template = Optional.ofNullable(templateOne).orElse(Optional.ofNullable(templateTwo).orElse(null));
        PortalApp carrousel = applications.get(PortalApplicationType.Carrousel);

        return template == null || carrousel == null
                ? PortalOptionsView.initialize(portalId, portal.getHomeURL())
                : new PortalOptionsView(portalId, portal.getHomeURL(), template.getType(), template, carrousel);
    }

    public void savePortalOptions(PortalOptionsView portalOptions, Long portalId, List<File> files) {
        Portal portal = portalDAO.get(portalId);

        portal.setHomeURL(portalOptions.getHomeURL());

        portalOptions.getTemplate().setPortal(portal);

        PortalApp carrousel = PortalApp.carrousel();
        carrousel.setPortal(portal);
        carrousel.setConfig(new PortalAppConfig(files));

        HashSet<PortalApp> portalApps = new HashSet<>();
        portalApps.add(portalOptions.getTemplate());
        portalApps.add(carrousel);

        portal.setApplications(portalApps);

        portalDAO.saveOrUpdate(portal);
    }
}
