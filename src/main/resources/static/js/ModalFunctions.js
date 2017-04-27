var url = "/solutionTest/";
var testModalTarget = url + "loadEntity/";
var haslo = "";
var isOpen;
var href = "";
var messageFAQ = "";
function showTestModal(index, value) {
    isOpen = value;
    var editUrl = testModalTarget + index;

    if (isOpen) {
        $("#spanTest").remove();
        document.getElementById('haslodostepu').style.display = "none";
        $("#labelhaslo").remove();
    } else
        document.getElementById('haslodostepu').style.display = "inline";
    loadEntity(editUrl);
}


function loadEntity(url) {
    $.getJSON(url, {}, function (data) {
        populateModal(data);
    });
}
function showReopen(index, name) {
    $('#nazwaTestu2').text(name);
    id = index;
    var today = new Date();
    var dd = today.getDate();
    var mm = today.getMonth() + 1; //January is 0!
    var yyyy = today.getFullYear();
    if (dd < 10) {
        dd = '0' + dd
    }
    if (mm < 10) {
        mm = '0' + mm
    }
    today = yyyy + '-' + mm + '-' + dd;
    document.getElementById("dataZamkniecia").value = today;
    $('#Reopen').modal('show');
}
function changeDate(date) {
    href = "/setTestDate?id=" + id + "&date=" + date.value;

    document.getElementById('form_reopen').setAttribute("action", href);
}
function populateModal(data) {
    $('#nazwaTestu').text(data.name);
    href = "/getSolutionTest?id=" + data.id;
    haslo = data.password;
    messageFAQ = data.messageFAQ;
    $('#messageFAQ').text(messageFAQ);
}
function checkPassword(pass) {
    if (!isOpen) {
        if (haslo == pass.value) {
            href = href + "&pass=" + pass.value;
            document.getElementById('form_test').setAttribute("action", href)
        } else alert("Złe hasło!");
    }
    else {
        document.getElementById('form_test').setAttribute("action", href);
    }
}

//======== NIZEJ DO /test/all
function showAttemptsModal(index, name) {
    var editUrl = "/solutionTest/loadResultEntity/" + index;
    $('#nazwaTestu').text(name);
    var table = $('#tabelaaa').dataTable();
    table.fnClearTable();
    var counter = 0;
    var lastPoints = -1;
    $.getJSON(editUrl, {}, function (data) {
        for (var i in data) {
            if (data[i].points != lastPoints) {
                lastPoints = data[i].points;
                counter++;
            }
            var year = data[i].endSolution.year;
            var day = data[i].endSolution.dayOfMonth;
            var month = data[i].endSolution.monthValue;
            var hours = data[i].endSolution.hour;
            var minutes = data[i].endSolution.minute;
            var seconds = data[i].endSolution.second;
            var percent = parseFloat(data[i].points / data[i].test.maxPoints * 100);

            if (day < 10)
                day = '0' + day;
            if (month < 10)
                month = '0' + month;
            if (hours < 10)
                hours = '0' + hours;
            if (minutes < 10)
                minutes = '0' + minutes;
            if (seconds < 10)
                seconds = '0' + seconds;

            table.fnAddData([
                counter,
                data[i].user.name + " " + data[i].user.lastName,
                data[i].points + "/" + data[i].test.maxPoints,
                percent.toPrecision(4) + '%',
                day + '/' + month + '/' + year + ' ' + hours + ':' + minutes + ':' + seconds,
                '<a href="/solutionTest/' + data[i].id + '">Zobacz</a>']);
        }
        table.fnSort([[0, 'asc']]);
        $('#wynikiA').modal('show');
    });


}

