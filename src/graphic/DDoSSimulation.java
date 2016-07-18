package graphic;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import processing.core.PApplet;

public class DDoSSimulation {

	private JFrame window;
	private JPanel configurePanel, terminalPanel;
	private ProcessingSimulation procGraphic;
	private String[] pArgs = {"ProcessingSimulation "};
	private int numSlavesConf = 0;
	
	public DDoSSimulation() {
		makeWindow();
		procGraphic = new ProcessingSimulation();
	}
	
	private void makeWindow() {
		window = new JFrame("DDoS simulation");
		window.setSize(300, 600); 
		window.setLayout(new BorderLayout());
		
		generateTabs();
		
		window.setVisible(true);
		window.setResizable(false);
	}
	
	public void runSimulation() {
	    PApplet.runSketch(pArgs, procGraphic);
	}
	
	private void generateTabs() {
		JTabbedPane tabs = new JTabbedPane();
		terminalPanel = new JPanel();
		configurePanel = new JPanel();
		
		// configure tab ------------------------
		JPanel cSlaves = new JPanel();
		cSlaves.setBorder(BorderFactory.createTitledBorder("Slaves"));
		
		cSlaves.add(new JLabel("Number of slaves:"));
		JTextField numSlaves = new JTextField(10);
		cSlaves.add(numSlaves);
		
		configurePanel.add(cSlaves);
		
		JButton submitConfiguration = new JButton("Configure");
		configurePanel.add(submitConfiguration);
		
		tabs.addTab("Configure", configurePanel);
		tabs.addTab("Simulation", terminalPanel);
		window.add(tabs);
		
		// action listeners --------------------------------------
		
		submitConfiguration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				numSlavesConf = Integer.parseInt(numSlaves.getText());
				procGraphic.setNumOfSlaves(numSlavesConf);
				procGraphic.makeNetwork();
				
				runSimulation();
			}
		});
	}
	
	public ProcessingSimulation getProcessingSimulation() { return procGraphic; }
	
	public static void main(String[] args) {
		DDoSSimulation mainThread = new DDoSSimulation();
	}

}
