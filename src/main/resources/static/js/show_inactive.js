/**
 * Show/hide rows in table
 *
 * @param {HTMLTableElement} table    The table to switch
 * @param {HTMLInputElement} checkbox The checkbox switcher
 */
function showInactiveTableRow (table, checkbox) {

  $(table).find('tr.inactive').each(function () {
    showInactiveElement($(this), checkbox);
  });
}

/**
 * Show/hide option in select
 *
 * @param {HTMLSelectElement} select     The input 'select' to switch
 * @param {HTMLInputElement}  checkbox   The checkbox switcher
 */
function showInactiveSelectOption (select, checkbox) {

  $(select).find('option.inactive').each(function () {
    showInactiveElement($(this), checkbox);
  });
}

/**
 * Add/remove style 'd-none' for show/hide option in element
 *
 * @param element
 * @param checkbox
 */
function showInactiveElement (element, checkbox) {
  if ($(checkbox).is(':checked')) {
    $(element).removeClass('d-none');
  } else {
    $(element).addClass('d-none');
  }
}
