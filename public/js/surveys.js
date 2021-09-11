$(document).ready(function() {
    Object.defineProperty(NodeList.prototype, "last", {
        value: function last() {
            if (this.length == 0)
                return;
            else
                return this[this.length - 1];
        },
        writable: true,
        configurable: true
    });

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

    if (!window.location.href.includes("answers")) {
        document.querySelectorAll(".rating-option").forEach(r => formatRatingOption(r));
    }
})

function deleteSection(element) {
    let nodeToRemove = element.parentNode.parentNode.parentNode;
    let nodeToRemoveIndex = Number(nodeToRemove.querySelector(".field-type").name.match(/\d+/)[0]);
    nodeToRemove.remove();
    
    let allNodes = document.querySelectorAll(".survey-fields-set");
     
    for (let i = nodeToRemoveIndex; i < allNodes.length; i++) {
        const node = allNodes[i];
        let next = i + 1;
        let elementsToUpdate = node.querySelectorAll(`[id^=fields_${next}], [for^=fields_${next}], [name^="fields[${next}]"], h3`);
        elementsToUpdate.forEach(element => {
            let id = element.id;
            let name = element.name;
            let _for = element.getAttribute("for");
            let innerText = element.innerText;

            if (id) element.id = id.replaceAll(`fields_${next}`, `fields_${i}`);
            if (id.includes("order")) element.setAttribute("value", i);
            if (name) element.name = name.replaceAll(`fields[${next}`, `fields[${i}`);
            if (_for) element.setAttribute("for", _for.replaceAll(`fields_${next}`, `fields_${i}`));
            if (innerText && ["LABEL", "H3"].includes(element.tagName)) {
                let newText = innerText.replaceAll(`fields.${next}`, `fields.${i}`).replaceAll(`Pregunta ${next}`, `Pregunta ${i}`);
                element.innerText = newText;
            }
        });
    }
    
}

function addRatingOption(element) {
    let options = element.parentNode.firstElementChild;
    let lastOption = options.lastElementChild;
    let newOption = lastOption.cloneNode(true);
    
    let questionOptions = options.querySelectorAll(".question-radio");
    let lastQuestionOption = questionOptions[questionOptions.length - 1];
    let i = Number([...lastQuestionOption.name.matchAll(/\d+/g)][1][0]);
    newOption.innerHTML = newOption.innerHTML.replaceAll(`Options_${i}`, `Options_${i+1}`).replaceAll(`Options[${i}]`, `Options[${i+1}]`).replaceAll(`Options.${i}`, `Options.${i+1}`);
    
    newOption.querySelectorAll("input").forEach(x => x.setAttribute("value", ''));
    newOption.querySelector("[id$=_index]").setAttribute("value", i+2);

    options.appendChild(newOption);
}

function cloneAndSelectInputs(hideText, hideRating, hideRadio, fieldType) {
    // Get last index
    let i = Number($(".field-type").last().attr("id").match(/\d+/)[0]);
    
    // Get last existing node and clone it
    let allNodes = document.querySelectorAll(".survey-fields-set")
    let node = allNodes.last();
    let parent = node.parentElement;
    let newNode = node.cloneNode(true);
    let nodeKey = node.querySelector("#fields_" + i + "_config_key").value;

    // Update cloned node index number
    newNode.innerHTML = newNode.innerHTML.replaceAll(`fields_${i}`, `fields_${i+1}`).replaceAll(`fields[${i}]`, `fields[${i+1}]`).replaceAll(`fields.${i}`, `fields.${i+1}`).replaceAll(`Pregunta ${i}`, `Pregunta ${i+1}`);
    
    // Clear inputs values
    newNode.querySelectorAll("input").forEach(x => x.setAttribute("value", ''));

    // Update key number
    newNode.querySelector("#fields_" + (i+1) + "_config_key").setAttribute("value", nodeKey.replaceAll(i, i+1));
    newNode.querySelector("#fields_" + (i+1) + "_config_order").setAttribute("value", i+1);

    // Hide not corresponding elements
    newNode.querySelectorAll(".question-text").forEach(e => e.parentNode.parentNode.hidden = hideText);
    newNode.querySelectorAll(".question-rating").forEach(e => e.parentNode.parentNode.parentNode.hidden = hideRating);
    //newNode.querySelectorAll(".question-radio").forEach(e => e.parentNode.parentNode.hidden = hideRadio);
    newNode.querySelector(".rating-options").hidden = hideRadio;
    
    let fieldTypeElement = newNode.querySelector(".field-type");
    fieldTypeElement.value = fieldType;
    fieldTypeElement.parentNode.parentNode.hidden = true;
    newNode.hidden = false;

    if (fieldType == "radio" || fieldType == "checkbox") {
        let div = createInlineRating(fieldType);
        let input = newNode.querySelectorAll(".question-radio").last();
        div.appendChild(input);
        newNode.querySelectorAll(".rating-options")[0].querySelectorAll("dd").last().replaceChildren(div);

        // Reset index
        newNode.querySelector("[id$=_index]").setAttribute("value", 1);
    }

    newNode.lastElementChild.lastElementChild.firstElementChild.replaceChildren(newNode.lastElementChild.lastElementChild.firstElementChild.firstElementChild)
    
    //newNode.querySelectorAll(".question-radio").last().type = fieldType;
    
    parent.appendChild(newNode);
}

function createInlineRating(fieldType) {
    let div = document.createElement("div");
    div.setAttribute("class", "input-group");
    let span = document.createElement("span");
    span.setAttribute("class", "input-group-addon");
    div.appendChild(span);
    let preInput = document.createElement("input");
    if (fieldType == "checkbox") {
        preInput.setAttribute("type", "checkbox");
    } else if (fieldType == "radio") {
        preInput.setAttribute("type", "radio");
    }
    span.appendChild(preInput);
    return div;
}

function formatRatingOption(ratingOption) {
    const fieldType = ratingOption.parentNode.parentNode.parentNode.querySelector(".field-type").value;
    let input = ratingOption.querySelectorAll("input").last();
    let div = createInlineRating(fieldType);
    div.appendChild(input);
    ratingOption.querySelectorAll("dd").last().replaceChildren(div);
}