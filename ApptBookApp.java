package edu.uwm.cs351;

import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;
//Estelle Brady
//CS 351 - section 401
//collaborated with Julien Moreno and Joshua Knight
public class ApptBookApp {
	public static void main(String args[]) {
		// A simple main program that finds conflicts 
		// in an appointment book
		NewApptBook book = new NewApptBook();
		ApptBookIO.read(book);
		
		// Print out all conflicts between neighboring appts today
		Time now = new Time();
		Calendar cal = now.asCalendar();
		cal.setTimeZone(TimeZone.getDefault());
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0); // start of day: 0:00 (midnight)
		Time startOfDay = new Time(cal);
		Appointment firstPossible = new Appointment(new Period(startOfDay,Duration.MILLISECOND),"");
		
		Appointment prev = null;
		Iterator<Appointment> app = book.iterator(firstPossible);
		int conflictsFound = 0;
		while (app.hasNext()) {
			Appointment next = app.next();
			if (next.getTime().getStart().difference(startOfDay).compareTo(Duration.DAY) >= 0) break;
			if (prev != null) {
				if (prev.getTime().overlap(next.getTime())) {
					System.out.println("Conflict:");
					System.out.println("    " + prev);
					System.out.println("    " + next);
					++conflictsFound;
				}
			}
			prev = next;
		}
		System.out.println("Conflicts found: " + conflictsFound);
	}
}
