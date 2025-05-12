package uk.matvey.vtornik.movie

import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.SuspendingConnection
import kotlinx.serialization.json.Json
import uk.matvey.slon.sql.execute
import uk.matvey.slon.sql.getDateOrFail
import uk.matvey.slon.sql.getIntOrFail
import uk.matvey.slon.sql.getLongOrFail
import uk.matvey.slon.sql.getStringOrFail
import uk.matvey.vtornik.VtornikSql.MOVIES
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

class MovieRepository(
    private val db: SuspendingConnection,
) {

    suspend fun getById(id: Long): Movie {
        return requireNotNull(findById(id)) {
            "Not found movie by id $id"
        }
    }

    suspend fun findById(id: Long): Movie? {
        return db.execute(
            "select * from $MOVIES where id = ?",
            listOf(id)
        ).rows.singleOrNull()?.let { toMovie(it) }
    }

    suspend fun findAllByIds(ids: List<Long>): List<Movie> {
        return db.execute("select * from $MOVIES where id = any(?)", listOf(ids))
            .rows.map { toMovie(it) }
    }

    suspend fun add(details: Movie.Details.Tmdb): Long {
        return db.execute(
            """
                |insert into $MOVIES (id, title, runtime, year, release_date, details, created_at, updated_at)
                | values (?, ?, ?, ?, ?, ?, now(), now())
                | returning id
                |""".trimMargin(),
            listOf(
                details.id,
                details.title,
                details.runtime,
                null,
                details.releaseDateOrNull(),
                Json.encodeToString(Movie.Details(tmdb = details)),
            )
        ).rows.single().getLongOrFail("id")
    }

    suspend fun update(movie: Movie): Int {
        return db.execute(
            """
            |update $MOVIES
            | set mentions = ?
            | where id = ? and updated_at = ?
            |""".trimMargin(),
            listOf(
                Json.encodeToString(movie.mentions),
                movie.id,
                LocalDateTime.ofInstant(movie.updatedAt, UTC)
            )
        )
            .rows.size
    }

    private fun toMovie(data: RowData): Movie {
        val details = Json.decodeFromString<Movie.Details>(data.getStringOrFail("details"))
        return Movie(
            id = data.getLongOrFail("id"),
            title = data.getStringOrFail("title"),
            runtime = data.getIntOrFail("runtime"),
            year = data.getInt("year"),
            releaseDate = data.getAs<LocalDate>("release_date"),
            details = details,
            mentions = Json.decodeFromString(data.getStringOrFail("mentions")),
            createdAt = data.getDateOrFail("created_at").toInstant(UTC),
            updatedAt = data.getDateOrFail("updated_at").toInstant(UTC),
        )
    }
}
