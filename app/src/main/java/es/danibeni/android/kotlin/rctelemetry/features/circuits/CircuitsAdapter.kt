package es.danibeni.android.kotlin.rctelemetry.features.circuits

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import es.danibeni.android.kotlin.rctelemetry.R
import es.danibeni.android.kotlin.rctelemetry.core.extension.inflate
import kotlinx.android.synthetic.main.circuit_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class CircuitsAdapter @Inject constructor() : RecyclerView.Adapter<CircuitsAdapter.ViewHolder>() {

    internal var collection: List<CircuitView> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    internal var clickListener: (CircuitView) -> Unit = { }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(parent.inflate(R.layout.circuit_item))

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) =
            viewHolder.bind(collection[position], clickListener)

    override fun getItemCount() = collection.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(circuitView: CircuitView, clickListener: (CircuitView) -> Unit) {
            itemView.circuitNameTV.text = circuitView.name
            itemView.circuitCityTV.text = circuitView.city
            itemView.circuitLenghtTV.text = circuitView.lenght.toString()
            itemView.setOnClickListener { clickListener(circuitView) }
        }
    }
}