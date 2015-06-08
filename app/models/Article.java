package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import play.db.jpa.JPA;

@Entity
public class Article {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String name;
	private Category category;
	private boolean checked = false;

	public Article() {

	}

	public Article(String name, Category category) {
		this.name = name;
		this.category = category;
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

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public boolean isChecked() {
		return checked;
	}

	public void checkArticle() {
		this.checked = true;
	}

	public void uncheckArticle() {
		this.checked = false;
	}

	public void toggleArticle() {
		if (this.checked)
			this.uncheckArticle();
		else
			this.checkArticle();
	}

	public static Article getArticleById(int id) {
		return JPA.em().find(Article.class, id);
	}
}
