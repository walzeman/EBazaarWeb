package business.usecasecontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import business.BusinessConstants;
import business.SessionCache;
import business.exceptions.BackendException;
import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import business.externalinterfaces.Address;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.ShoppingCartSubsystem;

@Component
public class CheckoutController{
	
	@Autowired ShoppingCartSubsystem shoppingCartSubsystem;
	
	private static final Logger LOG = Logger.getLogger(CheckoutController.class.getPackage().getName());
	
	
	public void runShoppingCartRules() throws RuleException, BusinessException {
		shoppingCartSubsystem.runShoppingCartRules();
	}
	
	public List<Address> getShippingAddresses(CustomerProfile custProf) throws BackendException {
		//implement
		LOG.warning("Method CheckoutController.getShippingAddresses has not been implemented");
		return new ArrayList<Address>();
	}
	
	public List<Address> getBillingAddresses(CustomerProfile custProf) throws BackendException {
		//implement
		LOG.warning("Method CheckoutController.getBillingAddresses has not been implemented");
		return new ArrayList<Address>();
	}
	
	//implement
//	public Address getDefaultShippingAddress(CustomerProfile custProf) throws BackendException {
//		
//	}
//	
//	public Address getDefaultBillingAddress(CustomerProfile custProf) throws BackendException {
//		
//	}
	
	
	public void setShippingAddress(Address addr) {
		shoppingCartSubsystem.setShippingAddress(addr);
	}
	
	public void setBillingAddress(Address addr) {
		shoppingCartSubsystem.setBillingAddress(addr);
	}
		
	public void setPaymentInfo(CreditCard creditCard) {
		shoppingCartSubsystem.setPaymentInfo(creditCard);
	}
		
	public void runPaymentRules(Address addr, CreditCard cc) throws RuleException, BusinessException {
		CustomerSubsystem cust = 
				(CustomerSubsystem)SessionCache.getInstance().get(BusinessConstants.CUSTOMER);
		cust.runPaymentRules(addr, cc);
	}
	
	public Address runAddressRules(Address addr) throws RuleException, BusinessException {
		CustomerSubsystem cust = 
			(CustomerSubsystem)SessionCache.getInstance().get(BusinessConstants.CUSTOMER);
		return cust.runAddressRules(addr);
	}
	
	/** Asks the ShoppingCart Subsystem to run final order rules */
	public void runFinalOrderRules(ShoppingCartSubsystem scss) throws RuleException, BusinessException {
		shoppingCartSubsystem.runFinalOrderRules();
	}
	
	/** Asks Customer Subsystem to check credit card against 
	 *  Credit Verification System 
	 */
	public void verifyCreditCard() throws BusinessException {
		CustomerSubsystem cust = 
				(CustomerSubsystem)SessionCache.getInstance().get(BusinessConstants.CUSTOMER);			
		cust.checkCreditCard();
	}
	
	public void saveNewAddress(Address addr) throws BackendException {
		CustomerSubsystem cust = 
			(CustomerSubsystem)SessionCache.getInstance().get(BusinessConstants.CUSTOMER);			
		cust.saveNewAddress(addr);
	}
	
	/** Asks Customer Subsystem to submit final order */
	public void submitFinalOrder() throws BackendException {
		CustomerSubsystem cust = 
				(CustomerSubsystem)SessionCache.getInstance().get(BusinessConstants.CUSTOMER);			
		cust.submitOrder();
	}


}
