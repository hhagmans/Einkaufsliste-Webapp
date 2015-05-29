package controllers;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.ShopOrder;
import play.data.DynamicForm;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.createEditShopOrder;
import views.html.viewShopOrder;
import views.html.viewShopOrders;

public class ShopOrderController extends Controller {

	@Transactional
	public static Result viewShopOrders() {
		return ok(viewShopOrders.render(JPA.em()
				.createQuery("Select s from ShopOrder s", ShopOrder.class)
				.getResultList()));
	}

	@Transactional
	public static Result viewShopOrder(int id) {
		return ok(viewShopOrder.render(JPA.em().find(ShopOrder.class, id)));
	}

	public static Result createShopOrder() {
		return ok(createEditShopOrder.render(null));
	}

	@Transactional
	public static Result editShopOrder(int id) {
		return ok(createEditShopOrder
				.render(JPA.em().find(ShopOrder.class, id)));
	}

	@Transactional
	public static Result createShopOrderSave() {
		DynamicForm bindedForm = form().bindFromRequest();

		String name = bindedForm.get("name");
		String order = bindedForm.get("order");
		System.out.println(order);
		List<String> orderArray = Arrays.asList(order.split("\\s*,\\s*"));
		ArrayList<Integer> categories = new ArrayList<Integer>();
		int i = 0;
		for (String ord : orderArray) {
			System.out.println(ord);
			categories.add(Integer.parseInt(ord));
		}

		ShopOrder shopOrder = new ShopOrder(name, categories);
		JPA.em().persist(shopOrder);

		flash("success", "Neue ShopOrder erfolgreich erstellt!");
		return redirect(controllers.routes.ShopOrderController.viewShopOrders());

	}

	@Transactional
	public static Result editShopOrderSave(int id) {
		DynamicForm bindedForm = form().bindFromRequest();

		String name = bindedForm.get("name");
		String order = bindedForm.get("order");
		System.out.println(order);
		List<String> orderArray = Arrays.asList(order.split("\\s*,\\s*"));
		ArrayList<Integer> categories = new ArrayList<Integer>();
		int i = 0;
		for (String ord : orderArray) {
			System.out.println(ord);
			categories.add(Integer.parseInt(ord));
		}

		ShopOrder shopOrder = JPA.em().find(ShopOrder.class, id);
		shopOrder.setName(name);
		shopOrder.setCategories(categories);
		JPA.em().merge(shopOrder);

		flash("success", "ShopOrder erfolgreich aktualisiert!");
		return redirect(controllers.routes.ShopOrderController.viewShopOrders());

	}

	@Transactional
	public static Result deleteShopOrder(int id) {
		JPA.em().remove(JPA.em().find(ShopOrder.class, id));
		flash("success", "ShopOrder erfolgreich gelöscht!");
		return redirect(controllers.routes.ShopOrderController.viewShopOrders());
	}
}
