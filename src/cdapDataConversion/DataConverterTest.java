package cdapDataConversion;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DataConverterTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testParseRosters() {
		//TODO Implement parseRosters test
		fail("Not yet implemented");
	}

	@Test
	public void testParseRosterLine() {
		//TODO Implement parseRosterLine test
		fail("Not yet implemented");
	}

	@Test
	public void testParseStudent() {
		DataConverter dc = new DataConverter();
		
		String line1 = "Student1,5115   Abdullah-Tucker, Ahmad";
		dc.parseStudent(line1);
		
		assertTrue(dc.students.containsKey(5115));
		assertEquals(1, dc.students.get(5115).lastInClassID);
		
		String line2 = "Student17, 3032   Andy Schneider";
		dc.parseStudent(line2);
		
		assertTrue(dc.students.containsKey(3032));
		assertEquals(17, dc.students.get(3032).lastInClassID);
		
		String line3 = "Student0, 30312   Andy Schneider";
		dc.parseStudent(line3);
		
		assertTrue(dc.students.containsKey(30312));
		assertEquals(0, dc.students.get(30312).lastInClassID);		
	}

	@Test
	public void testParseTeacher() {
		DataConverter dc = new DataConverter();
		
		String sLine1 = "Student1,5115   Abdullah-Tucker, Ahmad";
		Student ahmad = dc.parseStudent(sLine1);
		
		String sLine2 = "Student17, 3032   Andy Schneider";
		Student andy = dc.parseStudent(sLine2);
				
		String sLine3 = "Student0, 30312   Alisa Yu";
		Student alisa = dc.parseStudent(sLine3);
		
		String line1 = "Shoemaker, Daniel Hecht";
		String line2 = "Pickett, Alisa Yu";
		String line3 = "Shoemaker, Kimberly Crandall";
		
		dc.parseTeacher(line1, ahmad);
		
		assertTrue(dc.teachers.containsKey(line1));
		
		//TODO Finish testing parseTeacher method
	}

}
