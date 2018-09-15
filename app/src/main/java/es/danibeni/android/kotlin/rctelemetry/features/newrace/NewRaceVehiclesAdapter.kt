package es.danibeni.android.kotlin.rctelemetry.features.newrace

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import es.danibeni.android.kotlin.rctelemetry.R
import es.danibeni.android.kotlin.rctelemetry.core.extension.blink
import es.danibeni.android.kotlin.rctelemetry.core.extension.inflate
import kotlinx.android.synthetic.main.newrace_vehicle_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class NewRaceVehiclesAdapter
@Inject constructor(val context: Context) : RecyclerView.Adapter<NewRaceVehiclesAdapter.ViewHolder>() {

    internal var collection: List<NewRaceVehicleView> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    internal var clickListener: (NewRaceVehicleView, Int) -> Unit = { _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.newrace_vehicle_item))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(collection[position])
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            when (holder.itemView.newRaceVehicleStatusTV.text as String) {
                context.getString(R.string.new_race_vehicle_blinking) -> {
                    holder.itemView.newRaceVehicleStatusTV.text = context.getString(R.string.new_race_vehicle_blinking)
                    holder.itemView.newRaceVehicleStatusTV.blink()
                    holder.itemView.newRaceVehicleStatusTV.setTextColor(ContextCompat.getColor(context, R.color.colorTextBlink))
                }
                context.getString(R.string.new_race_vehicle_no_response) -> {
                    holder.itemView.newRaceVehicleStatusTV.text = context.getString(R.string.new_race_vehicle_no_response)
                    holder.itemView.newRaceVehicleStatusTV.clearAnimation()
                    holder.itemView.newRaceVehicleStatusTV.setTextColor(ContextCompat.getColor(context, R.color.colorTextError))
                }
                context.getString(R.string.new_race_vehicle_available) -> {
                    holder.itemView.newRaceVehicleStatusTV.text = context.getString(R.string.new_race_vehicle_available)
                    holder.itemView.newRaceVehicleStatusTV.clearAnimation()
                    holder.itemView.newRaceVehicleStatusTV.setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary))
                }
                context.getString(R.string.new_race_vehicle_selected) -> {
                    holder.itemView.newRaceVehicleStatusTV.text = context.getString(R.string.new_race_vehicle_selected)
                    holder.itemView.newRaceVehicleStatusTV.clearAnimation()
                    holder.itemView.newRaceVehicleStatusTV.setTextColor(ContextCompat.getColor(context, R.color.colorTextSelected))
                }
            }
        }
    }

    override fun getItemCount() = collection.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(vehicleView: NewRaceVehicleView) {
            itemView.newRaceVehicleNameTV.text = vehicleView.name
            itemView.newRaceVehicleIPAddressTV.text = vehicleView.addressId
            itemView.newRaceVehicleStatusTV.text = vehicleView.status
            itemView.setOnClickListener {
                (this@NewRaceVehiclesAdapter.clickListener)(vehicleView, adapterPosition)
            }
        }
    }
}