package aiss.api.resources;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import aiss.api.model.Store;
import aiss.api.model.comparator.BookAuthorComparator;
import aiss.api.model.comparator.BookItemPriceComparator;
import aiss.api.model.comparator.BookItemReversePriceComparator;
import aiss.api.model.comparator.BookPublicationDateComparator;
import aiss.api.model.comparator.BookReverseAuthorComparator;
import aiss.api.model.comparator.BookReversePublicationDateComparator;
import aiss.api.model.comparator.BookReverseTitleComparator;
import aiss.api.model.comparator.BookTitleComparator;
import aiss.api.model.repository.BookRepository;

@Path("/books")
public class BookResource {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static BookResource instance = null;
	private BookRepository repository = null;

	private BookResource() throws ParseException {
		repository = BookRepository.getInstance();
	}

	public static BookResource getInstance() throws ParseException {
		if (instance == null) {
			instance = new BookResource();
		}

		return instance;
	}

	@GET
	@Produces("application/json")
	public Collection<Book> getBooks(@QueryParam("q") String query, @QueryParam("order") String order,
			@QueryParam("author") String author, @QueryParam("publishedAfter") String publishedAfter,
			@QueryParam("publishedBefore") String publishedBefore) throws ParseException {
		List<Book> result = new ArrayList<Book>();

		if (query != null) {
			String lowercasedQuery = query.toLowerCase();
			for (Book book : repository.getBooks()) {
				if (book.getTitle().toLowerCase().contains(lowercasedQuery)) {
					result.add(book);
				}
			}
		} else {
			result.addAll(repository.getBooks());
		}

		if (author != null) {
			String lowercasedAuthor = author.toLowerCase();
			ListIterator<Book> iterator = result.listIterator();
			while (iterator.hasNext()) {
				if (!iterator.next().getAuthor().toLowerCase().equals(lowercasedAuthor)) {
					iterator.remove();
				}
			}
		}

		if (publishedAfter != null) {
			Date parsedPublishedAfter = null;
			try {
				parsedPublishedAfter = dateFormat.parse(publishedAfter);
			} catch (ParseException e) {
				throw new BadRequestException("publishedAfter must follow the format yyyy-MM-dd");
			}
			ListIterator<Book> iterator = result.listIterator();
			while (iterator.hasNext()) {
				Date parsedBookDate = dateFormat.parse(iterator.next().getPublicationDate());
				if (parsedBookDate.before(parsedPublishedAfter)) {
					iterator.remove();
				}
			}
		}

		if (publishedBefore != null) {
			Date parsedPublishedBefore = null;
			try {
				parsedPublishedBefore = dateFormat.parse(publishedBefore);
			} catch (ParseException e) {
				throw new BadRequestException("PublishedBefore must follow the format yyyy-MM-dd");
			}
			ListIterator<Book> iterator = result.listIterator();
			while (iterator.hasNext()) {
				Date parsedBookDate = dateFormat.parse(iterator.next().getPublicationDate());
				if (parsedBookDate.after(parsedPublishedBefore)) {
					iterator.remove();
				}
			}
		}

		if (order != null) {
			switch (order) {
			case "title":
				result.sort(new BookTitleComparator());
				break;
			case "-title":
				result.sort(new BookReverseTitleComparator());
				break;
			case "publicationDate":
				result.sort(new BookPublicationDateComparator());
				break;
			case "-publicationDate":
				result.sort(new BookReversePublicationDateComparator());
				break;
			case "author":
				result.sort(new BookAuthorComparator());
				break;
			case "-author":
				result.sort(new BookReverseAuthorComparator());
				break;
			default:
				throw new BadRequestException(
						"Filter must be one of these: title, -title, publicationDate, -publicationDate, author, -author");
			}
		}

		return result;
	}

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response addBook(@Context UriInfo uriInfo, Book book) throws ParseException {
		if (book.getIsbn() == null || book.getIsbn().contentEquals("") || book.getTitle() == null
				|| book.getTitle().contentEquals("") || book.getAuthor() == null || book.getAuthor().contentEquals("")
				|| book.getPublicationDate() == null || book.getPublicationDate().contentEquals(""))
			throw new BadRequestException("No atribute can be null or empty");

		try {
			dateFormat.parse(book.getPublicationDate());
		} catch (ParseException e) {
			throw new BadRequestException("Publication date must follow the format yyyy-MM-dd");
		}

		if (!repository.addBook(book)) {
			throw new BadRequestException("Book already exists");
		}

		UriBuilder urlBuilder = uriInfo.getAbsolutePathBuilder().path(this.getClass(), "getBook");
		ResponseBuilder response = Response.created(urlBuilder.build(book.getIsbn()));
		response.entity(book);
		return response.build();
	}

	@PUT
	@Consumes("application/json")
	public Response updateBook(Book newBook) {
		Book currentBook = repository.getBook(newBook.getIsbn());
		if (currentBook == null) {
			throw new NotFoundException("Book doesn't exist");
		}

		if (newBook.getTitle() != null) {
			currentBook.setTitle(newBook.getTitle());
		}

		if (newBook.getAuthor() != null) {
			currentBook.setAuthor(newBook.getAuthor());
		}

		if (newBook.getPublicationDate() != null) {
			try {
				dateFormat.parse(newBook.getPublicationDate());
			} catch (ParseException e) {
				throw new BadRequestException("Publication date must follow the format yyyy-MM-dd");
			}

			currentBook.setPublicationDate(newBook.getPublicationDate());
		}

		return Response.noContent().build();
	}

	@GET
	@Path("/{isbn}")
	@Produces("application/json")
	public Book getBook(@PathParam("isbn") String isbn) {
		Book book = repository.getBook(isbn);

		if (book == null) {
			throw new NotFoundException("Book doesn't exist");
		}

		return book;
	}
	
	@DELETE
	@Path("/{isbn}")
	public Response removeSong(@PathParam("isbn") String isbn) {
		if (repository.removeBook(isbn) == false) {
			throw new NotFoundException("Book doesn't exist");
		}

		return Response.noContent().build();
	}

	@GET
	@Path("/{isbn}/stores")
	@Produces("application/json")
	public Collection<Store> searchBookInStores(@PathParam("isbn") String isbn, @QueryParam("order") String order) {
		List<Store> stores = repository.searchBookInStores(isbn);

		if (stores == null) {
			throw new NotFoundException("Book doesn't exist");
		}
		
		if (order != null) {
			switch (order) {
			case "price":
				stores.sort(new BookItemPriceComparator(isbn));
				break;
			case "-price":
				stores.sort(new BookItemReversePriceComparator(isbn));
				break;
			default:
				throw new BadRequestException("Order must be one of these: price, -price");
			}
		}

		return stores;
	}
}
