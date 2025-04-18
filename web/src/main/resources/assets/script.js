function closePane(id) {
    const el = document.getElementById(id)
    while (el.firstChild) {
        el.removeChild(el.firstChild)
    }
}
