package csi311;

public class Order {

	// fields for order object
	private long timeStamp;
	private String orderId;
	private String customerId;
	private String currentState;
	private String orderDescription;
	private int quantity;
	private double price;
	private boolean flagged;
	
	// primary order constructor
	public Order(long newTimeStamp, String newOrderId, String newCustomerId, String newCurrentState,
			String newOrderDesc, int quantity, double price) {
		
		this.setTimeStamp(newTimeStamp);
		this.orderId = newOrderId;
		this.customerId = newCustomerId;
		this.setNewState(newCurrentState);
		this.setNewDescription(newOrderDesc);
		this.setQuantity(quantity);
		this.setPrice(price);
		this.flagged = false;
	}
	
	// create a dummy flagged order if the line is malformed
	public Order(String orderId) {
		this.orderId = orderId;
		this.flag();
	}
	
	// create a copy of otherOrder with state new for new orders
	public Order(Order otherOrder) {
		this.setTimeStamp(otherOrder.getTimeStamp());
		this.orderId = otherOrder.getOrderId();
		this.customerId = otherOrder.getCustomerId();
		this.setNewState("start");
		this.setNewDescription(otherOrder.getOrderDescription());
		this.setQuantity(otherOrder.getQuantity());
		this.setPrice(otherOrder.getPrice());
		this.flagged = false;
	}

	// getters and setters for fields
	public long getTimeStamp() {return this.timeStamp;}
	public void setTimeStamp(long newTimeStamp) {this.timeStamp = newTimeStamp;}
	public String getOrderId() {return this.orderId;}
	public String getCustomerId() {return this.customerId;}
	public String getCurrentState() {return this.currentState;}
	public void setNewState(String newState) {this.currentState = newState;}
	public String getOrderDescription() {return this.orderDescription;}
	public void setNewDescription(String newDescription) {this.orderDescription = newDescription;}
	public int getQuantity() {return this.quantity;}
	public void setQuantity(int newQuantity) {this.quantity = newQuantity;}
	public double getPrice() {return this.price;}
	public void setPrice(double newPrice) {this.price = newPrice;}
	public boolean isFlagged() {return this.flagged;}
	public void flag() {this.flagged = true;}
}
