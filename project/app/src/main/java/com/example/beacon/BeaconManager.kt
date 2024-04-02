package com.example.beacon

object BeaconManager {
    private lateinit var beacon: MyBeaconsInfo
    fun setBeacon(beacon: MyBeaconsInfo) {
        this.beacon = beacon
    }

    fun getBeacon(): MyBeaconsInfo {
        return this.beacon
    }
}