package controllers;

import static play.data.Form.form;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.Article;
import models.Category;
import models.ShopOrder;
import models.ShoppingList;
import models.User;

import org.whispersystems.gcm.server.Message;
import org.whispersystems.gcm.server.Sender;

import play.data.DynamicForm;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.createEditShoppingList;
import views.html.index;
import views.html.viewShoppingList;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Security.Authenticated(LoginSecured.class)
public class Application extends Controller {

	/**
	 * Validiert das übergebenen Formvalue und parsed ein @Date aus dem
	 * übergebenen Formvalue.
	 * 
	 * @param formValue
	 * @return
	 * @throws ParseException
	 */
	public static Date getDateFromForm(String formValue) throws ParseException {
		Date date;
		if (formValue == "") {
			date = null;
		} else {
			if (!formValue.matches("\\d{2}.\\d{2}.\\d{4}")) {
				throw new ParseException(
						" Startdate does not match the correct regex.", 370);
			}
			date = new SimpleDateFormat("dd.MM.yyyy").parse(formValue);
		}
		return date;
	}

	public static boolean isDateInPast(Date date) {
		Calendar calCurrent = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		calCurrent.setTime(new Date());
		cal.setTime(date);
		if (calCurrent.get(Calendar.YEAR) > cal.get(Calendar.YEAR)) {
			return true;
		} else if (calCurrent.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
			if (calCurrent.get(Calendar.DAY_OF_YEAR) > cal
					.get(Calendar.DAY_OF_YEAR)) {
				return true;
			}
		}
		return false;
	}

	private static Category getCategoryFromForm(String trim) {
		Category category = null;
		if (trim.contains("0")) {
			category = Category.FLEISCHFISCH;
		} else if (trim.contains("1")) {
			category = Category.GEMUESEOBST;
		} else if (trim.contains("2")) {
			category = Category.KOCHENBACKEN;
		} else if (trim.contains("3")) {
			category = Category.MILCHPRODUKTE;
		} else if (trim.contains("4")) {
			category = Category.TIEFKUEHLPRODUKTE;
		} else if (trim.contains("5")) {
			category = Category.GETRAENKE;
		} else if (trim.contains("6")) {
			category = Category.SUESSIGKEITEN;
		} else if (trim.contains("7")) {
			category = Category.HAUSHALT;
		} else if (trim.contains("8")) {
			category = Category.SONSTIGES;
		}
		return category;
	}

	@Transactional
	public static Result index() {
		return ok(index.render(session("username"),
				ShoppingList.getCurrentShoppingLists(session("username"))));
	}

	@Transactional
	public static Result viewShoppingList(int id) {
		return ok(viewShoppingList.render(session("username"),
				JPA.em().find(ShoppingList.class, id)));
	}

	@Transactional
	public static Result createShoppingList() {
		return ok(createEditShoppingList.render(session("username"), null, JPA
				.em().find(User.class, session("username")).getShopOrders()));
	}

	@Transactional
	public static Result createShoppingListSave() {
		DynamicForm bindedForm = form().bindFromRequest();
		Date date = null;
		try {
			date = getDateFromForm(bindedForm.get("date"));
		} catch (ParseException e) {
			flash("error", "Datum nicht im richtigen Format");
			return ok(createEditShoppingList.render(session("username"), null,
					JPA.em().find(User.class, session("username"))
							.getShopOrders()));
		}
		if (isDateInPast(date)) {
			flash("error", "Das gewählte Datum ist in der Vergangenheit");
			return ok(createEditShoppingList.render(session("username"), null,
					JPA.em().find(User.class, session("username"))
							.getShopOrders()));
		} else if (ShoppingList.shoppingListExistsAtDate(date)) {
			flash("error",
					"Es existiert bereits eine Einkaufsliste an diesem Tag");
			return ok(createEditShoppingList.render(session("username"), null,
					JPA.em().find(User.class, session("username"))
							.getShopOrders()));
		}

		ShopOrder shopOrder = JPA.em().find(ShopOrder.class,
				Integer.parseInt(bindedForm.get("shopOrder")));

		int i = 0;
		List<Article> articles = new ArrayList<Article>();
		while (true) {
			if (bindedForm.get("article" + i) == null) {
				break;
			} else if (bindedForm.get("article" + i) == "") {

			} else {
				String name = bindedForm.get("article" + i);
				Category category = getCategoryFromForm(bindedForm
						.get("category" + i));
				Article article = new Article(name, category);
				JPA.em().persist(article);
				articles.add(article);
			}

			i++;
		}

		if (date == null) {
			flash("error", "Datum nicht im richtigen Format");
			return ok(createEditShoppingList.render(session("username"), null,
					JPA.em().find(User.class, session("username"))
							.getShopOrders()));
		} else {
			ShoppingList list = ShoppingList.createhoppingList(date,
					session("username"));
			JPA.em().persist(list);
			list.setArticles(articles);
			if (shopOrder != null) {
				list.setShopOrder(shopOrder);
				shopOrder.setShoppingList(list);
				JPA.em().merge(list);
				JPA.em().merge(shopOrder);
			}
			flash("success", "Neue Einkaufsliste erfolgreich erstellt!");
			return redirect(controllers.routes.Application.index());
		}
	}

	@Transactional
	public static Result editShoppingList(int id) {
		return ok(createEditShoppingList.render(session("username"), JPA.em()
				.find(ShoppingList.class, id),
				JPA.em().find(User.class, session("username")).getShopOrders()));
	}

	@Transactional
	public static Result editShoppingListSave(int id) {
		ShoppingList list = JPA.em().find(ShoppingList.class, id);
		DynamicForm bindedForm = form().bindFromRequest();
		Date date = null;
		try {
			date = getDateFromForm(bindedForm.get("date"));
		} catch (ParseException e) {
			flash("error", "Datum nicht im richtigen Format");
			return ok(createEditShoppingList.render(session("username"), list,
					JPA.em().find(User.class, session("username"))
							.getShopOrders()));
		}
		if (isDateInPast(date)) {
			flash("error", "Das gewählte Datum ist in der Vergangenheit");
			return ok(createEditShoppingList.render(session("username"), list,
					JPA.em().find(User.class, session("username"))
							.getShopOrders()));
		} else if (!list.getDate().equals(date)
				&& ShoppingList.shoppingListExistsAtDate(date)) {
			flash("error",
					"Es existiert bereits eine Einkaufsliste an diesem Tag");
			return ok(createEditShoppingList.render(session("username"), list,
					JPA.em().find(User.class, session("username"))
							.getShopOrders()));
		}

		ShopOrder shopOrder = JPA.em().find(ShopOrder.class,
				Integer.parseInt(bindedForm.get("shopOrder")));

		int i = 0;
		List<Article> articles = new ArrayList<Article>();
		while (true) {
			if (bindedForm.get("article" + i) == null) {
				break;
			} else if (bindedForm.get("article" + i) == "") {

			} else {
				String name = bindedForm.get("article" + i);
				Category category = getCategoryFromForm(bindedForm
						.get("category" + i));
				String articleId = bindedForm.get("articleId" + i);
				if (articleId == null) {
					if (!list.containsArticle(name, category)) {
						Article article = new Article(name, category);
						JPA.em().persist(article);
						articles.add(article);
					}
				} else {
					Article oldArticle = JPA.em().find(Article.class,
							Integer.parseInt(articleId));
					oldArticle.setName(name);
					oldArticle.setCategory(category);
					JPA.em().merge(oldArticle);
				}
			}

			i++;
		}

		if (date == null) {
			flash("error", "Datum nicht im richtigen Format");
			return ok(createEditShoppingList.render(session("username"), list,
					JPA.em().find(User.class, session("username"))
							.getShopOrders()));
		} else {
			list.setDate(date);
			list.addArticles(articles);
			JPA.em().merge(list);
			if (shopOrder != null) {
				list.setShopOrder(shopOrder);
				shopOrder.setShoppingList(list);
				JPA.em().merge(list);
				JPA.em().merge(shopOrder);
			}

			sendMessageToAndroid(Json.toJson(list.getArticles()).asText());
			flash("success", "Einkaufsliste erfolgreich bearbeitet!");
			return redirect(controllers.routes.Application.index());
		}
	}

	public static void sendMessageToAndroid(String message) {
		if (JPA.em().find(User.class, session("username")).getRegId() != null) {
			String apiKey = "AIzaSyAQp3JsPuqziuRQvl-XvNzG7L52oBUvtPk";
			Sender sender = new Sender(apiKey);
			ListenableFuture<org.whispersystems.gcm.server.Result> future = sender
					.send(Message
							.newBuilder()
							.withDestination(
									User.createUser("test", "test").getRegId())
							.withDataPart("message", message).build());

			Futures.addCallback(future,
					new FutureCallback<org.whispersystems.gcm.server.Result>() {
						@Override
						public void onSuccess(
								org.whispersystems.gcm.server.Result result) {
							if (result.isSuccess()) {
								System.out.println(result.getMessageId());
							} else {
								System.out.println(result.getError());
							}
						}

						@Override
						public void onFailure(Throwable throwable) {
							System.out.println("FAIL");
						}
					});
		}
	}

	@Transactional
	public static Result deleteShoppingList(int id) {
		ShoppingList list = JPA.em().find(ShoppingList.class, id);
		User user = JPA.em().find(User.class, session("username"));
		user.getShopOrders().remove(list);
		JPA.em().merge(user);
		JPA.em().remove(list);
		flash("success", "Einkaufsliste erfolgreich gelöscht!");
		return redirect(controllers.routes.Application.index());
	}

	@Transactional
	public static Result deleteArticle(int listId, int articleId) {
		Article article = JPA.em().find(Article.class, articleId);
		ShoppingList list = JPA.em().find(ShoppingList.class, listId);
		list.getArticles().remove(article);
		JPA.em().merge(list);
		JPA.em().remove(article);
		sendMessageToAndroid(Json.toJson(list).asText());
		flash("success", "Artikel erfolgreich gelöscht!");
		return redirect(controllers.routes.Application.editShoppingList(list
				.getId()));
	}

	@Transactional
	public static Result checkArticle(int id) {
		Article article = JPA.em().find(Article.class, id);
		if (article != null) {
			article.checkArticle();
			JPA.em().merge(article);
		}
		return redirect(controllers.routes.Application.index());
	}

	@Transactional
	public static Result uncheckArticle(int id) {
		Article article = JPA.em().find(Article.class, id);
		if (article != null) {
			article.uncheckArticle();
			JPA.em().merge(article);
		}
		return redirect(controllers.routes.Application.index());
	}

}
