package net.digitalid.database.conversion.columndeclarations;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.collections.list.FreezableArrayList;
import net.digitalid.utility.conversion.converter.CustomAnnotation;
import net.digitalid.utility.conversion.converter.CustomField;
import net.digitalid.utility.immutable.ImmutableList;
import net.digitalid.utility.tuples.Pair;
import net.digitalid.utility.validation.annotations.math.NonNegative;

import net.digitalid.database.core.SQLType;
import net.digitalid.database.core.Site;
import net.digitalid.database.core.Table;
import net.digitalid.database.core.Tables;
import net.digitalid.database.dialect.ast.identifier.SQLColumnName;
import net.digitalid.database.dialect.ast.identifier.SQLQualifiedTableName;
import net.digitalid.database.dialect.ast.statement.insert.SQLInsertStatement;
import net.digitalid.database.dialect.ast.statement.table.create.SQLTypeNode;

/**
 * Implements column declarations for the insertion of SQL data. The column declaration is essentially the {@link SQLColumnName SQL column name} type.
 * 
 * @see SQLColumnDeclarations
 */
public class SQLInsertIntoTableColumnDeclarations extends SQLColumnDeclarations<@Nonnull SQLInsertIntoTableColumnDeclarations, @Nonnull SQLColumnName<?>, @Nonnull SQLInsertStatement> {
    
    /* -------------------------------------------------- SQL Column Declarations -------------------------------------------------- */
    
    @Pure
    @Override
    protected @Nonnull Pair<@Nonnull SQLColumnName<?>, @Nonnull ImmutableList<@Nonnull CustomAnnotation>> getColumnDeclaration(@Nonnull String columnName, @Nullable SQLTypeNode type, @Nonnull ImmutableList<CustomAnnotation> annotations) {
        return Pair.of(SQLColumnName.get(columnName), annotations);
    }
    
    @Pure
    @Override
    protected @Nonnull Pair<@Nonnull SQLColumnName<?>, @Nonnull ImmutableList<@Nonnull CustomAnnotation>> fromField(@Nonnull CustomField field) {
        return getColumnDeclaration(field.getName(), null, field.getAnnotations());
    }
    
    /* -------------------------------------------------- Constructor -------------------------------------------------- */
    
    private SQLInsertIntoTableColumnDeclarations(@Nonnull String tableName, @Nonnull Site site, @NonNegative int currentColumn) {
        super(tableName, site, currentColumn);
    }
    
    @Pure
    public static @Nonnull SQLInsertIntoTableColumnDeclarations get(@Nonnull String tableName, @Nonnull Site site) {
        return new SQLInsertIntoTableColumnDeclarations(tableName, site, 0);
    }
    
    @Pure
    @Override
    protected @Nonnull SQLInsertIntoTableColumnDeclarations getInstance(@Nonnull String tableName, @Nonnull Site site, @NonNegative int currentColumn) {
        return new SQLInsertIntoTableColumnDeclarations(tableName, site, currentColumn);
    }
    
    /* -------------------------------------------------- Helper Methods -------------------------------------------------- */
    
    @Pure
    @Override
    protected boolean isColumnDeclaration(@Nonnull String columnName, @Nonnull SQLColumnName<?> columnDeclaration) {
        return columnDeclaration.getValue().equals(columnName);
    }
    
    @Pure
    @Override
    protected @Nonnull SQLTypeNode getColumnType(@Nonnull SQLColumnName<?> columnDeclaration) {
        final @Nonnull Table table = Tables.get(site.getDatabaseName() + "." + tableName);
        return SQLTypeNode.of(SQLType.of(table.getTypeOfColumn(columnDeclaration.getValue())));
    }
    
    @Pure
    @Override
    protected @Nonnull String getColumnName(@Nonnull SQLColumnName<?> columnDeclaration) {
        return columnDeclaration.getValue();
    }
    
    /* -------------------------------------------------- Statement -------------------------------------------------- */
    
    @Pure
    @Override
    @SuppressWarnings("unchecked")
    public @Nonnull SQLInsertStatement getStatement() {
        return SQLInsertStatement.get(SQLQualifiedTableName.get(tableName, site), FreezableArrayList.withElementsOf(getColumnDeclarationList().map(Pair::get0)));
    }
    
}