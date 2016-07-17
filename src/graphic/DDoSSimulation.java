package graphic;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import processing.core.PApplet;


public class DDoSSimulation {

	private JFrame window;
	private JPanel configurePanel, simulationPanel;
	private ProcessingSimulation procGraphic;
	private String[] pArgs = {"ProcessingSimulation "};
	
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
		simulationPanel = new JPanel();
		configurePanel = new JPanel();
		tabs.addTab("Simulation", simulationPanel);
		tabs.addTab("Configure", configurePanel);
		window.add(tabs);
	}
	
	public ProcessingSimulation getProcessingSimulation() { return procGraphic; }
	
	public static void main(String[] args) {
		DDoSSimulation mainThread = new DDoSSimulation();
		
		int nodes = 5;
		mainThread.getProcessingSimulation().setNumOfSlaves(nodes);
		mainThread.getProcessingSimulation().makeNetwork();
		mainThread.runSimulation();
		
		//while(true)
			//mainThread.getProcessingSimulation().draw();
	}

}
