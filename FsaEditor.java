import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;
import java.util.*;
import java.beans.*;

public class FsaEditor extends JFrame {
	static boolean RIGHT_TO_LEFT = false;
	final private JFileChooser fc;
	private FsaReaderWriter readWrite;
	private FsaImpl fsa;
	private FsaPanel panel;

	public FsaEditor() {
		this.readWrite = new FsaReaderWriter();
		this.fsa = new FsaImpl();
		this.panel = new FsaPanel(this.fsa);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container pane = this.getContentPane();
		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		fc = new JFileChooser();
		try {
			File f = new File(new File(".").getCanonicalPath());
    		fc.setCurrentDirectory(f);
		}
		
		catch (IOException io) {
			System.out.println("Input/Output Exception");
		}

		JMenuBar menubar = new JMenuBar();
		JMenu file = new JMenu("File");

		JMenuItem open = new JMenuItem("Open...");
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		file.add(open);
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fc.showOpenDialog(FsaEditor.this) == JFileChooser.APPROVE_OPTION) {
					fsa = new FsaImpl();
					fsa.addListener(panel);
					try (BufferedReader r = new BufferedReader(new FileReader(fc.getSelectedFile()))) {
						readWrite.read(r, fsa);
					}
					
					catch (FileNotFoundException fnf) {
						JOptionPane optionPane = new JOptionPane("File not found", JOptionPane.ERROR_MESSAGE);
						final JDialog error = new JDialog(FsaEditor.this, "File Not Found Exception", true);
						error.setContentPane(optionPane);
						optionPane.addPropertyChangeListener(new PropertyChangeListener() {
							public void propertyChange(PropertyChangeEvent e) {
        						error.setVisible(false);
        					}
						} );

						error.pack();
						error.setVisible(true);
					}

					catch (IOException ex) {
						JOptionPane optionPane = new JOptionPane("IO", JOptionPane.ERROR_MESSAGE);
						final JDialog error = new JDialog(FsaEditor.this, "Input/Output Exception", true);
						error.setContentPane(optionPane);
						optionPane.addPropertyChangeListener(new PropertyChangeListener() {
							public void propertyChange(PropertyChangeEvent e) {
        						error.setVisible(false);
        					}
						} );

						error.pack();
						error.setVisible(true);
					}

					catch (FsaFormatException format) {
						JOptionPane optionPane = new JOptionPane("Illegal input format", JOptionPane.ERROR_MESSAGE);
						final JDialog error = new JDialog(FsaEditor.this, "Fsa Format Exception", true);
						error.setContentPane(optionPane);
						optionPane.addPropertyChangeListener(new PropertyChangeListener() {
        					public void propertyChange(PropertyChangeEvent e) {
        						error.setVisible(false);
        					}
        				} );

						error.pack();
						error.setVisible(true);
					}
				}
			}
		} );

		JMenuItem saveAs = new JMenuItem("Save as...");
		saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		file.add(saveAs);
		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fc.showSaveDialog(FsaEditor.this) == JFileChooser.APPROVE_OPTION) {
					try (Writer w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fc.getSelectedFile())))) {
						readWrite.write(w, fsa);
					}

					catch (FileNotFoundException fnf) {
						JOptionPane optionPane = new JOptionPane("File not found", JOptionPane.ERROR_MESSAGE);
						final JDialog error = new JDialog(FsaEditor.this, "File Not Found Exception", true);
						error.setContentPane(optionPane);
						optionPane.addPropertyChangeListener(new PropertyChangeListener() {
							public void propertyChange(PropertyChangeEvent e) {
								error.setVisible(false);
							}
						} );

						error.pack();
						error.setVisible(true);
					}

					catch (UnsupportedEncodingException ue) {
						JOptionPane optionPane = new JOptionPane("Unsupported encoding", JOptionPane.ERROR_MESSAGE);
						final JDialog error = new JDialog(FsaEditor.this, "Unsupported Encoding Exception", true);
						error.setContentPane(optionPane);
						optionPane.addPropertyChangeListener(new PropertyChangeListener() {
							public void propertyChange(PropertyChangeEvent e) {
								error.setVisible(false);
							}
						} );

						error.pack();
						error.setVisible(true);
					}

					catch (IOException ex) {
						JOptionPane optionPane = new JOptionPane("IO", JOptionPane.ERROR_MESSAGE);
						final JDialog error = new JDialog(FsaEditor.this, "Input/Output Exception", true);
						error.setContentPane(optionPane);
						optionPane.addPropertyChangeListener(new PropertyChangeListener() {
							public void propertyChange(PropertyChangeEvent e) {
								error.setVisible(false);
							}
						} );

						error.pack();
						error.setVisible(true);
					}
				}
			}
		} );

		JMenuItem quit = new JMenuItem("Quit");
		quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
		file.add(quit);
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		} );

		menubar.add(file);

		JMenu edit = new JMenu("Edit");
		edit.add(new JMenuItem("New state"));
		edit.add(new JMenuItem("New transition"));
		
		JMenuItem setInitial = new JMenuItem("Set initial");
		edit.add(setInitial);
		setInitial.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Iterator it = panel.getIcons().iterator();
				while (it.hasNext()) {
					StateIcon icon = (StateIcon)it.next();
					if (icon.checkSelected()) {
						icon.setInitial(true);
					}
				}
			}
		} );

		JMenuItem unsetInitial = new JMenuItem("Unset initial");
		edit.add(unsetInitial);
		unsetInitial.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Iterator it = panel.getIcons().iterator();
				while (it.hasNext()) {
					StateIcon icon = (StateIcon)it.next();
					if (icon.checkSelected()) {
						icon.setInitial(false);
					}
				}
			}
		} );

		JMenuItem setFinal = new JMenuItem("Set final");
		edit.add(setFinal);
		setFinal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Iterator it = panel.getIcons().iterator();
				while (it.hasNext()) {
					StateIcon icon = (StateIcon)it.next();
					if (icon.checkSelected()) {
						icon.setFinal(true);
					}
				}
			}
		} );

		JMenuItem unsetFinal = new JMenuItem("Unset final");
		edit.add(unsetFinal);
		unsetFinal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Iterator it = panel.getIcons().iterator();
				while (it.hasNext()) {
					StateIcon icon = (StateIcon)it.next();
					if (icon.checkSelected()) {
						icon.setFinal(false);
					}
				}
			}
		} );

		edit.add(new JMenuItem("Delete"));
		menubar.add(edit);
		setJMenuBar(menubar);

		pane.add(this.panel);
		this.fsa.addListener(this.panel);

		//Draw the GUI
		this.pack();
        this.setVisible(true);
	}

	public static void main(String[] args) {
		FsaEditor test = new FsaEditor();
	}
}