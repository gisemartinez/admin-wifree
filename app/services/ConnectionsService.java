package services;

import daos.NetworkUserConnectionLogDAO;
import daos.PortalDAO;
import daos.PortalNetworkConfigurationDAO;
import models.NetworkUserConnectionLog;
import models.Portal;
import models.PortalNetworkConfiguration;
import operations.responses.DatasetFilter;
import views.dto.ConnectedUser;
import views.dto.ConnectionsPage;

import javax.inject.Inject;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ConnectionsService {

	private static final Map<String, Integer> map = new HashMap<>();

	public static Integer connectedValue(String portalId) {
		return map.getOrDefault(portalId, ThreadLocalRandom.current().nextInt(179, 210));
	}

	@Inject
	PortalNetworkConfigurationDAO portalNetworkConfigurationDAO;

	@Inject
	PortalDAO portalDAO;

	@Inject
	NetworkUserConnectionLogDAO connectionLogDAO;
	
	public void saveNetworkConfiguration(Integer connectionTimeout, Long portalId) {
		Portal portal = portalDAO.get(portalId);
		PortalNetworkConfiguration networkConfiguration = portal.getNetworkConfiguration();
		if (networkConfiguration == null) {
			networkConfiguration = new PortalNetworkConfiguration(portal);
		}
		networkConfiguration.setConnectionTimeout(connectionTimeout);
		portal.setNetworkConfiguration(networkConfiguration);
		portalNetworkConfigurationDAO.save(networkConfiguration);
		portalDAO.save(portal);
	}

	public ConnectionsPage connectionsPage(Long portalId) {
		ArrayList<ConnectedUser> connectedUsers = connectedUsers(portalId);
		Optional<Integer> connectionTimeout = portalNetworkConfigurationDAO.getConnectionTimeout(portalId);
		return new ConnectionsPage(connectedUsers, connectionTimeout);
	}

	public PortalNetworkConfiguration networkConfiguration(Long portalId) {
		return portalNetworkConfigurationDAO.findForPortal(portalId);
	}

	public ArrayList<ConnectedUser> connectedUsers(Long portalId) {
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
