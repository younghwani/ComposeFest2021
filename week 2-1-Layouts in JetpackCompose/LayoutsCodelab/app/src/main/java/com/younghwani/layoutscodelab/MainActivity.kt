package com.younghwani.layoutscodelab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import coil.compose.rememberImagePainter
import com.younghwani.layoutscodelab.ui.theme.LayoutsCodelabTheme
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LayoutsCodelabTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    BodyContent()
                }
            }
        }
    }
}

val topics = listOf(
    "Arts & Crafts", "Beauty", "Books", "Business", "Comics", "Culinary",
    "Design", "Fashion", "Film", "History", "Maths", "Music", "People", "Philosophy",
    "Religion", "Social sciences", "Technology", "TV", "Writing"
)

@Stable
fun Modifier.padding(all: Dp) =
    this.then(PaddingModifier(start = all, top = all, end = all, bottom = all, rtlAware = true))

private class PaddingModifier(
    val start: Dp = 0.dp,
    val top: Dp = 0.dp,
    val end: Dp = 0.dp,
    val bottom: Dp = 0.dp,
    val rtlAware: Boolean
) : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val horizontal = start.roundToPx() + end.roundToPx()
        val vertical = top.roundToPx() + bottom.roundToPx()

        val placeable = measurable.measure(constraints.offset(-horizontal, -vertical))

        val width = constraints.constrainWidth(placeable.width + horizontal)
        val height = constraints.constrainHeight(placeable.height + vertical)

        return layout(width, height) {
            if (rtlAware) {
                placeable.placeRelative(start.roundToPx(), top.roundToPx())
            } else {
                placeable.place(start.roundToPx(), top.roundToPx())
            }
        }
    }
}

@Composable
fun Chip(modifier: Modifier = Modifier, text: String) {
    Card(
        modifier = modifier,
        border = BorderStroke(color = Color.Black, width = Dp.Hairline),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp, 16.dp)
                    .background(color = MaterialTheme.colors.secondary)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text= text)
        }
    }
}

@Composable
fun StaggeredGrid(
    modifier: Modifier = Modifier,
    rows: Int = 3,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val rowWidths = IntArray(rows) { 0 }
        val rowHeights = IntArray(rows) { 0 }

        val placeables = measurables.mapIndexed { index, measurable ->
            val placeable = measurable.measure(constraints)

            val row = index % rows
            rowWidths[row] += placeable.width
            rowHeights[row] = Math.max(rowHeights[row], placeable.height)

            placeable
        }

        val width = rowWidths.maxOrNull()
            ?.coerceIn(constraints.minWidth.rangeTo(constraints.maxWidth)) ?: constraints.minWidth

        val height = rowHeights.sumOf { it }
            .coerceIn(constraints.minHeight.rangeTo(constraints.maxHeight))

        val rowY = IntArray(rows) { 0 }
        for (i in 1 until rows) {
            rowY[i] = rowY[i-1] + rowHeights[i-1]
        }

        layout(width, height) {
            val rowX = IntArray(rows) { 0 }

            placeables.forEachIndexed { index, placeable ->
                val row = index % rows
                placeable.placeRelative(
                    x = rowX[row],
                    y = rowY[row]
                )
                rowX[row] += placeable.width
            }
        }
    }
}

@Composable
fun BodyContent(modifier: Modifier = Modifier) {
    Row(modifier = modifier
        .background(color = Color.LightGray, shape = RectangleShape)
        .padding(16.dp)
        .size(200.dp)
        .horizontalScroll(rememberScrollState())
    ) {
        StaggeredGrid {
            for (topic in topics) {
                Chip(modifier = Modifier.padding(8.dp), text = topic)
            }
        }
    }
}

@Composable
fun LayoutsCodelab() {
    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "LayoutsCodelab")
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Filled.Favorite, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        BodyContent(
            Modifier
                .padding(innerPadding)
                .padding(8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LayoutsCodelabTheme {
        LayoutsCodelab()
    }
}