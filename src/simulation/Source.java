package simulation;
import java.util.Random;

/**
 *	A source of products
 *	This class implements CProcess so that it can execute events.
 *	By continuously creating new events, the source keeps busy.
 *	@author Joel Karel
 *	@version %I%, %G%
 */
public class Source implements CProcess
{
	/** Eventlist that will be requested to construct events */
	private CEventList list;
	/** Queue that buffers products for the machine */
	private ProductAcceptor queue;
	/** Name of the source */
	private String name;
	/** Mean interarrival time */

	private Random rand = new Random();
	/**
	*	Constructor, creates objects
	*        Interarrival times are exponentially distributed with mean 33
	*	@param q	The receiver of the products
	*	@param l	The eventlist that is requested to construct events
	*	@param n	Name of object
	*/
	public Source(ProductAcceptor q,CEventList l,String n)
	{
		list = l;
		queue = q;
		name = n;
		// put first event in list for initialization
		list.add(this,0,drawRandomPoisson(0)); //target,type,time
		list.add(this,1,drawRandomPoisson(0)); //target,type,time
		list.add(this,2,drawRandomPoisson(0)); //target,type,time
	}

	// arrival of patients
        @Override
	public void execute(int type, double tme)
	{
		// A1
		if(type==0) {
			// show arrival
			System.out.println("A1 Arrival at time = " + tme);
			// give arrived product to queue
			// new patient, need to add priority level to the creation
			Product p = new Product(0);
			p.stamp(tme, "Creation", name);
			queue.giveProduct(p);
			// generate duration
			double duration = drawRandomPoisson(tme);
			// Create a new event in the eventlist
			list.add(this, 0, tme + duration); //target,type,time
		}
		// B
		if(type==1) {
			// show arrival
			System.out.println("B Arrival at time = " + tme);
			// give arrived product to queue
			// new patient, need to add priority level to the creation
			Product p = new Product(1);
			p.stamp(tme, "Creation", name);
			queue.giveProduct(p);
			// generate duration
			double duration = drawRandomPoisson(tme);
			// Create a new event in the eventlist
			list.add(this, 1, tme + duration); //target,type,time
		}
		// A2
		if(type==2) {
			// show arrival
			System.out.println("A2 Arrival at time = " + tme);
			// give arrived product to queue
			// new patient, need to add priority level to the creation
			Product p = new Product(2);
			p.stamp(tme, "Creation", name);
			queue.giveProduct(p);
			// generate duration
			double duration = drawRandomPoisson(tme);
			// Create a new event in the eventlist
			list.add(this, 2, tme + duration); //target,type,time
		}

	}

	// Generate a random patient arrival time according to the time-varying Poisson process
	public double drawRandomPoisson(double t) {
		// Rate of patient arrival (3 - 2 * sin((5 * (π + t)) / 6π))
		double lambda = 3 - 2 * Math.sin((5 * (Math.PI + t)) / (6 * Math.PI));
		// Generate random number using Poisson distribution
		return -Math.log(1 - rand.nextDouble()) / lambda;
	}
}