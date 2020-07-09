package pat_manager.security;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EncryptionTest {
	@Test
	public void test() {
		String key = "aaaaaaaaaaaaaaaa";
		String text = "zxcbvsdkjhauiasdhlasiduy";
		Encryption encr = new Encryption();
		String ret = encr.encrypt(text, key);
		String orig = encr.decrypt(ret, key);
		assertTrue(orig.equals(text));
	}
}
