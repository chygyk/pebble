package net.sourceforge.pebble.web.action;

import net.sourceforge.pebble.Constants;
import net.sourceforge.pebble.web.view.View;
import net.sourceforge.pebble.web.view.impl.MessagesView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Allows the user to see all messages for the current blog.
 *
 * @author    Simon Brown
 */
public class ViewMessagesAction extends SecureAction {

  /**
   * Peforms the processing associated with this action.
   *
   * @param request  the HttpServletRequest instance
   * @param response the HttpServletResponse instance
   * @return the name of the next view
   */
  public View process(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    return new MessagesView();
  }

  /**
   * Gets a list of all roles that are allowed to access this action.
   *
   * @return  an array of Strings representing role names
   * @param request
   */
  public String[] getRoles(HttpServletRequest request) {
    return new String[]{Constants.BLOG_OWNER_ROLE, Constants.BLOG_PUBLISHER_ROLE, Constants.BLOG_CONTRIBUTOR_ROLE};
  }

}