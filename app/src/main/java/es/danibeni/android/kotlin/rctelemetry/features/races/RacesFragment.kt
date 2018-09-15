package es.danibeni.android.kotlin.rctelemetry.features.races

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import es.danibeni.android.kotlin.rctelemetry.R
import es.danibeni.android.kotlin.rctelemetry.core.exception.Failure
import es.danibeni.android.kotlin.rctelemetry.core.extension.invisible
import es.danibeni.android.kotlin.rctelemetry.core.extension.observe
import es.danibeni.android.kotlin.rctelemetry.core.extension.failure
import es.danibeni.android.kotlin.rctelemetry.core.extension.viewModel
import es.danibeni.android.kotlin.rctelemetry.core.extension.visible
import es.danibeni.android.kotlin.rctelemetry.core.navigation.Navigator
import es.danibeni.android.kotlin.rctelemetry.core.platform.BaseFragment
import es.danibeni.android.kotlin.rctelemetry.data.Lap
import kotlinx.android.synthetic.main.races_fragment.*
import javax.inject.Inject

class RacesFragment : BaseFragment() {
    @Inject
    lateinit var navigator: Navigator
    @Inject
    lateinit var racesAdapter: RacesAdapter

    private lateinit var racesViewModel: RacesViewModel

    override fun layoutId() = R.layout.races_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        racesViewModel = viewModel(viewModelFactory) {
            observe(racesLiveData, ::renderRacesList)
            failure(failure, ::handleFailure)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
        loadRacesList()
    }


    private fun initializeView() {
        setHasOptionsMenu(true)

        raceList.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        raceList.adapter = racesAdapter
        racesAdapter.clickListener = { race ->
            navigator.raceDetails(activity!!, race.id)
        }
        setupFab()
    }

    private fun setupFab() {
        fabAddRace.run {
            setImageResource(R.drawable.ic_add)
            setOnClickListener {
                navigator.newRace(activity!!)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if (inflater != null) {
            inflater.inflate(R.menu.races_nav_menu, menu)
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {

        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadRacesList() {
        noRaces.invisible()
        raceList.visible()
        racesViewModel.loadRaces()
    }

    private fun renderRacesList(races: List<RaceView>?) {
        racesAdapter.collection = races.orEmpty()
    }

    private fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.NetworkConnection -> renderFailure(R.string.failure_network_connection)
            is Failure.ServerError -> renderFailure(R.string.failure_server_error)
            is RaceFailure.ListNotAvailable -> renderFailure(R.string.failure_races_list_unavailable)
        }
    }

    private fun renderFailure(@StringRes message: Int) {
        raceList.invisible()
        noRaces.visible()
        //notify(message)
    }
}