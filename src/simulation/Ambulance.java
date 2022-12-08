package simulation;

import java.util.Arrays;
import java.util.Random;

/**
 *	Ambulance
 *	@author Joel Karel
 *	@version %I%, %G%
 */
public class Ambulance implements CProcess, PatientAcceptor
{
	/** Patient that is being handled  */
	private Patient patient;
	/** Eventlist that will manage events */
	private final CEventList eventlist;
	/** Queue from which the machine has to take products */
	private Queue queue;
	/** Sink to dump products */
	private PatientAcceptor sink;
	/** Status of the machine (b=busy, i=idle) */
	private char status;
	/** Ambulance name */
	private final String name;

	public double x;
	public double y;
	public double[] homeDockingStation;

	public boolean driving;
	public double startOfDriving;
	// difference between hospital and waiting dock;
	public double xDiff;
	public double yDiff;

	public double drivingDistance;


	// Location needs to be stored.

	// deal with crews switching out
	// switching out crews would be a type of event
	// look for the nearest docking station not at capacity
	//
	//
	// seperate class for docking bays, location and capacity
	/**
	*	Constructor
	*        Service times are exponentially distributed with mean 30
	*	@param q	Queue from which the machine has to take products
	*	@param s	Where to send the completed products
	*	@param e	Eventlist that will manage events
	*	@param n	The name of the machine
	*/
	public Ambulance(Queue q, PatientAcceptor s, CEventList e, String n, double[] dockCoords)
	{
		status='i';
		queue=q;
		sink=s;
		eventlist=e;
		name=n;
		queue.askProduct(this);
		homeDockingStation = dockCoords;
		x=dockCoords[0];
		y=dockCoords[1];
		xDiff=homeDockingStation[0]-Simulation.hospitalLoc[0];
		yDiff=homeDockingStation[1]-Simulation.hospitalLoc[1];
		drivingDistance=manhattanDistance(Simulation.hospitalLoc,homeDockingStation);
	}


	/**
	*	Method to have this object execute an event
	*	@param type	The type of the event that has to be executed
	*	@param tme	The current time
	*/
	// execute needs to also change the location.
	public void execute(int type, double tme)
	{
		if(type==0) {
			// show arrival
			System.out.println(name+": Patient finished at time = " + tme);
			System.out.println(patient.type+" Patient starting location: x: "+patient.x+" y: "+patient.y);
			System.out.println("Ambulance starting location: x: "+x+" y: "+y);
			System.out.println("Manhattan distance: "+manhattanDistance(x,y,patient.x,patient.y));
			// Remove patient from system
			patient.stamp(tme, "Production complete", name);
			sink.giveProduct(patient);
			patient = null;
			// set machine status to idle
			status = 'i';
			x = Simulation.hospitalLoc[0];
			y = Simulation.hospitalLoc[1];
			if(!Arrays.equals(homeDockingStation,Simulation.hospitalLoc)) {
				driving(tme);
			}
			// Ask the queue for products
			queue.askProduct(this);
		}
		if(type==1){
			driving=false;
			x = homeDockingStation[0];
			y = homeDockingStation[1];
		}
	}

	// driving from hospital to docking station
	private void driving(double tme){
		driving=true;
		startOfDriving=tme;
		double duration = drivingDistance;
		eventlist.add(this,1,tme+duration);
	}
	
	/**
	*	Let the machine accept a patient and let it start handling it
	*	@param p	The patient that is offered
	*	@return	true if the patient is accepted and started, false in all other cases
	*/
        @Override
	public boolean giveProduct(Patient p)
	{
		// Only accept something if the machine is idle
		if(status=='i')
		{
			// accept the patient
			patient =p;
			// mark starting time
			patient.stamp(eventlist.getTime(),"Production started",name);
			// start production
			startProduction();
			// Flag that the patient has arrived
			return true;
		}
		// Flag that the patient has been rejected
		else return false;
	}
	
	/**
	*	Starting routine for the production
	*	Start the handling of the current patient with an exponentionally distributed processingtime with average 30
	*	This time is placed in the eventlist
	*/
	// 1. drive to patient. 2. process patient at scene. 3. drive to hospital. Calculate time and add to event list
	private void startProduction()
	{
		// assuming static.
		double duration = 0;
		double tme = eventlist.getTime();
		if(!driving) {
			duration = manhattanDistance(x, y, patient.x, patient.y);
		}else{
			double timeElapsed = tme - startOfDriving;
			double percentage = timeElapsed/drivingDistance;
			double newX = Simulation.hospitalLoc[0] + (xDiff * percentage);
			double newY = Simulation.hospitalLoc[1] + (yDiff * percentage);
			duration = manhattanDistance(newX, newY, patient.x, patient.y);
			driving=false;
		}
		duration+=calculateProcessingTime();
		duration+= manhattanDistance(patient.x,patient.y,Simulation.hospitalLoc);
		eventlist.add(this,0,tme+duration);
		status='b';
	}

	public static double calculateProcessingTime()
	{
		// Erlang 3 is the sum of 3 independent exponentially distributed random variables with mean 1/lambda
		// so we take the sum of the negative log of 3 U(0,1) divided by lambda
		Random rand = new Random();
		double lambda = 1;
		double processingTime = 0;
		for (int i = 0; i < 3; i++)
		{
			processingTime += -Math.log(rand.nextDouble()) / lambda;
		}
		// convert from minutes to hours
		return processingTime/60;
	}

	public static double manhattanDistance(double x1, double y1, double x2, double y2)
	{
		double xDiff = Math.abs(x1 - x2);
		double yDiff = Math.abs(y1 - y2);
		return (xDiff + yDiff)/60;
	}
	public static double manhattanDistance(double x1, double y1, double[] x2y2)
	{
		double xDiff = Math.abs(x1 - x2y2[0]);
		double yDiff = Math.abs(y1 - x2y2[1]);
		return (xDiff + yDiff)/60;
	}

	public static double manhattanDistance(double[] x1y1, double x2, double y2)
	{
		double xDiff = Math.abs(x1y1[0] - x2);
		double yDiff = Math.abs(x1y1[1] - y2);
		return xDiff + yDiff;
	}
	public static double manhattanDistance(double[] x1y1, double[] x2y2)
	{
		double xDiff = Math.abs(x1y1[0] - x2y2[0]);
		double yDiff = Math.abs(x1y1[1] - x2y2[1]);
		return (xDiff + yDiff)/60;
	}

	// Get Location. If it's travelling back from hospital, calculate where based on time elapsed.
}