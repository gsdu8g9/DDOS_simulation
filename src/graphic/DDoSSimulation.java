package graphic;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.*;
import bin.*;
import bin.Package;
import javafx.scene.layout.Border;
import processing.core.*;

public class DDoSSimulation {

	private static final int WINDOW_WIDTH = 500,	POPUP_WIDTH = 400,
							 WINDOW_HEIGHT = 800,	POPUP_HEIGHT = 300;
	
	public static final int CYN_FLOOD = 1, ICMP_FLOOD = 2;
	
	public static boolean globalResourceTypeInternal = true, globalDDOSTypeDirect = true, globalPackageTypeCYN = true, globalGraphTypeU45 = true;
	public static int globalNumSlaves = 35, globalNumMasterSlaves = 10;
	public static int ttlConf = 4, memoryConf = 0, packageConf = 32;
	
	private JFrame window, popUpStart, ipAddressConfig;
	private Font labelFont = new Font("Cambria", Font.BOLD, 15),
				 descriptionFont = new Font("Cambria", Font.ITALIC, 15),	
				 terminalFont = new Font("Lucida Sans Typewriter", Font.PLAIN, 12);
	private JPanel configurePanel, terminalPanel, detailsPanel, historyPanel, startingPanel, ipMainPanel, computerDetailsPanel, userHelpPanel;
	private JLabel id_detail, ipAddress_detail, ttl_detail, domain_detail, type_detail, memory_detail;
	private JTextField numSlavesTF, ttlTF, memoryTF, packagesizeTF, numMastersTF; 
	private boolean userInput = false, defaultInput = false, fileInput = false;
	private JTextArea terminal, packages_received_detail, packages_sent_detail;
	private JButton startDDoS;
	
	private int packageType = 1, lastInputTerminal = 1;
	
	private ProcessingSimulation procGraphic = null;
	private String[] pArgs = {"ProcessingSimulation "};
	
	
	public DDoSSimulation() {
		makePopUpStart();
		//makeWindow();
		procGraphic = new ProcessingSimulation(this);
		//procGraphic.makeNetworkDefault();
		//runSimulation();
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
		
	/*	JPanel numberSlaves = new JPanel();
		numberSlaves.setBorder(BorderFactory.createTitledBorder("Number of slaves"));
		ButtonGroup slavesG = new ButtonGroup();
		JRadioButton under45 = new JRadioButton("REAL SIM- under 60");
		JRadioButton above45 = new JRadioButton("GRAPH SIM - above 60");
		under45.setSelected(true);
		slavesG.add(under45);				
		slavesG.add(above45);			
		numberSlaves.add(under45);				
		numberSlaves.add(above45);			
		startingPanel.add(numberSlaves); */
		
		JButton confirm = new JButton("START");
		buttonPanel.add(confirm);
		startingPanel.add(confirm);
		
		popUpStart.add(startingPanel);
		
		popUpStart.setVisible(true);
		popUpStart.setResizable(false);
		
		confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				globalResourceTypeInternal = internalResources.isSelected();
				globalDDOSTypeDirect = direct.isSelected();
				globalPackageTypeCYN = cyn.isSelected();
				//globalGraphTypeU45 = under45.isSelected();
				
				makeWindow();
			}
		});
	}
	
	private void makeWindow() {
		popUpStart.setVisible(false);
		
		window = new JFrame("DDoS simulation");
		window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT); 
		generateTabs();
		window.setVisible(true);
		window.setResizable(false);
	}
	
	public void runSimulation() {
	    PApplet.runSketch(pArgs, procGraphic);
	    procGraphic.saveInitialNetwork();
	}
	
	private void generateTabs() {
		JTabbedPane tabs = new JTabbedPane();
		terminalPanel = new JPanel();
		configurePanel = new JPanel(new BorderLayout());
		computerDetailsPanel = new JPanel(new BorderLayout());
		userHelpPanel = new JPanel(new BorderLayout());
		
		// configure tab -------------------------------------------------------------------------------
		JPanel cSlavesConfig = new JPanel(new GridLayout(6,3,3,3));
		cSlavesConfig.setBorder(BorderFactory.createTitledBorder("Slaves configuration"));
		
		numSlavesTF = new JTextField(15);		JTextField dummy7 = new JTextField(10);		dummy7.setVisible(false);
		ttlTF = new JTextField(15);				JTextField dummy8 = new JTextField(10);		dummy8.setVisible(false);
		memoryTF = new JTextField(15);			JTextField dummy9 = new JTextField(10);		dummy9.setVisible(false);
		packagesizeTF = new JTextField(15);		JTextField dummy10 = new JTextField(10);	dummy10.setVisible(false);
												JTextField dummy11 = new JTextField(10);	dummy11.setVisible(false);
												JTextField dummy12 = new JTextField(10);	dummy12.setVisible(false);
		
		cSlavesConfig.add(new JLabel("Number of slaves:"));		cSlavesConfig.add(numSlavesTF); 	cSlavesConfig.add(dummy7);
		cSlavesConfig.add(new JLabel("Memory size:"));			cSlavesConfig.add(memoryTF); 		cSlavesConfig.add(dummy8);
		cSlavesConfig.add(new JLabel("Time in memory:"));		cSlavesConfig.add(ttlTF); 			cSlavesConfig.add(dummy9);
		cSlavesConfig.add(new JLabel("Package size: "));		cSlavesConfig.add(packagesizeTF); 	cSlavesConfig.add(dummy10);
																									
		JButton submitConfiguration = new JButton("Configure");					
		cSlavesConfig.add(dummy11);
		cSlavesConfig.add(submitConfiguration);
		
		
		//********************************88
		JPanel cAttackOptions = new JPanel(new GridLayout(3,1,3,3));
		cAttackOptions.setBorder(BorderFactory.createTitledBorder("DDOS Attack options"));
		
		JButton startInfectingMasters = new JButton("Start infecting master zombies");
		cAttackOptions.add(startInfectingMasters);
		
		startDDoS = new JButton("Start DDOS attack");
		startDDoS.setEnabled(false);
		cAttackOptions.add(startDDoS);
		
		JButton pausePlay = new JButton("Pause/Play simulation");
		pausePlay.setEnabled(false);
		cAttackOptions.add(pausePlay);
		//**************************************88
		
		configurePanel.add(cSlavesConfig, BorderLayout.NORTH);
		configurePanel.add(cAttackOptions, BorderLayout.CENTER);
		
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
		
		computerDetailsPanel.add(detailsPanel, BorderLayout.NORTH);
		detailsPanel.setVisible(false); 	// -> will be visible when mouse click on component
		//history panel --------------------------------------------------------------------------------
		
		historyPanel = new JPanel(new GridLayout(2,1,3,3));
		historyPanel.setBorder(BorderFactory.createTitledBorder("Packages history"));
		
		packages_received_detail = new JTextArea(12,65);
		packages_received_detail.setBackground(Color.BLACK);
		packages_received_detail.setForeground(Color.RED);
		packages_received_detail.setFont(terminalFont);
		JScrollPane sp_packReceived = new JScrollPane(packages_received_detail);
		
		packages_sent_detail = new JTextArea(12,65);
		packages_sent_detail.setBackground(Color.BLACK);
		packages_sent_detail.setForeground(Color.MAGENTA);
		packages_sent_detail.setFont(terminalFont);
		JScrollPane sp_packSent = new JScrollPane(packages_sent_detail);
		
		//JLabel received = new JLabel("Received packages"); 		received.setFont(labelFont); 
		//JLabel sent = new JLabel("Sent packages"); 				sent.setFont(labelFont);
		
		//historyPanel.add(received);
		historyPanel.add(sp_packReceived);
		//historyPanel.add(sent);
		historyPanel.add(sp_packSent);
		
		computerDetailsPanel.add(historyPanel, BorderLayout.CENTER);
		historyPanel.setVisible(false); 	// -> will be visible when mouse click on component
		// terminal tab --------------------------------------------------------------------------------
		
		
		terminal = new JTextArea(44,67);
		terminal.setBackground(Color.BLACK);
		terminal.setForeground(Color.WHITE);
		terminal.setFont(terminalFont);
		terminal.append(">");
		JScrollPane sp = new JScrollPane(terminal);
		
		terminal.addKeyListener(new KeyListener(){
		    @Override
		    public void keyPressed(KeyEvent e){
		        if(e.getKeyCode() == KeyEvent.VK_ENTER){
		        	e.consume();
		        	
		        	String command = terminal.getText();
		        	command = command.substring(lastInputTerminal);
		        	
		        	if (command.equals("start infecting")) { //---------------------------------------------------------
		        		if (procGraphic == null) {
		        			terminal.append("\n>Configure network first... \n>");
		        			updateLastInputTerminal();
		        		} else 
		        			procGraphic.infectSlaves();
		        	} 
		        	else if (command.equals("start ddos")) { //----------------------------------------------------------
		        		if (procGraphic.getStage() == ProcessingSimulation.STAGE_ALL_INFECTED)
		        			procGraphic.startDDos();
		        		else {
		        			terminal.append("\n>Infect slaves first... \n>");
		        			updateLastInputTerminal();
		        		}
		        	} 
		        	
		        	
		        }
		    }

		    @Override
		    public void keyTyped(KeyEvent e) { return; }
		    
		    @Override
		    public void keyReleased(KeyEvent e) { return; }

		});
		
		terminalPanel.add(sp);
		
		// USER HELP TAB------------------------------------------------------------------------------------------
		
		
		//---------------------------------------------------------------------------------------------------------
		
		tabs.addTab("Configure", configurePanel);
		tabs.addTab("Terminal", terminalPanel);
		tabs.addTab("Computer details", computerDetailsPanel);
		tabs.addTab("User help", userHelpPanel);
		window.add(tabs);
		
		// 'Configure' button -> locks textfields (for new parameters new simulation must be started)
							//-> open new JFrame where user chooses how to configure ip addresses for slaves
							//-> 1. default way | 2. from document	| 3. typing
		submitConfiguration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				globalNumSlaves = Integer.parseInt(numSlavesTF.getText());
				ttlConf = Integer.parseInt(ttlTF.getText());
				memoryConf = Integer.parseInt(memoryTF.getText());
				packageConf = Integer.parseInt(packagesizeTF.getText());
				
				numSlavesTF.setEditable(false);
				ttlTF.setEditable(false);
				memoryTF.setEditable(false);
				packagesizeTF.setEditable(false);
				
				globalGraphTypeU45 = globalDDOSTypeDirect? (globalNumSlaves <= 45 ? true : false ) : (globalNumSlaves <= 35 ? true : false);
				
				submitConfiguration.setEnabled(false);
				makeIPAddressConfigWindow();
			}
		});
		
		startInfectingMasters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				procGraphic.infectSlaves();
				startInfectingMasters.setEnabled(false);
				pausePlay.setEnabled(true);
			}
		});
		
		startDDoS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				procGraphic.startDDos();
				startDDoS.setEnabled(false);
				pausePlay.setEnabled(true);
			}
		});
		
		pausePlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (procGraphic.getStage() == ProcessingSimulation.STAGE_PAUSE) { //continue
					procGraphic.continueSimulation();
				}
				else { //pause
					procGraphic.pauseSimulation();
				}
			}
		});
		
	}
	
	public void makeIPAddressConfigWindow() {
		ipAddressConfig = new JFrame();
		ipAddressConfig.setSize(WINDOW_WIDTH, WINDOW_HEIGHT/4);
		ipMainPanel = new JPanel(new BorderLayout());
		
		JPanel topChoicePanel = new JPanel();
		topChoicePanel.setBorder(BorderFactory.createTitledBorder("Choose way of configuring IP Addresses"));
		
		JButton r_default = new JButton("Default");		topChoicePanel.add(r_default);
		JButton r_file = new JButton("From file");		topChoicePanel.add(r_file);
		JButton r_user = new JButton("User input");		topChoicePanel.add(r_user);

		ipMainPanel.add(topChoicePanel, BorderLayout.NORTH);
		
		JPanel filePanel = new JPanel();
					
		JButton buttonSubmit = new JButton("Submit");
		ipMainPanel.add(buttonSubmit, BorderLayout.SOUTH);
		
		filePanel.setVisible(false);
		ipMainPanel.add(filePanel, BorderLayout.CENTER);
		
		ipAddressConfig.add(ipMainPanel);
		ipAddressConfig.setVisible(true);
		ipAddressConfig.setResizable(true);
		
		r_file.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				r_default.setEnabled(false);
				r_user.setEnabled(false);
				
				filePanel.setBorder(BorderFactory.createTitledBorder("Choose file with IP Addresses"));
				JLabel pathL = new JLabel("Path: ");
				JTextField pathTF = new JTextField(30);
				filePanel.add(pathL);
				filePanel.add(pathTF);
				
				fileInput = true;
				filePanel.setVisible(true);
			}
		});
		
		//this must be global in this class, so method configureNetworkFromUserInput() can access this
		ArrayList<JTextField> ipInputs = new ArrayList<JTextField>();
		
		// case when same button is pushed multiple times ? - lock this button too?
		r_user.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				r_default.setEnabled(false);
				r_file.setEnabled(false);
				
				filePanel.setBorder(BorderFactory.createTitledBorder("Type in ip addresses"));
				filePanel.setLayout(new GridLayout(globalNumSlaves/2,2,5,5));
				for (int i=0; i < globalNumSlaves; i++) {
					JTextField ipInput = new JTextField(20);
					ipInputs.add(ipInput);
					filePanel.add(ipInput);
				}
				
				userInput = true;
				filePanel.setVisible(true);
			}
		});
		
		r_default.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				r_user.setEnabled(false);
				r_file.setEnabled(false);
				defaultInput = true;
			}
		});
		
		buttonSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (userInput) return; 		//configureNetworkFromUserInput();
				else if (fileInput) return; //configureNetworkFromFileInput();
				else if (defaultInput) configureNetworkByDefault();
				
				ipAddressConfig.setVisible(false);
			}
		});
	}
	
	private void configureNetworkByDefault() {
		procGraphic.makeNetworkDefault();
		runSimulation();
	}
	
	public ProcessingSimulation getProcessingSimulation() { return procGraphic; }
	
	public void showComputerDetails(Computer comp, int nodeID) {
		id_detail.setText(": " + nodeID);
		ipAddress_detail.setText(": " + comp.getIpAddress());
		domain_detail.setText(": " + comp.getDomain());
		type_detail.setText(": " + comp.getTypeString());
		ttl_detail.setText(": " + comp.getTTL());
		memory_detail.setText(": " + comp.getMemBuffSizeCurrent() + " / " + comp.getMemBuffSize());
		packages_received_detail.setText("RECEIVED PACKAGES\n\n");
		packages_sent_detail.setText("SENT PACKAGES\n\n");
		
		int numPackRec = 0;
		Set<Package> received = comp.getReceivedPackages();
		for (Package pack: received) {
			packages_received_detail.append("___PACKAGE "+numPackRec+"___\n");
			packages_received_detail.append("sender IP: "+pack.getEdge().getNodeFrom().getComputer().getIpAddress()+"\n");
			packages_received_detail.append("size: "+pack.getSize()+"\n");
			packages_received_detail.append("time sent: "+pack.getTimeStartSending()+"\n");
			packages_received_detail.append("time received: "+pack.getReceivedTime()+"\n");
			packages_received_detail.append("________________\n");
			numPackRec++;
		}
		int numPackSent = 0;
		Set<Package> sent = comp.getSentPackages();
		for (Package pack: sent) {
			packages_sent_detail.append("___PACKAGE "+numPackSent+"___\n");
			packages_sent_detail.append("receiver IP: "+pack.getEdge().getNodeTo().getComputer().getIpAddress()+"\n");
			packages_sent_detail.append("size: "+pack.getSize()+"\n");
			packages_sent_detail.append("time sent: "+pack.getTimeStartSending()+"\n");
			packages_sent_detail.append("time received: "+pack.getReceivedTime()+"\n");
			packages_sent_detail.append("________________\n");
			numPackSent++;
		}
		
	}
	
	public void detailPanelVisible(boolean value) { detailsPanel.setVisible(value);}
	public void historyPanelVisible(boolean value) { historyPanel.setVisible(value);}
	public void setStartDDoSEnabled() {startDDoS.setEnabled(true);}
	
	public JTextArea getTerminal() {
		return terminal;
	}
	
	public void updateLastInputTerminal(){
		String textTerminal = terminal.getText();
		lastInputTerminal = textTerminal.length();
	}
	
	public static void main(String[] args) {
		DDoSSimulation mainThread = new DDoSSimulation();
	}

}