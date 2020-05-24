package aiss.api.model.comparator;

import java.util.Comparator;

import aiss.api.model.Store;

public class StoreNameComparator implements Comparator<Store> {
	@Override
	public int compare(Store o1, Store o2) {
		return o1.getName().compareTo(o2.getName());
	}
}
