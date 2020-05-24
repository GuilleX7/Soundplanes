package aiss.api.model.comparator;

import java.util.Comparator;

import aiss.api.model.Store;

public class BookItemReversePriceComparator implements Comparator<Store> {
	private String isbn;
	
	public BookItemReversePriceComparator(String isbn) {
		this.isbn = isbn;
	}

	@Override
	public int compare(Store o1, Store o2) {
		return o2.getAvailableItems().get(isbn).getPrice().compareTo(o1.getAvailableItems().get(isbn).getPrice());
	}
}
