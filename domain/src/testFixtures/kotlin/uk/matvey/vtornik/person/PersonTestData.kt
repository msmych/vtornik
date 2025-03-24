package uk.matvey.vtornik.person

import uk.matvey.slon.random.randomWord
import kotlin.random.Random

fun aPersonTmdbDetails(
    id: Long = Random.nextLong(),
    name: String = randomWord(),
) = Person.Details.Tmdb(
    id = id,
    name = name,
)