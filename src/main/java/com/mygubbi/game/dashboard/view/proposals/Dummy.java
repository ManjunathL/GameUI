package com.mygubbi.game.dashboard.view.proposals;

import com.google.gwt.view.client.MultiSelectionModel;
import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.config.ConfigHolder;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.domain.JsonPojo.AddonMaster;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.*;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ClickableRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.ImageRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.logging.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.gridutil.cell.GridCellFilter;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Created by user on 05-Apr-17.
 */
public class Dummy extends Window
{
    private static final Logger LOG = LogManager.getLogger(Dummy.class);
    Grid addonsGrid;
    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private BeanItemContainer<ProductLibrary> productsContainer;
    Product product;
    ProposalVersion proposalVersion;

    private Dummy(ProposalVersion proposalVersion)
    {
        this.proposalVersion=proposalVersion;
        setModal(true);
        removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        addStyleName("module-window");
        setWidth("80%");
        setHeight("90%");
        setClosable(false);

        VerticalLayout verticalLayout = new VerticalLayout();
        setSizeFull();
        setContent(verticalLayout);

        Component componentAdd=buildAddButton();
        verticalLayout.addComponent(componentAdd);

        Component componentAddonDetails = buildAddons();
        verticalLayout.addComponent(componentAddonDetails);

    }
    public static void open(ProposalVersion proposalVersion)
    {
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
        Dummy w=new Dummy(proposalVersion);
        UI.getCurrent().addWindow(w);
        w.focus();
    }

    private Component buildHeading()
    {
        HorizontalLayout formLayoutLeft = new HorizontalLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        Label customerDetailsLabel = new Label("Products Library");
        customerDetailsLabel.addStyleName(ValoTheme.LABEL_HUGE);
        customerDetailsLabel.addStyleName(ValoTheme.LABEL_COLORED);
        customerDetailsLabel.addStyleName("products-and-addons-heading-text");
        formLayoutLeft.addComponent(customerDetailsLabel);

        return formLayoutLeft;

    }
    private Component buildAddButton()
    {
        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setMargin(new MarginInfo(true,true,true,true));
        verticalLayout.setSizeFull();

        Button addButton=new Button("ADD");
        verticalLayout.addComponent(addButton);
        verticalLayout.setComponentAlignment(addButton,Alignment.TOP_RIGHT);
        addButton.addClickListener(this::AddToProposalProduct);

        return verticalLayout;

    }
    private Component buildAddons() {

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setMargin(new MarginInfo(true,true,true,true));

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        horizontalLayout.setStyleName("v-has-width-forLabel");

        productsContainer = new BeanItemContainer<>(ProductLibrary.class);
        GeneratedPropertyContainer genContainer = createGeneratedAddonsPropertyContainer();
        addonsGrid = new Grid(genContainer);
        addonsGrid.addStyleName("v-lst-event");
        addonsGrid.setRowStyleGenerator(new Grid.RowStyleGenerator() {
            @Override
            public String getStyle(Grid.RowReference rowReference) {
                return "v-grid-header";
            }
        });
        addonsGrid.setSizeFull();
        addonsGrid.setHeightByRows(11);
        addonsGrid.setHeightMode(HeightMode.ROW);
        //Grid.MultiSelectionModel multiselection=addonsGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        //MultiSelectionModel<ProductLibrary> selectionModel = (MultiSelectionModel<ProductLibrary>) addonsGrid.setSelectionMode(Grid.SelectionMode.MULTI);


        //addonsGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        addonsGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        //grid.setSelectionMode(SelectionMode.MULTI);
        //addonsGrid.addSelectionListener(this::updateTotal);
        //addonsGrid.setColumns(AddonMaster.PRODUCT,AddonMaster.CATEGORY_CODE,AddonMaster.CATALOUGE_CODE,AddonMaster.IMAGE_PATH);
        addonsGrid.setColumns(ProductLibrary.PRODUCT_CATEGORY_CODE,ProductLibrary.SUB_CATEGORY,ProductLibrary.TITLE,ProductLibrary.IMAGE_PATH);



       /* selectionModel.addMultiSelectionListener(event -> {
        Notification.show(selection.getAddedSelection().size()
                    + " items added, "
                    + selection.getRemovedSelection().size()
                    + " removed.");

            // Allow deleting only if there's any selected
            deleteSelected.setEnabled(
                    event.getNewSelection().size() > 0);
        });*/


        addonsGrid.addSelectionListener( selectionEvent -> {
            if (!selectionEvent.getAdded().isEmpty()) {
                //Object selected = ((Grid.MultiSelectionModel) addonsGrid.getSelectionModel()).getSelectedRows();
                Collection<Object> selected = ((Grid.MultiSelectionModel) addonsGrid.getSelectionModel()).getSelectedRows();
                for(Object obj:selected)
                {
                    String Title = (String) addonsGrid.getContainerDataSource().getItem(obj).getItemProperty(ProductLibrary.TITLE).getValue();
                    LOG.info("grid selected value" +Title);
                }
            }
        });

        GridCellFilter filter = new GridCellFilter(addonsGrid);
        filter.setTextFilter(ProductLibrary.PRODUCT_CATEGORY_CODE,true,true);
        filter.setTextFilter(ProductLibrary.SUB_CATEGORY,true,true);
        filter.setTextFilter(ProductLibrary.TITLE,true,true);

        List<Grid.Column> columns = addonsGrid.getColumns();
        int idx = 0;
        //columns.get(idx++).setHeaderCaption("image").setRenderer(new ImageRenderer());
        columns.get(idx++).setHeaderCaption("Product Category");
        columns.get(idx++).setHeaderCaption("Product Subcategory");
        columns.get(idx++).setHeaderCaption("Title");
        columns.get(idx++).setHeaderCaption("Image Path").setRenderer(new HtmlRenderer(), new Converter<String, String>()
        {
            @Override
            public String convertToModel(String value,
                                         Class<? extends String> targetType, Locale locale)
                    throws Converter.ConversionException {
                return "not implemented";
            }

            @Override
            public String convertToPresentation(String value, Class<? extends String> targetType, Locale locale)
                    throws com.vaadin.data.util.converter.Converter.ConversionException {
                //LOG.info("Addon master image path " +AddonMaster.IMAGE_PATH);
                return "<a href='" + value + "' target='_blank'>click to view image</a>";
            }

            @Override
            public Class<String> getModelType() {
                return String.class;
            }

            @Override
            public Class<String> getPresentationType() {
                return String.class;
            }
        });
        verticalLayout.addComponent(horizontalLayout);
        verticalLayout.setSpacing(true);
        verticalLayout.addComponent(addonsGrid);

        List<ProductLibrary> products = proposalDataProvider.getAllProductsLibrary();
        productsContainer.addAll(products);
        return verticalLayout;

    }
    private GeneratedPropertyContainer createGeneratedAddonsPropertyContainer() {
        GeneratedPropertyContainer genContainer = new GeneratedPropertyContainer(productsContainer);
        genContainer.addGeneratedProperty("Link", getEmptyActionTextGenerator());
        return genContainer;
    }
    private PropertyValueGenerator<String> getEmptyActionTextGenerator() {
        return new PropertyValueGenerator<String>() {

            @Override
            public String getValue(Item item, Object o, Object o1) {
                return "";
            }

            @Override
            public Class<String> getType() {
                return String.class;
            }
        };
    }

    private void AddToProposalProduct(Button.ClickEvent clickEvent)
    {
        Collection<Object> selected = ((Grid.MultiSelectionModel) addonsGrid.getSelectionModel()).getSelectedRows();
        for(Object obj:selected)
        {
            Integer proposalId = (Integer) addonsGrid.getContainerDataSource().getItem(obj).getItemProperty(ProductLibrary.PROPOSAL_ID).getValue();
            LOG.info("ProposalId "+proposalId);
            String version=(String) addonsGrid.getContainerDataSource().getItem(obj).getItemProperty(ProductLibrary.FROM_VERSION).getValue();
            Integer seq=(Integer) addonsGrid.getContainerDataSource().getItem(obj).getItemProperty(ProductLibrary.SEQ).getValue();

            List<ProductLibrary> productList=proposalDataProvider.getProductsLibrary(proposalId.toString(),seq.toString(),version);
            for(ProductLibrary productlibrary:productList)
            {
                LOG.info("Product library" +productlibrary);
                close();
            }
        }
    }
}
