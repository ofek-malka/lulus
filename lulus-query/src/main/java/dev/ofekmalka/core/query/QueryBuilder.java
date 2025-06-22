package dev.ofekmalka.core.query;

import dev.ofekmalka.core.assertion.result.Result;
import dev.ofekmalka.core.data_structure.list.List;

public final class QueryBuilder {

	public static AddTables select(final String... columns) {

		return tables -> conditions -> {

			final var lColumns = List.list(columns)//
					.intersperse(",")//
					.cons("SELECT ")//
					.reduce(a -> b -> a + b)//

					.removeCalledByMethodPrefixes()//
					.prependFailureMessage("error in select method:")

			;
			final var lTables = List.list(tables)//
					.intersperse(",")///
					.cons("FROM ")//
					.reduce(a -> b -> a + b)

					.removeCalledByMethodPrefixes()//
					.prependFailureMessage("error in from method:");//
			final var lConditions = List.list(conditions)//
					.intersperse(" AND ")//
					.cons("WHERE ")//
					.reduce(a -> b -> a + b) //
					.removeCalledByMethodPrefixes()//
					.prependFailureMessage("error in where method:");//

			return List.list(lColumns, lTables, lConditions)//

					.foldLeft(Result.success(""), acc -> ele ->

					acc.flatMap(s -> ele.map(e -> s + "\n" + e)))//
					.flatMap(t -> t);//

		};

	}

	interface AddTables {

		AddConditions from(String... tables);

	}

	interface AddConditions {

		Result<String> where(String... conditions);
	}

	public static void main(final String[] args) {
//		QueryBuilder//
//				.select("first_name", "last_name")//
//				.from("employees")//
//				.where("age > 25", "department = 'IT'")//
//
//				.printlnToConsole()//
//				.run();//

		QueryBuilder.insertInto("t").columns("first_name", "last_name").values("ofek", "malka")

				.printlnToConsole()//
				.run();//

	}

	// Example for INSERT
	public static InsertInto insertInto(final String table) {
		return columns -> values -> Result.success("INSERT INTO " + table + " (" + String.join(",", columns)
				+ ") VALUES (" + String.join(",", values) + ")");
	}

	interface InsertInto {
		AddValues columns(String... columns); //
	}

	interface AddValues {
		Result<String> values(String... values); //
	}

	// Example for UPDATE
	public static UpdateTable update(final String table) {
		return setClauses -> conditions -> Result.success("UPDATE " + table + " SET " + String.join(",", setClauses)
				+ " WHERE " + String.join(" AND ", conditions));
	}

	interface UpdateTable {
		AddConditions set(String... setClauses); // e.g., "column1 = 'value1'"
	}

	// Example for DELETE
	public static DeleteFrom deleteFrom(final String table) {
		return conditions -> Result.success("DELETE FROM " + table + " WHERE " + String.join(" AND ", conditions));
	}

	interface DeleteFrom {
		Result<String> where(String... conditions);
	}

	// Usage:
	// QueryBuilder.select("name").from("users").where("id = 1").orderBy("name
	// DESC").printlnToConsole().run();
	// QueryBuilder.select("name").from("users").noConditions().noOrderBy().printlnToConsole().run();

}