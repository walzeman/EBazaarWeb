
package business.externalinterfaces;

import java.util.List;

import business.exceptions.BackendException;
import middleware.exceptions.DatabaseException;


public interface OrderSubsystem {
    
	void setCustomerProfile(CustomerProfile customerProfile);
	
	/** 
     *  Used by customer subsystem at login to obtain this customer's order history from the database.
	 *  Assumes cust id has already been stored into the order subsystem facade */
    List<Order> getOrderHistory() throws BackendException;

	/** 
	 * Used by customer subsystem when a final order is submitted; this method extracts order and order items
	 * from the passed in shopping cart and saves to database using data access subsystem
	 * @throws DatabaseException 
	 */ 
    void submitOrder(ShoppingCart shopCart) throws BackendException, DatabaseException;
	
    public List<Integer> getAllOrderIds() throws DatabaseException;

}
