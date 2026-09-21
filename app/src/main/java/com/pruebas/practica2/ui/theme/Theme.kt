package com.pruebas.practica2.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.pruebas.practica2.colores

private val EsquemaApp = lightColorScheme(
    primary = colores[1],
    onPrimary = colores[3],
    background = colores[3],
    onBackground = colores[0],
    surface = colores[3],
    onSurface = colores[0],
    outline = colores[1]
)

@Composable
fun Practica2Theme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EsquemaApp,
        typography = Typography,
        content = content
    )
}