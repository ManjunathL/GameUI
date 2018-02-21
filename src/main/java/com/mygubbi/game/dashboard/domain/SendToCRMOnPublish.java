package com.mygubbi.game.dashboard.domain;

/**
 * Created by User on 08-05-2017.
 */
public class SendToCRMOnPublish {


    public static final String CRM_ID= "opportunity_name";
    public static final String ESTIMATED_PROJECT_COST = "estimated_project_cost_c";
    public static final String QUOTE_NO = "quotation_number_c";
    public static final String PROPOSAL_LINK_C="proposal_link_c";
    public static final String PRESALES_USER_EMAIL="presales_user_email";


    private String opportunity_name;
    private double estimated_project_cost_c;
    private String quotation_number_c;
    private String proposal_link_c;
    private String presales_user_email;


    public String getOpportunity_name() {
        return opportunity_name;
    }

    public void setOpportunity_name(String opportunity_name) {
        this.opportunity_name = opportunity_name;
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

    public String getProposal_link_c() {
        return proposal_link_c;
    }

    public void setProposal_link_c(String proposal_link_c) {
        this.proposal_link_c = proposal_link_c;
    }

    public String getPresales_user_email() {
        return presales_user_email;
    }

    public void setPresales_user_email(String presales_user_email) {
        this.presales_user_email = presales_user_email;
    }

    @Override
    public String toString() {
        return "SendToCRMOnPublish{" +
                "opportunity_name='" + opportunity_name + '\'' +
                ", estimated_project_cost_c=" + estimated_project_cost_c +
                ", quotation_number_c='" + quotation_number_c + '\'' +
                ", proposal_link_c='" + proposal_link_c + '\'' +
                ", presales_user_email='" + presales_user_email + '\'' +
                '}';
    }
}


