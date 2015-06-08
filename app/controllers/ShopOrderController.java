package controllers;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.ShopOrder;
import models.User;
import play.data.DynamicForm;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.createEditShopOrder;
import views.html.viewShopOrder;
import views.html.viewShopOrders;

/**
 * Enthält alle Funktionen zu ShopOrders
 * 
 * @author Hendrik Hagmans
 * 
 */
@Security.Authenticated(LoginSecured.class)
public class ShopOrderController extends Controller {

	/**
	 * 
	 * @return viewShopOrders View
	 */
	@Transactional
	public static Result viewShopOrders() {
		return ok(viewShopOrders.render(session("username"),
				JPA.em().find(User.class, session("username")).getShopOrders()));
	}

	/**
	 * viewShopOrder View
	 * 
	 * @param id
	 * @return
	 */
	@Transactional
	public static Result viewShopOrder(int id) {
		return ok(viewShopOrder.render(session("username"),
				JPA.em().find(ShopOrder.class, id)));
	}

	/**
	 * 
	 * @return createShopOrderView
	 */
	public static Result createShopOrder() {
		return ok(createEditShopOrder.render(session("username"), null));
	}

	/**
	 * 
	 * @param id
	 *            der ShopOrder
	 * @return editShopOrder View
	 */
	@Transactional
	public static Result editShopOrder(int id) {
		return ok(createEditShopOrder.render(session("username"), JPA.em()
				.find(ShopOrder.class, id)));
	}

	/**
	 * Erstellt die {@link ShopOrder} mit den übergebenen Formvalues
	 * 
	 * @return viewShopOrders View
	 */
	@Transactional
	public static Result createShopOrderSave() {
		DynamicForm bindedForm = form().bindFromRequest();

		String name = bindedForm.get("name");

		if (name == "") {
			flash("error", "Kein Name angegeben!");
			return ok(createEditShopOrder.render(session("username"), null));
		}

		String order = bindedForm.get("order");
		ArrayList<Integer> categories = new ArrayList<Integer>();
		if (order != "") { // Prüfen ob es Änderungen gibt
			List<String> orderArray = Arrays.asList(order.split("\\s*,\\s*"));
			int i = 0;
			for (String ord : orderArray) {
				categories.add(Integer.parseInt(ord));
			}
		}
		// Keine Änderungen gemacht
		else {
			for (int i = 0; i < 9; i++) {
				categories.add(i);
			}
		}

		ShopOrder shopOrder = new ShopOrder(name, categories);
		JPA.em().persist(shopOrder);
		User user = JPA.em().find(User.class, session("username"));
		user.addShopOrder(shopOrder);
		JPA.em().merge(user);

		flash("success", "Neue ShopOrder erfolgreich erstellt!");
		return redirect(controllers.routes.ShopOrderController.viewShopOrders());

	}

	/**
	 * Speichert die {@link ShopOrder} mit den übergebenen Formvalues
	 * 
	 * @return viewShopOrders View
	 */
	@Transactional
	public static Result editShopOrderSave(int id) {
		DynamicForm bindedForm = form().bindFromRequest();

		String name = bindedForm.get("name");

		if (name == "") {
			flash("error", "Kein Name angegeben!");
			return ok(createEditShopOrder.render(session("username"), JPA.em()
					.find(ShopOrder.class, id)));
		}

		String order = bindedForm.get("order");
		if (order != "") { // Prüfen ob Änderungen gemacht wurden
			List<String> orderArray = Arrays.asList(order.split("\\s*,\\s*"));
			ArrayList<Integer> categories = new ArrayList<Integer>();
			int i = 0;
			for (String ord : orderArray) {
				categories.add(Integer.parseInt(ord));
			}

			ShopOrder shopOrder = JPA.em().find(ShopOrder.class, id);
			shopOrder.setName(name);
			shopOrder.setCategories(categories);
			JPA.em().merge(shopOrder);
		}

		flash("success", "ShopOrder erfolgreich aktualisiert!");
		return redirect(controllers.routes.ShopOrderController.viewShopOrders());

	}

	/**
	 * Löscht die {@link ShopOrder}
	 * 
	 * @param id
	 *            der ShopOrder
	 * @return viewShopOrders View
	 */
	@Transactional
	public static Result deleteShopOrder(int id) {
		ShopOrder shopOrder = JPA.em().find(ShopOrder.class, id);
		User user = JPA.em().find(User.class, session("username"));
		user.getShopOrders().remove(shopOrder);
		JPA.em().merge(user);
		JPA.em().remove(shopOrder);
		flash("success", "ShopOrder erfolgreich gelöscht!");
		return redirect(controllers.routes.ShopOrderController.viewShopOrders());
	}
}
