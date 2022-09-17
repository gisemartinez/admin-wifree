package controllers;

import org.pac4j.http.client.indirect.FormClient;
import play.mvc.Result;

import java.util.Optional;

/**
 * Created by jesu on 27/06/17.
 */
public class LoginController extends WiFreeController {

    public Result login() {
        final FormClient formClient = (FormClient) config.getClients().findClient("FormClient");
        if (Optional.ofNullable(request().getQueryString("error")).isPresent()) {
            flash(request().getQueryString("error"), "Por favor, ingrese campos válidos para iniciar sesión");
        }
        return ok(views.html.admin.login.render(formClient.getCallbackUrl()));
    }
}
