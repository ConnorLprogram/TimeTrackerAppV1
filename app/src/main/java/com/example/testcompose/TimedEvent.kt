package com.example.testcompose

class TimedEvent (private var name : String, private var time: Long){
    //private var name: String = ""
    //private var time: Long = 0
    private var group: String = ""

    fun SetName(newName : String){
        this.name = newName;
    }

    fun GetName() : String{
        return this.name;
    }

    fun SetTime(newTime : Long){
        this.time = newTime;
    }

    fun GetTime() : Long{
        return this.time;
    }
}