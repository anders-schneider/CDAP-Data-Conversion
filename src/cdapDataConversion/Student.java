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
public class Student implements Comparable {

	String id;
	private HashMap<String, HashMap<String, Integer>> ratings;
	int lastInClassID; // Holds on to this information to be stored in its Teacher
	
	/**
	 * Constructor for a Student
	 * 
	 * @param id This student's unique student ID.
	 */
	public Student(String id) {
		this.id = id;
		ratings = new HashMap<String, HashMap<String, Integer>>();
	}
	
	/**
	 * Sets the rating for this student for the input character habit
	 * and subject. If the student already has a rating for this habit-
	 * subject combination, then that previous rating is averaged with
	 * the input rating to give a new rating, which is stored. This
	 * reflects the assumption that a student will have no more than two
	 * teachers for any given subject.
	 * 
	 * @param habit A character habit
	 * @param subject A subject
	 * @param rating The student's individual rating for this habit and subject
	 */
	public void setRating(String habit, String subject, int rating) {
		HashMap<String, Integer> ratingsByHabit;
		
		if (ratings.containsKey(habit)) ratingsByHabit = ratings.get(habit);
		else {
			ratingsByHabit = new HashMap<String, Integer>();
			ratings.put(habit, ratingsByHabit);
		}
		
		if (ratingsByHabit.containsKey(subject)) {
			rating = (rating + ratingsByHabit.get(subject)) / 2;
		}
		
		ratingsByHabit.put(subject, rating);
	}
	
	/**
	 * Returns the rating for this student for the input character habit
	 * and subject. If this student does not have a rating for the
	 * character habit-subject combination, returns <code>-1</code>.
	 * 
	 * @param habit A character habit
	 * @param subject A subject
	 * @return The student's rating for the input habit and subject
	 */
	public int getRating(String habit, String subject) {
		if (!ratings.containsKey(habit)) return -1;
		if (!ratings.get(habit).containsKey(subject)) return -1;
		return ratings.get(habit).get(subject);
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

	@Override
	public int compareTo(Object arg0) {
		Student other = (Student) arg0;
		return Integer.parseInt(this.id) - Integer.parseInt(other.id);
	}
}
