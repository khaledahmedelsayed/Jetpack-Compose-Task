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
package com.squadio.jetpackcomposetask.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.squadio.jetpackcomposetask.R
import coil.compose.rememberImagePainter
import com.gowtham.ratingbar.RatingBar
import com.squadio.jetpackcomposetask.data.Movie

@Composable
fun MovieItem(movie: Movie, onMovieItemClicked: (movieId: String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = { onMovieItemClicked(movie.id ?: "") }),
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.onPrimary
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

                Image(
                    modifier = Modifier
                        .size(120.dp, 120.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    painter = rememberImagePainter(
                        data = movie.imageUrl,
                        builder = { placeholder(R.drawable.ic_launcher_background) }
                    ),
                    alignment = Alignment.CenterStart,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(0.85F)
            ) {
                Text(
                    text = movie.title ?: "",
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    fontWeight = FontWeight.Bold,
                    style = typography.subtitle1
                )

                Text(
                    text = movie.releaseDate?.take(4) ?: "",
                    modifier = Modifier.padding(end = 12.dp),
                    style = typography.caption
                )

                Text(
                    text = movie.overview ?: "",
                    modifier = Modifier.padding(end = 12.dp),
                    style = typography.caption,
                    maxLines = 2
                )
            }

            Row(
                modifier = Modifier.weight(0.25F),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = movie.rating.toString(),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 4.dp),
                    style = typography.button
                )
                RatingBar(
                    value = movie.rating ?: 0F,
                    numStars = 1,
                    isIndicator = true,
                    onRatingChanged = {},
                    onValueChange = {}
                )
            }
        }
    }
}

@Preview
@Composable
fun MovieItemPreview() {
    MovieItem(
        movie = Movie(
            title = "Title",
            releaseDate = "2021",
            overview = "This is an overview, This is an overview, This is an overview",
            rating = 4.7F
        ), onMovieItemClicked = { })
}
