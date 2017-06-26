package com.mygubbi.game.dashboard.view.proposals;

import com.google.common.eventbus.Subscribe;
import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.JsonPojo.LookupItem;
import com.mygubbi.game.dashboard.domain.Product;
import com.mygubbi.game.dashboard.domain.ProposalHeader;
import com.mygubbi.game.dashboard.domain.User;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.event.ProposalEvent;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.gridutil.cell.GridCellFilter;

import java.util.List;
import java.util.stream.Collectors;

import static com.mygubbi.game.dashboard.domain.Product.TYPE;

/**
 * Created by shruthi on 05-Apr-17.
 */
public class AllProductsDetailsWindow extends Window
{
    private static final Logger LOG = LogManager.getLogger(AllProductsDetailsWindow.class);
    private BeanItemContainer productsContainer;
    private Grid productsgrid;
    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private ComboBox categoryCombobox;
    private ComboBox subCategoryCombobox;
    private Button searchButton;

    private AllProductsDetailsWindow()
    {
        setModal(true);
        removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        addStyleName("module-window");
        setWidth("80%");
        setHeight("90%");
        setClosable(false);

        VerticalLayout verticalLayout = new VerticalLayout();
        setSizeFull();

        verticalLayout.setSpacing(true);
        setContent(verticalLayout);

        Component componentHeading=buildHeading();
        verticalLayout.addComponent(componentHeading);

        Component componentSearch=buildSearchLayout();
        verticalLayout.addComponent(componentSearch);

        Component componentGrid=buildProductsLibray();
        verticalLayout.addComponent(componentGrid);
    }

    public static void open()
    {
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
        AllProductsDetailsWindow w = new AllProductsDetailsWindow();
        UI.getCurrent().addWindow(w);
        w.focus();
    }

    private Component buildSearchLayout()
    {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);

        categoryCombobox=new ComboBox("Catagory");
        categoryCombobox.addItem("Kitchen");
        categoryCombobox.addItem("Wardrobe");
        horizontalLayout.addComponent(categoryCombobox);

        subCategoryCombobox=new ComboBox("Sub Category");
        subCategoryCombobox.addItem("L Shaped Kitchen");
        subCategoryCombobox.addItem("U Shaped Kitchen");
        subCategoryCombobox.addItem("Hinged Wardrobe");
        horizontalLayout.addComponent(subCategoryCombobox);

        return horizontalLayout;
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

    private Component buildProductsLibray()
    {
        HorizontalLayout layout=new HorizontalLayout();
        layout.setSizeFull();
        layout.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        productsContainer=new BeanItemContainer<>(Product.class);
        GeneratedPropertyContainer generatedPropertyContainer=createGeneratedAddonsPropertyContainer();

        productsgrid=new Grid(generatedPropertyContainer);
        productsgrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        productsgrid.setSizeFull();

        productsgrid.setColumns(Product.SEQ,Product.ROOM_CODE, Product.TITLE, Product.AMOUNT);
        List<Grid.Column> columns = productsgrid.getColumns();
        int idx = 0;

        columns.get(idx++).setHeaderCaption("Seq");
        columns.get(idx++).setHeaderCaption("Room");
        columns.get(idx++).setHeaderCaption("Title");
        columns.get(idx++).setHeaderCaption("Amount");
        columns.get(idx++).setHeaderCaption("Type");

        layout.addComponent(productsgrid);
        layout.setSpacing(true);

        List<Product> products=proposalDataProvider.getAllProducts("K");

        //LOG.info("product " +products.size());
        if (!products.isEmpty()) {
            productsContainer.addAll(products);
            //productsGrid.sort(Product.SEQ, SortDirection.ASCENDING);
        }


        return layout;
    }

    private GeneratedPropertyContainer createGeneratedAddonsPropertyContainer() {
        GeneratedPropertyContainer genContainer = new GeneratedPropertyContainer(productsContainer);
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

    /*private GeneratedPropertyContainer createGeneratedProductPropertyContainer() {
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
                Product product = (Product) ((BeanItem) item).getBean();

                if (product.getType().equals(Product.TYPES.CUSTOMIZED.name())) {
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

    @Subscribe
    public void productDelete(final ProposalEvent.ProductDeletedEvent event) {
        List<Product> products = proposalDataProvider.getAllProducts();
        boolean removed = products.remove(event.getProduct());
        if (removed) {
            productsContainer.removeAllItems();
            productsContainer.addAll(products);
            productsgrid.setContainerDataSource(createGeneratedProductPropertyContainer());
            productsgrid.getSelectionModel().reset();
            //updateTotal();
            //saveVersionAmounts();
            productsgrid.sort(Product.SEQ, SortDirection.ASCENDING);
        }
    }

   *//* @Subscribe
    public void productCreatedOrUpdated(final ProposalEvent.ProductCreatedOrUpdatedEvent event) {
        List<Product> products = proposalDataProvider.getVersionProducts(proposalHeader.getId(),proposalVersion.getVersion());
        productContainer.removeAllItems();
        productContainer.addAll(products);
        productsGrid.setContainerDataSource(createGeneratedProductPropertyContainer());
        productsGrid.getSelectionModel().reset();
        updateTotal();
        saveVersionAmounts();
        productsGrid.sort(Product.SEQ, SortDirection.ASCENDING);
    }*/

}
