package net.digitalid.database.sqlite;

import java.util.Map;

import javax.annotation.Nonnull;

import net.digitalid.utility.collections.annotations.elements.NonNullableElements;
import net.digitalid.utility.exceptions.InternalException;
import net.digitalid.utility.validation.reference.NonCapturable;

import net.digitalid.database.dialect.ast.SQLDialect;
import net.digitalid.database.dialect.ast.SQLNode;
import net.digitalid.database.dialect.ast.Transcriber;
import net.digitalid.database.dialect.ast.expression.bool.SQLBooleanLiteral;
import net.digitalid.database.core.Site;

/**
 *
 */
public class SQLiteDialect extends SQLDialect {

    private static @Nonnull @NonNullableElements Map<Class<?>, Transcriber<?>> dialectSpecificTranscribers;
   
    private static void register(Transcriber<?> transcriber) {
        dialectSpecificTranscribers.put(transcriber.type, transcriber);
    }
    
    static {
        register(new Transcriber<SQLBooleanLiteral>(SQLBooleanLiteral.class) {

            @Override
            protected void transcribe(@Nonnull SQLDialect dialect, @Nonnull SQLBooleanLiteral node, @Nonnull Site site, @Nonnull @NonCapturable StringBuilder string, boolean parameterizable) throws InternalException {
                string.append(node.getValue() ? "1" : "0");
            }
        });
    }
    
    @Override
    public void transcribe(@Nonnull Site site, @NonCapturable @Nonnull StringBuilder string, @Nonnull SQLNode<?> node, boolean parameterizable) throws InternalException {
        if (dialectSpecificTranscribers.containsKey(node.getClass())) {
            dialectSpecificTranscribers.get(node.getClass()).transcribeNode(this, node, site, string);
        } else {
            super.transcribe(site, string, node, parameterizable);
        }
    }
    
}