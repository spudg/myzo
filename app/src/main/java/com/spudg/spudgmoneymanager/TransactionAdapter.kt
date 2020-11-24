package com.spudg.spudgmoneymanager

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.transaction_row.view.*
import java.lang.Exception
import java.text.DecimalFormat
import java.text.NumberFormat


class TransactionAdapter(val context: Context, private val items: ArrayList<TransactionModel>) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val transactionItem = view.transaction_row_layout!!
        val categoryView = view.category!!
        val amountView = view.amount!!
        val noteView = view.note!!
        val colourView = view.category_colour!!
        val dateView = view.date_header!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.transaction_row, parent, false)
        )
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val formatter: NumberFormat = DecimalFormat("#,##0.00")

        holder.dateView.visibility = View.GONE

        val transaction = items[position]

        if (context is MainActivity) {
            try {
                if (transaction.day != items[position + 1].day) {
                    holder.dateView.text = "${transaction.day}/${transaction.month}/${transaction.year}"
                    holder.dateView.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                Log.v("Transactions", e.message.toString())
            }
        }

        if (context is MainActivity) {
            holder.categoryView.text = context.getTransactionCategoryTitle(transaction.category)
        }

        holder.amountView.text = formatter.format((transaction.amount).toDouble()).toString()
        holder.noteView.text = transaction.note

        if (context is MainActivity) {
            val colour = context.getTransactionCategoryColour(transaction.category)
            holder.colourView.setBackgroundColor(colour)
        }

        holder.transactionItem.setOnClickListener {
            if (context is MainActivity) {
                context.updateTransaction(transaction)
            }
        }

        holder.transactionItem.setOnLongClickListener {
            if (context is MainActivity) {
                context.deleteTransaction(transaction)
            }
            true
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }
}