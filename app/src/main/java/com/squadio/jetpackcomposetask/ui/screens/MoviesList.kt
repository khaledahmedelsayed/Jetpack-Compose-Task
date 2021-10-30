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

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.squadio.jetpackcomposetask.R
import com.squadio.jetpackcomposetask.data.MoviesRepository
import com.squadio.jetpackcomposetask.entities.Movie
import com.squadio.jetpackcomposetask.ui.components.MovieItem
import com.squadio.jetpackcomposetask.utils.PullToRefresh
import kotlinx.coroutines.*


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MoviesList(onMovieItemClicked: (movieId: String) -> Unit) {
    var list by rememberSaveable { mutableStateOf(listOf<Movie>()) }
    var isLoading by remember{ mutableStateOf(true) }
    LaunchedEffect(null){
        fetchMoviesList {
        list = it
        isLoading = false
    }}
    PullToRefresh(
        isRefreshing = isLoading,
        onRefresh = {
            isLoading = true
            fetchMoviesList {
                list = it
                isLoading = false
            }
        },
        content = {
            LazyColumn(
                content = {
                    stickyHeader {
                        Text(
                            text = stringResource(R.string.movies_list_title),
                            style = MaterialTheme.typography.h5,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .background(MaterialTheme.colors.background)
                        )
                    }
                    items(list) { movie ->
                        MovieItem(movie = movie, onMovieItemClicked = { onMovieItemClicked(it) })
                    }
                }
            )
        }
    )
}

private fun fetchMoviesList(onMoviesListRetrieved: (List<Movie>) -> Unit) {
    val job = Job()
    val scope = CoroutineScope(Dispatchers.Main + job)
    scope.launch(context = Dispatchers.IO) {
        try {
            val moviesResponse = MoviesRepository.getInstance().fetchMoviesList()
            val moviesList = moviesResponse.body()!!.results
            withContext(Dispatchers.Main){
                onMoviesListRetrieved(moviesList)
                job.complete()
            }
        } catch (e: Exception) {
            Log.e("ApiError",e.message, e)
            job.cancel()
        }
    }

}

