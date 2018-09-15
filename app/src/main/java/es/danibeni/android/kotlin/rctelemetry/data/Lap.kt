package es.danibeni.android.kotlin.rctelemetry.data

data class Lap (
    var lapNumber: Int = 0,
    var lapTime: Long = 0L,
    var lapDiff: Long = 0L,
    var lapVelocity: Float = 0F
)

data class LapMeasures ( val measuresNumber: Int) {
    var measures:MutableList<Float> = MutableList(measuresNumber) {index -> 0.0F }
    var count:Int = 0
    var time:Long = 0L
    var done:Boolean = false

    fun shiftMeasures(newMeasure: Float) {
        measures.removeAt(0)
        measures.add(newMeasure)
    }
}

