package es.danibeni.android.kotlin.rctelemetry.features.newrace

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import es.danibeni.android.kotlin.rctelemetry.R
import es.danibeni.android.kotlin.rctelemetry.core.extension.blink
import es.danibeni.android.kotlin.rctelemetry.core.extension.inflate
import kotlinx.android.synthetic.main.vehicle_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class VehiclesAdapter
@Inject constructor(context: Context) : RecyclerView.Adapter<VehiclesAdapter.ViewHolder>() {
    val context = context

    internal var collection: List<VehicleView> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    internal var clickListener: (VehicleView, Int) -> Unit = { _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.e("VehiclesAdapter", "Creando item view holder")
        return ViewHolder(parent.inflate(R.layout.vehicle_item))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(collection[position], clickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            for (data in  payloads) {
                when (data as String) {
                    context.getString(R.string.new_race_vehicle_blinking) -> {
                        holder.itemView.vehicleStatusTV.text = context.getString(R.string.new_race_vehicle_blinking)
                        holder.itemView.vehicleStatusTV.blink()
                        holder.itemView.vehicleStatusTV.setTextColor(ContextCompat.getColor(context, R.color.colorTextBlink))
                    }
                    context.getString(R.string.new_race_vehicle_no_response) -> {
                        holder.itemView.vehicleStatusTV.text = context.getString(R.string.new_race_vehicle_no_response)
                        holder.itemView.vehicleStatusTV.clearAnimation()
                        holder.itemView.vehicleStatusTV.setTextColor(ContextCompat.getColor(context, R.color.colorTextError))
                    }
                    context.getString(R.string.new_race_vehicle_available) -> {
                        holder.itemView.vehicleStatusTV.text = context.getString(R.string.new_race_vehicle_available)
                        holder.itemView.vehicleStatusTV.clearAnimation()
                        holder.itemView.vehicleStatusTV.setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary))
                    }
                }
            }

        }
    }

    override fun getItemCount() = collection.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(vehicleView: VehicleView, clickListener: (VehicleView, Int) -> Unit) {
            itemView.vehicleNameTV.text = vehicleView.name
            itemView.vehicleIPAddressTV.text = vehicleView.addressId
            itemView.vehicleStatusTV.text = vehicleView.status
            itemView.setOnClickListener {
                (this@VehiclesAdapter.clickListener)(vehicleView, adapterPosition)
            }
        }
    }
}