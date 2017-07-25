package com.mygubbi.game.dashboard.view.proposals;

import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.mygubbi.game.dashboard.domain.ProposalHeader;
import com.mygubbi.game.dashboard.domain.ProposalVersion;
import com.mygubbi.game.dashboard.domain.SendToCRM;
import com.mygubbi.game.dashboard.domain.SendToCRMOnPublish;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Shruthi on 7/24/2017.
 */
public class PublishOnCRM
{
    ProposalHeader proposalHeader;
    private static final Logger LOG = LogManager.getLogger(PublishOnCRM.class);
    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();
    PublishOnCRM(ProposalHeader proposalHeader)
    {
        this.proposalHeader=proposalHeader;
    }
    public SendToCRMOnPublish updatePriceInCRMOnPublish() {
        Double amount=0.0;
        String quoteNumberCRM="";
        SendToCRMOnPublish sendToCRM = new SendToCRMOnPublish();
        sendToCRM.setOpportunity_name(proposalHeader.getCrmId());
        List<ProposalHeader> proposalHeaders=proposalDataProvider.getProposalHeadersByCrmIds(proposalHeader.getCrmId());
        List<ProposalVersion> proposalVersionList= new ArrayList<>();
        List<ProposalVersion> proposalVersionList1=new ArrayList<>();
        for(ProposalHeader p:proposalHeaders)
        {
            proposalVersionList= new ArrayList<>();
            List<ProposalVersion> pv=proposalDataProvider.getAllProductDetails(p.getId());
            for(ProposalVersion version:pv)
            {
                if (version.getVersion().equals("2.0"))
                {
                    proposalVersionList.add(version);
                }
                else if(version.getVersion().startsWith("1.") && version.getStatus().equals("Published") && !version.getVersion().equals("1.0"))
                {
                        proposalVersionList.add(version);
                }
                else if(version.getVersion().equals("1.0"))
                {
                    proposalVersionList.add(version);
                }
                else if(version.getVersion().startsWith("0.") && version.getStatus().equals("Published"))
                {
                    proposalVersionList.add(version);
                }
            }
            LOG.info("List1 size : " +proposalVersionList.size());
            if(proposalVersionList.size()!=0)
            {
                Date date = proposalVersionList.get(0).getUpdatedOn();
                ProposalVersion proposalVersionTobeConsidered = proposalVersionList.get(0);
                for(ProposalVersion proposalVersion:proposalVersionList)
                {
                    if (proposalVersion.getUpdatedOn().after(date))
                    {
                        proposalVersionTobeConsidered = proposalVersion;
                    }
                }
                amount+=proposalVersionTobeConsidered.getFinalAmount();
                quoteNumberCRM+=p.getQuoteNoNew();
            }
        }
        sendToCRM.setEstimated_project_cost_c(amount);
        sendToCRM.setQuotation_number_c(quoteNumberCRM);
        LOG.info("CRM JSON ON PUBLISH " +sendToCRM.toString());
        return sendToCRM;
    }

    public SendToCRM updatePriceInCRMOnConfirm() {
        Double amount=0.0;
        String quoteNumberCRM="";
        SendToCRM sendToCRM = new SendToCRM();
        sendToCRM.setOpportunity_name(proposalHeader.getCrmId());
        List<ProposalHeader> proposalHeaders=proposalDataProvider.getProposalHeadersByCrmIds(proposalHeader.getCrmId());
        for(ProposalHeader p:proposalHeaders)
        {
            List<ProposalVersion> pv=proposalDataProvider.getAllProductDetails(p.getId());
            for(ProposalVersion version:pv)
            {
                if (version.getVersion().equals("2.0"))
                {
                    amount+=version.getFinalAmount();
                    quoteNumberCRM+=p.getQuoteNoNew();
                }
                else if(version.getVersion().equals("1.0"))
                {
                    amount+=version.getFinalAmount();
                    quoteNumberCRM+=p.getQuoteNoNew();
                }
            }
        }


        /*List<ProposalHeader> proposalHeaders=proposalDataProvider.getProposalHeadersByCrmIds(proposalHeader.getCrmId());
        for(ProposalHeader p:proposalHeaders) {
            if ((p.getStatus().equals("DSO")) || (p.getStatus().equals("Confirmed"))){
                List<ProposalVersion> pv = proposalDataProvider.getLatestVersionDetails(p.getId(), p.getVersion());
                LOG.info("size of pv"+pv.size());

                for (ProposalVersion version : pv) {
                    LOG.info("pv" + version.getVersion() + "pv quote" + version.getProposalId());
                    amount += version.getFinalAmount();
                    LOG.info("confirmed amount " + amount);
                }

                quoteNumberCRM += p.getQuoteNoNew();


                LOG.info("psize***" + proposalHeaders.size());
            }
        }*/
        sendToCRM.setFinal_proposal_amount_c(amount);
        sendToCRM.setEstimated_project_cost_c(amount);
        sendToCRM.setQuotation_number_c(quoteNumberCRM);
        LOG.debug("Send to CRM on confirm### : " + sendToCRM.toString());

        return sendToCRM;
    }
}
