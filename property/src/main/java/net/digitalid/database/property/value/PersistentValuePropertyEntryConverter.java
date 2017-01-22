package net.digitalid.database.property.value;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.annotations.ownership.Capturable;
import net.digitalid.utility.annotations.ownership.NonCaptured;
import net.digitalid.utility.annotations.parameter.Modified;
import net.digitalid.utility.annotations.parameter.Unmodified;
import net.digitalid.utility.collaboration.annotations.Review;
import net.digitalid.utility.collaboration.annotations.TODO;
import net.digitalid.utility.collaboration.enumerations.Author;
import net.digitalid.utility.collaboration.enumerations.Priority;
import net.digitalid.utility.conversion.model.CustomAnnotation;
import net.digitalid.utility.conversion.model.CustomField;
import net.digitalid.utility.conversion.interfaces.Decoder;
import net.digitalid.utility.conversion.interfaces.Encoder;
import net.digitalid.utility.conversion.enumerations.Representation;
import net.digitalid.utility.conversion.model.CustomType;
import net.digitalid.utility.generator.annotations.generators.GenerateBuilder;
import net.digitalid.utility.generator.annotations.generators.GenerateSubclass;
import net.digitalid.utility.immutable.ImmutableList;
import net.digitalid.utility.exceptions.ExternalException;
import net.digitalid.utility.validation.annotations.string.DomainName;
import net.digitalid.utility.validation.annotations.type.Immutable;

import net.digitalid.database.annotations.constraints.PrimaryKey;
import net.digitalid.database.annotations.type.Embedded;
import net.digitalid.database.auxiliary.Time;
import net.digitalid.database.auxiliary.TimeConverter;
import net.digitalid.database.property.PersistentPropertyEntryConverter;
import net.digitalid.database.subject.Subject;
import net.digitalid.database.unit.Unit;

/**
 * This class converts the {@link PersistentValuePropertyEntry entries} of the {@link PersistentValuePropertyTable value property table}.
 */
@Immutable
@GenerateBuilder
@GenerateSubclass
public abstract class PersistentValuePropertyEntryConverter<SITE extends Unit<?>, SUBJECT extends Subject<SITE>, VALUE, PROVIDED_FOR_VALUE> extends PersistentPropertyEntryConverter<SITE, SUBJECT, PersistentValuePropertyEntry<SUBJECT, VALUE>> {
    
    /* -------------------------------------------------- Property Table -------------------------------------------------- */
    
    @Pure
    @Override
    public abstract @Nonnull PersistentValuePropertyTable<SITE, SUBJECT, VALUE, PROVIDED_FOR_VALUE> getPropertyTable();
    
    /* -------------------------------------------------- Type -------------------------------------------------- */
    
    @Pure
    @Override
    public @Nonnull Class<? super PersistentValuePropertyEntry<SUBJECT, VALUE>> getType() {
        return PersistentValuePropertyEntry.class;
    }
    
    /* -------------------------------------------------- Package -------------------------------------------------- */
    
    @Pure
    @Override
    public @Nonnull @DomainName String getTypePackage() {
        return "net.digitalid.database.property.value";
    }
    
    /* -------------------------------------------------- Fields -------------------------------------------------- */
    
    @Pure
    @Override
    @TODO(task = "Support @Cached on methods without parameters.", date = "2016-09-24", author = Author.KASPAR_ETTER, assignee = Author.STEPHANIE_STROKA, priority = Priority.LOW)
    public @Nonnull ImmutableList<@Nonnull CustomField> getFields(@Nonnull Representation representation) {
        return ImmutableList.withElements(
                CustomField.with(CustomType.TUPLE.of(getPropertyTable().getParentModule().getSubjectConverter()), getPropertyTable().getParentModule().getSubjectConverter().getTypeName(), ImmutableList.withElements(CustomAnnotation.with(PrimaryKey.class), CustomAnnotation.with(Nonnull.class), CustomAnnotation.with(Embedded.class))),
                CustomField.with(CustomType.TUPLE.of(TimeConverter.INSTANCE), "time", ImmutableList.withElements(CustomAnnotation.with(Nonnull.class), CustomAnnotation.with(Embedded.class))),
                CustomField.with(CustomType.TUPLE.of(getPropertyTable().getValueConverter()), "value", ImmutableList.withElements(CustomAnnotation.with(Embedded.class)/* TODO: Pass them? Probably pass the whole custom field instead. */))
        );
    }
    
    /* -------------------------------------------------- Convert -------------------------------------------------- */
    
    @Pure
    @Override
    public <X extends ExternalException> int convert(@Nullable @NonCaptured @Unmodified PersistentValuePropertyEntry<SUBJECT, VALUE> entry, @Nonnull @NonCaptured @Modified Encoder<X> encoder) throws X {
        int i = 1;
        i *= getPropertyTable().getParentModule().getSubjectConverter().convert(entry == null ? null : entry.getSubject(), encoder);
        i *= TimeConverter.INSTANCE.convert(entry == null ? null : entry.getTime(), encoder);
        i *= getPropertyTable().getValueConverter().convert(entry == null ? null : entry.getValue(), encoder);
        return i;
    }
    
    /* -------------------------------------------------- Recover -------------------------------------------------- */
    
    @Pure
    @Override
    @Review(comment = "How would you handle the nullable recovered objects?", date = "2016-09-30", author = Author.KASPAR_ETTER, assignee = Author.STEPHANIE_STROKA, priority = Priority.LOW)
    public @Capturable <X extends ExternalException> @Nullable PersistentValuePropertyEntry<SUBJECT, VALUE> recover(@Nonnull @NonCaptured @Modified Decoder<X> decoder, @Nonnull SITE site) throws X {
        final @Nullable SUBJECT subject = getPropertyTable().getParentModule().getSubjectConverter().recover(decoder, site);
        final @Nullable Time time = TimeConverter.INSTANCE.recover(decoder, null);
        if (subject != null && time != null) {
            final VALUE value = getPropertyTable().getValueConverter().recover(decoder, getPropertyTable().getProvidedObjectExtractor().evaluate(subject));
            return new PersistentValuePropertyEntrySubclass<>(subject, time, value);
        } else {
            return null;
        }
    }
    
}
