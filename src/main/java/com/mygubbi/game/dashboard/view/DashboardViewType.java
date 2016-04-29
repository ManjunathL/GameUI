package com.mygubbi.game.dashboard.view;

import com.mygubbi.game.dashboard.view.catalog.*;
import com.mygubbi.game.dashboard.view.proposals.CreateProposalsView;
import com.mygubbi.game.dashboard.view.catalog.CatalogView;
import com.mygubbi.game.dashboard.view.proposals.ProposalsView;

import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;

import java.util.ArrayList;
import java.util.List;

public enum DashboardViewType {
    PROPOSALS(new ViewType("Proposals", ProposalsView.class, FontAwesome.ANCHOR, false, new ArrayList<ViewType>() {{
        add(new ViewType("Create Proposal", CreateProposalsView.class, FontAwesome.PLUS_CIRCLE, false, new ArrayList<>()));
    }})),
    CATALOG(new ViewType("Catalog", CatalogView.class, FontAwesome.BAR_CHART_O, false, new ArrayList<ViewType>(){{
        add(new ViewType("JSON", CatalogView.class, FontAwesome.PLUS_CIRCLE, false, new ArrayList<>()));
    }}));

    private final ViewType viewType;

    private DashboardViewType(final ViewType viewType) {
        this.viewType = viewType;
    }

    public ViewType getViewType() {
        return viewType;
    }

    public static DashboardViewType getByViewName(final String viewName) {
        DashboardViewType result = null;
        for (DashboardViewType viewType : values()) {
            if (viewType.getViewType().getViewName().equals(viewName)) {
                result = viewType;
                break;
            }
        }
        return result;
    }

    public static class ViewType {
        private final String viewName;
        private final Class<? extends View> viewClass;
        private final Resource icon;
        private final boolean stateful;
        private List<ViewType> subViewTypes;

        private ViewType(final String viewName,
                         final Class<? extends View> viewClass, final Resource icon,
                         final boolean stateful, final List<ViewType> subViewTypes) {
            this.viewName = viewName;
            this.viewClass = viewClass;
            this.icon = icon;
            this.stateful = stateful;
            this.subViewTypes = subViewTypes;
        }

        public String getViewName() {
            return viewName;
        }

        public Class<? extends View> getViewClass() {
            return viewClass;
        }

        public Resource getIcon() {
            return icon;
        }

        public boolean isStateful() {
            return stateful;
        }

        public List<ViewType> getSubViewTypes() {
            return subViewTypes;
        }
    }

}
