package com.sunyard.insurance.filenet.ce.base;

import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.constants.MergeMode;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;

public class CeQuery {

	protected Integer pageSize = null;
	protected Boolean continuable = null;
	
	public CeQuery(int pageSize, boolean continuable) {
		super();
		this.pageSize = new Integer(pageSize);
		this.continuable = new Boolean(continuable);
	}
	
	public IndependentObjectSet searchObjects(String sql, ObjectStore... stores) {
		return searchObjects(sql, pageSize, null, continuable, stores);
	}

	public IndependentObjectSet searchObjects(String sql, int pageSize,
			PropertyFilter filter, boolean continuable, ObjectStore... stores) {
		SearchSQL sSql = new SearchSQL();
		sSql.setQueryString(sql);
		SearchScope sScope = new SearchScope(stores, MergeMode.INTERSECTION);
		IndependentObjectSet objSet = sScope.fetchObjects(sSql, pageSize, filter, continuable);
		return objSet;
	}

	public RepositoryRowSet searchRows(String sql, ObjectStore... stores) {
		return searchRows(sql, pageSize, null, continuable, stores);
	}

	public RepositoryRowSet searchRows(String sql, int pageSize,
			PropertyFilter filter, boolean continuable, ObjectStore... stores) {
		SearchSQL sSql = new SearchSQL();
		sSql.setQueryString(sql);
		SearchScope sScope = new SearchScope(stores, MergeMode.INTERSECTION);
		RepositoryRowSet rowSet = sScope.fetchRows(sSql, pageSize, filter, continuable);
		return rowSet;
	}
	
}
