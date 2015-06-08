package controllers;

import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Prüft ob der Nutzer angemeldet ist und leitet sonst auf die Login Seite um
 * 
 * @author Hendrik Hagmans
 * 
 */
public class LoginSecured extends Security.Authenticator {

	@Override
	public Result onUnauthorized(Context ctx) {
		return redirect(routes.LoginController.login());
	}
}
