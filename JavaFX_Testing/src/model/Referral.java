package model;

import PersistenceHandler.DBHandler;

public class Referral {
    private int referralId;
    private String referralName;
    private String referralEmail;
    private static final DBHandler sqlhandler = DBHandler.getInstance();

    public Referral(String referralName, String referralEmail) {
        this.referralName = referralName;
        this.referralEmail = referralEmail;
    }
    public boolean insertReferral(Referral referral) {
    	return sqlhandler.insertReferral(referral);	
    }

    public int getReferralId() {
        return referralId;
    }

    public void setReferralId(int referralId) {
        this.referralId = referralId;
    }

    public String getReferralName() {
        return referralName;
    }

    public void setReferralName(String referralName) {
        this.referralName = referralName;
    }

    public String getReferralEmail() {
        return referralEmail;
    }

    public void setReferralEmail(String referralEmail) {
        this.referralEmail = referralEmail;
    }
}
