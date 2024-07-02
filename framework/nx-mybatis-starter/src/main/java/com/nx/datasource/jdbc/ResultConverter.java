package com.nx.datasource.jdbc;

import java.sql.ResultSet;

public interface ResultConverter<T> {

	T convert(ResultSet rs);
}
