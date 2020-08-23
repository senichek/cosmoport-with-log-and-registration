function clickCreate() {
    let elem = document.getElementById("createButton");
    if (elem.style.display === "none") {
        elem.style.display = "block";
    } else {
        elem.style.display = "none"
    }
}

function post(requestUrl, body) {
    let Httpreq = new XMLHttpRequest(); // a new request
    Httpreq.open("POST", requestUrl, false);
    Httpreq.setRequestHeader("Content-type", "application/json;charset=UTF-8");
    Httpreq.send(body);
    if (Httpreq.status === 400) {
        $('#error-text').text("Bad request to POST " + requestUrl);
        $('#myModal').modal('show');
    }
    if (Httpreq.status === 404) {
        $('#error-text').text("Not found POST " + requestUrl);
        $('#myModal').modal('show');
    }
    return Httpreq;
}

function processCreate(root) {

    let body = {};
    body.username = document.getElementById("inputUsernameNew").value;
    body.password = document.getElementById("inputPasswordNew").value;


    let response = post(root + "/rest/login/", JSON.stringify(body));
    if (response.status === 200) {
        document.getElementById("inputUsernameNew").value = "";
        document.getElementById("inputPasswordNew").value = "";

        if (document.getElementById("inlineRadioNew2").checked) {
            document.getElementById("inlineRadioNew2").checked = false;
            document.getElementById("inlineRadioNew1").checked = true;
        }

    }

}

    
        
     