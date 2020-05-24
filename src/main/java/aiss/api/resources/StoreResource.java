package aiss.api.resources;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.NotFoundException;

import aiss.api.model.Book;
import aiss.api.model.BookItem;
import aiss.api.model.Store;
import aiss.api.model.comparator.StoreLocationComparator;
import aiss.api.model.comparator.StoreNameComparator;
import aiss.api.model.comparator.StoreReverseLocationComparator;
import aiss.api.model.comparator.StoreReverseNameComparator;
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

	@GET
	@Produces("application/json")
	public Collection<Store> getStores(@QueryParam("q") String query, @QueryParam("order") String order,
			@QueryParam("location") String location) {
		List<Store> result = new ArrayList<Store>();

		if (query != null) {
			String lowercasedQuery = query.toLowerCase();
			for (Store store : repository.getStores()) {
				if (store.getName().toLowerCase().contains(lowercasedQuery)) {
					result.add(store);
				}
			}
		} else {
			result.addAll(repository.getStores());
		}

		if (location != null) {
			String lowercasedLocation = location.toLowerCase();
			ListIterator<Store> iterator = result.listIterator();
			while (iterator.hasNext()) {
				if (!iterator.next().getLocation().toLowerCase().equals(lowercasedLocation)) {
					iterator.remove();
				}
			}
		}

		if (order != null) {
			switch (order) {
			case "name":
				result.sort(new StoreNameComparator());
				break;
			case "-name":
				result.sort(new StoreReverseNameComparator());
				break;
			case "location":
				result.sort(new StoreLocationComparator());
				break;
			case "-location":
				result.sort(new StoreReverseLocationComparator());
				break;
			default:
				throw new BadRequestException("Filter must be one of these: name, -name, location, -location");
			}
		}

		return result;
	}

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response addStore(@Context UriInfo uriInfo, Store store) throws ParseException {
		if (store.getName() == null || store.getName().contentEquals("") || store.getLocation() == null
				|| store.getLocation().contentEquals(""))
			throw new BadRequestException("No atribute can be null or empty");

		repository.addStore(store);

		UriBuilder urlBuilder = uriInfo.getAbsolutePathBuilder().path(this.getClass(), "getStore");
		ResponseBuilder response = Response.created(urlBuilder.build(store.getId()));
		response.entity(store);
		return response.build();
	}

	@PUT
	@Consumes("application/json")
	public Response updateStore(Store newStore) {
		Store currentStore = repository.getStore(Integer.valueOf(newStore.getId()));
		if (currentStore == null) {
			throw new NotFoundException("Store doesn't exist");
		}

		if (newStore.getName() != null) {
			currentStore.setName(newStore.getName());
		}

		if (newStore.getLocation() != null) {
			currentStore.setLocation(newStore.getLocation());
		}

		if (newStore.getAvailableItems() != null) {
			throw new BadRequestException("Available items are not allowed to be changed this way");
		}

		return Response.noContent().build();
	}

	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Store getStore(@PathParam("id") Integer id) {
		Store store = repository.getStore(id);

		if (store == null) {
			throw new NotFoundException("Store doesn't exist");
		}

		return store;
	}

	@DELETE
	@Path("/{id}")
	public Response removeStore(@PathParam("id") Integer id) {
		if (repository.removeStore(id) == false) {
			throw new NotFoundException("Store doesn't exist");
		}

		return Response.noContent().build();
	}

	@POST
	@Path("/{storeId}/{bookIsbn}")
	@Produces("application/json")
	public Response addBookItemToStore(@Context UriInfo uriInfo, @PathParam("storeId") Integer storeId,
			@PathParam("bookIsbn") String bookIsbn, @QueryParam("price") Double price) {
		Store store = repository.getStore(storeId);
		Book book = repository.getBook(bookIsbn);

		if (store == null) {
			throw new NotFoundException("Store not found");
		}

		if (book == null) {
			throw new NotFoundException("Book not found");
		}

		if (store.getAvailableItems().containsKey(bookIsbn) == true) {
			throw new BadRequestException("Book is already at the store.");
		}

		if (price == null) {
			throw new BadRequestException("Price can't be null");
		}

		store.getAvailableItems().put(bookIsbn, BookItem.of(book, price));

		UriBuilder urlBuilder = uriInfo.getAbsolutePathBuilder().path(this.getClass(), "getStore");
		ResponseBuilder response = Response.created(urlBuilder.build(store.getId()));
		response.entity(store);
		return response.build();
	}

	@PUT
	@Path("/{storeId}/{bookIsbn}")
	@Produces("application/json")
	public Response updateBookItemFromStore(@Context UriInfo uriInfo, @PathParam("storeId") Integer storeId,
			@PathParam("bookIsbn") String bookIsbn, @QueryParam("price") Double price) {
		Store store = repository.getStore(storeId);
		Book book = repository.getBook(bookIsbn);

		if (store == null) {
			throw new NotFoundException("Store not found");
		}

		if (book == null) {
			throw new NotFoundException("Book not found");
		}

		if (!store.getAvailableItems().containsKey(bookIsbn)) {
			throw new BadRequestException("Book is not at the store");
		}

		if (price == null) {
			throw new BadRequestException("Price can't be null");
		}

		store.getAvailableItems().put(bookIsbn, BookItem.of(book, price));

		UriBuilder urlBuilder = uriInfo.getAbsolutePathBuilder().path(this.getClass(), "getStore");
		ResponseBuilder response = Response.created(urlBuilder.build(store.getId()));
		response.entity(store);
		return response.build();
	}

	@DELETE
	@Path("/{storeId}/{bookIsbn}")
	public Response removeBookItemFromStore(@PathParam("storeId") Integer storeId,
			@PathParam("bookIsbn") String bookIsbn) {
		Store store = repository.getStore(storeId);
		Book book = repository.getBook(bookIsbn);

		if (store == null) {
			throw new NotFoundException("Store not found");
		}

		if (book == null) {
			throw new NotFoundException("Book not found");
		}

		if (!store.getAvailableItems().containsKey(bookIsbn)) {
			throw new NotFoundException("Book is not at the store");
		}

		store.getAvailableItems().remove(bookIsbn);

		return Response.noContent().build();
	}
}
