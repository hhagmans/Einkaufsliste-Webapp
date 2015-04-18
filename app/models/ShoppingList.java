package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class ShoppingList {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Temporal(TemporalType.DATE)
	private Date date;

	@OneToMany
	@JoinTable(name = "ShoppingList_Articles", joinColumns = { @JoinColumn(name = "ShoppingList_Id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "Article_Id", referencedColumnName = "id", unique = true) })
	private List<Article> articles = new ArrayList<Article>();;

	private ShoppingList() {

	}

	private ShoppingList(Date date) {
		this.date = date;
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

	public List<Article> getArticles() {
		return articles;
	}

	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}
}
