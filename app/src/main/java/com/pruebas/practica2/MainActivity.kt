package com.pruebas.practica2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pruebas.practica2.ui.theme.Practica2Theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Practica2Theme {
                App()
            }
        }
    }
}

@Composable
fun App()
{
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "mainmenu"
    )
    {
        composable("mainmenu")
        {
            MainMenu(   {
                Log.d("MyApp", "boton de login presionado")
                navController.navigate("home")

            }    )
        }
        composable("home")
        {
            Home()
        }
    }

}

@Composable
//recibe como parametro un callback
fun MainMenu( onLoginSuccess: ()->Unit  )
{
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text= "Appify",
            fontSize = 36.sp)
        Spacer( modifier = Modifier.height(16.dp))
        Image(
            modifier = Modifier.size(300.dp),
            painter = painterResource(R.drawable.computer_greenback_man),
            contentDescription = "application logo"
        )
        Text(
            text= "Una app de pruebas",
            fontSize = 16.sp)


        var showLogin by remember{ mutableStateOf(false) }
        AnimatedVisibility(
            visible = !showLogin

        ) {

            Button(
                onClick = {
                    showLogin =  !showLogin
                }
            ){
                Text("Continuar")
            }
        }
        AnimatedVisibility(
            visible = showLogin

        ) {
            Login( onLoginSuccess )
        }




    }


}

@Composable
fun Login(   onLoginSuccess: ()->Unit   )
{
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxWidth(0.9f)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    )
    {
        val usernameState = rememberTextFieldState(initialText = "")
        TextField(
            state = usernameState,
            label = {
                Text("Nombre de usuario")
            }
        )
        val passState = rememberTextFieldState(initialText = "")
        OutlinedSecureTextField(
            state = passState,
            label = { Text("password") }
        )

        ///botonazo de login
        Button (
            onClick = {
                onLoginSuccess()
                //onLoginSucces( respuesta del servidor porcesada)
            }
        )
        {
            Text("Log in!")
        }
    }

}

@Composable
fun Home()
{
    Column(
        modifier=Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            fontSize = 28.sp,
            text = "menu de home"
        )
        Spacer( modifier = Modifier.size(20.dp))
        Text(
            fontSize = 14.sp,
            text = "aqui van a ir cositas"
        )
    }

    Text(
        fontSize = 28.sp,
        text = "menu de home"
    )
}


@Preview(showBackground = true)
@Composable
fun MainPreview() {
    Practica2Theme {
        MainMenu( {}  )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview()
{
    Login( {})
}