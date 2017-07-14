package com.mygubbi.game.dashboard.view.proposals;

import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.domain.JsonPojo.LookupItem;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.event.ProposalEvent;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.tools.CvalChecker;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.gridutil.cell.GridCellFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.lang.StrictMath.round;

/**
 * Created by shruthi on 15-Apr-17.
 */
public class AllProposalLibrary extends Window
{
    private static final Logger LOG = LogManager.getLogger(AllProposalLibrary.class);
    Grid addonsGrid;
    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private BeanItemContainer<ProductLibrary> productsContainer;
    Product product;
    ProposalVersion proposalVersion;
    Proposal proposal;
    ProposalHeader proposalHeader;
    String Id;

    private AllProposalLibrary(Proposal proposal,ProposalVersion proposalVersion,ProposalHeader proposalHeader)
    {
        this.proposal=proposal;
        this.proposalVersion=proposalVersion;
        this.proposalHeader=proposalHeader;
        setModal(true);
        removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        addStyleName("module-window");
        setWidth("80%");
        setHeight("90%");
        setClosable(false);

        VerticalLayout verticalLayout = new VerticalLayout();
        setSizeFull();
        setContent(verticalLayout);

        Component componentheading=buildHeading();
        verticalLayout.addComponent(componentheading);

        Component componentAdd=buildAddButton();
        verticalLayout.addComponent(componentAdd);

        Component componentAddonDetails = buildAddons();
        verticalLayout.addComponent(componentAddonDetails);

        Component component=buildFooter();
        verticalLayout.addComponent(component);

    }
    public static void open(Proposal proposal,ProposalVersion proposalVersion,ProposalHeader proposalHeader)
    {

        AllProposalLibrary w=new AllProposalLibrary(proposal,proposalVersion,proposalHeader);
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
    private  Component buildFooter()
    {
        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setMargin(new MarginInfo(true,true,true,true));
        verticalLayout.setSizeFull();

        Button closeButton=new Button("Close");
        verticalLayout.addComponent(closeButton);
        verticalLayout.setComponentAlignment(closeButton,Alignment.MIDDLE_CENTER);
        closeButton.addClickListener((Button.ClickListener) clickEvent -> {
        close();
        });
        return verticalLayout;
    }

    private Component buildAddButton()
    {
        VerticalLayout verticalLayout=new VerticalLayout();
        verticalLayout.setMargin(new MarginInfo(false,true,false,true));
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
        verticalLayout.setMargin(new MarginInfo(false,true,false,true));

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
        addonsGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        addonsGrid.setColumns(ProductLibrary.COLLECTION,ProductLibrary.PRODUCT_CATEGORY_CODE,ProductLibrary.SUB_CATEGORY,ProductLibrary.PRODUCT_TITLE,ProductLibrary.SIZE,ProductLibrary.FINISH_TYPE_CODE,ProductLibrary.AMOUNT,ProductLibrary.DESIGNER,ProductLibrary.IMAGE_PATH,ProductLibrary.PRODUCT_LOCATION);
        addonsGrid.addSelectionListener( selectionEvent -> {
            if (!selectionEvent.getAdded().isEmpty()) {
                Object selected = ((Grid.SingleSelectionModel) addonsGrid.getSelectionModel()).getSelectedRow();
                Id = (String) addonsGrid.getContainerDataSource().getItem(selected).getItemProperty(ProductLibrary.ID).getValue().toString();
                    //LOG.info("grid selected value" +Id);
            }
        });

        GridCellFilter filter = new GridCellFilter(addonsGrid);

        filter.setTextFilter(ProductLibrary.COLLECTION,true,false);
        filter.setTextFilter(ProductLibrary.PRODUCT_CATEGORY_CODE,true,false);
        filter.setTextFilter(ProductLibrary.SIZE,true,false);
        filter.setTextFilter(ProductLibrary.DESIGNER,true,false);
        filter.setTextFilter(ProductLibrary.SUB_CATEGORY,true,false);
        filter.setTextFilter(ProductLibrary.PRODUCT_TITLE,true,false);
        filter.setTextFilter(ProductLibrary.PRODUCT_CATEGORY_CODE,true,false);
        filter.setTextFilter(ProductLibrary.AMOUNT,true,false);
        filter.setTextFilter(ProductLibrary.FINISH_TYPE_CODE,true,false);

        List<Grid.Column> columns = addonsGrid.getColumns();
        int idx = 0;
        columns.get(idx++).setHeaderCaption("Collection");
        columns.get(idx++).setHeaderCaption("Category");
        columns.get(idx++).setHeaderCaption("Subcategory");
        columns.get(idx++).setHeaderCaption("Name");
        columns.get(idx++).setHeaderCaption("Size");
        columns.get(idx++).setHeaderCaption("Finish");
        columns.get(idx++).setHeaderCaption("Price");
        columns.get(idx++).setHeaderCaption("Designer");
        columns.get(idx++).setHeaderCaption("Image").setRenderer(new HtmlRenderer(), new Converter<String, String>()
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
                return "<img src='" +value+ "' width='100' height='100'> <style> a, a:visited, a:hover { color: #4396ea;} .v-grid-row-selected .img-anc { color: #fff; } .v-grid-row-selected .img-anc:hover { color: #fff; }</style>";
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

        columns.get(idx++).setHeaderCaption("Location").setRenderer(new HtmlRenderer(), new Converter<String, String>()
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
                return "<a href='" + value +"' target='_blank' onclick='window.open('" + value + "' ,'popup','width=600,height=600')>Open Link in Popup </a>";
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
        genContainer.addGeneratedProperty("productCategoryText", getProductCategoryTextGenerator());
        return genContainer;
    }

    private GeneratedPropertyContainer createGeneratedProductPropertyContainer() {
        GeneratedPropertyContainer genContainer = new GeneratedPropertyContainer(productsContainer);
        genContainer.addGeneratedProperty("actions", getActionTextGenerator());
        genContainer.addGeneratedProperty("productCategoryText", getProductCategoryTextGenerator());
        return genContainer;
    }

    private PropertyValueGenerator<String> getActionTextGenerator() {
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

    private PropertyValueGenerator<String> getProductCategoryTextGenerator() {
        return new PropertyValueGenerator<String>() {

            @Override
            public String getValue(Item item, Object o, Object o1) {
                ProductLibrary product = (ProductLibrary) ((BeanItem) item).getBean();

                if (product.getType().equals(Product.TYPES.CUSTOMIZED.name()) || product.getType().equals(Product.TYPES.PRODUCT_LIBRARY.name())) {
                    if (StringUtils.isNotEmpty(product.getProductCategory())) {
                        return product.getProductCategory();
                    } else {
                        List<LookupItem> lookupItems = proposalDataProvider.getLookupItems(ProposalDataProvider.CATEGORY_LOOKUP);
                        return lookupItems.stream().filter(lookupItem -> lookupItem.getCode().equals(product.getProductCategoryCode())).findFirst().get().getTitle();
                    }
                } else {
                    List<LookupItem> subCategories = proposalDataProvider.getLookupItems(ProposalDataProvider.SUB_CATEGORY_LOOKUP);
                    return subCategories.stream().filter(
                            lookupItem -> lookupItem.getCode().equals(product.getProductCategoryCode()))
                            .collect(Collectors.toList()).get(0).getTitle();
                }
            }
            @Override
            public Class<String> getType() {
                return String.class;
            }
        };
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
        List<ProductLibrary> productLibraries=proposalDataProvider.getProductsLibraryBasedonId(Id);
        for(ProductLibrary p:productLibraries)
        {
            Product product=new Product();
            product.setType(Product.TYPES.PRODUCT_LIBRARY.name());
           // LOG.info("product type name " +Product.TYPES.PRODUCT_LIBRARY.name());
            //product.setSeq(length);
            product.setProposalId(proposalHeader.getId());
            product.setFromVersion(proposalVersion.getVersion());
          //  product.setTitle(p.getTitle());
            product.setProductCategory(p.getProductCategory());
            product.setProductCategoryCode(p.getProductCategoryCode());
           // product.setRoom(p.getRoom());
            //product.setRoomCode(p.getRoomCode());
            product.setShutterDesign(p.getShutterDesign());
            product.setShutterDesignCode(p.getShutterDesignCode());
            product.setCatalogueName(p.getCatalogueName());
            product.setCatalogueId(p.getCatalogueId());
            product.setBaseCarcass(p.getBaseCarcass());
            product.setBaseCarcassCode(p.getBaseCarcassCode());
            product.setWallCarcass(p.getWallCarcass());
            product.setWallCarcassCode(p.getWallCarcassCode());
            product.setFinishType(p.getFinishType());
            product.setFinishTypeCode(p.getFinishTypeCode());
            product.setFinish(p.getFinish());
            product.setFinishCode(p.getFinishCode());
            product.setDimension(p.getDimension());
            product.setAmount(p.getAmount());
            product.setQuantity(p.getQuantity());
            product.setType(p.getType());
            product.setQuoteFilePath(p.getQuoteFilePath());
            product.setCreatedBy(p.getCreatedBy());
            product.setCostWoAccessories(p.getCostWoAccessories());
            product.setProfit(p.getProfit());
            product.setMargin(p.getMargin());
            product.setManufactureAmount(p.getManufactureAmount());
            product.setAmountWoTax(p.getAmountWoTax());
            product.setHandleType(p.getHandleType());
            product.setKnobType(p.getKnobType());
            product.setHandleFinish(p.getHandleFinish());
            product.setKnobFinish(p.getKnobFinish());
            product.setHandleImage(p.getHandleImage());
            product.setKnobImage(p.getKnobImage());
            product.setGlass(p.getGlass());
            product.setHinge(p.getHinge());
            product.setHandleTypeSelection(p.getHandleTypeSelection());
            product.setCloseButtonFlag("Yes");
            product.setShutterDesignImage(p.getShutterImageUrl());
            product.setHandleThickness(p.getHandleThickness());
            product.setHandleCode(p.getHandleCode());
            product.setKnobCode(p.getKnobCode());
            product.setlConnectorPrice(p.getlConnectorPrice());
            product.setNoOfLengths(p.getNoOfLengths());
            List<Module> modules = p.getModules();
            List<Module> refreshedModules = new ArrayList<>();
            for (Module refreshedModule: modules)
            {
                ModuleForPrice moduleForPrice = new ModuleForPrice();

                if (proposalHeader.getPriceDate() == null) {
                    java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
                    moduleForPrice.setPriceDate( date);
                }
                else
                {
                    moduleForPrice.setPriceDate(proposalHeader.getPriceDate());
                    moduleForPrice.getModule().setModuleSource("Button");
                }

                moduleForPrice.setCity(proposalHeader.getPcity());
                moduleForPrice.setProduct(product);

                moduleForPrice.setModule(refreshedModule);
                ModulePrice modulePrice = proposalDataProvider.getModulePrice(moduleForPrice);
                double amount = round(modulePrice.getTotalCost());
                double areainsft = modulePrice.getModuleArea();
                double costwoaccessories = round(modulePrice.getWoodworkCost());


                modules.get(modules.indexOf(refreshedModule)).setAmount(amount);
                modules.get(modules.indexOf(refreshedModule)).setAmountWOAccessories(costwoaccessories);
                modules.get(modules.indexOf(refreshedModule)).setArea(areainsft);
            }
            product.setModules(refreshedModules);
            product.setModules(p.getModules());
            product.setSource(p.getSource());
            product.setAddons(p.getAddons());
            //LOG.debug("NEW PRoduct crearted from product library"+ product);
            proposalDataProvider.updateProduct(product);
//            Product updatedProduct = proposalDataProvider.updateProductWithRefreshedPrice(product);
            close();
           // LOG.info("windows ***" +UI.getCurrent().getWindows());
            CustomizedProductDetailsWindow.open(proposal,product,proposalVersion,proposalHeader);
        }
    }
}

