document.addEventListener('click', function (event) {
    const searchResults = document.getElementById('search-results')

    if (!searchResults.contains(event.target)) {
        while (searchResults.firstChild) {
            searchResults.removeChild(searchResults.firstChild)
        }
    }
})

function openDialog(id) {
    const dialog = document.getElementById(id);
    dialog.showModal()
}

function closeDialog(id) {
    const dialog = document.getElementById(id);
    dialog.close()
}
