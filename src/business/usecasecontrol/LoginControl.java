
package business.usecasecontrol;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import business.BusinessConstants;
import business.DbClassLogin;
import business.Login;
import business.SessionCache;
import business.exceptions.BackendException;
import business.exceptions.UserException;
import business.externalinterfaces.CustomerSubsystem;


@Component
public class LoginControl {
	
	@Autowired CustomerSubsystem customerSubsystem;
	@Autowired DbClassLogin dbClassLogin;
	//returns authorization level if authenticated
	public int authenticate(Login login) throws UserException, BackendException {
		
		dbClassLogin.setLogin(login);
        if(!dbClassLogin.authenticate()) {
        	throw new UserException("Authentication failed for ID: " + login.getCustId());
        }
        return dbClassLogin.getAuthorizationLevel();
        
	}
	
	public CustomerSubsystem prepareAndStoreCustomerObject(Login login, int authorizationLevel) throws BackendException {
		
		//need to place into SessionContext immediately since the facade will be used during
		//initialization; alternative: createAddress, createCreditCard methods
		//made to be static
		SessionCache cache = SessionCache.getInstance();
		cache.add(BusinessConstants.LOGGED_IN, Boolean.TRUE);
		cache.add(BusinessConstants.CUSTOMER, customerSubsystem);
        
        //finish initialization
        customerSubsystem.initializeCustomer(login.getCustId(), authorizationLevel);
        return customerSubsystem;
	}
    
}
