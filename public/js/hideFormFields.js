$(document).ready(function () {
  'use strict'
  const anyCheckbox = $("input[type=checkbox]");

  changeFormFields();
  anyCheckbox.change(function(event) {
    changeFormFields()
  });
})

function changeFormFields() {
  if ($('#facebook_enabled').is(':checked')) {
    changeToRequired("toHidefacebook_loginMethod", "facebook_keys_secret", "facebook_keys_clientId")
  } else {
    changeToNotRequired("toHidefacebook_loginMethod", "facebook_keys_secret", "facebook_keys_clientId")
  }

  if ($('#google_enabled').is(':checked')) {
    changeToRequired("toHidegoogle_loginMethod", "google_keys_secret", "google_keys_clientId")
  } else {
    changeToNotRequired("toHidegoogle_loginMethod", "google_keys_secret", "google_keys_clientId")
  }
}

function changeToRequired(formId, secretId, clientId) {
  $("#" + formId).css('display', 'block');
  $("#" + secretId).prop('required', true);
  $("#" + clientId).prop('required', true);
}

function changeToNotRequired(formId, secretId, clientId) {
  $("#" + secretId).prop('required', false);
  $("#" + clientId).prop('required', false);
  $("#" + formId).css('display', 'none');
}