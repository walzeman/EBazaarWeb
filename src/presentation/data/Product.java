package presentation.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import business.util.Convert;

@XmlRootElement 
public class Product implements Serializable{

	private static final long serialVersionUID = 3678107792576131001L;

	//@Pattern(regexp="P[1-9]+", message="{Pattern.Product.productId.validation}")	
	private String productId;
	
	@Size(min=4, max=50, message="Min length 4 characters")
	private String name;
	
	@Min(value=0, message="Min value is 0")
	@Digits(integer=8, fraction=2, message="Must be all digits")
	@NotNull(message= "**Must**")
	private double unitPrice;
	private String description;
	private String manufacturer;
	
	@DateTimeFormat(pattern = Convert.DATE_PATTERN)
	private LocalDate mfg = LocalDate.now();
	private int category;
	
	@Min(value=0, message="Min value is 0")
	@Digits(integer=8, fraction=2, message="Must be all digits")
	@NotNull(message= "**Must**")
	private int unitsInStock;
	private int unitsInOrder;
	private boolean discontinued;
	private String condition;
	@JsonIgnore 
	private MultipartFile  productImage;

	public Product() {
		super();
	}

	public Product(String productId, String name, double unitPrice) {
		this.productId = productId;
		this.name = name;
		this.unitPrice = unitPrice;
	}

	public String getProductId() {
		return productId;
	}

	public LocalDate getMfg() {
		return mfg;
	}

	public void setMfg(LocalDate mfg) {
		this.mfg = mfg;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getDescription() {
		return description;
	}

	@XmlTransient  
	public MultipartFile getProductImage() {
		return productImage;
	}

	public void setProductImage(MultipartFile productImage) {
		this.productImage = productImage;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public int getUnitsInStock() {
		return unitsInStock;
	}

	public void setUnitsInStock(int unitsInStock) {
		this.unitsInStock = unitsInStock;
	}

	public int getUnitsInOrder() {
		return unitsInOrder;
	}

	public void setUnitsInOrder(int unitsInOrder) {
		this.unitsInOrder = unitsInOrder;
	}

	public boolean isDiscontinued() {
		return discontinued;
	}

	public void setDiscontinued(boolean discontinued) {
		this.discontinued = discontinued;
	}
	
	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (productId == null) {
			if (other.productId != null)
				return false;
		} else if (!productId.equals(other.productId))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((productId == null) ? 0 : productId.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Product [productId=" + productId + ", name=" + name + "]";
	}
}
