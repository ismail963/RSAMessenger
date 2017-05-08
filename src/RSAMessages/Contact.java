package RSAMessages;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * @class Contact
 * @brief meant to save contact informations, to exchange public keys and saving it,
 * 		   and to exchange encrypted messages and saving it
 */

public class Contact implements Runnable{

	public static Key key;
	public static final String path="/home/etudiant/workspace/RSAMessages/src/RSAMessages/Photos/";
	private static int cmp=0;
	
	public boolean sendfirst=false;
	public boolean active=true;
	
	private Messenger messenger;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private Key public_key;
	private ArrayList<String> messages;
	private int id;
	
	private String name, sexe, file_path;

	/* * * * * * * * *
	* CONSTRUCTORS  *
	* * * * * * * * */
	
	/**
	* Constructor with parameters
	* @brief save the address of the client 
	* @param s: socket of the contact
	* @param m: is the instance of the messenger server
	*/	
	public Contact(Socket s, Messenger m) throws IOException{
		id=cmp;
		socket = s;
		sexe="mf";
		name="";
		file_path="";
		messenger=m;
		in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out=new PrintWriter(socket.getOutputStream(),true);
		messages=new ArrayList<String>();
		cmp++;
	}
	
	/* * * * * * * * * * * * * *
	* ACCESSORS  AND MUTATORS *
	* * * * * * * * * * * * * */
	
	/**
	* @brief getter of the name or address of the contact
	*/
	public String getName(){
		if(name!=null && !name.equals("") && !name.trim().equals("") ) return name;
		return ""+socket.getInetAddress()+":"+socket.getPort();
	}
	
	/**
	* @brief getter of the communications between the server and this contact  
	*/
	public ArrayList<String> getMessages() {
		return messages;
	}
	
	/**
	* @brief getter of the id of this contact  
	*/
	public int getId() {
		return id;
	}
	
	/**
	* @brief getter of the id of this contact  
	*/
	
	public String getSexe() {
		return sexe;
	}
	
	/**
	* @brief getter of socket
	*/
	public Socket getSocket() {
		return socket;
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
	
	
	/* * * * * *
	 * METHODS *
	 * * * * * */
	

	/**
	* @brief function to send an message to this contact
	* @param msg: string of the message to send to contact 
	*/	
	public void sendMessage(String msg){
		messages.add("<span color=\"red\">Me: "+msg+"<span/>");
		out.println(public_key.encrypt(msg));
		System.out.println("-- Messagge sent :"+public_key.encrypt(msg));
		messenger.getWindow().notifyMessagesChange(id);
	}

	/**
	* @brief function to send the photo of profile
	* @return boolean 
	*/	
	public boolean sendPhoto() throws IOException {
		 File myFile = new File (messenger.file_path);
		 out.print(myFile.length());
		 byte [] mybytearray  = new byte [1024];
		 FileInputStream fis = new FileInputStream(myFile);
		 BufferedInputStream bis = new BufferedInputStream(fis);
		 OutputStream os =  socket.getOutputStream();
		 int trxBytes =0;
		 while((trxBytes = bis.read(mybytearray, 0, mybytearray.length)) !=-1){           
	        os.write(mybytearray, 0, mybytearray.length);
	      }
		 fis.close();
		 bis.close();
		 try {
			Thread.sleep(1000);
			os.write("end".getBytes(), 0, "end".getBytes().length);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 System.out.println("end sending");
		 return true;
	}

	/**
	* @brief function to receive the photo of profile
	* @return boolean 
	*/	
	public boolean recievePhoto() throws IOException{
		 long FILE_SIZE = (long) 621544999; 
		 file_path=path+name+".png";
		 File myFile=new File(file_path);
		 byte [] mybytearray  = new byte [1024];
		 byte [] ex = " ".getBytes();
	     InputStream is = socket.getInputStream();
	     int byteRead =0;
	     OutputStream os = new FileOutputStream(myFile);
		 while((byteRead = is.read(mybytearray, 0, mybytearray.length))!= -1)
         {
			 String msg=new String(Arrays.copyOfRange(mybytearray, 0, byteRead));
			 if(msg.equals("end")) {
				 break;
			 }
             os.write(mybytearray, 0, byteRead);
         }
	     os.close();
	     return true;
	}

	/**
	* @brief function to send informations of the users before begin chat 
	*/
	public void sendPrimaryInformation() throws IOException{
		String msg;
		if(sendfirst) {
			out.println(key.getPublicKey());
	    	msg=in.readLine();
	    	System.out.println("Key received");
	    	public_key=new Key(msg);
	    	out.println(messenger.getName());
	    	msg=in.readLine();
	    	name=msg;
	    	System.out.println("Name received "+msg);
	    	out.println(messenger.getSexe());
	    	String s=in.readLine();
	    	System.out.println("Sexe received "+s);
	    	sexe=s;
	    	System.out.println("sending photo ..."+messenger.file_path);
	    	if(messenger.havePhoto()) out.println("1");
	    	else out.println("0");
	    	msg=in.readLine();
	    	boolean havePhoto= (Integer.parseInt(msg)==1);
	    	if(messenger.havePhoto()) sendPhoto();	    	
	    	if(havePhoto) recievePhoto();
	    	out.println("end");
	    	msg=in.readLine();
	    		
		}
		else {
	    	msg=in.readLine();
	    	System.out.println("Key received");
	    	public_key=new Key(msg);
	    	out.println(key.getPublicKey());
	    	msg=in.readLine();
	    	System.out.println("Name received "+msg);
	    	name=msg;
	    	out.println(messenger.getName());
	    	String s=in.readLine();
	    	System.out.println("Sexe received "+s);
	    	sexe=s;
	    	out.println(messenger.getSexe());
	    	msg=in.readLine();
	    	boolean havePhoto= (Integer.parseInt(msg)==1);
	    	if(messenger.havePhoto()) out.println("1");
	    	else out.println("0");
	    	if(havePhoto) recievePhoto();
	    	if(messenger.havePhoto()) sendPhoto();
	    	msg=in.readLine();
	    	out.println("end");
			
		}
	}

	/**
	* @brief function to get the path of the image of this contact
	* @return String
	*/
	public String getPathImage(){
		if(!file_path.equals("")) {
			return name+".png";
		}
		else return sexe+".png";
	}
	
	/**
	* @brief run the thread and wait for the messages sent from the contact and print it
	*/	
	public void run() {
        try {
        	String msg;
        	this.sendPrimaryInformation();
			messenger.getWindow().notifyContactChange();
			messenger.getWindow().notifyMessagesChange(id);
    		while(messenger.active && active && !socket.isClosed()){
					msg=in.readLine();
					if("|end|".equals(key.decrypt(msg))) break;
					messages.add("<span color=\"green\">"+getName()+": "+key.decrypt(msg)+"<span/>");
					messenger.getWindow().notifyMessagesChange(id);
    		}        
        } catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
