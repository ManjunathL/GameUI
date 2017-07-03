package com.mygubbi.game.dashboard.domain;

/**
 * Created by user on 04-01-2017.
 */
public class ProposalCity
{
    public static final String ID = "id";
    public static final String CITY = "city";
    public static final String CUR_MONTH = "curmonth";
    public static final String PROPOSAL_ID = "proposalId";
    public static final String QUOTE_NO = "quoteNo";
    public static final String CITY_LOCKED = "cityLocked";

    private int id;
    private String city;
    private int curmonth;
    private String proposalId;
    private String quoteNo;
    private String cityLocked;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getCurmonth() {
        return curmonth;
    }

    public void setCurmonth(int curmonth) {
        this.curmonth = curmonth;
    }

    public String getProposalId() {
        return proposalId;
    }

    public void setProposalId(String proposalId) {
        this.proposalId = proposalId;
    }

    public String getQuoteNo() {
        return quoteNo;
    }

    public void setQuoteNo(String quoteNo) {
        this.quoteNo = quoteNo;
    }

    public String getCityLocked() {
        return cityLocked;
    }

    public void setCityLocked(String cityLocked) {
        this.cityLocked = cityLocked;
    }

    @Override
    public String toString() {
        return "ProposalCity{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", curmonth=" + curmonth +
                ", proposalId='" + proposalId + '\'' +
                ", quoteNo='" + quoteNo + '\'' +
                ", cityLocked='" + cityLocked + '\'' +
                '}';
    }
}
