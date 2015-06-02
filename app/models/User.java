package models;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.db.jpa.JPA;

@Entity
@Table(name = "\"User\"")
public class User {

	@Id
	private String name;

	private String password;

	private String regId = null;

	@OneToMany(cascade = CascadeType.REMOVE)
	@JoinTable(name = "User_ShoppingList", joinColumns = { @JoinColumn(name = "User_Id", referencedColumnName = "name") }, inverseJoinColumns = { @JoinColumn(name = "ShoppingList_Id", referencedColumnName = "id", unique = true) })
	private List<ShoppingList> shoppingLists = new ArrayList<ShoppingList>();

	@OneToMany(cascade = CascadeType.REMOVE)
	@JoinTable(name = "User_ShopOrder", joinColumns = { @JoinColumn(name = "User_Id", referencedColumnName = "name") }, inverseJoinColumns = { @JoinColumn(name = "ShopOrder_Id", referencedColumnName = "id", unique = true) })
	private List<ShopOrder> shopOrders = new ArrayList<ShopOrder>();

	private User() {
	}

	private User(String name, String password) {
		this.name = name;
		this.password = password;
	}

	private User(String name, String password, String regId) {
		this.name = name;
		this.password = password;
		this.regId = regId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public List<ShoppingList> getShoppingLists() {
		return shoppingLists;
	}

	public void setShoppingLists(List<ShoppingList> shoppingLists) {
		this.shoppingLists = shoppingLists;
	}

	public void addShoppingList(ShoppingList list) {
		this.shoppingLists.add(list);
	}

	public List<ShopOrder> getShopOrders() {
		return shopOrders;
	}

	public void setShopOrders(List<ShopOrder> shopOrders) {
		this.shopOrders = shopOrders;
	}

	public void addShopOrder(ShopOrder shopOrder) {
		this.shopOrders.add(shopOrder);
	}

	public static String byte2HexStr(byte binary) {
		StringBuffer sb = new StringBuffer();
		int hex;

		hex = (int) binary & 0x000000ff;
		if (0 != (hex & 0xfffffff0)) {
			sb.append(Integer.toHexString(hex));
		} else {
			sb.append("0" + Integer.toHexString(hex));
		}
		return sb.toString();
	}

	/**
	 * Verschlüsselt das angegebene Passwort
	 * 
	 * @param password
	 * @return
	 */
	public static String encryptPassword(String password) {
		DESKeySpec dk;
		SecretKey secretKey = null;
		try {
			dk = new DESKeySpec(new Long(7490854493772951678L).toString()
					.getBytes());
			SecretKeyFactory kf = SecretKeyFactory.getInstance("DES");
			secretKey = kf.generateSecret(dk);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Cipher c;

		try {
			c = Cipher.getInstance("DES/ECB/PKCS5Padding");
			c.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] encrypted;
			encrypted = c.doFinal(password.getBytes());

			// convert into hexadecimal number, and return as character string.
			String result = "";
			for (int i = 0; i < encrypted.length; i++) {
				result += byte2HexStr(encrypted[i]);
			}

			return result;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Entschlüsselt das von encryptPassword verschlüsselte Passwort.
	 * 
	 * @param password
	 * @return
	 */
	public static String decryptPassword(String password) {
		DESKeySpec dk;
		SecretKey secretKey = null;
		try {
			dk = new DESKeySpec(new Long(7490854493772951678L).toString()
					.getBytes());
			SecretKeyFactory kf = SecretKeyFactory.getInstance("DES");
			secretKey = kf.generateSecret(dk);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Cipher c;

		try {
			byte[] tmp = new byte[password.length() / 2];
			int index = 0;
			while (index < password.length()) {
				// convert hexadecimal number into decimal number.
				int num = Integer.parseInt(
						password.substring(index, index + 2), 16);

				// convert into signed byte.
				if (num < 128) {
					tmp[index / 2] = new Byte(Integer.toString(num))
							.byteValue();
				} else {
					tmp[index / 2] = new Byte(
							Integer.toString(((num ^ 255) + 1) * -1))
							.byteValue();
				}
				index += 2;
			}

			c = Cipher.getInstance("DES/ECB/PKCS5Padding");
			c.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(c.doFinal(tmp));
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

		return "";
	}

	public static User createUser(String name, String password) {
		User user = JPA.em().find(User.class, name);

		if (user == null) {
			user = new User(name, encryptPassword(password));
			JPA.em().persist(user);
			return user;
		} else {
			return null;
		}
	}

	public static User findUser(String name, String password) {
		User user = JPA.em().find(User.class, name);

		if (user != null
				&& user.getPassword().equals(encryptPassword(password))) {
			return user;
		} else {
			return null;
		}
	}
}
