package es.danibeni.android.kotlin.rctelemetry.data.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import es.danibeni.android.kotlin.rctelemetry.data.Circuit

/**
 * Data Access Object for the circuits table.
 */
@Dao
interface CircuitsDao {

    /**
     * Select all circuits from the circuits table.
     *
     * @return all circuits.
     */
    @get:Query("SELECT * FROM Circuits")
    val circuits: List<Circuit>

    /**
     * Select a circuit by id.
     *
     * @param circuitId the circuit id.
     * @return the circuit with circuitId.
     */
    @Query("SELECT * FROM Circuits WHERE id = :circuitId")
    fun getCircuitById(circuitId: Long): Circuit

    /**
     * Insert a circuit in the database. If the circuit already exists, replace it.
     *
     * @param circuit the circuit to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCircuit(circuit: Circuit): Long

    /**
     * Update a circuit.
     *
     * @param circuit circuit to be updated
     * @return the number of circuits updated. This should always be 1.
     */
    @Update
    fun updateCircuit(circuit: Circuit): Int

    /**
     * Update a circuit name.
     *
     * @param circuitId ID of the circuit to be updated
     * @param circuitName Name of the circuit to be updated
     * @return the number of circuits updated. This should always be 1.
     */
    @Query("UPDATE Circuits SET name= :circuitName WHERE id = :circuitId")
    fun updateCircuitName(circuitId: Long, circuitName: String): Int

    /**
     * Delete a circuit by id.
     *
     * @return the number of circuits deleted. This should always be 1.
     */
    @Query("DELETE FROM Circuits WHERE id = :circuitId")
    fun deleteCircuitById(circuitId: Long): Int

    /**
     * Delete all circuits.
     */
    @Query("DELETE FROM Circuits")
    fun deleteCircuits()

}