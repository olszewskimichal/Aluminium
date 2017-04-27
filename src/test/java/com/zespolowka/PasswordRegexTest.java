package com.zespolowka;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.springframework.test.context.ActiveProfiles;

/**
 * Created by Pitek on 2015-12-03.
 */
@ActiveProfiles("test")
public class PasswordRegexTest {
	@Test
	public void checkValidation() throws Exception {
		String regex = "^(?=.*?[A-Z])(?=(.*[a-z]){1,})(?=(.*[\\d]){1,})(?=(.*[\\W]){1,})(?!.*\\s).{8,}$";

		assertEquals(true, "zaq1@WSX".matches(regex));
		assertEquals(false, "zaq1@WS".matches(regex));
		assertEquals(false, "1111111111".matches(regex));
		assertEquals(false, "!!!!!!!!!!!".matches(regex));
		assertEquals(false, "        zaq1@WSX".matches(regex));
		assertEquals(false, "aaaaaaaaaaaa".matches(regex));
		assertEquals(false, "m@łpa1_\\".matches(regex));
		assertEquals(true, "Da✡mian1993".matches(regex));
		assertEquals(true, "\\u2721_\\bCoś_".matches(regex));
		assertEquals(false, ".*\\".matches(regex));
	}
}
