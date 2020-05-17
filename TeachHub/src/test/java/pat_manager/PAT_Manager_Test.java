package pat_manager;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class PAT_Manager_Test {
	@Test
	public void getExtentionTest() {
		PAT_Manager ptm = new PAT_Manager();
		String test1 = "this.txt";
		String test2 = "this.txttxt";
		String test3 = "thistxt";
		String test4 = ".txt";
		String test5 = "thistxt.";
		assertTrue(ptm.getExtention(test1).equals(".txt"));
		assertTrue(ptm.getExtention(test2).equals(".txttxt"));
		assertTrue(ptm.getExtention(test3) == null);
		assertTrue(ptm.getExtention(test4).equals(".txt"));
		assertTrue(ptm.getExtention(test5).equals("."));
	}
	
	@Test
	public void nameTypeTest() {
		//happy path
		String test1 = "[ShalomGottesman]-[main]-[NONE].json";//valid
		//test security level string comparison
		String test2 = "[ShalomGottesman]-[main]-[NoNe].json";//invalid
		//test 3 parts
		String test3 = "[ShalomGottesman]-[None].json";//invalid
		//test regular expression of the string split
		String test4 = "[ShalomGottesman]-[main-[None].json";//invalid
		//test subString operation
		String test5 = "[ShalomGottesman]-[main]-[None.json";//invalid
		PAT_Manager ptm = new PAT_Manager();
		assertTrue(ptm.nameTypeMatch(test1) == true);
		assertTrue(ptm.nameTypeMatch(test2) == false);
		assertTrue(ptm.nameTypeMatch(test3) == false);
		assertTrue(ptm.nameTypeMatch(test4) == false);
		assertTrue(ptm.nameTypeMatch(test5) == false);
		
	}
}
