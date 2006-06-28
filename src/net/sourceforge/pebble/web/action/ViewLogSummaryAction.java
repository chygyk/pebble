/*
 * Copyright (c) 2003-2006, Simon Brown
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 *   - Neither the name of Pebble nor the names of its contributors may
 *     be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.sourceforge.pebble.web.action;

import net.sourceforge.pebble.Constants;
import net.sourceforge.pebble.domain.Blog;
import net.sourceforge.pebble.logging.LogSummary;
import net.sourceforge.pebble.web.view.View;
import net.sourceforge.pebble.web.view.impl.LogSummaryByMonthView;
import net.sourceforge.pebble.web.view.impl.LogSummaryByYearView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Gets the contents of the specified log file.
 *
 * @author    Simon Brown
 */
public class ViewLogSummaryAction extends SecureAction {

  /**
   * Peforms the processing associated with this action.
   *
   * @param request  the HttpServletRequest instance
   * @param response the HttpServletResponse instance
   * @return the name of the next view
   */
  public View process(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    Blog blog = (Blog)getModel().get(Constants.BLOG_KEY);

    String year = request.getParameter("year");
    String month = request.getParameter("month");

    Calendar cal = blog.getCalendar();
    LogSummary logSummary;
    View view;

    if (year != null && year.length() > 0 &&
        month != null && month.length() > 0) {
      cal.set(Calendar.YEAR, Integer.parseInt(year));
      cal.set(Calendar.MONTH, Integer.parseInt(month)-1);
      logSummary = blog.getLogger().getLogSummary(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1);
      view = new LogSummaryByMonthView();
//    } else if (year != null && year.length() > 0) {
//      cal.set(Calendar.YEAR, Integer.parseInt(year));
//      logSummary = blog.getLogger().getLogSummary(cal.get(Calendar.YEAR));
//      view = new LogSummaryByYearView();
    } else {
      // get the log for this month
      logSummary = blog.getLogger().getLogSummary(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1);
      view = new LogSummaryByMonthView();
    }

    SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy", Locale.ENGLISH);
    yearFormatter.setTimeZone(blog.getTimeZone());
    SimpleDateFormat monthFormatter = new SimpleDateFormat("MM", Locale.ENGLISH);
    monthFormatter.setTimeZone(blog.getTimeZone());
    SimpleDateFormat dayFormatter = new SimpleDateFormat("dd", Locale.ENGLISH);
    dayFormatter.setTimeZone(blog.getTimeZone());
    getModel().put("year", yearFormatter.format(logSummary.getDate()));
    getModel().put("month", monthFormatter.format(logSummary.getDate()));

    getModel().put("logSummary", logSummary);

    return view;
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