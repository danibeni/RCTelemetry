package es.danibeni.android.kotlin.rctelemetry.data

import es.danibeni.android.kotlin.rctelemetry.core.exception.Failure
import es.danibeni.android.kotlin.rctelemetry.core.functional.Either
import es.danibeni.android.kotlin.rctelemetry.features.races.RaceFailure
import es.danibeni.android.kotlin.rctelemetry.data.local.RacesDao


interface RacesRepository {
    fun races(): Either<Failure, List<Race>>

    class LocalDataSource
    constructor(private val racesDao: RacesDao): RacesRepository {
        override fun races(): Either<Failure, List<Race>> {
            val races = racesDao.races
            return when (races.isEmpty()) {
                true -> Either.Left(RaceFailure.ListNotAvailable())
                false -> Either.Right(races)
            }
        }

    }

}