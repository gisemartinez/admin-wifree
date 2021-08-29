$(document).ready(function() {
    $("input:not(#answerNumber)").prop("readonly", true);

    $("#answerNumber").change(function(){
        var url = new URL(window.location.href);
        const total = Number($("#totalAnswers").text()) - 1;
        const offset = Number($("#answerNumber").val()) - 1;
        console.log("hola 1");
        if (offset >= 0 && offset <= total) {
            console.log("hola 2");
            url.searchParams.set("offset", offset);
            window.location.href = url.toString();
        }
    });

    $("[id$=_type_field]").hide();
    $("[id$=_config_key_field]").hide();
    $("[id$=_config_required_field]").hide();
    $("[id$=_config_order_field]").hide();
})

function next() {
    var url = new URL(window.location.href);
    var search = window.location.search;
    if (search == "") {
        url.searchParams.set("offset", 1);
    } else {
        const offset = Number(url.searchParams.get("offset")) + 1;
        url.searchParams.set("offset", offset);
    }
    window.location.href = url.toString();
}

function previous() {
    var url = new URL(window.location.href);
    var search = window.location.search;
    if (search == "") {
        url.searchParams.set("offset", 0);
    } else {
        const offset = Number(url.searchParams.get("offset")) - 1;
        url.searchParams.set("offset", offset);
    }
    window.location.href = url.toString();
}