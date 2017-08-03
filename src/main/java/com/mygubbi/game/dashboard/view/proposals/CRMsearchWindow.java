package com.mygubbi.game.dashboard.view.proposals;

import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.*;
import com.mygubbi.game.dashboard.domain.JsonPojo.LookupItem;
import com.mygubbi.game.dashboard.event.DashboardEvent;
import com.mygubbi.game.dashboard.event.DashboardEventBus;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Authentication;
import org.vaadin.gridutil.cell.GridCellFilter;

import java.util.*;

/**
 * Created by shruthi on 25-Apr-17.
 */
public class CRMsearchWindow extends Window
{
    private static final Logger LOG = LogManager.getLogger(CRMsearchWindow.class);
    Grid crmgrid;
    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    private BeanItemContainer<Profile> userprofilecontainer;
    //ArrayList<Profile> profiles=new List<Profile>();
    ArrayList<Profile> profiles=new ArrayList<Profile>();
    ProposalHeader proposalHeader;
    private CRMsearchWindow(ProposalHeader proposalHeader)
    {
        this.proposalHeader=proposalHeader;
        setModal(true);
        removeCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        addStyleName("module-window");
        setWidth("80%");
        setHeight("90%");
        setClosable(false);
        DashboardEventBus.post(new DashboardEvent.CloseOpenWindowsEvent());
        VerticalLayout verticalLayout = new VerticalLayout();
        setSizeFull();
        setContent(verticalLayout);

        Component componentheading=buildHeading();
        verticalLayout.addComponent(componentheading);

        Component componentAddonDetails = buildcrmgrid();
        verticalLayout.addComponent(componentAddonDetails);
    }

    private Component buildHeading()
    {
        HorizontalLayout formLayoutLeft = new HorizontalLayout();
        formLayoutLeft.setSizeFull();
        formLayoutLeft.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        Label customerDetailsLabel = new Label("CRM Details");
        customerDetailsLabel.addStyleName(ValoTheme.LABEL_HUGE);
        customerDetailsLabel.addStyleName(ValoTheme.LABEL_COLORED);
        customerDetailsLabel.addStyleName("products-and-addons-heading-text");
        formLayoutLeft.addComponent(customerDetailsLabel);

        return formLayoutLeft;
    }

    private Component buildcrmgrid()
    {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setMargin(new MarginInfo(false,true,false,true));

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        horizontalLayout.setStyleName("v-has-width-forLabel");

        userprofilecontainer=new BeanItemContainer<>(Profile.class);
        GeneratedPropertyContainer genContainer=createGeneratedAddonsPropertyContainer();
        crmgrid=new Grid(genContainer);

        crmgrid.setSizeFull();
        crmgrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        crmgrid.setColumns(Profile.CRM_ID,Profile.FIRST_NAME,Profile.CUSTOMER_EMAIL,Profile.CUSTOMER_PHONE);

        crmgrid.addSelectionListener(selectionEvent -> {
            if (!selectionEvent.getAdded().isEmpty()) {
                Object selected = ((Grid.SingleSelectionModel) crmgrid.getSelectionModel()).getSelectedRow();
                String Id = (String) crmgrid.getContainerDataSource().getItem(selected).getItemProperty(Profile.CRM_ID).getValue();
                //LOG.info("Id value of user_profile " +Id);

                List<UserProfile> profileInformation=proposalDataProvider.getUserProfileDetailsonCRMId(Id);
                for(UserProfile profile:profileInformation)
                {
                    for(Profile profile1:profile.getProfile())
                    {
                        proposalHeader.setCrmId(profile1.getOpportunityId());
                        proposalHeader.setTitle(profile1.getDisplayName());
                        proposalHeader.setCemail(profile1.getEmail());
                        proposalHeader.setCphone1(profile1.getMobile());

                        proposalHeader.setCname(profile1.getFirst_name());
                        proposalHeader.setSalesEmail(profile1.getSalesExecUserId());
                        List<User> Salesusers=proposalDataProvider.getUsersByEmail(profile1.getSalesExecUserId());
                        if (Salesusers.size()==0)
                        {
                            proposalHeader.setSalesName(null);
                            proposalHeader.setSalesPhone(null);
                        }
                        else
                        {
                            for(User user:Salesusers)
                            {
                                proposalHeader.setSalesName(user.getName());
                                proposalHeader.setSalesPhone(user.getPhone());
                            }
                        }

                        proposalHeader.setDesignerEmail(profile1.getDesignerUserId());
                        List<User> designusers=proposalDataProvider.getUsersByEmail(profile1.getDesignerUserId());
                        if (designusers.size() == 0)
                        {
                            proposalHeader.setDesignerName(null);
                            proposalHeader.setDesignerPhone(null);
                        }
                        else {
                            for (User user : designusers) {
                                proposalHeader.setDesignerName(user.getName());
                                proposalHeader.setDesignerPhone(user.getPhone());
                            }
                        }
                        /*proposalHeader.setDesignerName(profile1.getDesignerName());
                        proposalHeader.setDesignerPhone(profile1.getDesignerMobile());*/
                        for(CompleteProfile cp : profile1.getCompleteProfile())
                        {
                            //LOG.info("complete profile" +profile.getCompleteProfile());
                            proposalHeader.setCaddress1(cp.getAddress());
                            proposalHeader.setProjectName(cp.getProjectName());
                            proposalHeader.setPaddress1(cp.getPropertyAddressCity());
                        }
                        if(!(Objects.equals(proposalHeader.getPackageFlag(), "Yes")))
                        {
                            proposalHeader.setPackageFlag("No");
                            proposalHeader.setAdminPackageFlag("No");
                        }
                        boolean success = proposalDataProvider.saveProposal(this.proposalHeader);
                       /* LOG.info("proposal create value in crm search window " +success);
                        LOG.info("proposal header in crm window after save in crm search window" +proposalHeader);*/
                    }
                }
                UI.getCurrent().getNavigator().navigateTo("New Quotation/" + proposalHeader.getId());
               // LOG.info("navigated");
            }
        });

        List<Grid.Column> columns=crmgrid.getColumns();
        int idx = 0;
        columns.get(idx++).setHeaderCaption("Opportunity");
        columns.get(idx++).setHeaderCaption("Name");
        columns.get(idx++).setHeaderCaption("Email");
        columns.get(idx++).setHeaderCaption("Phone");

        GridCellFilter filter = new GridCellFilter(crmgrid);

        filter.setTextFilter(Profile.CRM_ID,true,false);
        filter.setTextFilter(Profile.FIRST_NAME,true,false);
        filter.setTextFilter(Profile.CUSTOMER_EMAIL,true,false);
        filter.setTextFilter(Profile.CUSTOMER_PHONE,true,false);

        verticalLayout.addComponent(horizontalLayout);
        verticalLayout.setSpacing(true);
        verticalLayout.addComponent(crmgrid);

        //UserProfile userProfile=new UserProfile();
        List<UserProfile> userProfiles=proposalDataProvider.getUserProfileDetails("SAL-1705-221656");
       // LOG.info("$$$" +userProfiles.toString());

        for(UserProfile u:userProfiles)
        {
            //profiles=u.getProfile();
            //LOG.info("profile " +u.getProfile());
            for(Profile profile:u.getProfile())
            {
                Profile p=new Profile();
                p.setCrmId(profile.getOpportunityId());
                p.setDisplayName(profile.getDisplayName());
                p.setEmail(profile.getEmail());
                p.setProfileImage(profile.getProfileImage());
                p.setFirst_name(profile.getFirst_name());
                p.setLast_name(profile.getLast_name());
                p.setMobile(profile.getMobile());
                p.setCity(profile.getCity());
                p.setDesignerUserId(profile.getDesignerUserId());
                p.setDisplayName(profile.getDisplayName());
                p.setDesignerMobile(profile.getDesignerMobile());
                p.setSalesExecUserId(profile.getSalesExecUserId());
                p.setSalesExecName(profile.getSalesExecName());
                p.setSalesExecMobile(profile.getSalesExecMobile());
                for(CompleteProfile cp : profile.getCompleteProfile())
                {
                    //LOG.info("complete profile" +profile.getCompleteProfile());
                    p.setAddress(cp.getAddress());
                    p.setPropertyAddressCity(cp.getPropertyAddressCity());
                    p.setProjectName(cp.getProjectName());
                }
                profiles.add(p);
                //LOG.info("created profile " +p);
            }
           // LOG.info("profile " +profiles.toString());
        }
        userprofilecontainer.addAll(profiles);
        return verticalLayout;
    }

    private GeneratedPropertyContainer createGeneratedAddonsPropertyContainer() {
        GeneratedPropertyContainer genContainer = new GeneratedPropertyContainer(userprofilecontainer);
        return genContainer;
    }

    public static void open(ProposalHeader proposalHeader)
    {
        CRMsearchWindow w=new CRMsearchWindow(proposalHeader);
        UI.getCurrent().addWindow(w);
        w.focus();
    }
}
