package com.mygubbi.game.dashboard.domain;

import com.vaadin.server.Resource;

/**
 * Created by User on 08-05-2017.
 */
public class SendToCRM {


    public static final String CRM_ID= "opportunity_name";
    public static final String FINAL_PROPOSAL_AMOUNT= "final_proposal_amount_c";
    public static final String ESTIMATED_PROJECT_COST = "estimated_project_cost_c";
    public static final String QUOTE_NO = "quotation_number_c";

    private String opportunity_name;
    private double final_proposal_amount_c;
    private double estimated_project_cost_c;
    private String quotation_number_c;

    public String getOpportunity_name() {
        return opportunity_name;
    }

    public void setOpportunity_name(String opportunity_name) {
        this.opportunity_name = opportunity_name;
    }

    public double getFinal_proposal_amount_c() {
        return final_proposal_amount_c;
    }

    public void setFinal_proposal_amount_c(double final_proposal_amount_c) {
        this.final_proposal_amount_c = final_proposal_amount_c;
    }

    public double getEstimated_project_cost_c() {
        return estimated_project_cost_c;
    }

    public void setEstimated_project_cost_c(double estimated_project_cost_c) {
        this.estimated_project_cost_c = estimated_project_cost_c;
    }

    public String getQuotation_number_c() {
        return quotation_number_c;
    }

    public void setQuotation_number_c(String quotation_number_c) {
        this.quotation_number_c = quotation_number_c;
    }
}


