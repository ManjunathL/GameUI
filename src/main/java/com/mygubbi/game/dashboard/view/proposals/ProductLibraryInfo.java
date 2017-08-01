package com.mygubbi.game.dashboard.view.proposals;

import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.domain.JsonPojo.CloudinaryImageUrl;
import com.mygubbi.game.dashboard.domain.JsonPojo.LookupItem;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.view.FileAttachmentComponent;
import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Runo;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import static com.mygubbi.game.dashboard.domain.ProposalHeader.P_CITY;

/**
 * Created by shruthi on 10-Apr-17.
 */
public class ProductLibraryInfo extends Window
{
    private static final Logger LOG = LogManager.getLogger(ProductLibraryInfo.class);

    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private final BeanFieldGroup<ProductLibrary> binder = new BeanFieldGroup<>(ProductLibrary.class);
    Product product;
    Proposal proposal;
    ProductLibrary productLibrary=new ProductLibrary();
    private ProposalHeader proposalHeader;

    //TextField subcategory;
    ComboBox subcategory;
    ComboBox collection;
    ComboBox designerName;
    private ComboBox subcategoryField;
    private Field<?> productTitleField;
    private Field<?> productdescriptionField;
    private Field<?> categoryField;
    private Field<?> sizeField;
    private ComboBox collectionField;
    private TextField width;
    private TextField length;
    private TextField height;
    private Field<?> productLocationField;
    private ComboBox designerField;

    TextField productdescription;
    TextField productTitle;
    List<ProductLibrary> productsLibraryList;
    private FileAttachmentComponent fileAttachmentComponent;
    private Upload quoteUploadCtrl;
    private File uploadedQuoteFile;
    public static void open(Product product, Proposal proposal)
    {
        ProductLibraryInfo w=new ProductLibraryInfo(product,proposal);
        UI.getCurrent().addWindow(w);
        w.focus();
    }

    public ProductLibraryInfo(Product product,Proposal proposal)
    {
        this.proposal=proposal;
        this.product=product;
        //LOG.info("Product " +product);
        setModal(true);
        removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        setWidth("60%");
        setClosable(false);
        this.binder.setItemDataSource(this.productLibrary);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(new MarginInfo(true, true, true, true));
        Responsive.makeResponsive(this);

        HorizontalLayout horizontalLayout2 = new HorizontalLayout();
        horizontalLayout2.setMargin(new MarginInfo(false, false, false, false));
        horizontalLayout2.setSizeFull();
        horizontalLayout2.addComponent(buildComponent());
        verticalLayout.addComponent(horizontalLayout2);
        horizontalLayout2.setHeightUndefined();

        Component footerLayOut = buildFooter();
        verticalLayout.addComponent(footerLayOut);

        setContent(verticalLayout);

    }
    private Component buildComponent()
    {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        verticalLayout.addComponent(horizontalLayout);

        FormLayout formLayoutLeft = new FormLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        horizontalLayout.addComponent(formLayoutLeft);
        horizontalLayout.setExpandRatio(formLayoutLeft,0.4f);


        collectionField = getCollectionCombo();
        binder.bind(collectionField, ProductLibrary.COLLECTION);
        collectionField.setRequired(true);
        formLayoutLeft.addComponent(collectionField);

        categoryField=getcategory();
        binder.buildAndBind("Category",ProductLibrary.PRODUCT_CATEGORY);
        ((TextField) categoryField).setNullRepresentation("");
        categoryField.setRequired(true);
        categoryField.setReadOnly(true);
        formLayoutLeft.addComponent(categoryField);

        sizeField = binder.buildAndBind("Size", ProductLibrary.SIZE);
        sizeField.setRequired(true);
        ((TextField) sizeField).setNullRepresentation("");
        formLayoutLeft.addComponent(sizeField);

        /*List<ProductLibraryMaster> productLibraryMasters=proposalDataProvider.getProductsubcategory(product.getProductCategoryCode());
        for(ProductLibraryMaster productLibraryMaster:productLibraryMasters)
        {
            LOG.info("category " +productLibraryMaster);
        }*/
        subcategoryField=getSubcategoryCombo();
        binder.bind(subcategoryField,ProductLibrary.SUB_CATEGORY);
        subcategoryField.setRequired(true);
        formLayoutLeft.addComponent(subcategoryField);

        productTitleField = binder.buildAndBind("Product Name", ProductLibrary.PRODUCT_TITLE);
        productTitleField.setRequired(true);
        ((TextField) productTitleField).setNullRepresentation("");
        formLayoutLeft.addComponent(productTitleField);

        productdescriptionField = binder.buildAndBind("Product Description", ProductLibrary.PRODUCT_DESCRIPTION);
        ((TextField) productdescriptionField).setNullRepresentation("");
        productdescriptionField.setRequired(true);
        formLayoutLeft.addComponent(productdescriptionField);
        formLayoutLeft.addComponent(getQuoteUploadControl());

        productLocationField=getProductLocation();
        binder.buildAndBind("Product Location",ProductLibrary.PRODUCT_LOCATION);
        ((TextField) productLocationField).setNullRepresentation("");
        formLayoutLeft.addComponent(productLocationField);

        designerField=getDesignerCombo();
        binder.bind(designerField,ProductLibrary.DESIGNER);
        designerField.setRequired(true);
        formLayoutLeft.addComponent(designerField);

        return formLayoutLeft;
    }

    private Component buildFooter()
    {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSizeFull();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.addClickListener((Button.ClickListener) clickEvent -> {
            close();
        });
        cancelBtn.focus();
        footer.addComponent(cancelBtn);
        footer.setSpacing(true);

        Button SaveBtn=new Button("Save");
        footer.addComponent(SaveBtn);

        SaveBtn.addClickListener(clickEvent -> {
            try
            {
                binder.commit();

            }
            catch (FieldGroup.CommitException e)
            {
                NotificationUtil.showNotification("Validation Error, please fill all mandatory fields", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
            String proposalID=String.valueOf(product.getProposalId()).replace(",","");
            productsLibraryList=proposalDataProvider.getProductsLibrary(productTitleField.getValue().toString(),collectionField.getValue().toString());
            if(productsLibraryList.size()==0)
            {
                productLibrary.setSize(product.getSpaceType());
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
                productLibrary.setRoomCode(product.getRoomCode());
                productLibrary.setSeq(product.getSeq());
                productLibrary.setShutterDesign(product.getShutterDesign());
                productLibrary.setShutterDesignCode(product.getShutterDesignCode());
                productLibrary.setSource(product.getSource());
                productLibrary.setType(Product.TYPES.PRODUCT_LIBRARY.name());
                productLibrary.setUpdatedBy(product.getUpdatedBy());
                productLibrary.setWallCarcass(product.getWallCarcass());
                productLibrary.setWallCarcassCode(product.getWallCarcassCode());
                productLibrary.setDesigner(designerName.getValue().toString());
                productLibrary.setSubCategory(subcategory.getValue().toString());
                productLibrary.setProductDescription(productdescriptionField.getValue().toString());
                productLibrary.setProductTitle(productTitleField.getValue().toString());
                productLibrary.setGlass(product.getGlass());
                productLibrary.setHinge(product.getHinge());
                productLibrary.setHandleType(product.getHandleType());
                productLibrary.setHandleFinish(product.getHandleFinish());
                product.setHandleImage(product.getHandleImage());
                product.setKnobImage(product.getKnobImage());
                productLibrary.setKnobType(product.getKnobType());
                productLibrary.setKnobFinish(product.getKnobFinish());
                productLibrary.setProductLocation(productLocationField.getValue().toString());
                productLibrary.setSize(String.valueOf(sizeField.getValue()));
                productLibrary.setHandleTypeSelection(product.getHandleTypeSelection());
                productLibrary.setShutterDesign(product.getShutterDesignCode());
                productLibrary.setShutterImageUrl(product.getShutterDesignImage());
                productLibrary.setHandleThickness(product.getHandleThickness());
                productLibrary.setHandleCode(product.getHandleCode());
                productLibrary.setKnobCode(product.getKnobCode());
                productLibrary.setlConnectorPrice(product.getlConnectorPrice());
                productLibrary.setNoOfLengths(product.getNoOfLengths());
                productLibrary.setSpaceType(product.getSpaceType());
                productLibrary.setProductCategoryLocked(product.getProductCategoryLocked());
                boolean success = proposalDataProvider.InsertProductLibrary(productLibrary);
               // LOG.info("field vale " +productLocationField.getValue() + " " +productLibrary.getProductLocation());
                //LOG.info("success in " + success);
                close();
            }
            else
            {
                NotificationUtil.showNotification("Product is already added to library", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return;
            }
        });
        footer.setComponentAlignment(cancelBtn, Alignment.TOP_RIGHT);
        return footer;
    }
    private ComboBox getSubcategoryCombo()
    {
        List<ProductLibraryMaster> productLibraryMasters=proposalDataProvider.getProductsubcategory(product.getProductCategoryCode());
        /*for(ProductLibraryMaster productLibraryMaster:productLibraryMasters)
        {
            LOG.info("category " +productLibraryMaster);
        }*/

        final BeanContainer<String, ProductLibraryMaster> container = new BeanContainer<>(ProductLibraryMaster.class);
        container.setBeanIdProperty(ProductLibraryMaster.SUB_CATEGORY);
        container.addAll(productLibraryMasters);

        subcategory=new ComboBox("Sub Category");
        subcategory.setWidth("300px");
        subcategory.setNullSelectionAllowed(false);
        subcategory.setContainerDataSource(container);
        subcategory.setItemCaptionPropertyId(ProductLibraryMaster.SUB_CATEGORY);

        return subcategory;
    }

    private ComboBox getDesignerCombo() {
        List<User> list = proposalDataProvider.getDesignerUsers();
        LOG.info("list^^^"+list.size());
        final BeanContainer<String, User> container =
                new BeanContainer<>(User.class);
        container.setBeanIdProperty(User.NAME);
        container.addAll(list);

        designerName = new ComboBox("Designer");
        designerName.setWidth("300px");
        designerName.setNullSelectionAllowed(false);
        designerName.setContainerDataSource(container);
        designerName.setItemCaptionPropertyId(User.NAME);

        return designerName;
    }

    private ComboBox getCollectionCombo()
    {
        List<LookupItem> collectionList =proposalDataProvider.getLookupItems(proposalDataProvider.COLLECTION_LOOKUP);
        final BeanContainer<String, LookupItem> container = new BeanContainer<>(LookupItem.class);
        container.setBeanIdProperty(LookupItem.TITLE);
        container.addAll(collectionList);

        collection=new ComboBox("Collection");
        collection.setWidth("300px");
        collection.setNullSelectionAllowed(false);
        collection.setContainerDataSource(container);
        collection.setItemCaptionPropertyId(LookupItem.TITLE);

        return collection;
    }
    private TextField getcategory()
    {
        TextField category=new TextField("Category");
        category.setValue(product.getProductCategoryCode());

        return category;
    }

    private TextField getsize()
    {
        TextField size=new TextField("Size");
        return size;
    }
    private Component getQuoteUploadControl() {
        this.quoteUploadCtrl = new Upload("Upload Image", (filename, mimeType) -> {
            LOG.debug("Received upload - " + filename);

            if (!commitValues()) return null;

            if (StringUtils.isEmpty(filename)) {
                NotificationUtil.showNotification("Please specify the file.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
                return null;
            }

            FileOutputStream fos = null;
            uploadedQuoteFile = new File("c:/Users/Public/productlibraryuploads" + "/" + filename);
            uploadedQuoteFile.getParentFile().mkdirs();
            try
            {
                fos = new FileOutputStream(uploadedQuoteFile);
                LOG.info("Upload file " +uploadedQuoteFile);

            }
            catch (final FileNotFoundException e) {
                NotificationUtil.showNotification("Please specify the file path correctly.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            }
            return fos;
        });

        this.quoteUploadCtrl.setStyleName("upload-btn");

        this.quoteUploadCtrl.addProgressListener((Upload.ProgressListener) (readBytes, contentLength) -> LOG.debug("Progress " + (readBytes * 100 / contentLength)));

        this.quoteUploadCtrl.addSucceededListener((Upload.SucceededListener) event -> {

            String filename = event.getFilename();
            CloudinaryImageUrl cloudinaryImageUrl=new CloudinaryImageUrl();
            cloudinaryImageUrl.setImageurl(uploadedQuoteFile.getAbsolutePath());
            CloudinaryImageUrl p=proposalDataProvider.addToCloudinary(cloudinaryImageUrl);
            NotificationUtil.showNotification("Image Uploaded successfully", NotificationUtil.STYLE_BAR_SUCCESS_SMALL);
           // LOG.info("succes value " +p);
            productLibrary.setImageurl(p.getImageurl());
        });
        return quoteUploadCtrl;
    }

    private TextField getProductLocation()
    {
        TextField productLocation=new TextField("Product Location");
       // productLocation.setValue(product.getProductLocation());

        return productLocation;
    }
    private boolean commitValues() {
        try {
            binder.commit();
        } catch (FieldGroup.CommitException e) {
            NotificationUtil.showNotification("Please fill all mandatory fields before upload!", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            return false;
        }
        return true;
    }
    private String getUploadBasePath() {
        return proposal.getProposalHeader().getFolderPath();
    }
   /* private HorizontalLayout getDimensionsPanel() {
        HorizontalLayout horizontalLayoutDimensions = new HorizontalLayout();
        horizontalLayoutDimensions.setSizeFull();
        horizontalLayoutDimensions.setMargin(new MarginInfo(false,true,false,true));
        horizontalLayoutDimensions.setCaption("Dimensions");
        horizontalLayoutDimensions.setSpacing(false);

        FormLayout formLayoutDepth = new FormLayout();
        formLayoutDepth.setSizeFull();
        formLayoutDepth.setMargin(new MarginInfo(false,false,false,false));
        this.length = new TextField();
        this.length.setNullRepresentation("");
        this.length.setCaption("Length");
        this.length.setRequired(true);
        this.length.setWidth("60px");
        this.length.addStyleName(Runo.TEXTFIELD_SMALL);
        binder.bind(this.length,productLibrary.LENGTH);
        formLayoutDepth.addComponent(length);
        horizontalLayoutDimensions.addComponent(formLayoutDepth);

        FormLayout formLayoutWidth = new FormLayout();
        formLayoutWidth.setSizeFull();
        formLayoutWidth.setMargin(new MarginInfo(false,false,false,false));
        this.width = new TextField();
        this.width.setNullRepresentation("");
        this.width.setCaption("Width");
        this.width.setRequired(true);
        this.width.setWidth("60px");
        this.width.addStyleName(Runo.TEXTFIELD_SMALL);
        binder.bind(this.width,ProductLibrary.WIDTH);
        formLayoutWidth.addComponent(width);
        horizontalLayoutDimensions.addComponent(formLayoutWidth);

        FormLayout formLayoutHeight = new FormLayout();
        formLayoutHeight.setSizeFull();
        formLayoutHeight.setMargin(new MarginInfo(false,false,false,false));
        this.height = new TextField();
        this.height.setNullRepresentation("");
        this.height.setCaption("Height");
        this.height.setRequired(true);
        this.height.setWidth("60px");
        this.height.addStyleName(Runo.TEXTFIELD_SMALL);
        binder.bind(this.height, ProductLibrary.HEIGHT);
        formLayoutHeight.addComponent(height);
        horizontalLayoutDimensions.addComponent(formLayoutHeight);

        return horizontalLayoutDimensions;
    }*/
}
