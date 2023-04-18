package com.example.ethan.usecases


class LunchBreakConsultantTest : UseCaseTest() {

    override var abstractUseCase = LunchBreakConsultant {
        // this code will be executed when the use case is finished
        println("Good morning dialogue is finished!")
    } as AbstractUseCase

    override fun mockingbird() {
        mock_waitForAPIs(5000)
        mock_onEthanVoiceOutputFinished(10)
        mock_speakAndHearSelectiveInput("15:00")
        println("a1")
        mock_waitForAPIs(5000)
        println("a2")
        mock_speakAndHearSelectiveInput("indian")
        mock_waitForAPIs(5000)
        mock_onEthanVoiceOutputFinished(10)
        println("a3")
        mock_speakAndHearSelectiveInput("1")
        mock_waitForAPIs(3000)

        println("a4s")
        mock_onEthanVoiceOutputFinished(1)


    }

    override fun mockingbird2() {
        TODO("Not yet implemented")
    }

    override fun mockingbird3() {
        TODO("Not yet implemented")
    }

    override fun mockingbird4() {
        TODO("Not yet implemented")
    }

}