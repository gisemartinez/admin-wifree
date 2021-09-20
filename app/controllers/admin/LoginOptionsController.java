package controllers.admin;

import controllers.WiFreeController;
import controllers.routes;
import play.data.Form;
import play.mvc.Result;
import services.LoginOptionsService;
import views.dto.SocialKeysView;

import javax.inject.Inject;

public class LoginOptionsController extends WiFreeController {

    @Inject
    LoginOptionsService loginOptionsService;

    public Result saveSocialKeys() {
        final Form<SocialKeysView> form = formFactory.form(SocialKeysView.class);
        final SocialKeysView socialKeys = form.bindFromRequest().get();
        loginOptionsService.saveLoginOptions(socialKeys, portalId());
        return redirect(routes.AdminAppController.loginSettings());
    }

}
