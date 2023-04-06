package com.example.ethan.usecases

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.ethan.AgentHandler
import com.example.ethan.api.connectors.CalendarConnector
import com.example.ethan.api.connectors.FortuneConnector
import com.example.ethan.api.connectors.NewsConnector
import com.example.ethan.api.connectors.StocksConnector
import com.example.ethan.ui.speech.Speech2Text
import com.example.ethan.ui.speech.Text2Speech
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime

class GoodMorningDialogue(onFinishedCallback: () -> Unit) : AbstractUseCase(onFinishedCallback) {
    private var fortuneConnector = FortuneConnector()
    private var newsConnector = NewsConnector()
    private var stocksConnector = StocksConnector()
    private var calendarConnector = CalendarConnector()

    override fun getExecutionTime(): LocalDateTime {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.of(
                /* year = */ 2023,
                /* month = */ 4,
                /* dayOfMonth = */ 6,
                /* hour = */ 12,
                /* minute = */ 0,
                /* second = */ 0,
                /* nanoOfSecond = */ 0)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

    override fun initUseCase() {
        Speech2Text.setCallback()
        { input ->
            AgentHandler.goodMorningDialogue.onUserVoiceInputReceived(input)
        }
        Speech2Text.setErrorCallback()
        { error: Int ->
            AgentHandler.goodMorningDialogue.onUserVoiceInputError(error)
        }
        Text2Speech.setCallback()
        {
            -> AgentHandler.goodMorningDialogue.onEthanVoiceOutputFinished()
        }
    }

    override fun executeUseCase() {

        println("GoodMorningDialogue Thread has been started!")

        // Request API 1
        println("a")
        val fortune_json = fortuneConnector.get()
        val fortune_string = fortune_json.getString("fortune")

        // Reqeuest API 0
        val eventsFreeBusy = calendarConnector.get()["answer"]

        // Request API 2
        val news_json = newsConnector.get()
        println(news_json)
        val news_articles = news_json.getJSONArray("articles")
        var news_string = ""
        for (i in 0..0) {
            val article = news_articles.getJSONObject(i)
            val title = article.getString("title")
            val description = article.getString("description")
            news_string += ("Article " + (i + 1) + ": $title. "
                            + "$description ")
        }
        println(news_string)
        // Request API 3
        val stockslist_tickers = listOf("AAPL", "MSFT", "GOOG")
        val stockslist_names = listOf("Apple", "Microsoft", "Alphabet")
        var stocknews_string = ""
        for (i in stockslist_tickers.indices)
        {
            val stocknews_json = stocksConnector.get(stockslist_tickers[i])
            println(stocknews_json)
            val stocknews_quote = stocknews_json.getJSONObject("Global Quote")
            val price = stocknews_quote.optString("05. price").toFloat().toString()
            stocknews_string += "Last price of " + stockslist_names[i] + " was $price$. "
        }
        //println(stocknews_string)


        val now = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        // Greet user with all gather information
        runBlocking { speak("Good Morning. Today is the ${now.dayOfMonth} of ${now.month}. It is ${now.hour} o'clock and ${now.minute} minutes. ")}
        runBlocking { speak("$eventsFreeBusy")}
        runBlocking { speak("Here is your daily update for your preferred stocks: $stocknews_string")}
        runBlocking { speak("Now your daily news: $news_string")}

        // Ask for his preferred transportation method
        runBlocking { askForUserVoiceInput("What is your favorite type of transportation for this day?") }

        if (checkIfContainsWord("bus")) {
            runBlocking { speak("You successfully set bus as your favourite transportation method for today.") }
        }else if(checkIfContainsWord("train")){
            runBlocking { speak("You successfully set train as your favourite transportation method for today.") }
        }else if(checkIfContainsWord("bike")){
                runBlocking { speak("You successfully set bike as your favourite transportation method for today.") }
        }else if(checkIfContainsWord("foot")){
                runBlocking { speak("You successfully set walking as your favourite transportation method for today.") }
        }

        var yesOrNo = false
        runBlocking { askForUserVoiceInput("Okay cool. Do you want to hear your horoscope for today?") }
        while (!(checkIfPositive(lastUserVoiceInput) || checkIfNegative(lastUserVoiceInput)))
            runBlocking { askForUserVoiceInput("I didn't understand you. Please repeat. ") }

        if (checkIfPositive(lastUserVoiceInput)) {
            runBlocking { speak(fortune_string) }
        } else if (checkIfNegative(lastUserVoiceInput)) {
            runBlocking { speak("...") }
            runBlocking { speak("My personal guess is that you won't have any luck today.") }
        }

        runBlocking { speak("Have a great day!") }
        // Say how long it'll take the user to its destination

        println("GoodMorningDialogue Thread is about to end!")
        onFinishedCallback()
    }
}