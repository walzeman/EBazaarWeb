package business.productsubsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import business.externalinterfaces.Catalog;
import business.jdbc.JdbcProductsDBTemplate;
import middleware.exceptions.DatabaseException;

/**
 * This class is concerned with managing data for a single
 * catalog. To read or update the entire list of catalogs in
 * the database, see DbClassCatalogs
 *
 */
@Repository
public class DbClassCatalog extends JdbcProductsDBTemplate {
	
	private static final Logger LOG = 
		Logger.getLogger(DbClassCatalog.class.getPackage().getName());
	
	private String readByIdQuery = "SELECT * FROM CatalogType where catalogid = ?";
	private String readByNameQuery = "SELECT * FROM CatalogType where catalogname = ?";
	private String updateQuery = "UPDATE CatalogType SET catalogname = ? where catalogid = ?";
	private String insertQuery = "INSERT into CatalogType (catalogname) VALUES(?)"; 
	private String deleteQuery = "DELETE FROM CatalogType WHERE catalogid = ?";
	
	@Transactional(value = "txManagerProducts", propagation=Propagation.REQUIRES_NEW, readOnly=false)
    public int saveNewCatalog(String catalogName) throws DatabaseException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {//anonymous inner class
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				PreparedStatement stmt = conn.prepareStatement(insertQuery, new String[]{"catalogid"});//autogen id field
				stmt.setString(1,catalogName);
				return stmt;
			}
		}, keyHolder);
		
		return keyHolder.getKey().intValue();
    }
    
    /* @added by Ngoc Nguyen */
    public Catalog readCatalogById(int catalogId) throws DatabaseException {
    	return jdbcTemplate.queryForObject(readByIdQuery, new CatalogRowMapper(), catalogId);
    }
    
    /* @added by Ngoc Nguyen */
    public Catalog readCatalogByName(String catalogName) throws DatabaseException {
    	return jdbcTemplate.queryForObject(readByIdQuery, new CatalogRowMapper(), catalogName);
    }
    
    /* @added by Ngoc Nguyen */
    public void updateCatalog(int catalogId, String catalogName) throws DatabaseException {
		jdbcTemplate.update(new PreparedStatementCreator() {//anonymous inner class
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				PreparedStatement stmt = conn.prepareStatement(updateQuery);//autogen id field
				stmt.setString(1,catalogName);
				stmt.setInt(2,catalogId);
				return stmt;
			}
		});
		
    }
    
    
    /* @added by Ngoc Nguyen */
    public int deleteCatalog(int catalogId) throws DatabaseException {
    	return jdbcTemplate.update(deleteQuery,catalogId);
    }
    
    private class CatalogRowMapper implements RowMapper<Catalog>{

		@Override
		public Catalog mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new CatalogImpl(rs.getInt("catalogid"), rs.getString("catalogname"));
		}
    	
    }
	
}
