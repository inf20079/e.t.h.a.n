package com.example.ethan.usecases

import com.example.ethan.LocalLocation
import com.example.ethan.api.connectors.*
import com.example.ethan.sharedprefs.SharedPrefs
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Math.abs
import com.example.ethan.transportation.transportTranslations
import kotlinx.coroutines.runBlocking

class LunchBreakConsultant(onFinishedCallback: () -> Unit) : AbstractUseCase(onFinishedCallback)  {

    override var shortForm: String = "LBC"
    private var openStreetMapRestaurant = OpenStreetMapApi()
    private var calendarConnector = CalendarConnector()
    private val openRouteConnector = OpenRouteConnector()

    override fun executeUseCase() {

        println("test0")
        var origin = LocalLocation.getCurrentLocation()

        var breakTime = 12
        var breakDuration = 1
        var suggested_breakTime = breakTime
        val timeOptions = mutableListOf<UserInputOption>()
        for (i in -23..0) {
            val option = UserInputOption(
                tokens = listOf(abs(i).toString()),
                onSuccess = {
                    breakTime = abs(i)
                    println("") // DO NOT DELETE THIS LINE
                }
            )
            timeOptions.add(option)
        }
        println("Test1")
        speakAndHearSelectiveInput(
            question = "Hi. I'm here to assure you having the best break today. Around what hour do" +
                    " prefer to eat something?", options = timeOptions
        )

        println("test2")
        val eventsFreeBusy_json = calendarConnector.get()
        val eventsTotal = eventsFreeBusy_json.getInt("total")
        var event_before: JSONObject? = null
        val breaks = JSONArray()

        if (eventsTotal == 0){
            // Preferred time is available
        }else {
            eventsFreeBusy_json.getJSONObject("events").keys().forEach {
                val event = eventsFreeBusy_json.getJSONObject("events").getJSONObject(it)
                val slot = JSONObject()
                if(event_before == null){
                    slot.put("startHour", 0)
                    slot.put("startMinute", 0)
                    slot.put("endHour", event.getInt("startHour"))
                    slot.put("endMinute", event.getInt("startMinute"))
                    slot.put("duration", event.getInt("startHour")*60+event.getInt("startMinute"))
                }else {
                    slot.put("startHour", event_before!!.getInt("endHour"))
                    slot.put("startMinute", event_before!!.getInt("endMinute"))
                    slot.put("endHour", event.getInt("startHour"))
                    slot.put("endMinute", event.getInt("startMinute"))
                    slot.put("duration", ( (event.getInt("startHour")*60+event.getInt("startMinute")) - (event_before!!.getInt("endHour")*60+event_before!!.getInt("endMinute"))))
                }
                event_before = event
                breaks.put(slot)
            }

            val slot = JSONObject()
            slot.put("startHour", event_before!!.getInt("endHour"))
            slot.put("startMinute", event_before!!.getInt("endMinute"))
            slot.put("endHour", 23)
            slot.put("endMinute", 59)
            slot.put("duration", (23*60+59) - (event_before!!.getInt("endHour")*60+event_before!!.getInt("endMinute")))
            breaks.put(slot)

            var min_distance = 24*60
            var best_break = JSONObject()
            for (x in 0 until breaks.length()){
                val option = breaks.getJSONObject(x)
                val distance_start = abs(breakTime*60 - (option.getInt("startHour")*60+option.getInt("startMinute")))
                val distance_end = abs(breakTime*60 - (option.getInt("endHour")*60+option.getInt("endMinute")))
                if(distance_start < min_distance){
                    min_distance = distance_start
                    best_break = option
                }
                if (distance_end < min_distance){
                    min_distance = distance_end
                    best_break = option
                }
            }

            if((best_break.getInt("startHour")*60+best_break.getInt("startMinute")) < breakTime*60 &&
                (best_break.getInt("endHour")*60+best_break.getInt("endMinute")) > breakTime*60+breakDuration*60 ){
                suggested_breakTime = breakTime
            }else if((best_break.getInt("endHour")*60+best_break.getInt("endMinute")) < breakTime*60+breakDuration*60) {
                // Break ends before preferred break ends
                if(best_break.getInt("duration") >= breakDuration*60){
                    suggested_breakTime = (best_break.getInt("endHour")*60+best_break.getInt("endMinute"))-breakDuration*60
                }else {
                    suggested_breakTime = (best_break.getInt("startHour")*60+best_break.getInt("startMinute"))
                }
            } else if((best_break.getInt("startHour")*60+best_break.getInt("startMinute")) > breakTime*60){
                // Break starts after preferred break starts
                suggested_breakTime = (best_break.getInt("startHour")*60+best_break.getInt("startMinute"))
            }
        }
        println("Start your break at: " + suggested_breakTime)

        val validCuisines = listOf("afghan", "african", "algerian", "american", "arab", "argentinian", "armenian", "asian", "australian", "austrian", "azerbaijani", "balkan",
            "bangladeshi", "basque", "bbq", "belarusian", "belgian", "brazilian", "breakfast", "british", "bulgarian", "burmese", "cajun", "cambodian", "cameroonian", "canadian",
            "caribbean", "caucasian", "central_asian", "chilean", "chinese", "colombian", "corsican", "cote_d'ivoirean", "croatian", "cuban", "cuisine_of_the_levant", "cypriot", "czech",
            "danish", "delicatessen", "dominican", "donut", "dutch", "east_african", "ecuadorian", "egyptian", "emirati", "english", "eritrean", "estonian", "ethiopian", "european", "filipino",
            "finnish", "fondue", "french", "fusion", "gabonese", "galician", "georgian", "german", "ghanaian", "greek", "grill", "guatemalan", "haitian", "hawaiian", "himalayan_nepalese", "honduran",
            "hong_kong", "hot_dogs", "hungarian", "ice_cream", "indian", "indonesian", "international", "iranian", "iraqi", "irish", "israeli", "italian", "jamaican", "japanese", "jewish_kosher",
            "jordanian", "kazakh", "kenyan", "korean", "kurdish", "kyrgyz", "laotian", "latin_american", "latvian", "lebanese", "libyan", "liechtenstein", "lithuanian", "luxembourgian", "macanese",
            "macedonian", "malagasy", "malaysian", "malian", "maltese", "mauritian", "mexican", "middle_eastern", "moldovan", "mongolian", "moroccan", "mozambican", "multicultural", "native_american",
            "nepalese", "new_zealand", "nicaraguan", "nigerian", "norwegian", "organic", "pakistani", "pan_asian", "paraguayan", "persian", "peruvian", "pizza", "polish", "polynesian", "portuguese",
            "pub_food", "puerto_rican", "romanian", "russian", "salvadoran", "sandwich", "scandinavian", "scottish", "seafood", "senegalese", "serbian", "sicilian", "singaporean", "slovak", "slovenian",
            "soul_food", "south_african", "south_american", "southern", "spanish", "sri_lankan", "steak_house", "sudanese", "swahili", "swedish", "swiss", "syrian", "taiwanese",
            "tanzanian", "tapas", "tex-mex", "thai", "tibetan", "tunisian", "turkish", "ukrainian", "uruguayan", "uzbek", "vegan", "vegetarian", "venezuelan", "vietnamese", "welsh",
            "west_african", "yemeni", "zambian","zimbabwean"
        )
        println("test3")
        val validCuisines_input = validCuisines.map {it.replace("_", " ")}

        var seletedCuisine = ""
        val cuisineOptions = mutableListOf<UserInputOption>()
        for (i in validCuisines_input.indices) {
            val option = UserInputOption(
                tokens = listOf(validCuisines_input[i]),

                onSuccess = {
                    seletedCuisine = validCuisines[i]
                    println("") // DO NOT DELETE THIS LINE
                }
            )
            cuisineOptions.add(option)
        }
        speakAndHearSelectiveInput(
            question = "What cuisine do you have in mind for today?", options = cuisineOptions
        )
        println("test3")
        var restaurants = listOf<OsmRestaurant>()
        while (restaurants.isEmpty()) {

            runBlocking { speak("Got you! I will find a restaurant with a $seletedCuisine cuisine and calculate how you can get there by ${transportTranslations[SharedPrefs.getTransportation()]}.") }
            runBlocking { speak("Beep, Boop, Beep, Boop...") }

            restaurants = openStreetMapRestaurant.findNearestRestaurants(
                origin.getString("lat").toDouble(),
                origin.getString("lon").toDouble(),
                1000,
                seletedCuisine
            )
            println(restaurants)
            if (restaurants.isEmpty()) {
                runBlocking { speak("Sadly, I didn't find a fitting restaurant in a radius of 1000 meters") }
                speakAndHearSelectiveInput(
                    question = "Please give me a different cuisine to search for.",
                    options = cuisineOptions
                )
                println("cuisineOptions")


            }
        }
        println("test4")

        val restaurantCount = minOf(3, restaurants.count())
        if (restaurantCount == 1) {
            val restaurant = restaurants[0]
            val name = restaurant
            val website = restaurant.website
            runBlocking { speak("What about $name? You can find their website here: $website") }
            println("test5.5")
        }
        else {
            println("test5")
            var restaurantsNamesString = ""
            for (i in 0 until restaurantCount) {
                if (i > 0)
                    restaurantsNamesString += ", "
                restaurantsNamesString += restaurants[i].name
            }
            runBlocking { speak("I found the following restaurants: $restaurantsNamesString") }
            println("test6")
            var selectedRestaurant = restaurants[0]
            val restaurantOptions = mutableListOf<UserInputOption>()
            for (i in 0 until restaurantCount) {
                val option = UserInputOption(
                    tokens = when (i) {
                        0 -> listOf("1", "one", "first", restaurants[i].name)
                        1 -> listOf("2", "two", "second", restaurants[i].name)
                        else -> listOf("3", "three", "third", "last", restaurants[i].name)
                    },
                    onSuccess = {
                        selectedRestaurant = restaurants[i]
                        println("") // DO NOT DELETE THIS LINE
                    }
                )
                restaurantOptions.add(option)
            }
            println("test7")
            speakAndHearSelectiveInput(
                question = "Which one sounds the most appealing for you?", options = restaurantOptions
            )
            runBlocking { speak("Okay. This is the website of ${selectedRestaurant.name}: ${selectedRestaurant.website}") }

            origin = LocalLocation.getCurrentLocation()
            val originString = "${origin.getString("lon")},${origin.getString("lat")}"
            val destinationString = "${selectedRestaurant.lon},${selectedRestaurant.lat}"
            val properties = openRouteConnector.getRoute(originString, destinationString, SharedPrefs.getTransportation())

            if (properties == null) {
                runBlocking { speak("I sadly could not find a route to this restaurant.") }
            } else {
                val duration = openRouteConnector.getRouteDuration(properties).toInt()
                val instructions = openRouteConnector.getRouteInstructions(properties)

                runBlocking { speak("By ${transportTranslations[SharedPrefs.getTransportation()]}, you will need to travel $duration minutes. "+
                    "You get there by following these instructions:") }

                var instructionsString = ""
                for (i in instructions.indices) {
                    instructionsString += "$i. ${instructions[i].first}. "
                    //instructionsString += if (i < instructions.size - 1)
                    //    ", then walk for ${instructions[i].second / 60} minutes."
                    //else
                    //    "."
                }

                runBlocking { speak(instructionsString) }
            }

        }
        onFinishedCallback()
    }
}