package net.shrine.webclient.client.domain;

import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.shrine.webclient.client.AbstractWebclientTest;
import net.shrine.webclient.client.util.Observable;

import org.junit.Test;

/**
 * 
 * @author clint
 * @date Apr 23, 2012
 */
public class ExpressionXmlTestGwt extends AbstractWebclientTest {
	@Test
	public void testFromQueryGroups() {
		 
		try {
			ExpressionXml.fromQueryGroups(Collections.<QueryGroup>emptyList());
			
			fail("Should have thrown");
		} catch (IllegalArgumentException expected) { }
		
		final Term t1 = new Term("foo");
		
		final QueryGroup queryGroup1 = new QueryGroup(t1, Observable.<HashMap<String, IntWrapper>>empty());
		
		{
			final List<QueryGroup> queryGroups = asList(queryGroup1);
			
			final String xml = ExpressionXml.fromQueryGroups(queryGroups);
			
			assertEquals(t1.toXmlString(), xml);
		}
		
		{
			final Term t2 = new Term("bar");
			
			final QueryGroup queryGroup2 = new QueryGroup(t2, Observable.<HashMap<String, IntWrapper>>empty());
			
			final String xml = ExpressionXml.fromQueryGroups(asList(queryGroup1, queryGroup2));
			
			assertEquals(new And(t1, t2).toXmlString(), xml);
		}
	}
}