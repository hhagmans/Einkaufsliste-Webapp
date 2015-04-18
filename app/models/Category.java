package models;

public enum Category {

	DELIVERY("DELIVERY"), DRIVE("DRIVE"), WALK("WALK");

	private String categoryName;

	Category(String categoryName) {
		this.categoryName = categoryName;
	}

	@Override
	public String toString() {
		return categoryName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

}
