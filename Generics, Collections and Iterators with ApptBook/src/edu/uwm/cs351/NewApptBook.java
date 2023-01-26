package edu.uwm.cs351;

import java.util.AbstractCollection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
//Estelle Brady
//CS 351 - section 401
//collaborated with Julien Moreno and Joshua Knight
//used Activity 2 to get help with ensureCapacity

/**
 * A variant of the ApptBook ADT that follows the Collection model.
 * In particular, it has no sense of a current element.
 * All access to elements by the client must be through the iterator.
 * The {@link #add(Appointment)} method should add at the correct spot in sorted order in the collection.
 */
public class NewApptBook extends AbstractCollection<Appointment> implements Cloneable {
	// TODO: Add all the contents here.
	// Remember:
	// - All public methods not marked @Override must be fully documented with javadoc
	// - A @Override method must be marked 'required', 'implementation', or 'efficiency'
	// - You need to define and check the data structure invariant
	//   (essentially the same as in Homework #2)
	// - You should define a nested iterator class called MyIterator (with its own data structure), 
	//   and then the iterator() method simply returns a new instance.
	// You are permitted to copy in any useful code/comments from the Homework #2 solution.
	// But do not include any of the cursor-related methods, and in particular,
	// make sure you have no "currentIndex" field.
	
	private static final int INITIAL_CAPACITY = 1;
	private Appointment[] data; //array of the elements
	private int manyItems; //used items
	private int version; //counting changes in iterator

	
	private static boolean doReport = true; // change only in invariant tests
	private boolean report(String error) {
		if (doReport) {
			System.out.println("Invariant error: " + error);
		}
		return false;
	}
	/**
	 * this method is an extra test within certain methods to check certain exceptions
	 *checks whether it is null, if there are at least as many items as in manyItems
	 *is any elements are null
	 *if they are in natural order
	 *if currentIndex is never negative or more than numbers in book
	 * @return report or true
	 */
	private boolean wellFormed() {
		// Check the invariant.
		// 1. data array is never null
		if(this.data == null)
			return report("an element in data is null");
		// 2. The data array has at least as many items in it as manyItems
		//    claims the book has
		if(data.length < manyItems)
			return report("the array is shorter than the amount of items");

		// 3. None of the elements are null and all are in natural order
		for(int i=0; i<manyItems; i++)
			if(data[i] == null)
				return report("An element in the array is null");
		for(int i=0; i+1<manyItems; i++)
			if(data[i].compareTo(data[i+1])>0)
				return report("This is out of order");
		return true;
	}
	/**
	 * sets data to the initial capacity
	 */
	
	public NewApptBook( )
	{
		this.data = new Appointment[INITIAL_CAPACITY];
		assert wellFormed() : "invariant failed at end of constructor";
	}
	
	/**
	 * Initialize an empty book with a specified initial capacity
	 * The {@link #insert(Appointment)} method works
	 * efficiently (without needing more memory) until this capacity is reached.
	 * @param initialCapacity
	 *   the initial capacity of this book, must not be negative
	 * @exception IllegalArgumentException
	 *   Indicates that name, bpm or initialCapacity are invalid
	 * @exception OutOfMemoryError
	 *   Indicates insufficient memory for an array with this many elements.
	 *   new Appointment[initialCapacity].
	 **/   
	
	public NewApptBook(int initialCapacity)
	{
		if(initialCapacity < 0)
			throw new IllegalArgumentException("initial Capacity cannot be negative");
		if(initialCapacity > Integer.MAX_VALUE)
			throw new OutOfMemoryError("Memory has gone over the limit");
		this.data = new Appointment[initialCapacity];	
		// TODO: Implemented by student.
		assert wellFormed() : "invariant failed at end of constructor";
	}
	
	/**
	 * Determine the number of elements in this book.
	 * @return
	 *   the number of elements in this book -manyItems
	 **/ 
	@Override //required
	public int size( )
	{	
		assert wellFormed() : "invariant failed at start of size";
		return manyItems;
	}
	
	/**
	 * Change the current capacity of this book as needed so that
	 * the capacity is at least as big as the parameter.
	 * This code must work correctly and efficiently if the minimum
	 * capacity is (1) smaller or equal to the current capacity (do nothing)
	 * (2) at most double the current capacity (double the capacity)
	 * or (3) more than double the current capacity (new capacity is the
	 * minimum passed).
	 * @param minimumCapacity
	 *   the new capacity for this book
	 * @postcondition
	 *   This book's capacity has been changed to at least minimumCapacity.
	 *   If the capacity was already at or greater than minimumCapacity,
	 *   then the capacity is left unchanged.
	 **/
	//helped by activity 2
	private void ensureCapacity(int minimumCapacity)
	{
		if(minimumCapacity <= data.length) return;
			int c1 = data.length*2;
		if(c1 <minimumCapacity) 
			c1 = minimumCapacity;
		Appointment [] temp = new Appointment[c1];
		for(int i = 0; i<manyItems; ++i)
			temp[i] = data[i];
		data = temp;
	}

	/**
	 * Add a new element to this book, in order.  If an equal appointment is already
	 * in the book, it is inserted after the last of these. 
	 * If the new element would take this book beyond its current capacity,
	 * then the capacity is increased before adding the new element.
	 * The current element (if any) is not affected.
	 * @param element
	 *   the new element that is being added, must not be null
	 * @return true or false
	 * @postcondition
	 *   A new copy of the element has been added to this book. The current
	 *   element (whether or not is exists) is not changed.
	 * @exception NullPointerException
	 * 	throws an exception if the appointment is null
	 **/
	//youtube video assisted in implementing the insert method. - link up top
	@Override // implementation
	public boolean add(Appointment element) {
		assert wellFormed() : "invariant failed at start of insert";
		if (element == null) throw new NullPointerException( "cannot insert null!");
		ensureCapacity(manyItems+1);
		int i;
		for (i=manyItems; i > 0 && data[i-1].compareTo(element) > 0; --i) {
				data[i]= data[i-1];
	}
		data[i] = element;
		++manyItems;	++version;
	
		assert wellFormed() : "invariant failed at end of insert";

		return true;
	}	
	/**
	 * Generate a copy of this book.
	 * @return
	 *   The return value is a copy of this book. Subsequent changes to the
	 *   copy will not affect the original, nor vice versa.
	 * @exception OutOfMemoryError
	 *   Indicates insufficient memory for creating the clone.
	 **/ 
	@Override //implemented
	public NewApptBook clone( ) { 
		assert wellFormed() : "invariant failed at start of clone";
		NewApptBook answer;
	
		try
		{
			answer = (NewApptBook) super.clone( );
		}
		catch (CloneNotSupportedException e)
		{  // This excetion should not occur. But if it does, it would probably
			// indicate a programming error that made super.clone unavailable.
			// The most common error would be forgetting the "Implements Cloneable"
			// clause at the start of this class.
			throw new RuntimeException
			("This class does not implement Cloneable");
		}
		assert wellFormed() : "invariant failed at end of clone";
		assert answer.wellFormed() : "invariant on answer failed at end of clone";
		return answer;
	}
	
	@Override //efficiency
	public void clear() {
		assert wellFormed() : "invariant failed at start of clear";
		++version;
		data = new Appointment[INITIAL_CAPACITY];
		manyItems = 0;
		assert wellFormed() : "invariant failed at end of clear";		
	}
	
	@Override // required
	public Iterator<Appointment> iterator() {
		return new MyIterator();
	}
	
	/**
	 * checks if s is null and returns a new iterator
	 * @param s 
	 * takes in an element and does checks
	 * @exception	NullPointerException
	 * checks if the argument is null
	 * @return
	 * the new iterator with argument s
	 */
	public Iterator<Appointment> iterator(Appointment s) {
		if(s == null)
			throw new NullPointerException("null in iterator specifying method");
		return new MyIterator(s);
	}
	
	
	private class MyIterator implements Iterator<Appointment> // TODO: what should this implement?	
	{
		private int current;
		private int myVersion;	
		private int next;
		
		public MyIterator() {
			current = 0;
			myVersion = version;
			next = 0;	
		}
		/**
		 * Does a binary search to have better efficiency
		 * @param s
		 * takes in an appointment
		 * @exception	NullPointerException
		 * checks to see if the argument is null
		 */
		//binary search
		public MyIterator(Appointment s) {
			//gets the highest index
			int high = manyItems -1;
			int middle;
			//low is the current index
			int low = 0;
			//makes sure s is valid
			if(s == null)
				throw new NullPointerException("there are no items");
			else
			{	
				while(low < high) {
					middle = low+(high-low)/2;
					//if the middle comes before s return the low +1
					if(data[middle].compareTo(s) < 0) {
						// check the right side
						low = middle + 1;
					} 
					//if the middle comes after s return high is middle -1
					else if (data[middle].compareTo(s) > 0) {
					 	// check the left side
						high = middle - 1;
					} else {
						// set the current
						low = middle;
						//prevents loop
						break;
					}
				}
				//checks if there is a single element
				if(manyItems > 0 && data[low].compareTo(s) < 0)
					low = low + 1;
				//has to set next to current so then next in incremented in the next method
				current = low;
				next = current;
				myVersion=version;
			}
			
		}
		// The nested MyIterator class should use the following
		// invariant checker:
		public boolean wellFormed() {
			if (!NewApptBook.this.wellFormed()) return false;
			if (version != myVersion) return true; // not my fault if invariant broken
			if (current < 0 || current > manyItems) return report("current out of range: " + current + " not in range [0," + manyItems + "]");
			if (next < 0 || next > manyItems) return report("next out of range: " + next + " not in range[0," + manyItems + "]");
			if (next != current && next != current + 1) return report("next " + next + " isn't current or its successor (current = " + current + ")");
			return true;
		}
		
		@Override//required
		public boolean hasNext() {
			assert wellFormed() : "invariant failed at start of hasNext";
			if(myVersion!=version) throw new ConcurrentModificationException("versions do not match in hasNext");
			assert wellFormed() : "invariant failed at end of hasNext";
			return next < manyItems;
			}

		@Override//required
		public Appointment next() {
			assert wellFormed() : "invariant failed at start of next";
			if(!hasNext()) throw new NoSuchElementException("no element in next");
			if (myVersion!= version) throw new ConcurrentModificationException("versions do not equal in next");
			if(next == current) {
				next++; 
				return data[current];
			}
			if(next<current)
				next++;
			current++;
			assert wellFormed() : "invariant failed at end of next";
			return data[next++];
		}
		@Override // implementation
		public void remove()
		{
			assert wellFormed() : "invariant failed at start of removeCurrent";
			if(myVersion != version) throw new ConcurrentModificationException("versions are not the same in remove");
			if(current == next) throw new IllegalStateException("this is not in the book");
		
			//takes the current index, replaces it with the next index, shifts all to the left 
			//after this forloop, we will get rid of the null element by --manyItems
			for(int i = current; i<manyItems-1; i++) {
						data[i]=data[i+1];
					}
				//next method helps keep next above current or equal to throw exception
				--next;
				//decrements manyItems because the last item should be null or last item is removed
				--manyItems;	
				myVersion = ++version;
				assert wellFormed() : "invariant failed at end of remove";		
				}		
	}
}




