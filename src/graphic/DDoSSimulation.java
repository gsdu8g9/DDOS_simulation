package graphic;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import bin.*;
import javafx.scene.layout.Border;
import processing.core.*;

public class DDoSSimulation {

	private static final int WINDOW_WIDTH = 500,	POPUP_WIDTH = 300,
							 WINDOW_HEIGHT = 800,	POPUP_HEIGHT = 300;
	
	private JFrame window, popUpStart, ipAddressConfig;
	private Font labelFont = new Font("Cambria", Font.BOLD, 15),
				 descriptionFont = new Font("Cambria", Font.ITALIC, 15);			
	private JPanel configurePanel, terminalPanel, detailsPanel, startingPanel, ipMainPanel;
	private JLabel id_detail, ipAddress_detail, ttl_detail, domain_detail, type_detail, memory_detail;
	private JTextField numSlavesTF, ttlTF, memoryTF, packagesizeTF; 
	
	private ProcessingSimulation procGraphic;
	private String[] pArgs = {"ProcessingSimulation "};
	private int numSlavesConf = 0, ttlConf = 4, memoryConf = 0, packageConf = 32;
	
	public DDoSSimulation() {
		makePopUpStart();
		procGraphic = new ProcessingSimulation(this);
	}
	
	private void makePopUpStart() {
		popUpStart = new JFrame("New configuration");
		popUpStart.setSize(POPUP_WIDTH, POPUP_HEIGHT);
		
		startingPanel = new JPanel(new GridLayout(5,1));
		JPanel buttonPanel = new JPanel();
		
		JPanel resourcesPanel = new JPanel();
		resourcesPanel.setBorder(BorderFactory.createTitledBorder("Resources"));
		ButtonGroup resourcesG = new ButtonGroup();
		JRadioButton internalResources = new JRadioButton("Internal");
		JRadioButton networkResources = new JRadioButton("Network");
		internalResources.setSelected(true);
		resourcesG.add(internalResources);		
		resourcesG.add(networkResources);
		resourcesPanel.add(internalResources);		
		resourcesPanel.add(networkResources);
		startingPanel.add(resourcesPanel, BorderLayout.NORTH);
		
		JPanel typePanel = new JPanel();
		typePanel.setBorder(BorderFactory.createTitledBorder("DDoS Type"));
		ButtonGroup ddosTypeG = new ButtonGroup();
		JRadioButton direct = new JRadioButton("Direct");
		JRadioButton reflected = new JRadioButton("Reflected");
		direct.setSelected(true);
		ddosTypeG.add(direct);		
		ddosTypeG.add(reflected);
		typePanel.add(direct);		
		typePanel.add(reflected);
		startingPanel.add(typePanel, BorderLayout.CENTER);
		
		JPanel packageType = new JPanel();
		packageType.setBorder(BorderFactory.createTitledBorder("Package Type"));
		ButtonGroup packageG = new ButtonGroup();
		JRadioButton cyn = new JRadioButton("CYN");
		JRadioButton other = new JRadioButton("other");
		cyn.setSelected(true);
		packageG.add(cyn);				
		packageG.add(other);			
		packageType.add(cyn);				
		packageType.add(other);			// --> UPDATE
		startingPanel.add(packageType, BorderLayout.SOUTH);
		
		JPanel numberSlaves = new JPanel();
		numberSlaves.setBorder(BorderFactory.createTitledBorder("Number of slaves"));
		ButtonGroup slavesG = new ButtonGroup();
		JRadioButton under200 = new JRadioButton("under 200");
		JRadioButton above200 = new JRadioButton("above 200");
		under200.setSelected(true);
		slavesG.add(under200);				
		slavesG.add(above200);			
		numberSlaves.add(under200);				
		numberSlaves.add(above200);			
		startingPanel.add(numberSlaves);
		
		JButton confirm = new JButton("START");
		buttonPanel.add(confirm);
		startingPanel.add(confirm);
		
		popUpStart.add(startingPanel);
		
		popUpStart.setVisible(true);
		popUpStart.setResizable(false);
		
		confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean r_internal = internalResources.isSelected();
				boolean y_direct = direct.isSelected();
				boolean p_cyn = cyn.isSelected();
				boolean n_slaves = under200.isSelected();
				
				makeWindow(r_internal, y_direct, p_cyn, n_slaves);
			}
		});
	}
	
	private void makeWindow(boolean internalResouces, boolean typeDirect, boolean packageCYN, boolean under200) {
		popUpStart.setVisible(false);
		
		window = new JFrame("DDoS simulation");
		window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT); 
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
		configurePanel = new JPanel(new BorderLayout());
		
		// configure tab -------------------------------------------------------------------------------
		JPanel cSlavesConfig = new JPanel(new GridLayout(5,3,3,3));
		cSlavesConfig.setBorder(BorderFactory.createTitledBorder("Slaves configuration"));
		
		numSlavesTF = new JTextField(15);		JTextField dummy7 = new JTextField(10);		dummy7.setVisible(false);
		ttlTF = new JTextField(15);				JTextField dummy8 = new JTextField(10);		dummy8.setVisible(false);
		memoryTF = new JTextField(15);			JTextField dummy9 = new JTextField(10);		dummy9.setVisible(false);
		packagesizeTF = new JTextField(15);		JTextField dummy10 = new JTextField(10);	dummy10.setVisible(false);
												JTextField dummy11 = new JTextField(10);	dummy11.setVisible(false);
												
		cSlavesConfig.add(new JLabel("Number of slaves:"));		cSlavesConfig.add(numSlavesTF); 	cSlavesConfig.add(dummy7);
		cSlavesConfig.add(new JLabel("Memory size:"));			cSlavesConfig.add(memoryTF); 		cSlavesConfig.add(dummy8);
		cSlavesConfig.add(new JLabel("TTL time:"));				cSlavesConfig.add(ttlTF); 			cSlavesConfig.add(dummy9);
		cSlavesConfig.add(new JLabel("Package size: "));		cSlavesConfig.add(packagesizeTF); 	cSlavesConfig.add(dummy10);
																									
		JButton submitConfiguration = new JButton("Configure");					
		cSlavesConfig.add(dummy11);
		cSlavesConfig.add(submitConfiguration);
		
		configurePanel.add(cSlavesConfig, BorderLayout.NORTH);
		
		// details panel -------------------------------------------------------------------------------
		detailsPanel = new JPanel(new GridLayout(6, 3, 3, 3));
		detailsPanel.setBorder(BorderFactory.createTitledBorder("Details"));
		
		JLabel ID = new JLabel("ID"); 				ID.setFont(labelFont);
		JLabel IPAddress = new JLabel("IPAddress"); IPAddress.setFont(labelFont);
		JLabel Domain = new JLabel("Domain"); 		Domain.setFont(labelFont);
		JLabel Type = new JLabel("Type"); 			Type.setFont(labelFont);
		JLabel Memory = new JLabel("Memory"); 		Memory.setFont(labelFont);
		JLabel TTL = new JLabel("TTL");				TTL.setFont(labelFont);
		
		id_detail = new JLabel();			id_detail.setFont(descriptionFont);			
		ipAddress_detail = new JLabel();	ipAddress_detail.setFont(descriptionFont);	
		domain_detail = new JLabel();		domain_detail.setFont(descriptionFont);		
		ttl_detail = new JLabel();			ttl_detail.setFont(descriptionFont);		
		memory_detail = new JLabel();		memory_detail.setFont(descriptionFont);		
		type_detail = new JLabel();			type_detail.setFont(descriptionFont);		
		
		JTextField dummy1 = new JTextField("Dummy textfield.");		dummy1.setVisible(false);
		JTextField dummy2 = new JTextField("Dummy textfield.");		dummy2.setVisible(false);
		JTextField dummy3 = new JTextField("Dummy textfield.");		dummy3.setVisible(false);
		JTextField dummy4 = new JTextField("Dummy textfield.");		dummy4.setVisible(false);
		JTextField dummy5 = new JTextField("Dummy textfield.");		dummy5.setVisible(false);
		JTextField dummy6 = new JTextField("Dummy textfield.");		dummy6.setVisible(false);
		
		detailsPanel.add(ID);			detailsPanel.add(id_detail);			detailsPanel.add(dummy1);
		detailsPanel.add(IPAddress);	detailsPanel.add(ipAddress_detail);		detailsPanel.add(dummy2);
		detailsPanel.add(Domain);		detailsPanel.add(domain_detail);		detailsPanel.add(dummy3);
		detailsPanel.add(Type);			detailsPanel.add(type_detail);			detailsPanel.add(dummy4);
		detailsPanel.add(Memory);		detailsPanel.add(memory_detail);		detailsPanel.add(dummy5);
		detailsPanel.add(TTL);			detailsPanel.add(ttl_detail);			detailsPanel.add(dummy6);
		
		configurePanel.add(detailsPanel);
		detailsPanel.setVisible(false); 	// -> will be visible when mouse click on component
		
		// terminal tab --------------------------------------------------------------------------------
		JTextArea terminal = new JTextArea(290,590);
		terminal.setBackground(Color.BLACK);
		terminal.setForeground(Color.WHITE);
		terminal.append("Hello human");
		
		terminalPanel.add(terminal);
		
		tabs.addTab("Configure", configurePanel);
		tabs.addTab("Terminal", terminalPanel);
		window.add(tabs);
		
		// 'Configure' button -> locks textfields (for new parameters new simulation must be started)
							//-> open new JFrame where user chooses how to configure ip addresses for slaves
							//-> 1. default way | 2. from document	| 3. typing
		submitConfiguration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				numSlavesConf = Integer.parseInt(numSlavesTF.getText());
				ttlConf = Integer.parseInt(ttlTF.getText());
				memoryConf = Integer.parseInt(memoryTF.getText());
				packageConf = Integer.parseInt(packagesizeTF.getText());
				
				numSlavesTF.setEditable(false);
				ttlTF.setEditable(false);
				memoryTF.setEditable(false);
				packagesizeTF.setEditable(false);
				
				submitConfiguration.setEnabled(false);
				makeIPAddressConfigWindow(numSlavesConf);
			}
		});
		
	}
	
	public void makeIPAddressConfigWindow(int numSlaves) {
		ipAddressConfig = new JFrame();
		ipAddressConfig.setSize(WINDOW_WIDTH, WINDOW_HEIGHT/2);
		ipMainPanel = new JPanel(new BorderLayout());
		
		JPanel topChoicePanel = new JPanel();
		topChoicePanel.setBorder(BorderFactory.createTitledBorder("Choose way of configuring IP Addresses"));
		
		JButton r_default = new JButton("Default");		topChoicePanel.add(r_default);
		JButton r_file = new JButton("From file");		topChoicePanel.add(r_file);
		JButton r_user = new JButton("User input");		topChoicePanel.add(r_user);

		ipMainPanel.add(topChoicePanel, BorderLayout.NORTH);
		
		JPanel filePanel = new JPanel();
		filePanel.setBorder(BorderFactory.createTitledBorder("Choose file with IP Addresses"));
		JLabel pathL = new JLabel("Path: ");
		JTextField pathTF = new JTextField(30);
		filePanel.add(pathL);
		filePanel.add(pathTF);
					
		JButton buttonSubmit = new JButton("Submit");
		ipMainPanel.add(buttonSubmit, BorderLayout.SOUTH);
		
		filePanel.setVisible(false);
		ipMainPanel.add(filePanel, BorderLayout.CENTER);
		
		ipAddressConfig.add(ipMainPanel);
		ipAddressConfig.setVisible(true);
		ipAddressConfig.setResizable(false);
		
		r_file.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				r_default.setEnabled(false);
				r_user.setEnabled(false);
				
				filePanel.setVisible(true);
			}
		});
	}
	
	public ProcessingSimulation getProcessingSimulation() { return procGraphic; }
	
	public void showComputerDetails(Computer comp, int nodeID) {
		id_detail.setText(": " + nodeID);
		ipAddress_detail.setText(": " + comp.getIpAddress());
		domain_detail.setText(": " + comp.getDomain());
		type_detail.setText(": " + comp.getTypeString());
		ttl_detail.setText(": " + comp.getTTL());
		memory_detail.setText(": " + comp.getMemBuffSizeCurrent() + " / " + comp.getMemBuffSize());
	}
	
	public void detailPanelVisible(boolean value) { detailsPanel.setVisible(value);}
	
	public static void main(String[] args) {
		DDoSSimulation mainThread = new DDoSSimulation();
	}

}
