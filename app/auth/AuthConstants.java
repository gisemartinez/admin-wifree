package auth;

import utils.CollectionHelper;

import java.util.Map;

public class AuthConstants {

	public static final String ADMIN_LOGIN_URL = "/login";
	public static final String CONSOLE_LOGIN_URL = "/console/login";
	public static final String CALLBACK_URL = "/callback";
	public static final String DEFAULT_LOGOUT_URL = "/dashboard";

	public static final String[] CONSOLE_USERS = new String[] { "jesu772@gmail.com", "mcruz342@gmail.com", "nicohuvi@gmail.com", "gise.cpna@gmail.com" };
	private static final String[] CONSOLE_PASSWORDS = new String[] {"wifreeConsole-jesu", "wifreeConsole-mauri", "wifreeConsole-nico", "wifreeConsole-gise"};
	public static final Map<String, String> CONSOLE_CREDENTIALS = CollectionHelper.initializeMap(CONSOLE_USERS, CONSOLE_PASSWORDS);

}
