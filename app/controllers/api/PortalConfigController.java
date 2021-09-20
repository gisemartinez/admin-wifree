package controllers.api;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.WiFreeController;
import controllers.api.dto.*;
import daos.PortalDAO;
import daos.SurveyDAO;
import models.Portal;
import models.Survey;
import play.libs.Json;
import play.mvc.Result;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Optional.ofNullable;

public class PortalConfigController extends WiFreeController {

    @Inject
    private PortalDAO portalDAO;

    @Inject
    private SurveyDAO surveyDAO;

    public Result getPortalConfig(Long portalId) {
        return ofNullable(portalDAO.get(portalId))
                .map(portal -> ok(getLoginConfigDTO(portalId, portal)))
                .orElse(badRequest("Portal [" + portalId + "] not found."));
    }

    public Result clientLanding(Long portalId) {
        String uniqueId = UUID.randomUUID().toString(); // TODO hace falta?
        String title = "Esto es un titulo de no se que"; // TODO implementar ABM
        String iframeURL = "https://www.refugiodelpescador.com/"; // TODO implementar ABM
        LandingChoicesDTO landingChoices = new LandingChoicesDTO(title, iframeURL);
        String templateId = "template-2"; // TODO implementar ABM
        ClientLandingResponse clientLandingResponse = new ClientLandingResponse(uniqueId, landingChoices, portalId.toString(), templateId);
        return ok(clientLandingResponse.toJson());
    }
    
    public Result clientAuth(Long portalId) {
        String uniqueId = UUID.randomUUID().toString(); // TODO hace falta?
        String loginType = "survey"; // TODO leer valor
        String id = "hotel-1"; // TODO id de encuesta o del portal/cliente?
        String title = "Queremos ofrecerte el mejor servicio pero para eso necesitamos conocer tu impresion de nuestras instalaciones. No te preocupes, es totalmente anonima"; // TODO leer apropiadamente

        LoginTypeOptionsDTO loginTypeOptions;
        if (loginType == "survey") {
            Survey portalActiveSurvey = surveyDAO.findPortalActiveSurvey(portalId);
            SurveyFormV2DTO surveyForm = SurveyFormV2DTO.fromDomain(portalActiveSurvey);
            loginTypeOptions = new LoginTypeOptionsDTO(surveyForm);
        } else {
            // TODO leer de db
            SocialKeys facebook = new SocialKeys("131065570894352", "a74151d55bae152570b3a0e8874086db");
            SocialKeys google = new SocialKeys("500143808314-9psv199snl4g7e6dargf6f8sog0023u1.apps.googleusercontent.com", "8oJHj4r0tCWBxJ_wTFNBOtD2");
            SocialMediaKeysDTO socialMediaKeys = new SocialMediaKeysDTO(facebook, google);
            loginTypeOptions = new LoginTypeOptionsDTO(socialMediaKeys);
        }

        AuthDataDTO authData = new AuthDataDTO(uniqueId, loginType, loginTypeOptions, portalId.toString());
        String name = "Example client. Fixed to look like a hotel"; // TODO implementar ABM
        ClientDataDTO clientData = new ClientDataDTO(portalId.toString(), name);
        ClientAuthResponse clientAuthResponse = new ClientAuthResponse(authData, clientData);
        return ok(clientAuthResponse.toJson());
    }

    private JsonNode getLoginConfigDTO(Long portalId, Portal portal) {
        if (portal.hasSocialLoginEnabled())
            return SocialLoginPortalConfigDTO.json(portal);
        else {
            Survey portalActiveSurvey = surveyDAO.findPortalActiveSurvey(portalId);
            return SurveyLoginPortalConfigDTO.json(portal, portalActiveSurvey);
        }
    }

}
