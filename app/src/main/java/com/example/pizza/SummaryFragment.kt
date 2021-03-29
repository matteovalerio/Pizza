package com.example.pizza

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pizza.databinding.FragmentSummaryBinding
import com.example.pizza.model.OrderViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * [SummaryFragment] contains a summary of the order details with a button to share the order
 * via another app.
 */
class SummaryFragment : Fragment() {

    // Binding object instance corresponding to the fragment_summary.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var binding: FragmentSummaryBinding? = null
    private val sharedViewModel: OrderViewModel by activityViewModels()
    private var name: String = ""

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentSummaryBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            viewModel = sharedViewModel
            summaryFragment = this@SummaryFragment
            lifecycleOwner = viewLifecycleOwner
        }
    }

    /**
     * Submit the order by sharing out the order details to another app via an implicit intent.
     */
    fun sendOrder() {

        val numberOfPizza = sharedViewModel.getQuantity()
        if (numberOfPizza != 0) {

            val orderSummary = getString(
                    R.string.order_details,
                    sharedViewModel.orderReview(),
                    sharedViewModel.date.value.toString(),
                    sharedViewModel.price.value.toString(),
                    name
            )
            val intent = Intent(Intent.ACTION_SEND)
                    .setType("text/plain")
                    .putExtra(Intent.EXTRA_SUBJECT, R.string.new_pizza_order)
                    .putExtra(Intent.EXTRA_TEXT, orderSummary)
            if (activity?.packageManager?.resolveActivity(intent, 0) != null)
                startActivity(intent)
        } else {
            Toast.makeText(activity, "Ordine non valido!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * This fragment lifecycle method is called when the view hierarchy associated with the fragment
     * is being removed. As a result, clear out the binding object.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun cancelOrder() {
        sharedViewModel.resetOrder()
        findNavController().navigate(R.id.action_summaryFragment_to_startFragment)
    }

    fun getName(): String {
        val builder = AlertDialog.Builder(activity)
        val inflater = layoutInflater
        builder.setTitle(R.string.name)
        val dialogLayout = inflater.inflate(R.layout.name_dialog, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton(R.string.accept,
                DialogInterface.OnClickListener { _, _ ->
                    name = editText?.text.toString()
                    if (name == "") {
                        val formatter = SimpleDateFormat("DDDHHMMSS", Locale.getDefault())
                        name = formatter.format(Date())
                    }
                    sendOrder()
                })
        builder.show()

        return name
    }
}