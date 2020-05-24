package aiss.api.model;

public class Book {
	private String isbn;
	private String title;
	private String author;
	private String publicationDate;
	
	public static Book of(String isbn, String title, String author, String publicationDate) {
		return new Book(isbn, title, author, publicationDate);
	}
	
	private Book(String isbn, String title, String author, String publicationDate) {
		super();
		this.isbn = isbn;
		this.title = title;
		this.author = author;
		this.publicationDate = publicationDate;
	}
	
	private Book() {
		super();
		this.isbn = null;
		this.title = null;
		this.author = null;
		this.publicationDate = null;
	}

	public String getIsbn() {
		return isbn;
	}
	
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String getPublicationDate() {
		return publicationDate;
	}
	
	public void setPublicationDate(String publicationDate) {
		this.publicationDate = publicationDate;
	}
}
