function checkbox(){

    const check = document.getElementById('flexSwitchShowInactive');
    const table = document.getElementById('mainTable');
    if (check.checked) {
        table.querySelectorAll("tr.inactive").forEach(tr => tr.classList.remove("hide-row"));
    } else {
        table.tBodies[0].querySelectorAll("tr.inactive").forEach(tr => tr.classList.add("hide-row"));
    }
}