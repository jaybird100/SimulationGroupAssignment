package simulation;

import java.util.ArrayList;

/**
 *	Queue that stores products until they can be handled on a machine machine
 *	@author Joel Karel
 *	@version %I%, %G%
 */
public class Queue implements PatientAcceptor
{
	/** List in which the products are kept */
	// List of patients arriving
	// sort the patient queue for priority
	private ArrayList<Patient> row;
	/** Requests from machine that will be handling the products */
	private ArrayList<Ambulance> requests;
	// Stores available ambulances.
	
	/**
	*	Initializes the queue and introduces a dummy machine
	*	the machine has to be specified later
	*/
	public Queue()
	{
		row = new ArrayList<>();
		requests = new ArrayList<>();
	}
	
	/**
	*	Asks a queue to give a product to a ambulance
	*	True is returned if a product could be delivered; false if the request is queued
	*/
	/* Given an ambulance, check if any patients are available.
	 */
	public boolean askProduct(Ambulance ambulance)
	{
		// This is only possible with a non-empty queue
		// check if row is empty
		if(row.size()>0)
		{
			// ambulance chooses the patient with the highest priority then shortest manhattan distance
			sortByDistance(ambulance);
			if(ambulance.giveProduct(row.get(0)))
			{
				row.remove(0);// Remove it from the queue
				return true;
			}
			else
				return false; // Ambulance rejected; don't queue request
		}
		else
		{
			requests.add(ambulance);
			return false; // queue request
		}
	}
	
	/**
	*	Offer a product to the queue
	*	It is investigated whether a machine wants the product, otherwise it is stored
	*/
	// give product needs to discriminate by distance
	public boolean giveProduct(Patient p)
	{
		// Check if the machine accepts it
		if(requests.size()<1)
			row.add(p); // Otherwise store it
		else
		{
			boolean delivered = false;
			sortByDistance(p);
			while(!delivered & (requests.size()>0))
			{
				delivered=requests.get(0).giveProduct(p);
				// remove the request regardless of whether or not the product has been accepted
				requests.remove(0);
			}
			if(!delivered)
				row.add(p); // Otherwise store it
		}
		return true;
	}

	// return a copy of row that is sorted first by their priority then by their closest distance to the ambulance.
	public void sortByDistance(Ambulance ambulance)
	{
		// create a new ArrayList to store the sorted patients
		ArrayList<Patient> sortedRow = new ArrayList<>();
		// loop through the patients in the row
		for (Patient patient : row)
		{
			// calculate the Manhattan distance between the ambulance and the patient
			double distance = Ambulance.manhattanDistance(ambulance.getLocation()[0], ambulance.getLocation()[1], patient.x, patient.y);

			// insert the patient into the sorted row at the appropriate position based on the type and distance
			int index = 0;
			for (Patient sortedPatient : sortedRow)
			{
				// if the patient has a smaller type than the sorted patient, insert the patient before the sorted patient
				if (patient.type < sortedPatient.type)
				{
					break;
				}
				// if the patient has the same type as the sorted patient, insert the patient based on the distance
				else if (patient.type == sortedPatient.type)
				{
					if (distance < Ambulance.manhattanDistance(ambulance.x, ambulance.y, sortedPatient.x, sortedPatient.y))
					{
						break;
					}
				}
				index++;
			}
			sortedRow.add(index, patient);
		}

		// return the sorted row
		row= sortedRow;
	}
	public void sortByDistance(Patient patient)
	{
		// create a new ArrayList to store the sorted ambulances
		ArrayList<Ambulance> sortedRequests = new ArrayList<>();
		// loop through the ambulances in the requests
		for (Ambulance ambulance : requests)
		{
			// calculate the Manhattan distance between the patient and the ambulance
			double distance = Ambulance.manhattanDistance(patient.x, patient.y, ambulance.x, ambulance.y);

			// insert the ambulance into the sorted requests at the appropriate position based on the distance
			int index = 0;
			for (Ambulance sortedAmbulance : sortedRequests)
			{
				if (distance < Ambulance.manhattanDistance(patient.x, patient.y, sortedAmbulance.x, sortedAmbulance.y))
				{
					break;
				}
				index++;
			}
			sortedRequests.add(index, ambulance);
		}
		// return the sorted requests
		requests = sortedRequests;
	}
}