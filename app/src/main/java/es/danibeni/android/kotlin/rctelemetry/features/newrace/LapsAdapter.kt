package es.danibeni.android.kotlin.rctelemetry.features.newrace

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import es.danibeni.android.kotlin.rctelemetry.R
import es.danibeni.android.kotlin.rctelemetry.core.extension.empty
import es.danibeni.android.kotlin.rctelemetry.core.extension.inflate
import es.danibeni.android.kotlin.rctelemetry.core.extension.minutesFormat
import kotlinx.android.synthetic.main.lap_item.view.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.properties.Delegates

class LapsAdapter @Inject constructor(val context: Context) : RecyclerView.Adapter<LapsAdapter.ViewHolder>() {

    internal var collection: List<LapView> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(parent.inflate(R.layout.lap_item))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(collection[position])
        val lapDiff = holder.itemView.lapDifferenceTV.text.toString().toFloat()
        when {
            lapDiff < 0 ->  holder.itemView.lapDifferenceTV.setTextColor(ContextCompat.getColor(context, R.color.colorOK))
            lapDiff == 0.0F -> holder.itemView.lapDifferenceTV.setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary))
            lapDiff > 0 ->  {
                holder.itemView.lapDifferenceTV.text = "+" + holder.itemView.lapDifferenceTV.text
                holder.itemView.lapDifferenceTV.setTextColor(ContextCompat.getColor(context, R.color.colorError))
            }
        }
    }

    override fun getItemCount() = collection.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(lapView: LapView) {
            itemView.lapNumberTV.text = lapView.lapNumber.toString()
            itemView.lapTimeTV.text = String.minutesFormat(lapView.lapTime)
            val lapViewDiff = String.format(Locale.US, "%.1f", (lapView.lapDiff.toFloat() / 1000))
            itemView.lapDifferenceTV.text = lapViewDiff
        }
    }
}