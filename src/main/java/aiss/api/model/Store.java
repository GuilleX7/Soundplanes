package aiss.api.model;

import java.util.Map;

public class Store {
	private Integer id;
	private String name;
	private String location;
	private Map<String, BookItem> availableItems;
	
	public static Store of(Integer id, String name, String location, Map<String, BookItem> availableItems) {
		return new Store(id, name, location, availableItems);
	}
	
	private Store(Integer id, String name, String location, Map<String, BookItem> availableItems) {
		super();
		this.id = id;
		this.name = name;
		this.location = location;
		this.availableItems = availableItems;
	}
	
	private Store() {
		super();
		this.id = null;
		this.name = null;
		this.location = null;
		this.availableItems = null;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Map<String, BookItem> getAvailableItems() {
		return availableItems;
	}

	public void setAvailableItems(Map<String, BookItem> availableItems) {
		this.availableItems = availableItems;
	}
}
