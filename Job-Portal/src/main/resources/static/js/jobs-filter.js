(function () {
  var input = document.getElementById("tableFilter");
  var table = document.getElementById("jobsTable");
  if (!input || !table) return;

  var rows = table.querySelectorAll("tbody tr");

  function norm(s) {
    return (s || "").toLowerCase().trim();
  }

  input.addEventListener("input", function () {
    var q = norm(input.value);
    rows.forEach(function (row) {
      var text = norm(row.textContent);
      row.style.display = !q || text.indexOf(q) !== -1 ? "" : "none";
    });
  });
})();
