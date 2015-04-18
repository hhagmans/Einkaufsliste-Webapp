package controllers;

import java.util.ArrayList;

import models.ShoppingList;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.createShoppingList;

public class Application extends Controller {

	public static Result index() {
		return ok(index.render(new ArrayList<ShoppingList>()));
	}

	public static Result createShoppingList() {
		return ok(createShoppingList.render());
	}

	public static Result createShoppingListSave() {
		return ok(index.render(new ArrayList<ShoppingList>()));
	}

}
