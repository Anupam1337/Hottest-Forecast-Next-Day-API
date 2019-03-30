# Hottest Forecast Next Day API

## Open Source API used and feature:-

OpenWeather.co.uk
Simple, fast, and utterly convenient APIs for developers
Current weather, historical weather and forecast
Data are available for any geolocation
Data sources are NOAA, Canadian Environment, and raw data from weather stations
Open for integration of other data sources
https://openweathermap.com

## My API functions

getCityCode(String city_name)- checks if entered city exists and returns City Code of city entered by user if exist.
getNextDayDate()- returns next day date.
getMaxTemp(String city_id or city_name):- returns max temperature in given city for whole day.
getMaxTemp(String city_id or city_name, int from_time, to_time):- returns max temperature in given city and time.

**Note:- from_time and to_time are only hours from 0-23.**

## Jar File used

java-json.jar
org.json.jar
json-simple-1.1.jar

Note:- all jar file exist in lib folder.

## External file or DB

citylist.json :- JSON file that contains bulk data from OpenWeather API.
This file gives list of all cities and its related detail.
