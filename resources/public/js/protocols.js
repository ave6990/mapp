document.addEventListener("dblclick", (event) => {
  console.log(document.getElementsByClassName("sign_img"))
  const signs = document.getElementsByClassName("sign_img")

  for (const el of signs) {
    el.style.visibility = el.style.visibility == "visible" ? "hidden" : "visible"
  }
})

/*var blurred = false

window.addEventListener('blur', (e) => {
  blurred = true
})

window.addEventListener('focus', (e) => {
  if (blurred) {
    location.reload()
  }
})*/
