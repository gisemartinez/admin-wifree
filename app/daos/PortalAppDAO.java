package daos;

import models.PortalApp;
import models.types.PortalApplicationType;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.ebean.Expr.eq;

public class PortalAppDAO extends GenericDAO<PortalApp> {

    public PortalAppDAO() {
        super(PortalApp.class);
    }

    public Map<PortalApplicationType, PortalApp> findForPortal(Long portalId) {
        return listWhere(eq("portal.id", portalId))
                .stream()
                .collect(Collectors.toMap(PortalApp::getType, Function.identity()));
    }

}
