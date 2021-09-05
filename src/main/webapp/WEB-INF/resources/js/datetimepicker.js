
// Initializing
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