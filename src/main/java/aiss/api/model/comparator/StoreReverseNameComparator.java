package aiss.api.model.comparator;

import java.util.Comparator;

import aiss.api.model.Store;

public class StoreReverseNameComparator implements Comparator<Store> {
	@Override
	public int compare(Store o1, Store o2) {
		return o2.getName().compareTo(o1.getName());
	}
}
