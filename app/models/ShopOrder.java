package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Stellt die Sortierung der {@link Category} einer ShoppingList dar
 * 
 * @author Hendrik Hagmans
 * 
 */
@Entity
public class ShopOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	private String name;

	@ElementCollection
	private List<Integer> categories = new ArrayList<Integer>();

	public ShopOrder() {

	}

	public ShopOrder(String name, ArrayList<Integer> categories) {
		this.name = name;
		this.categories = categories;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Integer> getCategories() {
		return categories;
	}

	public Category[] getCategoriesAsCategory() {
		Category[] categories = new Category[10];
		for (int i = 0; i < this.categories.size(); i++) {
			categories[i] = Category.getCategoryByIndex(this.categories.get(i));
		}
		return categories;
	}

	public void setCategories(ArrayList<Integer> categories) {
		this.categories = categories;
	}

}
