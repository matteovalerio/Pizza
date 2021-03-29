package com.example.pizza

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pizza.databinding.FragmentFlavorBinding
import com.example.pizza.model.OrderViewModel

/**
 * [FlavorFragment] allows a user to choose a pizza flavor for the order.
 */
class FlavorFragment : Fragment() {

    // Binding object instance corresponding to the fragment_flavor.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var binding: FragmentFlavorBinding? = null
    private val sharedViewModel: OrderViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentFlavorBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            viewModel = sharedViewModel
            flavorFragment = this@FlavorFragment
            lifecycleOwner = viewLifecycleOwner
        }
    }


    /**
     * Navigate to the next screen to choose pickup date.
     */
    fun goToNextScreen() {
        findNavController().navigate(R.id.action_flavorFragment_to_pickupFragment)
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
        findNavController().navigate(R.id.action_flavorFragment_to_startFragment)
    }


    fun quantityOption(chosenOption: String, checkBox: CheckBox) {

        val checked: Boolean = checkBox.isChecked
        if (checked) {
            val builder = AlertDialog.Builder(activity)
            val inflater = layoutInflater

            builder.setTitle(R.string.quantity)
            val dialogLayout = inflater.inflate(R.layout.quantity_dialog, null)
            val editText = dialogLayout.findViewById<EditText>(R.id.editText)
            builder.setView(dialogLayout)
            builder.setPositiveButton(R.string.accept,
                    DialogInterface.OnClickListener { _, _ ->
                        var text = editText?.text.toString()
                        if (text == "" || text == "0") {
                            text = "0"
                            checkBox.isChecked = !checkBox.isChecked
                        }
                        sharedViewModel.setQuantity(chosenOption, text.toInt())
                    })
            builder.show()
        } else {
            sharedViewModel.setQuantity(chosenOption, 0)
        }
    }
}
