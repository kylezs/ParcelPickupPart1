package automail;

import java.util.ArrayList;

import exceptions.ExcessiveDeliveryException;
import strategies.MailPool;

public class Team extends Carrier {
	static public final int PAIR_MAX_WEIGHT = 2600;
    static public final int TRIPLE_MAX_WEIGHT = 3000;
    
    private int stepCounter;
    
    private MailItem deliveryItem;
    
    private ArrayList<Robot> robots;
    
    public Team(MailItem deliveryItem, ArrayList<Robot> robots, MailPool mailPool) {
    	this.deliveryItem = deliveryItem;
    	this.destinationFloor = deliveryItem.getDestFloor();
    	this.robots = robots;
    	this.mailPool = mailPool;
    	stepCounter = 0;
    	for (Robot robot: robots) {
    		robot.changeState(Robot.RobotState.TEAMING);
    	}
    }
	
	
	public void step() throws ExcessiveDeliveryException {
		//System.out.println("Step being called in Team()");
		
		// a team moves every 3 time steps
		if (stepCounter < 2) {
			stepCounter++;
			return;
		}
		stepCounter = 0;
		for (Robot robot: robots) robot.teamStep();
		
		// ensure all robots are on the same floor
		int current_floor = robots.get(0).currentFloor;
		for (Robot robot: robots) assert(robot.currentFloor == current_floor);
		
		if (current_floor == destinationFloor) {
			/* make one of the robots "deliver" the item, to enable the team to deliver the item
			 * we would need to give MailPool a reference to the IMailDelivery in simulation
			 */
			robots.get(0).delivery.deliver(deliveryItem);
			
			// set the robots to either return or deliver their tube item
			for (Robot robot: robots) {
				robot.finishTeaming();
			}
			mailPool.removeFromAutomail(this);
		}
	}

}
