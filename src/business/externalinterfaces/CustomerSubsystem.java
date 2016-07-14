
package business.externalinterfaces;
import java.util.List;

import business.exceptions.BackendException;
import business.exceptions.BusinessException;
import business.exceptions.RuleException;



/**
 * Encapsulates the customer and the things the customers owns, like address, credit
 * card, shopping cart. 
 * 
 * Note: createAddress method is static now to permit creation of an Address object
 * before a customer has been created.
 *
 */
public interface CustomerSubsystem {
	/** Use for loading order history,
	 * default addresses, default payment info, 
	 * saved shopping cart,cust profile
	 * after login*/
    public void initializeCustomer(Integer id, int authorizationLevel) throws BackendException;
    
    /**
     * Returns true if user has admin access
     */
    public boolean isAdmin();
    
    
    /** Use for saving an address created by user and marked as 'new' */
    public void saveNewAddress(Address addr) throws BackendException;
    
    /** Use to supply all stored addresses of a customer when he wishes to select an
	 * address in ship/bill window */
    public List<Address> getAllAddresses() throws BackendException;
    
    /** Used to obtain this customer's order history. Used by other subsystems
     * to read current user's order history (not used during login process)*/
    public List<Order> getOrderHistory();
    
    
    /** Used whenever a customer name needs to be accessed */
    public CustomerProfile getCustomerProfile();
    
    /** Used when ship/bill window is first displayed */
    public Address getDefaultShippingAddress();
    
    /** Used when ship/bill window is first displayed */
    public Address getDefaultBillingAddress();
    
    /** Used when payment window is first displayed */
    public CreditCard getDefaultPaymentInfo();
    
    /** Used after user has decided upon an address and is proceeding to 
	 * payment window
	 */
    public void setShippingAddressInCart(Address addr);
    
    /** Used after user has decided upon an address and is proceeding to 
	 * payment window
	 */
    public void setBillingAddressInCart(Address addr);
    
    /** Used after user has filled out payment window and is proceeding to checkout*/
    public void setPaymentInfoInCart(CreditCard cc);
    
    /** Use when a credit card instance is needed outside the subsystem */
    //obsolete - this method has become static
    //public CreditCard createCreditCard(String name, String num, String type, String expDate);
    
    /** Use when user submits final order -- customer sends its shopping cart to order subsystem
	 *  and order subsystem extracts items from shopping cart and prepares order*/
    public void submitOrder() throws BackendException;
    
    /**
     * After an order is submitted, the list of orders cached in CustomerSubsystemFacade
     * will be out of date; this method should cause order data to be reloaded
     */
    public void refreshAfterSubmit() throws BackendException;
    
    /**
	 * Used whenever the shopping cart needs to be displayed
	 */
    public ShoppingCartSubsystem getShoppingCart();
    
    /**
	 * save shopping cart to database
	 */
    public void saveShoppingCart() throws BackendException;
		
    /**
	 *  create an IAddress based on address fields 
     */
    //public Address createAddress(String[] addressInfo);
    
    /**
	 *  run address rules and return the cleansed address
     *  if a RuleException is thrown, this represents a validation error
     *  and the error message should be extracted and displayed
     */
    public Address runAddressRules(Address addr) throws RuleException, BusinessException; 
    

    /**
	*  run payment rules;
     *  if a RuleException is thrown, this represents a validation error
     *  and the error message should be extracted and displayed
     */
    public void runPaymentRules(Address addr, CreditCard cc) throws RuleException, BusinessException;

    //NEW
    public void checkCreditCard() throws BusinessException;
    
    //TESTING
    public DbClassAddressForTest getGenericDbClassAddress(); 
    public CustomerProfile getGenericCustomerProfile();
    
}
