package cdapDataConversion;

import java.util.HashMap;

/**
 * Objects of this class represent individual students. Students are 
 * uniquely identified by their ID numbers or by the combination of
 * their teacher's name and their inClassID.
 * 
 * @author Anders Schneider
 *
 */
public class Student {

	int id;
	private HashMap<String, Integer> ratings; // Key: Subject | Value: Rating
	int lastInClassID; // Holds on to this information to be stored in its Teacher
	
	/**
	 * Constructor for a Student
	 * 
	 * @param id This student's unique student ID.
	 */
	public Student(int id) {
		this.id = id;
		ratings = new HashMap<String, Integer>();
	}

	@Override
	public String toString() {
		return "" + id;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Student)) return false;
		Student other = (Student) o;
		return this.id == other.id;
	}
}
