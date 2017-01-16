package net.digitalid.database.property.set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.digitalid.utility.annotations.method.CallSuper;
import net.digitalid.utility.annotations.method.Impure;
import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.annotations.ownership.Captured;
import net.digitalid.utility.annotations.ownership.NonCaptured;
import net.digitalid.utility.annotations.parameter.Unmodified;
import net.digitalid.utility.annotations.type.ThreadSafe;
import net.digitalid.utility.collaboration.annotations.TODO;
import net.digitalid.utility.collaboration.enumerations.Author;
import net.digitalid.utility.collaboration.enumerations.Priority;
import net.digitalid.utility.collections.set.FreezableSet;
import net.digitalid.utility.collections.set.ReadOnlySet;
import net.digitalid.utility.concurrency.exceptions.ReentranceException;
import net.digitalid.utility.contracts.Validate;
import net.digitalid.utility.freezable.annotations.NonFrozen;
import net.digitalid.utility.functional.interfaces.Predicate;
import net.digitalid.utility.generator.annotations.generators.GenerateBuilder;
import net.digitalid.utility.generator.annotations.generators.GenerateSubclass;
import net.digitalid.utility.property.set.WritableSetPropertyImplementation;
import net.digitalid.utility.validation.annotations.value.Valid;

import net.digitalid.database.annotations.transaction.Committing;
import net.digitalid.database.annotations.transaction.NonCommitting;
import net.digitalid.database.conversion.SQL;
import net.digitalid.database.dialect.identifier.SQLBooleanAlias;
import net.digitalid.database.exceptions.DatabaseException;
import net.digitalid.database.subject.Subject;
import net.digitalid.database.subject.site.Site;

/**
 * This class implements the {@link WritablePersistentSetProperty}.
 * 
 * @see WritablePersistentSimpleSetPropertyImplementation
 */
@ThreadSafe
@GenerateBuilder
@GenerateSubclass
public abstract class WritablePersistentSetPropertyImplementation<SITE extends Site<?>, SUBJECT extends Subject<SITE>, VALUE, READONLY_SET extends ReadOnlySet<@Nonnull @Valid VALUE>, FREEZABLE_SET extends FreezableSet<@Nonnull @Valid VALUE>> extends WritableSetPropertyImplementation<VALUE, READONLY_SET, DatabaseException, PersistentSetObserver<SUBJECT, VALUE, READONLY_SET>, ReadOnlyPersistentSetProperty<SUBJECT, VALUE, READONLY_SET>> implements WritablePersistentSetProperty<SUBJECT, VALUE, READONLY_SET, FREEZABLE_SET> {
    
    /* -------------------------------------------------- Validator -------------------------------------------------- */
    
    @Pure
    @Override
    public @Nonnull Predicate<? super VALUE> getValueValidator() {
        return getTable().getValueValidator();
    }
    
    /* -------------------------------------------------- Set -------------------------------------------------- */
    
    @Pure
    protected abstract @Nonnull @NonFrozen FREEZABLE_SET getSet();
    
    /* -------------------------------------------------- Table -------------------------------------------------- */
    
    @Pure
    @Override
    public abstract @Nonnull PersistentSetPropertyTable<SITE, SUBJECT, VALUE, ?> getTable();
    
    /* -------------------------------------------------- Loading -------------------------------------------------- */
    
    protected boolean loaded = false;
    
    /**
     * Loads the values of this property from the database.
     * 
     * @param locking whether this method acquires the non-reentrant lock.
     */
    @Pure
    @NonCommitting
    @TODO(task = "Instead of reading and adding a single entry, select and add all entries of the subject to the freezable set.", date = "2016-09-30", author = Author.KASPAR_ETTER, assignee = Author.STEPHANIE_STROKA, priority = Priority.HIGH)
    protected void load(final boolean locking) throws DatabaseException, ReentranceException {
        if (locking) { lock.lock(); }
        try {
            getSet().clear();
            final @Nullable PersistentSetPropertyEntry<SUBJECT, VALUE> entry = SQL.select(getTable().getEntryConverter(), SQLBooleanAlias.with("key = 'TODO'"), getSubject().getSite(), getSubject().getSite());
            if (entry != null) {
                getSet().add(entry.getValue());
            }
            this.loaded = true;
        } finally {
            if (locking) { lock.unlock(); }
        }
    }
    
    /* -------------------------------------------------- Getter -------------------------------------------------- */
    
    @Pure
    @Override
    @NonCommitting
    @SuppressWarnings("unchecked")
    public @Nonnull @NonFrozen READONLY_SET get() throws DatabaseException {
        if (!loaded) { load(true); } // This should never trigger a reentrance exception as add(value), remove(value) and reset() that call external code ensure that the set is loaded.
        return (READONLY_SET) getSet();
    }
    
    /* -------------------------------------------------- Operations -------------------------------------------------- */
    
    @Impure
    @Override
    @Committing
    public boolean add(@Captured @Nonnull @Valid VALUE value) throws DatabaseException, ReentranceException {
        lock.lock();
        try {
            if (!loaded) { load(false); }
            if (getSet().contains(value)) {
                return false;
            } else {
                final @Nonnull PersistentSetPropertyEntry<SUBJECT, VALUE> entry = new PersistentSetPropertyEntrySubclass<>(getSubject(), value);
                SQL.insert(entry, getTable().getEntryConverter(), getSubject().getSite());
                getSet().add(value);
                notifyObservers(value, true);
                return true;
            }
        } finally {
            lock.unlock();
        }
    }
    
    @Impure
    @Override
    @Committing
    @TODO(task = "Implement and use SQL.delete().", date = "2016-09-30", author = Author.KASPAR_ETTER, assignee = Author.STEPHANIE_STROKA, priority = Priority.HIGH)
    public boolean remove(@NonCaptured @Unmodified @Nonnull @Valid VALUE value) throws DatabaseException, ReentranceException {
        lock.lock();
        try {
            if (!loaded) { load(false); }
            if (getSet().contains(value)) {
                final @Nonnull PersistentSetPropertyEntry<SUBJECT, VALUE> entry = new PersistentSetPropertyEntrySubclass<>(getSubject(), value);
                SQL.insert(entry, getTable().getEntryConverter(), getSubject().getSite()); // TODO: SQL.delete()
                getSet().remove(value);
                notifyObservers(value, false);
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }
    
    /* -------------------------------------------------- Reset -------------------------------------------------- */
    
    @Impure
    @Override
    @NonCommitting
    public void reset() throws DatabaseException, ReentranceException {
        lock.lock();
        try {
            if (loaded) {
                if (observers.isEmpty()) {
                    this.loaded = false;
                } else {
                    final @Nonnull FreezableSet<VALUE> oldSet = getSet().clone();
                    load(false);
                    final @Nonnull FreezableSet<VALUE> newSet = getSet();
                    for (@Nonnull @Valid VALUE value : newSet.exclude(oldSet)) {
                        notifyObservers(value, true);
                    }
                    for (@Nonnull @Valid VALUE value : oldSet.exclude(newSet)) {
                        notifyObservers(value, false);
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }
    
    /* -------------------------------------------------- Validate -------------------------------------------------- */
    
    @Pure
    @Override
    @CallSuper
    public void validate() {
        super.validate();
        Validate.that(!getSet().containsNull()).orThrow("None of the values may be null.");
        Validate.that(getSet().matchAll(getValueValidator())).orThrow("Each value has to be valid.");
    }
    
}