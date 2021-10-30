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
package com.squadio.jetpackcomposetask.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.squadio.jetpackcomposetask.ui.navigation.Screens.Companion.MOVIE_ID
import com.squadio.jetpackcomposetask.ui.screens.MoviesList


@Composable
fun NavigationGraph(startDestination: String) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {

        with(navController) {
            composable(
                route = Screens.MoviesList.route,
                content = {
                    MoviesList(
                        onMovieItemClicked = { movieId ->
                            navigate(Screens.MovieDetails(movieId).route)
                        }
                    )
                }
            )

            composable(
                route = Screens.MovieDetails().route,
                content = {
//                        MovieDetails(
//                            movieId = it.arguments?.getString(MOVIE_ID),
//                            onBackButtonClick = { navController.popBackStack() }
//                        )
                    }
            )
        }
    }
}
