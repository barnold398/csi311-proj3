package csi311;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class FlexOMS {

	// variable for the current machineSpec and hashmap of orders
	private MachineSpec machineSpec;
	private HashMap<String, Order> orderMap = new HashMap<>();

	// default do-nothing constructor
	public FlexOMS() {}

	// primary method to process which command is being run
	private void run(String command, String secondArg) throws Exception {

		if (command.equals("--state")) {
			processStateMachine(secondArg);
		}else if (command.equals("--order")) {
			processOrderFile(secondArg);
		} else if (command.equals("--report")) {
			printReport(secondArg);
		} else {
			System.out.println("Command not recognized: " + command);
		}
	}
	
	public static boolean isValidId(String id) {
		return Pattern.matches("[0-9]{5}", id);
	}
	
	private void processStateMachine(String machineFileName) throws Exception {

		// parsing the machineSpec from the file
		ParseState parseState = new ParseState();
		String specJson = parseState.processFile(machineFileName);
		machineSpec = parseState.parseJson(specJson);
		
		// if the machinespec is null, the parser wasn't able to find a proper
		// machinespec, throw exception to main with a proper message.
		if (machineSpec == null) {
			throw new Exception("Unable to parse proper MachineSpec from: " + machineFileName);
		} else {
			int id = machineSpec.getTenantId();
			if (FlexOMS.isValidId(String.valueOf(id))) {
				FlexSql sql = new FlexSql();
				sql.saveStateMachine(id, specJson);
			} else {
				System.out.println("Id not valid: " + id);
			}
		}

	}

	private void processOrderFile(String orderFileName) throws FileNotFoundException {
		// filereader for order file
		//Scanner orderFile = new Scanner(new FileReader(orderFileName));
		
		FlexSql sql = new FlexSql();
		sql.saveOrderFile(orderFileName);

	}
	
	// method to print a report of the final state of each order
	private void printReport(String idString) {

		int id;
		if (FlexOMS.isValidId(idString)) {
			try {
				id = Integer.parseInt(idString);
			} catch (NumberFormatException e) {
				System.out.println("Id not valid: " + idString);
				return;
			}
		} else {
			System.out.println("Id not valid: " + idString);
			return;
		}
		
		FlexSql sql = new FlexSql();
		ParseState parseState = new ParseState();
		machineSpec = parseState.parseJson(sql.loadStateMachine(id));
		
		ArrayList<String> orderList = sql.getOrderList(id);
		ParseOrder parseOrder = new ParseOrder();
		
		
		// for loop to parse each order line in the order list
		for (String order : orderList) {
			
			// create parseOrder object and parse each line
			Order nextOrder = parseOrder.parseLine(order);

			// if the order is null, line was so badly formed there was no order id to flag,
			// if not, process the line
			if (nextOrder != null) {

				// if order is not flagged yet, check that the transition is proper
				if (!nextOrder.isFlagged()) {
					checkOrderTransition(nextOrder);

					// else if it is flagged already, print that it is being flagged again
				} else {
					System.out.println("Flagging order " + nextOrder.getOrderId());
					if (orderMap.containsKey(nextOrder.getOrderId())) {
						orderMap.get(nextOrder.getOrderId()).flag();
					} else {
						orderMap.put(nextOrder.getOrderId(), nextOrder);
					}

				}
			}

		} // end while

		int numFlagged = 0;

		HashMap<String, Boolean> transitions = machineSpec.getTransitions();

		// since flagged orders don't necessarily have correct entries, loop through
		// them first to count
		for (Order orderToPrint : orderMap.values()) {

			// test code
			// System.out.println(orderToPrint.getOrderId());
			// System.out.println(orderToPrint.getCurrentState());

			if (orderToPrint.isFlagged()) {
				numFlagged++;
				// System.out.println(orderToPrint.getOrderId());
			}
		}

		System.out.println("flagged " + numFlagged);

		// for each transition, count the number of orders that ended in that state
		for (String transition : transitions.keySet()) {
			int numInTransition = 0;
			double priceInTransition = 0.0;
			// System.out.println(transition);

			// loop through orders to check number in each transition
			for (Order orderToPrint : orderMap.values()) {
				if (!orderToPrint.isFlagged() && orderToPrint.getCurrentState().equals(transition)) {
					numInTransition++;
					priceInTransition += orderToPrint.getPrice();
				}
			}

			// print out each transition, number of orders in that transition
			// price of orders in that transition and add mark for terminal states
			if (!transitions.get(transition))
				System.out
						.println(transition + " " + numInTransition + " " + String.format("%,.2f", priceInTransition));
			else
				System.out.println(transition + " " + numInTransition + " " + String.format("%,.2f", priceInTransition)
						+ " (terminal)");
		}

		// System.out.println("pending: " + numPending + " " + pricePending);
	}

	// method to check if the transition is valid
	public void checkOrderTransition(Order order) {
		if (!orderMap.containsKey(order.getOrderId())) {
			Order startOrder = new Order(order);
			orderMap.put(startOrder.getOrderId(), startOrder);
		}

		Order prevOrder = orderMap.get(order.getOrderId());
		String prevState = prevOrder.getCurrentState();
		String nextState = order.getCurrentState();
		if (!prevOrder.getCustomerId().equals(order.getCustomerId())) {
			System.out.println("Flagging order " + prevOrder.getOrderId());
			prevOrder.flag();
		}

		if (machineSpec.verifyTransition(prevState, nextState)) {
			orderMap.replace(order.getOrderId(), order);
		} else {
			System.out.println("Flagging order " + prevOrder.getOrderId());
			prevOrder.flag();
		}
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Needs 2 arguments, a command to be run and its argument.");
		} else {
			try {
				FlexOMS flexOMS = new FlexOMS();
				flexOMS.run(args[0], args[1]);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

}
