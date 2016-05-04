package com.mygubbi.game.dashboard.view.proposals;

import com.google.common.eventbus.Subscribe;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.data.dummy.FileDataProviderUtil;
import com.mygubbi.game.dashboard.domain.ProposalListItem;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.event.ProposalEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.gridutil.cell.GridCellFilter;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private GridCellFilter filter;

    public ProposalsView() {
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

        List<ProposalListItem> proposalListItems = getProposalItems("active");
        BeanItemContainer<ProposalListItem> container = new BeanItemContainer<ProposalListItem>(ProposalListItem.class, proposalListItems);

        grid = new Grid(container);
        grid.setSizeFull();
        grid.setColumnReorderingAllowed(true);
        grid.setColumnOrder("crmId", "title", "status", "sales", "designer", "amount", "city", "lastUpdatedBy", "creationDate", "completionDate");

        this.filter = new GridCellFilter(grid);

        filter.setNumberFilter("amount");
        filter.setTextFilter("title", true, true);
        filter.setTextFilter("status", true, true);
        filter.setTextFilter("crmId", true, true);
        filter.setTextFilter("lastUpdatedBy", true, true);
        filter.setTextFilter("designer", true, true);
        filter.setTextFilter("sales", true, true);
        filter.setDateFilter("creationDate");

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

    @Subscribe
    public void proposalsDataUpdated(final ProposalEvent.ProposalUpdated event) {
        List<ProposalListItem> proposalListItems = getProposalItems("active");
        BeanItemContainer<ProposalListItem> container = new BeanItemContainer<ProposalListItem>(ProposalListItem.class, proposalListItems);
        this.grid.setContainerDataSource(container);
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

        for (int i = 0; i < proposal_classes.length(); i++) {
            try {
                JSONObject jsonObject = proposal_classes.getJSONObject(i);
                int id = jsonObject.getInt("count");
                String name = jsonObject.getString("class");

                Button status_button = new Button(name + " (" + id + ")");
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

    public List<ProposalListItem> getProposalItems(String proposalClass) {
        List<ProposalListItem> proposalListItems = new ArrayList<>();

        JSONArray proposals = proposalDataProvider.getProposals(proposalClass);
        try {
            for (int i = 0; i < proposals.length(); i++) {

                ProposalListItem proposalListItem = new ProposalListItem();
                JSONObject jsonObject = proposals.getJSONObject(i);

                proposalListItem.setProposalId(jsonObject.getString("proposal_id"));
                proposalListItem.setCrmId(jsonObject.getString("crm_id"));
                proposalListItem.setTitle(jsonObject.getString("title"));
                proposalListItem.setCreationDate(getDate(jsonObject));
                proposalListItem.setStatus(jsonObject.getString("status"));
                proposalListItem.setAmount(jsonObject.getInt("total_amount"));
                proposalListItem.setLastUpdatedBy(jsonObject.getString("last_actioned_by"));
                proposalListItem.setCompletionDate(jsonObject.getString("completion_dt"));
                proposalListItem.setSales(jsonObject.getString("sales_contact"));
                proposalListItem.setDesigner(jsonObject.getString("designer"));
                proposalListItem.setCity(jsonObject.getString("project_city"));

                proposalListItems.add(proposalListItem);
            }

            return proposalListItems;

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("Parse Exception", e);
        }
    }

    private Date getDate(JSONObject jsonObject) throws JSONException, ParseException {
        String createDt = jsonObject.getString("create_dt");
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(createDt);
    }

    @Override
    public void enter(ViewChangeEvent viewChangeEvent) {

    }


    public Object getSortContainerPropertyId() {
        return sortContainerPropertyId;
    }
}
