package aiss.api.model.repository;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aiss.api.model.Book;
import aiss.api.model.BookItem;
import aiss.api.model.Store;

public class BookRepository {
	private static BookRepository instance = null;
	private Map<String, Book> books;
	private Map<Integer, Store> stores;	
	private Integer storeIndex = 0;

	private BookRepository() throws ParseException {
		this.books = new HashMap<String, Book>();
		this.stores = new HashMap<Integer, Store>();

		this.books.put("1", Book.of("1", "The Pilgrim’s Progress", "John Bunyan", "1678-01-01"));
		this.books.put("2", Book.of("2", "Robinson Crusoe", "Daniel Defoe", "1719-01-01"));
		this.books.put("3", Book.of("3", "Gulliver’s Travels", "Jonathan Swift", "1726-01-01"));
		this.books.put("4", Book.of("4", "Clarissa", "Samuel Richardson", "1748-01-01"));
		this.books.put("5", Book.of("5", "Tom Jones", "Henry Fielding", "1749-01-01"));
		this.books.put("6",
				Book.of("6", "The Life and Opinions of Tristram Shandy, Gentleman", "Laurence Sterne", "1759-01-01"));
		this.books.put("7", Book.of("7", "Emma", "Jane Austen", "1816-01-01"));
		this.books.put("8", Book.of("8", "Frankenstein", "Mary Shelley", "1818-01-01"));
		this.books.put("9", Book.of("9", "Nightmare Abbey", "Thomas Love Peacock", "1818-01-01"));
		this.books.put("10",
				Book.of("10", "The Narrative of Arthur Gordon Pym of Nantucket", "Edgar Allan Poe", "1838-01-01"));
		this.books.put("11", Book.of("11", "Sybil", "Benjamin Disraeli", "1845-01-01"));
		this.books.put("12", Book.of("12", "Jane Eyre", "Charlotte Brontë", "1847-01-01"));
		this.books.put("13", Book.of("13", "Wuthering Heights", "Emily Brontë", "1847-01-01"));
		this.books.put("14", Book.of("14", "Vanity Fair", "William Thackeray", "1848-01-01"));
		this.books.put("15", Book.of("15", "David Copperfield", "Charles Dickens", "1850-01-01"));
		this.books.put("16", Book.of("16", "The Scarlet Letter", "Nathaniel Hawthorne", "1850-01-01"));
		this.books.put("17", Book.of("17", "Moby-Dick", "Herman Melville", "1851-01-01"));
		this.books.put("18", Book.of("18", "Alice’s Adventures in Wonderland", "Lewis Carroll", "1865-01-01"));
		this.books.put("19", Book.of("19", "The Moonstone", "Wilkie Collins", "1868-01-01"));
		this.books.put("20", Book.of("20", "Little Women", "Louisa May Alcott", "1868-01-01"));
		this.books.put("21", Book.of("21", "Middlemarch", "George Eliot", "1871-01-01"));
		this.books.put("22", Book.of("22", "The Way We Live Now", "Anthony Trollope", "1875-01-01"));
		this.books.put("23", Book.of("23", "The Adventures of Huckleberry Finn", "Mark Twain", "1884-01-01"));
		this.books.put("24", Book.of("24", "Kidnapped", "Robert Louis Stevenson", "1886-01-01"));
		this.books.put("25", Book.of("25", "Three Men in a Boat", "Jerome K Jerome", "1889-01-01"));
		this.books.put("26", Book.of("26", "The Sign of Four", "Arthur Conan Doyle", "1890-01-01"));
		this.books.put("27", Book.of("27", "The Picture of Dorian Gray", "Oscar Wilde", "1891-01-01"));
		this.books.put("28", Book.of("28", "New Grub Street", "George Gissing", "1891-01-01"));
		this.books.put("29", Book.of("29", "Jude the Obscure", "Thomas Hardy", "1895-01-01"));
		this.books.put("30", Book.of("30", "The Red Badge of Courage", "Stephen Crane", "1895-01-01"));
		this.books.put("31", Book.of("31", "Dracula", "Bram Stoker", "1897-01-01"));
		this.books.put("32", Book.of("32", "Heart of Darkness", "Joseph Conrad", "1899-01-01"));
		this.books.put("33", Book.of("33", "Sister Carrie", "Theodore Dreiser", "1900-01-01"));
		this.books.put("34", Book.of("34", "Kim", "Rudyard Kipling", "1901-01-01"));
		this.books.put("35", Book.of("35", "The Call of the Wild", "Jack London", "1903-01-01"));
		this.books.put("36", Book.of("36", "The Golden Bowl", "Henry James", "1904-01-01"));
		this.books.put("37", Book.of("37", "Hadrian the Seventh", "Frederick Rolfe", "1904-01-01"));
		this.books.put("38", Book.of("38", "The Wind in the Willows", "Kenneth Grahame", "1908-01-01"));
		this.books.put("39", Book.of("39", "The History of Mr Polly", "HG Wells", "1910-01-01"));
		this.books.put("40", Book.of("40", "Zuleika Dobson", "Max Beerbohm", "1911-01-01"));
		this.books.put("41", Book.of("41", "The Good Soldier", "Ford Madox Ford", "1915-01-01"));
		this.books.put("42", Book.of("42", "The Thirty-Nine Steps", "John Buchan", "1915-01-01"));
		this.books.put("43", Book.of("43", "The Rainbow", "DH Lawrence", "1915-01-01"));
		this.books.put("44", Book.of("44", "Of Human Bondage", "W Somerset Maugham", "1915-01-01"));
		this.books.put("45", Book.of("45", "The Age of Innocence", "Edith Wharton", "1920-01-01"));
		this.books.put("46", Book.of("46", "Ulysses", "James Joyce", "1922-01-01"));
		this.books.put("47", Book.of("47", "Babbitt", "Sinclair Lewis", "1922-01-01"));
		this.books.put("48", Book.of("48", "A Passage to India", "EM Forster", "1924-01-01"));
		this.books.put("49", Book.of("49", "Gentlemen Prefer Blondes", "Anita Loos", "1925-01-01"));
		this.books.put("50", Book.of("50", "Mrs Dalloway", "Virginia Woolf", "1925-01-01"));
		this.books.put("51", Book.of("51", "The Great Gatsby", "F Scott Fitzgerald", "1925-01-01"));
		this.books.put("52", Book.of("52", "Lolly Willowes", "Sylvia Townsend Warner", "1926-01-01"));
		this.books.put("53", Book.of("53", "The Sun Also Rises", "Ernest Hemingway", "1926-01-01"));
		this.books.put("54", Book.of("54", "The Maltese Falcon", "Dashiell Hammett", "1929-01-01"));
		this.books.put("55", Book.of("55", "As I Lay Dying", "William Faulkner", "1930-01-01"));
		this.books.put("56", Book.of("56", "Brave New World", "Aldous Huxley", "1932-01-01"));
		this.books.put("57", Book.of("57", "Cold Comfort Farm", "Stella Gibbons", "1932-01-01"));
		this.books.put("58", Book.of("58", "Nineteen Nineteen", "John Dos Passos", "1932-01-01"));
		this.books.put("59", Book.of("59", "Tropic of Cancer", "Henry Miller", "1934-01-01"));
		this.books.put("60", Book.of("60", "Scoop", "Evelyn Waugh", "1938-01-01"));
		this.books.put("61", Book.of("61", "Murphy", "Samuel Beckett", "1938-01-01"));
		this.books.put("62", Book.of("62", "The Big Sleep", "Raymond Chandler", "1939-01-01"));
		this.books.put("63", Book.of("63", "Party Going", "Henry Green", "1939-01-01"));
		this.books.put("64", Book.of("64", "At Swim-Two-Birds", "Flann O’Brien", "1939-01-01"));
		this.books.put("65", Book.of("65", "The Grapes of Wrath", "John Steinbeck", "1939-01-01"));
		this.books.put("66", Book.of("66", "Joy in the Morning", "PG Wodehouse", "1946-01-01"));
		this.books.put("67", Book.of("67", "All the King’s Men", "Robert Penn Warren", "1946-01-01"));
		this.books.put("68", Book.of("68", "Under the Volcano", "Malcolm Lowry", "1947-01-01"));
		this.books.put("69", Book.of("69", "The Heat of the Day", "Elizabeth Bowen", "1948-01-01"));
		this.books.put("70", Book.of("70", "Nineteen Eighty-Four", "George Orwell", "1949-01-01"));
		this.books.put("71", Book.of("71", "The End of the Affair", "Graham Greene", "1951-01-01"));
		this.books.put("72", Book.of("72", "The Catcher in the Rye", "JD Salinger", "1951-01-01"));
		this.books.put("73", Book.of("73", "The Adventures of Augie March", "Saul Bellow", "1953-01-01"));
		this.books.put("74", Book.of("74", "Lord of the Flies", "William Golding", "1954-01-01"));
		this.books.put("75", Book.of("75", "Lolita", "Vladimir Nabokov", "1955-01-01"));
		this.books.put("76", Book.of("76", "On the Road", "Jack Kerouac", "1957-01-01"));
		this.books.put("77", Book.of("77", "Voss", "Patrick White", "1957-01-01"));
		this.books.put("78", Book.of("78", "To Kill a Mockingbird", "Harper Lee", "1960-01-01"));
		this.books.put("79", Book.of("79", "The Prime of Miss Jean Brodie", "Muriel Spark", "1960-01-01"));
		this.books.put("80", Book.of("80", "Catch-22", "Joseph Heller", "1961-01-01"));
		this.books.put("81", Book.of("81", "The Golden Notebook", "Doris Lessing", "1962-01-01"));
		this.books.put("82", Book.of("82", "A Clockwork Orange", "Anthony Burgess", "1962-01-01"));
		this.books.put("83", Book.of("83", "A Single Man", "Christopher Isherwood", "1964-01-01"));
		this.books.put("84", Book.of("84", "In Cold Blood", "Truman Capote", "1966-01-01"));
		this.books.put("85", Book.of("85", "The Bell Jar", "Sylvia Plath", "1966-01-01"));
		this.books.put("86", Book.of("86", "Portnoy’s Complaint", "Philip Roth", "1969-01-01"));
		this.books.put("87", Book.of("87", "Mrs Palfrey at the Claremont", "Elizabeth Taylor", "1971-01-01"));
		this.books.put("88", Book.of("88", "Rabbit Redux", "John Updike", "1971-01-01"));
		this.books.put("89", Book.of("89", "Song of Solomon", "Toni Morrison", "1977-01-01"));
		this.books.put("90", Book.of("90", "A Bend in the River", "VS Naipaul", "1979-01-01"));
		this.books.put("91", Book.of("91", "Midnight’s Children", "Salman Rushdie", "1981-01-01"));
		this.books.put("92", Book.of("92", "Housekeeping", "Marilynne Robinson", "1981-01-01"));
		this.books.put("93", Book.of("93", "Money: A Suicide Note", "Martin Amis", "1984-01-01"));
		this.books.put("94", Book.of("94", "An Artist of the Floating World", "Kazuo Ishiguro", "1986-01-01"));
		this.books.put("95", Book.of("95", "The Beginning of Spring", "Penelope Fitzgerald", "1988-01-01"));
		this.books.put("96", Book.of("96", "Breathing Lessons", "Anne Tyler", "1988-01-01"));
		this.books.put("97", Book.of("97", "Amongst Women", "John McGahern", "1990-01-01"));
		this.books.put("98", Book.of("98", "Underworld", "Don DeLillo", "1997-01-01"));
		this.books.put("99", Book.of("99", "Disgrace", "JM Coetzee", "1999-01-01"));
		this.books.put("100", Book.of("100", "True History of the Kelly Gang", "Peter Carey", "2000-01-01"));
	}

	public static BookRepository getInstance() throws ParseException {
		if (instance == null) {
			instance = new BookRepository();
		}

		return instance;
	}

	public Collection<Book> getBooks() {
		return this.books.values();
	}

	public Book getBook(String isbn) {
		return this.books.getOrDefault(isbn, null);
	}

	public Boolean addBook(Book book) {
		if (this.books.containsKey(book.getIsbn())) {
			return false;
		}

		this.books.put(book.getIsbn(), book);
		return true;
	}

	public Boolean removeBook(String isbn) {
		if (!this.books.containsKey(isbn)) {
			return false;
		}

		this.books.remove(isbn);
		return true;
	}

	public List<Store> searchBookInStores(String isbn) {
		if (!this.books.containsKey(isbn)) {
			return null;
		}

		List<Store> availableStores = new ArrayList<Store>();

		for (Store store : this.stores.values()) {
			if (store.getAvailableItems().containsKey(isbn)) {
				availableStores.add(store);
			}
		}

		return availableStores;
	}

	public Collection<Store> getStores() {
		return this.stores.values();
	}

	public Store getStore(Integer id) {
		return this.stores.getOrDefault(id, null);
	}

	public void addStore(Store store) {
		store.setId(this.storeIndex);
		store.setAvailableItems(new HashMap<String, BookItem>());
		this.stores.put(this.storeIndex, store);
		this.storeIndex++;
	}

	public Boolean removeStore(Integer id) {
		if (!this.stores.containsKey(id)) {
			return false;
		}

		this.stores.remove(id);
		return true;
	}
}
