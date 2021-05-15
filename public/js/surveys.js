$(document).ready(function() {
    $("#newInputButton").click(function() {
        cloneAndSelectInputs(false, false, false, "");
    });

    $("#newTextInputButton").click(function() {
        cloneAndSelectInputs(false, true, true, "textbox");
    });

    $("#newRatingInputButton").click(function() {
        cloneAndSelectInputs(true, false, true, "rating");
    });

    $("#newRadioInputButton").click(function() {
        cloneAndSelectInputs(true, true, false, "radio");
    });

    $("#newSelectorInputButton").click(function() {
        cloneAndSelectInputs(true, true, false, "selector");
    });

    $("#newCheckboxInputButton").click(function() {
        cloneAndSelectInputs(true, true, false, "checkbox");
    });
})

function deleteSection(element) {
    let nodeToRemove = element.parentNode.parentNode;
    let nodeToRemoveIndex = Number(nodeToRemove.querySelector(".field-type").name.match(/\d+/)[0]);
    nodeToRemove.remove();
    
    let allNodes = document.querySelectorAll(".survey-fields-set");
     
    for (let i = nodeToRemoveIndex; i < allNodes.length; i++) {
        const node = allNodes[i];
        let next = i+1;
        let elementsToUpdate = node.querySelectorAll(`[id^=fields_${next}], [for^=fields_${next}], [name^="fields[${next}]"], h3`);
        elementsToUpdate.forEach(element => {
            let id = element.id;
            let name = element.name;
            let _for = element.getAttribute("for");
            let innerText = element.innerText;

            if (id) element.id = id.replaceAll(`fields_${i+1}`, `fields_${i}`);
            if (name) element.name = name.replaceAll(`fields[${i+1}`, `fields[${i}`);
            if (_for) element.setAttribute("for", _for.replaceAll(`fields_${i+1}`, `fields_${i}`));
            if (innerText && ["LABEL", "H3"].includes(element.tagName)) {
                let newText = innerText.replaceAll(`fields.${i+1}`, `fields.${i}`).replaceAll(`Pregunta ${i+1}`, `Pregunta ${i}`);
                element.innerText = newText;
            }
        });
    }
    
}

function addRatingOption(element) {
    console.log("click en add rating option, elem: " + element);
    let options = element.parentNode.firstElementChild;
    let lastOption = options.lastElementChild;
    let newOption = lastOption.cloneNode(true);
    
    let questionOptions = options.querySelectorAll(".question-radio");
    let lastQuestionOption = questionOptions[questionOptions.length - 1];
    let i = Number([...lastQuestionOption.name.matchAll(/\d+/g)][1][0]);
    newOption.innerHTML = newOption.innerHTML.replaceAll(`Options_${i}`, `Options_${i+1}`).replaceAll(`Options[${i}]`, `Options[${i+1}]`).replaceAll(`Options.${i}`, `Options.${i+1}`);

    options.appendChild(newOption);
}

function cloneAndSelectInputs(hideText, hideRating, hideRadio, fieldType) {
    let i = Number($(".field-type").last().attr("id").match(/\d+/)[0]);
    let allNodes = document.querySelectorAll(".survey-fields-set")
    let node = allNodes[allNodes.length -1];
    let parent = node.parentElement;
    let newNode = node.cloneNode(true);
    newNode.innerHTML = newNode.innerHTML.replaceAll(`fields_${i}`, `fields_${i + 1}`).replaceAll(`fields[${i}]`, `fields[${i + 1}]`).replaceAll(`fields.${i}`, `fields.${i + 1}`).replaceAll(`Pregunta ${i}`, `Pregunta ${i+1}`);
    newNode.querySelectorAll(".question-text").forEach(e => e.parentNode.parentNode.hidden = hideText);
    newNode.querySelectorAll(".question-rating").forEach(e => e.parentNode.parentNode.hidden = hideRating);
    //newNode.querySelectorAll(".question-radio").forEach(e => e.parentNode.parentNode.hidden = hideRadio);
    newNode.querySelector(".rating-options").hidden = hideRadio;
    let fieldTypeElement = newNode.querySelector(".field-type");
    fieldTypeElement.value = fieldType;
    fieldTypeElement.parentNode.parentNode.hidden = true;
    newNode.hidden = false;
    newNode.lastElementChild.lastElementChild.firstElementChild.replaceChildren(newNode.lastElementChild.lastElementChild.firstElementChild.firstElementChild)
    parent.appendChild(newNode);
}