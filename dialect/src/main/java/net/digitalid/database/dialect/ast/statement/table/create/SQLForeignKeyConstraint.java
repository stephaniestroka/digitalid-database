package net.digitalid.database.dialect.ast.statement.table.create;

import javax.annotation.Nonnull;

import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.annotations.ownership.NonCaptured;
import net.digitalid.utility.annotations.parameter.Modified;
import net.digitalid.utility.conversion.converter.CustomAnnotation;
import net.digitalid.utility.exceptions.InternalException;

import net.digitalid.database.core.interfaces.SQLValueCollector;
import net.digitalid.database.core.Site;
import net.digitalid.database.annotations.metadata.References;
import net.digitalid.database.dialect.ast.SQLDialect;
import net.digitalid.database.exceptions.operation.FailedSQLValueConversionException;


/**
 *
 */
public class SQLForeignKeyConstraint extends SQLColumnConstraint {
    
    public final @Nonnull CustomAnnotation references;
    
    SQLForeignKeyConstraint(@Nonnull CustomAnnotation references) {
        this.references = references;
    }
    
    @Pure
    public static @Nonnull SQLForeignKeyConstraint with(@Nonnull CustomAnnotation references) {
        return new SQLForeignKeyConstraint(references);
    }
    
    @Pure
    @Override
    public @Nonnull String getConstraintDeclaration(@Nonnull SQLDialect dialect, @Nonnull SQLColumnConstraint node, @Nonnull Site site) throws InternalException {
        final @Nonnull StringBuilder string = new StringBuilder();
        //string.append("FOREIGN KEY REFERENCES ");
        string.append("REFERENCES ");
        string.append(references.get("foreignTable", String.class));
        string.append(" (");
        string.append(references.get("columnName", String.class));
        string.append(") ON DELETE ");
        final @Nonnull String onDelete;
        if (references.get("onDelete", References.Action.class) != null) {
            onDelete = references.get("onDelete", References.Action.class).value;
        } else {
            onDelete = References.Action.RESTRICT.value;
        }
        string.append(onDelete);
        string.append(" ON UPDATE ");
        final @Nonnull String onUpdate;
        if (references.get("onUpdate", References.Action.class) != null) {
            onUpdate = references.get("onUpdate", References.Action.class).value;
        } else {
            onUpdate = References.Action.RESTRICT.value;
        }
        string.append(onUpdate);
        return string.toString();
    }
    
    @Pure
    @Override
    public void storeValues(@Nonnull @NonCaptured @Modified SQLValueCollector collector) throws FailedSQLValueConversionException {}
    
}