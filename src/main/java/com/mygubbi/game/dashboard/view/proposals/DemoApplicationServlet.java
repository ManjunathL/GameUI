package com.mygubbi.game.dashboard.view.proposals;

import org.vaadin.teemu.jsoncontainer.JsonContainer;

import com.vaadin.terminal.gwt.server.ApplicationServlet;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by test on 04-04-2016.
 */
public class DemoApplicationServlet extends ApplicationServlet{
    @Override
    protected void writeAjaxPageHtmlHeader(BufferedWriter page, String title,
                                           String themeUri, HttpServletRequest request) throws IOException {
        super.writeAjaxPageHtmlHeader(page, title, themeUri, request);
        page.write("<meta name=\"viewport\" content=\"width=1100\"/>\n");
    }
}
