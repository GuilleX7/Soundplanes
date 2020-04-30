package aiss.controller.client;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.repackaged.com.google.gson.JsonObject;

import aiss.model.client.ClientResponse;
import aiss.model.client.ClientResponseStatus;
import aiss.model.resource.UserResource;
import aiss.model.user.User;

/**
 * Servlet implementation class GetUserInfo
 */
public class GetUserInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetUserInfo() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ClientResponse cr = ClientResponse.create();

		UUID uuid = (UUID) request.getSession().getAttribute("UUID");
		if (uuid != null) {
			System.out.println("Searching for " + uuid);
			User user = UserResource.getInstance().getUser(uuid);
			System.out.println("Finded " + user);
			if (user != null) {
				cr.setStatus(ClientResponseStatus.OK);
				cr.setData(user);
			} else {
				cr.setStatus(ClientResponseStatus.BAD_LOGIN);
			}
		} else {
			cr.setStatus(ClientResponseStatus.NOT_LOGGED);
		}
		
		response.setContentType("application/json");
		response.setStatus(200);
		ObjectMapper om = new ObjectMapper();
		om.writeValue(response.getWriter(), cr);
		response.flushBuffer();
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
