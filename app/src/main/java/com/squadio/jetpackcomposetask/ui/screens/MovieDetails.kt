/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squadio.jetpackcomposetask.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.squadio.jetpackcomposetask.R
import com.squadio.jetpackcomposetask.data.Movie

@Composable
fun MovieDetails(movie: Movie, onBackButtonClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()) // Behaves like ScrollView
    ) {
        Row(Modifier.fillMaxWidth()){
            Image(
                modifier = Modifier
                    .size(200.dp, 300.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                ,
                painter = rememberImagePainter(
                    data = movie.imageUrl,
                    builder = { placeholder(R.drawable.ic_launcher_background) }
                ),
                alignment = Alignment.CenterStart,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            Column(Modifier.wrapContentHeight().padding(8.dp)) {
                Text("Score", color = Color.Gray, style = MaterialTheme.typography.h6)
                Text(movie.rating.toString(), style = MaterialTheme.typography.h6)
                Spacer(Modifier.height(40.dp))
                Text("Rating", color = Color.Gray, style = MaterialTheme.typography.h6)
                Text(movie.popularity.toString(), style = MaterialTheme.typography.h6)
                Spacer(Modifier.height(40.dp))
                Text("Release Date", color = Color.Gray, style = MaterialTheme.typography.h6)
                Text(movie.releaseDate ?: "", style = MaterialTheme.typography.h6)
            }

        }
        Text(
            text = movie.title ?: "",
            Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = movie.overview ?: "",
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            style = MaterialTheme.typography.body1
        )
        Button(
            modifier = Modifier
                .padding(20.dp)
                .clip(RoundedCornerShape(8.dp))
                .align(Alignment.CenterHorizontally),
            onClick = { onBackButtonClick() }
        ) {
            Text(text = "Go back")
        }
    }
}

@Preview
@Composable
fun MovieDetailsPreview() {
    MovieDetails(
        movie = Movie(
            title = "Movie Title",
            releaseDate = "2021-10-15",
            overview = "This is an overview, This is an overview, This is an overview, This is an overview, This is an overview",
            rating = 4.7F,
            popularity = 155.661F
        ),
    onBackButtonClick = {})
}
