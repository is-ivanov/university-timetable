/**
 * Fill select 'selectGroup' with groups data from the DB
 */
function fillSelectGroups () {
  let faculty = $('#selectFaculty').val()
  $.get('/student/faculty?facultyId=' + faculty, function (data) {
    $('#selectGroup').
      empty().
      append('<option disabled selected>Please select group...</option>')
    data.sort(function (a, b) {
      const nameA = a.name.toLowerCase()
      const nameB = b.name.toLowerCase()
      if (nameA < nameB)
        return -1
      if (nameA > nameB)
        return 1
      return 0
    })
    data.forEach(function (item) {
      let option
      if (item.active === true) {
        option = '<option class = \'active\' value = ' + item.id + '>' +
          item.name + '</option>'
      } else if ($('#switchShowInactiveGroups').is(':checked')) {
        // const
        option = '<option class = \'inactive\' value = ' + item.id + '>' +
          item.name +
          ' - inactive' + '</option>'
      } else {
        option = '<option class = \'inactive d-none\' value = ' + item.id +
          '>' +
          item.name +
          ' - inactive' + '</option>'
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
  $.get('/teacher/faculty?facultyId=' + faculty, function (data) {
    $('#selectDepartment').
      empty().
      append('<option disabled selected>Please select department...</option>')
    data.sort(function (a, b) {
      const nameA = a.name.toLowerCase()
      const nameB = b.name.toLowerCase()
      if (nameA < nameB)
        return -1
      if (nameA > nameB)
        return 1
      return 0
    })
    data.forEach(function (item) {
      let option = '<option value = ' + item.id + '>' + item.name + '</option>'
      $('#selectDepartment').append(option)
    })
  })
}

/**
 * Fill select 'selectTeacher' with teachers data from the DB
 */
function fillSelectTeachers () {
  let department = $('#selectDepartment').val()
  $.get('/lesson/department?departmentId=' + department, function (data) {
    $('#selectTeacher').
      empty().
      append('<option disabled selected>Please select teacher...</option>')
    data.forEach(function (teacher) {
      teacher.fullName = teacher.lastName + ' ' + teacher.firstName.charAt(0)
        + '.' + teacher.patronymic.charAt(0) + '.'
    })
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
      let option = '<option value = ' + teacher.id + '>' + teacher.fullName + '</option>'
      $('#selectTeacher').append(option)
    })
  })
}