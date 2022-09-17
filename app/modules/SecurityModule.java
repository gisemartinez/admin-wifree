package modules;

import auth.AuthConstants;
import auth.WiFreeAdminAuthenticator;
import be.objectify.deadbolt.java.cache.HandlerCache;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.typesafe.config.Config;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.direct.AnonymousClient;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.play.CallbackController;
import org.pac4j.play.LogoutController;
import org.pac4j.play.deadbolt2.Pac4jHandlerCache;
import org.pac4j.play.deadbolt2.Pac4jRoleHandler;
import org.pac4j.play.store.PlayCacheSessionStore;
import org.pac4j.play.store.PlaySessionStore;
import play.Environment;
import play.cache.SyncCacheApi;

/**
 * Created by jesu on 11/07/17.
 */
public class SecurityModule extends AbstractModule {

    private final String baseUrl;

    public SecurityModule(final Environment environment, final Config configuration) {
        baseUrl = configuration.getString("baseUrl");
    }

    @Override
    protected void configure() {
        bind(HandlerCache.class).to(Pac4jHandlerCache.class);

        bind(Pac4jRoleHandler.class).to(WiFreePac4jRoleHandler.class);
        final PlayCacheSessionStore playCacheSessionStore = new PlayCacheSessionStore(getProvider(SyncCacheApi.class));
        //bind(PlaySessionStore.class).toInstance(playCacheSessionStore);
        bind(PlaySessionStore.class).to(PlayCacheSessionStore.class);

        // callback
        final CallbackController callbackController = new CallbackController();
        callbackController.setDefaultUrl("/");
        callbackController.setMultiProfile(true);
        bind(CallbackController.class).toInstance(callbackController);

        // logout
        final LogoutController logoutController = new LogoutController();
        logoutController.setDefaultUrl(AuthConstants.DEFAULT_LOGOUT_URL);
        //logoutController.setDestroySession(true);
        bind(LogoutController.class).toInstance(logoutController);
    }

    @Provides
    protected FormClient adminFormClient() {
        //adminLoginClient.setName("AdminClient");
        return new FormClient(baseUrl + AuthConstants.ADMIN_LOGIN_URL, new WiFreeAdminAuthenticator());
    }

    @Provides
    protected org.pac4j.core.config.Config provideConfig(FormClient adminLoginClient) {
        final Clients clients = new Clients(baseUrl + AuthConstants.CALLBACK_URL, adminLoginClient, new AnonymousClient());

        final org.pac4j.core.config.Config config = new org.pac4j.core.config.Config(clients);
        config.addAuthorizer("admin", new RequireAnyRoleAuthorizer<>("ROLE_ADMIN"));
        config.addAuthorizer("custom", new CustomAuthorizer());
//		config.addMatcher("excludedPath", new PathMatcher().excludeRegex("^/facebook/notprotected\\.html$"));
        config.setHttpActionAdapter(new DemoHttpActionAdapter());

        return config;
    }

    private static class WiFreePac4jRoleHandler implements Pac4jRoleHandler {
    }

}