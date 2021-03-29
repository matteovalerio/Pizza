package com.example.pizza.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

private const val PRICE_MARGHERITA = 4.00
private const val PRICE_PROSCIUTTO_FUNGHI = 6.00
private const val PRICE_DIAVOLA = 5.50
private const val PRICE_PATATOSA = 5.50
private const val PRICE_SPECK_BRIE = 7.00
private const val PRICE_QUATTRO_FORMAGGI = 8.00
private const val PRICE_CAPRICCIOSA = 7.00
private const val PRICE_MARIO = 10.00

private const val MARGHERITA = "Margherita"
private const val PROSCIUTTO_FUNGHI = "Prosciutto e funghi"
private const val DIAVOLA = "Diavola"
private const val PATATOSA = "Patatosa"
private const val SPECK_BRIE = "Speck e brie"
private const val QUATTRO_FORMAGGI = "Quattro formaggi"
private const val CAPRICCIOSA = "Capricciosa"
private const val PIZZA_MARIO = "Pizza Mario"

class OrderViewModel : ViewModel() {

    private val _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> = _quantity

    private val _flavor = MutableLiveData<MutableList<String>>()
    val flavor: MutableLiveData<MutableList<String>> = _flavor

    private val _price = MutableLiveData<Double>()
    val price: LiveData<String> = Transformations.map(_price) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> = _date

    private val _flavorQuantity: HashMap<String, Int> = setMenu()
    val flavorQuantity: Map<String, Int> = _flavorQuantity

    private val _priceTags: HashMap<String, Double> = setPrices()
    val priceTags = _priceTags

    val dateOptions = getPickupOptions()

    init {
        resetOrder()
    }

    fun setQuantity(flavor: String, numberPizzas: Int) {
        _flavorQuantity[flavor] = numberPizzas
        Log.d("FLAVOR FRAGMENT", "flavor + $flavor, number of pizzas $numberPizzas")
        updatePrice()
    }

    fun setDate(desiredDate: String) {
        _date.value = desiredDate
        updatePrice()
    }


    /**
     * Setup the HashMap
     * Starting quantity for each taste = 0
     */
    private fun setMenu(): HashMap<String, Int> {
        val menu = HashMap<String, Int>()
        val menuItem = listOf(MARGHERITA, PATATOSA, PROSCIUTTO_FUNGHI, DIAVOLA, SPECK_BRIE, QUATTRO_FORMAGGI, CAPRICCIOSA, PIZZA_MARIO)
        for (item in menuItem) {
            menu[item] = 0
        }
        return menu
    }

    private fun setPrices(): HashMap<String, Double> {
        val menuPrice = HashMap<String, Double>()
        menuPrice[MARGHERITA] = PRICE_MARGHERITA
        menuPrice[PROSCIUTTO_FUNGHI] = PRICE_PROSCIUTTO_FUNGHI
        menuPrice[DIAVOLA] = PRICE_DIAVOLA
        menuPrice[PATATOSA] = PRICE_PATATOSA
        menuPrice[SPECK_BRIE] = PRICE_SPECK_BRIE
        menuPrice[QUATTRO_FORMAGGI] = PRICE_QUATTRO_FORMAGGI
        menuPrice[CAPRICCIOSA] = PRICE_CAPRICCIOSA
        menuPrice[PIZZA_MARIO] = PRICE_MARIO
        return menuPrice
    }

    private fun getPickupOptions(): List<String> {
        val options = mutableListOf<String>()
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())//SimpleDateFormat("EEE d MMM", Locale.getDefault())
        val calendar = Calendar.getInstance()
        repeat(4) {
            options.add(formatter.format(calendar.time))
            calendar.add(Calendar.MINUTE, 15)
        }
        return options
    }

    fun resetOrder() {
        _date.value = dateOptions[0]
        _quantity.value = 0
        _flavor.value = mutableListOf<String>()
        _price.value = 0.0
        _flavorQuantity.forEach { pizza ->
            _flavorQuantity[pizza.key] = 0
        }
    }

    private fun updatePrice() {
        var calculatedPrice = 0.0

        flavorQuantity.forEach { pizza ->
            calculatedPrice += priceTags[pizza.key]?.let { flavorQuantity[pizza.key]?.times(it) }!!
        }
        _price.value = calculatedPrice
    }

    fun orderReview(): String {
        var ret = ""
        flavorQuantity.forEach { pizza ->
            if (flavorQuantity[pizza.key]!! > 0) {
                ret += flavorQuantity[pizza.key].toString() + "x" + pizza.key + "\n"
            }
        }
        return ret
    }

    fun getQuantity(): Int {
        var ret = 0
        flavorQuantity.forEach { pizza ->
            ret += flavorQuantity[pizza.key]!!
        }
        return ret
    }

}