package presentation.data;

import java.util.ArrayList;
import java.util.List;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import business.BusinessConstants;
import business.SessionCache;
import business.exceptions.BackendException;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.Order;
import business.usecasecontrol.ViewOrdersController;

@Component
public class ViewOrdersData {
	
	@Autowired
	ViewOrdersController viewOrdersController;
	
	private static final Logger LOG = 
		Logger.getLogger(ViewOrdersData.class.getSimpleName());
	private OrderPres selectedOrder;
	public OrderPres getSelectedOrder() {
		return selectedOrder;
	}
	public void setSelectedOrder(OrderPres so) {
		selectedOrder = so;
	}
	
	public void refreshAfterSubmit() throws BackendException{
		SessionCache cache = SessionCache.getInstance();
		CustomerSubsystem  css = (CustomerSubsystem) cache.get(BusinessConstants.CUSTOMER);
		viewOrdersController.refreshAfterSubmit(css);
	}
	
	public List<OrderPres> getOrders() {
		LOG.warning("ViewOrdersData method getOrders() has not been implemented.");
		/* 
		 * @miki
		 * to get order history, get the customersusbystem info from the global cache
		 * and pass it to the getOrderHistory Method.  
		 * Get the orders and change each order into orderPres.
		 * */
		SessionCache cache = SessionCache.getInstance();
		CustomerSubsystem  css = (CustomerSubsystem) cache.get(BusinessConstants.CUSTOMER);

		List<Order> or_hist = new ArrayList<Order>();
		
		or_hist = viewOrdersController.getOrderHistory(css);
		
		List<OrderPres> ord_pres_hist = new ArrayList<OrderPres>();
		
		
		for(Order o : or_hist){
			OrderPres temp_ordPres = new OrderPres();
			temp_ordPres.setOrder(o);
			ord_pres_hist.add(temp_ordPres);
		}
		
		return ord_pres_hist;
		//return DefaultData.ALL_ORDERS;
	}
}
