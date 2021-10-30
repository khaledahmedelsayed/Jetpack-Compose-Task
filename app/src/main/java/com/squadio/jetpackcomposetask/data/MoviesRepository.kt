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
package com.squadio.jetpackcomposetask.data


import com.squadio.jetpackcomposetask.entities.TheMovieDBResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface MoviesRepository {
    @GET("movie/now_playing")
    suspend fun fetchMoviesList(
        @Query("api_key") apiKey: String = "3e68c56cf7097768305e38273efd342c"
    ): Response<TheMovieDBResponse>
    companion object {
        var moviesRepository: MoviesRepository? = null
        fun getInstance() : MoviesRepository {
            if (moviesRepository == null) {
                moviesRepository = Retrofit.Builder()
                    .baseUrl("https://api.themoviedb.org/3/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor()).build())
                    .build().create(MoviesRepository::class.java)
            }
            return moviesRepository!!
        }
    }
}
