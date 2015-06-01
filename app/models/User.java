package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.jpa.JPA;

@Entity
@Table(name = "\"User\"")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	private String name;

	private String regId = null;

	private User() {
	}

	private User(String name) {
		this.name = name;
	}

	private User(String name, String regId) {
		this.name = name;
		this.regId = regId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public int getId() {
		return id;
	}

	public static User createUser(String name) {
		List<User> users = JPA.em()
				.createQuery("Select u from User u", User.class)
				.getResultList();
		boolean found = false;
		User newUser = null;
		for (User user : users) {
			if (user.getName().equals(name)) {
				found = true;
				newUser = user;
				break;
			}
		}
		if (!found) {
			newUser = new User(name);
			JPA.em().persist(newUser);
		}
		return newUser;
	}
}
