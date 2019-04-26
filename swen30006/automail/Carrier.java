package automail;

import exceptions.ExcessiveDeliveryException;

public abstract class Carrier {
	protected int currentFloor;
	protected int destinationFloor;
	
	
	// can we add delivertyItem here. Since in current Design diagram, we have it in both (= handItem for Robot)
	
	abstract public void step() throws ExcessiveDeliveryException;

}
