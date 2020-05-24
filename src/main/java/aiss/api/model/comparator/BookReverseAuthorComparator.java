package aiss.api.model.comparator;

import java.util.Comparator;

import aiss.api.model.Book;

public class BookReverseAuthorComparator implements Comparator<Book> {
	@Override
	public int compare(Book arg0, Book arg1) {
		return arg1.getAuthor().compareTo(arg0.getAuthor());
	}
}