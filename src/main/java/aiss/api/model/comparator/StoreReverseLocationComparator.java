package aiss.api.model.comparator;

import java.util.Comparator;

import aiss.api.model.Store;

public class StoreReverseLocationComparator implements Comparator<Store> {
	@Override
	public int compare(Store o1, Store o2) {
		return o2.getLocation().compareTo(o1.getLocation());
	}
}
