package com.example.ethan.usecases

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.example.ethan.AgentHandler
import com.example.ethan.sharedprefs.SharedPrefs
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockedStatic.Verification
import org.mockito.Mockito
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mockStatic
import org.mockito.MockitoAnnotations
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId



abstract class UseCaseTest {

    abstract var abstractUseCase: AbstractUseCase

    @Mock
    lateinit var activity: Activity

    @Mock
    lateinit var sharedPrefs: SharedPreferences

    @Mock
    lateinit var mockDateTime: LocalDateTime


    @Before
    fun setUp(){
        /*
        val clock: Clock = Clock.fixed(Instant.parse("2023-04-20T06:08:30.00Z"), ZoneId.of("UTC"))

        val dateTime: LocalDateTime = LocalDateTime.now(clock)
        val instant = Instant.now(clock)

        mockStatic(Instant::class.java).use { mockedStatic ->
            mockedStatic.`when`<Any>(Verification { Instant.now() })
                .thenReturn(instant)
        }

        mockStatic(LocalDateTime::class.java).use { mockedStatic ->
            mockedStatic.`when`<Any>(Verification { LocalDateTime.now() })
                .thenReturn(dateTime)
        }*/
        /*
        mockDateTime = Mockito.mock(LocalDateTime::class.java)

        val clock: Clock = Clock.fixed(Instant.parse("2023-04-20T06:08:30.00Z"), ZoneId.of("UTC"))
        val dateTime: LocalDateTime = LocalDateTime.now(clock)
        Mockito.`when`(mockDateTime.getnow()).thenReturn(dateTime)

        // now you can call the execute method to start the use case
        MockitoAnnotations.openMocks(this)
        */

        val mockEditor = Mockito.mock(SharedPreferences.Editor::class.java)
        Mockito.`when`(activity.getPreferences(Context.MODE_PRIVATE)).thenReturn(sharedPrefs)

        // First time initialization
        Mockito.`when`(sharedPrefs.contains("initialized")).thenReturn(false)
        Mockito.`when`(sharedPrefs.edit()).thenReturn(mockEditor)
        Mockito.`when`(
            mockEditor.putString(
                "08:00",
                AgentHandler.goodMorningDialogue.getResTimeID()
            )
        ).thenReturn(mockEditor)
        Mockito.`when`(
            mockEditor.putString(
                "12:00",
                AgentHandler.lunchBreakConsultant.getResTimeID()
            )
        ).thenReturn(mockEditor)
        Mockito.`when`(
            mockEditor.putString(
                "15:00",
                AgentHandler.navigationAssistance.getResTimeID()
            )
        ).thenReturn(mockEditor)
        Mockito.`when`(mockEditor.putString("18:00", AgentHandler.socialAssistance.getResTimeID())).thenReturn(mockEditor)
        Mockito.`when`(mockEditor.putString("steam_id", "76561198198615839")).thenReturn(mockEditor)
        Mockito.`when`(mockEditor.putBoolean("initialized", true)).thenReturn(mockEditor)
        Mockito.`when`(sharedPrefs.getString("steam_id","")).thenReturn("76561198198615839")
        Mockito.`when`(sharedPrefs.getInt("fav_games_genre",-1)).thenReturn(-1)


        doNothing().`when`(mockEditor).apply()

        SharedPrefs.initSharedPrefs(activity)

        setStaticFieldViaReflection(Build.VERSION::class.java.getField("SDK_INT"), 33)
    }

    @Test
    fun createTest1() {

        var t = Thread {
            mockingbird1()
        }.start()
        abstractUseCase.executeUseCase()

        println("Test")
    }

    @Test
    fun createTest2() {

        var t = Thread {
            mockingbird2()
        }.start()
        abstractUseCase.executeUseCase()

        println("Test")
    }

    @Test
    fun createTest3() {

        var t = Thread {
            mockingbird3()
        }.start()
        abstractUseCase.executeUseCase()

        println("Test")
    }

    @Test
    fun createTest4() {

        var t = Thread {
            mockingbird4()
        }.start()
        abstractUseCase.executeUseCase()

        println("Test")
    }

    abstract fun mockingbird1()

    abstract fun mockingbird2()

    abstract fun mockingbird3()

    abstract fun mockingbird4()


    fun mock_waitForAPIs(duration: Long) {
        Thread.sleep(duration)
    }

    fun mock_speakAndHearSelectiveInput(userInput: String) {
        mock_onEthanVoiceOutputFinished(1)
        abstractUseCase.onUserVoiceInputReceived(userInput)
        mock_onEthanVoiceOutputFinished(2)
    }

    fun mock_onEthanVoiceOutputFinished(times: Int) {
        for (i in 0 until times) {
            abstractUseCase.onEthanVoiceOutputFinished()
            if (i < times -1)Thread.sleep(100)
        }
    }

    private fun setStaticFieldViaReflection(field: Field, value: Any) {
        field.isAccessible = true
        Field::class.java.getDeclaredField("modifiers").apply {
            isAccessible = true
            setInt(field, field.modifiers and Modifier.FINAL.inv())
        }
        field.set(null, value)
    }
}