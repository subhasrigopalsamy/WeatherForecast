# WeatherForecast
Features:
The App shows recent weather forecast for Cambridge along with wind speed, rain , humidity and pressure.A simple description of the weather in the 3 hour time period is shown along with pictorial representation. Also you can see the forthcoming weather forecast for 5 days in 3 hours interval.

Units are set as imperial by default and App tries to fetch data by using openweathermap api immediately on the launch. If there is no internet or server can't be reached, user sees a toast message saying "Connectivity error". If for some reason , api returns non-JSON/unparsable data, user sees a toast message saying "Json parsing error" with error explanation. 

Weather details are shown only for the current interval and future forecast shows only the min/max temperature, pictorial representation of the weather condition.

Features in future commits:
1.Location searchBar
2.Show weather based on geo location 
3.Settings for default location, units
4.Caching data and reducing the network requests
5.Multiple location forecast

