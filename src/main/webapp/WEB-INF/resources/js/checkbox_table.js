let checkAll = $('#check-all')
let checkboxes = $('input.checkbox')

// function changeStateElementsAfterCheckboxUpdate(){
//   if ()
// }
checkAll.on('click', function () {
  for (let i = 0; i < checkboxes.length; i++) {
    checkboxes[i].attr('checked', '')
  }
})

function enableElementsAfterCheckboxUpdate () {
  $('.checkbox__depends').removeAttr('disabled')
}

function disableElementsAfterCheckboxUpdate () {
  $('.checkbox__depends').attr('disabled', '')
}

