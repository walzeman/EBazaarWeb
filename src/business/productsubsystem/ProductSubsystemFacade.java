package business.productsubsystem;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import business.exceptions.BackendException;
import business.externalinterfaces.Catalog;
import business.externalinterfaces.Product;
import business.externalinterfaces.ProductSubsystem;
import middleware.exceptions.DatabaseException;

/* @modified by Ngoc Nguyen */

@Service
public class ProductSubsystemFacade implements ProductSubsystem {
	
	private static final Logger LOG = 
			Logger.getLogger(ProductSubsystemFacade.class.getPackage().getName());
	
	
	@Autowired 
	DbClassProduct dbClassProduct;
	
	@Autowired
	DbClassCatalog dbClassCatalog;
	
	@Autowired 
	DbClassCatalogTypes dbClassCatalogTypes;
	
	public static Catalog createCatalog(int id, String name) {
		return new CatalogImpl(id, name);
	}
	public static Product createProduct(Catalog c, String name, 
			LocalDate date, int numAvail, double price) {
		return new ProductImpl(c, name, date, numAvail, price);
	}
	public static Product createProduct(Catalog c, Integer pi, String pn, int qa, 
			double up, LocalDate md, String desc) {
		return new ProductImpl(c, pi, pn, qa, up, md, desc);
	}
	
	
	@Override
	public int readQuantityAvailable(Product product) throws BackendException {
		// TODO Auto-generated method stub
    	try {
			Product updatedProduct = dbClassProduct.readProductById(product.getProductId());
			return updatedProduct.getQuantityAvail();
		} catch(DatabaseException e) {
			throw new BackendException(e);
		}	
    }
	@Override
	public Product getProductFromName(String prodName) throws BackendException {
		// TODO Auto-generated method stub
		try {
			Product product =  dbClassProduct.readProductByName(prodName);
			//update catalog name
			product.getCatalog().setName(getCatalogFromId(product.getCatalog().getId()).getName());
			return product;
		} catch(DatabaseException e) {
			throw new BackendException(e);
		}
	}
	@Override
    public Product getProductFromId(Integer prodId) throws BackendException {
		// TODO Auto-generated method stub
		try {
			Product product =  dbClassProduct.readProductById(prodId);
			//update catalog name
			product.getCatalog().setName(getCatalogFromId(product.getCatalog().getId()).getName());
			return product;
		} catch(DatabaseException e) {
			throw new BackendException(e);
		}
	}
	@Override
    public List<Catalog> getCatalogList() throws BackendException {
		// TODO Auto-generated method stub
    	try {
			return dbClassCatalogTypes.getCatalogTypes().getCatalogs();
		} catch(DatabaseException e) {
			throw new BackendException(e);
		}
    }
	@Override
    public List<Product> getProductList(Catalog catalog) throws BackendException {
		// TODO Auto-generated method stub
    	try {
    		return dbClassProduct.readProductList(catalog);
    	} catch(DatabaseException e) {
    		throw new BackendException(e);
    	}
    }
	@Override
	public Integer getProductIdFromName(String prodName) throws BackendException {
		// TODO Auto-generated method stub
		try {
			Product product = dbClassProduct.readProductByName(prodName);
			if(product == null) return null;
			return product.getProductId();
		} catch(DatabaseException e) {
			throw new BackendException(e);
		}
	}
	@Override
	public Catalog getCatalogFromName(String catName) throws BackendException {
		// TODO Auto-generated method stub
		try{
			return dbClassCatalog.readCatalogByName(catName);
		} catch(DatabaseException e) {
			throw new BackendException(e);
		}	
	}
	
	@Override
	public Catalog getCatalogFromId(int catId) throws BackendException {
		// TODO Auto-generated method stub
		try{
			return dbClassCatalog.readCatalogById(catId);
		} catch(DatabaseException e) {
			throw new BackendException(e);
		}	
	}
	@Override
	public int saveNewCatalog(String catName) throws BackendException {
		// TODO Auto-generated method stub
		try{
			return dbClassCatalog.saveNewCatalog(catName);
		} catch(DatabaseException e){
			throw new BackendException(e);
		}
	}
	
	@Override
	public int saveNewProduct(Product product) throws BackendException {
		// TODO Auto-generated method stub
		try {
			return dbClassProduct.saveNewProduct(product);
		} catch(DatabaseException e) {
    		throw new BackendException(e);
    	}
	}
	@Override
	public void deleteProduct(Product product) throws BackendException {
		// TODO Auto-generated method stub
		try {			
			dbClassProduct.deleteProduct(product.getProductId());
		} catch(DatabaseException e) {
    		throw new BackendException(e);
    	}
	}
	@Override
	public void deleteCatalog(Catalog catalog) throws BackendException {
		// TODO Auto-generated method stub
		try {
			dbClassCatalog.deleteCatalog(catalog.getId());
		} catch(DatabaseException e) {
    		throw new BackendException(e);
    	}
	}
	
	/** @implemented by Ngoc Nguyen */
	@Override
	public void updateCatalog(Catalog catalog) throws BackendException {
		// TODO Auto-generated method stub
		try {
			dbClassCatalog.updateCatalog(catalog.getId(), catalog.getName());
		} catch(DatabaseException e) {
    		throw new BackendException(e);
    	}
	}
	
	@Override
	public void updateProduct(Product product) throws BackendException {
		// TODO Auto-generated method stub
		try {
			dbClassProduct.updateProduct(product);
		} catch(DatabaseException e) {
    		throw new BackendException(e);
		}
	}
	
	
	@Override
	public DbClassCatalogTypes getGenericDbClassCatalogTypes() {
		// TODO Auto-generated method stub
		DbClassCatalogTypes dbclass = new DbClassCatalogTypes();
		return dbclass;
	}
	

	
}
