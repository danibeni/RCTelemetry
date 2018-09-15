package es.danibeni.android.kotlin.rctelemetry.features.races

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import es.danibeni.android.kotlin.rctelemetry.R
import es.danibeni.android.kotlin.rctelemetry.core.extension.inflate
import es.danibeni.android.kotlin.rctelemetry.core.navigation.Navigator
import kotlinx.android.synthetic.main.race_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class RacesAdapter @Inject constructor() : RecyclerView.Adapter<RacesAdapter.ViewHolder>() {

    internal var collection: List<RaceView> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    internal var clickListener: (RaceView) -> Unit = { }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(parent.inflate(R.layout.race_item))

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) =
            viewHolder.bind(collection[position], clickListener)

    override fun getItemCount() = collection.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(raceView: RaceView, clickListener: (RaceView) -> Unit) {
            itemView.circuitNameTV.text = raceView.circuit
            itemView.driverNameTV.text = raceView.driver
            itemView.raceTimeTV.text = raceView.raceTime
            itemView.raceDateTV.text = raceView.raceDate
            itemView.setOnClickListener { clickListener(raceView) }
        }
    }
}