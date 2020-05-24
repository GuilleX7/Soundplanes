package aiss.api;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import aiss.api.resources.BookResource;
import aiss.api.resources.StoreResource;

public class BookstoreApplication extends Application {
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> classes = new HashSet<Class<?>>();

	public BookstoreApplication() throws ParseException {
		singletons.add(BookResource.getInstance());
		singletons.add(StoreResource.getInstance());
	}

	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
