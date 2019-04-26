package strategies;

import java.util.LinkedList;
import java.util.Comparator;
import java.util.ListIterator;

import automail.Carrier;
import automail.MailItem;
import automail.PriorityMailItem;
import automail.Robot;
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
	private LinkedList<Carrier> carriers;

	public MailPool(int nrobots){
		// Start empty
		pool = new LinkedList<Item>();
		carriers = new LinkedList<Carrier>();
	}

	public void addToPool(MailItem mailItem) {
		Item item = new Item(mailItem);
		pool.add(item);
		pool.sort(new ItemComparator());
	}
	
	@Override
	public void step() throws ItemTooHeavyException {
		try{
			ListIterator<Carrier> i = carriers.listIterator();
			// Explicitly cast as Robot in loadRobot
			while (i.hasNext()) loadCarrier(i);
		} catch (Exception e) { 
            throw e; 
        } 
	}
	
	private void loadCarrier(ListIterator<Carrier> i) throws ItemTooHeavyException {
		Robot robot = (Robot) i.next();
		assert(robot.isEmpty());
		// System.out.printf("P: %3d%n", pool.size());
		ListIterator<Item> j = pool.listIterator();
		if (pool.size() > 0) {
			try {
				MailItem currMailItem = j.next().mailItem;
				if (currMailItem.getWeight() > Robot.INDIVIDUAL_MAX_WEIGHT) {
					System.out.println("Make a team here");
					// Make the team out of 2 or 3 robots, check against constants in Team class for how many
				} else {
					System.out.println("Only need a robot for this");
					robot.addToHand(currMailItem); // hand first as we want higher priority delivered first
					j.remove();
					if (pool.size() > 0) {
						robot.addToTube(j.next().mailItem);
						j.remove();
					}
					robot.dispatch(); // send the robot off if it has any items to deliver
					i.remove();       // remove from mailPool queue
				}

			} catch (Exception e) { 
	            throw e; 
	        } 
		}
	}

	@Override
	public void registerWaiting(Robot robot) { // assumes won't be there already
		carriers.add(robot);
	}

}
