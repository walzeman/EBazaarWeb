
package business.externalinterfaces;
import java.util.List;




import business.exceptions.BackendException;
import business.productsubsystem.DbClassCatalogTypes;

public interface ProductSubsystem {

	public int readQuantityAvailable(Product product) throws BackendException;
	
	/** obtains product for a given product name */
    public Product getProductFromName(String prodName) throws BackendException;
    
    /** reads the product from the productid */
	public Product getProductFromId(Integer prodId) throws BackendException;
	
	public List<Catalog> getCatalogList() throws BackendException;
	
	/** gets a list of products from the database, based on catalog */
	public List<Product> getProductList(Catalog catalog) throws BackendException;
	
	public Integer getProductIdFromName(String prodName) throws BackendException;
	
	/** retrieves catalog from database based on catalog Name */
    public Catalog getCatalogFromName(String catName) throws BackendException;
    
	/** retrieves catalog from database based on catalog id  */
    public Catalog getCatalogFromId(int catId) throws BackendException;

	/** saves newly created catalog */
	public int saveNewCatalog(String catName) throws BackendException;

	/** saves a new product obtained from user input */
	public int saveNewProduct(Product product) throws BackendException;

	/** deletes a product obtained from user input */
	public void deleteProduct(Product product) throws BackendException;
	
	/** deletes a catalog obtained from user input */
	public void deleteCatalog(Catalog catalog) throws BackendException;
	
	/** update Catalog */
	public void updateCatalog(Catalog catalog) throws BackendException;
	
	/** update Product */
	public void updateProduct(Product product) throws BackendException;
	
	/** for testing*/
	public DbClassCatalogTypes getGenericDbClassCatalogTypes();

}