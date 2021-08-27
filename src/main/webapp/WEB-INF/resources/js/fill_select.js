/**
 * Fill select 'selectGroup' with groups data from the DB
 */
function fillSelectGroups () {
  let faculty = $('#selectFaculty').val()
  $.get('/students/groups?facultyId=' + faculty, function (data) {
    $('#selectGroup').
      empty().
      append('<option value="0" selected>Please select group...</option>')
    data.sort(sortByName)
    data.forEach(function (item) {
      let option
      if (item.active === true) {
        option = '<option class = \'active\' value = ' + item.id + '>' +
          item.name + '</option>'
      } else if ($('#switchShowInactiveGroups').is(':checked')) {
        option = '<option class = \'inactive\' value = ' + item.id + '>' +
          item.name + ' - inactive' + '</option>'
      } else {
        option = '<option class = \'inactive d-none\' value = ' + item.id +
          '>' + item.name + ' - inactive' + '</option>'
      }
      $('#selectGroup').append(option)
    })
  })
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
 * @param {Number} valueSelect The value from 'select' with condition
 * @param {String} type        Type of select (faculty; department)
 */
function fillSelectTeachers (valueSelect, type) {
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

function sortByName (a, b) {
  const nameA = a.name.toLowerCase()
  const nameB = b.name.toLowerCase()
  if (nameA < nameB)
    return -1
  if (nameA > nameB)
    return 1
  return 0
}