package presentation.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import presentation.gui.GuiConstants;
import presentation.gui.GuiUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import business.externalinterfaces.Order;
import business.externalinterfaces.OrderItem;

public class OrderPres {
	private Order order;
	
	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
	public SimpleStringProperty orderIdProperty() {
		return new SimpleStringProperty(String.valueOf(order.getOrderId()));
	}
	public SimpleStringProperty dateProperty() {
		return new  SimpleStringProperty(
			order.getOrderDate().format(
				DateTimeFormatter.ofPattern(GuiConstants.DATE_FORMAT)));
		
	}
	public SimpleStringProperty totalPriceProperty() {
		return new SimpleStringProperty(
				String.format("%.2f", order.getTotalPrice()));
	}

	public ObservableList<OrderItemPres> getOrderItemsPres() {
		return GuiUtils.orderItemsToOrderItemsPres(order.getOrderItems());
	}
	
	
	public double getTotalPrice() {
		return order.getTotalPrice();
	}
	
	public int getOrderId() {
		return order.getOrderId();
	}
	
	public LocalDate getOrderDate() {
		return order.getOrderDate();
	}
	
	public List<OrderItem> getOrderItems() {
		return order.getOrderItems();
	}
}
