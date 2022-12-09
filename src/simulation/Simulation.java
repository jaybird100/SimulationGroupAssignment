/**
 *	Example program for using eventlists
 *	@author Joel Karel
 *	@version %I%, %G%
 */

package simulation;

import java.util.Arrays;

public class Simulation {

    public CEventList list;
    public Queue queue;
    public Source source;
    public Sink sink;
    public Ambulance mach;

    public static final double[] hospitalLoc = {0,0};
    public static final double[] station1 = {0, 10};
    public static final double[] station2 = {8.66025404, 5};
    public static final double[] station3 = {8.66025404, -5};
    public static final double[] station4 = {0, -10};
    public static final double[] station5 = {-8.66025404, -5};
    public static final double[] station6 = {-8.66025404, 5};
	

        /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	// Create an eventlist
	CEventList l = new CEventList();
	// A queue for the machine
	Queue q = new Queue();
	// A source
	Source s = new Source(q,l,"Source 1");
	// A sink
	Sink si = new Sink("Sink 1");
	// A machine
    int counter = 0;
    for(int i=0;i<5;i++){
        counter++;
        String temp = "Ambulance "+counter;
        Ambulance m = new Ambulance(q,si,l,temp,hospitalLoc);
    }
    for(int i=0;i<5;i++){
        counter++;
        String temp = "Ambulance "+counter;
        Ambulance m = new Ambulance(q,si,l,temp,station1);
    }
    for(int i=0;i<5;i++){
        counter++;
        String temp = "Ambulance "+counter;
        Ambulance m = new Ambulance(q,si,l,temp,station2);
    }
    for(int i=0;i<5;i++){
        counter++;
        String temp = "Ambulance "+counter;
        Ambulance m = new Ambulance(q,si,l,temp,station3);
    }
    for(int i=0;i<5;i++){
        counter++;
        String temp = "Ambulance "+counter;
        Ambulance m = new Ambulance(q,si,l,temp,station4);
    }
    for(int i=0;i<5;i++){
        counter++;
        String temp = "Ambulance "+counter;
        Ambulance m = new Ambulance(q,si,l,temp,station5);
    }
    for(int i=0;i<5;i++){
        counter++;
        String temp = "Ambulance "+counter;
        Ambulance m = new Ambulance(q,si,l,temp,station6);
    }
	// start the eventlist
	l.start(72);
    System.out.println(Arrays.toString(Patient.allX.toArray()));
    System.out.println(Arrays.toString(Patient.allY.toArray()));

    }
    
}
