package net.sourceforge.pebble.api.comment;

import net.sourceforge.pebble.domain.Comment;
import net.sourceforge.pebble.domain.Blog;
import net.sourceforge.pebble.util.SecurityUtils;
import net.sourceforge.pebble.api.event.comment.CommentListener;
import net.sourceforge.pebble.api.event.comment.CommentEvent;
import net.sourceforge.pebble.event.response.IpAddressListener;
import net.sourceforge.pebble.event.response.ContentSpamListener;
import net.sourceforge.pebble.event.response.SpamScoreListener;
import net.sourceforge.pebble.event.response.LinkSpamListener;
import net.sourceforge.pebble.PluginProperties;

import javax.servlet.http.HttpServletRequest;

/**
 * Starting point for CommentConfirmationStrategy implementations.
 *
 * @author    Simon Brown
 */
public abstract class AbstractCommentConfirmationStrategy implements CommentConfirmationStrategy {

  /** the name of the required override property */
  public static final String REQUIRED_KEY = "CommentConfirmationStrategy.required";

  /**
   * Called to determine whether confirmation is required. This default
   * implementation returns false if the user is authenticated. Otherwise,
   * it runs the default set of comment listeners to determine
   * whether the comment is spam. If so, true is returned.
   *
   * @param request the HttpServletRequest used in the confirmation
   * @param comment the Comment being confirmed
   * @return true if the comment should be confirmed, false otherwise
   */
  public boolean confirmationRequired(HttpServletRequest request, Comment comment) {
    PluginProperties props = comment.getBlogEntry().getBlog().getPluginProperties();
    String required = props.getProperty(REQUIRED_KEY);

    Blog blog = comment.getBlogEntry().getBlog();
    if (SecurityUtils.isUserAuthorisedForBlog(blog)) {
      return false;
    } else {
      // run a subset of the default comment listeners to figure out whether
      // the comment is spam
      CommentListener listener1 = new IpAddressListener();
      CommentListener listener2 = new LinkSpamListener();
      CommentListener listener3 = new ContentSpamListener();
      CommentListener listener4 = new SpamScoreListener();
      CommentEvent event = new CommentEvent(comment, CommentEvent.COMMENT_ADDED);

      listener1.commentAdded(event);
      listener2.commentAdded(event);
      listener3.commentAdded(event);
      listener4.commentAdded(event);

      return (required != null && required.equalsIgnoreCase("true")) || !comment.isApproved();
    }
  }

  /**
   * Called before showing the confirmation page.
   *
   * @param request the HttpServletRequest used in the confirmation
   * @param comment the Comment being confirmed
   */
  public void setupConfirmation(HttpServletRequest request, Comment comment) {
  }

}
