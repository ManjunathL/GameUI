package com.mygubbi.game.dashboard.event;

import com.mygubbi.game.dashboard.domain.Module;
import com.mygubbi.game.dashboard.domain.ProposalListViewItem;

/**
 * Created by nitinpuri on 04-05-2016.
 */
public abstract class ProposalEvent {

    public static class ProposalUpdated {
    }

    public static class ListItemAdded {
        private ProposalListViewItem proposalListViewItem;
        public ListItemAdded(ProposalListViewItem proposalListViewItem) {
            this.proposalListViewItem = proposalListViewItem;
        }
        public ProposalListViewItem getProposalListViewItem() {
            return proposalListViewItem;
        }
    }

    public static class ModuleUpdated {
        private Module module;

        public ModuleUpdated(Module module) {
            this.module = module;
        }

        public Module getModule() {
            return module;
        }
    }

}
