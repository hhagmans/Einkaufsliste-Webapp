package models;

public enum Category {

	FLEISCHFISCH("Fleisch und Fisch"), GEMUESEOBST("Gemüse und Obst"), KOCHENBACKEN(
			"Kochen und Backen"), MILCHPRODUKTE("Milchprodukte"), TIEFKUEHLPRODUKTE(
			"Tiefkühlprodukte"), GETRAENKE("Getränke"), SUESSIGKEITEN(
			"Süßigkeiten"), HAUSHALT("Haushalt"), SONSTIGES("Sonstiges");

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

	public static Category getCategoryByIndex(int index) {
		Category categories[] = new Category[] { Category.FLEISCHFISCH,
				Category.GEMUESEOBST, Category.KOCHENBACKEN,
				Category.MILCHPRODUKTE, Category.TIEFKUEHLPRODUKTE,
				Category.GETRAENKE, Category.SUESSIGKEITEN, Category.HAUSHALT,
				Category.SONSTIGES };
		return categories[index];
	}
}
