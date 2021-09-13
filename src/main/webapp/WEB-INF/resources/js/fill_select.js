/**
 * Send GET request and call function for filling select Teachers
 *
 * @param {String} time_start - Time start lesson for GET request
 * @param {String} time_end - Time end lesson for GET request
 * @param {HTMLSelectElement} select - The select element for filling data
 */
function updateSelectTeacherFilteredByDate (time_start, time_end, select) {
  $.get('/teachers/free', { time_start: time_start, time_end: time_end },
    function (data) {
      fillSelectPersonWithDefaultPerson(select, data, false);
    });
}

/**
 * Send GET request and call function for filling select Rooms
 *
 * @param {String} time_start - Time start lesson for GET request
 * @param {String} time_end - Time end lesson for GET request
 * @param {HTMLSelectElement} select - The select element for filling data
 */
function updateSelectRoomFilteredByDate (time_start, time_end, select) {
  $.get('/rooms/free', { time_start: time_start, time_end: time_end },
    function (data) {
      fillSelectRoomFilteredByDate(select, data);
    });
}

/**
 * Send GET request and call function for filling select Groups
 *
 * @param {String} time_start - Time start lesson for GET request
 * @param {String} time_end - Time end lesson for GET request
 * @param {Number} facultyId - Selected faculty id
 * @param {HTMLSelectElement} select - The select element for filling data
 */
function updateSelectGroupFilteredByFacultyAndDate (
  time_start, time_end, facultyId, select) {
  let uri = '/faculties/' + facultyId + '/groups/free';
  $.get(uri, { time_start: time_start, time_end: time_end }, function (data) {
    fillSelectGroups(select, data, false);
  });
}

/**
 * Send GET request and call function for filling select Groups
 *
 * @param {Number} facultyId - Selected faculty id
 * @param {HTMLSelectElement} select - The select element for filling data
 * @param {Boolean} isShowInactive - With true show inactive object
 */
function updateSelectGroupFilteredByFaculty (
  facultyId, select, isShowInactive) {
  let uri = '/faculties/' + facultyId + '/groups';
  $.get(uri, function (data) {
    fillSelectGroups(select, data, isShowInactive);
  });
}

/**
 * Send GET request and call function for filling select Students
 *
 * @param {String} time_start - Time start lesson for GET request
 * @param {String} time_end - Time end lesson for GET request
 * @param {Number} groupId - Selected group id
 * @param {HTMLSelectElement} select - The select element for filling data
 */
function updateSelectStudentsFilteredByGroupAndDate (
  time_start, time_end, groupId, select) {
  let uri = '/groups/' + groupId + '/students/free';
  $.get(uri, { time_start: time_start, time_end: time_end }, function (data) {
    fillSelectPerson(select, data, false);
  });
}

/**
 * Send GET request and call function for filling select departments
 *
 * @param {Number} facultyId - Selected faculty id
 * @param {HTMLSelectElement} select - The select element for filling data
 */
function updateSelectDepartmentsFilteredByFaculty (facultyId, select) {
  let uri = '/faculties/' + facultyId + '/departments';
  $.get(uri, function (data) {
    fillSelectDepartment(select, data);
  });
}

//--------------------------------------------------

/**
 * Fill options in selector for person filtered by date lesson
 *
 * @param {HTMLSelectElement} select - The select element for filling data
 * @param {Object} data - Returned data from GET response
 * @param {Boolean} isShowInactive - With true show inactive object
 */
function fillSelectPersonWithDefaultPerson (select, data, isShowInactive) {
  data.sort(sortByFullName);
  let selectedValue = $(select).children('option:selected').val();
  let selectedText = $(select).children('option:selected').text();
  $(select).
    empty().
    append('<option value="0">Please select person ...</option>').
    append('<option value="' + selectedValue + '" selected>' + selectedText +
      '</option>');
  data.forEach(function (person) {
    fillSelectActive(person.id, person.fullName, person.active, isShowInactive,
      select);
  });
}

/**
 * Fill options in selector for person filtered by date lesson
 *
 * @param {HTMLSelectElement} select - The select element for filling data
 * @param {Object} data - Returned data from GET response
 * @param {Boolean} isShowInactive - With true show inactive object
 */
function fillSelectPerson (select, data, isShowInactive) {
  data.sort(sortByFullName);
  $(select).
    empty().
    append('<option value="0">Please select person ...</option>');
  data.forEach(function (person) {
    fillSelectActive(person.id, person.fullName, person.active, isShowInactive,
      select);
  });
}

/**
 * Fill options in selector for room filtered by date lesson
 *
 * @param {HTMLSelectElement} select - The select element for filling data
 * @param {Object} data - Returned data from GET response
 */
function fillSelectRoomFilteredByDate (select, data) {
  data.sort(sortByRoom);
  let selectedValue = $(select).children('option:selected').val();
  let selectedText = $(select).children('option:selected').text();
  $(select).
    empty().
    append('<option value="0">Please select room...</option>').
    append('<option value="' + selectedValue + '" selected>' + selectedText +
      '</option>');
  data.forEach(function (room) {
    let name = room.building + ' - ' + room.number;
    let option = '<option value = ' + room.id + '>' + name + '</option>';
    select.append(option);
  });
}

/**
 * Fills in the selector for selecting a group with data from the database
 *
 * @param {HTMLSelectElement} select - The select element for filling data
 * @param {Object} data - Returned data from GET response
 * @param {Boolean} isShowInactive - With true we show inactive groups
 */
function fillSelectGroups (select, data, isShowInactive) {
  data.sort(sortByName);
  $(select).
    empty().
    append('<option value="0" selected>Please select group...</option>');
  data.forEach(function (group) {
    fillSelectActive(group.id, group.name, group.active, isShowInactive,
      select);
  });
}

/**
 * Fills option in the selector for selecting a department
 *
 * @param {HTMLSelectElement} select - The select element for filling data
 * @param {Object} data - Returned data from GET response
 */
function fillSelectDepartment (select, data) {
  data.sort(sortByName);
  $(select).
    empty().
    append('<option value="0" selected>Please select department...</option>');
  data.forEach(function (department) {
    fillSelectWithoutActive(department.id, department.name, select);
  });
}

/**
 * Fills select with active fields
 *
 * @param {Number}            value
 * @param {Text}              text
 * @param {Boolean}           isActive
 * @param {Boolean}           isShowInactive
 * @param {HTMLSelectElement} select
 */
function fillSelectActive (value, text, isActive, isShowInactive, select) {
  let option;
  if (isActive === true) {
    option = '<option class = \'active\' value = ' + value + '>' + text +
      '</option>';
  } else if (isShowInactive) {
    option = '<option class = \'inactive\' value = ' + value + '>' + text +
      ' - inactive' + '</option>';
  } else {
    option = '<option class = \'inactive d-none\' value = ' + value + '>' +
      text + ' - inactive' + '</option>';
  }
  select.append(option);
}

/**
 * Fills select without active fields
 *
 * @param {Number}            value
 * @param {Text}              text
 * @param {HTMLSelectElement} select
 */
function fillSelectWithoutActive (value, text, select) {
  let option = '<option value = ' + value + '>' + text + '</option>';
  select.append(option);
}

/**
 * Fill select 'selectTeacher' with teachers data from the DB
 *
 * @param {HTMLSelectElement} select The select element for filling data
 * @param {Number} valueSelect The value from 'select' with condition
 * @param {String} type Type of select (faculty; department)
 */
function fillSelectTeachers (select, valueSelect, type) {
  let uri;
  if (type === 'faculty') {
    uri = 'lessons/faculties?facultyId=' + valueSelect;
  } else if (type === 'department') {
    uri = 'lessons/departments?departmentId=' + valueSelect;
  }
  $.get(uri, function (data) {
    $('#selectTeacher').
      empty().
      append('<option value="0" selected>Please select teacher...</option>');
    data.sort(function (a, b) {
      const nameA = a.fullName.toLowerCase();
      const nameB = b.fullName.toLowerCase();
      if (nameA < nameB)
        return -1;
      if (nameA > nameB)
        return 1;
      return 0;
    });
    data.forEach(function (teacher) {
      let option;
      if (teacher.active === true) {
        option = '<option class = \'active\' value = ' + teacher.id + '>' +
          teacher.fullName + '</option>';
      } else if ($('#switchShowInactiveTeachers').is(':checked')) {
        option = '<option class = \'inactive\' value = ' + teacher.id + '>' +
          teacher.fullName + ' - inactive' + '</option>';
      } else {
        option = '<option class = \'inactive d-none\' value = ' + teacher.id +
          '>' + teacher.fullName + ' - inactive' + '</option>';
      }
      $('#selectTeacher').append(option);
    });
  });
}

function sortByName (a, b) {
  const nameA = a.name.toLowerCase();
  const nameB = b.name.toLowerCase();
  return sortByText(nameA, nameB);
}

function sortByFullName (a, b) {
  const fullNameA = a.fullName.toLowerCase();
  const fullNameB = b.fullName.toLowerCase();
  return sortByText(fullNameA, fullNameB);
}

function sortByRoom (a, b) {
  let roomA = a.building + ' - ' + a.number;
  let roomB = b.building + ' - ' + b.number;
  roomA = roomA.toLowerCase();
  roomB = roomB.toLowerCase();
  return sortByText(roomA, roomB);
}

function sortByText (a, b) {
  if (a < b)
    return -1;
  if (a > b)
    return 1;
  return 0;
}
