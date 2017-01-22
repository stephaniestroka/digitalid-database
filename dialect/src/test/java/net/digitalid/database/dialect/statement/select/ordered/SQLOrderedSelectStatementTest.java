package net.digitalid.database.dialect.statement.select.ordered;

import javax.annotation.Nonnull;

import net.digitalid.utility.immutable.ImmutableList;

import net.digitalid.database.dialect.SQLDialect;
import net.digitalid.database.dialect.statement.SQLStatementTest;
import net.digitalid.database.dialect.statement.select.unordered.simple.SQLSimpleSelectStatement;
import net.digitalid.database.dialect.statement.select.unordered.simple.SQLSimpleSelectStatementBuilder;
import net.digitalid.database.dialect.statement.select.unordered.simple.columns.SQLAllColumnsBuilder;
import net.digitalid.database.dialect.statement.select.unordered.simple.sources.SQLTableSource;
import net.digitalid.database.dialect.statement.select.unordered.simple.sources.SQLTableSourceBuilder;
import net.digitalid.database.unit.Unit;

import org.junit.Test;

public class SQLOrderedSelectStatementTest extends SQLStatementTest {
    
    @Test
    public void testOrderedSelectStatement() {
        final @Nonnull SQLTableSource tableSource = SQLTableSourceBuilder.withSource(qualifiedTable).build();
        final @Nonnull SQLSimpleSelectStatement simpleSelectStatement = SQLSimpleSelectStatementBuilder.withColumns(ImmutableList.withElements(SQLAllColumnsBuilder.build())).withSources(ImmutableList.withElements(tableSource)).build();
        final @Nonnull SQLOrderingTerm orderingTerm = SQLOrderingTermBuilder.withExpression(firstColumn).withAscending(false).build();
        final @Nonnull SQLOrderedSelectStatement selectStatement = SQLOrderedSelectStatementBuilder.withSelectStatement(simpleSelectStatement).withOrders(ImmutableList.withElements(orderingTerm)).withLimit(10).withOffset(20).build();
        assertEquals("SELECT * FROM (\"default\".\"test_table\") ORDER BY \"first_column\" DESC LIMIT 10 OFFSET 20", SQLDialect.unparse(selectStatement, Unit.DEFAULT));
    }
    
}
