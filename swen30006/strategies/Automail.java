package strategies;

import java.util.ArrayList;

import automail.Carrier;
import automail.IMailDelivery;
import automail.Robot;

public class Automail {
	      
	public ArrayList<Carrier> carriers;
    public IMailPool mailPool;
    
    public Automail(IMailPool mailPool, IMailDelivery delivery, int numCarriers) {
    	// Swap between simple provided strategies and your strategies here
    	    	
    	/** Initialize the MailPool */
    	
    	this.mailPool = mailPool;
    	((MailPool) mailPool).setAutomail(this);
    	
    	/** Initialize carriers */
    	carriers = new ArrayList<Carrier>();
    	for (int i = 0; i < numCarriers; i++) carriers.add(new Robot(delivery, mailPool));
    }    
}
