package com.mygubbi.game.dashboard.view.proposals;

import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.data.dummy.FileDataProviderUtil;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.vaadin.addon.charts.themes.VaadinTheme;
import com.vaadin.addon.charts.themes.ValoDarkTheme;
import com.vaadin.annotations.Theme;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.teemu.jsoncontainer.JsonContainer;
import us.monoid.json.JSONArray;

@SuppressWarnings("serial")
public final class ProposalsView extends TabSheet implements View {

    public static final String TITLE_ID = "proposals-title";

    private Label titleLabel;

    private Window window;

    private Grid grid;

    private static final String ACTIVE = "active";
    private static final String CONVERTED = "converted";
    private static final String CANCELLED = "cancelled";
    private final VerticalLayout root;
    private ProposalDataProvider proposalDataProvider = new ProposalDataProvider(new FileDataProviderUtil());

    public ProposalsView() {
        addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        DashboardEventBus.register(this);


        root = new VerticalLayout();
        /*root.setSizeFull();*/
        root.setMargin(true);
        root.setCaption("All Proposals");


        addTab(root);

        Responsive.makeResponsive(root);



        // root.addComponent(buildHeader());
/*
        Label label = new Label("Proposal Listing");
        label.addStyleName(ValoTheme.LABEL_H1);*/
        //  label.addStyleName(ValoTheme.LABEL_NO_MARGIN);

        root.addComponent(buildHeader());
        root.setSpacing(true);

      /*  Button test = new Button("Open Proposal");
        final String title = "Kitchen for Sanjay Gupta, Durga Solitaire";
        final ProposalDetailsView proposalDetailsView = new ProposalDetailsView("123", title);

        test.addClickListener(event -> {
            addTab(proposalDetailsView).setClosable(true);
            setSelectedTab(getComponentCount() - 1);
            getTab(proposalDetailsView).setCaption(title);
        });
        root.addComponent(test);
*/

        grid = new Grid();

        // grid.addStyleName(Reindeer.TABLE_STRONG);
        grid.setWidth("800px");
        // grid.setHeightMode(HeightMode.ROW);
        grid.setHeight("300px");

        //grid.setSizeFull();
        grid.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent itemClickEvent) {

            }
        });

        updateTable(ACTIVE);

        //root.setSpacing(true);
        root.addComponent(grid);


        // All the open sub-windows should be closed whenever the root layout
        // gets clicked.
        root.addLayoutClickListener(new LayoutClickListener() {
            @Override
            public void layoutClick(final LayoutClickEvent event) {
                DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
            }
        });
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setSpacing(true);

        titleLabel = new Label("Proposal Listing");
        titleLabel.setId(TITLE_ID);
        titleLabel.setSizeUndefined();
        titleLabel.addStyleName(ValoTheme.LABEL_H1);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(titleLabel);

        Button active = new Button("Active");

        active.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);


        active.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                /*updateTable(ACTIVE);*/
            }
        });
        Button converted = new Button("Converted");

        converted.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);

        converted.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                /*updateTable(CONVERTED);*/
            }
        });
        Button cancelled = new Button("Cancel");

        cancelled.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);

        cancelled.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                /*updateTable(CANCELLED);*/
            }
        });
        Button assigned_to_me = new Button("Assigned to Me");
        assigned_to_me.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);

        assigned_to_me.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {


            }
        });


        HorizontalLayout tools = new HorizontalLayout(active,converted,cancelled,assigned_to_me);

        tools.setSpacing(true);

        tools.addStyleName("toolbar");
        header.addComponent(tools);

        return header;
    }

    private void updateTable(String proposalClass) {
        try {
            // Use the factory method of JsonContainer to instantiate the
            // data source for the table.

            JSONArray proposals = proposalDataProvider.getProposals(proposalClass);

            JsonContainer dataSource = JsonContainer.Factory.newInstance(proposals.toString());
            grid.setContainerDataSource(dataSource);



            grid.setColumnReorderingAllowed(true);


/*
            grid.setCellStyleGenerator(new Table.CellStyleGenerator() {
                @Override
                public String getStyle(Table table, Object o, Object o1) {
                    return null;
                }
            });
*/
        } catch (IllegalArgumentException ignored) {

        }

    }


    @Override
    public void enter(ViewChangeEvent viewChangeEvent) {

    }

}
