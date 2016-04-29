package com.mygubbi.game.dashboard.view.proposals;

import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.data.dummy.FileDataProviderUtil;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.vaadin.data.sort.Sort;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.renderers.Renderer;
import com.vaadin.ui.themes.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.teemu.jsoncontainer.JsonContainer;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    private Object sortContainerPropertyId;

/*
    private GridCellFilter filter;
*/

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
        grid = new Grid();

        grid.addColumn("Proposal ID",String.class);
        grid.addColumn("CRM #",String.class);
        grid.addColumn("Title",String.class);
        grid.addColumn("Creation Date", Date.class);
        grid.addColumn("Status",String.class);
        grid.addColumn("Amount",Integer.class);
        grid.addColumn("Last Updated By");
        grid.addColumn("Completion Date");
        grid.addColumn("Sales");
        grid.addColumn("Design");
        grid.addColumn("City");


        Object[][] people = { {"Nicolaus Copernicus", 143},
                {"Galileo Galilei", 1564},
                {"Johannes Kepler", 11}};
        for (Object[] person: people)
            grid.addRow(person);

    /*
        grid.addRow(new Dummy("dkhc",200));
        grid.addRow(new Dummy("dkhc",2));*/




        // grid.addStyleName(Reindeer.TABLE_STRONG);
        //grid.setWidth("800px");
        // grid.setHeightMode(HeightMode.ROW);
        //grid.setHeight("300px");

        //grid.setSizeFull();
        grid.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent itemClickEvent) {
                final String title = "Kitchen for Sanjay Gupta, Durga Solitaire";
                final ProposalDetailsView proposalDetailsView = new ProposalDetailsView("123", title);
                addTab(proposalDetailsView).setClosable(true);
                setSelectedTab(getComponentCount() - 1);
                getTab(proposalDetailsView).setCaption(title);

            }
        });

       // updateTable(ACTIVE);

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

        JSONArray proposal_classes = proposalDataProvider.getProposalClasses();

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

    public void updateTable(String proposalClass) {
        try {
            // Use the factory method of JsonContainer to instantiate the
            // data source for the table.


/*
            JSONArray proposals = proposalDataProvider.getProposals(proposalClass);

            JsonContainer dataSource = JsonContainer.Factory.newInstance(proposals.toString());
            grid.setContainerDataSource(dataSource);*/

            grid.setColumnReorderingAllowed(true);
            grid.setColumnOrder("crm_id", "title", "status", "last_actioned_by", "designer", "sales_contact", "total_amount", "create_dt", "project_city");
            grid.setColumnReorderingAllowed(true);

            grid.addColumn("name",String.class);
            grid.addColumn("age",Integer.class);


/*
            grid.addRow(new Dummy("dkhc",22));
            grid.addRow(new Dummy("dkhc",200));
            grid.addRow(new Dummy("dkhc",2));
*/



          /*  grid.getColumn("proposal_id").setHidden(true);

            grid.getColumn("crm_id").setHeaderCaption("CRM #");
            grid.getColumn("title").setHeaderCaption("Title");
            grid.getColumn("status").setHeaderCaption("Status");
            grid.getColumn("last_actioned_by").setHeaderCaption("Last Updated By");
            grid.getColumn("designer").setHeaderCaption("Design");
            grid.getColumn("sales_contact").setHeaderCaption("Sales");
            grid.getColumn("total_amount").setHeaderCaption("Amount");
            grid.getColumn("create_dt").setHeaderCaption("Creation Date");
            grid.getColumn("project_city").setHeaderCaption("City");

            grid.sort(Sort.by("total_amount", SortDirection.ASCENDING));
*/



/*
            grid.getColumn("total_amount").setConverter(new StringToIntegerConverter());
*/

/*
            this.filter=new GridCellFilter(grid);

            filter.setNumberFilter("total_amount");
            filter.setTextFilter("title",true,true);
            filter.setTextFilter("status",true,true);
            filter.setTextFilter("crm_id",true,true);
            filter.setTextFilter("last_actioned_by",true,true);
            filter.setTextFilter("designer",true,true);
            filter.setTextFilter("sales_contact",true,true);
            filter.setDateFilter("create_dt");
*/


/*
            grid.setColumnHeaders("CRM #", "Title", "Status", "Last Updated By", "Design", "Sales", "Amount", "Creation Date", "City");
*/
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

    }

    @Override
    public void enter(ViewChangeEvent viewChangeEvent) {

    }


    public Object getSortContainerPropertyId() {
        return sortContainerPropertyId;
    }
}
