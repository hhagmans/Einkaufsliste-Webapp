package controllers;

import static play.data.Form.form;
import models.User;
import play.data.DynamicForm;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.login;
import views.html.register;

public class LoginController extends Controller {

	public static Result login() {
		return ok(login.render(session("username")));
	}

	@Transactional
	public static Result doLogin() {
		DynamicForm bindedForm = form().bindFromRequest();

		String name = bindedForm.get("name");
		String password = bindedForm.get("password");

		if (User.findUser(name, password) != null) {
			session("username", name);
			flash("success", "Erfolgreich eingeloggt!");
			return redirect(controllers.routes.Application.index());
		} else {
			flash("error", "Falscher Username/Passwort");
			return redirect(controllers.routes.LoginController.login());
		}
	}

	public static Result logout() {
		session().clear();
		return redirect(controllers.routes.LoginController.login());
	}

	public static Result register() {
		return ok(register.render(session("username")));
	}

	@Transactional
	public static Result registerSave() {
		DynamicForm bindedForm = form().bindFromRequest();

		String name = bindedForm.get("name");
		String password = bindedForm.get("password");

		if (User.createUser(name, password) != null) {
			flash("success", "Erfolgreich registriert!");
			return redirect(controllers.routes.LoginController.login());
		} else {
			flash("error", "Diesen User gibt es schon");
			return redirect(controllers.routes.LoginController.register());
		}
	}

	@Transactional
	public static Result setRegId(String name, String password, String regId) {
		User user = User.findUser(name, User.decryptPassword(password));
		if (user != null) {
			user.setRegId(regId);
			JPA.em().merge(user);
			return redirect(controllers.routes.Application.index());
		} else {
			return unauthorized();
		}
	}

	@Transactional
	public static Result getRegId(String name, String password) {
		User user = User.findUser(name, User.decryptPassword(password));
		if (user != null) {
			if (user.getRegId() != null) {
				return ok(user.getRegId());
			} else {
				return notFound();
			}
		} else {
			return unauthorized();
		}
	}
}
