package com.example.jettip

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jettip.components.InputField
import com.example.jettip.components.InputField
import com.example.jettip.ui.theme.JetTipTheme
import com.example.jettip.util.calculateTotalPerPerson
import com.example.jettip.util.calculateTotalTip
import com.example.jettip.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                MyApp {
                }
            Column {
                MainContent()
            }

            }
        }
    }
@Composable
fun MyApp(content: @Composable () -> Unit){
    JetTipTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background) {
            content()
        }
    }
}
//@Preview
@Composable
fun TopHeader(totalPerPerson : Double = 0.0){
    Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(15.dp)
        .height(150.dp)
        //.clip(shape = RoundedCornerShape(corner = CornerSize(12.dp)))
        .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)){
        Column(
            modifier= Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            val total = "%.2f".format(totalPerPerson)// used to make in 2 decimal points
            Text(text = "Total Per Person", style = MaterialTheme.typography.headlineSmall)
            Text(text = "$$total", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)

        }
    }

}


@Preview
@Composable
fun MainContent(){
    BillForm()

    }


@Composable
fun BillForm(modifier: Modifier = Modifier,
             onValChange :(String)-> Unit ={}){
    val totalBillState   = remember{
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController  = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableFloatStateOf(0f)
    }
    val tipPercentage = (sliderPositionState.value * 100).toInt()
    val splitByState = remember {
        mutableIntStateOf(1)
    }
    val range = IntRange(start = 1 , endInclusive = 100)

    val tipAmountState = remember {
        mutableDoubleStateOf(0.0)
    }
    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }
    TopHeader(totalPerPerson = totalPerPersonState.value)
    Surface(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth(),
        shape = RoundedCornerShape(CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)){
        Column(modifier = Modifier.padding(6.dp),
               verticalArrangement = Arrangement.Top,
               horizontalAlignment = Alignment.Start){
            InputField(valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions{
                    if (!validState) return@KeyboardActions
                    onValChange(totalBillState.value.trim())

                    keyboardController?.hide()
                })
            if (validState){
                Row(modifier= Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start) {
                    Text(text = "Split",
                        modifier = Modifier.align(
                            alignment = Alignment.CenterVertically), fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End) {
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                      splitByState.intValue =
                                          if (splitByState.intValue > 1 ){ splitByState.intValue - 1}
                                          else 1
                                totalPerPersonState.value =
                                    calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.intValue,
                                        tipPercentage = tipPercentage)

                            },
                            elevation = CardDefaults.cardElevation(4.dp))
                        Text(text = "${splitByState.intValue} ",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(9.dp), fontSize = 20.sp)
                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = { if (splitByState.intValue < range.last){
                                splitByState.intValue += 1
                                totalPerPersonState.value =
                                    calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.intValue,
                                        tipPercentage = tipPercentage)
                            }
                                                                      },
                            elevation = CardDefaults.cardElevation(4.dp))
                    }
                }
            // Tip row
            Row(modifier = Modifier.padding(horizontal = 5.dp , vertical = 15 .dp),
                horizontalArrangement = Arrangement.Start){
                Text(text = "Tip",
                    modifier = Modifier.align(
                        alignment = Alignment.CenterVertically), fontSize = 20.sp)
                Spacer(modifier = Modifier.width(180.dp))
                Text(text = "$ ${tipAmountState.doubleValue}",modifier = Modifier.align(
                    alignment = Alignment.CenterVertically), fontSize = 20.sp)
            }
            Column (verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally){
                Text(text = "$tipPercentage %", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(15.dp))

                // Slider
                Slider(value = sliderPositionState.floatValue,
                    onValueChange = {newvalue->
                        sliderPositionState.floatValue = newvalue
                        tipAmountState.doubleValue = calculateTotalTip(totalBillState.value.toDouble() ,tipPercentage)

                        totalPerPersonState.value =
                            calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                                splitBy = splitByState.intValue,
                                tipPercentage = tipPercentage)
                    },
                    modifier = Modifier.padding(start = 16.dp , end = 16.dp))

                
            }

            }
            else{
                Box {}}
        }

    }

}




@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetTipTheme {
        MyApp {

        }
    }
}