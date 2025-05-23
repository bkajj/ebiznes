waiting = false;

async function sendMessage() {
  const chatDiv = document.getElementById("chat");

  if(!waiting)
  {
    const prompt = document.getElementById("prompt").value;
    chatDiv.innerHTML += `<p><b>Ty:</b> ${prompt}</p>`;
    document.getElementById("prompt").value = "";

    waiting = true;
    const res = await fetch("/chat/", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ prompt: prompt })
    });
    const data = await res.json();
    chatDiv.innerHTML += `<p><b>Asystent:</b> ${data.response}</p>`;
    waiting = false;
  }
  else
  {
    alert("Podczekaj na odpowiedź asystenta");
  }
}

document.addEventListener("DOMContentLoaded", function () {
  const input = document.getElementById("prompt");

  input.addEventListener("keydown", function (event) {
    if (event.key === "Enter") {
      event.preventDefault();
      sendMessage();
    }
  });

  sendStartMessage();
});

async function sendStartMessage(text) {
  const res = await fetch("/chat/", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ prompt: "start" })
    });
  const data = await res.json();
  const chatDiv = document.getElementById("chat");
  chatDiv.innerHTML += `<p><b>Asystent:</b> ${data.response}</p>`;
}