package controllers.admin;

import controllers.WiFreeController;
import core.constants.ConnectionsConstants;
import models.PortalNetworkConfiguration;
import play.data.Form;
import play.mvc.Result;
import services.ConnectionsService;

import javax.inject.Inject;

public class ConnectionsController extends WiFreeController implements ConnectionsConstants {

	@Inject
	private ConnectionsService connectionsService;

	public Result setConnectionTimeout() {
		final Form<PortalNetworkConfiguration> form = formFactory.form(PortalNetworkConfiguration.class);
		final PortalNetworkConfiguration portalNetworkConfiguration = form.bindFromRequest().get();
		connectionsService.saveNetworkConfiguration(portalNetworkConfiguration.getConnectionTimeout(), portalNetworkConfiguration.getLoginMethod(), portalId());
		return redirect(controllers.routes.AdminAppController.connections()); // with something
	}

}
