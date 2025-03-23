package uk.matvey.slon.sql

import com.github.jasync.sql.db.RowData

fun RowData.getIntOrFail(column: String) = requireNotNull(getInt(column)) {
    "Required $column value not found"
}

fun RowData.getLongOrFail(column: String) = requireNotNull(getLong(column)) {
    "Required $column value not found"
}

fun RowData.getStringOrFail(column: String) = requireNotNull(getString(column)) {
    "Required $column value not found"
}

fun RowData.getDateOrFail(column: String) = requireNotNull(getDate(column)) {
    "Required $column value not found"
}
