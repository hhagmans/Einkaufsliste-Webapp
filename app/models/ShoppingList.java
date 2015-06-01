package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.db.jpa.JPA;

@Entity
public class ShoppingList {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Temporal(TemporalType.DATE)
	private Date date;

	@OneToOne
	private ShopOrder shopOrder = null;

	@OneToMany(cascade = CascadeType.REMOVE)
	@JoinTable(name = "ShoppingList_Articles", joinColumns = { @JoinColumn(name = "ShoppingList_Id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "Article_Id", referencedColumnName = "id", unique = true) })
	private List<Article> articles = new ArrayList<Article>();;

	public ShoppingList() {

	}

	public ShoppingList(Date date) {
		this.date = date;
	}

	public ShoppingList(Date date, List<Article> articles, ShopOrder shopOrder) {
		this.date = date;
		this.articles = articles;
		this.shopOrder = shopOrder;
	}

	public static List<ShoppingList> getCurrentShoppingLists() {
		List<ShoppingList> currentLists = new ArrayList<ShoppingList>();
		List<ShoppingList> lists = JPA
				.em()
				.createQuery("Select s from ShoppingList s", ShoppingList.class)
				.getResultList();
		for (ShoppingList list : lists) {
			Calendar calList = Calendar.getInstance();
			Calendar cal = Calendar.getInstance();
			calList.setTime(list.getDate());
			cal.setTime(new Date());
			if (calList.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
				if (calList.get(Calendar.DAY_OF_YEAR) >= cal
						.get(Calendar.DAY_OF_YEAR)) {
					currentLists.add(list);
				}
			} else if (calList.get(Calendar.YEAR) >= cal.get(Calendar.YEAR)) {
				currentLists.add(list);
			}
		}
		return currentLists;
	}

	public static ShoppingList getCurrentShoppingList() {
		List<ShoppingList> lists = JPA
				.em()
				.createQuery("Select s from ShoppingList s", ShoppingList.class)
				.getResultList();
		for (ShoppingList list : lists) {
			Calendar calList = Calendar.getInstance();
			Calendar cal = Calendar.getInstance();
			calList.setTime(list.getDate());
			cal.setTime(new Date());
			if (calList.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
					&& calList.get(Calendar.DAY_OF_YEAR) == cal
							.get(Calendar.DAY_OF_YEAR)) {
				return list;
			}

		}
		return null;
	}

	public static ShoppingList createhoppingList(Date date) {
		if (!shoppingListExistsAtDate(date)) {
			ShoppingList list = new ShoppingList(date);
			JPA.em().persist(list);
			return list;
		}
		return null;
	}

	public static boolean shoppingListExistsAtDate(Date date) {
		List<ShoppingList> lists = JPA
				.em()
				.createQuery("Select s from ShoppingList s", ShoppingList.class)
				.getResultList();
		for (ShoppingList list : lists) {
			Calendar calList = Calendar.getInstance();
			Calendar cal = Calendar.getInstance();
			calList.setTime(list.getDate());
			cal.setTime(date);
			if (calList.get(Calendar.DAY_OF_YEAR) == cal
					.get(Calendar.DAY_OF_YEAR)
					&& calList.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
				return true;
			}
		}
		return false;
	}

	public int getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public ShopOrder getShopOrder() {
		return shopOrder;
	}

	public String getShopOrderName() {
		if (shopOrder != null) {
			return shopOrder.getName();
		} else {
			return "Unsortiert";
		}
	}

	public void setShopOrder(ShopOrder shopOrder) {
		this.shopOrder = shopOrder;
	}

	public List<Article> getArticles() {
		if (shopOrder != null) {
			ArrayList<Article> fleischArticles = new ArrayList<Article>();
			ArrayList<Article> gemuseArticles = new ArrayList<Article>();
			ArrayList<Article> kochenArticles = new ArrayList<Article>();
			ArrayList<Article> milchArticles = new ArrayList<Article>();
			ArrayList<Article> tiefkuhlArticles = new ArrayList<Article>();
			ArrayList<Article> getraenkeArticles = new ArrayList<Article>();
			ArrayList<Article> sussArticles = new ArrayList<Article>();
			ArrayList<Article> haushaltArticles = new ArrayList<Article>();
			ArrayList<Article> sonstigesArticles = new ArrayList<Article>();

			for (Article article : this.articles) {
				if (article.getCategory().equals(Category.FLEISCHFISCH)) {
					fleischArticles.add(article);
				} else if (article.getCategory().equals(Category.GEMUESEOBST)) {
					gemuseArticles.add(article);
				} else if (article.getCategory().equals(Category.KOCHENBACKEN)) {
					kochenArticles.add(article);
				} else if (article.getCategory().equals(Category.MILCHPRODUKTE)) {
					milchArticles.add(article);
				} else if (article.getCategory().equals(
						Category.TIEFKUEHLPRODUKTE)) {
					tiefkuhlArticles.add(article);
				} else if (article.getCategory().equals(Category.GETRAENKE)) {
					getraenkeArticles.add(article);
				} else if (article.getCategory().equals(Category.SUESSIGKEITEN)) {
					sussArticles.add(article);
				} else if (article.getCategory().equals(Category.HAUSHALT)) {
					haushaltArticles.add(article);
				} else if (article.getCategory().equals(Category.SONSTIGES)) {
					sonstigesArticles.add(article);
				}
			}

			ArrayList<Article> articles = new ArrayList<Article>();
			for (Category category : shopOrder.getCategoriesAsCategory()) {
				if (category.equals(Category.FLEISCHFISCH)) {
					articles.addAll(fleischArticles);
				} else if (category.equals(Category.GEMUESEOBST)) {
					articles.addAll(gemuseArticles);
				} else if (category.equals(Category.KOCHENBACKEN)) {
					articles.addAll(kochenArticles);
				} else if (category.equals(Category.MILCHPRODUKTE)) {
					articles.addAll(milchArticles);
				} else if (category.equals(Category.TIEFKUEHLPRODUKTE)) {
					articles.addAll(tiefkuhlArticles);
				} else if (category.equals(Category.GETRAENKE)) {
					articles.addAll(getraenkeArticles);
				} else if (category.equals(Category.SUESSIGKEITEN)) {
					articles.addAll(sussArticles);
				} else if (category.equals(Category.HAUSHALT)) {
					articles.addAll(haushaltArticles);
				} else if (category.equals(Category.SONSTIGES)) {
					articles.addAll(sonstigesArticles);
				}
			}
			return articles;
		} else {
			return this.articles;
		}
	}

	public List<Article> getArticlesUnsorted() {
		return articles;
	}

	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}

	public boolean containsArticle(String name, Category category) {
		boolean contains = false;
		for (Article article : getArticles()) {
			if (article.getName().equals(name)
					&& article.getCategory().equals(category)) {
				contains = true;
				break;
			}
		}
		return contains;
	}

	public Article getArticle(String name, Category category) {
		Article returnarticle = null;
		for (Article article : getArticles()) {
			if (article.getName().equals(name)
					&& article.getCategory().equals(category)) {
				returnarticle = article;
				break;
			}
		}
		return returnarticle;
	}

	public void addArticle(Article article) {
		this.articles.add(article);
	}

	public void addArticles(List<Article> articles) {
		for (Article article : articles) {
			addArticle(article);
		}
	}
}
