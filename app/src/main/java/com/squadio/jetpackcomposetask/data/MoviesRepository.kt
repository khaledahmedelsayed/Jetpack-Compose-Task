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


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.squadio.jetpackcomposetask.entities.Movie
import com.squadio.jetpackcomposetask.entities.TheMovieDBResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import okhttp3.OkHttpClient

import okhttp3.logging.HttpLoggingInterceptor


interface MoviesRepository {

    @GET(GET_MOVIES_LIST)
    suspend fun fetchMoviesList(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String = API_KEY
    ): Response<TheMovieDBResponse>

    @GET(GET_MOVIE_DETAILS)
    suspend fun fetchMovieDetails(
        @Path(MOVIE_ID_PATH) movieId: String?,
        @Query("api_key") apiKey: String = API_KEY
    ): Response<Movie>

    companion object {
        private const val BASE_URL = "https://api.themoviedb.org/3/"
        private const val API_KEY = "3e68c56cf7097768305e38273efd342c"

        private const val GET_MOVIES_LIST = "movie/now_playing"
        private const val MOVIE_ID_PATH = "id"
        private const val GET_MOVIE_DETAILS = "movie/{$MOVIE_ID_PATH}"

        private var moviesRepository: MoviesRepository? = null
        private fun getInstance(): MoviesRepository {
            if (moviesRepository == null) {
                moviesRepository = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(
                        OkHttpClient.Builder()
                            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
                            .build()
                    )
                    .build().create(MoviesRepository::class.java)
            }
            return moviesRepository!!
        }

        private val moviesListPager = Pager(PagingConfig(pageSize = 20)) {
            object : PagingSource<Int, Movie>() {

                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
                    return try {
                        val nextPage = params.key ?: 1
                        val movieListResponse = getInstance().fetchMoviesList(nextPage).body()

                        LoadResult.Page(
                            data = movieListResponse?.results?: emptyList(),
                            prevKey = if (nextPage == 1) null else nextPage - 1,
                            nextKey = movieListResponse?.page?.plus(1)
                        )
                    } catch (e: Exception) {
                        LoadResult.Error(e)
                    }
                }

                override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
                    return state.anchorPosition?.let { anchorPosition ->
                        state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                            ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
                    }
                }
            }
        }

        fun getMoviesPagerAsFlow() = moviesListPager.flow

        suspend fun getMovieDetails(id: String?) =
            getInstance().fetchMovieDetails(id).body() ?: Movie()
    }
}
