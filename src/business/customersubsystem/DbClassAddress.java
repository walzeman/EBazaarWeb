package business.customersubsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import business.externalinterfaces.Address;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.DbClassAddressForTest;
import business.jdbc.JdbcAccountsDBTemplate;
import middleware.exceptions.DatabaseException;


@Repository
class DbClassAddress extends JdbcAccountsDBTemplate implements DbClassAddressForTest{
	     
	//column names for Address table
    private final String STREET="street";
    private final String CITY = "city";
    private final String STATE = "state";
    private final String ZIP = "zip";
    private final String ISSHIP = "isship";
    private final String ISBILL = "isbill";
    
    private CustomerProfile customerProfile;
    
    public void setCustomerProfile(CustomerProfile cp){
    	this.customerProfile = cp;
    }
        
    private String insertQuery = "INSERT into altaddress " +
    		"(custid,street,city,state,zip,isship,isbill) " +
    		"VALUES(?,?,?,?,?,?,?)";
	private String readAllQuery  = "SELECT * from altaddress WHERE custid = ?";
	private String readDefaultBillQuery = "SELECT billaddress1 as street, billaddress2, billcity as city, billstate as state, billzipcode as zip " +
	            "FROM Customer WHERE custid = ?";
	private String readDefaultShipQuery = "SELECT shipaddress1 as street, shipaddress2, shipcity as city, shipstate as state, shipzipcode as zip "+
	    "FROM Customer WHERE custid = ?" ;
	private String readDefaultCardQuery = "SELECT nameoncard, expdate, cardnum,cardtype FROM Customer WHERE custid = ?";
		
    @Transactional(value = "txManagerAccounts", propagation=Propagation.REQUIRES_NEW, readOnly=false)
    void saveAddress(CustomerProfile custProfile, Address address) {
    	KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {//anonymous inner class
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				PreparedStatement stmt = conn.prepareStatement(insertQuery, new String[]{"addressid"});//autogen id field
				stmt.setInt(1, custProfile.getCustId());
				stmt.setString(2, address.getStreet());
				stmt.setString(3, address.getCity());
				stmt.setString(4, address.getState());
				stmt.setString(5, address.getZip());
				stmt.setBoolean(6, address.isShippingAddress());
				stmt.setBoolean(7, address.isBillingAddress());
				return stmt;
			}
		}, keyHolder);
    }
    
    CreditCard readDefaultCreditCardInfo(CustomerProfile custProfile) throws DatabaseException{    	
		return jdbcTemplate.queryForObject(readDefaultCardQuery,new Object[]{custProfile.getCustId()}, new RowMapper<CreditCard>() {
			@Override
			public CreditCard mapRow(ResultSet rs, int rowNum) throws SQLException {
				String nameoncard = rs.getString("nameoncard");
				String expdate = rs.getString("expdate");
				String cardnum = rs.getString("cardnum");
				String cardtype = rs.getString("cardtype");
				CreditCard creditCard = new CreditCardImpl(nameoncard, expdate, cardnum, cardtype);
				return creditCard;
			}
		});
    }
    
    Address readDefaultShipAddress(CustomerProfile custProfile) throws DatabaseException {
    	return jdbcTemplate.queryForObject(readDefaultShipQuery,new Object[]{custProfile.getCustId()}, new RowMapper<Address>() {
			@Override
			public Address mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new AddressImpl(rs.getString(STREET), rs.getString(CITY), rs.getString(STATE), rs.getString(ZIP),true,false);
			}
		});
    }
    
    Address readDefaultBillAddress(CustomerProfile custProfile) throws DatabaseException {
    	return jdbcTemplate.queryForObject(readDefaultBillQuery, new Object[]{custProfile.getCustId()}, new RowMapper<Address>(){
			@Override
			public Address mapRow(ResultSet rs, int rowNum) throws SQLException {
                  return new AddressImpl(rs.getString(STREET),rs.getString(CITY),rs.getString(STATE),rs.getString(ZIP),false,true);                 
			}    		
    	});
    }
     
    public List<Address> readAllAddresses(CustomerProfile custProfile) throws DatabaseException {
    	if(custProfile == null)
    		custProfile = this.customerProfile;
    	
    	return  jdbcTemplate.query(readAllQuery, new RowMapper<Address>(){
			@Override
			public Address mapRow(ResultSet rs, int rowNum) throws SQLException {
				  boolean isship = true;
                  
                  if(rs.getString(ISSHIP) != null && rs.getString(ISSHIP).equals("1"))
                  	isship = true;
                  else
                  	isship = false;                  
                  boolean isbill = true;
                  if(rs.getString(ISBILL) != null && rs.getString(ISBILL).equals("1"))
                  	isbill = true;
                  else
                  	isbill = false;
                  
                  return new AddressImpl(rs.getString(STREET),rs.getString(CITY),rs.getString(STATE),rs.getString(ZIP),isship,isbill);                 
			}    		
    	}, custProfile.getCustId());
    }
}