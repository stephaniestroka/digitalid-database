package net.digitalid.database.dialect.ast.expression.number;

import javax.annotation.Nonnull;

import net.digitalid.utility.exceptions.InternalException;
import net.digitalid.utility.exceptions.internal.UncoveredCaseException;
import net.digitalid.utility.validation.annotations.reference.NonCapturable;
import net.digitalid.utility.validation.annotations.type.Immutable;

import net.digitalid.database.dialect.ast.SQLDialect;
import net.digitalid.database.dialect.ast.Transcriber;
import net.digitalid.database.dialect.ast.expression.SQLUnaryOperator;
import net.digitalid.database.core.table.Site;

/**
 * This class enumerates the supported unary number operators.
 */
@Immutable
public enum SQLUnaryNumberOperator implements SQLUnaryOperator {
    
    /* -------------------------------------------------- Constants -------------------------------------------------- */
    
    /**
     * This operator returns the rounded value.
     */
    ROUND(),
    
    /**
     * This operator returns the negated value.
     */
    NEGATE(),
    
    /**
     * This operator returns the absolute value.
     */
    ABSOLUTE();
    
    /* -------------------------------------------------- SQLNode -------------------------------------------------- */
    
    /**
     * The transcriber that stores a string representation of this SQL node in the string builder.
     */
    private static final @Nonnull Transcriber<SQLUnaryNumberOperator> transcriber = new Transcriber<SQLUnaryNumberOperator>() {
        
        @Override
        protected void transcribe(@Nonnull SQLDialect dialect, @Nonnull SQLUnaryNumberOperator node, @Nonnull Site site, @Nonnull @NonCapturable StringBuilder string, boolean parameterizable) throws InternalException {
            switch (node) {
                case ROUND: string.append("ROUND"); break;
                case NEGATE: string.append("-"); break;
                case ABSOLUTE: string.append("ABS"); break;
                default: throw UncoveredCaseException.with(node.name() + " not implemented.");
            }
        }
        
    };
    
    @Override
    public @Nonnull Transcriber<SQLUnaryNumberOperator> getTranscriber() {
        return transcriber;
    }
    
}
