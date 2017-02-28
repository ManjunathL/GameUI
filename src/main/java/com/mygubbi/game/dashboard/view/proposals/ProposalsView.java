package com.mygubbi.game.dashboard.view.proposals;

import com.google.common.eventbus.Subscribe;
import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.ProposalHeader;
import com.mygubbi.game.dashboard.domain.ProposalVersion;
import com.mygubbi.game.dashboard.domain.User;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.event.ProposalEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.gridutil.cell.GridCellFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public final class ProposalsView extends TabSheet implements View {


    private static final Logger LOG = LogManager.getLogger(ProposalsView.class);



    private Grid grid;
    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();

    public ProposalsView() {
        addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        DashboardEventBus.register(this);

        VerticalLayout root = new VerticalLayout();
        root.setMargin(true);
        root.setCaption("All Quotations");

        addTab(root);
        Responsive.makeResponsive(root);
        List<ProposalHeader> proposalHeaders = getProposalsListing();
        root.addComponent(buildHeader(proposalHeaders));
        root.setSpacing(true);

        buildListingGrid(proposalHeaders, root);

        // All the open sub-windows should be closed whenever the root layout
        // gets clicked.
        root.addLayoutClickListener(new LayoutClickListener() {
            @Override
            public void layoutClick(final LayoutClickEvent event) {
                DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
            }
        });

    }

    private void buildListingGrid(List<ProposalHeader> proposalHeaders, VerticalLayout root) {
        BeanItemContainer<ProposalHeader> container = buildDataContainer(proposalHeaders);
        grid = new Grid(container);
        grid.setSizeFull();
        grid.setHeightByRows(11);
        grid.setHeightMode(HeightMode.ROW);
        grid.setColumnReorderingAllowed(true);
        grid.setColumns(ProposalHeader.QUOTE_NO_NEW,ProposalHeader.QUOTE_NO,ProposalHeader.CRM_ID, ProposalHeader.VERSION,  ProposalHeader.TITLE, ProposalHeader.STATUS,
                ProposalHeader.SALES_NAME, ProposalHeader.DESIGNER_NAME, ProposalHeader.DESIGN_PARTNER_NAME, ProposalHeader.CREATED_ON,
                ProposalHeader.CREATED_BY);

        List<Grid.Column> columns = grid.getColumns();
        int idx = 0;
        columns.get(idx++).setHeaderCaption("Quotation #");
        columns.get(idx++).setHeaderCaption("Quotation # (old)");
        columns.get(idx++).setHeaderCaption("CRM #");
        columns.get(idx++).setHeaderCaption("Version No");
        columns.get(idx++).setHeaderCaption("Title");
        columns.get(idx++).setHeaderCaption("Status");
        columns.get(idx++).setHeaderCaption("Sales");
        columns.get(idx++).setHeaderCaption("Designer");
        columns.get(idx++).setHeaderCaption("DesignPartner");
        columns.get(idx++).setHeaderCaption("Create Date");
        columns.get(idx++).setHeaderCaption("Created By");
        grid.sort("createdOn", SortDirection.DESCENDING);
        GridCellFilter filter = new GridCellFilter(grid);

        filter.setTextFilter(ProposalHeader.TITLE, true, false);
        filter.setTextFilter(ProposalHeader.CRM_ID, true, false);
        filter.setTextFilter(ProposalHeader.ID,true,false);
        filter.setTextFilter(ProposalHeader.VERSION,true,true);
        filter.setTextFilter(ProposalHeader.QUOTE_NO_NEW,true,false);
        filter.setTextFilter(ProposalHeader.QUOTE_NO, true, true);
        filter.setTextFilter(ProposalHeader.STATUS, true, true);
        filter.setTextFilter(ProposalHeader.CREATED_BY, true, false);
        filter.setTextFilter(ProposalHeader.DESIGNER_NAME, true, false);
        filter.setTextFilter(ProposalHeader.DESIGN_PARTNER_NAME, true, false);
        filter.setTextFilter(ProposalHeader.SALES_NAME, true, true);
        filter.setDateFilter(ProposalHeader.CREATED_ON);

        grid.addSelectionListener(selectionEvent -> {
                    if (!selectionEvent.getAdded().isEmpty()) {
                        Object selected = ((Grid.SingleSelectionModel) grid.getSelectionModel()).getSelectedRow();
                       int proposalId = (Integer) grid.getContainerDataSource().getItem(selected).getItemProperty(ProposalHeader.ID).getValue();

                        UI.getCurrent().getNavigator()
                                .navigateTo("New Quotation/" + proposalId);
                    }
                }
        );

        root.addComponent(grid);
    }

    private BeanItemContainer<ProposalHeader> buildDataContainer(List<ProposalHeader> proposalHeaders) {
        BeanItemContainer<ProposalHeader> container = new BeanItemContainer<>(ProposalHeader.class);
        container.addAll(proposalHeaders);
        return container;
    }

    @Subscribe
    public void proposalsDataUpdated(final ProposalEvent.ProposalUpdated event) {
        List<ProposalHeader> proposalHeaders = getProposalsListing();
        this.grid.setContainerDataSource(buildDataContainer(proposalHeaders));
    }

    private Component buildHeader(List<ProposalHeader> proposalHeaders) {
        HorizontalLayout header = new HorizontalLayout();
        header.setSpacing(true);

        Label titleLabel = new Label("Quotations");
        titleLabel.setSizeUndefined();
        titleLabel.addStyleName(ValoTheme.LABEL_H1);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(titleLabel);
        header.setSpacing(true);

        HorizontalLayout tools = new HorizontalLayout();
        Map<String, Integer> statusCounts = new HashMap<>();

        proposalHeaders.stream().forEach(proposalHeader -> {
            Integer count = statusCounts.get(proposalHeader.getStatus());
            statusCounts.put(proposalHeader.getStatus(), count == null ? 1 : count + 1);
        });

        for (Map.Entry<String, Integer> statusCount : statusCounts.entrySet()) {

            String name = statusCount.getKey();
            int count = statusCount.getValue();

            Button statusButton = new Button(name + " (" + count + ")");
            statusButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
            statusButton.setData(name);
            statusButton.addClickListener(this::filterByStatus);

            tools.addComponent(statusButton);

        }
        Button statusButton = new Button("all" + " (" + proposalHeaders.size() + ")");
        statusButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        statusButton.setData("all");
        statusButton.addClickListener(this::filterByStatus);
        tools.addComponent(statusButton);

        tools.setSpacing(true);

        tools.addStyleName("toolbar");
        header.addComponent(tools);

        return header;
    }

    private void filterByStatus(Button.ClickEvent clickEvent) {
        String status = (String) clickEvent.getButton().getData();
        BeanItemContainer containerDataSource = (BeanItemContainer) this.grid.getContainerDataSource();
        containerDataSource.removeContainerFilters(ProposalVersion.STATUS);

        if (!status.equals("all")) {
            containerDataSource.addContainerFilter(new SimpleStringFilter(ProposalHeader.STATUS, status, true, true));
        }
    }

    private List<ProposalHeader> getProposalsListing() {
        String role = ((User) VaadinSession.getCurrent().getAttribute(User.class.getName())).getRole();
        String email = ((User) VaadinSession.getCurrent().getAttribute(User.class.getName())).getEmail();

        if (("designpartner").equals(role))
        {
            return proposalDataProvider.getProposalHeadersBasedOnDesignPartner(email);
        }
        else
        {
        return proposalDataProvider.getProposalHeaders();
        }
    }

    private List<ProposalHeader> getProposalsListingByStatus(String proposalStatus) {
        return proposalDataProvider.getProposalHeadersByStatus(proposalStatus);
    }

    @Override
    public void enter(ViewChangeEvent viewChangeEvent) {

    }

}
