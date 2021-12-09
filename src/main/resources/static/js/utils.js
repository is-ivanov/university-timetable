/**
 * Getting person full name from row in table
 * @param {HTMLTableRowElement} row - The row with person first name, patronymic and last name
 * @param {string} type - student or teacher
 * @returns {string}
 */
function getPersonFullNameFromRow (row, type) {
  let firstName = $(row).children('.first_name').text();
  let patronymic = $(row).children('.patronymic').text();
  let lastName = $(row).children('.last_name').text();
  return  type + ' \'' + firstName + ' ' + patronymic + ' ' + lastName + '\' ';
}

