package business.shoppingcartsubsystem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import business.customersubsystem.RulesPayment;
import business.exceptions.BackendException;
import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import business.externalinterfaces.Address;
import business.externalinterfaces.CartItem;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.Product;
import business.externalinterfaces.ProductSubsystem;
import business.externalinterfaces.Rules;
import business.externalinterfaces.ShoppingCart;
import business.externalinterfaces.ShoppingCartSubsystem;
import middleware.exceptions.DatabaseException;

@Service
public class ShoppingCartSubsystemFacade implements ShoppingCartSubsystem {
	
	@Autowired
	DbClassShoppingCart dbClassShoppingCart;
	
	@Autowired 
	ProductSubsystem productSubsystem;
	
	ShoppingCartImpl liveCart = new ShoppingCartImpl(new LinkedList<CartItem>());
	ShoppingCartImpl savedCart;
	Integer shopCartId;
	CustomerProfile customerProfile;
	Logger log = Logger.getLogger(this.getClass().getPackage().getName());

	// interface methods
	public void setCustomerProfile(CustomerProfile customerProfile) {
		this.customerProfile = customerProfile;
	}
	
	public void makeSavedCartLive() {
		liveCart = savedCart;
	}
	
	public ShoppingCart getLiveCart() {
		return liveCart;
	}
	
	public void retrieveSavedCart() throws BackendException {
		try {
			ShoppingCartImpl cartFound = dbClassShoppingCart.retrieveSavedCart(customerProfile);
			if(cartFound == null) {
				savedCart = new ShoppingCartImpl(new ArrayList<CartItem>());
			} else {
				savedCart = cartFound;
			}
		} catch(DatabaseException e) {
			throw new BackendException(e);
		}

	}
	
	
	
	public void updateShoppingCartItems(List<CartItem> list) {
		liveCart.setCartItems(list);
	}
	
	public List<CartItem> getCartItems() {
		return liveCart.getCartItems();
	}
	
	//static methods
	public static CartItem createCartItem(int productId, String productName, String quantity, String totalprice) {
		try {
			return new CartItemImpl(productId, productName, quantity, totalprice);
		} catch(BackendException e) {
			throw new RuntimeException("Can't create a cartitem because of productid lookup: " + e.getMessage());
		}
	}


	
	//interface methods for testing
	
	public ShoppingCart getEmptyCartForTest() {
		return new ShoppingCartImpl();
	}

	
	public CartItem getEmptyCartItemForTest() {
		return new CartItemImpl();
	}

	@Override
	public boolean deleteCartItem(String itemName) {

		for(Iterator<CartItem> iterator = liveCart.getCartItems().listIterator(); iterator.hasNext();){
			if(itemName.equals(iterator.next().getProductName())){
				iterator.remove();
			}
		}
		
		return false;
	}

	@Override
	public boolean deleteCartItem(int pos) {
		int index = 0;
		for(Iterator<CartItem> iterator = liveCart.getCartItems().listIterator(); iterator.hasNext();){
			if(++index == pos){
				iterator.remove();
			}
		}
		return false;
	}

	@Override
	public void clearLiveCart() {
		liveCart.clearCart();
	}

	@Override
	public void addCartItem(String itemName, String quantity, String totalPrice, Integer position)
			throws BackendException {
		Product product = productSubsystem.getProductFromName(itemName);
		liveCart.insertItem(position, new CartItemImpl(product.getProductId(), itemName, quantity, totalPrice));
	}

	@Override
	public List<CartItem> getLiveCartItems() {
		return liveCart.getCartItems();
	}

	@Override
	public void setShippingAddress(Address addr) {
		liveCart.setShipAddress(addr);
	}

	@Override
	public void setBillingAddress(Address addr) {
		liveCart.setBillAddress(addr);
	}

	@Override
	public void setPaymentInfo(CreditCard cc) {
		liveCart.setPaymentInfo(cc);
	}

	@Override
	public void saveLiveCart() throws BackendException {
		try {
			dbClassShoppingCart.saveCart(customerProfile, liveCart);
						
			//Update savedCart to liveCart
			savedCart = liveCart;
		} catch(DatabaseException e) {
			throw new BackendException(e);
		}
	}

	@Override
	public void runShoppingCartRules() throws RuleException, BusinessException {
		Rules transferObject = new RulesShoppingCart(liveCart);
		transferObject.runRules();
	}

	@Override
	public void runFinalOrderRules() throws RuleException, BusinessException {
		Rules transferObject = new RulesFinalOrder(liveCart);
		transferObject.runRules();
	}

}
