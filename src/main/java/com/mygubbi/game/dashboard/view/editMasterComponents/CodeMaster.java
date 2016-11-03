package com.mygubbi.game.dashboard.view.editMasterComponents;

import com.mygubbi.game.dashboard.domain.ProposalHeader;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.LayoutEvents;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.gridutil.cell.GridCellFilter;


import java.util.List;

/**
 * Created by test on 27-06-2016.
 */
public class CodeMaster extends Panel implements View {

    private Grid grid;

    public CodeMaster() {
        addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        DashboardEventBus.register(this);

        VerticalLayout root = new VerticalLayout();
        root.setMargin(true);
        root.setCaption("All Proposals");

        Responsive.makeResponsive(root);
//        List<ProposalHeader> proposalHeaders = getProposalsListing();
//        root.addComponent(buildHeader(proposalHeaders));
        root.setSpacing(true);

//        buildListingGrid(proposalHeaders, root);

        // All the open sub-windows should be closed whenever the root layout
        // gets clicked.
        root.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {
            @Override
            public void layoutClick(final LayoutEvents.LayoutClickEvent event) {
                DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
            }
        });

    }


    private void buildListingGrid(List<ProposalHeader> proposalHeaders, VerticalLayout root) {
        BeanItemContainer<ProposalHeader> container = buildDataContainer(proposalHeaders);
        grid = new Grid(container);
        grid.setSizeFull();
        grid.setColumnReorderingAllowed(true);
        grid.setColumns(ProposalHeader.CRM_ID, ProposalHeader.QUOTE_NO, ProposalHeader.TITLE, ProposalHeader.STATUS,
                ProposalHeader.SALES_NAME, ProposalHeader.DESIGNER_NAME, ProposalHeader.CREATED_ON,
                ProposalHeader.CREATED_BY);

        List<Grid.Column> columns = grid.getColumns();
        int idx = 0;
        columns.get(idx++).setHeaderCaption("CRM #");
        columns.get(idx++).setHeaderCaption("Quotation #");
        columns.get(idx++).setHeaderCaption("Title");
        columns.get(idx++).setHeaderCaption("Status");
        columns.get(idx++).setHeaderCaption("Sales");
        columns.get(idx++).setHeaderCaption("Designer");
        columns.get(idx++).setHeaderCaption("Create Date");
        columns.get(idx++).setHeaderCaption("Created By");
        grid.sort("createdOn", SortDirection.DESCENDING);


        GridCellFilter filter = new GridCellFilter(grid);

        filter.setTextFilter(ProposalHeader.TITLE, true, false);
        filter.setTextFilter(ProposalHeader.CRM_ID, true, true);
        filter.setTextFilter(ProposalHeader.QUOTE_NO, true, true);
        filter.setTextFilter(ProposalHeader.STATUS, true, true);
        filter.setTextFilter(ProposalHeader.CREATED_BY, true, true);
        filter.setTextFilter(ProposalHeader.DESIGNER_NAME, true, true);
        filter.setTextFilter(ProposalHeader.SALES_NAME, true, true);
        filter.setDateFilter(ProposalHeader.CREATED_ON);

        grid.addSelectionListener(selectionEvent -> {
                    if (!selectionEvent.getAdded().isEmpty()) {
                        Object selected = ((Grid.SingleSelectionModel) grid.getSelectionModel()).getSelectedRow();
                        String title = grid.getContainerDataSource().getItem(selected).getItemProperty(ProposalHeader.TITLE).getValue().toString();
                        int proposalId = (Integer) grid.getContainerDataSource().getItem(selected).getItemProperty(ProposalHeader.ID).getValue();

                        UI.getCurrent().getNavigator()
                                .navigateTo("New Proposal/" + proposalId);
                        //DashboardViewType.PROPOSALS.getViewType().getSubViewTypes().stream().filter(viewType -> viewType.getViewClass().equals(CreateProposalsView.class)).findFirst().get().getViewName());



/*
                        final ProposalDetailsView proposalDetailsView = new ProposalDetailsView(proposalId, title);
                        addTab(proposalDetailsView).setClosable(true);
                        setSelectedTab(getComponentCount() - 1);
                        getTab(proposalDetailsView).setCaption(title);
*/
                    }
                }
        );

        root.addComponent(grid);
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

        return header;
    }

    private BeanItemContainer<ProposalHeader> buildDataContainer(List<ProposalHeader> proposalHeaders) {
        BeanItemContainer<ProposalHeader> container = new BeanItemContainer<>(ProposalHeader.class);
        container.addAll(proposalHeaders);
        return container;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }

}
