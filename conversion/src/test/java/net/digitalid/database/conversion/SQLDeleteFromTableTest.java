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
package net.digitalid.database.conversion;

import javax.annotation.Nonnull;

import net.digitalid.utility.storage.interfaces.Unit;

import net.digitalid.database.conversion.testenvironment.embedded.Convertible1;
import net.digitalid.database.conversion.testenvironment.embedded.Convertible1Builder;
import net.digitalid.database.conversion.testenvironment.embedded.Convertible1Converter;
import net.digitalid.database.conversion.testenvironment.embedded.Convertible2;
import net.digitalid.database.conversion.testenvironment.embedded.Convertible2Builder;
import net.digitalid.database.conversion.testenvironment.embedded.EmbeddedConvertibles;
import net.digitalid.database.conversion.testenvironment.embedded.EmbeddedConvertiblesBuilder;
import net.digitalid.database.conversion.testenvironment.embedded.EmbeddedConvertiblesConverter;
import net.digitalid.database.conversion.testenvironment.simple.SingleBooleanColumnTable;
import net.digitalid.database.conversion.testenvironment.simple.SingleBooleanColumnTableConverter;
import net.digitalid.database.testing.DatabaseTest;

import org.junit.Test;

/**
 * Tests whether SQL delete statement works.
 */
public class SQLDeleteFromTableTest extends DatabaseTest {

    private static final @Nonnull Unit unit = Unit.DEFAULT;
    
    /**
     * Tests whether delete works on rows that match the where-object completely.
     */
    @Test
    public void shouldDeleteFromSimpleBooleanTable() throws Exception {
        SQL.createTable(SingleBooleanColumnTableConverter.INSTANCE, unit);
        try {
            final @Nonnull SingleBooleanColumnTable convertibleObject = SingleBooleanColumnTable.get(true);
            SQL.insertOrAbort(SingleBooleanColumnTableConverter.INSTANCE, convertibleObject, unit);
    
            assertRowCount(SingleBooleanColumnTableConverter.INSTANCE.getTypeName(), unit.getName(), 1);
            assertTableContains(SingleBooleanColumnTableConverter.INSTANCE.getTypeName(), unit.getName(), Expected.column("value").value("TRUE"));
    
            SQL.delete(SingleBooleanColumnTableConverter.INSTANCE, unit, WhereConditionBuilder.withConverter(SingleBooleanColumnTableConverter.INSTANCE).withObject(convertibleObject).build());
    
            assertRowCount(SingleBooleanColumnTableConverter.INSTANCE.getTypeName(), unit.getName(), 0);
        } finally {
            SQL.dropTable(SingleBooleanColumnTableConverter.INSTANCE, unit);
        }
    }
    
    /**
     * Tests whether delete works on rows with cells that match the where-object.
     */
    @Test
    public void shouldDeleteTableWithEmbeddedConvertibles() throws Exception {
        SQL.createTable(EmbeddedConvertiblesConverter.INSTANCE, unit);
        try {
            final @Nonnull Convertible1 convertible1 = Convertible1Builder.withValue(2).build();
            final @Nonnull Convertible2 convertible2 = Convertible2Builder.withValue(3).build();
            final @Nonnull EmbeddedConvertibles embeddedConvertibles = EmbeddedConvertiblesBuilder.withConvertible1(convertible1).withConvertible2(convertible2).build();
            SQL.insertOrAbort(EmbeddedConvertiblesConverter.INSTANCE, embeddedConvertibles, unit);
    
            assertRowCount(EmbeddedConvertiblesConverter.INSTANCE.getTypeName(), unit.getName(), 1);
            assertTableContains(EmbeddedConvertiblesConverter.INSTANCE.getTypeName(), unit.getName(), Expected.column("convertible1_value").value("2"), Expected.column("convertible2_value").value("3"));
    
            SQL.delete(EmbeddedConvertiblesConverter.INSTANCE, unit, WhereConditionBuilder.withConverter(Convertible1Converter.INSTANCE).withObject(convertible1).withPrefix("convertible1").build());
    
            assertRowCount(EmbeddedConvertiblesConverter.INSTANCE.getTypeName(), unit.getName(), 0);
        } finally {
            SQL.dropTable(EmbeddedConvertiblesConverter.INSTANCE, unit);
        }
    }
    
}
