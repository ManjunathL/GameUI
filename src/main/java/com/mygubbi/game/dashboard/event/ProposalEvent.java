package com.mygubbi.game.dashboard.event;

import com.mygubbi.game.dashboard.domain.ProposalListItem;

/**
 * Created by nitinpuri on 04-05-2016.
 */
public abstract class ProposalEvent {

    public static class ProposalUpdated {
    }

    public static class ListItemAdded {
        private ProposalListItem proposalListItem;
        public ListItemAdded(ProposalListItem proposalListItem) {
            this.proposalListItem = proposalListItem;
        }
        public ProposalListItem getProposalListItem() {
            return proposalListItem;
        }
    }


}
