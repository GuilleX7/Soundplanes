package aiss.controller.oauth;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import aiss.model.resource.FacebookResource;
import aiss.model.resource.UserResource;
import aiss.model.soundplanes.User;

/**
 * Servlet implementation class LinkFacebookController
 */
public class LinkFacebookController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LinkFacebookController() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		UUID uuid = (UUID) session.getAttribute("UUID");
		String facebookId = new FacebookResource((String) session.getAttribute("Facebook-token")).getUserProfile().getId();
		User linkedUser = UserResource.getInstance().getUserByFacebookId(facebookId); //Get user linked with that Facebook ID
		User user;
		
		if (linkedUser != null) { // There is already a linked user
			session.setAttribute("UUID", linkedUser.getUUID()); // Log in
			session.removeAttribute("Spotify-id");
			session.removeAttribute("Facebook-id");
			response.sendRedirect("/map");
		} else { // There is not a linked user
			if (uuid != null) { // But user is logged
				user = UserResource.getInstance().getUser(uuid);
				user.setFacebookId(facebookId); // Link the Facebook Id
				UserResource.getInstance().indexUserByFacebookId(user, facebookId);
				response.sendRedirect("/map");
			} else { // And user is not logged
				session.setAttribute("Facebook-id", facebookId); // Save Facebook Id for future linking
				response.sendRedirect("/registerUser");
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}