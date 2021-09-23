package controllers.api;

import com.typesafe.config.Config;
import controllers.WiFreeController;
import controllers.api.dto.*;
import daos.PortalDAO;
import daos.PortalLoginConfigurationDAO;
import daos.SurveyDAO;
import models.Portal;
import models.PortalApp;
import models.PortalLoginConfiguration;
import models.Survey;
import models.types.LoginMethodType;
import models.types.PortalApplicationType;
import play.mvc.Result;

import javax.inject.Inject;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
        LoginMethodType loginMethodType = portal.getNetworkConfiguration().getLoginMethod();
        String id = portalId.toString();
        Map<PortalApplicationType, PortalApp> appsByType = portal.getApplicationsByType();
        List<String> images = appsByType.get(PortalApplicationType.Carrousel).getConfig().getImages().stream()
                .map(File::getName)
                .collect(Collectors.toList());

        LoginTypeOptionsDTO loginTypeOptions = null;
        if (loginMethodType == LoginMethodType.Survey) {
            Survey portalActiveSurvey = surveyDAO.findPortalActiveSurvey(portalId);
            SurveyFormDTO surveyForm = SurveyFormDTO.fromDomain(portalActiveSurvey);
            loginTypeOptions = new LoginTypeOptionsDTO(surveyForm, images);
        } else {
            List<PortalLoginConfiguration> configs = portalLoginConfigurationDAO.findEnabled(portalId);
            List<SocialMediaKeysDTO> socialMediaKeys = configs.stream()
                    .map(PortalLoginConfiguration::getKeys)
                    .map(SocialKeysDTO::fromDomain)
                    .map(SocialMediaKeysDTO::new)
                    .collect(Collectors.toList());
            loginTypeOptions = new LoginTypeOptionsDTO(socialMediaKeys, images);
        }

        AuthDataDTO authData = new AuthDataDTO(uniqueId, loginMethodType.id, loginTypeOptions, portalId.toString());
        String name = portal.getName();
        ClientDataDTO clientData = new ClientDataDTO(portalId.toString(), name);
        ClientAuthResponse clientAuthResponse = new ClientAuthResponse(authData, clientData);
        return ok(clientAuthResponse.toJson());
    }

}
