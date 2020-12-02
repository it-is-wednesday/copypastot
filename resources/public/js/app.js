/** yoinked from https://stackoverflow.com/a/30810322/1471460 */
function copyTextToClipboard(text) {
  var textArea = document.createElement("textarea");
  textArea.value = text;

  // Avoid scrolling to bottom
  textArea.style.top = "0";
  textArea.style.left = "0";
  textArea.style.position = "fixed";

  document.body.appendChild(textArea);
  textArea.focus();
  textArea.select();

  try {
    var successful = document.execCommand('copy');
    var msg = successful ? 'successful' : 'unsuccessful';
    console.log('Fallback: Copying text command was ' + msg);
  } catch (err) {
    console.error('Fallback: Oops, unable to copy', err);
  }

  document.body.removeChild(textArea);
}

function copyCurrentlyViewedPasta() {
  const pasta = document.querySelector(".pasta-content").textContent;
  copyTextToClipboard(pasta);

  // turn the copy button into green and pretty
  const buttonCopy = document.querySelector("#btn-copy");
  buttonCopy.classList.remove("btn-danger");
  buttonCopy.classList.add("btn-success");
  buttonCopy.textContent = "העתקתי 💋";
}

document.addEventListener('DOMContentLoaded', function() {
  document.body.addEventListener("click", function(e) {
    var el = e.target;
    var confirmMessage = el.getAttribute("data-confirm");

    if(!!confirmMessage) {
      e.preventDefault();

      if(confirmMessage && confirm(confirmMessage)) {
        el.closest('form').submit();
      }
    }
  });
});
