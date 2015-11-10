package net.digitalid.utility.database.table;

import java.sql.SQLException;
import javax.annotation.Nonnull;
import net.digitalid.utility.annotations.state.Pure;
import net.digitalid.utility.annotations.state.Validated;
import net.digitalid.utility.database.annotations.Locked;
import net.digitalid.utility.database.annotations.NonCommitting;
import net.digitalid.utility.database.site.Site;

/**
 * This interface models a database table.
 */
public interface DatabaseTable {
    
    /**
     * Returns the name of this table with the prefix of the given site.
     * 
     * @param site the site whose prefix is to be used for the returned name.
     * 
     * @return the name of this table with the prefix of the given site.
     */
    @Pure
    public @Nonnull @Validated String getName(@Nonnull Site site);
    
    /**
     * Creates this table for the given site.
     * 
     * @param site the site for which to create this table.
     */
    @Locked
    @NonCommitting
    public void create(@Nonnull Site site) throws SQLException;
    
}
