package views.dto;

import models.PortalLoginConfiguration;
import models.types.LoginMethodType;

public class SocialKeysView {
    private PortalLoginConfiguration google;
    private PortalLoginConfiguration facebook;

    public SocialKeysView() {
    }

    public SocialKeysView(PortalLoginConfiguration google, PortalLoginConfiguration facebook) {
        this.google = google;
        this.facebook = facebook;
    }

    public static SocialKeysView initialize() {
        PortalLoginConfiguration google = new PortalLoginConfiguration(null, LoginMethodType.Google, true, false, null, null);
        PortalLoginConfiguration facebook = new PortalLoginConfiguration(null, LoginMethodType.Facebook, true, false, null, null);
        return new SocialKeysView(google, facebook);
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
}
