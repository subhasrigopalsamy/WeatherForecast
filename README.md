# WeatherForecast
<b>Features:</b><br />
The App shows recent weather forecast for Cambridge along with wind speed, rain , humidity and pressure.A simple description of the weather in the 3 hour time period is shown along with pictorial representation. Also you can see the forthcoming weather forecast for 5 days in 3 hours interval.

Units are set as imperial by default and App tries to fetch data by using openweathermap api immediately on the launch. If there is no internet or server can't be reached, user sees a toast message saying "Connectivity error". If for some reason , api returns non-JSON/unparsable data, user sees a toast message saying "Json parsing error" with error explanation. 

Weather details are shown only for the current interval and future forecast shows only the min/max temperature, pictorial representation of the weather condition.

<b>Features in future commits:</b><br />
Location searchBar <br />
Show weather based on geo location <br />
Settings for default location, units <br />
Caching data and reducing the network requests <br />
Multiple location forecast <br />
View-Data model implementation</br>

<b>Build and Run project</b><br/>
Clone or download project from Github<br/>
Open Android Studio and import project<br/>
Gradle dependencies(2.3.3) should resolve itself <br/>
Build and Run apk from android studio <br/>

![alt text](https://github.com/subhasrigopalsamy/WeatherForecast/blob/master/Screenshot.png)
