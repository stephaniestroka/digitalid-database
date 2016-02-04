package net.digitalid.database.dialect.ast.statement.table.create;

import java.util.Arrays;
import javax.annotation.Nonnull;

import net.digitalid.database.core.interfaces.ValueCollector;
import net.digitalid.database.core.table.Site;
import net.digitalid.database.dialect.annotations.References;
import net.digitalid.database.dialect.ast.SQLDialect;
import net.digitalid.database.exceptions.operation.FailedValueStoringException;

import net.digitalid.utility.exceptions.InternalException;
import net.digitalid.utility.string.iterable.Brackets;
import net.digitalid.utility.string.iterable.IterableConverter;
import net.digitalid.utility.validation.annotations.reference.NonCapturable;

/**
 *
 */
public class SQLForeignKeyConstraint extends SQLColumnConstraint {
    
    private final @Nonnull References references;
    
    SQLForeignKeyConstraint(@Nonnull References references) {
        this.references = references;
    }
    
    @Override
    public void getConstraintDeclaration(@Nonnull SQLDialect dialect, @Nonnull SQLColumnConstraint node, @Nonnull Site site, @Nonnull @NonCapturable StringBuilder string) throws InternalException {
        //string.append("FOREIGN KEY REFERENCES ");
        string.append("REFERENCES ");
        string.append(references.foreignTable());
        string.append(" (");
        string.append(references.columnName());
        string.append(") ON DELETE ");
        string.append(references.onDelete().value);
        string.append(" ON UPDATE ");
        string.append(references.onUpdate().value);
    }
    
    @Override
    public void storeValues(@NonCapturable @Nonnull ValueCollector collector) throws FailedValueStoringException {}
    
}
