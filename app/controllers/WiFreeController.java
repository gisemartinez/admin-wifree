package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pac4j.core.config.Config;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.play.PlayWebContext;
import org.pac4j.play.store.PlaySessionStore;
import play.Logger;
import play.data.FormFactory;
import play.mvc.Controller;
import utils.DateHelper;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Optional;

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
		return (Long) getCurrentProfile().getAttribute("portal");
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
		return getRequestJson().toString();
	}

	public static class NoProfileFoundException extends RuntimeException {
		NoProfileFoundException(String msg) {
			super(msg);
		}
	}

}
