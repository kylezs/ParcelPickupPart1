package automail;

import exceptions.ExcessiveDeliveryException;

public abstract class Carrier {
	protected int currentFloor;
	protected int destinationFloor;
	
	abstract public void step() throws ExcessiveDeliveryException;
}
