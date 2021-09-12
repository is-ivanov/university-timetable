/**
 * Getting person full name from row in table
 * @param {HTMLTableRowElement} row - The row with person first name, patronymic and last name
 * @returns {string}
 */
function getPersonFullNameFromRow (row) {
  let firstName = $(row).children('.first_name').text();
  let patronymic = $(row).children('.patronymic').text();
  let lastName = $(row).children('.last_name').text();
  return  'student \'' + firstName + ' ' + patronymic + ' ' + lastName + '\' ';
}

