import java.util.*;

public class StateImpl implements State {
	private String stateName;
	private int xPos;
	private int yPos;
	private Set<StateListener> listeners;
	private Set<Transition> fromTransitions;
	private Set<Transition> toTransitions;
	private boolean initialState;
	private boolean finalState;
	private boolean currentState;

	public StateImpl(String name, int x, int y) {
		this.stateName = name;
		this.xPos = x;
		this.yPos = y;
        listeners = new HashSet<StateListener>();
        fromTransitions = new HashSet<Transition>();
        toTransitions = new HashSet<Transition>();
		this.initialState = false;
		this.finalState = false;
		this.currentState = false;
	}

	//Add a listener to this state
    public void addListener(StateListener sl) {
    	this.listeners.add(sl);
    }

    //Remove a listener from this state
    public void removeListener(StateListener sl) {
    	this.listeners.remove(sl);
    }

    //Return a set containing all transitions FROM this state
    public Set<Transition> transitionsFrom() {
    	return this.fromTransitions;
    }

    //Return a set containing all transitions TO this state
    public Set<Transition> transitionsTo() {
    	return this.toTransitions;
    }

    //Move the position of this state 
    //by (dx,dy) from its current position
    public void moveBy(int dx, int dy) {
    	this.xPos += dx;
    	this. yPos += dy;
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((StateIcon)it.next()).updateState(this);
        }
    }

    //Return a string containing information about this state 
    //in the form (without the quotes, of course!) :
    //"stateName(xPos,yPos)jk"
    //where j is 1/0 if this state is/is-not an initial state  
    //where k is 1/0 if this state is/is-not a final state  
    public String toString() {
    	String stateString = "";
    	stateString += this.stateName;
    	stateString += "(";
    	stateString += this.xPos;
    	stateString += ",";
    	stateString += this.yPos;
    	stateString += ")";
		if (this.initialState) {
			stateString += "1";
		}

		else {
			stateString += "0";
		}

		if (this.finalState) {
			stateString += "1";
		}

		else {
			stateString += "0";
		}

		return stateString;
    }

    //Return the name of this state 
    public String getName() {
    	return this.stateName;
    }

    //Return the X position of this state
    public int getXpos() {
    	return this.xPos;
    }

    //Return the Y position of this state
    public int getYpos() {
    	return this.yPos;
    }

    //Set/clear this state as an initial state
    public void setInitial(boolean b) {
    	this.initialState = b;
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((StateIcon)it.next()).updateState(this);
        }
    }

    //Indicate if this is an initial state
    public boolean isInitial() {
    	return this.initialState;
    }

    //Set/clear this state as a final state
    public void setFinal(boolean b) {
    	this.finalState = b;
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((StateIcon)it.next()).updateState(this);
        }
    }

    //Indicate if this is a final state
    public boolean isFinal() {
    	return this.finalState;
    }

    //Indicate if this is a current state
    public boolean isCurrent() {
    	return this.currentState;
    }

    public void setCurrent(boolean b) {
        this.currentState = b;
    }

    public void addFromTransition(Transition t) {
        this.fromTransitions.add(t);
    }

    public void addToTransition(Transition t) {
        this.toTransitions.add(t);
    }

    public void removeFromTransition(Transition t) {
        this.fromTransitions.remove(t);
    }

    public void removeToTransition(Transition t) {
        this.toTransitions.remove(t);
    }
}