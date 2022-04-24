package controllers.api;

import com.typesafe.config.Config;
import controllers.WiFreeController;
import controllers.api.dto.*;
import daos.PortalDAO;
import daos.PortalLoginConfigurationDAO;
import daos.SurveyDAO;
import models.*;
import models.types.LoginMethodType;
import models.types.PortalApplicationType;
import play.mvc.Result;

import javax.inject.Inject;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class PortalConfigController extends WiFreeController {

    @Inject
    private PortalDAO portalDAO;

    @Inject
    private SurveyDAO surveyDAO;

    @Inject
    private PortalLoginConfigurationDAO portalLoginConfigurationDAO;

    @Inject
    Config config;

    public Result clientLanding(Long portalId) {
        logRequest();

        Portal portal = portalDAO.get(portalId);
        String uniqueId = UUID.randomUUID().toString(); // TODO hace falta?
        String title = portal.getName();
        String iframeURL = portal.getHomeURL();
        LandingChoicesDTO landingChoices = new LandingChoicesDTO(title, iframeURL);
        String templateId = "template-2"; // TODO implementar ABM
        ClientLandingResponse clientLandingResponse = new ClientLandingResponse(uniqueId, landingChoices, portalId.toString(), templateId);
        return ok(clientLandingResponse.toJson());
    }

    public Result clientAuth(Long portalId) {
        logRequest();

        Portal portal = portalDAO.get(portalId);

        String uniqueId = UUID.randomUUID().toString(); // TODO hace falta?
        List<LoginMethodType> loginMethodTypes = portal.getNetworkConfigurations().stream()
                .map(PortalNetworkConfiguration::getLoginMethod)
                .collect(Collectors.toList());
        String id = portalId.toString();
        Map<PortalApplicationType, PortalApp> appsByType = portal.getApplicationsByType();
        List<String> images = appsByType.get(PortalApplicationType.Carousel).getConfig().getImages().stream()
                .map(File::getName)
                .collect(Collectors.toList());

        List<LoginTypeOptionsDTO> loginTypeOptions = new ArrayList<>();

        if (loginMethodTypes.contains(LoginMethodType.Survey)) {
            Optional<Survey> portalActiveSurvey = Optional.ofNullable(surveyDAO.findPortalActiveSurvey(portalId));
            if (portalActiveSurvey.isPresent()) {
                SurveyFormDTO surveyForm = SurveyFormDTO.fromDomain(portalActiveSurvey.get());
                loginTypeOptions.add(new LoginTypeOptionsDTO(surveyForm));
            }
        }
        if (loginMethodTypes.contains(LoginMethodType.SocialLogin)) {
            List<PortalLoginConfiguration> configs = portalLoginConfigurationDAO.findEnabled(portalId);
            List<SocialMediaKeysDTO> socialMediaKeys = configs.stream()
                    .map(PortalLoginConfiguration::getKeys)
                    .map(SocialKeysDTO::fromDomain)
                    .map(SocialMediaKeysDTO::new)
                    .collect(Collectors.toList());
            loginTypeOptions.add(new LoginTypeOptionsDTO(socialMediaKeys));
        }

        AuthDataDTO authData = new AuthDataDTO(uniqueId,
                loginMethodTypes.stream().map(l -> l.id).collect(Collectors.toList()), 
                loginTypeOptions, 
                portalId.toString());
        String name = portal.getName();
        ClientDataDTO clientData = new ClientDataDTO(portalId.toString(), name, portal.getDescription());
        ClientAuthResponse clientAuthResponse = new ClientAuthResponse(authData, clientData, new CarouselDataDTO(images));
        return ok(clientAuthResponse.toJson());
    }

}
