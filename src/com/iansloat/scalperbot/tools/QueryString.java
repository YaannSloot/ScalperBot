package com.iansloat.scalperbot.tools;

import java.util.LinkedList;
import java.util.List;

public class QueryString {
	
	private List<Query> queries;
	
	public QueryString() {
		this.queries = new LinkedList<>();
	}
	
	public void addQuery(Query query) {
		queries.add(query);
	}
	
	public void addQuery(String key, String value) {
		queries.add(new Query(key, value));
	}
	
	public List<Query> getQueries() {
		return queries;
	}

	@Override
	public String toString() {
		String qs = "";
		for(Query q : queries) {
			if(!qs.equals(""))
				qs += '&';
			qs += q;
		}
		return qs;
	}
	
}
