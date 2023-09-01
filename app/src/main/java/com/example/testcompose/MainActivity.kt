package com.example.testcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.with
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testcompose.ui.theme.TestComposeTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestComposeTheme {
                // A surface container using the 'background' color from the theme
                AppHandler()
            }
        }
    }
}

@Composable
fun AppHandler(){
    //val curEvents = rememberSaveable { mutableStateListOf<TimedEvent>()}
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        App()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun App(){
    val curEvents = remember { mutableStateListOf<TimedEvent>()}
    val curChosenEvents = remember { mutableStateListOf<TimedEvent>()}
    val curTT = TimeTrackerView()


    Column {

        Text(text = "Events", Modifier.fillMaxWidth(),
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center)



        var infoText by remember { mutableStateOf("Hello") }
        var tempTimeText by remember { mutableStateOf("---") }

        AnimatedContent(
            targetState = infoText,
            transitionSpec = {
                scaleIn(animationSpec = tween(durationMillis = 500)) with
                        ExitTransition.None
            }) {infoString ->
                Text(text = infoString)
        }
        AnimatedContent(
            targetState = tempTimeText,
            transitionSpec = {
                scaleIn(animationSpec = tween(durationMillis = 500)) with
                        ExitTransition.None
            }) {tempTimeString ->
            Text(text = tempTimeString)
        }

        //InputBox("Name")
        //InputNumBox("Time")
        var nameText by remember { mutableStateOf("Hello") }

        //InputBoxTest(onValueChange = { tempNameText = it})

        var numText by remember { mutableStateOf("123") }


        val addEventButton = {
            if (nameText.isNullOrBlank() || numText.isNullOrBlank()) {
                infoText = "Invalid input"
            } else {
                val name = nameText
                val time = numText.toLong()
                infoText = if (curEvents.indexOfFirst { event -> event.GetName() == name } < 0){
                    curEvents.add(TimedEvent(name, time))
                    "Event $name added"
                } else{
                    "Already exists"
                }
            }
        }

        val deleteEventButton = { curVal : String ->
            //val curVal = it
            curEvents.removeAt(curEvents.indexOfFirst { event ->
                event.GetName() ==  curVal
            })

            if (curChosenEvents.removeAll { event ->
                    event.GetName() == curVal
                }){


                val time = curTT.AddTime(curChosenEvents)
                tempTimeText = "Current total time: ${FormatTime(time)}"

                infoText = "Deleted $curVal"
            }
        }

        val addEventTimeButton = { curVal : String ->
            val tEvent = curEvents[curEvents.indexOfFirst { event ->
                event.GetName() ==  curVal }]

            curChosenEvents.add(tEvent)
            val time = curTT.AddTime(curChosenEvents)
            tempTimeText = "Current total time: ${FormatTime(time)}"
            infoText = "Added ${FormatTime(tEvent.GetTime())}"
        }

        val removeEventTimeButton = { curVal : String ->
            //val curVal = it

            val index = curChosenEvents.indexOfFirst { event ->
                event.GetName() == curVal
            }

            if (index >= 0){
                val event = curChosenEvents[index]
                curChosenEvents.removeAt(index)
                val time = curTT.AddTime(curChosenEvents)
                tempTimeText = "Current total time: ${FormatTime(time)}"
                infoText = "Subtracted ${FormatTime(event.GetTime())}"
            }
        }

        var expanded by remember { mutableStateOf (true) }

        Column() {
            Row() {
                var showText by remember { mutableStateOf("Hide") }

                if (expanded){
                    showText = "Hide"
                }
                else{
                    showText = "Show"
                }
                Button(onClick = { expanded = !expanded }, modifier = Modifier
                    .height(25.dp)
                    .width(40.dp),
                    contentPadding = PaddingValues(0.dp)) {
                    Text(text = showText)
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column() {
                    TextField(
                        value = nameText,
                        onValueChange = { nameText = it },
                        label = { Text("Name") }
                    )

                    TextField(
                        value = numText,
                        onValueChange = { numText = it },
                        label = { Text("Time") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Row() {
                        Button(onClick = addEventButton) {
                            Text(text = "Add Event")
                        }
                        Button(onClick = {
                            curChosenEvents.clear()
                            tempTimeText = "Current total time: 0 seconds"
                            infoText = "Cleared events used"
                        }) {
                            Text(text = "Clear")
                        }
                    }
                }

            }
        }



        Row(Modifier.weight(3f)) {
            EventList(curEvents,
                onDelete = deleteEventButton,
                onAddEvent = addEventTimeButton,
                onRemoveEvent = removeEventTimeButton,
                Modifier.weight(3f))
        }

        Row(Modifier.weight(1f)) {
            UsedEventList(curChosenEvents, onAddEvent = addEventTimeButton,
                onRemoveEvent = removeEventTimeButton)
        }

    }
}

/*fun addEventToList(eventsList : SnapshotStateList<TimedEvent>,
                   curEvents : SnapshotStateList<TimedEvent>,
                   curTT : TimeTrackerView,
                   eventName : String,
                   timeText : String,
                   infoText : String) {
    val tEvent = eventsList[eventsList.indexOfFirst { event ->
                    event.GetName() == eventName }]

    curEvents.add(tEvent)

    val time = curTT.AddTime(curEvents)
    timeText = "Current total time: $time"
    infoText = "Added ${tEvent.GetTime()} seconds"
}*/

@Composable
fun EventList(curEvents : SnapshotStateList<TimedEvent>,
              onDelete : (String) -> Unit,
              onAddEvent : (String) -> Unit,
              onRemoveEvent : (String) -> Unit,
              modifier: Modifier = Modifier){

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 135.dp)
    ) {
        items(curEvents.sortedBy { it.GetName() }) { event ->
            EventItem(event.GetName(),
                        event.GetTime(),
                        onDelete,
                        onAddEvent,
                        onRemoveEvent)
        }
    }
}

@Composable
fun UsedEventList(
    curEvents: SnapshotStateList<TimedEvent>,
    onAddEvent: (String) -> Unit,
    onRemoveEvent: (String) -> Unit
){
    var eventMap = mutableMapOf<String, Int>()

    curEvents.forEach { event ->
        val curVal = eventMap[event.GetName()]
        if (curVal != null) {
            eventMap[event.GetName()] = curVal + 1
        }
        else{
            eventMap[event.GetName()] = 1
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 135.dp)
    ) {
        items(eventMap.keys.toList().sorted()) { eventName ->
            //Text(text = "$eventName x ${eventMap[eventName]}")
            if (eventMap[eventName] != null){
                UsedEventItem(eventName, eventMap[eventName]!!, onAddEvent, onRemoveEvent)
            }
        }
    }
}

@Composable
fun EventItem(name : String, time: Long,
              onDelete : (String) -> Unit,
              onAddEvent : (String) -> Unit,
              onRemoveEvent : (String) -> Unit){
    Column{
        Row (horizontalArrangement = Arrangement.SpaceBetween){
            Column(Modifier.weight(1f)) {
                Text(text = name)
                Text(text = "${FormatTime(time)}")
            }
            /*Button(onClick = {

            }) {
                Text("Edit", fontSize = 7.sp)
            }*/
            Button(onClick = { onDelete(name) },
                    Modifier.weight(1f)) {
                Text(text = "Delete", fontSize = 7.sp)
            }
        }
        Row {
            Button(onClick = {
                onAddEvent(name)
            }, Modifier.weight(1f)) {
                Text(text = "Add Time", fontSize = 7.sp)
            }

            Button(onClick = {
                onRemoveEvent(name)
            }, Modifier.weight(1f)) {
                Text(text = "Remove Time", fontSize = 7.sp)
            }
        }
    }
}

@Composable
fun UsedEventItem(eventName : String,
                  numEvents : Int,
                  onAddEvent : (String) -> Unit,
                  onRemoveEvent : (String) -> Unit){
    Row() {
        Column(Modifier.weight(3f)) {
            Text(text = "$eventName x ")
        }

        Column(Modifier.weight(1f)) {
            Button(onClick = { onRemoveEvent(eventName) },
                modifier = Modifier
                    .height(20.dp)
                    .width(20.dp),
                contentPadding = PaddingValues(0.dp)) {
                Text(text = "-")
            }
        }

        Column(Modifier.weight(1f)) {
            Text(text = " $numEvents" )
        }

        Column(Modifier.weight(1f)) {
            Button(onClick = { onAddEvent(eventName) },
                modifier = Modifier
                    .height(20.dp)
                    .width(20.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(text = "+")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputBox(labelName : String){
    var text by remember { mutableStateOf("Hello") }

    TextField(
        value = text,
        onValueChange = { text = it },
        label = { Text(labelName) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputBoxTest(onValueChange : (String) -> Unit){
    var text by remember { mutableStateOf("Hello") }
    //var text = outputText
    TextField(
        value = text,
        onValueChange = { text = it }
    )

    Button(onClick = { onValueChange(text) }) {
        Text(text = "Change")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputNumBox(labelName : String){
    var text by remember { mutableStateOf("123") }

    TextField(
        value = text,
        onValueChange = { text = it },
        label = { Text(labelName) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

/*@Composable
fun SimpleFilledTextFieldSample() {
    var text by remember { mutableStateOf("Hello") }

    TextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("Label") }
    )
}*/

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun AppPreview(){
    TestComposeTheme {
        App()
    }
}

@Preview(showBackground = true)
@Composable
fun EventListPreview(){
    val demoList = SnapshotStateList<TimedEvent>()
    demoList.add(TimedEvent("Test", 5))
    demoList.add(TimedEvent("Test2", 5))
    TestComposeTheme {
        EventList(demoList,
            onDelete = {},
            onAddEvent = {},
            onRemoveEvent = {})
    }
}

@Preview(showBackground = true)
@Composable
fun EventItemPreview(){
    TestComposeTheme {
        EventItem("Test", 4,
            onDelete = {},
            onAddEvent = {},
            onRemoveEvent = {})
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestComposeTheme {
        Greeting("Android")
    }
}

@Preview(showBackground = true)
@Composable
fun UsedEventItemPreview(){
    TestComposeTheme() {
        UsedEventItem(eventName = "Test", numEvents = 3, onAddEvent = {}, onRemoveEvent = {})
    }
}



fun FormatTime (seconds : Long) : String{
    val MIN_IN_HR : Long = 60
    val SEC_IN_MIN : Long = 60

    val curSec = seconds % SEC_IN_MIN
    val totMin = seconds / MIN_IN_HR
    val curMin = totMin % MIN_IN_HR
    val curHr = totMin / MIN_IN_HR

    return "${if (curHr < 10) {"0$curHr"} else curHr}:${if (curMin < 10) {"0$curMin"} else curMin}:${if (curSec < 10) {"0$curSec"} else curSec}"
}