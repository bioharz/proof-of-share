var tbody = document.querySelector('tbody');

if (tbody) {
    tbody.addEventListener('click', function(e) {
        if (e.target.dataset.action === 'delete') {
            var confirmDelete = confirm("Do you want to remove the campaign?");

            if (confirmDelete) {
                fetch('/tweet/' + e.target.dataset.id, {
                    method: 'DELETE',
                    credentials: 'include',
                    headers: {
                        'content-type': 'application/x-www-form-urlencoded'
                    }
                }).then(function() {
                    location.reload();
                });
            }
        }
    });
}