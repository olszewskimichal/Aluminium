var url = "/solutionTest/";
var testModalTarget = url + "loadEntity/";
var haslo = "";
var isOpen;
var href = "";
function showTestModal(index, value) {
    isOpen = value;
    var editUrl = testModalTarget + index;

    if (isOpen) {
        $("#spanTest").remove();
        document.getElementById('haslodostepu').style.display = "none";
        $("#labelhaslo").remove();
    }
    loadEntity(editUrl);
}
function loadEntity(url) {
    $.getJSON(url, {}, function (data) {
        populateModal(data);
    });
}
function populateModal(data) {
    $('#nazwaTestu').text(data.name);
    href = "/getSolutionTest?id=" + data.id;
    haslo = data.password;
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
            table.fnAddData([
                counter,
                data[i].user.name + " " + data[i].user.lastName,
                data[i].points,
                '<a href="/solutionTest/' + data[i].id + '">Zobacz</a>'
            ]);
        }
        table.fnSort([[0, 'asc']]);
        $('#wynikiA').modal('show');
    });


}

