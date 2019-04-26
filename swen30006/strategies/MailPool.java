package strategies;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ListIterator;

import automail.Carrier;
import automail.MailItem;
import automail.PriorityMailItem;
import automail.Robot;
import automail.Team;
import exceptions.ItemTooHeavyException;

public class MailPool implements IMailPool {

	private class Item {
		int priority;
		int destination;
		MailItem mailItem;
		// Use stable sort to keep arrival time relative positions
		
		public Item(MailItem mailItem) {
			priority = (mailItem instanceof PriorityMailItem) ? ((PriorityMailItem) mailItem).getPriorityLevel() : 1;
			destination = mailItem.getDestFloor();
			this.mailItem = mailItem;
		}
	}
	
	public class ItemComparator implements Comparator<Item> {
		@Override
		public int compare(Item i1, Item i2) {
			int order = 0;
			if (i1.priority < i2.priority) {
				order = 1;
			} else if (i1.priority > i2.priority) {
				order = -1;
			} else if (i1.destination < i2.destination) {
				order = 1;
			} else if (i1.destination > i2.destination) {
				order = -1;
			}
			return order;
		}
	}
	
	private LinkedList<Item> pool;
	private LinkedList<Robot> robots;
	private Automail automail;

	public MailPool(int nrobots){
		// Start empty
		pool = new LinkedList<Item>();
		robots = new LinkedList<Robot>();
	}

	public void setAutomail(Automail automail){
		this.automail = automail;
	}
	
	public void removeFromAutomail(Carrier carrier) {
		automail.carriers.remove(carrier);
	}
	
	public void addToPool(MailItem mailItem) {
		Item item = new Item(mailItem);
		pool.add(item);
		pool.sort(new ItemComparator());
	}
	
	@Override
	public void step() throws ItemTooHeavyException {
		try{
			ListIterator<Robot> i = robots.listIterator();
			// Explicitly cast as Robot in loadRobot
			while (i.hasNext()) loadRobot(i);
		} catch (Exception e) { 
            throw e; 
        } 
	}
	
	private void loadRobot(ListIterator<Robot> i) throws ItemTooHeavyException {
		Robot robot = i.next();
		assert(robot.isEmpty());
		// System.out.printf("P: %3d%n", pool.size());
		ListIterator<Item> j = pool.listIterator();
	
		if (pool.size() > 0) {
			MailItem item = j.next().mailItem;
			
			// if a single robot can carry it
			if (item.getWeight() <= Robot.INDIVIDUAL_MAX_WEIGHT) {
				try {
				robot.addToHand(item); // hand first as we want higher priority delivered first
				j.remove();
				if (pool.size() > 0) {
					item = j.next().mailItem;
					if (item.getWeight() <= Robot.INDIVIDUAL_MAX_WEIGHT) {
						robot.addToTube(item);
						j.remove();
					} 
				}
				robot.dispatch(); // send the robot off if it has any items to deliver
				i.remove();       // remove from mailPool queue
				} catch (Exception e) { 
		            throw e; 
		        } 
			} 
			// if a team of 2 robots can carry it 
			else if (item.getWeight() <= Team.PAIR_MAX_WEIGHT && robots.size() >= 2) {
				ArrayList<Robot> temp = new ArrayList<Robot>();
				for (int k=0; k < 2; k++) {
					temp.add(robot);
					i.remove();
					robot = (Robot) i.next();
				}
				automail.carriers.add(new Team(item, temp, this));
				j.remove();
			} 
			// if a team of 3 robots can carry it
			else if (item.getWeight() <= Team.TRIPLE_MAX_WEIGHT && pool.size() > 0 && robots.size() >= 3) {
				ArrayList<Robot> temp = new ArrayList<Robot>();
				for (int k=0; k < 3; k++) {
					temp.add(robot);
					i.remove();
					if (i.hasNext()) robot = (Robot) i.next(); // only for the k=2 looping
				}
				automail.carriers.add(new Team(item, temp, this));
				j.remove();
			} else {
				System.out.println(String.format("Item %s is too heavy for any carrier, or not enough robots exist to make a team", item.toString()));
			}
		}
	}

	@Override
	public void registerWaiting(Robot robot) { // assumes won't be there already
		robots.add(robot);
	}

}
