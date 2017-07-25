package com.mygubbi.game.dashboard.view.proposals;

import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.domain.JsonPojo.LookupItem;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.mygubbi.game.dashboard.event.ProposalEvent;
import com.mygubbi.game.dashboard.view.NotificationUtil;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Date;
import java.util.List;

import static java.lang.StrictMath.round;

/**
 * Created by nitinpuri on 02-06-2016.
 */
public class CustomAddonDetailsWindow extends Window {

    private final boolean isProposalAddon;
    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private final AddonProduct addonProduct;
    private final BeanFieldGroup<AddonProduct> binder = new BeanFieldGroup<>(AddonProduct.class);

    private TextArea title;
    private TextField uom;
    private TextField rate;
    private TextField quantity;
    private TextField amount;

    private ComboBox spaceType;
    private TextField roomText;

    private Button applyButton;

    private String originalImagePath;

    private boolean readOnly;
    private ProposalVersion proposalVersion;
    private Date priceDate;
    private String city;
    Double rateToBeUsed;
    Double addonsTotal=0.0;
    Double addonsTotalWOtax=0.0;
    Double addonsProfit=0.0;
    Double addonsMargin=0.0;

    String codeForAddonWOTax;
    String codeForAddonSourcePrice;
    double rateForAddonWOTax;
    double rateForAddonSourcePrice;
    Double addonDealerPrice=0.0;

    private static final Logger LOG = LogManager.getLogger(AddonProduct.class);


    public CustomAddonDetailsWindow(AddonProduct addonProduct, String title, boolean isProposalAddon, ProposalVersion proposalVersion) {
        this.addonProduct = addonProduct;
        this.isProposalAddon = isProposalAddon;
        this.originalImagePath = this.addonProduct.getImagePath();
        this.proposalVersion = proposalVersion;
        this.binder.setItemDataSource(this.addonProduct);
        setModal(true);
        removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        setWidth("40%");
        setClosable(false);
        setCaption("Addon Configuration for " + title);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(new MarginInfo(true, true, true, true));
        Responsive.makeResponsive(this);

        HorizontalLayout horizontalLayout2 = new HorizontalLayout();
        horizontalLayout2.setMargin(new MarginInfo(false, false, false, false));
        horizontalLayout2.setSizeFull();
        horizontalLayout2.addComponent(buildAddonSelectionsComponent());
        verticalLayout.addComponent(horizontalLayout2);
        horizontalLayout2.setHeightUndefined();

        Component footerLayOut = buildFooter();
        verticalLayout.addComponent(footerLayOut);

        setContent(verticalLayout);

        handleState();
    }

    private void handleState() {
        ProposalVersion.ProposalStage proposalStage = ProposalVersion.ProposalStage.valueOf(proposalVersion.getInternalStatus());
        switch (proposalStage) {
            case Draft:
                break;
            case Published:
                setComponentsReadOnly();
                break;
            case Confirmed:
                setComponentsReadOnly();
                break;
            case Locked:
                setComponentsReadOnly();
                break;
            case DSO:
                setComponentsReadOnly();
                break;
            case PSO:
                setComponentsReadOnly();
                break;
            default:
                throw new RuntimeException("Unknown State");
        }
    }

    private void setComponentsReadOnly() {

        title.setReadOnly(true);
        uom.setReadOnly(true);
        quantity.setReadOnly(true);
        rate.setReadOnly(true);
        amount.setReadOnly(true);
    }

    private Component buildAddonSelectionsComponent() {

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        FormLayout formLayout = new FormLayout();
        formLayout.setSizeFull();
        formLayout.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        verticalLayout.addComponent(formLayout);

        this.spaceType = getSimpleItemFilledCombo("Space Type", ProposalDataProvider.SPACETYPE_LOOKUP, null);
        this.spaceType.setWidth("100%");
        spaceType.setRequired(true);
        binder.bind(spaceType, AddonProduct.ADDON_SPACE_TYPE);
        spaceType.addValueChangeListener(valueChangeEvent -> {
            String code = (String) valueChangeEvent.getProperty().getValue();
            String title = (String) ((ComboBox) ((Field.ValueChangeEvent) valueChangeEvent).getSource()).getContainerDataSource().getItem(code).getItemProperty("title").getValue();
            addonProduct.setSpaceType(title);

        });
        formLayout.addComponent(this.spaceType);


        this.roomText = new TextField("Room");
        this.roomText.setRequired(true);
        roomText.setNullRepresentation("");
        binder.bind(this.roomText, AddonProduct.ADDON_ROOM);
        formLayout.addComponent(roomText);



        this.title = new TextArea("Product");
        this.title.setNullRepresentation("");
        this.title.setHeight("45px");

        binder.bind(this.title, AddonProduct.PRODUCT);
        formLayout.addComponent(this.title);

        this.uom = new TextField("UOM");
        this.uom.setNullRepresentation("");
        binder.bind(this.uom, AddonProduct.UOM);
        this.uom.setReadOnly(false);
        formLayout.addComponent(this.uom);

        this.rate = new TextField("Rate");
        this.rate.setRequired(true);
        binder.bind(this.rate, AddonProduct.RATE);
        this.rate.addValueChangeListener(this::rateChanged);
        formLayout.addComponent(this.rate);

        this.quantity = new TextField("Qty");
        this.quantity.setRequired(true);
        binder.bind(this.quantity, AddonProduct.QUANTITY);
        if (addonProduct.getQuantity() == 0) {
            this.quantity.setValue("1");
        }
        this.quantity.addValueChangeListener(this::quantityChanged);
        formLayout.addComponent(this.quantity);

        this.amount = new TextField("Amount");
        binder.bind(this.amount, AddonProduct.AMOUNT);
        this.amount.setReadOnly(true);
        formLayout.addComponent(this.amount);


        return verticalLayout;
    }

    private void rateChanged(Property.ValueChangeEvent valueChangeEvent) {

        String rateValue = this.rate.getValue().replaceAll(",", "");
        String quantityValue = this.quantity.getValue().replaceAll(",", "");
        this.amount.setReadOnly(false);
        this.amount.setValue(String.valueOf(Double.parseDouble(rateValue) * Double.parseDouble(quantityValue)));
        this.amount.setReadOnly(true);
//        checkApply();
    }

    private void quantityChanged(Property.ValueChangeEvent valueChangeEvent) {
        String rateValue = this.rate.getValue().replaceAll(",", "");
        String quantityValue = this.quantity.getValue().replaceAll(",", "");
        if (Double.parseDouble(quantityValue) <= 0) {
            quantityValue = "1";
            this.quantity.setValue("1");
        }
        this.amount.setReadOnly(false);
        this.amount.setValue(String.valueOf(Double.parseDouble(rateValue) * Double.parseDouble(quantityValue)));
        this.amount.setReadOnly(true);
//        checkApply();
    }


    private ComboBox getSimpleItemFilledCombo(String caption, String dataType, Property.ValueChangeListener listener) {
        List<LookupItem> list = proposalDataProvider.getLookupItems(dataType);
        return getSimpleItemFilledCombo(caption, list, listener);
    }

    private ComboBox getSimpleItemFilledCombo(String caption, List<LookupItem> list, Property.ValueChangeListener listener) {

        final BeanContainer<String, LookupItem> container =
                new BeanContainer<>(LookupItem.class);
        container.setBeanIdProperty("code");
        container.addAll(list);

        ComboBox select = new ComboBox(caption);
        select.setNullSelectionAllowed(false);
        select.setWidth("250px");
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId("title");
        if (listener != null) select.addValueChangeListener(listener);
        if (container.size() > 0) select.setValue(select.getItemIds().iterator().next());
        return select;
    }



    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSizeFull();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.addClickListener((Button.ClickListener) clickEvent -> {
            binder.discard();
            this.addonProduct.setImagePath(this.originalImagePath);
            close();
        });
        cancelBtn.focus();
        footer.addComponent(cancelBtn);
        footer.setSpacing(true);

        applyButton = new Button("Apply");
        applyButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        applyButton.addClickListener(event -> {

            if (!binder.isValid()) {
                NotificationUtil.showNotification("Please ensure all mandatory fields are filled.", NotificationUtil.STYLE_BAR_ERROR_SMALL);
            } else {

                try {

                    if (this.title == null || this.title.isEmpty())
                    {
                        NotificationUtil.showNotification("Title cannot be empty",NotificationUtil.STYLE_BAR_ERROR_SMALL);
                        return;
                    }
                    int seq = 0;
                    binder.commit();
                    List<AddonProduct> addons = proposalDataProvider.getVersionAddons(proposalVersion.getProposalId(),proposalVersion.getVersion());
                    if (addons.size() == 0)
                    {
                        addonProduct.setSeq(1);
                    }
                    else
                    {
                        seq = addons.size()+1;
                        addonProduct.setSeq(seq);
                    }
                    addonProduct.setFromVersion(proposalVersion.getVersion());
                    addonProduct.setCategoryCode("Custom Addon");
                    addonProduct.setProductTypeCode("NA");
                    addonProduct.setProductSubtypeCode("NA");
                    addonProduct.setBrandCode("NA");
                    addonProduct.setTitle(this.title.getValue());
                    addonProduct.setProduct(this.title.getValue());
                    addonProduct.setCode("NA");
                    //LOG.debug("Addon product Class :" + addonProduct.toString());
                    if (this.priceDate == null)
                    {
                        this.priceDate = new Date(System.currentTimeMillis());
                    }

                    List<RateCard> Addonwotaxlist=proposalDataProvider.getFactorRateCodeDetails("F:ADWOTAX");
                    for (RateCard addonWOcode : Addonwotaxlist ) {
                        codeForAddonWOTax=addonWOcode.getCode();
                    }
                    List<RateCard> Addonsourcepricelist=proposalDataProvider.getFactorRateCodeDetails("F:CASP");
                    for (RateCard addonsourceprice : Addonsourcepricelist ) {
                        codeForAddonSourcePrice=addonsourceprice.getCode();
                    }
                    //LOG.info("parameters" +codeForAddonWOTax+ " " +this.priceDate+ " " +this.city);
                    PriceMaster addonwotaxpriceMaster=proposalDataProvider.getFactorRatePriceDetails(codeForAddonWOTax,this.priceDate,this.city);
                    rateForAddonWOTax=addonwotaxpriceMaster.getSourcePrice();
                    //LOG.info("Addon wo tax" +addonwotaxpriceMaster);
                    PriceMaster addonsourcepricepriceMaster=proposalDataProvider.getFactorRatePriceDetails(codeForAddonSourcePrice,this.priceDate,this.city);
                    rateForAddonSourcePrice=addonsourcepricepriceMaster.getSourcePrice();

                    //LOG.info("rate for addon" +rateForAddonWOTax);
                    addonsTotalWOtax+=addonProduct.getAmount()*rateForAddonWOTax;
                    if(addonProduct.getCode().equals("NA"))
                    {
                        //LOG.info("source price for custom addom " +rateForAddonSourcePrice);
                        double addonDiscountedPrice=0;
                        addonDiscountedPrice=rateForAddonSourcePrice*addonProduct.getAmount();
                        addonDealerPrice+=addonDiscountedPrice;
                    }

                    PriceMaster addonMasterRate=proposalDataProvider.getAddonRate(addonProduct.getCode(),this.priceDate,this.city);
                    {
                        addonDealerPrice+=Double.valueOf(addonMasterRate.getSourcePrice());
                    }
                    addonsProfit=addonsTotalWOtax-addonDealerPrice;
                    addonsMargin=(addonsProfit / addonsTotalWOtax)*100;
                    addonProduct.setAmountWOTax(round(addonsTotalWOtax));
                    addonProduct.setSourcePrice(round(addonDealerPrice));
                    addonProduct.setMargin(round(addonsMargin));
                    addonProduct.setProfit(round(addonsProfit));
                    addonProduct.setProposalId(proposalVersion.getProposalId());


                        if (isProposalAddon) {
                            DashboardEventBus.post(new ProposalEvent.ProposalAddonUpdated(addonProduct));
                        } else {
                            DashboardEventBus.post(new ProposalEvent.AddonUpdated(addonProduct));
                        }
                    close();
                } catch (FieldGroup.CommitException e) {
                    e.printStackTrace();
                }
            }

        });
        applyButton.focus();
        applyButton.setVisible(true);
//        applyButton.setEnabled(this.binder.isValid());

        footer.addComponent(applyButton);
        footer.setComponentAlignment(cancelBtn, Alignment.TOP_RIGHT);

        return footer;
    }

    private void checkApply() {
        if (this.binder.isValid()) {
            this.applyButton.setEnabled(true);
        }
    }

    public static void open(AddonProduct addon, String title, boolean isProposalAddon, ProposalVersion proposalVersion) {
        Window w = new CustomAddonDetailsWindow(addon, title, isProposalAddon,proposalVersion);
        UI.getCurrent().addWindow(w);
        w.focus();

    }
}
