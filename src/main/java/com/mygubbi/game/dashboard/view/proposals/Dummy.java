package com.mygubbi.game.dashboard.view.proposals;

import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.config.ConfigHolder;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.Addon;
import com.mygubbi.game.dashboard.domain.AddonProduct;
import com.mygubbi.game.dashboard.domain.JsonPojo.AddonMaster;
import com.mygubbi.game.dashboard.domain.ProposalHeader;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ClickableRenderer;
import com.vaadin.ui.renderers.ImageRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.gridutil.cell.GridCellFilter;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer;

import java.util.List;

/**
 * Created by user on 05-Apr-17.
 */
public class Dummy extends Window
{
    private static final Logger LOG = LogManager.getLogger(Dummy.class);
    Grid addonsGrid;
    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private BeanItemContainer<AddonMaster> addonsContainer;
    private Embedded productImage;

    private Dummy()
    {
        setModal(true);
        removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        addStyleName("module-window");
        setWidth("80%");
        setHeight("90%");
        setClosable(false);

        VerticalLayout verticalLayout = new VerticalLayout();

        setSizeFull();

        //verticalLayout.setSpacing(true);
        setContent(verticalLayout);

        /*Component componentHeading=buildHeading();
        verticalLayout.addComponent(componentHeading);*/

        Component componentAddonDetails = buildAddons();
        verticalLayout.addComponent(componentAddonDetails);

    }
    public static void open()
    {
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
        Dummy w=new Dummy();
        //ProductAndAddons w = new ProductAndAddons(proposalHeader,proposal,vid,proposalVersion);
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

        /*Component headingbutton=buildHeadingButtons();
        formLayoutLeft.addComponent(headingbutton);*/

        //formLayoutLeft.setComponentAlignment(headingbutton,Alignment.TOP_LEFT);
        return formLayoutLeft;

    }

    private Component buildAddons() {

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setMargin(new MarginInfo(true,true,true,true));

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        horizontalLayout.setStyleName("v-has-width-forLabel");

        /*Label addonTitle = new Label("Addon Details");
        addonTitle.setStyleName("products-and-addons-label-text");
        horizontalLayout.addComponent(addonTitle);
        horizontalLayout.setComponentAlignment(addonTitle,Alignment.TOP_LEFT);
        verticalLayout.setSpacing(true);*/

        HorizontalLayout hLayoutInner = new HorizontalLayout();

        addonsContainer = new BeanItemContainer<>(AddonMaster.class);

        GeneratedPropertyContainer genContainer = createGeneratedAddonsPropertyContainer();

        addonsGrid = new Grid(genContainer);
        addonsGrid.addStyleName("v-lst-event");
        addonsGrid.setRowStyleGenerator(new Grid.RowStyleGenerator() {
            @Override
            public String getStyle(Grid.RowReference rowReference) {
                return "v-grid-header";
                //return "v-grid-header";
            }
        });

        /*addonsGrid.addStyleName("v-lst-event1");
        addonsGrid.setRowStyleGenerator(new Grid.RowStyleGenerator() {
            @Override
            public String getStyle(Grid.RowReference rowReference) {
                return "v-grid-header";
            }
        });*/




        //addonsGrid.setHeightByRows(100);

        //addonsGrid.setStyleName("v-lst-event");//define your own style name

        addonsGrid.setSelectionMode(Grid.SelectionMode.NONE);
        addonsGrid.setSizeFull();
        addonsGrid.setHeightByRows(11);
        addonsGrid.setHeightMode(HeightMode.ROW);
        /*addonsGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        addonsGrid.addSelectionListener(this::updateTotal);*/
        /*addonsGrid.setColumnReorderingAllowed(true);
        addonsGrid.addColumn(AddonMaster.CATALOUGE_CODE);
        addonsGrid.addColumn(AddonMaster.BRANCH_CODE);
        addonsGrid.addColumn(AddonMaster.TITLE);
        addonsGrid.addColumn("picture", Resource.class).setRenderer(new ImageRenderer());*/
        addonsGrid.setColumns(AddonMaster.IMAGE_PATH,AddonMaster.PRODUCT,AddonMaster.CATEGORY_CODE,AddonMaster.CATALOUGE_CODE);
        //addonsGrid.getColumn(AddonMaster.IMAGE_PATH).setWidth(100);

        addonsGrid.addSelectionListener(selectionEvent -> {
            if (!selectionEvent.getAdded().isEmpty()) {
                Object selected = ((Grid.SingleSelectionModel) addonsGrid.getSelectionModel()).getSelectedRow();
                int proposalId = (Integer) addonsGrid.getContainerDataSource().getItem(selected).getItemProperty(ProposalHeader.ID).getValue();

            }
        });

        GridCellFilter filter = new GridCellFilter(addonsGrid);
        filter.setTextFilter(AddonMaster.CATALOUGE_CODE,true,true);
        filter.setTextFilter(AddonMaster.TITLE,true,true);
        filter.setTextFilter(AddonMaster.CATEGORY_CODE,true,true);


        /*emptyImage = new ThemeResource("img/empty-poster.png");
        productImage = new Embedded("", emptyImage);*/

        /*String imageBasePath = ConfigHolder.getInstance().getCatalogueImageBasePath();
        this.productImage = new Embedded("", new ExternalResource(imageBasePath + product.getImages().get(0)));*/


        /*addonsGrid.setColumns(AddonProduct.SEQ, AddonProduct.ADDON_CATEGORY_CODE, AddonProduct.PRODUCT_TYPE_CODE, AddonProduct.PRODUCT_SUBTYPE_CODE, AddonProduct.BRAND_CODE,
                AddonProduct.PRODUCT, AddonProduct.UOM, AddonProduct.RATE, AddonProduct.QUANTITY, AddonProduct.AMOUNT, "actions");*/

        List<Grid.Column> columns = addonsGrid.getColumns();
        int idx = 0;
        columns.get(idx++).setHeaderCaption("image").setRenderer(new ImageRenderer());
        columns.get(idx++).setHeaderCaption("Title");
        columns.get(idx++).setHeaderCaption("Product Category");
        columns.get(idx++).setHeaderCaption("Product Subcategory");

//        addonsGrid.getColumn("image").setWidth(100);
        /*columns.get(idx++).setHeaderCaption("Brand");
        columns.get(idx++).setHeaderCaption("Product Name");
        columns.get(idx++).setHeaderCaption("UOM");
        columns.get(idx++).setHeaderCaption("Rate");
        columns.get(idx++).setHeaderCaption("Qty");
        columns.get(idx++).setHeaderCaption("Amount");
        Grid.Column actionColumn = columns.get(idx++);*/

        verticalLayout.addComponent(horizontalLayout);
        verticalLayout.setSpacing(true);

        verticalLayout.addComponent(addonsGrid);

        List<AddonMaster> existingAddons = proposalDataProvider.getAddondDetails("ADDON58");
        for(AddonMaster addonMaster:existingAddons)
        {
            LOG.info("Addon master " +addonMaster);
            //addonsContainer.addItem(addonMaster.getImagePath(),addonMaster.getCategoryCode(),addonMaster.getCatalogueCode());
        }
        int seq = 0;
        /*for (AddonMaster existingAddon : existingAddons) {
            existingAddon.setSeq(++seq);
        }*/
        addonsContainer.addAll(existingAddons);

//        addonsGrid.sort(AddonProduct.SEQ, SortDirection.ASCENDING);

        return verticalLayout;

    }
    private GeneratedPropertyContainer createGeneratedAddonsPropertyContainer() {
        GeneratedPropertyContainer genContainer = new GeneratedPropertyContainer(addonsContainer);
        genContainer.addGeneratedProperty("actions", getEmptyActionTextGenerator());
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

}
