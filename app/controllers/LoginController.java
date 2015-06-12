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

/**
 * Enthält Funktionen zum Login und zu Funktionen, bei denen die Credentials
 * geprüft werden müssen
 * 
 * @author Hendrik Hagmans
 * 
 */
public class LoginController extends Controller {

	/**
	 * 
	 * @return Login View
	 */
	public static Result login() {
		return ok(login.render(session("username")));
	}

	/**
	 * Prüft die Credentials des Formvalues und loggt den User ein
	 * 
	 * @return Index View, wenn Credentials richtig, sonst wieder Login View
	 */
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

	/**
	 * Loggt den user aus
	 * 
	 * @return Login View
	 */
	public static Result logout(boolean userDelete) {
		session().clear();
		if (userDelete) {
			flash("success", "User erfolgreich gelöscht!");
		} else {
			flash("success", "User erfolgreich ausgeloggt!");
		}
		return redirect(controllers.routes.LoginController.login());
	}

	/**
	 * 
	 * @return Register View
	 */
	public static Result register() {
		return ok(register.render(session("username")));
	}

	/**
	 * Speichert die Regitrierung des Users mit den angegebenen Formvalues
	 * 
	 * @return Login View, wenn alles korrekt, ansonsten Register View
	 */
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

	/**
	 * Speichert die RegistrierungsId des Android Geräts des Nutzers
	 * 
	 * @param name
	 * @param password
	 * @param regId
	 * @return
	 */
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

	/**
	 * Gibt die RegistrierungsId des Android Geräts des Nutzers zurück
	 * 
	 * @param name
	 * @param password
	 * @return regId des Nutzers
	 */
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

	/**
	 * Prüfe die Credentials und return true wenn gültig
	 * 
	 * @param name
	 * @param password
	 * @return
	 */
	@Transactional
	public static Result checkLogin(String name, String password) {
		User user = User.findUser(name, User.decryptPassword(password));
		if (user != null) {
			return ok("true");
		} else {
			return ok("");
		}
	}

}
