package cdapDataConversion;

import java.util.HashMap;

public class Teacher {
	
	String name;
	private HashMap<Integer, Student> students; // Maps student IDs (keys) to students (values)
	String subject;
	
	/**
	 * Constructor for a Teacher object.
	 * 
	 * @param name This teacher's unique name
	 */
	public Teacher(String name) {
		this.name = name;
		students = new HashMap<Integer, Student>();
	}
	
	/**
	 * Adds a student to the collection of students that this teacher
	 * teaches.
	 * 
	 * @param student The student taught by this teacher
	 * @param inClassID The ID number of this student (within this class only)
	 */
	public void addStudent(Student student, int inClassID) {
		students.put(inClassID, student);
	}
	
	public Student getStudent(int inClassID) {
		//TODO Implement getStudent method
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Teacher)) return false;
		
		Teacher other = (Teacher) o;
		return this.name == other.name;
	}
}
