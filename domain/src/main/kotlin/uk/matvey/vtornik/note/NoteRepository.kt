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

    suspend fun findAllByUser(
        userId: Int,
    ): List<Note> {
        return db.execute(
            "select * from $NOTES where user_id = ?",
            listOf(userId)
        ).rows.map { toNote(it) }
    }

    suspend fun upsert(
        movieId: Long,
        userId: Int,
        note: String,
    ): Note {
        return db.execute(
            """
                |insert into $NOTES (movie_id, user_id, note, created_at, updated_at)
                | values (?, ?, ?, now(), now())
                | on conflict (movie_id, user_id) do update set note = ?, updated_at = now()
                | returning *
                |""".trimMargin(),
            listOf(movieId, userId, note, note)
        ).rows.single().let { toNote(it) }
    }

    private fun toNote(row: RowData): Note {
        return Note(
            movieId = row.getLongOrFail("movie_id"),
            userId = row.getIntOrFail("user_id"),
            note = row.getStringOrFail("note"),
        )
    }
}