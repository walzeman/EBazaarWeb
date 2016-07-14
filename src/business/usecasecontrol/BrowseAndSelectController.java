package business.usecasecontrol;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import business.RulesQuantity;
import business.exceptions.BackendException;
import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import business.externalinterfaces.CartItem;
import business.externalinterfaces.Catalog;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.Product;
import business.externalinterfaces.ProductSubsystem;
import business.externalinterfaces.Rules;
import business.externalinterfaces.ShoppingCart;
import business.externalinterfaces.ShoppingCartSubsystem;
import business.util.DataUtil;

@Component
public class BrowseAndSelectController {
		
	@Autowired ShoppingCartSubsystem shoppingCartSubsystem;
	@Autowired ProductSubsystem productSubsystem;
	
	public void updateShoppingCartItems(List<CartItem> cartitems) {
		shoppingCartSubsystem.updateShoppingCartItems(cartitems);
	}
	
	public List<CartItem> getCartItems() {
		return shoppingCartSubsystem.getCartItems();
	}
	
	public void saveLiveCart() throws BackendException {
		shoppingCartSubsystem.saveLiveCart();
	}
	
	public ShoppingCart getLiveCart(){
		return shoppingCartSubsystem.getLiveCart();
	}
	
	
	/** Makes saved cart live in subsystem and then returns the new list of cartitems */
	public void retrieveSavedCart() {
		//ShoppingCartSubsystem shopCartSS = ShoppingCartSubsystemFacade.INSTANCE;
		
		// Saved cart was retrieved during login
		shoppingCartSubsystem.makeSavedCartLive();	
	}
	
	public void runQuantityRules(Product product, String quantityRequested)
			throws RuleException, BusinessException {

		//find current quant avail since quantity may have changed
		//since product was first loaded into UI
		int currentQuantityAvail = 	getQuantityAvailable(product);
		Rules transferObject = new RulesQuantity(currentQuantityAvail, quantityRequested);
		transferObject.runRules();

	}
	
	public List<Catalog> getCatalogs() throws BackendException {
		//ProductSubsystem pss = new ProductSubsystemFacade();
		return productSubsystem.getCatalogList();
	}
	
	public Catalog getCatalog(int catalogId) throws BackendException {
		//ProductSubsystem pss = new ProductSubsystemFacade();
		return productSubsystem.getCatalogFromId(catalogId);
	}
	
	public List<Product> getProducts(Catalog catalog) throws BackendException {
		//ProductSubsystem pss = new ProductSubsystemFacade();
		return productSubsystem.getProductList(catalog);
	}
	public Product getProductForProductName(String name) throws BackendException {
		//ProductSubsystem pss = new ProductSubsystemFacade();
		return productSubsystem.getProductFromName(name);
	}
	
	/** Assume customer is logged in */
	public CustomerProfile getCustomerProfile() {
		CustomerSubsystem cust = DataUtil.readCustFromCache();
		return cust.getCustomerProfile();
	}
	
	public int getQuantityAvailable(Product product) throws BackendException{
		//ProductSubsystem pss = new ProductSubsystemFacade();
		return productSubsystem.readQuantityAvailable(product);
	}
}
