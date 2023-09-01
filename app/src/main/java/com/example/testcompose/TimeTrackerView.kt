package com.example.testcompose

class TimeTrackerView {
    fun AddTime( eventList : List<TimedEvent>) : Long{
        var sum = 0L

        eventList.forEach{event ->
            sum += event.GetTime()
        }
        return sum
    }

    fun FormatTime (seconds : Long) : String{
        val MIN_IN_HR : Long = 60
        val SEC_IN_MIN : Long = 60

        val curSec = seconds % SEC_IN_MIN
        val totMin = seconds / MIN_IN_HR
        val curMin = totMin % MIN_IN_HR
        val curHr = totMin / MIN_IN_HR

        return "$curHr : $curMin : $curSec"
    }
}