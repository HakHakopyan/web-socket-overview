var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#process_request").show();
        $("#request_answer").show();
        $("#request_error").show();
    } else {
        $("#process_request").hide();
        $("#request_answer").hide();
        $("#request_error").hide();
    }
    $("#process").html("");
    $("#answer").html("");
    $("#error").html("");
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
        stompClient.subscribe('/queue/process', function (regProcessInfo) {
            showRegProcessInfo(regProcessInfo.body);
        });
        stompClient.subscribe('/topic/answer', function (answer) {
            $("#answer").append("<tr><td>" + answer.body + "</td></tr>");
        });
        stompClient.subscribe('/queue/error', function (message) {
            $("#error").append("<tr><td>" + message.body + "</td></tr>");
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

function sendNameInParam() {
    stompClient.send("/app/register/" + $("#name").val() + "/param");
}

$(function () {
    $("form").on('submit', function (e) { e.preventDefault(); });

    $("#connect").click(function() { connect(); });
    $("#disconnect").click(function() { disconnect(); });

    $("#send_in_body").click(function() { sendNameInBody(); });
    $("#send_in_param").click(function() { sendNameInParam(); });
});
