console.log("Das work");

var ws;

function init() {
    ws = new WebSocket("ws://localhost:8080/chat");
    ws.onopen = function (event) {

    };
    ws.onmessage = function (event) {
        //var $textarea = document.getElementById("messages");
        //$textarea.value = $textarea.value + event.data + "\n";
        console.log("Return: " + event.data)
    };
    ws.onclose = function (event) {

    }
};

function sendMessage(message) {
    //var messageField = document.getElementById("message");
    //var userNameField = document.getElementById("username");
    //var message = userNameField.value + ":" + messageField.value;
    ws.send(message);
    //messageField.value = '';
}