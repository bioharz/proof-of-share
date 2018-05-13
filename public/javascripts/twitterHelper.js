/** This js file contains methods for sending fetch requests instead of reloading the whole page. (better usability) */

/** Execute when js embedded. */
addLikeEventListeners();

/** Unfortunately, needed bc. of security policy. */
function addLikeEventListeners() {
    for (var elem of document.getElementsByClassName('businessLike')) {
        elem.addEventListener('click',sendTwitterLike(elem.id));
    }
}

function sendTwitterLike(twitterPostId) {
    var formData = new FormData();
    formData.append('postId',twitterPostId);

    fetch("/api/v1/like",
        {
            method: "POST",
            body: formData
        })
        .then(function(res) {
            console.log('Submitted like request: '+res);
        })
        .catch(function (reason) { console.error('Could not like post: '+reason); });
}