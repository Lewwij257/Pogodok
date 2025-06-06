package com.locaspes.pogodok

sealed class Screen(val route: String){
    data object TodayWeatherScreen: Screen("TodayWeatherScreen")
    data object WeekWeatherScreen: Screen("WeekWeatherScreen")
}