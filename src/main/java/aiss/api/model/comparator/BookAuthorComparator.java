package aiss.api.model.comparator;

import java.util.Comparator;

import aiss.api.model.Book;

public class BookAuthorComparator implements Comparator<Book> {
	@Override
	public int compare(Book arg0, Book arg1) {
		return arg0.getAuthor().compareTo(arg1.getAuthor());
	}
}