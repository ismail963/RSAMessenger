package RSAMessages;
import java.math.BigInteger;
import java.util.Random;
import java.util.ArrayList;

/**
 * @class Key
 * @brief meant to generate both keys public and private 
 *        or just create an public key send by another 
 *        client to encrypt the messages  
 */

public class Key {

	private BigInteger p , q , n , m, u;
	private int e;
	
	/* * * * * * * * *
	* CONSTRUCTORS  *
	* * * * * * * * */
	
	/**
	* Default constructor
	* @brief generate new keys (public and private) this is means that it's a client
	*/	
	public Key(){
		this.calculatPAndQ();
		this.CalculOfPrivateKey();
		this.test();
	}

	/**
	* Constructor with parameters
	* @brief Set n and e to generate the public key
	* @param publicKey : string that contents the pair n,e required to encrypt messages
	*/
	public Key(String publicKey) throws NumberFormatException{
		String[] value= publicKey.split(" ");
		n = new BigInteger(value[0]);
		if(value[1].trim().matches("-?\\d+")) e= Integer.parseInt(value[1].trim());
	}

	/* * * * * * * * * * * * * *
	* ACCESSORS  AND MUTATORS *
	* * * * * * * * * * * * * */

	/**
	* @brief getter of N 
	*/
	public BigInteger getN(){
		return n;
	}

	/**
	* @brief getter of e
	*/
	public int getE(){
		return e;
	}

	/**
	* @brief getter of public key
	*/
	public String getPublicKey(){
		return n.toString()+" "+e;
	}


	/* * * * * *
	 * METHODS *
	 * * * * * */
	
	/**
	 * @brief function to encrypt an message
	 * @param message : the message that we want to encrypt 
	 * @return String of the message encrypted using public key
	 */	
	public String encrypt(String message){
		String c="";
		char [] array= message.toCharArray();
		for(int i=0;i<array.length;i++){
			BigInteger result=new BigInteger(""+((int) array[i]));
			c+=result.modPow(new BigInteger(""+e), n).toString();
			if(i<array.length-1) c+=" ";
		}
		return c;		
	}

	/**
	 * @brief function to decrypt an message
	 * @param message : the message encrypted 
	 * @return String of the message decrypted using private key
	 */	
	public String decrypt(String message) {
		//System.out.println(message);
		if(m==null) return "ERROR :cant calculate the private key without p and q";  
		else if(message!=null && !message.trim().equals("")){
			String c="";
			String [] array= message.split(" ");
			for(int i=0;i<array.length;i++){
				BigInteger result=new BigInteger(array[i].trim());
				c+=Character.toString ((char) Integer.parseInt(result.modPow(u, n).toString()));
			}
			return c;		
		}
		return "";
	}
	/**
	 * @brief function to chose randomly p and q
	 */	
	public void calculatPAndQ(){
		 Random rand = new Random();
		 p = BigInteger.probablePrime((90+(int)(Math.random() * 120)), rand);
		 q = BigInteger.probablePrime((90+(int)(Math.random() * 120)), rand);
		 n = p.multiply(q);
		 m = (p.subtract(new BigInteger("1"))).multiply(q.subtract(new BigInteger("1")));
		 u=null;
		 BigInteger nbr = new BigInteger("1");
		 while(true){			
			nbr = nbr.nextProbablePrime();
			if(nbr.gcd(m).intValue()==1) {
				e=nbr.intValue();
				break;
			}		
		 }
	}
	
	/**
	 * @brief function to test if the private key works 
	 */	
	public void test(){
		boolean test_valide=false;
		while(!test_valide){
			if(!("test").equals(this.decrypt(this.encrypt("test")))){
				this.calculatPAndQ();
				this.CalculOfPrivateKey();
			}
			else test_valide=true;
		}
	}
	
	/**
	 * @brief function to calculate private key using p and q
	 */	
	public void CalculOfPrivateKey(){
		if(m==null) System.out.println("cant calculate the private key without p and q");   
		else if(u==null) {
			BigInteger r = new BigInteger(""+e);
			// r
			ArrayList<BigInteger> r_array = new ArrayList<BigInteger>();
			r_array.add(r);
			r_array.add(m);
			// u
			ArrayList<BigInteger> u_array = new ArrayList<BigInteger>();
			u_array.add(BigInteger.ONE);
			u_array.add(BigInteger.ZERO);
			// v
			ArrayList<BigInteger> v_array = new ArrayList<BigInteger>();
			v_array.add(BigInteger.ZERO);
			v_array.add(BigInteger.ONE);
			int i=1;
			while (r.compareTo(BigInteger.ZERO) !=0 ){			 
				 r = r_array.get(i); 
				 u_array.add(u_array.get(i-1).subtract((r_array.get(i-1).divide(r)).multiply(u_array.get(i))));
				 v_array.add(v_array.get(i-1).subtract((r_array.get(i-1).divide(r)).multiply(v_array.get(i))));	 
				 r = r_array.get(i-1).subtract((r_array.get(i-1).divide(r)).multiply(r));
				 r_array.add(r);
				 i++;
			}
			
			for(i=u_array.size()-1;i>=0;i--) {
				if(u_array.get(i).compareTo(new BigInteger("2"))==1 && u_array.get(i).compareTo(m)==-1) { 
					u=u_array.get(i);
				}
			}
			if(u==null){
				for(i=u_array.size()-1;i>=0;i--) {
					if(u_array.get(i).compareTo(m)==-1) {
						BigInteger u_now = u_array.get(i);
						int k=-1;
						while(u_now.compareTo(new BigInteger("2"))!=1){
							u_now = u_array.get(i);
							u_now=u_now.add(m.multiply(new BigInteger(""+(k*-1))));
							k--;
						}
						u=u_now;
						break;
					}
				}
			}
		}
		else System.out.println("Already calculated");
	}
	
}
