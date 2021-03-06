/**
 * Sorts a HTML table in frontend
 *
 * @param {HTMLTableElement} table  The table to sort
 * @param {number}           column The index of column to sort
 * @param {boolean}          asc    Determines if the sorting will be in ascending
 */
function sortTableByColumnFrontend (table, column, asc = true) {
  const dirModifier = asc ? 1 : -1;
  const tBody = table.tBodies[0];
  const rows = Array.from(tBody.querySelectorAll('tr'));

  // Sort each row
  const sortedRows = rows.sort((a, b) => {
    let aColText = a.querySelector(`td:nth-child(${column + 1})`).
      textContent.
      trim().
      toLowerCase();
    let bColText = b.querySelector(`td:nth-child(${column + 1})`).
      textContent.
      trim().
      toLowerCase();
    if (!isNaN(parseFloat(aColText)) && !isNaN(parseFloat((bColText)))) {
      aColText = parseFloat(aColText);
      bColText = parseFloat(bColText);
    } else if (!isNaN(Date.parse(aColText)) && !isNaN(Date.parse(bColText))) {
      aColText = Date.parse(aColText);
      bColText = Date.parse(bColText);
    }

    return aColText > bColText ? (1 * dirModifier) : (-1 * dirModifier);

  });

  // Remove all existing TRs from the table
  while (tBody.firstChild) {
    tBody.removeChild(tBody.firstChild);
  }

  // Re-add the newly sorted rows
  tBody.append(...sortedRows);

  // Remember how the column is currently sorted
  table.querySelectorAll('th').
    forEach(th => th.classList.remove('th-sort-asc', 'th-sort-desc'));
  table.querySelector(`th:nth-child(${column + 1})`).
    classList.
    toggle('th-sort-asc', asc);
  table.querySelector(`th:nth-child(${column + 1})`).
    classList.
    toggle('th-sort-desc', !asc);

}

document.querySelectorAll('.table-sortable thead .th-sort').
  forEach(headerCell => {
    headerCell.addEventListener('click', () => {
      const tableElement = headerCell.parentElement.parentElement.parentElement;
      const headerIndex = Array.prototype.indexOf.call(
        headerCell.parentElement.children, headerCell);
      const currentIsAscending = headerCell.classList.contains('th-sort-asc');
      if (!headerCell.classList.contains('sort-server')) {
        sortTableByColumnFrontend(tableElement, headerIndex, !currentIsAscending);
      } else {
        window.location.href = headerCell.getAttribute('data-href');
      }
    });
  });