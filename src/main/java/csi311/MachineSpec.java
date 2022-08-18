package csi311;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;


@SuppressWarnings("serial")
public class MachineSpec implements Serializable {

	// Since defined as an inner class, must be declared static or Jackson can't deal.
	public static class StateTransitions implements Serializable {
		private String state; 
		private List<String> transitions;
		public StateTransitions() { }
		public String getState() { return state; }
		public void setState(String state) { this.state = state.toLowerCase(); } 
		public List<String> getTransitions() { return transitions; } 
		public void setTransitions(List<String> transitions) { 
			this.transitions = transitions;
			if (this.transitions != null) {
				for (int i = 0; i < transitions.size(); i++) {
					transitions.set(i, transitions.get(i).toLowerCase()); 
				}
			}
		} 
	}
	
	private List<StateTransitions> machineSpec;
	
	public MachineSpec() { }
	private Integer tenantId;
	public void setTenantId(Integer tenantId) {this.tenantId = tenantId;}
	public Integer getTenantId() {return this.tenantId;}
	public List<StateTransitions> getMachineSpec() { return machineSpec; } 
	public void setMachineSpec(List<StateTransitions> machineSpec) { this.machineSpec = machineSpec; }
	
	
	// method to verify if a transition is proper according the Machine Spec
	public boolean verifyTransition(String previous_state, String next_state) {
		boolean result = false;
    	for (StateTransitions st : getMachineSpec()) {
    		//System.out.println(st.getState() + " : " + st.getTransitions());
    		if (st.getState().equals(previous_state)) {
    			if (st.getTransitions().contains(next_state))
    				result = true;
    		}
    	}
		return result;
	}
	
	public HashMap<String, Boolean> getTransitions() {

		HashMap<String, Boolean> transitions = new HashMap<>();
		for (StateTransitions st : getMachineSpec()) {
			
			// test code
			//System.out.println(st.getState());
			
			transitions.put(st.getState(), false);
		}
		for (StateTransitions st : getMachineSpec()) {
			for (String transition : st.getTransitions()) {
				if (!transitions.containsKey(transition)) {
					transitions.put(transition, true);
				}
			}
		}

		return transitions;
	}
}


