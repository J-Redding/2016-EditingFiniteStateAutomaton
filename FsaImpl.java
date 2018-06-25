import java.util.*;

public class FsaImpl implements Fsa, FsaSim {
	private Set<State> states;
    private Set<FsaListener> fsaListeners;
    private Set<String> inputEvents;
    private Set<Transition> transitions;

	public FsaImpl() {
        //Instantiate the Set elements as HashSets.
        this.states = new HashSet<State>();
        this.transitions = new HashSet<Transition>();
        this.fsaListeners = new HashSet<FsaListener>();
        this.inputEvents = new HashSet<String>();
	}

	//Create a new State and add it to this FSA
    //Returns the new state
    //Throws IllegalArgumentException if:
    //the name is not valid or is the same as that
    //of an existing state
    public State newState(String name, int x, int y) throws IllegalArgumentException {
        //Check whether the passed name value is valid.
        if (name == null) {
            throw new IllegalArgumentException("Name not valid");
        }

        if (name.length() == 0) {
            throw new IllegalArgumentException("Name not valid");
        }

    	if (!Character.isLetter(name.charAt(0))) { 
    		throw new IllegalArgumentException("Name not valid");
    	}

        //Check that every character of the string (after the first character) is a letter, digit, or underscore.
    	for (int i = 1; i < name.length(); i++) {
    		if (!Character.isLetterOrDigit(name.charAt(i))) {
    			if (name.charAt(i) != '_') {
    				throw new IllegalArgumentException("Name not valid");
    			}
    		}
    	}

        //Check that the name is not the same as that of an existing state.
        if (this.findState(name) != null) {
            throw new IllegalArgumentException("State already exists");
        }

        //The state is valid, and can be added.
    	State addState = new StateImpl(name, x, y);
    	this.states.add(addState);
        Iterator it = this.fsaListeners.iterator();
        while (it.hasNext()) {
            FsaPanel nextListener = (FsaPanel)it.next();
            nextListener.updateFsa(this);
            nextListener.statesChanged();
        }

        return addState;
  }

  	//Remove a state from the FSA
    //If the state does not exist, returns without error
    public void removeState(State s) {
        //Removing a state will break all transitions involving that state.
        //The associated transitions must then be removed as well.
        //Remove all transitions from this state.
        Iterator it = ((StateImpl)s).transitionsFrom().iterator();
        while (it.hasNext()) {
            Transition removeTransition = (Transition)it.next();
            this.removeTransition(removeTransition);
        }

        //Remove all transitions to this state.
        it = ((StateImpl)s).transitionsTo().iterator();
        while (it.hasNext()) {
            Transition removeTransition = (Transition)it.next();
            this.removeTransition(removeTransition);
        }

        //Iterator listenerIt = this.fsaListeners.iterator();
        //while (listenerIt.hasNext()) {
        //    ((FsaPanel)listenerIt.next()).statesChanged();
        //}

        this.states.remove(s);
    }

    //Find and return the State with the given name
    //If no state exists with given name, return NULL
    public State findState(String stateName) {
        //Iterate over the states.
        Iterator it = this.states.iterator();
        while (it.hasNext()) {
            State nextState = (State)it.next();
            //If the state whose name matches stateName is found.
            if (nextState.getName().equals(stateName)) {
                return nextState;
            }
        }

        return null;
    }

    //Return a set containing all the states in this Fsa
    public Set<State> getStates() {
        return this.states;
    }

    //Create a new Transition and add it to this FSA
    //Returns the new transition.
    //eventName==null specifies an epsilon-transition
    //Throws IllegalArgumentException if:
    //  The fromState or toState does not exist or
    //  The eventName is invalid or
    //  An identical transition already exists
    public Transition newTransition(State fromState, State toState, String eventName) throws IllegalArgumentException{
        //Check that the associated states exist.
        if (!this.states.contains(fromState)) {
            throw new IllegalArgumentException("Illegal argument");
        }
        
        if (!this.states.contains(toState)) {
            throw new IllegalArgumentException("Illegal argument");
        }

        Transition addTransition;

        //If the transition is an epislon transition, the event is empty.
        if (eventName == null) {
            addTransition = new TransitionImpl(fromState, "", toState);
        }

        //Otherwise, check that the event is valid.
        else {
            for (int i = 0; i < eventName.length(); i++) {
                if (!Character.isLetter(eventName.charAt(i))) {
                    throw new IllegalArgumentException("Event name not valid");
                }
            }

            addTransition = new TransitionImpl(fromState, eventName, toState);
        }

        //Check that an identical transition doesn't already exits.
        if (this.transitions.contains(addTransition)) {
            throw new IllegalArgumentException("Illegal argument");
        }

        //The transition is valid, and can be added.
        this.transitions.add(addTransition);
        //Connect the transition to the associated states.
        ((StateImpl)(this.findState(fromState.getName()))).addFromTransition(addTransition);
        ((StateImpl)(this.findState(toState.getName()))).addToTransition(addTransition);
        Iterator it = this.fsaListeners.iterator();
        while (it.hasNext()) {
            FsaPanel nextListener = (FsaPanel)it.next();
            nextListener.updateFsa(this);
            nextListener.transitionsChanged();
        }

        return addTransition;
    } 
    
    //Remove a transition from the FSA
    //If the transition does not exist, returns without error
    public void removeTransition(Transition t) {
        //Remove the tranition from the associated states.
        ((StateImpl)(this.findState(t.fromState().getName()))).removeFromTransition(t);
        ((StateImpl)(this.findState(t.toState().getName()))).removeToTransition(t);
        this.transitions.remove(t);
        //Iterator it = this.fsaListeners.iterator();
        //while (it.hasNext()) {
        //    ((FsaPanel)it.next()).transitionsChanged();
        //}
    }

    //Find all the transitions between two states
    //Throws IllegalArgumentException if:
    //  The fromState or toState does not exist
    public Set<Transition> findTransition(State fromState, State toState) throws IllegalArgumentException {
        //Check that the associated states exist.
        if (!this.states.contains(fromState)) {
            throw new IllegalArgumentException("Illegal argument");
        }

        if (!this.states.contains(toState)) {
            throw new IllegalArgumentException("Illegal argument");
        }

        Set<Transition> foundTransitions = new HashSet<Transition>();
        Iterator it = this.transitions.iterator();
        while (it.hasNext()) {
            Transition nextTransition = (Transition)it.next();
            //If the transition has the associated states of fromState and toState.
            if (nextTransition.fromState().equals(fromState)) {
                if (nextTransition.toState().equals(toState)) {
                    //Add the transition to a set of found transitions.
                    foundTransitions.add(nextTransition);
                }
            }
        }

        return foundTransitions;
    }

    //Return the set of initial states of this Fsa
    public Set<State> getInitialStates() {
        Set<State> initialStates = new HashSet<State>();
        Iterator it = this.states.iterator();
        while (it.hasNext()) {
            State nextState = (State)it.next();
            //If the state is initial.
            if (nextState.isInitial()) {
                //Add the state to a set of initial states.
                initialStates.add(nextState);
            }
        }

        return initialStates;
    }

    //Return the set of final states of this Fsa
    public Set<State> getFinalStates() {
        Set<State> finalStates = new HashSet<State>();
        Iterator it = this.states.iterator();
        while (it.hasNext()) {
            State nextState = (State)it.next();
            //If the state is final.
            if (nextState.isFinal()) {
                //Add to a set of final states.
                finalStates.add(nextState);
            }
        }

        return finalStates;
    }

    //Returns a set containing all the current states of this FSA
    public Set<State> getCurrentStates() {
        Set<State> currentStates = new HashSet<State>();

        Iterator it = this.states.iterator();
        while (it.hasNext()) {
            State nextState = (State)it.next();
            //If the state is current.
            if (nextState.isCurrent()) {
                //Add toi a set of current states.
                currentStates.add(nextState);
            }
        }

        return currentStates;
    }

    //Return a string describing this Fsa
    //Returns a string that contains (in this order):
    //for each state in the FSA, a line (terminated by \n) containing
    //  STATE followed the toString result for that state
    //for each transition in the FSA, a line (terminated by \n) containing
    //  TRANSITION followed the toString result for that transition
    //for each initial state in the FSA, a line (terminated by \n) containing
    //  INITIAL followed the name of the state
    //for each final state in the FSA, a line (terminated by \n) containing
    //  FINAL followed the name of the state
    public String toString() {
        String fsaString = "";
        //toString the states.
        Iterator it = this.states.iterator();
        while (it.hasNext()) {
            fsaString += "STATE ";
            fsaString += ((State)it.next()).toString();
            //Terminate the line
            fsaString += "\n";
        }

        //toString the transitions.
        it = this.transitions.iterator();
        while (it.hasNext()) {
            fsaString += "TRANSITION ";
            fsaString += ((Transition)it.next()).toString();
            fsaString += "\n";
        }

        //toString the initial states.
        it = this.getInitialStates().iterator();
        while (it.hasNext()) {
            fsaString += "INITIAL ";
            fsaString += ((State)it.next()).getName();
            fsaString += "\n";
        }

        //toString the final states.
        it = this.getFinalStates().iterator();
        while (it.hasNext()) {
            fsaString += "FINAL ";
            fsaString += ((State)it.next()).getName();
            fsaString += "\n";
        }

        return fsaString;
    }

    //Add a listener to this FSA
    public void addListener(FsaListener fl) {
        this.fsaListeners.add(fl);
    }

    //Remove a listener from this FSA
    public void removeListener(FsaListener fl) {
        this.fsaListeners.remove(fl);
    }

    //Reset the simulation to its initial state(s)
    public void reset() {
        //To reset the simulator, reset what states the simulator is currently in.
        //All current states should only be the initial states.
        Iterator it = this.getCurrentStates().iterator();
        while (it.hasNext()) {
            //Set all current states to false (no current states).
            //(Don't bother checking if they are initial, as the initial states will be iterated over anyway)
            ((StateImpl)it.next()).setCurrent(false);
        }

        it = this.getInitialStates().iterator();
        while (it.hasNext()) {
            //Set all of the initial states to current.
            ((StateImpl)it.next()).setCurrent(true);
        }
    }

    //Take one step in the simulation
    public void step(String event) {
        //To take a step in the simulator, the current states are updated.
        //Transitions are taken from the current states.
        //If the input event matches the eventName of transitions from that state, that transition is taken.
        //The toState of that transition is now a current state.

        //Epsilon event
        if (event == null) {
            event = "";
        }

        this.inputEvents.add(event);
        Iterator currentStates = this.getCurrentStates().iterator();
        while (currentStates.hasNext()) {
            State nextState = (State)currentStates.next();
            //Set the current state to false, since the transition will either move from this state, or back to this state.
            ((StateImpl)nextState).setCurrent(false);
            Iterator fromTransitions = nextState.transitionsFrom().iterator();
            //Check all of the transitions from the current states.
            while (fromTransitions.hasNext()) {
                Transition nextTransition = (Transition)fromTransitions.next();
                //If the event that causes the transition is entered.
                if ((nextTransition).eventName().equals(event)) {
                    //Make the transition.
                    //Set the toState to current.
                    ((StateImpl)nextTransition.toState()).setCurrent(true);
                }
            }
        }
    }

    //Returns true if the simulation has recognised
    //the sequence of events it has been given
    public boolean isRecognised() {
        //The simulation has recognised the sequence of events if all of the input events are events used by transitions.
        //Checks all of the input events, and compares them against transition eventNames.
        Iterator inputEvents = this.inputEvents.iterator();
        Iterator transitionIt = this.transitions.iterator();
        //Iterate over input events.
        while (inputEvents.hasNext()) {
            String nextInput = (String)inputEvents.next();
            //Iterate over each transition.
            while (transitionIt.hasNext()) {
                //If the transition eventName matches the input event.
                if (((Transition)transitionIt.next()).eventName().equals(nextInput)) {
                    //Move on to the next input.
                    nextInput = (String)inputEvents.next();
                    //Reset the transition iterator from the beginning.
                    //(In case later inputs match previous transition eventNames)
                    transitionIt = this.transitions.iterator();
                }

                //If the input cannot be matched, and all transitions have been checked.
                else if (!transitionIt.hasNext()) {
                    //The sequence of events has not been recognised.
                    return false;
                }
            }
        }

        //The sequence of events has been recognised.
        return true;
    }
}