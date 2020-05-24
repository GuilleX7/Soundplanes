package aiss.api.resources;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.ws.rs.Path;

import aiss.api.model.repository.BookRepository;

@Path("/stores")
public class StoreResource {
	private static StoreResource instance = null;
	private BookRepository repository = null;
	
	private StoreResource() throws ParseException {
		repository = BookRepository.getInstance();
	}
	
	public static StoreResource getInstance() throws ParseException {
		if (instance == null) {
			instance = new StoreResource();
		}
		
		return instance;
	}
	
	
}
