package es.danibeni.android.kotlin.rctelemetry.features.newrace

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.jjoe64.graphview.series.DataPoint
import es.danibeni.android.kotlin.rctelemetry.R
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseFragment
import kotlinx.android.synthetic.main.reconnaissancelap_fragment.*
import com.jjoe64.graphview.series.LineGraphSeries



class ReconnaissanceLapFragment : BaseFragment() {
    private val TAG: String = ReconnaissanceLapFragment::class.java.simpleName

    companion object {
        private const val PARAM_VEHICLE = "param_vehicle"

        fun forVehicle(vehicle: VehicleView): ReconnaissanceLapFragment {
            val reconnaissanceLapFragment = ReconnaissanceLapFragment()
            val arguments = Bundle()
            arguments.putParcelable(PARAM_VEHICLE, vehicle)
            reconnaissanceLapFragment.arguments = arguments

            return reconnaissanceLapFragment
        }
    }

    override fun layoutId(): Int = R.layout.reconnaissancelap_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
    }

    private fun initializeView() {
        Log.i(TAG, "Vehicle name: " + (arguments?.get(PARAM_VEHICLE) as VehicleView).name)
        val series = LineGraphSeries<DataPoint>(
                arrayOf<DataPoint>(
                        DataPoint(0.0, 1.0),
                        DataPoint(1.0, 5.0),
                        DataPoint(2.0, 3.0),
                        DataPoint(3.0, 2.0),
                        DataPoint(4.0, 6.0)))
        series.color = ContextCompat.getColor(context!!, R.color.colorGraphOne)
        vehicleSignalGV.addSeries(series)
        vehicleSignalGV.viewport.isXAxisBoundsManual = true
        vehicleSignalGV.viewport.setMinX(0.0)
        vehicleSignalGV.viewport.setMaxX(40.0)
        var graph2LastXValue = 5.0
        recLapStartButton.setOnClickListener {
            graph2LastXValue += 1.0
            series.appendData(
                    DataPoint(graph2LastXValue, 6.0),
                    true,
                    40)
        }
    }
}