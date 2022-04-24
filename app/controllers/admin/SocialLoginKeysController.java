package controllers.admin;

import com.typesafe.config.Config;
import controllers.WiFreeController;
import controllers.routes;
import play.data.Form;
import play.mvc.Http;
import play.mvc.Result;
import services.PortalAndLoginOptionsService;
import views.dto.PortalOptionsView;
import views.dto.SocialKeysView;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SocialLoginKeysController extends WiFreeController {

    @Inject
    PortalAndLoginOptionsService portalAndLoginOptionsService;

    @Inject
    Config config;

    public Result results() {
        return ok(render(views.html.admin.collectedSocialData.render()));
    }

    public Result saveSocialKeys() {
        final Form<SocialKeysView> form = formFactory.form(SocialKeysView.class);
        final SocialKeysView socialKeys = form.bindFromRequest().get();
        portalAndLoginOptionsService.saveLoginOptions(socialKeys, portalId());
        return redirect(routes.AdminAppController.loginSettings());
    }
}
