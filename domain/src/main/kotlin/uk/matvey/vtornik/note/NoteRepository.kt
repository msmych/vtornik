package uk.matvey.vtornik.note

import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.SuspendingConnection
import uk.matvey.slon.sql.execute
import uk.matvey.slon.sql.getIntOrFail
import uk.matvey.slon.sql.getLongOrFail
import uk.matvey.slon.sql.getStringOrFail
import uk.matvey.vtornik.VtornikSql.NOTES

class NoteRepository(
    private val db: SuspendingConnection,
) {

    suspend fun findByMovieAndUser(
        movieId: Long,
        userId: Int,
    ): Note? {
        return db.execute(
            "select * from $NOTES where movie_id = ? and user_id = ?",
            listOf(movieId, userId)
        ).rows.singleOrNull()?.let { toNote(it) }
    }

    private fun toNote(row: RowData): Note {
        return Note(
            movieId = row.getLongOrFail("movie_id"),
            userId = row.getIntOrFail("user_id"),
            note = row.getStringOrFail("note"),
        )
    }
}