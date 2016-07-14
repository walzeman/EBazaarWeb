package presentation.data;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import business.BusinessConstants;
import business.SessionCache;
import business.customersubsystem.CustomerSubsystemFacade;
import business.exceptions.BackendException;
import business.externalinterfaces.Address;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.CustomerSubsystem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import presentation.gui.GuiConstants;

@Component
public class CheckoutData {
	@Autowired
	CustomerSubsystem customerSubsystem;
	
	public Address createAddress(String street, String city, String state,
			String zip, boolean isShip, boolean isBill) {
		return CustomerSubsystemFacade.createAddress(street, city, state, zip, isShip, isBill);
	}
	
	public CreditCard createCreditCard(String nameOnCard,String expirationDate,
               String cardNum, String cardType) {
		return CustomerSubsystemFacade.createCreditCard(nameOnCard, expirationDate, 
				cardNum, cardType);
	}
	
	//Customer Ship Address Data
	private List<Address> shipAddresses;
	
	//Customer Bill Address Data
	private List<Address> billAddresses;
	/*
	private List<CustomerPres> shipInfoForCust() {
		//go to use case controller
		//get saved ship addresses for customer
		//get cust profile
		//assemble into a List<CustomerPres> and return
	}*/
	private List<Address> loadShipAddresses() {	
		
		return getAllShipAddresses()
						   .stream()
						   .filter(cust -> cust.isShippingAddress())
						   .collect(Collectors.toList());
										   
	}
	
	/* 
	 * @Miki
	 * added this method to get ship addresses from the database as a pure list *
	 */
	public List<Address> getAllShipAddresses(){
		
		try {
			List<Address> shipAdd = customerSubsystem.getAllAddresses()
					.stream()
					.filter(add->add.isShippingAddress())
					.collect(Collectors.toList());
			return shipAdd;
		} catch (BackendException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/* 
	 * @Miki
	 * added this method to get bill addresses from the database as a pure list *
	 */
	public List<Address> getAllBillAddresses(){
		
		try {
			List<Address> billAdd = customerSubsystem.getAllAddresses()
					.stream()
					.filter(add->add.isBillingAddress())
				.collect(Collectors.toList());
			
			return billAdd;
			
		} catch (BackendException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	

	public List<Address> loadBillAddresses() {
		return getAllBillAddresses()
				   .stream()
				   .filter(cust -> cust.isBillingAddress())
				   .collect(Collectors.toList());
	}
	public List<Address> getCustomerShipAddresses() {
		if(shipAddresses == null){
			shipAddresses = loadShipAddresses();
		}
			
		return shipAddresses;
	}
	public List<Address> getCustomerBillAddresses() {
		if(billAddresses == null){
			billAddresses = loadBillAddresses();
		}
		return billAddresses;
	}
	public List<String> getDisplayAddressFields() {
		return GuiConstants.DISPLAY_ADDRESS_FIELDS;
	}
	public List<String> getDisplayCredCardFields() {
		return GuiConstants.DISPLAY_CREDIT_CARD_FIELDS;
	}
	public List<String> getCredCardTypes() {
		return GuiConstants.CREDIT_CARD_TYPES;
	}
	// @added by walzeman
	public Address getDefaultShippingData() {
		return customerSubsystem.getDefaultShippingAddress();
	}
	
	//@added by walzeman
	public Address getDefaultBillingData() {
		return customerSubsystem.getDefaultBillingAddress();
	}
	
//	public List<String> getDefaultPaymentInfo() {
//		return DefaultData.DEFAULT_PAYMENT_INFO;
//	}
	
	// @added by walzeman
	public CreditCard getDefaultPaymentInfo(){
		
		return customerSubsystem.getDefaultPaymentInfo();
	}
	
	
	public CustomerProfile getCustomerProfile() {
		return customerSubsystem.getCustomerProfile();
	}
	
		
	
	private class ShipAddressSynchronizer implements Synchronizer {
		public void refresh(ObservableList list) {
			shipAddresses = list;
		}
	}
	public ShipAddressSynchronizer getShipAddressSynchronizer() {
		return new ShipAddressSynchronizer();
	}
	
	private class BillAddressSynchronizer implements Synchronizer {
		public void refresh(ObservableList list) {
			billAddresses = list;
		}
	}
	public BillAddressSynchronizer getBillAddressSynchronizer() {
		return new BillAddressSynchronizer();
	}
	
	public static class ShipBill {
		public boolean isShipping;
		public String label;
		public Synchronizer synch;
		public ShipBill(boolean shipOrBill, String label, Synchronizer synch) {
			this.isShipping = shipOrBill;
			this.label = label;
			this.synch = synch;
		}
		
	}
	
}
