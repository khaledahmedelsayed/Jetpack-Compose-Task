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

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.squadio.jetpackcomposetask.R
import com.squadio.jetpackcomposetask.data.Movie
import com.squadio.jetpackcomposetask.ui.components.MovieItem
import com.squadio.jetpackcomposetask.ui.components.PullToRefresh


@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun MoviesList(list : LazyPagingItems<Movie>, onMovieItemClicked: (movieId: String) -> Unit) {
    var displayLoadingOverlay by remember{ mutableStateOf(false) }
    var displayRefreshingIndicator by remember{ mutableStateOf(false) }
    PullToRefresh(
        isRefreshing = displayRefreshingIndicator,
        onRefresh = { list.refresh() },
        content = {
            Column(Modifier.fillMaxHeight()) {
                Text(
                    text = stringResource(R.string.movies_list_title),
                    style = MaterialTheme.typography.h5,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .background(MaterialTheme.colors.background)
                )
                AnimatedVisibility(visible = displayLoadingOverlay, enter = fadeIn(), exit = fadeOut()) {
                    Box(Modifier.fillMaxSize()) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                }
                LazyColumn(
                    modifier = Modifier.wrapContentHeight(),
                    content = {
                        items(list) { movie ->
                            MovieItem(
                                movie = movie ?: Movie(),
                                onMovieItemClicked = { onMovieItemClicked(it) })
                            list.apply {
                                when {
                                    loadState.refresh is LoadState.Loading -> displayLoadingOverlay = true
                                    loadState.append is LoadState.Loading -> displayRefreshingIndicator = true
                                    else -> {
                                        displayLoadingOverlay = false
                                        displayRefreshingIndicator = false
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    )
}
