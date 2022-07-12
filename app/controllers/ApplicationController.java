package controllers;

import models.PortalNetworkConfiguration;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.play.PlayWebContext;
import org.pac4j.play.java.Secure;
import org.pac4j.play.store.PlaySessionStore;
import play.data.Form;
import play.mvc.Result;
import services.ConnectionsService;
import views.dto.ConnectedUser;

import javax.inject.Inject;
import java.util.List;

public class ApplicationController extends WiFreeController {

	@Inject
	private Config config;

	@Inject
	private PlaySessionStore playSessionStore;

	@Inject
	private ConnectionsService connectionsService;
	
	@Secure(clients = "AnonymousClient", authorizers = "csrfToken")
	public Result index() {
		logger.debug("Entering index()...");
		final PlayWebContext context = new PlayWebContext(ctx(), playSessionStore);
		final String sessionId = context.getSessionIdentifier();
		final String token = (String) context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN);
		logger.debug("Finishing index()...");
		return ok(views.html.index.render(getProfiles(), token, sessionId));
	}
}
