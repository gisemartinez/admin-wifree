function hideOptions(checkbox, target){
    if (!checkbox.checked) {
        $("[class='" + target + "']")[0].style.display = 'none';
    } else {
        $("[class='" + target + "']")[0].style.display = 'block';
    }
};

function hideFormFields(document, target){
    document.getElementById(target).addEventListener('change', (event) => {
        hideOptions(event.currentTarget, target);
    });
    hideOptions(document.getElementById(target), target);
}