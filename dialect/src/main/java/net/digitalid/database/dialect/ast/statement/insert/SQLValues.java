package net.digitalid.database.dialect.ast.statement.insert;

import javax.annotation.Nonnull;

import net.digitalid.utility.castable.exceptions.InvalidClassCastException;
import net.digitalid.utility.collections.freezable.FreezableArrayList;
import net.digitalid.utility.exceptions.InternalException;
import net.digitalid.utility.string.iterable.Brackets;
import net.digitalid.utility.string.iterable.IterableConverter;
import net.digitalid.utility.string.iterable.NonNullableElementConverter;
import net.digitalid.utility.validation.annotations.elements.NonNullableElements;
import net.digitalid.utility.validation.annotations.elements.NullableElements;
import net.digitalid.utility.validation.annotations.reference.NonCapturable;

import net.digitalid.database.dialect.ast.SQLDialect;
import net.digitalid.database.dialect.ast.SQLParameterizableNode;
import net.digitalid.database.dialect.ast.Transcriber;
import net.digitalid.database.dialect.ast.expression.SQLExpression;
import net.digitalid.database.exceptions.operation.FailedValueStoringException;
import net.digitalid.database.core.interfaces.ValueCollector;
import net.digitalid.database.core.table.Site;

/**
 *
 */
public class SQLValues implements SQLParameterizableNode<SQLValues>, SQLValuesOrStatement<SQLValues> {
   
    public final @Nonnull @NonNullableElements FreezableArrayList<SQLExpression<?>> values;
    
    private SQLValues() {
        this.values = FreezableArrayList.get();
    }
    
    public static @Nonnull @NullableElements
    SQLValues get() {
        return new SQLValues();
    }
    
    @Override
    public void storeValues(@NonCapturable @Nonnull ValueCollector collector) throws FailedValueStoringException {
        for (@Nonnull SQLExpression sqlExpression : values) {
            // in the simplest case, the sqlExpression is a literal.
            sqlExpression.storeValues(collector);
        }
    }
    
    public void addValue(SQLExpression value) {
        values.add(value);
    }
    
    /* -------------------------------------------------- SQL Node -------------------------------------------------- */
    
    private static class SQLExpressionConverter implements NonNullableElementConverter<SQLExpression<?>> {
         
        private final @Nonnull SQLDialect dialect;
        private final @Nonnull Site site;
        
        SQLExpressionConverter(@Nonnull SQLDialect dialect, @Nonnull Site site) {
            this.dialect = dialect;
            this.site = site;
        }
        
        // TODO: the ElementConverter should also use the string builder instead of creating a new string everytime.
        @Override
        public @Nonnull String toString(@Nonnull SQLExpression<?> element) {
            StringBuilder string = new StringBuilder();
            try {
                dialect.transcribe(site, string, element, true);
            } catch (InternalException e) {
                e.printStackTrace();
            }
            return string.toString();
        }
        
    }
    
    /**
     * The transcriber that stores a string representation of this SQL node in the string builder.
     */
    private static final @Nonnull Transcriber<SQLValues> transcriber = new Transcriber<SQLValues>() {
        
        @Override
        protected void transcribe(@Nonnull SQLDialect dialect, @Nonnull SQLValues node, @Nonnull Site site, @Nonnull @NonCapturable StringBuilder string, boolean parameterizable) throws InternalException {
            if (node.values.size() > 0) {
                string.append("VALUES ");
                string.append(IterableConverter.toString(node.values, new SQLExpressionConverter(dialect, site), Brackets.ROUND, ", "));
            }
        }
        
    };
 
    @Override
    public @Nonnull Transcriber<SQLValues> getTranscriber() {
        return transcriber;
    }
    
    @Nonnull
    @Override
    public <T> T castTo(@Nonnull Class<T> targetClass) throws InvalidClassCastException {
        assert targetClass.isInstance(this) : "This object can only be casted to SQLValues";
        return (T) this;
    }
}
