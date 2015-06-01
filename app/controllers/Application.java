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
import play.data.DynamicForm;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.createEditShoppingList;
import views.html.index;
import views.html.viewShoppingList;

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
		return ok(index.render(ShoppingList.getCurrentShoppingLists()));
	}

	@Transactional
	public static Result viewShoppingList(int id) {
		return ok(viewShoppingList
				.render(JPA.em().find(ShoppingList.class, id)));
	}

	@Transactional
	public static Result createShoppingList() {
		return ok(createEditShoppingList.render(
				null,
				JPA.em()
						.createQuery("Select s from ShopOrder s",
								ShopOrder.class).getResultList()));
	}

	@Transactional
	public static Result createShoppingListSave() {
		DynamicForm bindedForm = form().bindFromRequest();
		Date date = null;
		try {
			date = getDateFromForm(bindedForm.get("date"));
		} catch (ParseException e) {
			flash("error", "Datum nicht im richtigen Format");
			return ok(createEditShoppingList.render(
					null,
					JPA.em()
							.createQuery("Select s from ShopOrder s",
									ShopOrder.class).getResultList()));
		}
		if (isDateInPast(date)) {
			flash("error", "Das gewählte Datum ist in der Vergangenheit");
			return ok(createEditShoppingList.render(
					null,
					JPA.em()
							.createQuery("Select s from ShopOrder s",
									ShopOrder.class).getResultList()));
		} else if (ShoppingList.shoppingListExistsAtDate(date)) {
			flash("error",
					"Es existiert bereits eine Einkaufsliste an diesem Tag");
			return ok(createEditShoppingList.render(
					null,
					JPA.em()
							.createQuery("Select s from ShopOrder s",
									ShopOrder.class).getResultList()));
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
			return ok(createEditShoppingList.render(
					null,
					JPA.em()
							.createQuery("Select s from ShopOrder s",
									ShopOrder.class).getResultList()));
		} else {
			ShoppingList list = ShoppingList.createhoppingList(date);
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
		return ok(createEditShoppingList.render(
				JPA.em().find(ShoppingList.class, id),
				JPA.em()
						.createQuery("Select s from ShopOrder s",
								ShopOrder.class).getResultList()));
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
			return ok(createEditShoppingList.render(
					list,
					JPA.em()
							.createQuery("Select s from ShopOrder s",
									ShopOrder.class).getResultList()));
		}
		if (isDateInPast(date)) {
			flash("error", "Das gewählte Datum ist in der Vergangenheit");
			return ok(createEditShoppingList.render(
					list,
					JPA.em()
							.createQuery("Select s from ShopOrder s",
									ShopOrder.class).getResultList()));
		} else if (!list.getDate().equals(date)
				&& ShoppingList.shoppingListExistsAtDate(date)) {
			flash("error",
					"Es existiert bereits eine Einkaufsliste an diesem Tag");
			return ok(createEditShoppingList.render(
					list,
					JPA.em()
							.createQuery("Select s from ShopOrder s",
									ShopOrder.class).getResultList()));
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
			return ok(createEditShoppingList.render(
					list,
					JPA.em()
							.createQuery("Select s from ShopOrder s",
									ShopOrder.class).getResultList()));
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
			flash("success", "Einkaufsliste erfolgreich bearbeitet!");
			return redirect(controllers.routes.Application.index());
		}
	}

	@Transactional
	public static Result deleteShoppingList(int id) {
		JPA.em().remove(JPA.em().find(ShoppingList.class, id));
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
