
package business.ordersubsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import business.exceptions.BackendException;
import business.externalinterfaces.Address;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.Order;
import business.externalinterfaces.OrderItem;
import business.externalinterfaces.Product;
import business.externalinterfaces.ProductSubsystem;
import business.jdbc.JdbcAccountsDBTemplate;
import business.util.Convert;
import middleware.exceptions.DatabaseException;

@Repository
public class DbClassOrder extends JdbcAccountsDBTemplate {
	
	@Autowired
	ProductSubsystem productSubsystem;
	
	private CustomerProfile customerProfile;
	
	public void setCustomerProfile(CustomerProfile cp){
		this.customerProfile = cp;
	}
	
	private String orderItemsQuery = "SELECT * FROM OrderItem WHERE orderid = ?";
	private String orderIdsQuery = "SELECT orderid FROM Ord WHERE custid = ?";
	private String orderDataQuery = "SELECT orderdate, totalpriceamount FROM Ord WHERE orderid = ?";
	private String submitOrderQuery = "INSERT into Ord "
			+ "(custid, shipaddress1, shipcity, shipstate, shipzipcode, billaddress1, billcity, billstate,"
			+ "billzipcode, nameoncard,  cardnum,cardtype, expdate, orderdate, totalpriceamount) "
			+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private String submitOrderItemQuery = "INSERT into OrderItem "
			+ "(orderid, productid, quantity, totalprice)"		// , shipmentcost,taxamount  parameters should be included
			+ "VALUES(?,?,?,?)";
	
	public List<Integer> getAllOrderIds(CustomerProfile custProfile) throws DatabaseException {
		if(custProfile == null)
			custProfile = customerProfile;
		
		List<Integer> orderIds = jdbcTemplate.query(orderIdsQuery, new RowMapper<Integer>(){
			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getInt("orderid");
			}			
		}, custProfile.getCustId());
		 
		return Collections.unmodifiableList(orderIds);
	}

	OrderImpl getOrderData(Integer orderId) throws DatabaseException {
		return jdbcTemplate.queryForObject(orderDataQuery, new Object[]{orderId}, new RowMapper<OrderImpl>(){
			@Override
			public OrderImpl mapRow(ResultSet rs, int rowNum) throws SQLException {
				OrderImpl orderData = new OrderImpl();
				String orderdate = rs.getString("orderdate");
				 double totalpriceamount = rs.getDouble("totalpriceamount");
				LocalDate date = Convert.localDateForString(orderdate);
				
				orderData.setDate(date);
				orderData.setTotalPriceAmount(totalpriceamount);
				return orderData;
			}
		});
	}

	/**
	 * This method submits top-level data in Order to the Ord table (this is
	 * executed within the helper method submitOrderData) and then, after it
	 * gets the order id, it submits each OrderItem from Order to the OrderItem
	 * table (items are submitted one at a time using submitOrderItem). All this
	 * is done within a transaction. Separate methods are provided
	 */
	@Transactional(value = "txManagerAccounts", propagation=Propagation.REQUIRES_NEW, readOnly=false)
	void submitOrder(CustomerProfile custProfile, Order order) throws DatabaseException {
		// implemented by walzeman
		LOG.warning("Method submitOrder(CustomerProfile custProfile, Order order) has been implemented");
			 
		Integer orderId =  submitOrderData(custProfile,order) ;
		order.setOrderId(orderId);
		
		List<OrderItem> itemList = order.getOrderItems(); 
		for(OrderItem item : itemList){
			item.setOrderId(orderId);
			submitOrderItem(item); 
		}
		
	}

	/** This is part of the general submitOrder method */
	private Integer submitOrderData(CustomerProfile custProfile, Order order) throws DatabaseException {
		Address shipAddr = order.getShipAddress();
		Address billAddr = order.getBillAddress();
		CreditCard cc = order.getPaymentInfo();
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {//anonymous inner class
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				PreparedStatement stmt = conn.prepareStatement(submitOrderQuery, new String[]{"orderid"});//autogen id field
				stmt.setInt(1, custProfile.getCustId());
				stmt.setString(2, shipAddr.getStreet());
				stmt.setString(3, shipAddr.getCity());
				stmt.setString(4, shipAddr.getState());
				stmt.setString(5, shipAddr.getZip());
				stmt.setString(6, billAddr.getStreet());
				stmt.setString(7, billAddr.getCity());
				stmt.setString(8, billAddr.getState());
				stmt.setString(9, billAddr.getZip());
				stmt.setString(10, cc.getNameOnCard());
				stmt.setString(11, cc.getCardNum());
				stmt.setString(12, cc.getCardType());
				stmt.setString(13, cc.getExpirationDate());
				stmt.setString(14, Convert.localDateAsString(order.getOrderDate()));
				stmt.setDouble(15, order.getTotalPrice());
				return stmt;
			}
		}, keyHolder);
		
		return keyHolder.getKey().intValue();
	}

	/** This is part of the general submitOrder method */
	private void submitOrderItem(OrderItem item) throws DatabaseException {
		// implemented by walzeman
		LOG.warning("Method submitOrderItem(OrderItem item) in DbClassOrder has been implemented.");
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {//anonymous inner class
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				PreparedStatement stmt = conn.prepareStatement(submitOrderItemQuery, new String[]{"orderitemid"});//autogen id field
				stmt.setInt(1, item.getOrderId());
				stmt.setInt(2, item.getProductId());
				stmt.setInt(3, item.getQuantity());
				stmt.setDouble(4, item.getTotalPrice());				
				return stmt;
			}
		}, keyHolder);		
	}
	
	// @implemented by walzeman
	public List<OrderItem> getOrderItems(Integer orderId) throws DatabaseException {
		LOG.warning("Wallelgn has implementd ===> Method getOrderItems(Integer orderId) has been implmeented");

		List<OrderItem> orderItems = jdbcTemplate.query(orderItemsQuery, new RowMapper<OrderItem>(){

			@Override
			public OrderItem mapRow(ResultSet rs, int rowNum) throws SQLException {
				int productId = rs.getInt("productid");
				int quantity = rs.getInt("quantity");
				double totalPrice = rs.getDouble("totalPrice");
				String productName = "";
				Product product;
				try {
					product = productSubsystem.getProductFromId(productId);
					if(product != null){
						productName = product.getProductName();
					}
				} catch (BackendException e) {
					e.printStackTrace();
				}				
				return new OrderItemImpl(productName, quantity, (totalPrice/quantity));					
			}
			
		}, orderId);
 
		return Collections.unmodifiableList(orderItems);
	}
}
