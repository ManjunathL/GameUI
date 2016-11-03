package com.mygubbi.game.dashboard.view.editMasterComponents;

import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.MasterDataProvider;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.data.dummy.FileDataProviderMode;
import com.mygubbi.game.dashboard.domain.MasterData;
import com.mygubbi.game.dashboard.domain.ProposalHeader;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.LayoutEvents;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.gridutil.cell.GridCellFilter;

import java.util.List;

/**
 * Created by test on 27-06-2016.
 */
public class EditMasterComponents extends Panel implements View {
    public static final String EDIT_ID = "masterComponents-edit";
    public static final String TITLE_ID = "masterComponents-title";


    private Label titleLabel;
    private final VerticalLayout root;
    private Grid grid;

    public EditMasterComponents() {
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();
        DashboardEventBus.register(this);

        root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        Responsive.makeResponsive(root);

        Component header = buildHeader();
        root.addComponent(header);
        root.setExpandRatio(header, 0.1f);

        setContent(root);

    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setSpacing(true);

        titleLabel = new Label("Setup Master Data");
        titleLabel.setId(TITLE_ID);
        // titleLabel.setSizeUndefined();
        titleLabel.addStyleName(ValoTheme.LABEL_H1);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(titleLabel);

        return header;
    }

    private Component buildGrid(List<MasterData> masterData) {
        BeanItemContainer<MasterData> container = buildDataContainer(masterData);
        grid = new Grid(container);
        grid.setSizeFull();
        grid.setColumnReorderingAllowed(true);
        grid.setColumns(MasterData.TABLE, MasterData.DESCRIPTION);


        List<Grid.Column> columns = grid.getColumns();
        int idx = 0;
        columns.get(idx++).setHeaderCaption("Table Name").setExpandRatio(2);
        columns.get(idx++).setHeaderCaption("Description").setExpandRatio(8);

        container.addAll(masterData);
        return grid;
    }

    private BeanItemContainer<MasterData> buildDataContainer(List<MasterData> masterData) {
        BeanItemContainer<MasterData> container = new BeanItemContainer<>(MasterData.class);
        return container;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
