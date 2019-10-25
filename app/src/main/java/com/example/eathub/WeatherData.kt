package com.example.eathub

data class WeatherData(
    val currently: Currently,
    val daily: Daily,
    val flags: Flags,
    val hourly: Hourly,
    val latitude: Double,
    val longitude: Double,
    val minutely: Minutely,
    val offset: Double,
    val timezone: String
)

data class Currently(
    val apparentTemperature: Double,
    val cloudCover: Double,
    val dewPoint: Double,
    val humidity: Double,
    val icon: String,
    val nearestStormBearing: Double,
    val nearestStormDistance: Double,
    val ozone: Double,
    val precipIntensity: Double,
    val precipProbability: Double,
    val pressure: Double,
    val summary: String,
    val temperature: Double,
    val time: Double,
    val uvIndex: Double,
    val visibility: Double,
    val windBearing: Double,
    val windGust: Double,
    val windSpeed: Double
)

data class Daily(
    val `data`: List<Data>,
    val icon: String,
    val summary: String
)

data class Data(
    val apparentTemperatureHigh: Double,
    val apparentTemperatureHighTime: Double,
    val apparentTemperatureLow: Double,
    val apparentTemperatureLowTime: Double,
    val apparentTemperatureMax: Double,
    val apparentTemperatureMaxTime: Double,
    val apparentTemperatureMin: Double,
    val apparentTemperatureMinTime: Double,
    val cloudCover: Double,
    val dewPoint: Double,
    val humidity: Double,
    val icon: String,
    val moonPhase: Double,
    val ozone: Double,
    val precipIntensity: Double,
    val precipIntensityMax: Double,
    val precipIntensityMaxTime: Double,
    val precipProbability: Double,
    val precipType: String,
    val pressure: Double,
    val summary: String,
    val sunriseTime: Double,
    val sunsetTime: Double,
    val temperatureHigh: Double,
    val temperatureHighTime: Double,
    val temperatureLow: Double,
    val temperatureLowTime: Double,
    val temperatureMax: Double,
    val temperatureMaxTime: Double,
    val temperatureMin: Double,
    val temperatureMinTime: Double,
    val time: Double,
    val uvIndex: Double,
    val uvIndexTime: Double,
    val visibility: Double,
    val windBearing: Double,
    val windGust: Double,
    val windGustTime: Double,
    val windSpeed: Double
)

data class Flags(
    val nearest_station: Double,
    val sources: List<String>,
    val units: String
)

data class Hourly(
    val `data`: List<DataX>,
    val icon: String,
    val summary: String
)

data class DataX(
    val apparentTemperature: Double,
    val cloudCover: Double,
    val dewPoint: Double,
    val humidity: Double,
    val icon: String,
    val ozone: Double,
    val precipIntensity: Double,
    val precipProbability: Double,
    val pressure: Double,
    val summary: String,
    val temperature: Double,
    val time: Double,
    val uvIndex: Double,
    val visibility: Double,
    val windBearing: Double,
    val windGust: Double,
    val windSpeed: Double
)

data class Minutely(
    val `data`: List<DataXX>,
    val icon: String,
    val summary: String
)

data class DataXX(
    val precipIntensity: Double,
    val precipProbability: Double,
    val time: Double
)