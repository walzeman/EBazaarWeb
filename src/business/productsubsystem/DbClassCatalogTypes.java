package business.productsubsystem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import business.jdbc.JdbcProductsDBTemplate;
import middleware.exceptions.DatabaseException;

/**
 * This class is concerned with managing the entire
 * list of catalogtypes -- this is different from
 * managing one particular catalog (which is managed
 * by DbClassCatalog)
 */

@Repository
public class DbClassCatalogTypes extends JdbcProductsDBTemplate {
	 
    private String getTypesQuery = "SELECT * FROM CatalogType";
     
    public CatalogTypesImpl getCatalogTypes() throws DatabaseException {
         List<CatalogImpl> catalogs = jdbcTemplate.query(getTypesQuery, new RowMapper<CatalogImpl>(){

			@Override
			public CatalogImpl mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new CatalogImpl(rs.getInt("catalogid"),rs.getString("catalogname"));
			}
 
         });
         
         CatalogTypesImpl types = new CatalogTypesImpl();
         for (CatalogImpl catalogImpl : catalogs) {
			types.addCatalog(catalogImpl.getId(), catalogImpl.getName());
         }
         
         return types;
    }    
}
