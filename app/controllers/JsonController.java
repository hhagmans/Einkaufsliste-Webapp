package controllers;

import models.Article;
import models.ShoppingList;
import models.User;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class JsonController extends Controller {

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
