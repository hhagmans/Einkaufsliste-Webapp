package controllers;

import models.Article;
import models.ShoppingList;
import models.User;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Enthält alle Funktionen zur Json Schnittstelle
 * 
 * @author Hendrik Hagmans
 * 
 */
public class JsonController extends Controller {

	/**
	 * Prüft die Credentials und returned den {@link Article} als Json
	 * 
	 * @param id
	 * @param name
	 * @param password
	 * @return Artikel als Json
	 */
	@Transactional
	public static Result getArticleAsJson(int id, String name, String password) {
		User user = User.findUser(name, User.decryptPassword(password));
		if (user != null) {
			Article article = JPA.em().find(Article.class, id);
			if (article != null) {
				return ok(Json.toJson(article));
			} else {
				return notFound();
			}
		} else {
			return unauthorized();
		}
	}

	/**
	 * Prüft die Credentials und returned die aktuelle {@link ShoppingList} als
	 * Json
	 * 
	 * @param name
	 * @param password
	 * @return
	 */
	@Transactional
	public static Result getCurrentShoppingListAsJson(String name,
			String password) {
		User user = User.findUser(name, User.decryptPassword(password));
		if (user != null) {
			ShoppingList list = ShoppingList.getCurrentShoppingList(user
					.getName());
			if (list != null) {
				return ok(Json.toJson(list.getArticles().toArray()));
			} else {
				return notFound();
			}
		} else {
			return unauthorized();
		}
	}

	/**
	 * Prüft die Credentials und checked den {@link Article}
	 * 
	 * @param id
	 * @param name
	 * @param password
	 * @return
	 */
	@Transactional
	public static Result checkArticle(int id, String name, String password) {
		User user = User.findUser(name, User.decryptPassword(password));
		Article article = JPA.em().find(Article.class, id);
		if (article != null && user != null) {
			article.checkArticle();
			JPA.em().merge(article);
		}
		return redirect(controllers.routes.Application.index());
	}

	/**
	 * Prüft die Credentials und unchecked den {@link Article}
	 * 
	 * @param id
	 * @param name
	 * @param password
	 * @return
	 */
	@Transactional
	public static Result uncheckArticle(int id, String name, String password) {
		User user = User.findUser(name, User.decryptPassword(password));
		Article article = JPA.em().find(Article.class, id);
		if (article != null && user != null) {
			article.uncheckArticle();
			JPA.em().merge(article);
		}
		return redirect(controllers.routes.Application.index());
	}
}
