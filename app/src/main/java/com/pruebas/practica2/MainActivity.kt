package com.pruebas.practica2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.google.gson.Gson
import kotlinx.coroutines.launch

import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.converter.gson.GsonConverterFactory
import java.security.MessageDigest

//Envoltura o wrapper para el digest sha256
fun sha256(text:String): String
{
    val bytes= MessageDigest.getInstance("SHA-256").digest(text.toByteArray())
    return bytes.joinToString(separator = "") { "%02X".format(it.toInt() and 0xFF) }
}


data class DatoPerturbador(
    val id: Int,
    val text: String,
    val source:String?
    )

data class LoginResponse(
    val username:String,
    val id: Int,
    val email:String,
    val firstname:String,
    val lastname:String,
    val pfp_url: String,
    val credits : Int,
    val xp: Int,
    val error: String?
)


val colores = listOf(
    Color(0xFF5D3140),
    Color(0xFFCF4173),
    Color(0xFFF39399),
    Color(0xFFF6D8BD)
)

interface ApiService {
    @GET("datoperturbador")
    suspend fun getDatoPerturbador(): DatoPerturbador
    
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field (value = "user") user: String,
        @Field (value = "pass") pass: String,
        @Field (value = "token") token: String,
    ): LoginResponse
}

//instancia de retrofir que prepara y hace las peticiones
object RetrofitInstance{
    val api: ApiService by lazy{
        Retrofit.Builder()
            .baseUrl("https://monsterballgo.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

}

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
    var userData by remember { mutableStateOf<LoginResponse?>(value = null) }
    NavHost(
        navController = navController,
        startDestination = "mainmenu",
        modifier = Modifier
            .fillMaxSize()
            .background(colores[3])
    )
    {
        composable("mainmenu")
        {
            MainMenu { ud ->
                userData = ud
                navController.navigate(route = "home")
            }
        }
        composable("home")
        {
            val user=userData
            if (user ==null){
                //o se puede llegar a home sin userDATA
                LaunchedEffect(key1 = Unit){
                     navController.navigate(route="mainmenu")
                }
            }else{

                Home (
                    userData = user,
                    onLogout = {
                        userData = null
                        navController.navigate(route = "mainmenu")
                    },
                    onCreditos = {
                        navController.navigate(route = "creditos")
                    } ,
                    onPerfil = {
                        navController.navigate(route = "perfil")
                    }
                )
            }

        }

        composable("creditos")
        {
            val user=userData
            if (user ==null){
                LaunchedEffect(key1 = Unit){
                     navController.navigate(route="mainmenu")
                }
            }else{
                Creditos(
                    userData = user,
                    onHome = {
                        navController.navigate(route = "home")
                    },
                    onPerfil = {
                        navController.navigate(route = "perfil")
                    },
                    onLogout = {
                        userData = null
                        navController.navigate(route = "mainmenu")
                    }
                )
            }
        }

        composable("perfil")
        {
            val user=userData
            if (user ==null){
                LaunchedEffect(key1 = Unit){
                    navController.navigate(route="mainmenu")
                }
            }else{
                Perfil(
                    userData = user,
                    onHome = {
                        navController.navigate(route = "home")
                    },
                    onCreditos = {
                        navController.navigate(route = "creditos")
                    },
                    onLogout = {
                        userData = null
                        navController.navigate(route = "mainmenu")
                    }
                )
            }
        }

    }

}

@Composable
//recibe como parametro un callback
fun MainMenu( onLoginSuccess: (LoginResponse)->Unit  )
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
fun Login(   onLoginSuccess: (LoginResponse)->Unit   )
{
    val scope = rememberCoroutineScope()
    var textoerror by remember {mutableStateOf(value = "")}
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


        var  buttonEnabledState by remember {mutableStateOf(value= true)}
        ///botonazo de login
        Button (
            enabled = buttonEnabledState,
            onClick = {
                buttonEnabledState = false
                scope.launch{
                    try{
                        val userData= RetrofitInstance.api.login(
                            user=usernameState.text.toString(),
                            pass= sha256(text = passState.text.toString()),
                            token = "code37"
                        )
                        if (userData.error!=null){
                            Log.d("Myapp","error: ${userData.error}")
                            buttonEnabledState = true
                            textoerror="Usuario o contraseña incorrectos"
                        } else {
                            textoerror=""
                            Log.d("Myapp","usuario logueado: ${userData.username}")
                            onLoginSuccess(userData)
                        }


                    } catch(error: Exception){
                        Log.d("Myapp","Excepción en login: ${error.message}")
                        buttonEnabledState = true
                        textoerror="Error al iniciar sesión, intentelo más tarde"
                    }
                }
                //onLoginSucces( respuesta del servidor porcesada)
            }
        )
        {
            Text("Log in!")
        }
        Text(
            fontSize = 26.sp,
            text = textoerror,
            color = Color.Red
        )
    }

}
@Composable
fun BarraTOP(
    onLogout: () -> Unit
)
{
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ){


        Button (
            colors = ButtonDefaults.buttonColors(containerColor = colores[1]),
            onClick = { onLogout() }
        )
        {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text("Cerrar sesión")
        }
    }
}


@Composable
fun BarraNavegacion(
    pantallaActual: String,
    onHome: () -> Unit,
    onCreditos: () -> Unit,
    onPerfil: () -> Unit,
)
{
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Button (
            colors = ButtonDefaults.buttonColors(containerColor = colores[2]),
            enabled = pantallaActual != "home",
            onClick = { onHome() }
        )
        {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text("Home")
        }

        Button (
            colors = ButtonDefaults.buttonColors(containerColor = colores[2]),
            enabled = pantallaActual != "creditos",
            onClick = { onCreditos() }
        )
        {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text("Créditos")
        }

        Button (
            colors = ButtonDefaults.buttonColors(containerColor = colores[2]),
            enabled = pantallaActual != "perfil",
            onClick = { onPerfil() }
        )
        {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text("Perfil")
        }

    }
}

@Composable
fun Home(
    userData: LoginResponse,
    onLogout: () -> Unit,
    onCreditos: () -> Unit,
    onPerfil: () -> Unit
)
{

    val scope=rememberCoroutineScope ()
    var textoDato by remember {mutableStateOf(value = "obteniendo dato...")}

    var cargandoDato by remember { mutableStateOf(value = true) }

    LaunchedEffect (key1 = Unit)
    {
        try{
            val res = RetrofitInstance.api.getDatoPerturbador()
            textoDato= res.text
        }catch(e: Exception){
            Log.d("MyApp", "error: ${e.message}")
        } finally {
            cargandoDato = false
        }
    }

    BarraTOP(
        onLogout = onLogout
    )



    Column(
        modifier=Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            fontSize = 28.sp,
            text = "Te damos la bienvenida,${userData.username}"
        )
        Spacer( modifier = Modifier.size(20.dp))

        AsyncImage(
            modifier = Modifier.size(300.dp),
            model= userData.pfp_url,
            //model = "https://cataas.com/cat?type=square",
            contentDescription="un gato",
            placeholder = painterResource(R.drawable.reloj_de_arena),
            error = painterResource(R.drawable.advertencia)
        )


        Text(
            fontSize = 14.sp,
            text = textoDato
        )

        BarraNavegacion(
            pantallaActual = "home",
            onHome = {},
            onCreditos = onCreditos,
            onPerfil = onPerfil
        )


    }


}

@Composable
fun Creditos(
    userData: LoginResponse,
    onHome: () -> Unit,
    onPerfil: () -> Unit,
    onLogout: () -> Unit
)
{
    val scope=rememberCoroutineScope ()

    BarraTOP(
        onLogout = onLogout
    )




    Column(
        modifier=Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            fontSize = 28.sp,
            text = "Te damos la bienvenida,${userData.username}"
        )
        Spacer( modifier = Modifier.size(20.dp))

        Image(
            modifier = Modifier.size(300.dp),
            painter = painterResource(R.drawable.advertencia),
            contentDescription = "un gato"
        )

        BarraNavegacion(
            pantallaActual = "creditos",
            onHome = onHome,
            onCreditos = {},
            onPerfil = onPerfil,
        )




    }


}

@Composable
fun Perfil(
    userData: LoginResponse,
    onHome: () -> Unit,
    onCreditos: () -> Unit,
    onLogout: () -> Unit
)
{
    val scope=rememberCoroutineScope ()

    BarraTOP(
        onLogout = onLogout
    )




    Column(
        modifier=Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            fontSize = 28.sp,
            text = "Te damos la bienvenida,${userData.username}"
        )
        Spacer( modifier = Modifier.size(20.dp))

        Image(
            modifier = Modifier.size(300.dp),
            painter = painterResource(R.drawable.advertencia),
            contentDescription = "un gato"
        )

        BarraNavegacion(
            pantallaActual = "perfil",
            onHome = onHome,
            onCreditos = onCreditos,
            onPerfil = {},
        )




    }


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

@Preview(showBackground = true)
@Composable
fun HomePreview()
{
    Home(
        userData = LoginResponse(
            username="",
            id =1,
            email="usa@gmail.com",
            firstname =  "",
            lastname = "",
            pfp_url = "urlsisisi",
            credits=100,
            xp=999,
            error=null
        ),
        onLogout = {},
        onCreditos = {},
        onPerfil = {}
    )
}

@Preview(showBackground = true)
@Composable
fun CreditosPreview()
{
    Creditos(
        userData = LoginResponse(
            username="",
            id =1,
            email="usa@gmail.com",
            firstname =  "",
            lastname = "",
            pfp_url = "urlsisisi",
            credits=100,
            xp=999,
            error=null
        ),
        onHome = {},
        onPerfil = {},
        onLogout = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PerfilPreview()
{
    Perfil(
        userData = LoginResponse(
            username="",
            id =1,
            email="usa@gmail.com",
            firstname =  "",
            lastname = "",
            pfp_url = "urlsisisi",
            credits=100,
            xp=999,
            error=null
        ),
        onHome = {},
        onCreditos = {},
        onLogout = {}
    )
}