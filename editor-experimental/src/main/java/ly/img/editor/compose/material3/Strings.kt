/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ly.img.editor.compose.material3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.R
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

@Immutable
@JvmInline
internal value class Strings private constructor(
    @Suppress("unused") private val value: Int = nextId(),
) {
    companion object {
        private var id = 0

        private fun nextId() = id++

        val NavigationMenu = Strings()
        val CloseDrawer = Strings()
        val CloseSheet = Strings()
        val DefaultErrorMessage = Strings()
        val ExposedDropdownMenu = Strings()
        val SliderRangeStart = Strings()
        val SliderRangeEnd = Strings()
        val Dialog = Strings()
        val MenuExpanded = Strings()
        val MenuCollapsed = Strings()
        val SnackbarDismiss = Strings()
        val SearchBarSearch = Strings()
        val SuggestionsAvailable = Strings()
        val DatePickerTitle = Strings()
        val DatePickerHeadline = Strings()
        val DatePickerYearPickerPaneTitle = Strings()
        val DatePickerSwitchToYearSelection = Strings()
        val DatePickerSwitchToDaySelection = Strings()
        val DatePickerSwitchToNextMonth = Strings()
        val DatePickerSwitchToPreviousMonth = Strings()
        val DatePickerNavigateToYearDescription = Strings()
        val DatePickerHeadlineDescription = Strings()
        val DatePickerNoSelectionDescription = Strings()
        val DatePickerTodayDescription = Strings()
        val DatePickerScrollToShowLaterYears = Strings()
        val DatePickerScrollToShowEarlierYears = Strings()
        val DateInputTitle = Strings()
        val DateInputHeadline = Strings()
        val DateInputLabel = Strings()
        val DateInputHeadlineDescription = Strings()
        val DateInputNoInputDescription = Strings()
        val DateInputInvalidNotAllowed = Strings()
        val DateInputInvalidForPattern = Strings()
        val DateInputInvalidYearRange = Strings()
        val DatePickerSwitchToCalendarMode = Strings()
        val DatePickerSwitchToInputMode = Strings()
        val DateRangePickerTitle = Strings()
        val DateRangePickerStartHeadline = Strings()
        val DateRangePickerEndHeadline = Strings()
        val DateRangePickerScrollToShowNextMonth = Strings()
        val DateRangePickerScrollToShowPreviousMonth = Strings()
        val DateRangePickerDayInRange = Strings()
        val DateRangeInputTitle = Strings()
        val DateRangeInputInvalidRangeInput = Strings()
        val BottomSheetDragHandleDescription = Strings()
        val BottomSheetPartialExpandDescription = Strings()
        val BottomSheetDismissDescription = Strings()
        val BottomSheetExpandDescription = Strings()
        val TooltipLongPressLabel = Strings()
        val TimePickerAM = Strings()
        val TimePickerPM = Strings()
        val TimePickerPeriodToggle = Strings()
        val TimePickerHourSelection = Strings()
        val TimePickerMinuteSelection = Strings()
        val TimePickerHourSuffix = Strings()
        val TimePicker24HourSuffix = Strings()
        val TimePickerMinuteSuffix = Strings()
        val TimePickerHour = Strings()
        val TimePickerMinute = Strings()
        val TimePickerHourTextField = Strings()
        val TimePickerMinuteTextField = Strings()
        val TooltipPaneDescription = Strings()
    }
}

@Composable
@ReadOnlyComposable
internal fun getString(string: Strings): String {
    LocalConfiguration.current
    val resources = LocalContext.current.resources
    return when (string) {
        Strings.NavigationMenu -> resources.getString(R.string.navigation_menu)
        Strings.CloseDrawer -> resources.getString(R.string.close_drawer)
        Strings.CloseSheet -> resources.getString(R.string.close_sheet)
        Strings.DefaultErrorMessage -> resources.getString(R.string.default_error_message)
        Strings.ExposedDropdownMenu -> resources.getString(R.string.dropdown_menu)
        Strings.SliderRangeStart -> resources.getString(R.string.range_start)
        Strings.SliderRangeEnd -> resources.getString(R.string.range_end)
        Strings.Dialog -> resources.getString(androidx.compose.material3.R.string.dialog)
        Strings.MenuExpanded -> resources.getString(androidx.compose.material3.R.string.expanded)
        Strings.MenuCollapsed -> resources.getString(androidx.compose.material3.R.string.collapsed)
        Strings.SnackbarDismiss ->
            resources.getString(
                androidx.compose.material3.R.string.snackbar_dismiss,
            )

        Strings.SearchBarSearch ->
            resources.getString(
                androidx.compose.material3.R.string.search_bar_search,
            )

        Strings.SuggestionsAvailable ->
            resources.getString(androidx.compose.material3.R.string.suggestions_available)

        Strings.DatePickerTitle ->
            resources.getString(
                androidx.compose.material3.R.string.date_picker_title,
            )

        Strings.DatePickerHeadline ->
            resources.getString(
                androidx.compose.material3.R.string.date_picker_headline,
            )

        Strings.DatePickerYearPickerPaneTitle ->
            resources.getString(
                androidx.compose.material3.R.string.date_picker_year_picker_pane_title,
            )

        Strings.DatePickerSwitchToYearSelection ->
            resources.getString(
                androidx.compose.material3.R.string.date_picker_switch_to_year_selection,
            )

        Strings.DatePickerSwitchToDaySelection ->
            resources.getString(
                androidx.compose.material3.R.string.date_picker_switch_to_day_selection,
            )

        Strings.DatePickerSwitchToNextMonth ->
            resources.getString(
                androidx.compose.material3.R.string.date_picker_switch_to_next_month,
            )

        Strings.DatePickerSwitchToPreviousMonth ->
            resources.getString(
                androidx.compose.material3.R.string.date_picker_switch_to_previous_month,
            )

        Strings.DatePickerNavigateToYearDescription ->
            resources.getString(
                androidx.compose.material3.R.string.date_picker_navigate_to_year_description,
            )

        Strings.DatePickerHeadlineDescription ->
            resources.getString(
                androidx.compose.material3.R.string.date_picker_headline_description,
            )

        Strings.DatePickerNoSelectionDescription ->
            resources.getString(
                androidx.compose.material3.R.string.date_picker_no_selection_description,
            )
        Strings.DatePickerTodayDescription ->
            resources.getString(
                androidx.compose.material3.R.string.date_picker_today_description,
            )
        Strings.DatePickerScrollToShowLaterYears ->
            resources.getString(
                androidx.compose.material3.R.string.date_picker_scroll_to_later_years,
            )
        Strings.DatePickerScrollToShowEarlierYears ->
            resources.getString(
                androidx.compose.material3.R.string.date_picker_scroll_to_earlier_years,
            )
        Strings.DateInputTitle ->
            resources.getString(
                androidx.compose.material3.R.string.date_input_title,
            )
        Strings.DateInputHeadline ->
            resources.getString(
                androidx.compose.material3.R.string.date_input_headline,
            )
        Strings.DateInputLabel ->
            resources.getString(
                androidx.compose.material3.R.string.date_input_label,
            )
        Strings.DateInputHeadlineDescription ->
            resources.getString(
                androidx.compose.material3.R.string.date_input_headline_description,
            )
        Strings.DateInputNoInputDescription ->
            resources.getString(
                androidx.compose.material3.R.string.date_input_no_input_description,
            )
        Strings.DateInputInvalidNotAllowed ->
            resources.getString(
                androidx.compose.material3.R.string.date_input_invalid_not_allowed,
            )
        Strings.DateInputInvalidForPattern ->
            resources.getString(
                androidx.compose.material3.R.string.date_input_invalid_for_pattern,
            )
        Strings.DateInputInvalidYearRange ->
            resources.getString(
                androidx.compose.material3.R.string.date_input_invalid_year_range,
            )
        Strings.DatePickerSwitchToCalendarMode ->
            resources.getString(
                androidx.compose.material3.R.string.date_picker_switch_to_calendar_mode,
            )
        Strings.DatePickerSwitchToInputMode ->
            resources.getString(
                androidx.compose.material3.R.string.date_picker_switch_to_input_mode,
            )
        Strings.DateRangePickerTitle ->
            resources.getString(
                androidx.compose.material3.R.string.date_range_picker_title,
            )
        Strings.DateRangePickerStartHeadline ->
            resources.getString(
                androidx.compose.material3.R.string.date_range_picker_start_headline,
            )
        Strings.DateRangePickerEndHeadline ->
            resources.getString(
                androidx.compose.material3.R.string.date_range_picker_end_headline,
            )
        Strings.DateRangePickerScrollToShowNextMonth ->
            resources.getString(
                androidx.compose.material3.R.string.date_range_picker_scroll_to_next_month,
            )
        Strings.DateRangePickerScrollToShowPreviousMonth ->
            resources.getString(
                androidx.compose.material3.R.string.date_range_picker_scroll_to_previous_month,
            )
        Strings.DateRangePickerDayInRange ->
            resources.getString(
                androidx.compose.material3.R.string.date_range_picker_day_in_range,
            )
        Strings.DateRangeInputTitle ->
            resources.getString(
                androidx.compose.material3.R.string.date_range_input_title,
            )
        Strings.DateRangeInputInvalidRangeInput ->
            resources.getString(
                androidx.compose.material3.R.string.date_range_input_invalid_range_input,
            )
        Strings.BottomSheetDragHandleDescription ->
            resources.getString(
                androidx.compose.material3.R.string.bottom_sheet_drag_handle_description,
            )
        Strings.BottomSheetPartialExpandDescription ->
            resources.getString(
                androidx.compose.material3.R.string.bottom_sheet_collapse_description,
            )
        Strings.BottomSheetDismissDescription ->
            resources.getString(
                androidx.compose.material3.R.string.bottom_sheet_dismiss_description,
            )
        Strings.BottomSheetExpandDescription ->
            resources.getString(
                androidx.compose.material3.R.string.bottom_sheet_expand_description,
            )
        Strings.TooltipLongPressLabel ->
            resources.getString(
                androidx.compose.material3.R.string.tooltip_long_press_label,
            )
        Strings.TimePickerAM ->
            resources.getString(
                androidx.compose.material3.R.string.time_picker_am,
            )
        Strings.TimePickerPM ->
            resources.getString(
                androidx.compose.material3.R.string.time_picker_pm,
            )
        Strings.TimePickerPeriodToggle ->
            resources.getString(
                androidx.compose.material3.R.string.time_picker_period_toggle_description,
            )
        Strings.TimePickerMinuteSelection ->
            resources.getString(
                androidx.compose.material3.R.string.time_picker_minute_selection,
            )
        Strings.TimePickerHourSelection ->
            resources.getString(
                androidx.compose.material3.R.string.time_picker_hour_selection,
            )
        Strings.TimePickerHourSuffix ->
            resources.getString(
                androidx.compose.material3.R.string.time_picker_hour_suffix,
            )
        Strings.TimePickerMinuteSuffix ->
            resources.getString(
                androidx.compose.material3.R.string.time_picker_minute_suffix,
            )
        Strings.TimePicker24HourSuffix ->
            resources.getString(
                androidx.compose.material3.R.string.time_picker_hour_24h_suffix,
            )
        Strings.TimePickerHour ->
            resources.getString(
                androidx.compose.material3.R.string.time_picker_hour,
            )
        Strings.TimePickerMinute ->
            resources.getString(
                androidx.compose.material3.R.string.time_picker_minute,
            )
        Strings.TimePickerHourTextField ->
            resources.getString(
                androidx.compose.material3.R.string.time_picker_hour_text_field,
            )
        Strings.TimePickerMinuteTextField ->
            resources.getString(
                androidx.compose.material3.R.string.time_picker_minute_text_field,
            )
        Strings.TooltipPaneDescription ->
            resources.getString(
                androidx.compose.material3.R.string.tooltip_pane_description,
            )
        else -> ""
    }
}
