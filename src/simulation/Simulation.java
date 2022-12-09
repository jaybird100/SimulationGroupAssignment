/**
 *	Example program for using eventlists
 *	@author Joel Karel
 *	@version %I%, %G%
 */

package simulation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class Simulation {

    public CEventList list;
    public Queue queue;
    public Source source;
    public Sink sink;
    public Ambulance mach;

    public static final double[] hospitalLoc = {0,0};
    public static final double[] station1 = {0, 8.6602540378444};
    public static final double[] station2 = {7.4983802049147, 4.3301270189222};
    public static final double[] station3 =  {7.4983802049147, -4.3301270189222};
    public static final double[] station4 = {0, -8.6602540378444};
    public static final double[] station5 = {-7.4983802049147, -4.3301270189222};
    public static final double[] station6 = {-7.4983802049147,  4.330127018922};
	

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
	l.start(24);
//    System.out.println(Arrays.toString(Patient.allX.toArray()));
//    System.out.println(Arrays.toString(Patient.allY.toArray()));
        toCSV(si);
    }

    public static void toCSV(Sink si){
//        System.out.println(System.getProperty("user.dir")+" "+si.getEvents().length+" "+si.getTimes().length+" "+
//                si.getNumbers().length+" "+si.getStations().length);

        File file=new File(System.getProperty("user.dir")+"\\src\\Log.csv");

        try {

            FileWriter outputfile = new FileWriter(file);
            outputfile.write("Event,Time,Numbers,Station,type,location\n");
            for(int i=0;i<si.getEvents().length;i++){
                outputfile.write(si.getEvents()[i]+","+si.getTimes()[i]+","+
                        si.getNumbers()[i]+","+si.getStations()[i]+","+si.types.get(i)+
                        ","+Arrays.toString(si.coord.get(i))+"\n");
            }
            outputfile.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
