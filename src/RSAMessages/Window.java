package RSAMessages;
import javax.swing.*;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.io.File;     

/**
 * @class Window
 * @brief meant to be the interface of the application
 */

public class Window extends JFrame implements Messenger.InterfaceCallBacks{
	
	private JButton  send;
	private JTextArea  message;
	private JEditorPane messages;
	private JMenuBar menu;
	private JLabel name, sexe;
	private JRadioButton h,f;
	private Messenger messenger;
	private Contact c;
	private JPanel panel_list_contacts;
	private ArrayList<JButton> b_contacts;
	private int selected_one=-1;
	
	/* * * * * * * * *
	* CONSTRUCTORS  *
	* * * * * * * * */
	
	/**
	* Constructor with parameters
	* @brief execute the server and show the interface
	* @param p: an integer of the port of the server
	*/	
	public Window(int p) throws IOException{
		 messenger =new Messenger(p,this);
		 this.setTitle("Messenger "+p);
		 this.draw();
		 this.move(0, 0);
	}
	
	/* * * * * *
	 * METHODS *
	 * * * * * */
	
	/**
	* @brief method to construct the different panels of the window
	*/	
	public void draw(){
		Thread t = new Thread(messenger);
		t.start();
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	ArrayList<Contact> cc=messenger.getContacts();
		    	for(int i=0;i<cc.size();i++) {
		    		cc.get(i).sendMessage("|end|");
		    		cc.get(i).active=false;
		    	}
		    	messenger.active=false;
		    	System.exit(0);
		    }
		});
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(screenSize.width/2, screenSize.height);      
		messages = new JEditorPane();
		name= new JLabel(messenger.getName());
		sexe=new JLabel("");
		JPanel pan = new JPanel();
		pan.setBackground(Color.white);  
		pan.setLayout(new BorderLayout());
		this.create_menu();
		pan.add(this.panel_messages(), BorderLayout.CENTER);
		
		JPanel p_right= new JPanel();
		p_right.setLayout(new BorderLayout());
		p_right.add(this.panel_list_contacts(), BorderLayout.NORTH);
		p_right.add(this.panel_info_user(), BorderLayout.SOUTH);
		
		pan.add(p_right, BorderLayout.EAST);
		pan.add(this.send_message(), BorderLayout.SOUTH);
		this.getContentPane().add(pan);	
		this.setVisible(true);
	}
	
	/**
	* @brief create the menu
	*/
	public void create_menu(){
		menu =new JMenuBar();
		JMenu cnx = new JMenu("Connexion");
		cnx.addMenuListener(new MenuListener() { 
				 public void menuSelected(MenuEvent e) { 
						try {
							  String result= JOptionPane.showInputDialog(null, "Adress ip and port ex: 127.0.0.1:1995");
							  String [] adr = (result==null)? null: result.split(":");
							  if(adr!=null && adr.length==2){
								  Contact tmpc=messenger.add_connect(adr[0].trim(), 
										  Integer.parseInt(adr[1].trim()));
								  if(tmpc!=null) {
									  c=tmpc;
									  showDialog("Connection succeeded with "+c.getName());
								  }
								  else showDialog("Connection faild ");  
							  }
							  else showDialog("Argument error");  
						} catch (IOException e1) {
							e1.printStackTrace();
						}
				  }

				public void menuCanceled(MenuEvent arg0) {	
				}
				public void menuDeselected(MenuEvent arg0) {	
				}
		} );
		JMenu profil=new JMenu("Profil");
		JMenuItem Mname = new JMenuItem("First & Last name");
		Mname.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String m_name= JOptionPane.showInputDialog(null, 
						  "Enter youre name please");
				messenger.setName(m_name);
				name.setText(m_name);
			}
		});
		JMenuItem  Msexe = new JMenuItem("Gender");
		
		Msexe.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				messenger.setSexe("m");
				sexe.setText("Male");
				JOptionPane.showConfirmDialog(null,
						selectSexe(),
                        "",
                        JOptionPane.OK_OPTION);
			} 

		} );
		final JFrame obj=this;
		JMenuItem Mphoto = new JMenuItem("Photo");
		Mphoto.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				if (fileChooser.showOpenDialog(obj) == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					System.out.println("Selected file: " + selectedFile.getAbsolutePath());
					messenger.file_path=selectedFile.getAbsolutePath();
				}
			} 

		} );
		profil.add(Mname);
		profil.add(Mphoto);
		profil.add(Msexe);
		
		
		menu.add(cnx);
		menu.add(profil);
		this.setJMenuBar(menu);
	}
	
	/**
	* @brief method to construct messages panel to show the conversations 
	*/
	public JPanel panel_messages(){
		JPanel panel = new JPanel();
		messages.setContentType( "text/html" ); 
		messages.setEditable(false);
		panel.setBorder(BorderFactory.createTitledBorder("Messages")); 
		panel.setLayout(new GridLayout(0,1));
		panel.add(messages);
		return panel;
	}

	/**
	* @brief method to construct the panel of list of contacts connected to this server
	*/
	public JPanel panel_list_contacts(){
		panel_list_contacts = new JPanel();
		
		panel_list_contacts.setBorder(BorderFactory.createTitledBorder("You're contacts"));
		panel_list_contacts.setLayout(new GridLayout(10,1));
		
		b_contacts = new ArrayList<JButton>();
		
		return panel_list_contacts;
	}
	
	/**
	* @brief method to show the information about the user
	*/
	public JPanel panel_info_user(){
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("You're information"));
		panel.setLayout(new GridLayout(2,2));
		panel.add(new JLabel("Name : "));
		panel.add(name);
		panel.add(new JLabel("Sexe : "));
		panel.add(sexe);
		return panel;
	}
	
	/**
	* @brief panel of text area to write an message to an contact 
	*/
	public JPanel send_message(){
		JPanel panel = new JPanel();
		message = new JTextArea();
		send= new JButton("Send");
		send.setEnabled(false);
		send.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  c.sendMessage(message.getText());
				  message.setText("");
			  } 
		} );
		panel.setBorder(BorderFactory.createTitledBorder("Send Message")); 
		panel.setLayout(new BorderLayout());
		panel.add(message, BorderLayout.CENTER);
		panel.add(send, BorderLayout.EAST);
		return panel;
	}

	/**
	* @brief panel to select the sexe of the user
	*/
	public JPanel selectSexe(){
		JPanel panel=new JPanel();
		h = new JRadioButton("Male", true);
		f= new JRadioButton("Female");
		h.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				f.setSelected(false);
				messenger.setSexe("m");
				sexe.setText("Male");
			}
		});
		f.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				h.setSelected(false);
				messenger.setSexe("f");
				sexe.setText("Female");
			}
		});
		panel.add(h); panel.add(f);
		return panel;
	}
	
	/**
	* @brief method to update the conversation 
	*/
	public void notifyMessagesChange(int id){
		if (c.getId()==id){
			messages.setContentType("text/html"); 
			ArrayList<String> msg=c.getMessages();
			String msgs="<html><head><head><body>";
			for(int i=0;i<msg.size();i++) {
				msgs+=msg.get(i)+"<br/>";
			}
			msgs+="</body></html>";
			messages.setText(msgs);
		}
	}
	
	/**
	* @brief method to update the conversation when choosing different contact
	*/
	public void notifyContactChange() {
		ArrayList<Contact> cs= messenger.getContacts();
		//if(panel_list_contacts.countComponents()<cs.size()){
			panel_list_contacts.removeAll();
			for(int i=0;i<cs.size();i++) { 
				ImageIcon icon = new ImageIcon(Contact.path+cs.get(i).getPathImage());
				Image img = icon.getImage() ;  
				Image newimg = img.getScaledInstance( 150, 150,  java.awt.Image.SCALE_SMOOTH ) ;  
				icon = new ImageIcon( newimg );
				JButton b= new JButton(cs.get(i).getName(), icon);
				b_contacts.add(b);
				final int index= i;
				b.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						if(selected_one!=-1){
							b_contacts.get(index).setBackground(null);
						}
						selected_one=index;
						c=messenger.getContacts().get(index);
						b_contacts.get(index).setBackground(Color.RED);
						notifyMessagesChange(c.getId());
					}
				});
				panel_list_contacts.add(b);
			}
		//}
		if(cs.size()>0 && !send.isEnabled() ) send.setEnabled(true); 
		panel_list_contacts.repaint();
	}
	
	/**
	* @brief function to show an notification
	*/
	public void showDialog(String msg){
		JOptionPane.showMessageDialog(null, msg);
	}
	
	/**
	* @brief function to change the actual contact when user click in another contact
	*/
	public void setActualContact(Contact c){
		this.c=c;
	}
	
	/**
	* @brief method to accept contact how want to connect with this user
	*/
	public boolean acceptContact(Socket s){
		if (JOptionPane.showConfirmDialog(null, "Do you want to add "+s.getLocalAddress()+" to your contacts ?") == JOptionPane.YES_OPTION) {
			return true;
		}
		return false;
	}
	
	/**
	* @brief the main program to execute the interface and server
	*/
	public static void main(String[] args) {
		try {
			new Window(1994);
			new Window(1995);
			//new Window(1996);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
