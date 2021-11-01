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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.CombinedModifier
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp

private const val MAX_OFFSET = 400f
private const val MIN_REFRESH_OFFSET = 250f
private const val PERCENT_INDICATOR_PROGRESS_ON_DRAG = 0.85f
private const val BASE_OFFSET = -48

/**
 * A layout composable with [content].
 *
 * Example usage:
 * @sample com.puculek.pulltorefresh.samples.PullToRefreshPreview
 *
 * @param modifier The modifier to be applied to the layout.
 * @param maxOffset How many pixels can the progress indicator can be dragged down
 * @param minRefreshOffset Minimum drag value to trigger [onRefresh]
 * @param isRefreshing Flag describing if [PullToRefresh] is refreshing.
 * @param onRefresh Callback to be called if layout is pulled to refresh.
 * @param content The content of the [PullToRefresh].
 */
@Composable
fun PullToRefresh(
    modifier: Modifier = Modifier,
    maxOffset: Float = MAX_OFFSET,
    minRefreshOffset: Float = MIN_REFRESH_OFFSET,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    var indicatorOffset by remember { mutableStateOf(0f) }

    // check if isRefreshing has been changed
    var isRefreshingInternal by remember { mutableStateOf(false) }

    // User cancelled dragging before reaching MAX_REFRESH_OFFSET
    var isResettingScroll by remember { mutableStateOf(false) }

    // How much should indicator scroll to reset its position
    var scrollToReset by remember { mutableStateOf(0f) }

    // Trigger for scaling animation
    var isFinishingRefresh by remember { mutableStateOf(false) }

    val scaleAnimation by animateFloatAsState(
        targetValue = if (isFinishingRefresh) 0f else 1f,
        finishedListener = {
            indicatorOffset = 0f
            isFinishingRefresh = false
        }
    )
    val offsetAnimation by animateFloatAsState(
        targetValue = if (isRefreshing || isFinishingRefresh) {
            indicatorOffset - minRefreshOffset
        } else {
            0f
        }
    )
    val resettingScrollOffsetAnimation by animateFloatAsState(
        targetValue = if (isResettingScroll) {
            scrollToReset
        } else {
            0f
        },
        finishedListener = {
            if (isResettingScroll) {
                indicatorOffset = 0f
                isResettingScroll = false
            }
        }
    )

    if (isResettingScroll) {
        indicatorOffset -= resettingScrollOffsetAnimation
    }
    if (!isRefreshing && isRefreshingInternal) {
        isFinishingRefresh = true
        isRefreshingInternal = false
    }
    if (isRefreshing && !isRefreshingInternal) {
        isRefreshingInternal = true
    }

    val nestedScrollConnection = object : NestedScrollConnection {

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            if (!isRefreshing && source == NestedScrollSource.Drag) {
                val diff = if (indicatorOffset + available.y > maxOffset) {
                    available.y - (indicatorOffset + available.y - maxOffset)
                } else if (indicatorOffset + available.y < 0) {
                    0f
                } else {
                    available.y
                }
                indicatorOffset += diff
                return Offset(0f, diff)
            }
            return super.onPostScroll(consumed, available, source)
        }

        override fun onPreScroll(
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            if (!isRefreshing && source == NestedScrollSource.Drag) {
                if (available.y < 0 && indicatorOffset > 0) {
                    val diff = if (indicatorOffset + available.y < 0) {
                        indicatorOffset = 0f
                        indicatorOffset
                    } else {
                        indicatorOffset += available.y
                        available.y
                    }
                    isFinishingRefresh = false
                    return Offset.Zero.copy(y = diff)
                }
            }
            return super.onPreScroll(available, source)
        }

        override suspend fun onPostFling(
            consumed: Velocity,
            available: Velocity
        ): Velocity {
            if (!isRefreshing) {
                if (indicatorOffset > minRefreshOffset) {
                    onRefresh()
                    isRefreshingInternal = true
                } else {
                    isResettingScroll = true
                    scrollToReset = indicatorOffset
                }
            }
            return super.onPostFling(consumed, available)
        }
    }

    Box(
        modifier = CombinedModifier(
            inner = Modifier.nestedScroll(nestedScrollConnection),
            outer = modifier
        )
    ) {
        content()

        val offsetPx = if (isRefreshing || isFinishingRefresh) {
            offsetAnimation
        } else {
            0f
        }
        val absoluteOffset = BASE_OFFSET.dp + with(LocalDensity.current) {
            (indicatorOffset - offsetPx).toDp()
        }

        PullToRefreshProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .absoluteOffset(y = absoluteOffset)
                .scale(scaleAnimation)
                .rotate(if (indicatorOffset > minRefreshOffset) (indicatorOffset - minRefreshOffset) else 0f),
            progress = if (!isRefreshing) indicatorOffset / maxOffset * PERCENT_INDICATOR_PROGRESS_ON_DRAG else null
        )
    }
}

@Composable
internal fun PullToRefreshProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float? = null
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.padding(4.dp),
            shape = CircleShape,
            elevation = 6.dp
        ) {
            if (progress == null) {
                CircularProgressIndicator(
                    modifier = Modifier.scale(.66f),
                    strokeWidth = ProgressIndicatorDefaults.StrokeWidth * 1.25f,
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.scale(.66f),
                    strokeWidth = ProgressIndicatorDefaults.StrokeWidth * 1.25f,
                    progress = progress
                )
            }
        }
    }
}
