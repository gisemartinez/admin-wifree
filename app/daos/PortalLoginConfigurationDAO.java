package daos;

import models.PortalLoginConfiguration;
import models.types.LoginMethodType;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.ebean.Expr.eq;

public class PortalLoginConfigurationDAO extends GenericDAO<PortalLoginConfiguration> {

    public PortalLoginConfigurationDAO() {
        super(PortalLoginConfiguration.class);
    }

    public Map<LoginMethodType, PortalLoginConfiguration> findForPortal(Long portalId) {
        return listWhere(eq("portal.id", portalId))
                .stream()
                .collect(Collectors.toMap(PortalLoginConfiguration::getLoginMethod, Function.identity()));
    }

}
