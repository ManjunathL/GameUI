package com.mygubbi.game.dashboard;

import com.mygubbi.game.dashboard.config.ConfigHolder;
import com.vaadin.server.VaadinServlet;

import javax.servlet.ServletException;

@SuppressWarnings("serial")
public class DashboardServlet extends VaadinServlet {

    @Override
    protected final void servletInitialized() throws ServletException {
        super.servletInitialized();
        ServerManager.getInstance();
        getService().addSessionInitListener(new DashboardSessionInitListener());
    }
}