package controllers.api;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.WiFreeController;
import controllers.api.dto.NetworkUserDTO;
import controllers.api.dto.UserAuthenticatedDTO;
import daos.NetworkUserConnectionLogDAO;
import daos.NetworkUserDAO;
import daos.PortalDAO;
import models.NetworkUser;
import models.NetworkUserConnectionLog;
import models.Portal;
import models.types.Gender;
import play.mvc.Result;
import utils.DateHelper;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class SocialUserController extends WiFreeController {

    @Inject
    private NetworkUserDAO networkUserDAO;

    @Inject
    private PortalDAO portalDAO;

    @Inject
    private NetworkUserConnectionLogDAO connectionLogDAO;

    public Result saveUserConnected() throws IOException {
        logRequest();

        JsonNode json = request().body().asJson();
        UserAuthenticatedDTO userDTO = objectMapper.readValue(json.toString(), UserAuthenticatedDTO.class);

        Portal portal = portalDAO.get(userDTO.getPortalId());

        NetworkUser user = networkUserDAO.findByEmail(userDTO.getEmail());
        if (user == null) {
            user = new NetworkUser(portal, userDTO.getName(), userDTO.getEmail(),
                    "", userDTO.getConnectionTime(), true, "",
                    Optional.ofNullable(userDTO.getGender()).map(Gender::valueOf).orElse(Gender.Undefined),
                    null, null,
                    null,
                    Optional.ofNullable(userDTO.getAge()).orElse(0));
        } else {
            user.setLastConnection(userDTO.getConnectionTime());
        }
        networkUserDAO.save(user);

        NetworkUserConnectionLog connectionLog = new NetworkUserConnectionLog(portal, user, userDTO.getConnectionTime(), Instant.now(), "");
        connectionLogDAO.save(connectionLog);

        return ok(userDTO.toJson());
    }

    public Result getSocialUser(String email) {
        return ofNullable(networkUserDAO.findByEmail(email))
                .map(networkUser -> ok(NetworkUserDTO.json(networkUser)))
                .orElse(badRequest("User [" + email + "] not found."));
    }

    public Result saveSocialUser() {
        JsonNode bodyJson = request().body().asJson();

        Long portalId = bodyJson.findValue("portalId").asLong();
        JsonNode names = bodyJson.withArray("names");
        JsonNode nameNode = names.get(0);
        String name = nameNode.findValue("displayName").asText();

        JsonNode genders = bodyJson.withArray("genders");
        JsonNode genderNode = genders.get(0);
        String gender = genderNode.findValue("formattedValue").asText();

        JsonNode birthdays = bodyJson.withArray("birthdays");
        JsonNode birthdayNode = birthdays.get(0);
        JsonNode date = birthdayNode.findValue("date");
        int year = date.findValue("year").asInt();
        int month = date.findValue("month").asInt();
        int day = date.findValue("day").asInt();

        JsonNode emailAddresses = bodyJson.withArray("emailAddresses");
        JsonNode emailAddressNode = emailAddresses.get(0);
        String email = emailAddressNode.findValue("value").asText();

        NetworkUserDAO networkUserDAO = new NetworkUserDAO();
        NetworkUser networkUser = networkUserDAO.findByEmail(email);
        if (networkUser == null) {
            Portal portal = new PortalDAO().get(portalId);
            int age = createAge(year, month, day);
            networkUser = new NetworkUser(
                    portal,
                    name,
                    email,
                    null,
                    Instant.now(),
                    true,
                    "hola123",
                    Gender.valueOf(gender),
                    null,
                    null,
                    null,
                    age
            );
            networkUserDAO.save(networkUser);
        }

        return ok(networkUser.toLogString());
    }

    private int createAge(int year, int month, int day) {
        return (int) DateHelper.yearsBetween(
                Instant.now(),
                Instant.parse(year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day) + "T00:00:00.000Z")
        );
    }

}
