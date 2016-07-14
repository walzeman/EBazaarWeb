package business.productsubsystem;

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

import business.externalinterfaces.Catalog;
import business.externalinterfaces.Product;
import business.jdbc.JdbcProductsDBTemplate;
import business.util.Convert;
import business.util.TwoKeyHashMap;
import middleware.exceptions.DatabaseException;

@Repository
public class DbClassProduct extends JdbcProductsDBTemplate {	
	private String loadProdTableQuery = "SELECT * FROM product";
	private String readProductByIdQuery = "SELECT * FROM Product WHERE productid = ?";
	private String readProductByNameQuery = "SELECT * FROM Product WHERE productname = ?";
	private String readProdListQuery = "SELECT * FROM Product WHERE catalogid = ?";
	private String updateProductQuery = "UPDATE Product SET productname = ?, totalquantity = ?, "
												+ "priceperunit = ?, mfgdate = ?, catalogid=? WHERE productid = ?";
	private String deleteProductQuery = "DELETE FROM product WHERE productid = ?";
	private String saveNewProdQuery = "INSERT INTO product(catalogid, productname, totalquantity, "
			+ "priceperunit, mfgdate, description) VALUES (?, ?, ?, ?, ?, ?)"; 
	
	/**
	 * The productTable matches product ID and product name with
	 * the corresponding Product object. It is static so
	 * that requests for "read product" based on product ID can be handled
	 * without extra db hits. Useful for customer use cases, but not
	 * for manage products use case
	 */
	private static TwoKeyHashMap<Integer, String, Product> productTable;

	public TwoKeyHashMap<Integer, String, Product> readProductTable()
			throws DatabaseException {
		if (productTable != null) {
			return productTable.clone();
		}
		//productTable needs to be populated, so call refresh
		return refreshProductTable();
	}
 
	public TwoKeyHashMap<Integer, String, Product> refreshProductTable() throws DatabaseException {
		 List<Product> products =  jdbcTemplate.query(loadProdTableQuery,new ProductRowMapper());
		 for (Product product : products) {
			productTable.put(product.getProductId(), product.getProductName(), product);
		}
				
		// Return a clone since productTable must not be corrupted
		return productTable.clone();
	}

	public List<Product> readProductList(Catalog cat) throws DatabaseException {		
		return jdbcTemplate.query(readProdListQuery, new ProductRowMapper(), cat.getId());		
	}

	public Product readProductById(Integer productId)
			throws DatabaseException {
		if (productTable != null && productTable.isAFirstKey(productId)) {
			return productTable.getValWithFirstKey(productId);
		}
		return jdbcTemplate.queryForObject(readProductByIdQuery, new ProductRowMapper(), productId);
	}
	
	public Product readProductByName(String productName) throws DatabaseException {
		if (productTable != null && productTable.isASecondKey(productName)) {
			return productTable.getValWithSecondKey(productName);
		}
		
		return jdbcTemplate.queryForObject(readProductByNameQuery, new ProductRowMapper(), productName);
	}
	
	/* @added by Ngoc Nguyen */
	public int deleteProduct(Integer productId)	throws DatabaseException {
		return jdbcTemplate.update(deleteProductQuery,productId);
	}
	
	/* @added by Ngoc Nguyen */
	@Transactional(value = "txManagerProducts", propagation=Propagation.REQUIRES_NEW, readOnly=false)
	public void updateProduct(Product product) throws DatabaseException {
		jdbcTemplate.update(new PreparedStatementCreator() {//anonymous inner class
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				PreparedStatement stmt = conn.prepareStatement(updateProductQuery, new String[]{});
				stmt.setString(1, product.getProductName());				
				stmt.setInt(2, product.getQuantityAvail());
				stmt.setDouble(3, product.getUnitPrice());
				stmt.setString(4, Convert.localDateAsString(product.getMfgDate()));
				stmt.setInt(5, product.getCatalog().getId());
				stmt.setInt(6, product.getProductId());
				return stmt;
			}
		});
	}

	/**
	 * Database columns: productid, productname, totalquantity, priceperunit,
	 * mfgdate, catalogid, description
	 */
	@Transactional(value = "txManagerProducts", propagation=Propagation.REQUIRES_NEW, readOnly=false)
	public int saveNewProduct(Product product) throws DatabaseException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {//anonymous inner class
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				PreparedStatement stmt = conn.prepareStatement(saveNewProdQuery, new String[]{"productid"});//autogen id field
				stmt.setInt(1, product.getCatalog().getId());
				stmt.setString(2, product.getProductName());
				stmt.setInt(3, product.getQuantityAvail());
				stmt.setDouble(4, product.getUnitPrice());
				stmt.setString(5, Convert.localDateAsString(product.getMfgDate()));
				stmt.setString(6, product.getDescription());
				return stmt;
			}
		}, keyHolder);
		
		return keyHolder.getKey().intValue();
	}
	
	private class ProductRowMapper implements RowMapper<Product>{

		@Override
		public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
			CatalogImpl catalog = new CatalogImpl(rs.getInt("catalogid"), null);
			
			return new ProductImpl(catalog, rs.getInt("productid"),
					rs.getString("productname"),
					rs.getInt("totalquantity"),
					rs.getDouble("priceperunit"),
					Convert.localDateForString(rs.getString("mfgdate")),
					rs.getString("description"));
		}		
	}
	
}
