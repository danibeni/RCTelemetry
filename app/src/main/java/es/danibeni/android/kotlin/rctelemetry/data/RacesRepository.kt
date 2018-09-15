package es.danibeni.android.kotlin.rctelemetry.data

import es.danibeni.android.kotlin.rctelemetry.core.exception.Failure
import es.danibeni.android.kotlin.rctelemetry.core.functional.Either
import es.danibeni.android.kotlin.rctelemetry.core.interactor.UseCase
import es.danibeni.android.kotlin.rctelemetry.core.platform.Converters
import es.danibeni.android.kotlin.rctelemetry.features.races.RaceFailure
import es.danibeni.android.kotlin.rctelemetry.data.local.RacesDao
import es.danibeni.android.kotlin.rctelemetry.features.newrace.NewRaceFailure


interface RacesRepository {
    fun races(): Either<Failure, List<Race>>
    fun drivers(): Either<Failure, List<String>>
    fun racesAndCircuits(): Either<Failure, List<RaceAndCircuit>>
    fun addRace(race: Race): Either<Failure, Race>
    fun addRace(race: Race, laps: List<Lap>): Either<Failure, Race>
    fun addLapsToRace(raceid: Long, laps: List<Lap>): Either<Failure, Int>
    fun raceAndCircuit(raceId: Long): Either<Failure, RaceAndCircuit>
    fun updateRace(raceId: Long, driver: String, vehicle: String, circuitName: String): Either<Failure, Circuit>
    fun deleteRace(raceId: Long): Either<Failure, Int>
    fun deleteRacesForCircuit(circuitId: Long): Either<Failure, Int>


    class LocalDataSource
    constructor(private val racesDao: RacesDao) : RacesRepository {
        override fun races(): Either<Failure, List<Race>> {
            val races = racesDao.races
            return when (races.isEmpty()) {
                true -> Either.Left(RaceFailure.ListNotAvailable())
                false -> Either.Right(races)
            }
        }

        override fun drivers(): Either<Failure, List<String>> {
            val drivers = racesDao.drivers
            return when (drivers.isEmpty()) {
                true -> Either.Left(RaceFailure.ListNotAvailable())
                false -> Either.Right(drivers)
            }
        }

        override fun racesAndCircuits(): Either<Failure, List<RaceAndCircuit>> {
            val races = racesDao.racesAndCircuits
            return when (races.isEmpty()) {
                true -> Either.Left(RaceFailure.ListNotAvailable())
                false -> {
                    races.map { it.lapList = Converters.fromStringToLaps(it.laps) }
                    Either.Right(races)
                }
            }
        }

        override fun raceAndCircuit(raceId: Long): Either<Failure, RaceAndCircuit> {
            val race = racesDao.getRaceAndCircuit(raceId)
            race.lapList = Converters.fromStringToLaps(race.laps)
            return Either.Right(race)
        }

        override fun addRace(race: Race): Either<Failure, Race> {
            race.id = racesDao.insertRace(race)
            return Either.Right(race)
        }

        override fun addRace(race: Race, laps: List<Lap>): Either<Failure, Race> {
            if (laps.isEmpty()) {
                return Either.Left(NewRaceFailure.NoLapDetected())
            } else {
                race.laps = Converters.fromLapsToString(laps)
                race.id = racesDao.insertRace(race)
                return Either.Right(race)
            }
        }

        override fun addLapsToRace(raceid: Long, laps: List<Lap>): Either<Failure, Int> {
            if (laps.isEmpty()) {
                return Either.Left(NewRaceFailure.NoLapDetected())
            } else {
                val lapsToJSON = Converters.fromLapsToString(laps)
                return Either.Right(racesDao.updateLapsInRace(raceid, lapsToJSON))
            }
        }

        override fun updateRace(raceId: Long, driver: String, vehicle: String, circuitName: String): Either<Failure, Circuit> {
            val racesUpdated = racesDao.updateRace(raceId, driver, vehicle)
            when (racesUpdated == 1) {
                true -> {
                    val circuitId = racesDao.getCircuitId(raceId)
                    when (circuitId == null) {
                        true -> return Either.Left(RaceFailure.CircuitIDNotFound())
                        false -> return Either.Right(Circuit(id = circuitId, name = circuitName))
                    }
                }
                false -> return Either.Left(RaceFailure.RaceUpdateFailed())
            }
        }

        override fun deleteRace(raceId: Long): Either<Failure, Int> {
            val racesDeleted = racesDao.deleteRaceById(raceId)
            return when (racesDeleted == 1) {
                true -> Either.Right(racesDeleted)
                false -> Either.Left(RaceFailure.RaceDeleteFailed())
            }
        }

        override fun deleteRacesForCircuit(circuitId: Long): Either<Failure, Int> {
            val racesDeleted = racesDao.deleteRacesForCircuit(circuitId)
            return when (racesDeleted > -1) {
                true -> Either.Right(racesDeleted)
                false -> Either.Left(RaceFailure.RaceDeleteFailed())
            }
        }

    }

}