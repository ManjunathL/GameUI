package com.mygubbi.game.dashboard.view.proposals;

import com.mygubbi.game.dashboard.ServerManager;
import com.mygubbi.game.dashboard.data.ProposalDataProvider;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by user on 25-Apr-17.
 */
public class CRMsearchWindow extends Window
{
    private static final Logger LOG = LogManager.getLogger(CRMsearchWindow.class);
    Grid crmgrid;
    private ProposalDataProvider proposalDataProvider = ServerManager.getInstance().getProposalDataProvider();

}
