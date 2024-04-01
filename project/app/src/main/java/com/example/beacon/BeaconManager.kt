package com.example.beacon

object BeaconManager {
    private lateinit var beacon: BeaconInfo
    fun setBeacon(beacon: BeaconInfo) {
        this.beacon = beacon
    }

    fun getBeacon(): BeaconInfo {
        return this.beacon
    }
}