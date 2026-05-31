package com.muscu.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest

/**
 * Mapping exerciseId → nom du fichier SVG dans assets/exercises/
 * Images source : OpenTraining / Everkinetic (CC-BY-SA 3.0)
 */
fun getExerciseIllustrationPath(exerciseId: String): String? {
    return when (exerciseId) {
        "ex-bench-1"       -> "exercises/bench_press.svg"
        "ex-row-1"         -> "exercises/rowing_1_arm.svg"
        "ex-curl-1"        -> "exercises/curl_halteres.svg"
        "ex-tri-1"         -> "exercises/extension_triceps.svg"
        "ex-mil-1"         -> "exercises/developpe_militaire.svg"
        "ex-fen-1"         -> "exercises/fentes.svg"
        "ex-ele-1"         -> "exercises/elevations_laterales.svg"
        "ex-gain-1"        -> "exercises/gainage.svg"
        "ex-bench-serr"    -> "exercises/bench_serre.svg"
        "ex-row-2"         -> "exercises/rowing_2_bras.svg"
        "ex-curl-mart"     -> "exercises/curl_marteau.svg"
        "ex-tri-2"         -> "exercises/extension_triceps_corde.svg"
        else -> null
    }
}

@Composable
fun ExerciseIllustration(
    exerciseId: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val path = getExerciseIllustrationPath(exerciseId)
    if (path == null) return

    val context = LocalContext.current
    val request = ImageRequest.Builder(context)
        .data("file:///android_asset/$path")
        .decoderFactory(SvgDecoder.Factory())
        .crossfade(true)
        .build()

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        SubcomposeAsyncImage(
            model = request,
            contentDescription = null,
            modifier = Modifier.padding(8.dp),
            contentScale = contentScale,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            loading = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            },
            error = {
                // Silently fail — no placeholder needed
            }
        )
    }
}
