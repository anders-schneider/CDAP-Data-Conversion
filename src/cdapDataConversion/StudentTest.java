package cdapDataConversion;

import static org.junit.Assert.*;

import org.junit.Test;

public class StudentTest {

	@Test
	public void test() {
		Student s1 = new Student("1234");
		
		s1.setRating("Grit", "Math", 4);
		s1.setRating("Self-Control", "Science", 5);
		s1.setRating("Grit", "Science", 3);
		
		assertEquals(4, s1.getRating("Grit", "Math"));
		assertEquals(5, s1.getRating("Self-Control", "Science"));
		assertEquals(3, s1.getRating("Grit", "Science"));
		
		assertEquals(-1, s1.getRating("Grit", "English"));
		assertEquals(-1, s1.getRating("Optimism", "Science"));
		assertEquals(-1, s1.getRating("Gratitude", "Spanish"));
	}

}
