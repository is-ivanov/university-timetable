/**
 * Validates form per AJAX. To be called as onSubmit handler.
 *
 * @param event onSubmit event
 */
function ajaxFormValidation (event) {
  event.preventDefault();
  removeValidationErrors();
  const _form = $(event.target);
  const _formData = getFormData(_form);
  const _formMethod = _form.attr('method');

  let json_form_data = JSON.stringify(_formData,
    (key, value) => {
      if (key.substring(0, 4) === 'time') {
        return value.replace(' ', 'T');
      }
      return value;
    },
  );

  let settings = {
    data: json_form_data,
    processData: false,
    type: _formMethod,
    contentType: 'application/json; charset=UTF-8',
    success: function (response) {
      // let responseText = JSON.parse(response);
      if (response._links.redirect.href) {
        // no validation errors
        // action has been executed and sent a redirect URL wrapped as JSON
        // cannot use a normal http-redirect (status-code 3xx) as this would be followed by browsers and would not be available here
        // follow JSON-redirect
        window.location.href = response._links.redirect.href;
      }
    },
    error: function (xhr) {
      // struts sends status code 400 when validation errors are present
      if (xhr.status === 400) {
        _handleValidationResult(_form, JSON.parse(xhr.responseText));
      } else {
        // a real error occurred -> show user an error message
        _handleValidationResult(_form,
          { errors: ['Network or server error!'] });
      }
    },
  };
  // send request, after delay to make sure everybody notices the visual feedback :)
  let url = _form[0].action;
  $.ajax(url, settings);
}

/**
 * Removes validation errors from HTML DOM.
 */
function removeValidationErrors () {
  // action errors
  // you might want to use a custom ID here
  $('form :input').removeClass('is-valid is-invalid');
  // field errors
  $('div.invalid-feedback,div.valid-feedback').remove();
}

/**
 * Incorporates validation errors in HTML DOM.
 *
 * @param form Form containing errors.
 * @param errors Errors from server.
 */
function _handleValidationResult (form, errors) {
  // field errors
  if (errors.violations) {
    form.find('.validated-field').each(function (index, element) {
      let counter = 0;
      $.each(errors.violations, function (indexError, error) {
        if (element.name === error.field ||
          $(element).attr('data-validate-field') === error.field) {
          $(element).addClass('is-invalid');
          let div = $('<div class="invalid-feedback"></div>');
          div.text(error.message); // use text() for security reasons
          div.insertAfter(element);
          counter++;
        }
      });
      if (counter === 0) {
        $(element).addClass('is-valid');
      }
    });
  }
}

// register onSubmit handler
$(window).bind('load', function () {
  $('form[data-validate]').bind('submit', ajaxFormValidation);
});

function getFormData ($form) {
  let array = $form.serializeArray();
  let indexed_array = {};

  $.map(array, function (n) {
    indexed_array[n['name']] = n['value'];
  });

  return indexed_array;
}