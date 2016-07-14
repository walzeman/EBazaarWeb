package business.ordersubsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import business.exceptions.BackendException;
import business.externalinterfaces.CartItem;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.Order;
import business.externalinterfaces.OrderItem;
import business.externalinterfaces.OrderSubsystem;
import business.externalinterfaces.ShoppingCart;
import middleware.exceptions.DatabaseException;

@Service
public class OrderSubsystemFacade implements OrderSubsystem {
	@Autowired
	DbClassOrder dbClassOrder;
	
	private static final Logger LOG = 
			Logger.getLogger(OrderSubsystemFacade.class.getPackage().getName());
	CustomerProfile custProfile;
	 
	@Override
	public void setCustomerProfile(CustomerProfile customerProfile) {
		this.custProfile = customerProfile;
	}
	
	/** 
     *  Used by customer subsystem at login to obtain this customer's order history from the database.
	 *  Assumes cust id has already been stored into the order subsystem facade 
	 *  This is created by using auxiliary methods at the bottom of this class file.
	 *  First get all order ids for this customer. For each such id, get order data
	 *  and form an order, and with that order id, get all order items and insert
	 *  into the order.
	 */
     //  @implemented by walzeman
    public List<Order> getOrderHistory() throws BackendException{
    	
    	
    	List<Order> orderList = new ArrayList<Order>();   	
    	
    	try{
    		List<Integer> orderIdList = getAllOrderIds() ; // current customer's list of orderid's fetched from database
    		for(Integer orderId : orderIdList ){
    			
    			Order order = new OrderImpl();
    			 
    			order = getOrderData(orderId);				// forming an order data from an orderId
    			
    			order.setOrderId(orderId);
    			order.setOrderItems(getOrderItems(orderId));	// fetching orderItem based on an orderId and inserting it into order form
    			orderList.add(order);							// adding order to the list of order 	
    			
    		}   		   		   		    				
    		return orderList;
    		
    	} catch (Exception e) {
			throw new BackendException(e);
		}
    	
    
    }
    
    public void submitOrder(ShoppingCart shopCart) throws DatabaseException{
    	
    	 List<CartItem> carts = shopCart.getCartItems(); 
    	 Order order = new OrderImpl();
    	 order.setBillAddress(shopCart.getBillingAddress());
    	 order.setShipAddress(shopCart.getShippingAddress());
    	 order.setPaymentInfo(shopCart.getPaymentInfo());
    	
    	 List<OrderItem> orderItemlist = new ArrayList<>();
    	 
    	 for(CartItem cartItem : carts){
    		// orderItem = Util.createOrderItemFromCartItem(cartItem, orderId);
    		 OrderItem orderItem = new OrderItemImpl();
    		 orderItem = createOrderItem(cartItem.getProductid(),1000,cartItem.getQuantity(),cartItem.getTotalprice());
    		    // Here i hard coded the orderId to 1000, just to be consistent with the Interface the teacher gave us
    		    // it will be overwritten by the correct OrderId which is created at the time of Insertion in to database
    		 orderItemlist.add(orderItem);
    		 
    	 }
    	 
    	 order.setOrderItems(orderItemlist);    	     	 
    	 dbClassOrder.submitOrder(custProfile, order);
    	 
    }
    
    public void submitOrder(CustomerProfile custProfile, Order order) throws DatabaseException {
    	dbClassOrder.submitOrder(custProfile, order);    	
    }
	
	/** Used whenever an order item needs to be created from outside the order subsystem */
    public OrderItem createOrderItem(Integer prodId, Integer orderId, String quantityReq, String totalPrice) {
    	//implemented By walzeman
      //  LOG.warning("Method createOrderItem(prodid, orderid, quantity, totalprice) still needs to be implemented");
        
        OrderItem orderItem = new OrderItemImpl();
        
        orderItem.setProductId(prodId);
        orderItem.setOrderId(orderId);
        orderItem.setQuantity(Integer.parseInt(quantityReq));
        orderItem.setUnitPrice((Double.parseDouble(totalPrice))/Integer.parseInt(quantityReq));
        
        return orderItem;
    }
    
    /** to create an Order object from outside the subsystem */
    public Order createOrder(Integer orderId, String orderDate, String totalPrice) {
    	//implement
        LOG.warning("Method  createOrder(Integer orderId, String orderDate, String totalPrice) still needs to be implemented");
    	return null;
    }
    
    ///////////// Methods internal to the Order Subsystem -- NOT public
    public List<Integer> getAllOrderIds() throws DatabaseException {       
        return dbClassOrder.getAllOrderIds(custProfile);
        
    }
    
    /** Part of getOrderHistory */
    List<OrderItem> getOrderItems(Integer orderId) throws DatabaseException {
        return dbClassOrder.getOrderItems(orderId);
    }
    
    /** Uses cust id to locate top-level order data for customer -- part of getOrderHistory */
    OrderImpl getOrderData(Integer orderId) throws DatabaseException {
    	return dbClassOrder.getOrderData(orderId);
    }

}
