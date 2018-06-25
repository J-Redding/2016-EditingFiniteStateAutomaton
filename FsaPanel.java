import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;
import java.util.*;
import java.beans.*;

public class FsaPanel extends JPanel implements FsaListener, MouseListener, MouseMotionListener {
    private FsaImpl fsa;
    private Set<State> states;
    private Set<StateIcon> icons;
    //State 0 => Idle
    //State 1 => Selecting
    //State 2 => Dragging
    private int state;
    private int x0;
    private int y0;
    private int x1;
    private int y1;

	public FsaPanel(FsaImpl fsaIn) {
        this.fsa = fsaIn;
        this.states = new HashSet<State>();
        this.icons = new HashSet<StateIcon>();
        this.state = 0;
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.x0 = 0;
        this.y0 = 0;
        this.x1 = 0;
        this.y1 = 0;
		this.setLayout(null);
	}

	//Called whenever the number of states in the FSA has changed
    public void statesChanged() {
        StateImpl[] fsaStates = this.fsa.getStates().toArray(new StateImpl[this.fsa.getStates().size()]);
        if (this.states.size() == 0) {
            StateIcon icon = new StateIcon(fsaStates[0]);
            fsaStates[0].addListener(icon);
            this.add(icon);
            icon.addMouseListener(this);
            icon.addMouseMotionListener(this);
            icon.stateHasChanged();
            this.states.add(fsaStates[0]);
            this.icons.add(icon);
        }

        else {
    	    State[] stateArray = this.states.toArray(new State[this.states.size()]);
            for (int i = 0; i < fsaStates.length;) {
                for (int j = 0; j < stateArray.length; j++) {
                    if (fsaStates[i] == stateArray[j]) {
                        i++;
                        if (i >= fsaStates.length - 1) {
                            j = stateArray.length;
                        }

                        else {
                            j = 0;
                        }
                    }

                    else if (j == stateArray.length - 1) {
                        StateIcon icon = new StateIcon(fsaStates[i]);
                        fsaStates[i].addListener(icon);
                        this.add(icon);
                        icon.addMouseListener(this);
                        icon.addMouseMotionListener(this);
                        icon.stateHasChanged();
                        this.states.add(fsaStates[i]);
                        this.icons.add(icon);
                        i++;
                    }
                }
            }
        }
    }

    //Called whenever the number of transitions in the FSA has changed
    public void transitionsChanged() {
    	System.out.println("Transitions changed.");
    }

    //Called whenever something about the FSA has changed
    //(other than states or transitions)
    public void otherChanged() {
    	System.out.println("Something changed.");
    }

    public void updateFsa(FsaImpl fsaIn) {
        this.fsa = fsaIn;
    }

    public Set<StateIcon> getIcons() {
        return this.icons;
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
        if (this.state == 0) {
            if (e.getComponent().getClass().getName().equals("FsaPanel")) {
                Iterator it = this.icons.iterator();
                while (it.hasNext()) {
                    StateIcon icon = (StateIcon)it.next();
                    if (icon.checkSelected()) {
                        icon.changeSelection();
                    }
                }

                this.x0 = e.getX();
                this.y0 = e.getY();
                this.state = 1;
            }

            else if (e.getComponent().getClass().getName().equals("StateIcon")) {
                this.state = 2;
                if (!((StateIcon)e.getComponent()).checkSelected()) {
                    Iterator it = this.icons.iterator();
                    while (it.hasNext()) {
                        StateIcon icon = (StateIcon)it.next();
                        if (icon.checkSelected()) {
                            icon.changeSelection();
                        }
                    }

                    ((StateIcon)e.getComponent()).changeSelection();
                }
            }
        }
    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {
        if (this.state == 1) {
            this.state = 0;
            this.repaint(0, 0, 600, 600);
        }

        else if (this.state == 2) {
            this.state = 0;
        }
    }

    public void mouseMoved(MouseEvent e) {

    }

    public void mouseDragged(MouseEvent e) {
        if (this.state == 1) {
            this.x1 = e.getX();
            this.y1 = e.getY();
            Rectangle rect;
            if (x1 - x0 < 0) {
                if (y1 - y0 < 0) {
                  rect = new Rectangle(this.x1, this.y1, Math.abs(this.x1 - this.x0), Math.abs(this.y1 - this.y0));
                }

                else {
                    rect = new Rectangle(this.x1, this.y0, Math.abs(this.x1 - this.x0), this.y1 - this.y0);
                }
            }

            else {
                if (y1 - y0 < 0) {
                  rect = new Rectangle(this.x0, this.y1, this.x1 - this.x0, Math.abs(this.y1 - this.y0));
                }

                else {
                    rect = new Rectangle(this.x0, this.y0, this.x1 - this.x0, this.y1 - this.y0);
                }
            }

            Component[] components = this.getComponents();
            for (int i = 0; i < components.length; i++) {
                if (rect.intersects(components[i].getBounds())) {
                    if (!((StateIcon)components[i]).checkSelected()) {
                        ((StateIcon)components[i]).changeSelection();
                    }
                }

                else {
                    if (((StateIcon)components[i]).checkSelected()) {
                        ((StateIcon)components[i]).changeSelection();
                    }
                }
            }

            this.repaint(0, 0, 600, 600);
        }

        else if (this.state == 2) {
            this.x1 = e.getX();
            this.y1 = e.getY();
            Iterator it = this.icons.iterator();
            while (it.hasNext()) {
                StateIcon icon = (StateIcon)it.next();
                if (icon.checkSelected()) {
                    icon.moveBy(this.x1 - this.x0, this.y1 - this.y0);
                }
            }
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(600, 570);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.state == 1) {
            g.setColor(Color.BLUE);
            g.fillRect(this.x0, this.y0, this.x1 - this.x0, this.y1 - this.y0);
        }
    }
}