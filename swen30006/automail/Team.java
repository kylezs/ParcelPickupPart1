package automail;

import java.util.ArrayList;

import exceptions.ExcessiveDeliveryException;

public class Team extends Carrier {
	static public final int PAIR_MAX_WEIGHT = 2600;
    static public final int TRIPLE_MAX_WEIGHT = 3000;
    
    private MailItem deliveryItem;
    
    private ArrayList<Robot> robots;
    
    Team(MailItem deliveryItem, ArrayList<Robot> robots) {
    	this.deliveryItem = deliveryItem;
    	this.robots = robots;
    }
	
	
	public void step() throws ExcessiveDeliveryException {
		System.out.println("Step being called in Team()");
		return;
	}

}
