import java.util.*;

public class TransitionImpl implements Transition {
	private Set<TransitionListener> listeners;
    private State fromState;
    private State toState;
    private String eventName;

	public TransitionImpl(State from, String event, State to) {
        this.fromState = from;
        this.eventName = event;
        this.toState = to;
        listeners = new HashSet<TransitionListener>();
	}

	//Add a listener to this Transition
    public void addListener(TransitionListener tl) {
    	this.listeners.add(tl);
    }

    //Remove a listener tfrom this Transition
    public void removeListener(TransitionListener tl) {
    	this.listeners.remove(tl);
    }

    //Return the from-state of this transition
    public State fromState() {
        return this.fromState;
    }

    //Return the to-state of this transition
    public State toState() {
        return this.toState;
    }

    //Return the name of the event that causes this transition
    public String eventName() {
        return this.eventName;
    }

    //Return a string containing information about this transition 
    //in the form (without quotes, of course!):
    //"fromStateName(eventName)toStateName"
    public String toString() {
        String transitionString = "";
        transitionString += this.fromState.getName();
        transitionString += "(";
        transitionString += this.eventName;        
        transitionString += ")";
        transitionString += this.toState.getName();
        return transitionString;
    }
}