document.addEventListener('click', function (event) {
    const searchResults = document.getElementById('search-results')

    if (!searchResults.contains(event.target)) {
        while (searchResults.firstChild) {
            searchResults.removeChild(searchResults.firstChild)
        }
    }
})
