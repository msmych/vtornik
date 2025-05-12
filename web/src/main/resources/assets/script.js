document.addEventListener('click', function (event) {
    const searchResults = document.getElementById('search-results')

    if (!searchResults.contains(event.target)) {
        while (searchResults.firstChild) {
            searchResults.removeChild(searchResults.firstChild)
        }
    }
})

function openLoginDialog() {
    const loginDialog = document.getElementById("login-dialog");
    loginDialog.showModal()
}

function openMovieNotesDialog() {
    const loginDialog = document.getElementById("movie-notes-dialog");
    loginDialog.showModal()
}
