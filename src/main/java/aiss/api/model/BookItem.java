package aiss.api.model;

public class BookItem {
	private Book book;
	private Double price;
	
	public static BookItem of(Book book, Double price) {
		return new BookItem(book, price);
	}
	
	private BookItem(Book book, Double price) {
		super();
		this.book = book;
		this.price = price;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}
}
