package controllers;

import models.Article;
import models.ShoppingList;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class JsonController extends Controller {

	@Transactional
	public static Result getArticleAsJson(int id) {
		Article article = JPA.em().find(Article.class, id);
		if (article != null) {
			return ok(Json.toJson(article));
		} else {
			return notFound();
		}
	}

	@Transactional
	public static Result getCurrentShoppingListAsJson() {
		ShoppingList list = ShoppingList.getCurrentShoppingList();
		if (list != null) {
			return ok(Json.toJson(list));
		} else {
			return notFound();
		}
	}
}
