package csi311;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
 

public class ParseState {

	public ParseState() {}
	
	// primary method to parse the machine spec file from json into a MachineSpec object
	// throws FileNotFoundException if the machine spec file is not found
	/*public MachineSpec run(String specJson) throws FileNotFoundException {
    	//System.out.println("Parse State");
    	MachineSpec machineSpec;
    	//String json = processFile(specJson); 
    	//System.out.println("Raw json = " + json);
    	machineSpec = parseJson(specJson);
    	//dumpMachine(machineSpec); 
    	
    	return machineSpec;
    }*/
    
    // test method to dump the machine spec
    /*
    private void dumpMachine(MachineSpec machineSpec) {
    	if (machineSpec == null) {
    		return;
    	}
    	for (StateTransitions st : machineSpec.getMachineSpec()) {
    		System.out.println(st.getState() + " : " + st.getTransitions());
    	}
    }*/
    
    // process the machine spec file into a string
    public String processFile(String filename) throws FileNotFoundException {
    	//System.out.println("Processing file: " + filename); 
    	Scanner inFile = new Scanner(new FileReader(filename));  
    	String json = "";
    	String line; 
    	while (inFile.hasNextLine()) {
    		line = inFile.nextLine();
    		json += " " + line; 
    	} 
    	inFile.close();
    	// Get rid of special characters - newlines, tabs.  
    	return json.replaceAll("\n", " ").replaceAll("\t", " ").replaceAll("\r", " "); 
    }

    // method to parse the json string into an object with Jackson
    public MachineSpec parseJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try { 
        	MachineSpec machineSpec = mapper.readValue(json, MachineSpec.class);
        	return machineSpec; 
        }
        catch (Exception e) {
            System.out.println(e.getMessage()); 
        }
        return null;
    }

}
