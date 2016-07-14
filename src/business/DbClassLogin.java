package business;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import business.exceptions.BackendException;
import business.jdbc.JdbcAccountsDBTemplate;

@Repository
public class DbClassLogin extends JdbcAccountsDBTemplate {
	 
	private String authQuery = "SELECT * FROM Customer WHERE custid = ? AND password = ?";
 
    private Integer custId;
    int authorizationLevel;
    private String password;
    private boolean authenticated = false;
    
    public void setLogin(Login login){
        this.custId = login.getCustId();
        this.password = login.getPassword();
    }
    
    
    public boolean authenticate() throws BackendException {
    	LOG.info("Authenticating");
    	List<Integer> result = jdbcTemplate.query(authQuery, new RowMapper<Integer>(){

			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getInt("admin");
			}
    		
    	} , this.custId, this.password);
    	
    	if(result.size() > 0){
    		authenticated = true;
    		authorizationLevel = result.get(0);
    	}
    	
    	return authenticated;
    }
    
    public int getAuthorizationLevel() {
    	LOG.info("authorizationLevel = " + authorizationLevel);
        return authorizationLevel;
    }
}
