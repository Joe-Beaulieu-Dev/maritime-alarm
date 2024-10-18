package com.example.alarmscratch.settings.ui.alarmdefaults.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.BoatSails

@Composable
fun SnoozeDurationPager(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        // There's no native way to do a looping Pager, so we have to create the illusion of one.
        // To do that, we have a small list of unique pages which is repeated multiple times.
        // The User is then placed in the middle of this repeated meta-list, giving the illusion
        // of a loop as long as they don't scroll to the beginning or end of the meta-list.
        val uniquePages = listOf(5, 10, 15, 20, 25, 30)
        val uniquePageCount = uniquePages.size
        val extendedPageCount = uniquePageCount * 100
        val pagerState = rememberPagerState(
            initialPage = extendedPageCount / 2,
            pageCount = { extendedPageCount }
        )
        val getUniquePageIndex: (Int) -> Int = { extendedPageIndex -> extendedPageIndex % uniquePageCount }

        // Pager sizing
        val pageWidth = 50.dp
        val pageHeight = 50.dp
        val verticalPagerHeight = pageHeight * 3
        val dividerVerticalOffset = pageHeight / 2

        // Fling
        val flingBehavior = PagerDefaults.flingBehavior(
            state = pagerState,
            pagerSnapDistance = PagerSnapDistance.atMost(10)
        )

        // Top Bar
        HorizontalDivider(
            color = BoatSails,
            modifier = Modifier
                .width(pageWidth)
                .offset(y = -(dividerVerticalOffset))
        )

        // Snooze Duration Options
        VerticalPager(
            state = pagerState,
            contentPadding = PaddingValues(vertical = pageHeight),
            pageSize = PageSize.Fixed(pageHeight),
            flingBehavior = flingBehavior,
            modifier = Modifier.height(verticalPagerHeight)
        ) { extendedPageIndex ->
            // Snooze Duration
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(pageWidth)
                    .height(pageHeight)
            ) {
                Text(text = "${uniquePages[getUniquePageIndex(extendedPageIndex)]}")
            }
        }

        // Bottom Bar
        HorizontalDivider(
            color = BoatSails,
            modifier = Modifier
                .width(pageWidth)
                .offset(y = dividerVerticalOffset)
        )
    }
}

/*
 * Previews
 */

@Preview
@Composable
private fun SnoozeDurationPagerPreview() {
    AlarmScratchTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                SnoozeDurationPager()
            }
        }
    }
}
