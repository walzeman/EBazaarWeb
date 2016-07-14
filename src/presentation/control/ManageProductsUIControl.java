package presentation.control;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import business.exceptions.BackendException;
import business.externalinterfaces.Catalog;
import business.usecasecontrol.ManageProductsController;
import business.util.Convert;
import presentation.data.BrowseSelectData;
import presentation.data.CatalogPres;
import presentation.data.Product;
import presentation.data.ProductPres;

@Controller
@RequestMapping("/admin")
public class ManageProductsUIControl {
	private static final Logger LOG = Logger.getLogger(ManageProductsController.class.getName());
	
	@Autowired
	ManageProductsController manageProductsController;
	
	@Autowired
	BrowseSelectData browseSelectData;

	@RequestMapping
	public String index(HttpServletRequest request) {		
		return "admin_home";
	}

	@RequestMapping("/catalog")
	public String catalog(HttpSession session) {		
		//Catalog
		List<Catalog> catalogs = null;
		try {
			catalogs = manageProductsController.getCatalogsList();
		} catch (BackendException e) {
			e.printStackTrace();
		}
		
		session.setAttribute("catalogs", catalogs);		
		return "admin_catalog";
	}

	@RequestMapping(value = "/catalog", method = RequestMethod.POST)
	public String addCatalog(HttpServletRequest request, ModelMap modelMap) {
		
		try {
			manageProductsController.saveNewCatalog(request.getParameter("name"));
		} catch (BackendException e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
		}
				
		//force to reload
		request.getSession().setAttribute("catalogs", null);

		return "redirect:/admin/catalog";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/catalog/{id}")
	public String deleteCatalog(@PathVariable int id, HttpSession session) {
		List<Catalog> catalogs = (List<Catalog>)session.getAttribute("catalogs");
		for (Catalog catalog : catalogs) {
			if(catalog.getId() == id){
				try {
					manageProductsController.deleteCatalog(catalog);
				} catch (BackendException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		return "redirect:/admin/catalog";
	}

	@RequestMapping("/products")
	public String getAllProducts(HttpSession session) {
		List<ProductPres> result = new ArrayList<>();		
		try {
			List<CatalogPres> catalogs = browseSelectData.getCatalogList();
			for (CatalogPres cp : catalogs) {
				List<ProductPres> pres = browseSelectData.getProductList(cp);
				for (ProductPres productPres : pres) {
					productPres.getCatalog().setName(cp.getName());
				}
				
				result.addAll(pres);
			}
			
		} catch (BackendException e) {
			e.printStackTrace();
		} 

		session.setAttribute("products", result);
		return "admin_product_list";
	}
	
	@RequestMapping("/product")
	public String product(HttpSession session, ModelMap modelMap) {
		populateCatalog(session);

		modelMap.addAttribute("newProduct", new Product());
		return "admin_product_add";
	}
	
	@SuppressWarnings("unchecked")
	private List<Catalog> populateCatalog(HttpSession session){
		if(session.getAttribute("catalogs") == null){
			List<Catalog> catalogs = null;
			try {
				catalogs = manageProductsController.getCatalogsList();
			} catch (BackendException e) {
				e.printStackTrace();
			}
			session.setAttribute("catalogs", catalogs);
		}
		
		return (List<Catalog>) session.getAttribute("catalogs");
	}

	@RequestMapping(value = "/product", method = RequestMethod.POST)
	public String processProductForm(@ModelAttribute("newProduct") @Valid Product productToBeAdded, 
			BindingResult result, HttpServletRequest request) {

		if (result.hasErrors()) {
			return "admin_product_add";
		}

		String[] suppressedFields = result.getSuppressedFields();

		if (suppressedFields.length > 0) {
			throw new RuntimeException("Attempting to bind disallowed fields: "
					+ StringUtils.arrayToCommaDelimitedString(suppressedFields));
		}
		
		Optional<Catalog> catalogPres;
		try {
			 catalogPres = manageProductsController.getCatalogsList()
			.stream()
			.filter(item ->productToBeAdded.getCategory() == item.getId())
			.findFirst(); 

			 int productId = 0;
			 if(productToBeAdded.getProductId().length() != 0){
				 business.externalinterfaces.Product p = manageProductsController.getProductFromId(Integer.parseInt(productToBeAdded.getProductId()));
				 p.setProductName(productToBeAdded.getName());
				 p.setQuantityAvail(productToBeAdded.getUnitsInStock());
				 p.setUnitPrice(productToBeAdded.getUnitPrice());
				 p.setDescription(productToBeAdded.getDescription());
				 p.setMfgDate(productToBeAdded.getMfg());
				 p.getCatalog().setId(productToBeAdded.getCategory());				 
				 manageProductsController.updateProduct(p);
			 }
			 else {
				 productId = manageProductsController.saveNewProduct(catalogPres.orElse(null), 
							productToBeAdded.getName(), 
							productToBeAdded.getMfg(),
							productToBeAdded.getUnitsInStock(), 
							productToBeAdded.getUnitPrice());
			 }		 			
			
			MultipartFile productImage = productToBeAdded.getProductImage();
			String rootDirectory = request.getSession().getServletContext().getRealPath("/");

			if (productImage != null && !productImage.isEmpty()) {
				try {
					productImage.transferTo(
							new File(rootDirectory + "resources\\images\\" + productId + ".png"));
				} catch (Exception e) {
					throw new RuntimeException("Product Image saving failed", e);
				}
			}		
			
		} catch (BackendException e1) {
			e1.printStackTrace();
		}

		return "redirect:/admin/products";
	}

	@RequestMapping(value = "/products/edit/{id}")
	public String editProduct(@PathVariable int id, HttpSession session, ModelMap modelMap) {
		//for dropdownbox 
		populateCatalog(session);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(Convert.DATE_PATTERN);
	 
		List<ProductPres> productPres = (List<ProductPres>)session.getAttribute("products");
		for (ProductPres pp : productPres) {
			if(pp.getProduct().getProductId() == id){
				Product product = new Product();
				product.setProductId(pp.getProduct().getProductId()+"");
				product.setCategory(pp.getCatalog().getId());
				product.setName(pp.getName());
				product.setUnitPrice( Double.parseDouble(pp.getUnitPrice()));
				product.setUnitsInStock(pp.getQuantityAvail());
				product.setDescription(pp.getDescription());
				product.setMfg(LocalDate.parse(pp.getMfg(),dtf));
				modelMap.addAttribute("newProduct", product);
				break;			
			}
		}
		
		modelMap.addAttribute("isEdit", true);
		return "admin_product_add";
	}
	
	@RequestMapping(value = "/products/delete/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		List<ProductPres> productPres = (List<ProductPres>)session.getAttribute("products");
		for (ProductPres pp : productPres) {
			if(pp.getProduct().getProductId() == id){		
				try {
					manageProductsController.deleteProduct(pp.getProduct());
				} catch (BackendException e) {
					e.printStackTrace();
				}
				break;			
			}
		}
		
		return "redirect:/admin/products";
	}
	
}
