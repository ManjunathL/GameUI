package com.mygubbi.game.dashboard.view.proposals;

import com.google.common.eventbus.Subscribe;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.data.dummy.FileDataProviderUtil;
import com.mygubbi.game.dashboard.domain.ProposalHeader;
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
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.gridutil.cell.GridCellFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public final class ProposalsView extends TabSheet implements View {

    private Grid grid;
    private ProposalDataProvider proposalDataProvider = new ProposalDataProvider(new FileDataProviderUtil());

    public ProposalsView() {
        addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        DashboardEventBus.register(this);

        VerticalLayout root = new VerticalLayout();
        root.setMargin(true);
        root.setCaption("All Proposals");

        addTab(root);
        Responsive.makeResponsive(root);
        List<ProposalHeader> proposalHeaders = getProposalsListing(null);
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
        grid.setColumnReorderingAllowed(true);
        grid.setColumns("crmId", "proposalTitle", "status", "salesContactName", "designContactName", "amount", "createDate", "createdBy");

        List<Grid.Column> columns = grid.getColumns();
        int idx = 0;
        columns.get(idx++).setHeaderCaption("CRM #");

        columns.get(idx++).setHeaderCaption("Title");
        columns.get(idx++).setHeaderCaption("Status");
        columns.get(idx++).setHeaderCaption("Sales");
        columns.get(idx++).setHeaderCaption("Designer");
        columns.get(idx++).setHeaderCaption("Amount");
        columns.get(idx++).setHeaderCaption("Create Date");
        columns.get(idx++).setHeaderCaption("Created By");

        GridCellFilter filter = new GridCellFilter(grid);

        filter.setNumberFilter("amount");
        filter.setTextFilter("proposalTitle", true, false);
        filter.setTextFilter("crmId", true, true);
        filter.setTextFilter("status", true, true);
        filter.setTextFilter("createdBy", true, true);
        filter.setTextFilter("designContactName", true, true);
        filter.setTextFilter("salesContactName", true, true);
        filter.setDateFilter("createDate");

        grid.addSelectionListener(selectionEvent -> {
                    if (!selectionEvent.getAdded().isEmpty()) {
                        Object selected = ((Grid.SingleSelectionModel) grid.getSelectionModel()).getSelectedRow();
                        String title = grid.getContainerDataSource().getItem(selected).getItemProperty("proposalTitle").getValue().toString();
                        int proposalId = (Integer) grid.getContainerDataSource().getItem(selected).getItemProperty("proposalId").getValue();
                        final ProposalDetailsView proposalDetailsView = new ProposalDetailsView(proposalId, title);
                        addTab(proposalDetailsView).setClosable(true);
                        setSelectedTab(getComponentCount() - 1);
                        getTab(proposalDetailsView).setCaption(title);
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
        List<ProposalHeader> proposalHeaders = getProposalsListing(null);
        this.grid.setContainerDataSource(buildDataContainer(proposalHeaders));
    }


    private Component buildHeader(List<ProposalHeader> proposalHeaders) {
        HorizontalLayout header = new HorizontalLayout();
        header.setSpacing(true);

        Label titleLabel = new Label("Proposals");
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
        containerDataSource.removeContainerFilters("status");

        if (!status.equals("all")) {
            containerDataSource.addContainerFilter(new SimpleStringFilter("status", status, true, true));
        }
    }

    private List<ProposalHeader> getProposalsListing(String proposalClass) {
        return proposalDataProvider.getProposalHeaders(proposalClass);
    }

    @Override
    public void enter(ViewChangeEvent viewChangeEvent) {

    }

}
