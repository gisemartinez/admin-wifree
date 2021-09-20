package services;

import daos.PortalDAO;
import daos.PortalLoginConfigurationDAO;
import models.Portal;
import models.PortalLoginConfiguration;
import models.types.LoginMethodType;
import views.dto.SocialKeysView;

import javax.inject.Inject;
import java.util.Map;

import static models.types.LoginMethodType.Facebook;
import static models.types.LoginMethodType.Google;

public class LoginOptionsService {

    @Inject
    PortalLoginConfigurationDAO loginConfigDAO;

    @Inject
    PortalDAO portalDAO;

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

}
