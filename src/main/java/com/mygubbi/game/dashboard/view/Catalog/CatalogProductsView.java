package com.mygubbi.game.dashboard.view.Catalog;

import com.mygubbi.game.dashboard.data.CatalogDataProvider;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.data.dummy.FileDataProviderUtil;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mysql.jdbc.Connection;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.teemu.jsoncontainer.JsonContainer;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

import java.sql.*;

@SuppressWarnings("serial")
public final class CatalogProductsView extends TabSheet implements View {

    public static final String TITLE_ID = "proposals-title";

    private Label titleLabel;

    private Window window;

    private Table grid;
    Connection con;
    PreparedStatement ps;
    Statement cs;
    ResultSet rs;
    String dbUrl = "jdbc:mysql://localhost:3306/mg";
    Window main = new Window("Sample");

    private static final String ACTIVE = "active";
    private static final String CONVERTED = "converted";
    private static final String CANCELLED = "cancelled";
    private final VerticalLayout root;
    private CatalogDataProvider productsDataProvider = new CatalogDataProvider(new FileDataProviderUtil());
    private Object sortContainerPropertyId;

    public CatalogProductsView() {
        addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        DashboardEventBus.register(this);


        root = new VerticalLayout();
        /*root.setSizeFull();*/
        root.setMargin(true);
        root.setCaption("All Proposals");


        addTab(root);

        Responsive.makeResponsive(root);

        root.addComponent(buildHeader());
        root.setSpacing(true);

     /*   Button test = new Button("Open Proposal");
        final String title = "Kitchen for Sanjay Gupta, Durga Solitaire";
        final ProposalDetailsView proposalDetailsView = new ProposalDetailsView("123", title);

        test.addClickListener(event -> {
            addTab(proposalDetailsView).setClosable(true);
            setSelectedTab(getComponentCount() - 1);
            getTab(proposalDetailsView).setCaption(title);
        });
        root.addComponent(test);
*/
        grid = new Table();
        root.addComponent(grid);
        grid.setStyleName("iso3166");
        grid.setPageLength(6);
        grid.setSizeFull();
        grid.setSelectable(true);
        grid.setMultiSelect(false);
        grid.setImmediate(true);
        grid.setColumnReorderingAllowed(true);
        grid.setColumnCollapsingAllowed(true);


        // grid.addStyleName(Reindeer.TABLE_STRONG);
        //grid.setWidth("800px");
        // grid.setHeightMode(HeightMode.ROW);
        //grid.setHeight("300px");

        //grid.setSizeFull();
    /*    grid.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent itemClickEvent) {
                final String title = "Kitchen for Sanjay Gupta, Durga Solitaire";
                final ProposalDetailsView proposalDetailsView = new ProposalDetailsView("123", title);
                addTab(proposalDetailsView).setClosable(true);
                setSelectedTab(getComponentCount() - 1);
                getTab(proposalDetailsView).setCaption(title);

            }
        });*/

        updateTable(ACTIVE);

        grid.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent itemClickEvent) {
                Notification.show("Clicked");
            }
        });

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


    public Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setSpacing(true);

        titleLabel = new Label("Proposals");
        titleLabel.setId(TITLE_ID);
        titleLabel.setSizeUndefined();
        titleLabel.addStyleName(ValoTheme.LABEL_H1);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(titleLabel);
        header.setSpacing(true);

        JSONArray catalog_classes = productsDataProvider.getCatalogClasses();

        HorizontalLayout tools = new HorizontalLayout();

        for(int i=0;i<catalog_classes.length();i++)
        {
            try {
                JSONObject jsonObject = catalog_classes.getJSONObject(i);
                int id = jsonObject.getInt("count");
                String name=jsonObject.getString("class");

                Button status_button=new Button(name+" ("+id+")");
                status_button.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);

                tools.addComponent(status_button);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        tools.setSpacing(true);

        tools.addStyleName("toolbar");
        header.addComponent(tools);

        return header;
    }

    public void updateTable(String catlogClass) {
        try {


            JSONArray catalogs = productsDataProvider.getCatalogs(catlogClass);

            JsonContainer dataSource = JsonContainer.Factory.newInstance(catalogs.toString());
            grid.setContainerDataSource(dataSource);


            grid.setColumnReorderingAllowed(true);
            grid.setVisibleColumns("crm_id", "title", "status", "last_actioned_by", "designer", "sales_contact", "create_dt", "project_city","total_amount");
            grid.setColumnHeaders("CRM #", "Title", "Status", "Last Updated By", "Design", "Sales", "Creation Date", "City","Total Amount");
            grid.setWidth("98%");
            grid.addStyleName(ChameleonTheme.TABLE_STRIPED);






            //grid.setHeightByRows();


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
        grid.setWidth("100%");
        grid.setHeight("100%");

        root.addComponent(grid);
    }



    @Override
    public void enter(ViewChangeEvent viewChangeEvent) {

    }


    public Object getSortContainerPropertyId() {
        return sortContainerPropertyId;
    }
}
