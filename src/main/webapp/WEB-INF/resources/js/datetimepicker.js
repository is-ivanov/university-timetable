/**
 * initializing datetimepickers
 */
$(function () {
  $('.date').datetimepicker({
    useCurrent: false,
    format: 'yyyy-MM-DD HH:mm',
    calendarWeeks: true,
    icons: {
      time: 'far fa-clock',
      today: 'far calendar-check',
    },
    buttons: {
      showToday: true,
      showClear: true,
      showClose: true,
    },
  })
})

const LESSON_DURATION_MINUTE = 90

/**
 * Update inputEndTime in pairs datetimepickers
 *
 * @param inputEnd {HTMLElement} element for END lesson datetimepicker
 * @param event
 */
function updateEndDatetimepicker (inputEnd, event) {
  let timeStart = event.date
  let timeEnd = moment(timeStart).add(LESSON_DURATION_MINUTE, 'minutes')
  inputEnd.children('input').val(moment(timeEnd).format('yyyy-MM-DD HH:mm'))
  inputEnd.datetimepicker('minDate', timeStart)
  inputEnd.datetimepicker('defaultDate', timeEnd)
  inputEnd.datetimepicker('viewMode', 'times')
}

/**
 * Update inputStartTime in pairs datetimepickers
 *
 * @param inputStart {HTMLElement} element for END lesson datetimepicker
 * @param event
 */
function updateStartDatetimepicker (inputStart, event) {
  let timeEnd = event.date
  inputStart.datetimepicker('maxDate', timeEnd)
  inputStart.datetimepicker('viewMode', 'times')
}