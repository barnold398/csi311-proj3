package csi311;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.derby.jdbc.EmbeddedDriver;


public class FlexSql {

	private static final String DB_URL = "jdbc:derby:csi311-testdb1;create=true";
    private Connection conn = null;
    private Statement stmt = null;
	
	public FlexSql() {}
    
    private void createConnection() {
        try {
            Driver derbyEmbeddedDriver = new EmbeddedDriver();
            DriverManager.registerDriver(derbyEmbeddedDriver);
            conn = DriverManager.getConnection(DB_URL);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

	public void saveStateMachine(int id, String jsonSpec) throws Exception {
		createConnection();
		createStateMachineTable();
		insertStateMachine(id, jsonSpec);
		shutdown();
	}
	
	public String loadStateMachine(int id) {
		createConnection();
		String stateJson = getStateMachine(id);
		shutdown();
		return stateJson;
	}
	
	private String getStateMachine(int tenantId) {
		String json_string = null;
		try {
			stmt = conn.createStatement();
			ResultSet results = stmt.executeQuery("select * from state_machines where id = " + tenantId);
			//ResultSetMetaData rsmd = results.getMetaData();
			
			while(results.next()) {
				json_string = results.getString("json_string");
			}
			//System.out.println(json_string);
			results.close();
			stmt.close();
			
		} catch (SQLException sqlExcept) {
			sqlExcept.printStackTrace();
		}
		//System.out.println(json_string);
		return json_string;
	}
	
	private void insertStateMachine(int id, String json) {
		try {
			
			String statementString;
			if (getStateMachine(id) == null) {
				stmt = conn.createStatement();
				statementString = "insert into state_machines (id,json_string) values (" + id + ",'" + json + "') ";
				//System.out.println(statementString);
				stmt.execute(statementString);
			} else {
				stmt = conn.createStatement();
				statementString = "delete from state_machines where id = " + id;
				//System.out.println(statementString);
				stmt.execute(statementString);
				statementString = "insert into state_machines (id,json_string) values (" + id + ",'" + json + "') ";
				stmt.execute(statementString);
			}
			stmt.close();
			
			// test code
			//System.out.println("Inserting " + id + " into state machine table.");
		} catch (SQLException sqlExcept) {
			sqlExcept.printStackTrace();
		}
	}
	
	public ArrayList<String> getOrderList(int id) {
		ArrayList<String> orderList = new ArrayList<>();
		ResultSet results;
		createConnection();
		
		try {
			stmt = conn.createStatement();
			results = stmt.executeQuery("select * from orders where id = " + id);
			while(results.next()) {
				orderList.add(results.getString("orderLine"));
			}
			stmt.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		shutdown();
		return orderList;
	}

	private void createStateMachineTable() {
		int maxChars = 1000;
		try {
			stmt = conn.createStatement();
			String statementString = 
					"create table state_machines (id INT NOT NULL PRIMARY KEY, json_string varchar(" + maxChars + 
					") not null)";
			//System.out.println(statementString);
			stmt.execute(statementString);
			stmt.close();
		} catch (SQLException sqlExcept) {
			if(!tableAlreadyExists(sqlExcept)) {
				sqlExcept.printStackTrace();
			}
		}
	}
	
	public void saveOrderFile(String orderFileName) throws FileNotFoundException {

		createConnection();
		createOrderTable();

		Scanner orderFile = new Scanner(new FileReader(orderFileName));

		while (orderFile.hasNextLine()) {
			String nextOrderLine = orderFile.nextLine().trim();
			String[] nextOrderSplit = nextOrderLine.split(",");
			int id;
			if (FlexOMS.isValidId(nextOrderSplit[0])) {
				try {
					id = Integer.parseInt(nextOrderSplit[0]);
				} catch (NumberFormatException e) {
					continue;
				}
				insertOrder(id, nextOrderLine);
			} else {
				System.out.println(nextOrderSplit[0] + " is not valid tenant id, skipping.");
			}
		}

		orderFile.close();
		shutdown();
	}
	
	
	private void insertOrder(int id, String order) {
		try {
			stmt = conn.createStatement();
			stmt.execute("insert into orders (id,orderLine) values (" + id + ",'" + order + "')");
			stmt.close();
			
			// test code
			//System.out.println("Inserting " + id + " into state machine table.");
		} catch (SQLException sqlExcept) {
			sqlExcept.printStackTrace();
		}
	}
	
	private void createOrderTable() {
		int maxChars = 1000;
		try {
			stmt = conn.createStatement();
			String statementString = 
					"create table orders (id INT NOT NULL, orderLine varchar(" + maxChars + 
					") not null)";
			//System.out.println(statementString);
			stmt.execute(statementString);
			stmt.close();
		} catch (SQLException sqlExcept) {
			if(!tableAlreadyExists(sqlExcept)) {
				sqlExcept.printStackTrace();
			}
		}
	}
    
    private boolean tableAlreadyExists(SQLException e) {
        boolean exists;
        if(e.getSQLState().equals("X0Y32")) {
            exists = true;
        } else {
            exists = false;
        }
        return exists;
    }
    
    private void shutdown() {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                DriverManager.getConnection(DB_URL + ";shutdown=true");
                conn.close();
            }           
        }
        catch (SQLException sqlExcept) {
        }
    }
}
