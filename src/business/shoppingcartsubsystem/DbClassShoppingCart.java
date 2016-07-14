
package business.shoppingcartsubsystem;

import static business.util.StringParse.makeString;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import business.customersubsystem.CustomerSubsystemFacade;
import business.exceptions.BackendException;
import business.externalinterfaces.Address;
import business.externalinterfaces.CartItem;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.ShoppingCart;
import business.jdbc.JdbcAccountsDBTemplate;
import middleware.exceptions.DatabaseException;

@Repository
public class DbClassShoppingCart extends JdbcAccountsDBTemplate {
	 
	/////// queries and params
	private String getIdQuery = "SELECT shopcartid FROM ShopCartTbl WHERE custid = ?";
	// param is custProfile.getCustId()
	private String saveCartQuery = "INSERT INTO shopcarttbl (custid,shipaddress1, "
			+ "shipaddress2, shipcity, shipstate, shipzipcode, billaddress1, "
			+ "billaddress2, billcity, billstate, billzipcode, nameoncard, "
			+ "expdate,cardtype, cardnum, totalpriceamount, totalshipmentcost, "
			+ "totaltaxamount, totalamountcharged) " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private String getTopLevelSavedCartQuery = "SELECT * FROM shopcarttbl WHERE shopcartid = ?";
	// param is cartId
	private String getSavedItemsQuery = "SELECT * FROM shopcartitem WHERE shopcartid = ?";
	// param is cartId
	private String saveCartItemQuery = "INSERT INTO shopcartitem (shopcartid, productid, quantity, totalprice, shipmentcost, taxamount) "
			+ "VALUES (?,?,?,?,?,?)";
	private String deleteCartQuery = "DELETE FROM shopcarttbl WHERE shopcartid = ?";
	// param is cartId.intValue()
	private String deleteAllCartItemsQuery = "DELETE FROM shopcartitem WHERE shopcartid = ?";
	// param is cartId.intValue()
 
	ShoppingCartImpl cartImpl;
	ShoppingCart cart;// this is inserted to support a save operation
	CartItem cartItem;// this is inserted to support a save operation
	List<CartItem> cartItemsList;
	Integer cartId; // used once when read, but don't use other times
 
	@Transactional(value = "txManagerAccounts", propagation=Propagation.REQUIRES_NEW, readOnly=false)
	public void saveCart(CustomerProfile custProfile, ShoppingCart cart) throws DatabaseException {
		// This will be used by support methods
		this.cart = cart;
		Integer cartId = null;

		// this.custProfile = custProfile;
		List<CartItem> cartItems = cart.getCartItems();

		// If customer has a saved cart already, get its cartId -- will
		// delete
		// this cart as part of the transaction
		Integer oldCartId = getShoppingCartId(custProfile);

		// First, delete old cart in two steps
		if (oldCartId != null) {
			deleteCart(oldCartId);
			deleteAllCartItems(oldCartId);
		}

		// Second, save top level of cart to be saved
		cartId = saveCartTopLevel(custProfile); // returns new cartId

		// Finally, save the associated cartitems in a loop
		// We have the cartId for these cartitems
		for (CartItem item : cartItems) {
			item.setCartId(cartId);
			saveCartItem(item);
		} 
	}

	// Support method for saveCart -- part of another transaction started within
	// saveCart
	private void deleteCart(Integer cartId) throws DatabaseException {
		jdbcTemplate.update(deleteCartQuery, cartId);
	}

	// Support method for saveCart -- part of another transaction started within
	// saveCart
	private void deleteAllCartItems(Integer cartId) throws DatabaseException {
		jdbcTemplate.update(deleteAllCartItemsQuery, cartId);
	}

	// support method for saveCart and also for retrieveSavedCart; part of
	// another transaction
	private Integer getShoppingCartId(CustomerProfile custProfile) throws DatabaseException {
		return jdbcTemplate.queryForInt(getIdQuery, new Object[]{custProfile.getCustId()});
	}

	// Support method for saveCart -- part of another transaction started within
	// saveCart
	// Precondition: shopping cart was stored as instance variable (should be
	// done by saveCart method)
	private int saveCartTopLevel(CustomerProfile custProfile) throws DatabaseException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		Address sa = cart.getShippingAddress();
		Address ba = cart.getBillingAddress();
		CreditCard pi = cart.getPaymentInfo();
		
		jdbcTemplate.update(new PreparedStatementCreator() {//anonymous inner class
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				PreparedStatement stmt = conn.prepareStatement(saveCartQuery, new String[]{"shopcartid"});//autogen id field
				stmt.setInt(1, custProfile.getCustId());
				stmt.setString(2, sa == null ? "" : cart.getShippingAddress().getStreet());
				stmt.setString(3,"");
				stmt.setString(4, sa == null ? "" : cart.getShippingAddress().getCity());
				stmt.setString(5, sa == null ? "" : cart.getShippingAddress().getState());
				stmt.setString(6, sa == null ? "" : cart.getShippingAddress().getZip());
				stmt.setString(7, ba == null ? "" : cart.getBillingAddress().getStreet());
				stmt.setString(8, "");
				stmt.setString(9, ba == null ? "" : cart.getBillingAddress().getCity());
				stmt.setString(10, ba == null ? "" : cart.getBillingAddress().getState());
				stmt.setString(11, ba == null ? "" : cart.getBillingAddress().getZip());
				stmt.setString(12, pi == null ? "" : cart.getPaymentInfo().getNameOnCard());
				stmt.setString(13, pi == null ? "" : cart.getPaymentInfo().getExpirationDate());
				stmt.setString(14,pi == null ? "" : cart.getPaymentInfo().getCardType());
				stmt.setString(15, pi == null ? "" : cart.getPaymentInfo().getCardNum());
				stmt.setDouble(16, cart.getTotalPrice());
				stmt.setDouble(17, 0.0);
				stmt.setDouble(18, 0.0);
				stmt.setDouble(19, cart.getTotalPrice());
				return stmt;
			}
		}, keyHolder);
		
		return keyHolder.getKey().intValue();		 
	}

	// Support method for saveCart -- part of another transaction started within
	// saveCart
	private void saveCartItem(CartItem item) throws DatabaseException {
		jdbcTemplate.update(new PreparedStatementCreator() {//anonymous inner class
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				PreparedStatement stmt = conn.prepareStatement(saveCartItemQuery);
				stmt.setInt(1, item.getCartid());
				stmt.setInt(2, item.getProductid());
				stmt.setString(3, item.getQuantity());
				stmt.setString(4, item.getTotalprice());
				stmt.setDouble(5, 0.0);
				stmt.setDouble(6, 0.0);
				return stmt;
			}
		});
		
	}

	@Transactional(value = "txManagerAccounts", propagation=Propagation.REQUIRES_NEW, readOnly=false)
	public ShoppingCartImpl retrieveSavedCart(CustomerProfile custProfile) throws DatabaseException {		
	
		ShoppingCartImpl cart = null;
		// First, get cartId
		Integer cartId = getShoppingCartId(custProfile);

		// Second, if saved cart found, get top level cart data
		if (cartId != null) {
			cart = getTopLevelSavedCart(cartId);

			// Last, get cart items associated with this cart id, and insert
			// into cart
			List<CartItem> items = getSavedCartItems(cartId);
			cart.setCartItems(items);
		}
		return cart;
	}

	// support method for retrieveSavedCart -- this is part of transaction that
	// begins in
	// retrieveSavedCart
	private List<CartItem> getSavedCartItems(Integer cartId) throws DatabaseException {
		return jdbcTemplate.query(getSavedItemsQuery, new RowMapper<CartItem>(){

			@Override
			public CartItem mapRow(ResultSet rs, int rowNum) throws SQLException {
				 
					try {
						return new CartItemImpl(rs.getInt("shopcartid"), rs.getInt("productid"), rs.getInt("cartitemid"),
									makeString(rs.getInt("quantity")), makeString(rs.getDouble("totalprice")), true);
					} catch (BackendException e) {
						LOG.log(Level.SEVERE, e.getMessage());
						e.printStackTrace();
					}			 
				 
				 return null;
			}
			
		}, cartId);
	}

	// support method for retrieveSavedCart -- this is part of transaction that
	// begins in
	// retrieveSavedCart
	private ShoppingCartImpl getTopLevelSavedCart(Integer cartId) throws DatabaseException {
		return jdbcTemplate.queryForObject(getTopLevelSavedCartQuery, new RowMapper<ShoppingCartImpl>(){

			@Override
			public ShoppingCartImpl mapRow(ResultSet rs, int rowNum) throws SQLException {
				ShoppingCartImpl cartImpl = new ShoppingCartImpl();
				Address shippingAddress = null;
				Address billingAddress = null;
				CreditCard creditCard = null;
				
				// load shipping address
				String shipStreet = rs.getString("shipaddress1");
				String shipCity = rs.getString("shipcity");
				String shipState = rs.getString("shipstate");
				String shipZip = rs.getString("shipzipcode");
				shippingAddress = CustomerSubsystemFacade.createAddress(shipStreet, shipCity, shipState, shipZip, true,
						false);

				// load billing address
				String billStreet = rs.getString("shipaddress1");
				String billCity = rs.getString("shipcity");
				String billState = rs.getString("shipstate");
				String billpZip = rs.getString("shipzipcode");
				billingAddress = CustomerSubsystemFacade.createAddress(billStreet, billCity, billState, billpZip, false,
						true);

				// load credit card: createCreditCard(String name, String num,
				// String type, expDate)
				String name = rs.getString("nameoncard");
				String num = rs.getString("cardnum");
				String type = rs.getString("cardtype");
				String exp = rs.getString("expdate");
				creditCard = CustomerSubsystemFacade.createCreditCard(name, exp, num, type);

				// load cart
				cartImpl.setCartId((new Integer(rs.getInt("shopcartid")).toString()));
				cartImpl.setShipAddress(shippingAddress);
				cartImpl.setBillAddress(billingAddress);
				cartImpl.setPaymentInfo(creditCard);
				
				return cartImpl;
			}
			
		}, cartId);
	}
}
