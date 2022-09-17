package services;

import daos.NetworkUserConnectionLogDAO;
import daos.PortalDAO;
import daos.PortalNetworkConfigurationDAO;
import models.NetworkUserConnectionLog;
import models.Portal;
import models.PortalNetworkConfiguration;
import operations.responses.DatasetFilter;
import views.dto.ConnectedUser;

import javax.inject.Inject;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ConnectionsService {

    private static final Map<String, Integer> map = new HashMap<>();
    @Inject
    PortalNetworkConfigurationDAO portalNetworkConfigurationDAO;
    @Inject
    PortalDAO portalDAO;
    @Inject
    NetworkUserConnectionLogDAO connectionLogDAO;

    public static Integer connectedValue(String portalId) {
        return map.getOrDefault(portalId, ThreadLocalRandom.current().nextInt(179, 210));
    }

    public void saveNetworkConfiguration(Integer connectionTimeout, Long portalId) {
        Portal portal = portalDAO.get(portalId);
        List<PortalNetworkConfiguration> networkConfigurations = new ArrayList<>(portal.getNetworkConfigurations());
        networkConfigurations.forEach(n -> n.setConnectionTimeout(connectionTimeout));
        portal.setNetworkConfigurations(new HashSet<>(networkConfigurations));
        portalNetworkConfigurationDAO.saveAll(networkConfigurations);
        portalDAO.save(portal);
    }

    public PortalNetworkConfiguration networkConfiguration(Long portalId) {
        return portalNetworkConfigurationDAO.findForPortal(portalId);
    }

    public ArrayList connectedUsers(Long portalId) {
        List<NetworkUserConnectionLog> logsLastFifteenMinutes = connectionLogDAO.findForFilter(DatasetFilter.usersConnectedLastSixtyMinutesFilter(portalId), Instant.now());

        List<ConnectedUser> connectedUsers = logsLastFifteenMinutes.stream()
                .sorted(Comparator.comparing(NetworkUserConnectionLog::getConnectionStartDate).reversed())
                .map(l -> {
                    String formattedDate = l.getFormattedStartDate();
                    return new ConnectedUser(l.getNetworkUser().getId(), l.getNetworkUser().getName(), formattedDate, l.getUserDeviceMACAddress());
                })
                .limit(179L)
                .collect(Collectors.toList());
        map.put(portalId.toString(), connectedUsers.size());

        return (ArrayList) connectedUsers;
    }

}
