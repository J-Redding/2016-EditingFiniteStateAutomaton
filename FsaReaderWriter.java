import java.io.*;
import java.util.*;

public class FsaReaderWriter implements FsaIo {
	private Set<String> states; 
    private int lineNr;

	public FsaReaderWriter() {
        //states is a list of all state records that have been added.
		this.states = new HashSet<String>();
        //lineNr keeps track of which line of input the reader is up to.
        this.lineNr = 0;
	}

	//This class handles reading and writing FSA representations as 
    //described in the practical specification

    //Read the description of a finite-state automaton from the 
    //Reader , r, and transfer it to Fsa, f.
    //If an error is detected, throw an exception that indicates the line
    //where the error was detected, and has a suitable text message
    public void read(Reader r, Fsa f) throws IOException, FsaFormatException {
    	BufferedReader br = new BufferedReader(r);
    	String line = br.readLine();
        this.lineNr++;
        //Read the input, line by line.
    	while (line != null) {
            //Remove leading and trailing whitespace
            line = line.trim();
            //If line is empty, skip it.
            if (line.length() > 0) {
                //State record
    	       if (line.charAt(0) == 's') {
    			    this.readState(line, f);
    		    }

                //Transition record
    		    else if (line.charAt(0) == 't') {
    			    this.readTransition(line, f);
    		    }

                //Initial record
    		    else if (line.charAt(0) == 'i') {
    			    this.readInitial(line, f);
                }

                //Final record
                else if (line.charAt(0) == 'f') {
                    this.readFinal(line, f);
                }

                //If none of these, and not a comment, throw an error.
                else if (line.charAt(0) != '#') {
    				throw new FsaFormatException(this.lineNr, "Illegal format");
                }
            }

    		line = br.readLine();
            this.lineNr++;
    	}
    }

    //Write a representation of the Fsa, f, to the Writer, w.
    public void write(Writer w, Fsa f) throws IOException {
    	this.writeStates(w, f);
        this.writeTransitions(w, f);
        this.writeInitials(w, f);
        this.writeFinals(w, f);
    }

    //Read a state record.
    public void readState(String line, Fsa f) throws FsaFormatException {
        StringTokenizer st = new StringTokenizer(line);
        //A state record must have 4 tokens to be valid.
        if (st.countTokens() != 4) {
            throw new FsaFormatException(this.lineNr, "Illegal format");
        }

        //Tokenise the line.
        String token = st.nextToken();
        //Check that the "state" token is valid.
    	if (token.charAt(1) != 't') {
    		throw new FsaFormatException(this.lineNr, "Illegal format");
    	}

    	if (token.charAt(2) != 'a') {
    		throw new FsaFormatException(this.lineNr, "Illegal format");
    	}

    	if (token.charAt(3) != 't') {
    		throw new FsaFormatException(this.lineNr, "Illegal format");
    	}

    	if (token.charAt(4) != 'e') {
    		throw new FsaFormatException(this.lineNr, "Illegal format");
    	}

        //Get the stateName, xPos, and yPos tokens.
        //Parse the xPos and yPos tokens as integers.
    	String stateName = st.nextToken();
    	String xString = st.nextToken();
        //Check that the xString token is a valid digit.
        //Check that all characters are digits.
        for (int i = 0; i < xString.length(); i++) {
            //If the character is not a digit, it may be a '-' character, as long as it is the first character.
            //This denotes a negative digit.
            if (!Character.isDigit(xString.charAt(i))) {
                if (i == 0) {
                    if (xString.charAt(i) != '-') {
                        throw new FsaFormatException(this.lineNr, "Illegal format");
                    }
                }

                else {
                    throw new FsaFormatException(this.lineNr, "Illegal format");
                }
            }
        }

    	int xPos = Integer.parseInt(xString);
    	String yString = st.nextToken();
        //Check that the yString token is a valid digit.
        for (int i = 0; i < yString.length(); i++) {
            //If the character is not a digit, it may be a '-' character, as long as it is the first character.
            //This denotes a negative digit.
            if (!Character.isDigit(yString.charAt(i))) {
                if (i == 0) {
                    if (yString.charAt(i) != '-') {
                        throw new FsaFormatException(this.lineNr, "Illegal format");
                    }
                }

                else {
                    throw new FsaFormatException(this.lineNr, "Illegal format");
                }
            }
        }

    	int yPos = Integer.parseInt(yString);
        //Add the state to the Fsa, and add it to the set of state records added.
    	f.newState(stateName, xPos, yPos);
        this.states.add(stateName);
    }

    //Read a transition record.
    public void readTransition(String line, Fsa f) throws FsaFormatException {
        StringTokenizer st = new StringTokenizer(line);
        //A transition record must have 4 tokens.
        if (st.countTokens() != 4) {
            throw new FsaFormatException(this.lineNr, "Illegal format");
        }

        String token = st.nextToken();
        //Check the "transition" token.
    	if (token.charAt(1) != 'r') {
    		throw new FsaFormatException(this.lineNr, "Illegal format");
    	}

    	if (token.charAt(2) != 'a') {
    		throw new FsaFormatException(this.lineNr, "Illegal format");
    	}

    	if (token.charAt(3) != 'n') {
    		throw new FsaFormatException(this.lineNr, "Illegal format");
    	}

    	if (token.charAt(4) != 's') {
    		throw new FsaFormatException(this.lineNr, "Illegal format");
    	}

    	if (token.charAt(5) != 'i') {
    		throw new FsaFormatException(this.lineNr, "Illegal format");
    	}

    	if (token.charAt(6) != 't') {
    		throw new FsaFormatException(this.lineNr, "Illegal format");
    	}

    	if (token.charAt(7) != 'i') {
    		throw new FsaFormatException(this.lineNr, "Illegal format");
    	}

    	if (token.charAt(8) != 'o') {
    		throw new FsaFormatException(this.lineNr, "Illegal format");
    	}

    	if (token.charAt(9) != 'n') {
    		throw new FsaFormatException(this.lineNr, "Illegal format");
    	}

    	String fromString = st.nextToken();
    	String event = st.nextToken();
        //If the event token is "?" it represents an epislon transition.
        //Fsa interprets epsilon transitions as null.
        if (event.equals("?")) {
            event = null;
        }

    	String toString = st.nextToken();
        //Check that both the fromState and the toState have been defined via record.
    	boolean fromFound = false;
    	boolean toFound = false;
        boolean bothFound = false;
        Iterator it = this.states.iterator();
        while (it.hasNext() && bothFound == false) {
            String nextState = (String)it.next();
            if (nextState.equals(fromString)) {
                fromFound = true;
                if (toFound == true) {
                    bothFound = true;
                }
            }

            else if (nextState.equals(toString)) {
                toFound = true;
                if (fromFound == true) {
                    bothFound = true;
                }
            }
        }

        //If they have both been defined, the transition can be added.
    	if (bothFound == true) {
    		State fromState = f.findState(fromString);
    		State toState = f.findState(toString);
    		f.newTransition(fromState, toState, event);
    	}

        else {
            throw new FsaFormatException(this.lineNr, "Illegal format");
        }
    }

    //Read an intial record.
    public void readInitial(String line, Fsa f) throws FsaFormatException {
        StringTokenizer st = new StringTokenizer(line);
        //An initial record must have 2 tokens.
        if (st.countTokens() != 2) {
            throw new FsaFormatException(this.lineNr, "Illegal format");
        }

        String token = st.nextToken();
        //Check the "initial" token.
    	if (token.charAt(1) != 'n') {
    		throw new FsaFormatException(this.lineNr, "Illegal format");
    	}

    	if (token.charAt(2) != 'i') {
    		throw new FsaFormatException(this.lineNr, "Illegal format");
    	}

    	if (token.charAt(3) != 't') {
    		throw new FsaFormatException(this.lineNr, "Illegal format");
    	}

    	if (token.charAt(4) != 'i') {
    		throw new FsaFormatException(this.lineNr, "Illegal format");
    	}

    	if (token.charAt(5) != 'a') {
    		throw new FsaFormatException(this.lineNr, "Illegal format");
    	}

    	if (token.charAt(6) != 'l') {
    		throw new FsaFormatException(this.lineNr, "Illegal format");
    	}

    	String stateName = st.nextToken();
        //Check that the state has been defined via a record.
    	if (this.findStateRecord(stateName)) {
    		f.findState(stateName).setInitial(true);
    	}

        else {
            throw new FsaFormatException(this.lineNr, "Illegal format");
        }
    }

    //Read a final record.
    public void readFinal(String line, Fsa f) throws FsaFormatException {
    	StringTokenizer st = new StringTokenizer(line);
        //A final record must have 2 tokens.
        if (st.countTokens() != 2) {
            throw new FsaFormatException(this.lineNr, "Illegal format");
        }

        String token = st.nextToken();
        //Check the "final" token.
    	if (token.charAt(1) != 'i') {
    		throw new FsaFormatException(this.lineNr, "Illegal format");
    	}

    	if (token.charAt(2) != 'n') {
    		throw new FsaFormatException(this.lineNr, "Illegal format");
    	}

    	if (token.charAt(3) != 'a') {
    		throw new FsaFormatException(this.lineNr, "Illegal format");
    	}

    	if (token.charAt(4) != 'l') {
    		throw new FsaFormatException(this.lineNr, "Illegal format");
    	}

    	String stateName = st.nextToken();
        //Check that the state has been defined via a record.
    	if (this.findStateRecord(stateName)) {
    		f.findState(stateName).setFinal(true);
    	}

        else {
            throw new FsaFormatException(this.lineNr, "Illegal format");
        }
    }

    //Write state records for states in the Fsa.
    public void writeStates(Writer w, Fsa f) throws IOException {
        //Get all states from the Fsa.
        Iterator it = f.getStates().iterator();
        while (it.hasNext()) {
            State nextState = (State)it.next();
            //Convert the state to a record.
            String record = "state ";
            record += nextState.getName();
            record += " ";
            record += nextState.getXpos();
            record += " ";
            record += nextState.getYpos();
            record += "\n";
            //Write the record to the writer.
            w.write(record);
        }
    }

    //Write transition records for transitions in the Fsa.
    public void writeTransitions(Writer w, Fsa f) throws IOException {
        //Transitions are a bit complicated.
        //There is not a simple way to get all transitions of an Fsa.
        //Instead, check every combination of states, and check for a set of transitions connecting them.
        Iterator firstStates = f.getStates().iterator();
        while (firstStates.hasNext()) {
            State firstState = (State)firstStates.next();
            Iterator secondStates = f.getStates().iterator();
            while (secondStates.hasNext()) {
                State secondState = (State)secondStates.next();
                //TransitionIt represents the set of transitions between firstState and lastState.
                Iterator transitionIt = f.findTransition(firstState, secondState).iterator();
                while (transitionIt.hasNext()) {
                    Transition nextTransition = (Transition)transitionIt.next();
                    //Convert the transition to a record.
                    String record = "transition ";
                    record += nextTransition.fromState().getName();
                    record += " ";
                    //If the eventName token is null it represents an epislon transition.
                    //Records interpret epsilon transitions as "?".
                    if (nextTransition.eventName() == null) {
                        record += "?";
                    }

                    //If the eventName token is "" it represents an epislon transition.
                    //Records interpret epsilon transitions as "?".
                    else if (nextTransition.eventName().equals("")) {
                        record += "?";
                    }

                    else {
                        record += nextTransition.eventName();
                    }

                    record += " ";
                    record += nextTransition.toState().getName();
                    record += "\n";
                    w.write(record);
                }
            }
        }
    }

    //Write initial records for initial states in the Fsa.
    public void writeInitials(Writer w, Fsa f) throws IOException {
        //Get the initial states from the Fsa.
        Iterator it = f.getInitialStates().iterator();
        while (it.hasNext()) {
            State nextState = (State)it.next();
            //Convert the initial state to a record.
            String record = "initial ";
            record += nextState.getName();
            record += "\n";
            w.write(record);
        }
    }

    //Write final records for final states in the Fsa.
    public void writeFinals(Writer w, Fsa f) throws IOException {
        //Get the final states from the Fsa.
        Iterator it = f.getFinalStates().iterator();
        while (it.hasNext()) {
            State nextState = (State)it.next();
            //Convert the final state to a record.
            String record = "final ";
            record += nextState.getName();
            record += "\n";
            w.write(record);
        }
    }

    //Check if a state record has been read by the reader.
    public boolean findStateRecord(String stateName) {
        //Get the set of read state records.
        Iterator it = this.states.iterator();
        while (it.hasNext()) {
            //If the state record had the same name as stateName, the state record has been read.
            if (((String)it.next()).equals(stateName)) {
                return true;
            }
        }

    	return false;
    }
}