package simulation;

import java.util.ArrayList;
import java.util.Random;

/**
 *	Patient that is send trough the system
 *	@author Joel Karel
 *	@version %I%, %G%
 */
class Patient
{
	/** Stamps for the products */
	// arrival time
	private ArrayList<Double> times;
	//
	private ArrayList<String> events;
	private ArrayList<String> ambulances;
	public ArrayList<double[]>locations;

	public double x;
	public double y;

	// 0 is a1
	// 1 is b
	// 2 is a2
	public int type;

	public double timeBirthed;

	/** 
	*	Constructor for the product
	*	Mark the time at which it is created
	 */
	static ArrayList<Double> allX = new ArrayList<>();
	static ArrayList<Double> allY = new ArrayList<>();
	public Patient(int t, double tme)
	{
		times = new ArrayList<>();
		events = new ArrayList<>();
		ambulances = new ArrayList<>();
		locations=new ArrayList<>();
		type = t;
		setLocation();
		timeBirthed=tme;
		allX.add(x);
		allY.add(y);
	}
	
	
	public void stamp(double time,String event,String station,double[] loc)
	{
		times.add(time);
		events.add(event);
		ambulances.add(station);
		locations.add(loc);
	}
	
	public ArrayList<Double> getTimes()
	{
		return times;
	}

	public ArrayList<String> getEvents()
	{
		return events;
	}

	public ArrayList<String> getAmbulances()
	{
		return ambulances;
	}

	public double[] getTimesAsArray()
	{
		times.trimToSize();
		double[] tmp = new double[times.size()];
		for (int i=0; i < times.size(); i++)
		{
			tmp[i] = (times.get(i)).doubleValue();
		}
		return tmp;
	}

	public String[] getEventsAsArray()
	{
		String[] tmp = new String[events.size()];
		tmp = events.toArray(tmp);
		return tmp;
	}

	public String[] getStationsAsArray()
	{
		String[] tmp = new String[ambulances.size()];
		tmp = ambulances.toArray(tmp);
		return tmp;
	}
	public void setLocation() {
		// Generate random point in circle with diameter 10 that surrounds hexagon structure
		Random rand = new Random();
		double radius = rand.nextDouble()*20;
		double angle = rand.nextDouble() * 2 * Math.PI;
		double x = radius * Math.cos(angle);
		double y = radius * Math.sin(angle);

		// Check if point is inside hexagon structure
		if (isInsideStructure(x, y)) {
			this.x = x;
			this.y = y;
		} else {
			// If point is not inside structure, generate another point and try again
			setLocation();
		}
	}

	private boolean isInsideStructure(double x, double y) {
		// Check if point is inside center hexagon
		// checks the distance from origin (0,0), if less than radius (radius is 5, but set at 6 to be safe) then we know it's in the structure.
		if (Math.sqrt(x*x + y*y) <= 6) {
			return true;
		}

		// Check if point is inside any of the outer hexagons
		double[][] outerHexagonCenters = {{0, 8.6602540378444}, {7.4983802049147, 4.3301270189222}, {7.4983802049147, -4.3301270189222}, {0, -8.6602540378444}, {-7.4983802049147, -4.3301270189222}, {-7.4983802049147,  4.330127018922}};
		for (double[] center : outerHexagonCenters) {
			// calculate coordinates of each vertex of the hexagon centered at the current center
			double[][] vertices = new double[6][2];
			for (int i = 0; i < 6; i++)
			{
				double angle = 2 * Math.PI / 6 * i;
				vertices[i][0] = center[0] + 5 * Math.cos(angle);
				vertices[i][1] = center[1] + 5 * Math.sin(angle);
			}

			// check if point is inside the hexagon
			// this is done by checking if the point is on the same side of each line formed by two consecutive vertices as the center of the hexagon
			boolean inside = true;
			for (int i = 0; i < 6; i++) {
				// get a line from the hexagon
				double x1 = vertices[i][0];
				double y1 = vertices[i][1];
				double x2 = vertices[(i + 1) % 6][0];
				double y2 = vertices[(i + 1) % 6][1];

				// calculate the position of the point (x,y) relative to the line
				double line = (x - x1) * (y2 - y1) - (y - y1) * (x2 - x1);
				// calculate the position of the center relative to the line
				double center1 = (center[0] - x1) * (y2 - y1) - (center[1] - y1) * (x2 - x1);

				// if point and center are on opposite sides of the line, the point is not inside the hexagon
				if (line * center1 < 0) {
					inside = false;
					break;
				}
			}
			if (inside)
			{
				return true;
			}
		}

		return false;
	}
}