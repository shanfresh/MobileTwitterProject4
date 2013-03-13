package com.ict.twitter.MessageBusTest;

import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import com.ict.twitter.plantform.ConsoleShowBean;

public class JPanelInfo extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * This is the default constructor
	 */
	private JLabel jLabel2 = null;
	private JTextField jTextFieldUsers = null;
	private JLabel jLabel3 = null;
	private JTextField jTextFieldUserRelationship = null;
	private JLabel jLabel4 = null;
	private JLabel jLabel5 = null;
	private JTextField jTextFieldMsgRelationship = null;
	private JTextField jTextFieldMessage = null;
	private int x=0,y=0;

	private JTextArea jTextArea = null;  //  @jve:decl-index=0:visual-constraint="241,22"
	private JScrollPane jScrollPane = null;
	private JButton jButtonShowStatus=null;
	private ZPanel zPanel;
	public int status=0;
	private ConsoleShowBean csb;
	public JPanelInfo() {
		super();
		initialize();
	}
	public JPanelInfo(int _x,int _y){
		x=_x;
		y=_y;
		initialize();
	}
	public JPanelInfo(int _x,int _y,ConsoleShowBean csb){
		x=_x;
		y=_y;
		this.csb=csb;
		initialize();
	}
	public JPanelInfo(int _x,int _y,int _status){
		this(_x,_y);
		status=_status;
	}

	public void changeStatus(int newstatus){
		if(newstatus==status)
			return;
		else{
			getStatusPanel(newstatus);
			this.status=newstatus;
			this.invalidate();
		}
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(null);
		jLabel5 = new JLabel();
		jLabel5.setBounds(new Rectangle(12, 99, 55, 28));
		jLabel5.setText("Msg关系");
		jLabel4 = new JLabel();
		jLabel4.setBounds(new Rectangle(11, 60, 55, 28));
		jLabel4.setText("Message");
		jLabel3 = new JLabel();
		jLabel3.setBounds(new Rectangle(9, 174, 55, 28));
		jLabel3.setText("User关系");
		jLabel2 = new JLabel();
		jLabel2.setBounds(new Rectangle(12, 138, 52, 25));
		jLabel2.setText("Users");
		jButtonShowStatus=new JButton("StatusTwitterWEB");
		jButtonShowStatus.addActionListener(csb);
		jButtonShowStatus.setBounds(new Rectangle(50, 280, 94, 28));
		
		
		setLayout(null);
		setBounds(new Rectangle(x, y, 200, 316));
		
		add(jLabel2, null);
		add(getJTextFieldUsers(), null);
		add(jLabel3, null);
		add(getJTextFieldUserRelationship(), null);
		add(jLabel4, null);
		add(jLabel5, null);
		add(getJTextFieldMsgRelationship(), null);
		add(getJTextFieldMessage(), null);
		add(jButtonShowStatus,null);
		//this.add(getJTextArea(), null);
		this.add(getJScrollPane(), null);
		this.add(getStatusPanel(status), null);
	}
	private JPanel getStatusPanel(int _status){
		if (zPanel == null) {
			zPanel=new ZPanel();
			 String path="UsefulFile/Image/red.jpg";
			 String red="UsefulFile/Image/red.jpg";
			 String green="UsefulFile/Image/green.jpg";
			 String yellow="UsefulFile/Image/yellow.jpg";
			 switch (_status){
			 case 0:
				 path=red;
				 break;
			 case 1:
				 path=green;
				 break;
			 case 2:
				 path=yellow;
				 break;
			 case 3:
				 path=yellow;
				 break;
				 
			 }
	        zPanel.setImagePath(path);  
	        zPanel.setPreferredSize(new Dimension(50, 50)); 	  
	        zPanel.setBounds(new Rectangle(73, 3, 50, 50));
	        
		}else{
			this.remove(zPanel);
			zPanel=null;
			getStatusPanel(_status);
			this.add(zPanel);
			System.out.println("new Status"+_status);
		}
		this.invalidate();
		this.repaint();
		return zPanel;
	}
	
	public JTextField[] getAllTextField(){
		JTextField[] all=new JTextField[4];
		all[0]=jTextFieldMessage;
		all[1]=jTextFieldMsgRelationship;
		all[2]=jTextFieldUsers;
		all[3]=jTextFieldUserRelationship;
		return all;
	}
	
	public void setValue(int [] value){
		if(value.length!=4){
			System.err.println("error value 不是4 的数组");
			return;
		}else{
			JTextField[] all=getAllTextField();
			for(int i=0;i<4;i++){
				all[i].setText(Integer.toString(value[i]));
			}
			
		}
		this.invalidate();
		this.repaint();
	}
	
	
	private JTextField getJTextFieldUsers() {
		if (jTextFieldUsers == null) {
			jTextFieldUsers = new JTextField();
			jTextFieldUsers.setBounds(new Rectangle(75, 138, 94, 28));
		}
		return jTextFieldUsers;
	}
	private JTextField getJTextFieldMsgRelationship() {
		if (jTextFieldMsgRelationship == null) {
			jTextFieldMsgRelationship = new JTextField();
			jTextFieldMsgRelationship.setBounds(new Rectangle(75, 96, 94, 37));
		}
		return jTextFieldMsgRelationship;
	}
	private JTextField getJTextFieldUserRelationship() {
		if (jTextFieldUserRelationship == null) {
			jTextFieldUserRelationship = new JTextField();
			jTextFieldUserRelationship.setBounds(new Rectangle(75, 174, 94, 28));
		}
		return jTextFieldUserRelationship;
	}
	/**
	 * This method initializes jTextFieldMessage	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldMessage() {
		if (jTextFieldMessage == null) {
			jTextFieldMessage = new JTextField();
			jTextFieldMessage.setBounds(new Rectangle(75, 63, 94, 28));
		}
		return jTextFieldMessage;
	}
	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setText("等待消息");
		}
		return jTextArea;
	}
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setBounds(new Rectangle(15, 214, 157, 50));
			jScrollPane.setViewportView(getJTextArea());
		}
		return jScrollPane;
	}
	
	
	
}  //  @jve:decl-index=0:visual-constraint="13,7"
