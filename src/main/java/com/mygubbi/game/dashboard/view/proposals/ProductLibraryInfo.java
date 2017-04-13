package com.mygubbi.game.dashboard.view.proposals;

import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.Product;
import com.mygubbi.game.dashboard.domain.ProductLibrary;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Created by shruthi on 10-Apr-17.
 */
public class ProductLibraryInfo extends Window
{
    private static final Logger LOG = LogManager.getLogger(ProductLibraryInfo.class);

    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    Product product;
    ProductLibrary productLibrary=new ProductLibrary();

    TextField subcategory;
    TextField productdescription;
    List<ProductLibrary> productsLibraryList;
    public static void open(Product product)
    {
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
        ProductLibraryInfo w=new ProductLibraryInfo(product);
        UI.getCurrent().addWindow(w);
        w.focus();
    }

    public ProductLibraryInfo(Product product)
    {
        this.product=product;

        DashboardEventBus.register(this);
        removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        setWidth("50%");
        setHeight("50%");
        //setHeightUndefined();
        setClosable(false);
        setModal(true);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setHeightUndefined();

        setSizeFull();

        verticalLayout.setSpacing(true);
        setContent(verticalLayout);

        Component componentHeading=buildHeading();
        verticalLayout.addComponent(componentHeading);

        Component component=buildLayout1();
        verticalLayout.addComponent(component);

        Component componentSave=buildSaveButton();
        verticalLayout.addComponent(componentSave);

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
    private Component buildLayout1()
    {
        HorizontalLayout formLayoutLeft = new HorizontalLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        formLayoutLeft.setMargin(new MarginInfo(true,true,true,true));

        subcategory=new TextField("Sub category");
        formLayoutLeft.addComponent(subcategory);

        productdescription=new TextField("Product Description");
        formLayoutLeft.addComponent(productdescription);

        return formLayoutLeft;
    }

    private  Component buildSaveButton()
    {
        HorizontalLayout formLayoutLeft = new HorizontalLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        formLayoutLeft.setMargin(new MarginInfo(true,true,true,true));

        Button SaveBtn=new Button("Save");
        formLayoutLeft.addComponent(SaveBtn);

        SaveBtn.addClickListener(clickEvent -> {
            String proposalID=String.valueOf(product.getProposalId()).replace(",","");
            LOG.info("Params" +product.getProposalId() + " " +product.getSeq()+ " " + product.getFromVersion());
            productsLibraryList=proposalDataProvider.getProductsLibrary(proposalID,String.valueOf(product.getSeq()),product.getFromVersion());
            LOG.info("products library list size" +productsLibraryList.size());

            if(productsLibraryList.size()==0) {
                productLibrary.setSubCategory(product.getSubCategory());
                productLibrary.setAmountWoTax(product.getAmountWoTax());
                productLibrary.setAmount(product.getAmount());
                productLibrary.setManufactureAmount(product.getManufactureAmount());
                productLibrary.setProposalId(product.getProposalId());
                productLibrary.setProductDescription(product.getProductDescription());
                productLibrary.setAddons(product.getAddons());
                productLibrary.setBaseCarcass(product.getBaseCarcass());
                productLibrary.setBaseCarcassCode(product.getBaseCarcassCode());
                productLibrary.setCatalogueId(product.getCatalogueId());
                productLibrary.setCatalogueName(product.getCatalogueName());
                productLibrary.setCostWoAccessories(product.getCostWoAccessories());
                productLibrary.setCreatedBy(product.getCreatedBy());
                productLibrary.setDimension(product.getDimension());
                //productLibrary.setFileAttachmentList(product.getFileAttachmentList());
                productLibrary.setFinish(product.getFinish());
                productLibrary.setFinishCode(product.getFinishCode());
                productLibrary.setFinishType(product.getFinishType());
                productLibrary.setFinishTypeCode(product.getFinishTypeCode());
                productLibrary.setFromVersion(product.getFromVersion());
                productLibrary.setManualSeq(product.getManualSeq());
                productLibrary.setManufactureAmount(product.getManufactureAmount());
                productLibrary.setMargin(product.getMargin());
                productLibrary.setModules(product.getModules());
                productLibrary.setProfit(product.getProfit());
                productLibrary.setProductCategory(product.getProductCategoryCode());
                productLibrary.setProductCategoryCode(product.getProductCategoryCode());
                productLibrary.setProposalId(product.getProposalId());
                productLibrary.setQuantity(product.getQuantity());
                productLibrary.setQuoteFilePath(product.getQuoteFilePath());
                productLibrary.setRoom(product.getRoom());
                productLibrary.setRoomCode(product.getRoomCode());
                productLibrary.setSeq(product.getSeq());
                productLibrary.setShutterDesign(product.getShutterDesign());
                productLibrary.setShutterDesignCode(product.getShutterDesignCode());
                productLibrary.setSource(product.getSource());
                productLibrary.setTitle(product.getTitle());
                productLibrary.setType(product.getType());
                productLibrary.setUpdatedBy(product.getUpdatedBy());
                productLibrary.setWallCarcass(product.getWallCarcass());
                productLibrary.setWallCarcassCode(product.getWallCarcassCode());
                productLibrary.setSubCategory(subcategory.getValue());
                productLibrary.setProductDescription(productdescription.getValue());
                boolean success = proposalDataProvider.InsertProductLibrary(productLibrary);
                LOG.info("success in " + success);

            }
            else
            {
                productLibrary.setSubCategory(product.getSubCategory());
                productLibrary.setAmountWoTax(product.getAmountWoTax());
                productLibrary.setAmount(product.getAmount());
                productLibrary.setManufactureAmount(product.getManufactureAmount());
                productLibrary.setProposalId(product.getProposalId());
                productLibrary.setProductDescription(product.getProductDescription());
                productLibrary.setAddons(product.getAddons());
                productLibrary.setBaseCarcass(product.getBaseCarcass());
                productLibrary.setBaseCarcassCode(product.getBaseCarcassCode());
                productLibrary.setCatalogueId(product.getCatalogueId());
                productLibrary.setCatalogueName(product.getCatalogueName());
                productLibrary.setCostWoAccessories(product.getCostWoAccessories());
                productLibrary.setCreatedBy(product.getCreatedBy());
                productLibrary.setDimension(product.getDimension());
                //productLibrary.setFileAttachmentList(product.getFileAttachmentList());
                productLibrary.setFinish(product.getFinish());
                productLibrary.setFinishCode(product.getFinishCode());
                productLibrary.setFinishType(product.getFinishType());
                productLibrary.setFinishTypeCode(product.getFinishTypeCode());
                productLibrary.setFromVersion(product.getFromVersion());
                productLibrary.setManualSeq(product.getManualSeq());
                productLibrary.setManufactureAmount(product.getManufactureAmount());
                productLibrary.setMargin(product.getMargin());
                productLibrary.setModules(product.getModules());
                productLibrary.setProfit(product.getProfit());
                productLibrary.setProductCategory(product.getProductCategoryCode());
                productLibrary.setProductCategoryCode(product.getProductCategoryCode());
                productLibrary.setProposalId(product.getProposalId());
                productLibrary.setQuantity(product.getQuantity());
                productLibrary.setQuoteFilePath(product.getQuoteFilePath());
                productLibrary.setRoom(product.getRoom());
                productLibrary.setRoomCode(product.getRoomCode());
                productLibrary.setSeq(product.getSeq());
                productLibrary.setShutterDesign(product.getShutterDesign());
                productLibrary.setShutterDesignCode(product.getShutterDesignCode());
                productLibrary.setSource(product.getSource());
                productLibrary.setTitle(product.getTitle());
                productLibrary.setType(product.getType());
                productLibrary.setUpdatedBy(product.getUpdatedBy());
                productLibrary.setWallCarcass(product.getWallCarcass());
                productLibrary.setWallCarcassCode(product.getWallCarcassCode());
                productLibrary.setSubCategory(subcategory.getValue());
                productLibrary.setProductDescription(productdescription.getValue());
                boolean success = proposalDataProvider.UpdateProductLibrary(productLibrary);
                LOG.info("updated  " +success);
            }
            });
        DashboardEventBus.unregister(this);
        close();
        return formLayoutLeft;
    }
}
