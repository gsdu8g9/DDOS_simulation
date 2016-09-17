package graphic;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bin.*;
import bin.Package;
import processing.core.*;

public class DDoSSimulation {

	private static final int WINDOW_WIDTH = 500,	POPUP_WIDTH = 400,
							 WINDOW_HEIGHT = 800,	POPUP_HEIGHT = 200;
	
	public static boolean globalResourceTypeInternal = false, globalDDOSTypeDirect = true, globalPackageTypeTCP = false, globalGraphTypeU45 = true;
	public static int globalNumSlaves =30, 
					  globalNumMasterSlaves = 5;
	public static int globalSpeedUpBar = 3,
					  globalInMemTimeConf = 10, 		
			          globalMemoryConf = 2048, 		
			          globalPackageSizeConf = 32;
	public static int globalGenPackagePerSec = 1;	// from masters - other we adjust according to this
					 
	private JFrame window, popUpStart;
	private Font labelFont = new Font("Cambria", Font.BOLD, 15),
				 descriptionFont = new Font("Cambria", Font.ITALIC, 15),	
				 terminalFont = new Font("Lucida Sans Typewriter", Font.PLAIN, 12);
	
	private JPanel configurePanel, terminalPanel, detailsPanel, historyPanel, startingPanel, computerDetailsPanel, userHelpPanel;
	private JLabel id_detail, ipAddress_detail, ttl_detail, type_detail, memory_detail;
	private JTextField ttlTF, memoryTF, packagesizeTF;
	private Choice numSlavesChoice;
	private JTextArea terminal, packages_received_detail;
	private JButton startDDoS;
	
	private int lastInputTerminal = 1;
	private boolean ddosStarted = false;
	
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
		
		startingPanel = new JPanel(new GridLayout(3,1));
		JPanel buttonPanel = new JPanel();
		
		JPanel resourcesPanel = new JPanel();
		resourcesPanel.setBorder(BorderFactory.createTitledBorder("DDoS attack"));
		ButtonGroup resourcesG = new ButtonGroup();
		JRadioButton internalResources = new JRadioButton("Internal - Direct - TCP");
		JRadioButton networkResources = new JRadioButton("Network - Reflected - ICMP");
		internalResources.setSelected(true);
		resourcesG.add(internalResources);		
		resourcesG.add(networkResources);
		resourcesPanel.add(internalResources);		
		resourcesPanel.add(networkResources);
		startingPanel.add(resourcesPanel, BorderLayout.NORTH);
		
	/*	JPanel typePanel = new JPanel();
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
		*/
		JPanel numberSlaves = new JPanel();
		numberSlaves.setBorder(BorderFactory.createTitledBorder("Number of slaves"));
		ButtonGroup slavesG = new ButtonGroup();
		JRadioButton under45 = new JRadioButton("REAL SIM- under 45");
		JRadioButton above45 = new JRadioButton("GRAPH SIM - above 45");
		under45.setSelected(true);
		slavesG.add(under45);				
		slavesG.add(above45);			
		numberSlaves.add(under45);				
		numberSlaves.add(above45);			
		//startingPanel.add(numberSlaves); 
		startingPanel.add(numberSlaves, BorderLayout.CENTER);
		
		JButton confirm = new JButton("START");
		buttonPanel.add(confirm);
		startingPanel.add(confirm, BorderLayout.SOUTH);
		
		popUpStart.add(startingPanel);
		
		popUpStart.setVisible(true);
		popUpStart.setResizable(false);
		
		confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				globalResourceTypeInternal = internalResources.isSelected();
				globalDDOSTypeDirect = internalResources.isSelected();
				globalPackageTypeTCP = internalResources.isSelected();
				globalGraphTypeU45 = under45.isSelected();
				
				makeWindow();
			}
		});
	}
	
	public void makePacketWindow(String s) {
		
		JOptionPane.showMessageDialog(null, s, "Packet overview", JOptionPane.PLAIN_MESSAGE);
		
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
		JPanel cSlavesConfig = new JPanel(new GridLayout(5,3,3,3));
		cSlavesConfig.setBorder(BorderFactory.createTitledBorder("Slaves configuration"));
		
												JTextField dummy7 = new JTextField(10);		dummy7.setVisible(false);			
		ttlTF = new JTextField(15);				JTextField dummy8 = new JTextField(10);		dummy8.setVisible(false);
		memoryTF = new JTextField(15);			JTextField dummy9 = new JTextField(10);		dummy9.setVisible(false);
		packagesizeTF = new JTextField(15);		JTextField dummy10 = new JTextField(10);	dummy10.setVisible(false);
												JTextField dummy11 = new JTextField(10);	dummy11.setVisible(false);
												JTextField dummy12 = new JTextField(10);	dummy12.setVisible(false);
		ttlTF.setText("4");										
		memoryTF.setText("2048");
		packagesizeTF.setText("32");
												
		String[] choicesDirectU45 = { "15", "24", "30", "33", "39", "42" };
		String[] choicesDirectA45 = { "56", "60", "80", "128", "140", "160" };
		String[] choicesReflectedU45 = { "7", "14", "21", "28", "35", "42" };
		String[] choicesReflectedA45 = { "49", "70", "84", "105", "133", "161" };
		numSlavesChoice = new Choice();
		
		String[] choices = globalDDOSTypeDirect == true ?  globalGraphTypeU45 == true?  choicesDirectU45 :  choicesDirectA45 : 
			 globalGraphTypeU45 == true?  choicesReflectedU45 :  choicesReflectedA45;
		for (String ch : choices)
			numSlavesChoice.add(ch);
														
		cSlavesConfig.add(new JLabel("Number of slaves:"));		cSlavesConfig.add(numSlavesChoice); 	cSlavesConfig.add(dummy7);
		cSlavesConfig.add(new JLabel("Memory size [B]:"));			cSlavesConfig.add(memoryTF); 		cSlavesConfig.add(dummy8);
		cSlavesConfig.add(new JLabel("Time in memory [s]:"));		cSlavesConfig.add(ttlTF); 			cSlavesConfig.add(dummy9);
		cSlavesConfig.add(new JLabel("Package size [B]: "));		cSlavesConfig.add(packagesizeTF); 	cSlavesConfig.add(dummy10);
				
		JButton submitConfiguration = new JButton("Configure");					
		cSlavesConfig.add(dummy11);
		cSlavesConfig.add(submitConfiguration);
		
		//********************************************************************************
		JPanel cTwoPanels = new JPanel(new GridLayout(6,1,5,5));
		JPanel cAttackOptions = new JPanel(new GridLayout(1,3,3,3));
		cAttackOptions.setBorder(BorderFactory.createTitledBorder("DDOS Attack options"));
		
		JButton startInfectingMasters = new JButton("INFECT ZOMBIES");
		startInfectingMasters.setEnabled(false);
		cAttackOptions.add(startInfectingMasters);
		
		startDDoS = new JButton("START DDOS");
		startDDoS.setEnabled(false);
		cAttackOptions.add(startDDoS);
		
		JButton pausePlay = new JButton("PAUSE/PLAY");
		pausePlay.setEnabled(false);
		cAttackOptions.add(pausePlay);
		
		JButton ping = new JButton("PING");
		ping.setEnabled(false);
		cAttackOptions.add(ping);
		ping.setBackground(Color.GREEN);
		
		cTwoPanels.add(cAttackOptions);
		//***********************************************************************************
		JPanel cspeedBar = generateSpeedBar();
		cTwoPanels.add(cspeedBar);
		JPanel cspeedGenerating = generateSpeedGeneratingPackages();
		cTwoPanels.add(cspeedGenerating);
		// ***************************************************************
		//for now dummy panel, just to fill space
		JPanel d1 = new JPanel(new GridLayout(3,1,5,5));
		JTextField d2 = new JTextField(10);		d2.setVisible(false);	d1.add(d2);
		JTextField d3 = new JTextField(10);		d3.setVisible(false);	d1.add(d3);
		JTextField d4 = new JTextField(10);		d4.setVisible(false);	d1.add(d4);
		
		configurePanel.add(cSlavesConfig, BorderLayout.NORTH);
		configurePanel.add(cTwoPanels, BorderLayout.CENTER);
		configurePanel.add(d1, BorderLayout.SOUTH);
		
		// details panel -------------------------------------------------------------------------------
		detailsPanel = new JPanel(new GridLayout(5, 3, 3, 3));
		detailsPanel.setBorder(BorderFactory.createTitledBorder("Details"));
		
		JLabel ID = new JLabel("ID"); 				ID.setFont(labelFont);
		JLabel IPAddress = new JLabel("IPAddress"); IPAddress.setFont(labelFont);
		JLabel Type = new JLabel("Type"); 			Type.setFont(labelFont);
		JLabel Memory = new JLabel("Memory"); 		Memory.setFont(labelFont);
		JLabel TTL = new JLabel("TTL");				TTL.setFont(labelFont);
		
		id_detail = new JLabel();			id_detail.setFont(descriptionFont);			
		ipAddress_detail = new JLabel();	ipAddress_detail.setFont(descriptionFont);	
		ttl_detail = new JLabel();			ttl_detail.setFont(descriptionFont);		
		memory_detail = new JLabel();		memory_detail.setFont(descriptionFont);		
		type_detail = new JLabel();			type_detail.setFont(descriptionFont);	
		
			
		JTextField dummy1 = new JTextField("Dummy textfield.");		dummy1.setVisible(false);
		JTextField dummy2 = new JTextField("Dummy textfield.");		dummy2.setVisible(false);
		JTextField dummy4 = new JTextField("Dummy textfield.");		dummy4.setVisible(false);
		JTextField dummy5 = new JTextField("Dummy textfield.");		dummy5.setVisible(false);
		JTextField dummy6 = new JTextField("Dummy textfield.");		dummy6.setVisible(false);
		
		detailsPanel.add(ID);			detailsPanel.add(id_detail);			detailsPanel.add(dummy1);
		detailsPanel.add(IPAddress);	detailsPanel.add(ipAddress_detail);		detailsPanel.add(dummy2);
		detailsPanel.add(Type);			detailsPanel.add(type_detail);			detailsPanel.add(dummy4);
		detailsPanel.add(Memory);		detailsPanel.add(memory_detail);		detailsPanel.add(dummy5);
		detailsPanel.add(TTL);			detailsPanel.add(ttl_detail);			detailsPanel.add(dummy6);
		
		computerDetailsPanel.add(detailsPanel, BorderLayout.NORTH);
		detailsPanel.setVisible(false); 	// -> will be visible when mouse click on component
		//history panel --------------------------------------------------------------------------------
		
		historyPanel = new JPanel();
		historyPanel.setPreferredSize(new Dimension(640, WINDOW_WIDTH));
		historyPanel.setBorder(BorderFactory.createTitledBorder("Packages history"));
		
		packages_received_detail = new JTextArea(100,65);
		packages_received_detail.setBackground(Color.BLACK);
		packages_received_detail.setForeground(Color.RED);
		packages_received_detail.setFont(terminalFont);
		JScrollPane sp_packReceived = new JScrollPane(packages_received_detail);
		
		historyPanel.add(sp_packReceived);
		
		computerDetailsPanel.add(historyPanel);
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
		        	
		        	if (command.equals("infect masters")) { //---------------------------------------------------------
		        		if (procGraphic == null) {
		        			terminal.append("\n>Configure network first... \n>");
		        			updateLastInputTerminal();
		        		} else {
		        			procGraphic.infectSlaves();
		    				startInfectingMasters.setEnabled(false);
		    				pausePlay.setEnabled(true);
		        		}
		        	} 
		        	else if (command.equals("start ddos")) { //----------------------------------------------------------
		        		if (procGraphic.getStage() == ProcessingSimulation.STAGE_ALL_INFECTED)
		        			procGraphic.startDDos();
		        		else {
		        			terminal.append("\n>Infect masters first... \n>");
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
				
				globalNumSlaves = Integer.parseInt(numSlavesChoice.getItem(numSlavesChoice.getSelectedIndex()));
				globalInMemTimeConf = Integer.parseInt(ttlTF.getText());
				globalMemoryConf = Integer.parseInt(memoryTF.getText());
				globalPackageSizeConf = Integer.parseInt(packagesizeTF.getText());
				
				numSlavesChoice.setEnabled(false);
				ttlTF.setEditable(false);
				memoryTF.setEditable(false);
				packagesizeTF.setEditable(false);
					
				submitConfiguration.setEnabled(false);
				startInfectingMasters.setEnabled(true);
				ping.setEnabled(true);
				configureNetworkByDefault();
			}
		});
		
		startInfectingMasters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				procGraphic.infectSlaves();
				startInfectingMasters.setEnabled(false);
				pausePlay.setEnabled(true);
				terminal.append("Infecting masters.....");
    			updateLastInputTerminal();
			}
		});
		
		startDDoS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				procGraphic.startDDos();
				startDDoS.setEnabled(false);
				ddosStarted = true;
				pausePlay.setEnabled(true);
				terminal.append("Starting DDoS.....");
    			updateLastInputTerminal();
			}
		});
		
		pausePlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (procGraphic.getStage() == ProcessingSimulation.STAGE_PAUSE) { //continue
					procGraphic.continueSimulation();
					if (!ddosStarted) startDDoS.setEnabled(true);
				}
				else { //pause
					procGraphic.pauseSimulation();
					startDDoS.setEnabled(false);
				}
			}
		});
		
		ping.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				procGraphic.pingFromUser();
				terminal.append("Ping command.....\n");
				terminal.append(">Sending ECHO REQUEST from USER to TARGET");
    			updateLastInputTerminal();
			}
		});
		
	}
	
	// bar for speed animation
	private JPanel generateSpeedBar() {
		JPanel cspeedBar = new JPanel(new GridLayout(1,1,3,3));
		cspeedBar.setBorder(BorderFactory.createTitledBorder("Speed for animation"));
		JSlider speedBar = new JSlider(JSlider.HORIZONTAL, 0, 3, 1);
		
		speedBar.setMajorTickSpacing(1);
		//speedBar.setMinorTickSpacing(10);
		speedBar.setPaintTicks(true);
		
		//Create the label table
		Hashtable labelTable = new Hashtable();
		labelTable.put( new Integer(0), new JLabel("SLOW") );
		labelTable.put( new Integer(1), new JLabel("NORMAL") );
		labelTable.put( new Integer(2), new JLabel("FAST") );
		labelTable.put( new Integer(3), new JLabel("ULTRA") );
		speedBar.setLabelTable(labelTable);
		speedBar.setPaintLabels(true);
		
		speedBar.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
		
		    JSlider source = (JSlider)e.getSource();
		    if (!source.getValueIsAdjusting()) {
		        int fps = (int)source.getValue();
		        if (fps == 0) { ProcessingSimulation.speedUp = 1; globalSpeedUpBar = 1; }
		        else if (fps == 1) { ProcessingSimulation.speedUp = 7; globalSpeedUpBar = 7; }
		        else if (fps == 2) { ProcessingSimulation.speedUp = 10; globalSpeedUpBar = 10; }
		        else if (fps == 3) { ProcessingSimulation.speedUp = 15; globalSpeedUpBar = 15; }
		    }
			}

			});
		
		cspeedBar.add(speedBar);
		return cspeedBar;
	}
	
	// bar for speed generating packages - packages/sec
	private JPanel generateSpeedGeneratingPackages() {
		JPanel cspeedBar = new JPanel(new GridLayout(1,1,3,3));
		cspeedBar.setBorder(BorderFactory.createTitledBorder("Speed for generating packages"));
		JSlider speedBar = new JSlider(JSlider.HORIZONTAL, 1, 5, 3);
		
		speedBar.setMajorTickSpacing(1);
		speedBar.setPaintTicks(true);
		speedBar.setPaintLabels(true);
		
		speedBar.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
		
		    JSlider source = (JSlider)e.getSource();
		    if (!source.getValueIsAdjusting()) {
		        int fps = (int)source.getValue();
		        switch (fps) {
			        case 1: globalGenPackagePerSec = 1; break;
			        case 2: globalGenPackagePerSec = 2; break;
			        case 3: globalGenPackagePerSec = 3; break;
			        case 4: globalGenPackagePerSec = 4; break;
			        case 5: globalGenPackagePerSec = 5; break;
			        default: globalGenPackagePerSec = 2; break;
		        }
		    }
			}
		});
		cspeedBar.add(speedBar);
		return cspeedBar;
	}
	
	private void configureNetworkByDefault() {
		procGraphic.makeNetworkDefault();
		runSimulation();
	}
	
	public ProcessingSimulation getProcessingSimulation() { return procGraphic; }
	
	public void showComputerDetails(Computer comp, int nodeID) {
		id_detail.setText(": " + nodeID);
		ipAddress_detail.setText(": " + comp.getIpAddress());
		type_detail.setText(": " + comp.getTypeString());
		ttl_detail.setText(": " + comp.getTTL());
		memory_detail.setText(": " + comp.getMemBuffSize());
		
		packages_received_detail.setText("RECEIVED PACKAGES\n" +"- Total number: "+ comp.getNumberOfPackagesReceived()+ "\n\n");
		packages_received_detail.append("- Still in memory:\n\n");
				
		int numPackRec = 1;
		List<Package> received = comp.getReceivedPackages();
		for (Package pack: received) {
			packages_received_detail.append("-------------PACKAGE "+numPackRec+"-------------\n");
			packages_received_detail.append("Sender IP: "+pack.getEdge().getNodeFrom().getComputer().getIpAddress()+"\n");
			packages_received_detail.append("Size: "+pack.getSize()+"\n");
			
			Date timeSentDate = new Date(pack.getTimeStartSending());
			DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
			String timeSent = formatter.format(timeSentDate);
			
			Date timeReceivedDate = new Date(pack.getReceivedTime());
			String timeReceived = formatter.format(timeReceivedDate);
			
			String packType = "";
			if (pack.getType() == Package.COMMAND) {
				CommandPacket comm = (CommandPacket)pack.getPacket();
				if (comm.getType() == CommandPacket.GEN_ECHO_REQ)
					packType = "Command - Generate ECHO REQUEST";
				else if (comm.getType() == CommandPacket.GEN_SYN)
					packType = "Command - Generate SYN";
				else if (comm.getType() == CommandPacket.INFECT)
					packType = "Commnad - Infect slave - email with virus";
			} 
			else if (pack.getType() == Package.EMAIL_VIRUS) {
				packType = "Email with virus";
			}
			else if (pack.getType() == Package.ICMP_PACKAGE) {
				ICMPpacket icmp = (ICMPpacket)pack.getPacket();
				if (icmp.getType() == ICMPpacket.ECHO_REPLY)
					packType = "ICMP packet - ECHO REPLY";
				else if (icmp.getType() == ICMPpacket.ECHO_REQUEST)
					packType = "ICMP packet - ECHO REQUEST";
			}
			else if (pack.getType() == Package.TCP_PACKAGE) {
				TCPpacket tcp = (TCPpacket)pack.getPacket();
				if (tcp.getType() == TCPpacket.SYN)
					packType = "TCP packet - SYN";
				else if (tcp.getType() == TCPpacket.ACK)
					packType = "TCP packet - ACK";
			}
			
			packages_received_detail.append("Time sent: " + timeSent + "\n");
			packages_received_detail.append("Time received: " + timeReceived + "\n");
			packages_received_detail.append("Package type: " + packType + "\n");
			packages_received_detail.append("------------------------------------\n");
			numPackRec++;
		}

		detailPanelVisible(true);
		historyPanelVisible(true);		
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