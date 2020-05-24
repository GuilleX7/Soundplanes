package aiss.api.model.comparator;

import java.util.Comparator;

import aiss.api.model.Store;

public class BookItemPriceComparator implements Comparator<Store> {
	private String isbn;
	
	public BookItemPriceComparator(String isbn) {
		this.isbn = isbn;
	}

	@Override
	public int compare(Store o1, Store o2) {
		return o1.getAvailableItems().get(isbn).getPrice().compareTo(o2.getAvailableItems().get(isbn).getPrice());
	}
}
