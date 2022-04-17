package auth;

import daos.AdminDAO;
import daos.AdminNotFoundException;
import models.Admin;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.AccountNotFoundException;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;

import static play.mvc.Controller.flash;

public class WiFreeAdminAuthenticator implements Authenticator<UsernamePasswordCredentials> {

	private final AdminDAO adminDAO = new AdminDAO();

	@Override
	public void validate(UsernamePasswordCredentials credentials, WebContext context) throws HttpAction, CredentialsException {
		if (credentials == null) {
			throwsException("No hay credenciales");
		}
		String email = credentials.getUsername();
		String password = credentials.getPassword();
		if (CommonHelper.isBlank(email)) {
			throwsException("El nombre de usuario no puede ser vacío");
		}
		if (CommonHelper.isBlank(password)) {
			throwsException("La contraseña no puede ser vacía");
		}

		// TODO: define appropiate validation method
		String dbPassword = null;
		try {
			dbPassword = getPasswordFor(email);
		} catch (AdminNotFoundException e) {
			throwsException("No se encontró usuario con nombre: " + email);
		}
		if (CommonHelper.areNotEquals(password, dbPassword)) {
			throwsException("Contraseña incorrecta para el usuario '" + email + "'");
		}

		final CommonProfile profile = new CommonProfile();
        Admin admin = adminDAO.getByEmail(email);
        String realName = admin.getName();
		profile.setId(email);
		profile.addAttribute(Pac4jConstants.USERNAME, email);
		profile.addAttribute("portal", admin.getPortal());
        profile.addAttribute("realName", realName);
		credentials.setUserProfile(profile);
	}

	protected void throwsException(final String message) throws CredentialsException {
		throw new CredentialsException(message);
	}

	private String getPasswordFor(String username) throws AdminNotFoundException {
		return adminDAO.getPasswordForUser(username);
	}
}
