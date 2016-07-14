package presentation.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import business.Login;
import business.exceptions.BackendException;
import business.exceptions.UserException;
import business.usecasecontrol.LoginControl;

@Service
public class LoginData {
	@Autowired LoginControl usecaseControl;
	
	public int authenticate(Login login) throws UserException, BackendException {   	
    	try {
    		int authorizationLevel = usecaseControl.authenticate(login);
    		return authorizationLevel;
    	} catch(UserException e) {
    		throw(e);
    	} catch(BackendException e) {
    		throw(e);
    	}	
	}
	/*public Login extractLogin(LoginWindow loginWindow) {
		Integer id = Integer.parseInt(loginWindow.getId());
    	String pwd = loginWindow.getPassword();
    	Login login = new Login(id, pwd);
    	return login;
	}*/
    public void loadCustomer(Login login, int authorizationLevel) throws BackendException {
        usecaseControl.prepareAndStoreCustomerObject(login, authorizationLevel); 
    }
}
