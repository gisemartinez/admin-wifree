package services;

import daos.NetworkUserConnectionLogDAO;
import daos.PortalDAO;
import daos.PortalNetworkConfigurationDAO;
import models.NetworkUserConnectionLog;
import models.Portal;
import models.PortalNetworkConfiguration;
import models.types.LoginMethodType;
import operations.responses.DatasetFilter;
import views.dto.ConnectedUser;
import views.dto.ConnectionsPage;

import javax.inject.Inject;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class ConnectionsService {

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
		ArrayList<ConnectedUser> connectedUsers = connectedUsers();
		Optional<Integer> connectionTimeout = portalNetworkConfigurationDAO.getConnectionTimeout(portalId);
		return new ConnectionsPage(connectedUsers, connectionTimeout);
	}

	public PortalNetworkConfiguration networkConfiguration(Long portalId) {
		return portalNetworkConfigurationDAO.findForPortal(portalId);
	}

	public ArrayList<ConnectedUser> connectedUsers() {
		List<NetworkUserConnectionLog> logsLastFifteenMinutes = connectionLogDAO.findForFilter(DatasetFilter.usersConnectedLastFifteenMinutesFilter(1), Instant.now());

		List<ConnectedUser> connectedUsers = logsLastFifteenMinutes.stream()
				.sorted(Comparator.comparing(NetworkUserConnectionLog::getConnectionStartDate).reversed())
				.map(l -> {
					String formattedDate = l.getFormattedStartDate();
					return new ConnectedUser(l.getNetworkUser().getId(), l.getNetworkUser().getName(), formattedDate, l.getUserDeviceMACAddress());
				})
				.collect(Collectors.toList());

//		final ArrayList<ConnectedUser> connectedUsers = new ArrayList<>();
//		connectedUsers.add(new ConnectedUser(1, "Juan perez", new Date(), "iPhone X"));
//		connectedUsers.add(new ConnectedUser(2, "Roque Saenz Peña", new Date(), "Samsung S8+"));
		return (ArrayList) connectedUsers;
	}

}
