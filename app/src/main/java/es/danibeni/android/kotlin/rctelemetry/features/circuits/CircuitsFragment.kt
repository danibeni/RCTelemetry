package es.danibeni.android.kotlin.rctelemetry.features.circuits

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import es.danibeni.android.kotlin.rctelemetry.R
import es.danibeni.android.kotlin.rctelemetry.R.id.circuitList
import es.danibeni.android.kotlin.rctelemetry.core.exception.Failure
import es.danibeni.android.kotlin.rctelemetry.core.extension.*
import es.danibeni.android.kotlin.rctelemetry.core.navigation.Navigator
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseFragment
import kotlinx.android.synthetic.main.circuits_fragment.*
import javax.inject.Inject

class CircuitsFragment : BaseFragment() {
    @Inject
    lateinit var navigator: Navigator
    @Inject
    lateinit var circuitsAdapter: CircuitsAdapter

    private lateinit var circuitsViewModel: CircuitsViewModel

    override fun layoutId() = R.layout.circuits_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        circuitsViewModel = viewModel(viewModelFactory) {
            observe(circuitsLiveData, ::renderCircuitsList)
            failure(failure, ::handleFailure)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
        loadCircuitsList()
    }


    private fun initializeView() {
        setHasOptionsMenu(true)

        circuitList.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        circuitList.adapter = circuitsAdapter
        circuitsAdapter.clickListener = { circuit ->
            navigator.circuitDetails(activity!!, circuit.id)
        }
        setupFab()
    }

    private fun setupFab() {
        fabAddCircuit.run {
            setImageResource(R.drawable.ic_add)
            setOnClickListener {
                navigator.circuitDetails(activity!!, 0)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if (inflater != null) {
            inflater.inflate(R.menu.circuits_nav_menu, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadCircuitsList() {
        noCircuits.invisible()
        circuitList.visible()
        circuitsViewModel.loadCircuits()
    }

    private fun renderCircuitsList(circuits: List<CircuitView>?) {
        circuitsAdapter.collection = circuits.orEmpty()
    }

    private fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.NetworkConnection -> renderFailure(R.string.failure_network_connection)
            is Failure.ServerError -> renderFailure(R.string.failure_server_error)
            is CircuitFailure.ListNotAvailable -> renderFailure(R.string.failure_circuits_list_unavailable)
        }
    }

    private fun renderFailure(@StringRes message: Int) {
        circuitList.invisible()
        noCircuits.visible()
        //notify(message)
    }
}