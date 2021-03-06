/*
 * Copyright (C) 2017 Synacts GmbH, Switzerland (info@synacts.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.digitalid.database.property.value;

import java.util.Map;

import javax.annotation.Nonnull;

import net.digitalid.utility.annotations.method.Impure;
import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.collaboration.annotations.TODO;
import net.digitalid.utility.collaboration.enumerations.Author;
import net.digitalid.utility.collections.set.ReadOnlySet;
import net.digitalid.utility.conversion.exceptions.RecoveryException;
import net.digitalid.utility.freezable.annotations.NonFrozen;
import net.digitalid.utility.generator.annotations.generators.GenerateBuilder;
import net.digitalid.utility.generator.annotations.generators.GenerateSubclass;
import net.digitalid.utility.generator.annotations.generators.GenerateTableConverter;
import net.digitalid.utility.storage.interfaces.Unit;
import net.digitalid.utility.validation.annotations.generation.Default;
import net.digitalid.utility.validation.annotations.type.Immutable;
import net.digitalid.utility.validation.annotations.value.Valid;

import net.digitalid.database.annotations.constraints.PrimaryKey;
import net.digitalid.database.conversion.SQL;
import net.digitalid.database.exceptions.DatabaseException;
import net.digitalid.database.interfaces.Database;
import net.digitalid.database.property.annotations.GeneratePersistentProperty;
import net.digitalid.database.property.map.WritablePersistentSimpleMapProperty;
import net.digitalid.database.property.set.WritablePersistentSimpleSetProperty;
import net.digitalid.database.property.subject.Subject;
import net.digitalid.database.testing.DatabaseTest;

import org.junit.BeforeClass;
import org.junit.Test;

@Immutable
@GenerateBuilder
@GenerateSubclass
@GenerateTableConverter
abstract class Student extends Subject<Unit> {
    
    /* -------------------------------------------------- Key -------------------------------------------------- */
    
    @Pure
    @PrimaryKey
    public abstract long getKey();
    
    @Pure
    @Default("\"default\"")
    @GeneratePersistentProperty
    public abstract @Nonnull WritablePersistentValueProperty<Student, @Nonnull String> name();
    
    @Pure
    @GeneratePersistentProperty
    public abstract @Nonnull WritablePersistentValueProperty<Student, Student> spouse();
    
    @Pure
    @Default("0")
    @GeneratePersistentProperty
    public abstract @Nonnull WritablePersistentValueProperty<Student, @Nonnull Integer> age();
    
    @Pure
    @GeneratePersistentProperty
    @TODO(task = "Introduce a way to pass a 'provided object extractor' to the friends table that provides the unit of the subject to the value recovery .", date = "2017-02-17", author = Author.KASPAR_ETTER)
    public abstract @Nonnull WritablePersistentSimpleSetProperty<Student, Student> friends();

    @Pure
    @GeneratePersistentProperty
    public abstract @Nonnull WritablePersistentSimpleMapProperty<Student, Integer, Integer> grades();
    
}

public class ValuePropertyTest extends DatabaseTest {
    
    private static final @Nonnull Student object = StudentBuilder.withKey(123).build();
    
    private static final @Nonnull Student friend = StudentBuilder.withKey(124).build();
    
    @Impure
    @BeforeClass
    public static void createTables() throws Exception {
        SQL.createTable(StudentConverter.INSTANCE, Unit.DEFAULT);
        StudentSubclass.MODULE.accept(table -> SQL.createTable(table, Unit.DEFAULT));
        SQL.insertOrAbort(StudentConverter.INSTANCE, object, Unit.DEFAULT);
        SQL.insertOrAbort(StudentConverter.INSTANCE, friend, Unit.DEFAULT);
        Database.commit();
    }
    
    @Test
    public void testStringProperty() throws DatabaseException, RecoveryException {
        object.name().set("test");
        object.name().reset();
        assertThat(object.name().get()).isEqualTo("test");
    }
    
    @Test
    public void testIntegerProperty() throws DatabaseException, RecoveryException {
        object.age().set(29);
        object.age().reset();
        assertThat(object.age().get()).isEqualTo(29);
    }
    
    @Test
    public void testFriendsProperty() throws DatabaseException, RecoveryException {
        object.friends().add(object);
        object.friends().add(friend);
        object.friends().reset();
        final @Nonnull @NonFrozen ReadOnlySet<@Nonnull @Valid Student> friends = object.friends().get();
        // TODO: Make the following work! assertThat(friends).as("friends").containsExactly/*InAnyOrder*/(object, friend); // TODO: Do we really guarantee the order in this case?
        assertThat(friends).as("friends").extracting("key").containsExactly(123l, 124l); // TODO: Remove this line afterwards.
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testGradesProperty() throws DatabaseException, RecoveryException {
        object.grades().add(1, 5);
        object.grades().add(2, 2);
        object.grades().reset();
        final @Nonnull Map<@Nonnull @Valid("key") Integer, @Nonnull @Valid Integer> grades = (Map<@Nonnull @Valid("key") Integer, @Nonnull @Valid Integer>) object.grades().get();
        assertThat(grades).as("grades").hasSize(2).containsKey(1).containsEntry(1, 5).containsEntry(2, 2);
    }
    
}
