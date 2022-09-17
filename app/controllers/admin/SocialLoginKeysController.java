package controllers.admin;

import com.typesafe.config.Config;
import controllers.WiFreeController;
import controllers.routes;
import models.PortalLoginConfiguration;
import play.data.Form;
import play.mvc.Result;
import services.PortalAndLoginOptionsService;
import views.dto.SocialKeysView;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

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

        List<String> errors = hasError(socialKeys.getFacebook(), "Facebook");
        errors.addAll(hasError(socialKeys.getGoogle(), "Google"));

        if (errors.isEmpty()) {
            portalAndLoginOptionsService.saveLoginOptions(socialKeys, portalId());
            flash("Success", "");
        } else {
            flash("Error", String.join("| ", errors));
        }

        return redirect(routes.AdminAppController.loginSettings());
    }

    private List<String> hasError(PortalLoginConfiguration p, String socialLogin) {
        List<String> errors = new ArrayList<>();
        if (p.isEnabled()) {
            if (p.getKeys().getClientId().isEmpty()) {
                errors.add("Por favor ingrese el 'clientId' provisto por " + socialLogin);
            }
            if (p.getKeys().getSecret().isEmpty()) {
                errors.add("Por favor ingrese el 'tokenId' provisto por " + socialLogin);
            }
        }
        return errors;
    }
}
