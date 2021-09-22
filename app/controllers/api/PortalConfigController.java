package controllers.api;

import controllers.WiFreeController;
import controllers.api.dto.*;
import daos.PortalDAO;
import daos.PortalLoginConfigurationDAO;
import daos.SurveyDAO;
import models.Portal;
import models.PortalLoginConfiguration;
import models.Survey;
import models.types.LoginMethodType;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class PortalConfigController extends WiFreeController {

    @Inject
    private PortalDAO portalDAO;

    @Inject
    private SurveyDAO surveyDAO;
    
    @Inject
    private PortalLoginConfigurationDAO portalLoginConfigurationDAO;

    public Result clientLanding(Long portalId) {
        Portal portal = portalDAO.get(portalId);
        String uniqueId = UUID.randomUUID().toString(); // TODO hace falta?
        String title = portal.getName();
        String iframeURL = portal.getHomeURL(); // TODO implementar ABM
        LandingChoicesDTO landingChoices = new LandingChoicesDTO(title, iframeURL);
        String templateId = "template-2"; // TODO implementar ABM
        ClientLandingResponse clientLandingResponse = new ClientLandingResponse(uniqueId, landingChoices, portalId.toString(), templateId);
        return ok(clientLandingResponse.toJson());
    }
    
    public Result clientAuth(Long portalId) {
        Portal portal = portalDAO.get(portalId);

        String uniqueId = UUID.randomUUID().toString(); // TODO hace falta?
        LoginMethodType loginMethodType = portal.getNetworkConfiguration().getLoginMethod();
        String id = portalId.toString();

        LoginTypeOptionsDTO loginTypeOptions = null;
        if (loginMethodType == LoginMethodType.Survey) {
            Survey portalActiveSurvey = surveyDAO.findPortalActiveSurvey(portalId);
            SurveyFormDTO surveyForm = SurveyFormDTO.fromDomain(portalActiveSurvey);
            loginTypeOptions = new LoginTypeOptionsDTO(surveyForm);
        } else {
            Map<LoginMethodType, PortalLoginConfiguration> configs = portalLoginConfigurationDAO.findForPortal(portalId);
            SocialMediaKeysDTO facebook = new SocialMediaKeysDTO(SocialKeysDTO.fromDomain(configs.get(LoginMethodType.Facebook).getKeys()));
            SocialMediaKeysDTO google = new SocialMediaKeysDTO(SocialKeysDTO.fromDomain(configs.get(LoginMethodType.Google).getKeys()));
            loginTypeOptions = new LoginTypeOptionsDTO(Arrays.asList(facebook, google));
        }

        AuthDataDTO authData = new AuthDataDTO(uniqueId, loginMethodType.id, loginTypeOptions, portalId.toString());
        String name = portal.getName();
        ClientDataDTO clientData = new ClientDataDTO(portalId.toString(), name);
        ClientAuthResponse clientAuthResponse = new ClientAuthResponse(authData, clientData);
        return ok(clientAuthResponse.toJson());
    }

}
