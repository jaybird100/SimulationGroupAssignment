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

	boolean needToChange = false;
	double shiftTime;

	// how long should ambulances get as a warning before they change
	final double timeToChange = (10.0/60);

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
		eventlist.add(this, 2, eventlist.getTime() + (7-timeToChange));
		shiftTime=7;
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
			System.out.println(name+": "+patient.type+" Patient finished at time = " + tme+"  Time elapsed: "+((tme-patient.timeBirthed)*60)+" minutes");
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
			if(!needToChange) {
				queue.askProduct(this);
			}else{
				crewChange();
				needToChange=false;
			}
		}
		if(type==1){
			driving=false;
			x = homeDockingStation[0];
			y = homeDockingStation[1];
		}
		if(type==2){
			//System.out.println(name+" CREW CHANGE 2: shift time: "+shiftTime+" current time: "+eventlist.getTime()%24);
			// crew change
			if(status=='i'){
				crewChange();
			}else{
				needToChange=true;
			}
		}
		//finish crew change. Shift starts only at time set or after.
		if(type==3){
			x=homeDockingStation[0];
			y=homeDockingStation[1];
		//	System.out.println(name+" CREW CHANGE 3: shift time: "+shiftTime+" current time: "+eventlist.getTime()%24);
			double time= eventlist.getTime()%24;
			if (time>=shiftTime){

				status='i';
				if(shiftTime<=15){
					if(Math.random()<0.25) {
						eventlist.add(this, 2, eventlist.getTime() + ((4-timeToChange)-(time-shiftTime)));
						shiftTime=(shiftTime+4)%24;
					}else{
						eventlist.add(this, 2, eventlist.getTime() + ((8-timeToChange)-(time-shiftTime)));
						shiftTime=(shiftTime+8)%24;
					}
				}else{
					if(shiftTime<=19){
						eventlist.add(this, 2, eventlist.getTime() + (4-timeToChange)-(time-shiftTime));
						shiftTime=(shiftTime+4)%24;
					}else{
						eventlist.add(this, 2, eventlist.getTime() + (8-timeToChange)-(time-shiftTime));
						shiftTime=7;
					}
				}
				queue.askProduct(this);
			} else{
				eventlist.add(this,3,tme+(shiftTime-time));
			}
		}
	}

	private void crewChange(){
		double duration = manhattanDistance(getLocation()[0],getLocation()[1],homeDockingStation);
		double tme = eventlist.getTime();
		status='b';
		eventlist.add(this,3,tme+duration);
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
		double[] location = getLocation();
		x=location[0];
		y=location[1];
		if(driving){
			driving=false;
		}
		double duration = manhattanDistance(location[0], location[1], patient.x, patient.y);
		double tme = eventlist.getTime();
		duration+=calculateProcessingTime();
		duration+= manhattanDistance(patient.x,patient.y,Simulation.hospitalLoc);
		eventlist.add(this,0,tme+duration);
		System.out.println(this.name+" ("+location[0]+", "+location[1]+") going to patient ("+patient.x+", "+patient.y+") Minutes since birth: "+((tme-patient.timeBirthed)*60)+" minutes");
		status='b';
	}

	public double[] getLocation(){
		if(!driving) {
			return new double[]{x,y};
		}else{

			double tme = eventlist.getTime();
			double timeElapsed = tme - startOfDriving;
			double percentage = timeElapsed/drivingDistance;
			double newX = Simulation.hospitalLoc[0] + (xDiff * percentage);
			double newY = Simulation.hospitalLoc[1] + (yDiff * percentage);
			return new double[]{newX,newY};
		}
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