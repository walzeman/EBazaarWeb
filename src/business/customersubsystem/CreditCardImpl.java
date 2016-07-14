
package business.customersubsystem;

import business.externalinterfaces.CreditCard;


public class CreditCardImpl implements CreditCard {
    String nameOnCard;
    String expirationDate;
    String cardNum;
    String cardType;
    public CreditCardImpl(){}
    public CreditCardImpl(String nameOnCard,String expirationDate,
               String cardNum, String cardType) {
        this.nameOnCard=nameOnCard;
        this.expirationDate=expirationDate;
        this.cardNum=cardNum;
        this.cardType=cardType;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public String getExpirationDate() {
        return expirationDate;
    }


    public String getCardNum() {
        return cardNum;
    }

 
    public void setNameOnCard(String nameOnCard) {
		this.nameOnCard = nameOnCard;
	}
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getCardType() {
        return cardType;
    }

}
