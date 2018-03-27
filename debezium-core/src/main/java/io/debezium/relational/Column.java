/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.relational;

import java.sql.Types;

import io.debezium.annotation.Immutable;

/**
 * An immutable definition of a column.
 *
 * @author Randall Hauch
 * @see Table
 */
@Immutable
public interface Column extends Comparable<Column> {

    int UNSET_INT_VALUE = -1;

    /**
     * Obtain an column definition editor that can be used to define a column.
     *
     * @return the editor; never null
     */
    public static ColumnEditor editor() {
        return new ColumnEditorImpl();
    }

    /**
     * Get the name of the column.
     *
     * @return the column name; never null
     */
    String name();

    /**
     * Get the position of the column in the table.
     *
     * @return the 1-based position
     */
    int position();

    /**
     * Get the {@link Types JDBC type} for this column
     *
     * @return the type constant
     */
    int jdbcType();

    /**
     * Get the database native type for this column
     *
     * @return a type constant for the specific database
     */
    int nativeType();

    /**
     * Get the database-specific name of the column's data type.
     *
     * @return the name of the type
     */
    String typeName();

    /**
     * Get the database-specific complete expression defining the column's data type, including dimensions, length, precision,
     * character sets, constraints, etc.
     *
     * @return the complete type expression
     */
    String typeExpression();

    /**
     * Get the database-specific name of the character set used by this column.
     *
     * @return the database-specific character set name, or null if the column's data type doesn't {@link #typeUsesCharset() use
     *         character sets} or no character set is specified
     */
    String charsetName();

    /**
     * Get the maximum length of this column's values. For numeric columns, this represents the precision.
     *
     * @return the length of the column
     */
    int length();

    /**
     * Get the scale of the column.
     *
     * @return the scale, or -1 if the scale does not apply to this type
     */
    int scale();

    /**
     * Determine whether this column is optional.
     *
     * @return {@code true} if it is optional, or {@code false} otherwise
     * @see #isRequired()
     */
    boolean isOptional();

    /**
     * Determine whether this column is required. This is equivalent to calling {@code !isOptional()}.
     *
     * @return {@code true} if it is required (not optional), or {@code false} otherwise
     * @see #isOptional()
     */
    default boolean isRequired() {
        return !isOptional();
    }

    /**
     * Determine whether this column's values are automatically incremented by the database.
     *
     * @return {@code true} if the values are auto-incremented, or {@code false} otherwise
     */
    boolean isAutoIncremented();

    /**
     * Determine whether this column's values are generated by the database.
     *
     * @return {@code true} if the values are generated, or {@code false} otherwise
     */
    boolean isGenerated();

    /**
     * Get the default value of the column
     *
     * @return the default value
     */
    Object defaultValue();

    /**
     * Determine whether this column's default values is null;
     * If the default value in ddl is null, we should set field isDefaultValueNull {@code true};
     *
     * @return {@code true} if the default values is null, or {@code false} otherwise
     */
    boolean isDefaultValueNull();

    default boolean shouldSetDefaultValue() {
        return isDefaultValueNull() || isOptional() || defaultValue() != null;
    }

    @Override
    default int compareTo(Column that) {
        if (this == that) return 0;
        return this.position() - that.position();
    }

    /**
     * Obtain an editor that contains the same information as this column definition.
     *
     * @return the editor; never null
     */
    ColumnEditor edit();

    /**
     * Determine whether this column has a {@link #typeName()} or {@link #jdbcType()} to which a character set applies.
     *
     * @return {@code true} if a character set applies the column's type, or {@code false} otherwise
     */
    default boolean typeUsesCharset() {
        switch (jdbcType()) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.CLOB:
            case Types.NCHAR:
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
            case Types.NCLOB:
            case Types.DATALINK:
            case Types.SQLXML:
                return true;
            default:
                return false;
        }
    }
}
