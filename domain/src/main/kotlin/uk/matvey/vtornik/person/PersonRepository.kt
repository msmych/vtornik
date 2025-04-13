package uk.matvey.vtornik.person

import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.SuspendingConnection
import kotlinx.serialization.json.Json
import uk.matvey.slon.sql.execute
import uk.matvey.slon.sql.getDateOrFail
import uk.matvey.slon.sql.getLongOrFail
import uk.matvey.slon.sql.getStringOrFail
import uk.matvey.vtornik.movie.Movie
import java.time.ZoneOffset.UTC

class PersonRepository(
    private val db: SuspendingConnection,
) {

    suspend fun findById(id: Long): Person? {
        return db.execute(
            "select * from people where id = ?",
            listOf(id)
        ).rows.singleOrNull()?.let { toPerson(it) }
    }

    suspend fun getById(id: Long): Person {
        return requireNotNull(findById(id)) {
            "Person with id $id was not found"
        }
    }

    suspend fun findAllPeopleByMovieId(
        movieId: Long,
        role: Movie.Role,
    ): List<Person> {
        return db.execute(
            """
            |select p.*
            | from people p
            | join movies_people mp on p.id = mp.person_id
            | where mp.movie_id = ? and mp.role = ?
            |""".trimMargin(),
            listOf(movieId, role.name)
        ).rows.map { toPerson(it) }
    }

    suspend fun findAllPeopleByMoviesIds(
        moviesIds: List<Long>,
        role: Movie.Role,
    ): Map<Long, List<Person>> {
        return db.execute(
            """
            |select mp.movie_id, p.*
            | from people p
            | join movies_people mp on p.id = mp.person_id
            | where mp.movie_id = any(?) and mp.role = ?
            |""".trimMargin(),
            listOf(moviesIds, role.name)
        ).rows.map { it.getLongOrFail("movie_id") to toPerson(it) }
            .groupBy { (k, _) -> k }
            .mapValues { (_, v) -> v.map { it.second } }
    }

    suspend fun ensurePerson(details: Person.Details.Tmdb): Long {
        return findById(details.id)?.id ?: db.execute(
            """
            |insert into people (id, name, details, created_at, updated_at)
            | values (?, ?, ?, now(), now())
            | on conflict do nothing
            | returning id
            |""".trimMargin(),
            listOf(
                details.id,
                details.name,
                Json.encodeToString(Person.Details(tmdb = details)),
            )
        ).rows.single().getLongOrFail("id")
    }

    private fun toPerson(data: RowData): Person {
        return Person(
            id = data.getLongOrFail("id"),
            name = data.getStringOrFail("name"),
            details = Json.decodeFromString(data.getStringOrFail("details")),
            createdAt = data.getDateOrFail("created_at").toInstant(UTC),
            updatedAt = data.getDateOrFail("updated_at").toInstant(UTC),
        )
    }
}