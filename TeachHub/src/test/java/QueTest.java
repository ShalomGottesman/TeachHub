import data_structures.Que;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class QueTest {
	@Test
	public void enqueTest() {
		Que<Integer> que = new Que<Integer>();
		enque15ints(que);
		System.out.println(que.size());
		assertTrue(15 == que.size());
		for (int x = 0; x < 15; x++) {
			assertTrue(x == que.peek());
			assertTrue(x == que.deque());
		}
	}
	
	
	
	
	
	
	private void enque15ints(Que<Integer> que) {
		for (int x = 0; x < 15; x++) {
			que.enque(x);
		}
	}
}
