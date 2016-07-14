//package business.ordersubsystem;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import business.customersubsystem.CustomerProfileImpl;
//import business.exceptions.BackendException;
//import business.externalinterfaces.CartItem;
//import business.externalinterfaces.CustomerProfile;
//import business.externalinterfaces.OrderSubsystem;
//import business.externalinterfaces.ShoppingCart;
//import business.shoppingcartsubsystem.CartItemImpl;
//import business.shoppingcartsubsystem.ShoppingCartImpl;
//import middleware.exceptions.DatabaseException;
//
//public class Test {
//
//	public static void main(String[] args) throws BackendException, DatabaseException {
//		// TODO Auto-generated method stub
//		CustomerProfile cpf = new CustomerProfileImpl(0103, "wallelgn", "abrham",false);
//		
//		OrderSubsystem osf = new OrderSubsystemFacade(cpf);
//		
//		CartItem cartItem1 = new CartItemImpl("soccer ball", "5", "400");
//		cartItem1.setProductId(4533);
//		
//		CartItem cartItem2 = new CartItemImpl("bascket ball", "3", "340");
//		cartItem2.setProductId(4633);
//		
//		CartItem cartItem3 = new CartItemImpl("volley ball", "8", "900");
//		cartItem3.setProductId(4733);
//		
//		List<CartItem> carts = new ArrayList<>();
//		
//		carts.add(cartItem1);
//		carts.add(cartItem2);
//		carts.add(cartItem3);
//		
//		ShoppingCart shopCart = new ShoppingCartImpl();
//		shopCart.setCartItems(carts);
//		
//		osf.submitOrder(shopCart);
//		
//		
//
//	}
//
//}
