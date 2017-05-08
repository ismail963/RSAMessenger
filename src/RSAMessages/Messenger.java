package RSAMessages;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * @class Messenger
 * @brief meant to be the server to receive demands of 
 *        connections, to manage all contacts of 
 *        this machine, and to connect with other servers
 */

public class Messenger implements Runnable{

	public ServerSocket socket;
	private Key key;
	private ArrayList<Contact> contacts;
	private String name, sexe;
	public String file_path;
	public boolean active;
	
    static interface InterfaceCallBacks {
        public void notifyContactChange();
        public void showDialog(String msg);
		public void setActualContact(Contact c);
		public void notifyMessagesChange(int id); 
		public boolean acceptContact(Socket s);
    }
    private InterfaceCallBacks window = null;
    
	
	/* * * * * * * * *
	* CONSTRUCTORS  *
	* * * * * * * * */
	
	/**
	* Constructor with parameters
	* @brief generate new keys (public and private) this is means that it's a client
	* @param port: an integer of the port that the server should connect in
	* @param w: is the interface widow of the app 
	*/	
	public Messenger(int port, Object w) throws IOException{
		socket = new ServerSocket(port);
		System.out.println(socket.getInetAddress()+" port="+socket.getLocalPort());
		name="";
		file_path="";
		sexe="mf";
		window=(InterfaceCallBacks) w;
	}	
	
	/* * * * * * * * * * * * * *
	* ACCESSORS  AND MUTATORS *
	* * * * * * * * * * * * * */
	
	/**
	* @brief getter of the list of contacts
	*/
	public ArrayList<Contact> getContacts() {
		return contacts;
	}

	/**
	* @brief setter of the list of contacts
	* @param contacts: list of contacts 
	*/
	public void setContacts(ArrayList<Contact> contacts) {
		this.contacts = contacts;
	}
	
	
	/**
	* @brief getter of the window of the app
	*/
	public InterfaceCallBacks getWindow() {
		return window;
	}

	/**
	* @brief getter of the id of this contact  
	*/
	public String getSexe() {
		return sexe;
	}

	/**
	* @brief setter of sexe
	*/
	public void setSexe(String sexe) {
		this.sexe = sexe;
	}

	/**
	* @brief seter of the name 
	*/
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	* @brief getter of the name 
	*/
	public String getName(){
		//if(name!=null && name.trim()!="") return
		return name;
	}
	
	/* * * * * *
	 * METHODS *
	 * * * * * */

	/**
	* @brief generate new key (public and private) and with for contacts to connect into this server
	*/	
	public void drive() throws IOException{
		active=true;
		key=new Key();
		Contact.key=key;
		contacts = new ArrayList<Contact>();
		while(active) {
			Socket s =socket.accept();
			if(window.acceptContact(s)) {
				Contact c= new Contact(s,this);
				contacts.add(c);
				window.showDialog(c.getName()+" is connected with you now ");
				window.setActualContact(c);
				window.notifyContactChange();
				Thread t = new Thread(c);
				t.start();
			}
			//else s.close();
		}
	}
	
	/**
	* @brief run the server
	*/	
	public void run(){
		try {
			this.drive();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	* @brief function to connect with another server using its address
	* @param adr: string of the address IP of the server 
	* @param port: an integer representing the port that the server run on
	*/
	public Contact add_connect(String adr, int port) throws IOException{
			Socket s=new Socket(adr, port);
			Contact c= new Contact(s,this);
			c.sendfirst=true;
			contacts.add(c);
			window.setActualContact(c);
			Thread t = new Thread(c);
			t.start();
			window.notifyMessagesChange(c.getId());
			window.notifyContactChange();
			return c;	
	}
	
	public boolean havePhoto(){
		return !file_path.equals("");
	}
	

}