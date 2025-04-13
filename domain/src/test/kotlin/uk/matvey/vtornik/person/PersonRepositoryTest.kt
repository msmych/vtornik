package uk.matvey.vtornik.person

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.matvey.vtornik.DomainTestSetup

class PersonRepositoryTest : DomainTestSetup() {

    @Test
    fun `should add person if not exists`() = runTest {
        // given
        val repository = PersonRepository(db)
        val tmdbDetails = aPersonTmdbDetails()

        // when
        val id = repository.ensurePerson(tmdbDetails)

        // then
        val person = repository.getById(id)
        assertThat(person.name).isEqualTo(tmdbDetails.name)
        assertThat(person.details.tmdb).isEqualTo(tmdbDetails)
    }

    @Test
    fun `should return existing id if already exists`() = runTest {
        // given
        val repository = PersonRepository(db)
        val tmdbDetails = aPersonTmdbDetails()
        val existingId = repository.ensurePerson(tmdbDetails)

        // when
        val id = repository.ensurePerson(tmdbDetails)

        // then
        assertThat(id).isEqualTo(existingId)
    }
}