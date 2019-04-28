package automail;

import java.util.ArrayList;

import automail.Robot.RobotState;
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
    		robot.destinationFloor = this.destinationFloor;
    	}
    }
	
	
	public void step() throws ExcessiveDeliveryException {
		System.out.println("Team step() call #" + stepCounter + ", currentFloor: " + currentFloor + " ; destinationFloor: " + destinationFloor);
		// a team moves every 3 time steps
		if (this.stepCounter < 2) {
			this.stepCounter++;
			return;
		}
		this.stepCounter = 0;
		
		// ensure all robots are on the same floor
		for (Robot robot: robots) assert(robot.currentFloor == this.currentFloor && robot.currentState == RobotState.TEAMING);
		
		for (Robot robot: robots) assert(robot.destinationFloor == this.destinationFloor);
		
		// move the robots AND the team up by a floor
		for (Robot robot: robots) robot.teamStep();
		this.moveTowards();
		
        
		if (this.currentFloor == this.destinationFloor) {
			System.out.println("Completing delivery");
			teamCompleteDelivery();
		}
		
		
	}
	
	private void moveTowards() {
        if(this.currentFloor < this.destinationFloor){
            this.currentFloor++;
        } else if (this.currentFloor > this.destinationFloor) {
            this.currentFloor--;
        }
	}
	
	private void teamCompleteDelivery() {
		/* make one of the robots "deliver" the item, to enable the team to deliver the item
		 * we would need to give MailPool a reference to the IMailDelivery in simulation
		 */
		this.robots.get(0).delivery.deliver(deliveryItem);
		
		// set the robots to either return or deliver their tube item
		for (Robot robot: robots) {
			robot.finishTeaming();
		}
		this.mailPool.removeFromAutomail(this);
	}

}
