/**
 * Fills in the selector for selecting a group with data from the database
 *
 * @param {HTMLSelectElement} select - The select element for filling data
 * @param {Number} facultyId - The id selected faculty (0 when not selected)
 * @param {Boolean} isShowInactive - With true we show inactive groups
 */
function fillSelectGroups (select, facultyId, isShowInactive) {
  let uri = '/faculties/' + facultyId + '/groups'
  $.get(uri, function (data) {
    select.
      empty().
      append('<option value="0" selected>Please select group...</option>')
    data.sort(sortByName)
    data.forEach(function (group) {
      fillSelectActive(group.id, group.name, group.active, isShowInactive,
        select)
    })
  })
}

/**
 * Fills in the selector for selecting a student with data from the database
 *
 * @param {HTMLSelectElement} select - The select element for filling data
 * @param {Number} facultyId - Id selected faculty (0 when not selected)
 * @param {Number} groupId - Id selected group (0 when not selected)
 * @param {Boolean} isShowInactive - With true we show inactive students
 */
function fillSelectStudents (select, facultyId, groupId, isShowInactive) {
  let uri
  if (groupId > 0) {
    uri = '/groups/' + groupId
  } else if (groupId === 0 && facultyId >= 0) {
    uri = '/faculties/' + facultyId
  }
  uri = uri + '/students'
  $.get(uri, function (data) {
    select.
      empty().
      append(
        '<option value="" disabled selected>Please select student...</option>')
    data.sort(sortByFullName)
    data.forEach(function (studentDto) {
      fillSelectActive(studentDto.id, studentDto.fullName, studentDto.active,
        isShowInactive, select)
    })
  })
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
  let option
  if (isActive === true) {
    option = '<option class = \'active\' value = ' + value + '>' +
      text + '</option>'
  } else if (isShowInactive) {
    option = '<option class = \'inactive\' value = ' + value + '>' +
      text + ' - inactive' + '</option>'
  } else {
    option = '<option class = \'inactive d-none\' value = ' + value + '>' +
      text + ' - inactive' + '</option>'
  }
  select.append(option)
}

/**
 * Fill select 'selectDepartment' with departments data from the DB
 */
function fillSelectDepartments () {
  let faculty = $('#selectFaculty').val()
  $.get('/teachers/departments?facultyId=' + faculty, function (data) {
    $('#selectDepartment').
      empty().
      append('<option value="0" selected>Please select department...</option>')
    data.sort(sortByName)
    data.forEach(function (item) {
      let option = '<option value = ' + item.id + '>' + item.name + '</option>'
      $('#selectDepartment').append(option)
    })
  })
}

/**
 * Fill select 'selectTeacher' with teachers data from the DB
 *
 * @param {HTMLSelectElement} select The select element for filling data
 * @param {Number} facultyId - The id selected faculty (0 when not selected)
 * @param {Number}            valueSelect The value from 'select' with condition
 * @param {String}            type        Type of select (faculty; department)
 */
function fillSelectTeachers (select, valueSelect, type) {
  let uri
  if (type === 'faculty') {
    uri = 'lessons/faculties?facultyId=' + valueSelect
  } else if (type === 'department') {
    uri = 'lessons/departments?departmentId=' + valueSelect
  }
  $.get(uri, function (data) {
    $('#selectTeacher').
      empty().
      append('<option value="0" selected>Please select teacher...</option>')
    data.sort(function (a, b) {
      const nameA = a.fullName.toLowerCase()
      const nameB = b.fullName.toLowerCase()
      if (nameA < nameB)
        return -1
      if (nameA > nameB)
        return 1
      return 0
    })
    data.forEach(function (teacher) {
      let option
      if (teacher.active === true) {
        option = '<option class = \'active\' value = ' + teacher.id + '>' +
          teacher.fullName + '</option>'
      } else if ($('#switchShowInactiveTeachers').is(':checked')) {
        option = '<option class = \'inactive\' value = ' + teacher.id + '>' +
          teacher.fullName + ' - inactive' + '</option>'
      } else {
        option = '<option class = \'inactive d-none\' value = ' + teacher.id +
          '>' + teacher.fullName + ' - inactive' + '</option>'
      }
      $('#selectTeacher').append(option)
    })
  })
}

function fillSelectFreeTeachers(select, )

function sortByName (a, b) {
  const nameA = a.name.toLowerCase()
  const nameB = b.name.toLowerCase()
  return sortByText(nameA, nameB)
}

function sortByFullName (a, b) {
  const fullNameA = a.fullName.toLowerCase()
  const fullNameB = b.fullName.toLowerCase()
  return sortByText(fullNameA, fullNameB)
}

function sortByText (a, b) {
  if (a < b)
    return -1
  if (a > b)
    return 1
  return 0
}
