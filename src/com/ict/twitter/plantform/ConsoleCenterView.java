package com.ict.twitter.plantform;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import com.ict.twitter.MessageBusTest.JPanelInfo;
import com.ict.twitter.MessageBusTest.ZPanel;

public class ConsoleCenterView extends JFrame implements ActionListener,WindowListener{
	private ConsoleShowBean csb;
	
	
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel11 = null;
	private JLabel jLabel111 = null;
	
	public JLabel jLabelStatus=null;
	public  JButton jButtonStart = null;
	public JButton jButtonPause = null;
	public JButton jButtonResume = null;
	public JButton jButtonStop = null;
	public JButton jButtonRestart = null;
	
	private JPanel allNode[];
	int[] AllNodeSignal={1,1,1,1};
	private JPanelInfo jPaneInfoTwitterAPI = null;
	private JPanelInfo jPaneInfoTwitterWEB = null;
	private JPanelInfo jPaneInfoFaceBookAPI = null;
	private JPanelInfo jPaneInfoFaceBookWEB = null;
	/**
	 * This method initializes jPaneInfo	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPaneInfoTwitterAPI(int x,int y) {
		jPaneInfoTwitterAPI=new JPanelInfo(x,y);
		return jPaneInfoTwitterAPI;
		
	}
	private JPanel getJPaneInfoTwitterWEB(int x,int y) {
		jPaneInfoTwitterWEB=new JPanelInfo(x,y,csb);
		return jPaneInfoTwitterWEB;
		
	}
	private JPanel getJPaneInfoFaceBookAPI(int x,int y) {
		jPaneInfoFaceBookAPI=new JPanelInfo(x,y);
		return jPaneInfoFaceBookAPI;
		
	}
	private JPanel getJPaneInfoFaceBookWEB(int x,int y) {
		jPaneInfoFaceBookWEB=new JPanelInfo(x,y);
		return jPaneInfoFaceBookWEB;
		
	}




	/**
	 * This is the default constructor
	 */
	public static void main(String[] args){
		new ConsoleCenterView();
		
	}
	
	public ConsoleCenterView(ConsoleShowBean _csb) {
		super();
		csb=_csb;
		initialize();
	}
	public ConsoleCenterView() {
		super();
		csb=null;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(800, 600);
		this.setContentPane(getJContentPane());
		this.setTitle("JFrame");
		Toolkit kit = Toolkit.getDefaultToolkit();  
        Dimension screenSize = kit.getScreenSize();  
        int screenHeight = screenSize.height;  
        int screenWidth = screenSize.width;  
        int frameH = getHeight();  
        int frameW = getWidth();  
        setLocation((screenWidth - frameW) / 2,  
                (screenHeight - frameH) / 2); 

        setVisible(true);
        this.addWindowListener(this);
        
        
        
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabel111 = new JLabel();
			jLabel111.setBounds(new Rectangle(637, 16, 120, 40));
			jLabel111.setText("FacebookWEB");
			jLabel111.setFont(new Font("Dialog", Font.BOLD, 14));
			jLabel11 = new JLabel();
			jLabel11.setBounds(new Rectangle(421, 16, 120, 40));
			jLabel11.setText("FacebookAPI");
			jLabel11.setFont(new Font("Dialog", Font.BOLD, 14));
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(223, 16, 120, 40));
			jLabel1.setText("TwitterWEB");
			jLabel1.setFont(new Font("Dialog", Font.BOLD, 14));
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(20, 16, 120, 40));
			jLabel.setFont(new Font("Dialog", Font.BOLD, 14));
			jLabel.setText("TwitterAPI");
			
			jLabelStatus=new JLabel();
			jLabelStatus.setBounds(new Rectangle(20,400,480,40));
			jLabelStatus.setFont(new Font("Dialog", Font.BOLD, 12));
			jLabelStatus.setText("×´Ì¬");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(jLabel, null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(jLabel11, null);
			jContentPane.add(jLabel111, null);
			jContentPane.add(jLabelStatus,null);
			jContentPane.add(getJButtonStart(), null);
			jContentPane.add(getJButtonPause(), null);
			jContentPane.add(getJButtonResume(), null);
			jContentPane.add(getJButtonStop(), null);
			jContentPane.add(getJButtonRestart(), null);
			jContentPane.add(getJPaneInfoTwitterAPI(9, 50),null);
			jContentPane.add(getJPaneInfoTwitterWEB(200, 50),null);
			jContentPane.add(getJPaneInfoFaceBookAPI(400, 50),null);
			jContentPane.add(getJPaneInfoFaceBookWEB(600, 50),null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jButtonStart	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonStart() {
		if (jButtonStart == null) {
			jButtonStart = new JButton();
			jButtonStart.setBounds(new Rectangle(14, 493, 142, 58));
			jButtonStart.setText("Æô¶¯CrawlServer");
			jButtonStart.setEnabled(false);
			jButtonStart.addActionListener(csb);
			
			
		}
		return jButtonStart;
	}


	/**
	 * This method initializes jButtonPause	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonPause() {
		if (jButtonPause == null) {
			jButtonPause = new JButton();
			jButtonPause.setBounds(new Rectangle(250, 493, 142, 58));
			jButtonPause.setText("ÔÝÍ£CrawlerServer");
			jButtonPause.addActionListener(csb);
			jButtonPause.setEnabled(true);
		}
		return jButtonPause;
	}

	/**
	 * This method initializes jButtonResume	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonResume() {
		if (jButtonResume == null) {
			jButtonResume = new JButton();
			jButtonResume.setBounds(new Rectangle(461, 493, 136, 52));
			jButtonResume.setText("»Ö¸´CrawlerServer");
			jButtonResume.setEnabled(true);
			jButtonResume.addActionListener(csb);
		}
		return jButtonResume;
	}

	/**
	 * This method initializes jButtonStop	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonStop() {
		if (jButtonStop == null) {
			jButtonStop = new JButton();
			jButtonStop.setBounds(new Rectangle(644, 494, 136, 49));
			jButtonStop.setText("Í£Ö¹CrawlerServer");
			jButtonStop.setEnabled(false);
			jButtonStop.addActionListener(csb);
		}
		return jButtonStop;
	}
	private JButton getJButtonRestart(){
		if (jButtonRestart == null) {
			jButtonRestart = new JButton();
			jButtonRestart.setBounds(new Rectangle(644, 440, 136, 49));
			jButtonRestart.setText("ÖØÆôCrawlerServer");
			jButtonRestart.setEnabled(true);
			jButtonRestart.addActionListener(csb);			
		}
		return jButtonRestart;
	}
	
	private JPanel getPanel(String name,int x,int y){
        ZPanel zPanel;
        zPanel = new ZPanel();  
        zPanel.setImagePath("F://red.jpg");  
        zPanel.setPreferredSize(new Dimension(50, 50)); 	  
        zPanel.setBounds(x, y, 50, 50);
        return zPanel;
	}
	private void getSignal(int signal[]){
		int x=20;
		int y=50;
		for(int i=0;i<signal.length;i++){
	        ZPanel zPanel;
	        zPanel = new ZPanel();
	        String path="F://red.jpg";
	        if(signal[i]==1){
	        	path="F://red.jpg";
	        }else if(signal[i]==2){
	        	path="F://green.jpg";
	        	
	        }else if(signal[i]==3){

	        	path="F://yellow.jpg";
	        }
	        zPanel.setImagePath(path);  
	        zPanel.setPreferredSize(new Dimension(50, 50)); 	  
	        zPanel.setBounds(x, y, 50, 50);
	        x+=210;
	        allNode[i]=zPanel;
	        
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equalsIgnoreCase("Æô¶¯")){
			if(jPaneInfoTwitterAPI.status==1){
				JOptionPane.showMessageDialog(this, "ÒÑ¾­Æô¶¯");
			}
			jPaneInfoTwitterAPI.changeStatus(1);
			int value[]={100,100,100,50};
			jPaneInfoTwitterAPI.setValue(value);
			
		}else if(e.getActionCommand().equalsIgnoreCase("ÔÝÍ£")){
			
			
		}else if(e.getActionCommand().equalsIgnoreCase("Í£Ö¹")){
		
		}else if(e.getActionCommand().equalsIgnoreCase("»Ö¸´")){
			
		}
		
	}
	public JButton[] getButtons(){
		JButton jbutton[]=new JButton[4];
		jbutton[0]=getJButtonStart();
		jbutton[1]=getJButtonPause();
		jbutton[2]=getJButtonResume();
		jbutton[3]=getJButtonStop();		
		return jbutton;
		
	}
	public void EnableButton(){
		JButton allButton[]=getButtons();
		for(int i=0;i<allButton.length;i++){
			allButton[i].setEnabled(true);
		} 
	}
	
	
	public void rePaintSignal(int[] signal){
		AllNodeSignal=signal;
		if(allNode!=null)
			for(int i=0;i<allNode.length;i++){
				jContentPane.remove(allNode[i]);
			}
			allNode=new JPanel[4];			
			getSignal(AllNodeSignal);
			for(int i=0;i<allNode.length;i++){
				jContentPane.add(allNode[i], null);
			}
			this.invalidate();
			this.repaint();
			System.out.println("!!!!!!!!!");
		
	}
	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
		
	}
	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		int res=JOptionPane.showConfirmDialog(this, "Òª¹Ø±ÕÂð£¿", "ÌáÊ¾", JOptionPane.YES_NO_OPTION );
		if(res==0)
			System.exit(0);
	}
	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	public void TwitterAPIShow(int[] value){
		jPaneInfoTwitterAPI.setValue(value);
	}

	

}
