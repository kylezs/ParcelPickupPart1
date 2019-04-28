package automail;

import exceptions.ExcessiveDeliveryException;
import strategies.MailPool;

public abstract class Carrier {
	protected int currentFloor;
	protected int destinationFloor;
	protected MailPool mailPool;
	protected MailItem deliveryItem = null;
	
	// can we add delivertyItem here. Since in current Design diagram, we have it in both (= handItem for Robot)
	
	abstract public void step() throws ExcessiveDeliveryException;

}
