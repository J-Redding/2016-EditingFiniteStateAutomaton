import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;
import java.util.*;
import java.beans.*;

public class StateIcon extends JComponent implements StateListener {
	private StateImpl state;
	private boolean isSelected;

	public StateIcon(StateImpl stateIn) {
		this.state = stateIn;
		this.isSelected = false;
	}

	//Called whenever the observable properties of a state have changed
    public void stateHasChanged() {
    	this.setBounds(this.state.getXpos(), this.state.getYpos(), 140, 190);
    	this.repaint(0, 0, 600, 600);
    }

    public void updateState(StateImpl stateIn) {
    	this.state = stateIn;
    	this.stateHasChanged();
    }

    public void changeSelection() {
    	this.isSelected = !this.isSelected;
    	this.stateHasChanged();
    }

    public boolean checkSelected() {
    	return this.isSelected;
    }

    public void setInitial(boolean b) {
    	this.state.setInitial(b);
    }

    public void setFinal(boolean b) {
    	this.state.setFinal(b);
    }

    public void moveBy(int dx, int dy) {
    	this.state.moveBy(dx, dy);
    }

    public Dimension getPreferredSize() {
        return new Dimension(140, 200);
    }

    protected void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	g.setColor(Color.BLACK);
    	g.drawString(this.state.getName(), 75, 105);
    	if (this.isSelected) {
    		g.fillOval(15, 45, 120, 120);
    	}

    	else {
    		g.drawOval(15, 45, 120, 120);
    	}

    	if (this.state.isInitial()) {
    		g.drawLine(0, 0, 25, 35);
    		g.drawLine(25, 35, 25, 10);
    		g.drawLine(25, 10, 50, 50);
    	}

    	if (this.state.isFinal()) {
    		g.setColor(Color.BLACK);
    		g.drawOval(10, 40, 129, 130);
    	}
    }
}