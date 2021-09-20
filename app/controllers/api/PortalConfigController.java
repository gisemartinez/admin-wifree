package controllers.api;

import controllers.WiFreeController;
import controllers.api.dto.*;
import daos.PortalDAO;
import daos.SurveyDAO;
import models.Survey;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.UUID;

public class PortalConfigController extends WiFreeController {

    @Inject
    private PortalDAO portalDAO;

    @Inject
    private SurveyDAO surveyDAO;

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
        if ("survey".equals(loginType)) {
            Survey portalActiveSurvey = surveyDAO.findPortalActiveSurvey(portalId);
            SurveyFormDTO surveyForm = SurveyFormDTO.fromDomain(portalActiveSurvey);
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

}
