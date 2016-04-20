package com.mygubbi.game.dashboard.view.Catalog;



import com.mygubbi.game.dashboard.data.CatalogDataProvider;
import com.mygubbi.game.dashboard.data.dummy.FileDataProviderUtil;
import org.vaadin.teemu.jsoncontainer.JsonContainer;

import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class CatalogProductsView extends TabSheet implements View {

    public static final String TITLE_ID = "proposals-title";

    private Label titleLabel;

    private Window window;

    private Table grid;

    private static final String ACTIVE = "active";
    private static final String CONVERTED = "converted";
    private static final String CANCELLED = "cancelled";
    private final VerticalLayout root;
    private CatalogDataProvider productsDataProvider = new CatalogDataProvider(new FileDataProviderUtil());
    private Object sortContainerPropertyId;

    public CatalogProductsView() {
        addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
/*        DashboardEventBus.register(this);
*/

        root = new VerticalLayout();
        root.setWidth("100%");
        root.setMargin(true);
        root.setCaption("All Products");


        addTab(root);

        Responsive.makeResponsive(root);



        // root.addComponent(buildHeader());
/*
        Label label = new Label("Proposal Listing");
        label.addStyleName(ValoTheme.LABEL_H1);*/
        //  label.addStyleName(ValoTheme.LABEL_NO_MARGIN);

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


        // grid.addStyleName(Reindeer.TABLE_STRONG);
        grid.setWidth("100%");
        // grid.setHeightMode(HeightMode.ROW);
        grid.setHeight("100%");

        //grid.setSizeFull();
        /*grid.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent itemClickEvent) {
                final String title = "Kitchen for Sanjay Gupta, Durga Solitaire";
                final CatalogDetailsView proposalDetailsView = new ProposalDetailsView("123", title);
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
/*                DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
*/            }
        });

    }


    public Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setSpacing(true);

        titleLabel = new Label("Products");
        titleLabel.setId(TITLE_ID);
        titleLabel.setSizeUndefined();
        titleLabel.addStyleName(ValoTheme.LABEL_H1);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(titleLabel);

        JSONArray proposal_classes = productsDataProvider.getCatalogClasses();
        HorizontalLayout tools = new HorizontalLayout();

        for(int i=0;i<proposal_classes.length();i++)
        {
            try {
                JSONObject jsonObject = proposal_classes.getJSONObject(i);
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

    public void updateTable(String catalogClass) {
        try {
            // Use the factory method of JsonContainer to instantiate the
            // data source for the table.

            JSONArray proposals = productsDataProvider.getCatalogs(catalogClass);

            JsonContainer dataSource = JsonContainer.Factory.newInstance(proposals.toString());
            grid.setContainerDataSource(dataSource);


            grid.setColumnReorderingAllowed(true);
            grid.setVisibleColumns("productId", "name", "defaultPrice", "dimension", "designer", "subcategory", "categoryId", "subcategoryId", "defaultMaterial");
            grid.setColumnHeaders("Product ID #", "Name", "Description","Dimension", "Designer", "Subcategory", "CategoryId", "SubcategoryId", "Def_material");

            grid.setWidth("98%");






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

    }



    @Override
    public void enter(ViewChangeEvent viewChangeEvent) {

    }


    public Object getSortContainerPropertyId() {
        return sortContainerPropertyId;
    }


}
