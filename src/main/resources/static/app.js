var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
}

function connect() {
    let socket = new SockJS('/websocket-overview');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);

        stompClient.subscribe('/app/ping', function (response) {});

        stompClient.subscribe('/topic/process', function (regProcessInfo) {
            showRegProcessInfo(regProcessInfo.body);
        });
    });
}

function showRegProcessInfo(content) {
    $("#process").append("<tr><td>" + content + "</td></tr>");
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendNameInBody() {
    stompClient.send("/app/register/body", {}, JSON.stringify({'name': $("#name").val()}));
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });

    $( "#send_in_body" ).click(function() { sendNameInBody(); });
});
