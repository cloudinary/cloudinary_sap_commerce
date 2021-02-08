/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.test;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import uk.ptr.cloudinary.util.ws.impl.DefaultSearchQueryCodec;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class SearchQueryDataParserTest
{

	private DefaultSearchQueryCodec defaultSearchQueryCodec;

	@Before
	public void setUp()
	{
		defaultSearchQueryCodec = new DefaultSearchQueryCodec();
	}

	@Test
	public void parseSearchQuery()
	{
		final String query = "easy::key1:value1:key2:value2";
		final SolrSearchQueryData data = defaultSearchQueryCodec.decodeQuery(query);
		assertEquals("easy", data.getFreeTextSearch());
		assertEquals(2, data.getFilterTerms().size());

		final SolrSearchQueryTermData filter1 = data.getFilterTerms().get(0);
		final SolrSearchQueryTermData filter2 = data.getFilterTerms().get(1);

		assertEquals("key1", filter1.getKey());
		assertEquals("value1", filter1.getValue());

		assertEquals("key2", filter2.getKey());
		assertEquals("value2", filter2.getValue());
	}

	@Test
	public void parseSearchQueryFreeTextOnly()
	{
		final String query = "easy";
		final SolrSearchQueryData data = defaultSearchQueryCodec.decodeQuery(query);
		assertEquals("easy", data.getFreeTextSearch());
		assertEquals(0, data.getFilterTerms().size());
	}

	@Test
	public void parseSearchQueryFreeTextAndSort()
	{
		final String query = "easy:somesort";
		final SolrSearchQueryData data = defaultSearchQueryCodec.decodeQuery(query);
		assertEquals("easy", data.getFreeTextSearch());
		assertEquals("somesort", data.getSort());
		assertEquals(0, data.getFilterTerms().size());
	}

	@Test
	public void parseSearchQueryWithSort()
	{
		final String query = "easy:somesort:key1:value1:key2:value2";
		final SolrSearchQueryData data = defaultSearchQueryCodec.decodeQuery(query);
		assertEquals("easy", data.getFreeTextSearch());
		assertEquals("somesort", data.getSort());
		assertEquals(2, data.getFilterTerms().size());

		final SolrSearchQueryTermData filter1 = data.getFilterTerms().get(0);
		final SolrSearchQueryTermData filter2 = data.getFilterTerms().get(1);

		assertEquals("key1", filter1.getKey());
		assertEquals("value1", filter1.getValue());

		assertEquals("key2", filter2.getKey());
		assertEquals("value2", filter2.getValue());
	}

	@Test
	public void serializeComplete()
	{
		final SolrSearchQueryData query = new SolrSearchQueryData();
		query.setFreeTextSearch("a");
		query.setSort("somesort");

		final List<SolrSearchQueryTermData> terms = new ArrayList<SolrSearchQueryTermData>();
		final SolrSearchQueryTermData term1 = createSearchQueryTermData("key1", "value1");
		final SolrSearchQueryTermData term2 = createSearchQueryTermData("key2", "value2");

		terms.add(term1);
		terms.add(term2);

		query.setFilterTerms(terms);

		assertEquals("a:somesort:key1:value1:key2:value2", defaultSearchQueryCodec.encodeQuery(query));
	}

	protected SolrSearchQueryTermData createSearchQueryTermData(final String key, final String value)
	{
		final SolrSearchQueryTermData term = new SolrSearchQueryTermData();
		term.setKey(key);
		term.setValue(value);
		return term;
	}

	@Test
	public void serializeFreeTextAndTermsOnly()
	{
		final SolrSearchQueryData query = new SolrSearchQueryData();
		query.setFreeTextSearch("a");
		query.setSort("somesort");

		assertEquals("a:somesort", defaultSearchQueryCodec.encodeQuery(query));
	}

	@Test
	public void serializeFreeTextAndSort()
	{
		final SolrSearchQueryData query = new SolrSearchQueryData();
		query.setFreeTextSearch("a");

		final List<SolrSearchQueryTermData> terms = new ArrayList<SolrSearchQueryTermData>();
		final SolrSearchQueryTermData term1 = createSearchQueryTermData("key1", "value1");
		final SolrSearchQueryTermData term2 = createSearchQueryTermData("key2", "value2");

		terms.add(term1);
		terms.add(term2);

		query.setFilterTerms(terms);

		assertEquals("a::key1:value1:key2:value2", defaultSearchQueryCodec.encodeQuery(query));
	}

}
