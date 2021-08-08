/**
 * Add/remove style 'd-none' for show/hide rows in table
 *
 * @param {HTMLTableElement} table The table to switch
 * @param {HTMLInputElement} checkbox The checkbox switcher
 */
function showInactiveTableRow (table, checkbox) {

  if (checkbox.checked) {
    table.querySelectorAll('tr.inactive').forEach(tr =>
      tr.classList.remove('d-none'))
  } else {
    table.tBodies[0].querySelectorAll('tr.inactive').forEach(tr =>
      tr.classList.add('d-none'))
  }
}

/**
 * Add/remove style 'd-none' for show/hide option in select
 *
 * @param {HTMLInputElement} select     The input 'select' to switch
 * @param {HTMLInputElement} checkbox   The checkbox switcher
 */
function showInactiveSelectOption (select, checkbox) {

  const optionsInactive = select.querySelectorAll('option.inactive')
  if (checkbox.checked) {
    optionsInactive.forEach(option =>
      option.classList.remove('d-none'))
  } else {
    optionsInactive.forEach(option =>
      option.classList.add('d-none'))
  }

}