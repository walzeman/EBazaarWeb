package business.customersubsystem;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import business.jdbc.JdbcAccountsDBTemplate;
import middleware.exceptions.DatabaseException;

@Repository
class DbClassCustomerProfile extends JdbcAccountsDBTemplate {
	 
    private String readQuery 
        = "SELECT custid,fname,lname FROM Customer WHERE custid = ?";
     
    public CustomerProfileImpl readCustomerProfile(Integer custId) throws DatabaseException {
    	
    	return jdbcTemplate.queryForObject(readQuery,  new Object[]{custId}, new RowMapper<CustomerProfileImpl>(){
			@Override
			public CustomerProfileImpl mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new CustomerProfileImpl(rs.getInt("custid"),rs.getString("fname"),rs.getString("lname"));
			}
    	});
    }
}
