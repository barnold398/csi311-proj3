package csi311;

import java.util.regex.Pattern;

public class ParseOrder {
	private final String ORDERIDREGEX = "[0-9]{3}-[a-zA-Z]{3}-[0-9]{4}";
	private final String CUSTIDREGEX = "[0-9]{9}";
	public ParseOrder() {}
	
	public Order parseLine(String orderLine) {
		
		long timeStamp;
		String orderId;
		String customerId;
		String currentState;
		String orderDescription;
		int quantity;
		double price;
		
		Order order = null;
		
		String[] orderSplit = orderLine.split(",");
		
		// if the order line is less than 3 entries, it doesn't have an order id to check
		// so ignore
		if (orderSplit.length > 3) {

			// if the order line has 7 entries, check line for errors
			if (orderSplit.length == 8) {
				
				for (int i = 0; i < 8; i++) {
					// trim out any extra whitespace
					orderSplit[i] = orderSplit[i].trim();
				}
				
				// try to parse an long for the time stamp, create flagged dummy order if timestamp
				// isn't a number that can be read as a long
				try {timeStamp = Long.parseLong(orderSplit[1]);}
				catch(NumberFormatException e) {
					return order = new Order(orderSplit[2]);
				}
				
				// if the order id doesn't match the proper form, create a dummy flagged version of the order
				if (!Pattern.matches(ORDERIDREGEX, orderSplit[2])) {
					return order = new Order(orderSplit[2]);
				} else {orderId = orderSplit[2];}
				
				// if customer id doesn't match the proper form, create a dummy flagged version of the order
				if (!Pattern.matches(CUSTIDREGEX, orderSplit[3])) {
					return order = new Order(orderSplit[2]);
				} else {customerId = orderSplit[3];}
				
				// order desc shouldn't have any wrong forms
				currentState = orderSplit[4].toLowerCase();
				orderDescription = orderSplit[5];
				
				// try to parse an int for quantity, if wrong create dummy flagged order
				try {quantity = Integer.parseInt(orderSplit[6]);}
				catch(NumberFormatException e) {
					return order = new Order(orderSplit[2]);
				}
				// quantity must be positive
				if (quantity < 0)
					return order = new Order(orderSplit[2]);

				// try to parse a double for price, if wrong create dummy flagged order
				try {price = Double.parseDouble(orderSplit[7]);}
				catch(NumberFormatException e) {
					return order = new Order(orderSplit[2]);
				}
				
				// create the order if it passed all the tests
				order = new Order(timeStamp, orderId, customerId, currentState, 
						orderDescription, quantity, price);
			} else {
				// if the line didn't have exactly 8 entries, it is malformed so create dummy flagged order
				order = new Order(orderSplit[2].trim());
			}
		}
		
		return order;
	}
}
