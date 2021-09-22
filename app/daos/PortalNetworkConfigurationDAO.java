package daos;

import models.PortalNetworkConfiguration;
import models.types.LoginMethodType;

import javax.inject.Inject;
import java.util.Optional;

import static io.ebean.Expr.eq;
import static java.util.Optional.ofNullable;

public class PortalNetworkConfigurationDAO extends GenericDAO<PortalNetworkConfiguration> {

	@Inject
	private PortalDAO portalDAO;

	public PortalNetworkConfigurationDAO() {
		super(PortalNetworkConfiguration.class);
	}

	public Optional<Integer> getConnectionTimeout(Long portalId) {
		return ofNullable(find(eq("portal.id", portalId)))
				.map(PortalNetworkConfiguration::getConnectionTimeout);
	}

	public Optional<LoginMethodType> getLoginMethod(Long portalId) {
		return ofNullable(findForPortal(portalId))
				.map(PortalNetworkConfiguration::getLoginMethod);
	}

	public PortalNetworkConfiguration findForPortal(Long portalId) {
		return find(eq("portal.id", portalId));
	}

	@Override
	public PortalNetworkConfiguration getOrCreate(Long portalId) {
		PortalNetworkConfiguration portalNetworkConfiguration = super.getOrCreate(portalId);
		if (!portalNetworkConfiguration.hasPortal()) portalNetworkConfiguration.setPortal(portalDAO.get(portalId));
		return portalNetworkConfiguration;
	}
}
