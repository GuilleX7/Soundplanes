package aiss.controller.client;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aiss.model.resource.UserResource;
import aiss.model.soundplanes.User;
import aiss.model.soundplanes.client.ClientResponse;
import aiss.model.soundplanes.client.ClientResponseStatus;

/**
 * Servlet implementation class ClientUserProfileController
 */
public class ClientUserProfileController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ClientUserProfileController() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ClientResponse cr = ClientResponse.create();
		
		String uuid = (String) request.getSession().getAttribute("UUID");
		if (uuid != null) {
			System.out.println("Client connected as " + uuid);
			User user = UserResource.getUser(uuid);
			System.out.println("User data: " + user);
			if (user != null) {
				cr.setData(user);
			} else {
				cr.setStatus(ClientResponseStatus.NOT_FOUND);
			}
		} else {
			cr.setStatus(ClientResponseStatus.UNAUTHORIZED);
		}
		
		cr.writeTo(response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
