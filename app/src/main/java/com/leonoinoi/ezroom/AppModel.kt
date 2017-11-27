package com.leonoinoi.ezroom

import java.io.Serializable

data class Building(
        var name: String = "",
        var phone: String = "",
        var email: String = "",
        var address: String = "",
        var gps: String = "",
        var comment: String = "",
        var power_rate: Int = 0,
        var power_bill_min: Int = 0,
        var water_rate: Int = 0,
        var water_bill_min: Int = 0
) : Serializable

data class RoomType(
        var type: String = "",
        var name_building: String = "",
        var rental_month: Int = 0,
        var deposit: Int = 0,
        var deposit_period_month: Int = 0
) : Serializable

data class Room(
        var id: String = "",
        var name_building: String = "",
        var type: String = "",
        var comment: String = "",
        var name_renter: String = "" //book,renting,empty,maintenance
) : Serializable

data class Option(
        var name: String = "",
        var type: String = "", //month,deposit,onetime
        var price: Int = 0
) : Serializable

data class Renter(
        var name: String = "",
        var id: String = "",
        var phone: String = "",
        var email: String = "",
        var id_room: String = "",
        var status: String = "", //Renting,Booking,Old Renter
        var date_create: String = "",
        var date_check_out: String = "",
        var date_change: String = "",
        var option: List<Option> = ArrayList(),
        var comment: String = ""
) : Serializable

data class Electricity(
        var yyyyMM: String = "",
        var id_room: String = "",
        var meter: Int = 0
) : Serializable

data class Water(
        var yyyyMM: String = "",
        var id_room: String = "",
        var meter: Int = 0
) : Serializable

data class Contract(
        var id: String = "",
        var name_renter: String = "",
        var id_room: String = "",
        var name_building: String = "",
        var date_create: String = "",
        var date_check_out: String = "",
        var date_change: String = "",
        var status: String = "",
        var comment: String = ""
) : Serializable

data class Invoice(
        var id: String = "",
        var date: String = "",
        var name_renter: String = "",
        var id_room: String = "",
        var name_building: String = "",
        var price_room: Int = 0,
        var price_electricity: Int = 0,
        var price_water: Int = 0,
        var price_others: Int = 0,
        var comment: String = ""
) : Serializable