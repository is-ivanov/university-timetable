let checkAll = $('#check-all')
let checkboxes = $('input.checkbox-table')

for (let checkbox of checkboxes) {
  $(checkbox).on('click', function () {
    $(this).parents('tr').toggleClass('selected')
    let checkedCount = $('input.checkbox-table:checked').length
    checkAll.prop('checked', checkedCount > 0)
    checkAll.prop('indeterminate', checkedCount > 0 && checkedCount <
      checkboxes.length)
    changeAvailabilityElements(checkedCount)
  })
}

checkAll.on('click', function () {
  $('.checkbox-table').prop('checked', this.checked)
  changeAvailabilityElements($('input.checkbox-table:checked').length)
  if (this.checked === true) {
    checkboxes.parents('tr').addClass('selected')
  } else {
    checkboxes.parents('tr').removeClass('selected')
  }
})

/**
 *
 *
 * @param checkedCount {Number} - The count checked checkbox
 */
function changeAvailabilityElements (checkedCount) {
  if (checkedCount > 0) {
    enableElementsAfterCheckboxUpdate()
  } else {
    disableElementsAfterCheckboxUpdate()
  }
}

function enableElementsAfterCheckboxUpdate () {
  $('.checkbox__depends').removeAttr('disabled')
}

function disableElementsAfterCheckboxUpdate () {
  $('.checkbox__depends').attr('disabled', '')
}

