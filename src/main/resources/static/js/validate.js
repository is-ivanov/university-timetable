// // $('#formNew').submit(function() {
// //   console.log($(this).serializeArray());
// //   return false;
// // });
//
// /**
//  * Validate form on server-side (send post request to server)
//  *
//  * @param {HTMLFormElement} form - Form for validate
//  * @param {String} url - url for validating request
//  */
// function validateForm (form, url) {
//   let dataRequest = form.serializeArray();
//
//   let posting = $.post(url, dataRequest);
//
//   posting.done(function (data) {
//     return true;
//   });
//   posting.fail(function (data) {
//     alert(data);
//   });
//
// }

/**
 * Validates form per AJAX. To be called as onSubmit handler.
 *
 * @param event onSubmit event
 */
function ajaxFormValidation (event) {
  event.preventDefault();
  _removeValidationErrors();
  let _form = $(event.target);
  let _formData = _form.serialize(true);
  _formData = _formData.replace('_method=put&', '');
  // // prepare visual feedback
  // // you may want to use other elements here
  // let originalButton = _form.find('.btn-primary');
  // // note: jQuery returns an array-like object
  // if (originalButton && originalButton.length && originalButton.length > 0) {
  //   originalButton.hide();
  //   let feedbackElement = $('<div class="feedback"></div>').
  //     insertAfter(originalButton);
  //   var restoreFunction = function () {
  //     originalButton.show();
  //     feedbackElement.remove();
  //   };
  // }
  let settings = {
    data: _formData,
    processData: false,
    type: 'POST',
    // success: function (response, statusText, xhr) {
    success: function () {
      $('form[data-validate]').unbind('submit');
      _form.submit();

      // if (response.location) {
      //   // no validation errors
      //   // action has been executed and sent a redirect URL wrapped as JSON
      //   // cannot use a normal http-redirect (status-code 3xx) as this would be followed by browsers and would not be available here
      //   // follow JSON-redirect
      //   window.location.href = response.location;
      // } else {
      //   if (restoreFunction) {
      //     restoreFunction();
      //   }
      //   _handleValidationResult(_form, response);
      // }
    },
    error: function (xhr, textStatus, errorThrown) {
      // if (restoreFunction) {
      //   restoreFunction();
      // }
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
  // window.setTimeout(function () {
  //   // let url = _form[0].action;
  //   let url = _form.attr('data-validate');
  //   jQuery.ajax(url, settings);
  // }, 1000);
  let url = _form.attr('data-validate');
  $.ajax(url, settings);
}

/**
 * Removes validation errors from HTML DOM.
 */
function _removeValidationErrors () {
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
  // // action errors
  // if (errors.errors) {
  //   // you might want to use a custom ID here
  //   var errorContainer = $('ul.errorMessage');
  //   $.each(errors.errors, function (index, errorMsg) {
  //     var li = $('<li><span></span></li>');
  //     li.text(errorMsg); // use text() for security reasons
  //     errorContainer.append(li);
  //   });
  // }
  // field errors
  if (errors.violations) {
    form.find('.validated-field').each(function (index, element) {
      // console.log('element: ' + index + '//' + element.name);
      let counter = 0;
      $.each(errors.violations, function (indexError, error) {
        if (element.name === error.field) {
          $(element).addClass('is-invalid');
          let div = $('<div class="invalid-feedback"></div>');
          div.text(error.message); // use text() for security reasons
          div.insertAfter(element);
          counter ++;
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