
package business.usecasecontrol;

import java.util.List;

import org.springframework.stereotype.Component;

import business.exceptions.BackendException;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.Order;

/**
 * @author pcorazza
 */
@Component
public class ViewOrdersController   {
		
	public List<Order> getOrderHistory(CustomerSubsystem cust) {
		return cust.getOrderHistory();
	}
	
	public  void refreshAfterSubmit(CustomerSubsystem cust) throws BackendException {
		cust.refreshAfterSubmit();
	}
	
	
}
