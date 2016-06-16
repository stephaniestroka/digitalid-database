package net.digitalid.database.dialect.ast.statement.select;

import javax.annotation.Nonnull;

import net.digitalid.utility.collections.readonly.ReadOnlyList;
import net.digitalid.utility.exceptions.InternalException;
import net.digitalid.utility.string.iterable.IterableConverter;
import net.digitalid.utility.validation.annotations.elements.NonNullableElements;
import net.digitalid.utility.validation.annotations.reference.NonCapturable;
import net.digitalid.utility.validation.annotations.size.MinSize;

import net.digitalid.database.core.table.Site;
import net.digitalid.database.dialect.ast.SQLDialect;
import net.digitalid.database.dialect.ast.SQLNode;
import net.digitalid.database.dialect.ast.Transcriber;
import net.digitalid.database.dialect.ast.utility.SQLNodeConverter;

/**
 * This SQL node represents the order clause of an SQL select statement.
 */
public class SQLOrderByClause implements SQLNode<SQLOrderByClause> {
    
    /* -------------------------------------------------- Final Fields -------------------------------------------------- */
    
    /**
     * A list of ordering terms.
     */
    public final @Nonnull @MinSize(1) @NonNullableElements ReadOnlyList<SQLOrderingTerm> orderingTerms;
    
    /* -------------------------------------------------- Constructor -------------------------------------------------- */
    
    /**
     * Constructs a new order-by clause node with a given list of ordering terms.
     */
    private SQLOrderByClause(@Nonnull @MinSize(1) @NonNullableElements ReadOnlyList<SQLOrderingTerm> orderingTerms) {
        this.orderingTerms = orderingTerms;
    }
    
    /**
     * Returns a new order-by clause node with a given list of ordering terms.
     */
    public static @Nonnull SQLOrderByClause get(@Nonnull @MinSize(1) @NonNullableElements ReadOnlyList<SQLOrderingTerm> orderingTerms) {
        return new SQLOrderByClause(orderingTerms);
    }
    
    /* -------------------------------------------------- Transcriber -------------------------------------------------- */
    
    /**
     * The transcriber that stores a string representation of this SQL node in the string builder.
     */
    private static final @Nonnull Transcriber<SQLOrderByClause> transcriber = new Transcriber<SQLOrderByClause>() {
        
        @Override
        protected void transcribe(@Nonnull SQLDialect dialect, @Nonnull SQLOrderByClause node, @Nonnull Site site, @Nonnull @NonCapturable StringBuilder string, boolean parameterizable) throws InternalException {
            string.append("ORDER BY ");
            string.append(IterableConverter.toString(node.orderingTerms, SQLNodeConverter.get(dialect, site)));
        }
        
    };
    
    @Override 
    public @Nonnull Transcriber<SQLOrderByClause> getTranscriber() {
        return transcriber;
    }
    
}
