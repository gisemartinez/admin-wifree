package views.dto;

import models.PortalLoginConfiguration;
import models.types.LoginMethodType;

public class SocialKeysView {

    private LoginMethodType loginMethod;
    private PortalLoginConfiguration google;
    private PortalLoginConfiguration facebook;

    public SocialKeysView() {}

    public SocialKeysView(LoginMethodType loginMethod, PortalLoginConfiguration google, PortalLoginConfiguration facebook) {
        this.loginMethod = loginMethod;
        this.google = google;
        this.facebook = facebook;
    }

    public static SocialKeysView initialize() {
        PortalLoginConfiguration google = new PortalLoginConfiguration(null, LoginMethodType.Google, true, true, null, null);
        PortalLoginConfiguration facebook = new PortalLoginConfiguration(null, LoginMethodType.Facebook, true, true, null, null);
        return new SocialKeysView(LoginMethodType.SocialLogin, google, facebook);
    }

    public PortalLoginConfiguration getGoogle() {
        return google;
    }

    public void setGoogle(PortalLoginConfiguration google) {
        this.google = google;
    }

    public PortalLoginConfiguration getFacebook() {
        return facebook;
    }

    public void setFacebook(PortalLoginConfiguration facebook) {
        this.facebook = facebook;
    }

    public LoginMethodType getLoginMethod() {
        return loginMethod;
    }

    public void setLoginMethod(LoginMethodType loginMethod) {
        this.loginMethod = loginMethod;
    }
}
