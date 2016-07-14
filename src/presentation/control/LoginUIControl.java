package presentation.control;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.ws.spi.http.HttpContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import business.BusinessConstants;
import business.Login;
import business.SessionCache;
import business.exceptions.BackendException;
import business.exceptions.UserException;
import business.usecasecontrol.LoginControl;
import presentation.data.LoginData;

@Controller
public class LoginUIControl {

    @Autowired LoginData loginData;
    
	@RequestMapping(value = "/login", method = {RequestMethod.GET})
	public String index(Model model) {
		Login login = new Login(0, "");
		model.addAttribute("login", login);
		return "login";
	}
	
    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    public String authenticate(HttpSession session, HttpServletResponse response, @ModelAttribute("login") Login login, Model model, BindingResult result) {
     
    	try{    		
    		if(!tryParseInt(login.getLoginId())){ //not a integer
    			throw new UserException("User id is not integer");
    		}
    		
    		login.setCustId(Integer.parseInt(login.getLoginId()));    		
    		int authorizationLevel = loginData.authenticate(login);
    		loginData.loadCustomer(login, authorizationLevel);

    		login.setLoggedIn(true);
    		login.setAdmin(authorizationLevel == 1);
    		session.setAttribute(BusinessConstants.LOGGED_IN, login);
    		
    		String lastVisitedUrl = (String)session.getAttribute(BusinessConstants.LAST_REQUEST_URL);
    		if(lastVisitedUrl != null && lastVisitedUrl.length() > 0){
    			try {
					response.sendRedirect(lastVisitedUrl);
				} catch (IOException e) {
					e.printStackTrace();
				}
    		}
    		else{    		
    			return authorizationLevel == 1 ? "redirect:/admin" : "redirect:/";
    		}
    	} catch(UserException e) {
    		result.addError(new ObjectError("*", "username/password error"));
    	} catch(BackendException e) {
    		result.addError(new ObjectError("*", "Unknow exception in server"));
    	}	
    	
        return "login";
    }

    @RequestMapping(value = "/logout")
    public String logout(HttpSession session) {
    	
    	SessionCache cache = SessionCache.getInstance();
		boolean loggedIn = (Boolean)cache.get(BusinessConstants.LOGGED_IN);
	
		if(loggedIn) {
			cache.add(BusinessConstants.LOGGED_IN, Boolean.FALSE);
			cache.remove(BusinessConstants.CUSTOMER);
			
    		session.setAttribute(BusinessConstants.LOGGED_IN, null);
		}
		
        return "redirect:/";
    }
    
    private boolean tryParseInt(String value) {  
        try {  
            Integer.parseInt(value);  
            return true;  
         } catch (NumberFormatException e) {  
            return false;  
         }  
   }
}
