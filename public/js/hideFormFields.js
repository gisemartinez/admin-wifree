function hideFormFields(document, target, fieldToHide){
    $('#' + target + ':checkbox').change(changeFields(target, fieldToHide));
}

function changeFields(target,fieldToHide){
    if($('#' + target).is(':checked')) {
        $("#"+fieldToHide+"* input").prop('required',true);
        $("#"+fieldToHide).css('display','block');
    } else {
        $("#"+fieldToHide+"* input").prop('required',false);
        $("#"+fieldToHide).css('display','none');
    }
}

