package aiss.api.model.comparator;

import java.util.Comparator;

import aiss.api.model.Book;

public class BookReverseTitleComparator implements Comparator<Book> {
	@Override
	public int compare(Book arg0, Book arg1) {
		return arg1.getTitle().compareTo(arg0.getTitle());
	}
}