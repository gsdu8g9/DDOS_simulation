package graphic;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import processing.core.PApplet;


public class DDoSSimulation {

	private JFrame window;
	private JPanel configurePanel, simulationPanel;
	private ProcessingSimulation sim;
	
	public DDoSSimulation() {
		makeWindow();
		runSimulation();
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
		String[] pArgs = {"ProcessingSimulation "};
	    sim = new ProcessingSimulation();
	    PApplet.runSketch(pArgs, sim);
	}
	
	private void generateTabs() {
		JTabbedPane tabs = new JTabbedPane();
		simulationPanel = new JPanel();
		configurePanel = new JPanel();
		tabs.addTab("Simulation", simulationPanel);
		tabs.addTab("Configure", configurePanel);
		window.add(tabs);
	}
	
	public static void main(String[] args) {
		DDoSSimulation mainThread = new DDoSSimulation();
	}

}
