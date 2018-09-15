package es.danibeni.android.kotlin.rctelemetry.data.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import es.danibeni.android.kotlin.rctelemetry.data.Lap

import es.danibeni.android.kotlin.rctelemetry.data.Race
import es.danibeni.android.kotlin.rctelemetry.data.RaceAndCircuit

/**
 * Data Access Object for the races table.
 */
@Dao
interface RacesDao {

    /**
     * Select all races from the races table.
     *
     * @return all races.
     */
    @get:Query("SELECT * FROM Races")
    val races: List<Race>

    /**
     * Select all distinct drivers in races tables
     *
     * @return list with distinct drivers
     */
    @get:Query("SELECT DISTINCT driver FROM Races")
    val drivers: List<String>

    /**
     * Select all races from the races table and circuit name.
     *
     * @return all races and circuit name.
     */
    @get:Query("SELECT Races.*, Circuits.name AS circuitName FROM Races, Circuits WHERE Races.circuitId = Circuits.id ORDER BY Races.raceDate DESC")
    val racesAndCircuits: List<RaceAndCircuit>

    /**
     * Select race from the races table and circuit name from race ID.
     *
     * @return race and circuit name.
     */
    @Query("SELECT Races.*, Circuits.name AS circuitName FROM Races, Circuits WHERE Races.id = :raceId and Races.circuitId = Circuits.id")
    fun getRaceAndCircuit(raceId: Long): RaceAndCircuit

    /**
     * Select a race by id.
     *
     * @param raceId the race id.
     * @return the race with raceId.
     */
    @Query("SELECT * FROM Races WHERE id = :raceId")
    fun getRaceById(raceId: Long): Race

    /**
     * Select circuit id based on race id
     *
     * @param raceId the reace id.
     * @return circuit id for that race
     */
    @Query("SELECT circuitId FROM Races WHERE id = :raceId")
    fun getCircuitId(raceId: Long): Long

    /**
     * Insert a race in the database. If the race already exists, replace it.
     *
     * @param race the race to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRace(race: Race) : Long

    /**
     * Update a race.
     *
     * @param race race to be updated
     * @return the number of races updated. This should always be 1.
     */
    @Update
    fun updateRace(race: Race): Int

    /**
     * Update lap list in a race
     *
     *  @param raceId id of the race to update
     *  @param laps lap list to insert into race
     *  @return the number of races updated. This should always be 1.
     */
    @Query("UPDATE Races SET laps = :laps WHERE id = :raceId")
    fun updateLapsInRace(raceId: Long, laps: String): Int

    /**
     * Update a race and circuit.
     *
     * @param raceId Id of race to be updated
     * @param driver name of the race driver
     * @param vehicle custom alias for vehicle
     * @return the number of races updated. This should always be 1.
     */
    @Query("UPDATE Races SET driver = :driver, vehicleAlias = :vehicle WHERE Races.id = :raceId ")
    fun updateRace(raceId: Long, driver: String, vehicle: String): Int

    /**
     * Delete a race by id.
     *
     * @return the number of races deleted. This should always be 1.
     */
    @Query("DELETE FROM Races WHERE id = :raceId")
    fun deleteRaceById(raceId: Long): Int

    /**
     * Delete races associated to a concrete circuit.
     *
     * @return the number of races deleted.
     */
    @Query("DELETE FROM Races WHERE circuitId = :circuitId")
    fun deleteRacesForCircuit(circuitId: Long): Int

    /**
     * Delete all races.
     */
    @Query("DELETE FROM Races")
    fun deleteRaces()
}