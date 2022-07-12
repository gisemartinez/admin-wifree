package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Portal;
import models.PortalNetworkConfiguration;
import models.types.LoginMethodType;
import org.pac4j.core.config.Config;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.play.PlayWebContext;
import org.pac4j.play.store.PlaySessionStore;
import play.Logger;
import play.data.FormFactory;
import play.mvc.Controller;
import play.twirl.api.Html;
import utils.DateHelper;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class WiFreeController extends Controller {

    @Inject
    protected FormFactory formFactory;

    @Inject
    protected Config config;

    @Inject
    protected PlaySessionStore playSessionStore;

    @Inject
    protected ObjectMapper objectMapper;

    protected final Logger.ALogger logger = Logger.of(this.getClass());

    protected Instant now() {
        return DateHelper.now();
    }

    protected Long portalId() {
        return getCurrentProfile().getAttribute("portal", Portal.class).getId();
    }

    protected void logRequest() {
        logger.info("*** Received request " + request().method() + " " + request().path() + " - Body: " + getRequestJsonString());
    }

    public List<CommonProfile> getProfiles() {
        final PlayWebContext context = new PlayWebContext(ctx(), playSessionStore);
        final ProfileManager<CommonProfile> profileManager = new ProfileManager(context);
        return profileManager.getAll(true);
    }

    protected CommonProfile getCurrentProfile() {
        final PlayWebContext context = new PlayWebContext(ctx(), playSessionStore);
        final ProfileManager<CommonProfile> profileManager = new ProfileManager<>(context);
        Optional<CommonProfile> currentProfile = profileManager.get(true);
        return currentProfile.orElseThrow(() -> new NoProfileFoundException("No profile in current session logged in. There should be a profile in session at this point."));
    }
    
    protected JsonNode getRequestJson() {
        return request().body().asJson();
    }

    protected String getRequestJsonString() {
        return Optional.ofNullable(getRequestJson()).map(JsonNode::toString).orElse("No body");
    }

    public static class NoProfileFoundException extends RuntimeException {
        NoProfileFoundException(String msg) {
            super(msg);
        }
    }

    /**
     * Compose with main view where navbar is handled
     */
    public Html render(Html content) {
        CommonProfile currentProfile = getCurrentProfile();
        Optional<Portal> portalOptional = Optional.ofNullable(currentProfile.getAttribute("portal", Portal.class));
        Html navbar;
        if (!portalOptional.isPresent()) {
             navbar = views.html.parts.side_navigation.apply(currentProfile, false, false, false);
        } else {
            Portal portal = portalOptional.get();

            Set<LoginMethodType> loginMethodTypes =
                    portal.getNetworkConfigurations().stream()
                    .map(n-> n.getLoginMethod())
                    .collect(Collectors.toSet());

            navbar = views.html.parts.side_navigation.apply(currentProfile,
                    true,
                    loginMethodTypes.stream().anyMatch(p -> p.id.equals(LoginMethodType.SocialLogin.id)),
                    loginMethodTypes.stream().anyMatch(p -> p.id.equals(LoginMethodType.Survey.id)));
        }

        return views.html.main.apply("Wifree", navbar, content);
    }
}
