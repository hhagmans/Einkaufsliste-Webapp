package controllers;

import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class LoginSecured extends Security.Authenticator {

	@Override
	public Result onUnauthorized(Context ctx) {
		return redirect(routes.LoginController.login());
	}
}
