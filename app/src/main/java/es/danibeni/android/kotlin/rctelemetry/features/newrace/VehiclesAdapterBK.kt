package es.danibeni.android.kotlin.rctelemetry.features.newrace

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import es.danibeni.android.kotlin.rctelemetry.R
import es.danibeni.android.kotlin.rctelemetry.core.extension.inflate
import kotlinx.android.synthetic.main.vehicle_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class VehiclesAdapterBK
@Inject constructor() : RecyclerView.Adapter<VehiclesAdapterBK.ViewHolder>() {

    internal var collection: List<VehicleView> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    internal var clickListener: (VehicleView, Int) -> Unit = { _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.e("VehiclesAdapter", "Creando item view holder")
        return ViewHolder(parent.inflate(R.layout.vehicle_item))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(collection[position], clickListener)
    }

    override fun getItemCount() = collection.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(vehicleView: VehicleView, clickListener: (VehicleView, Int) -> Unit) {
            itemView.vehicleNameTV.text = vehicleView.name
            itemView.vehicleIPAddressTV.text = vehicleView.addressId
            itemView.vehicleStatusTV.text = vehicleView.status
            itemView.setOnClickListener {
                (this@VehiclesAdapterBK.clickListener)(vehicleView, adapterPosition)
            }
        }
    }
}