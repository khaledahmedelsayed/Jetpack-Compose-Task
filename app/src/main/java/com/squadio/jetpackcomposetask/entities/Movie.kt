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
package com.squadio.jetpackcomposetask.entities

import com.google.gson.annotations.SerializedName


data class Movie    (
    val id: String? = null,
    val title: String? = null,
    @SerializedName("release_date") val releaseDate: String? = null,
    val overview : String? = null,
    @SerializedName("vote_average") val rating : Float? = null,
    @SerializedName("poster_path") val imagePath : String? = null
){
    val imageUrl get() = "https://image.tmdb.org/t/p/w500/$imagePath"
}

data class TheMovieDBResponse(
    val results : List<Movie>
)

