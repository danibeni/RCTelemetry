package es.danibeni.android.kotlin.rctelemetry.data.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

import es.danibeni.android.kotlin.rctelemetry.data.Race

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
     * Select a race by id.
     *
     * @param raceId the race id.
     * @return the race with raceId.
     */
    @Query("SELECT * FROM Races WHERE raceid = :raceId")
    fun getRaceById(raceId: String): Race

    /**
     * Insert a race in the database. If the race already exists, replace it.
     *
     * @param race the race to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRace(race: Race)

    /**
     * Update a race.
     *
     * @param race race to be updated
     * @return the number of races updated. This should always be 1.
     */
    @Update
    fun updateRace(race: Race): Int

    /**
     * Delete a race by id.
     *
     * @return the number of races deleted. This should always be 1.
     */
    @Query("DELETE FROM Races WHERE raceid = :raceId")
    fun deleteRaceById(raceId: String): Int

    /**
     * Delete all races.
     */
    @Query("DELETE FROM Races")
    fun deleteRaces()
}