/** This js file contains methods for sending fetch requests instead of reloading the whole page. (better usability) */

/** Execute when js embedded. */
addLikeEventListeners();

/** Unfortunately, needed bc. of security policy. */
function addLikeEventListeners() {
    for (let elem of document.getElementsByClassName('businessLike')) {
        const elemId = elem.id;
        elem.addEventListener('click',function () {
            sendTwitterLike(elemId);
        });
    }
}

function sendTwitterLike(twitterPostId) {
    let formData = new FormData();
    formData.append('postId',twitterPostId);

    fetch("/api/v1/like",
        {
            method: "POST",
            body: formData
        })
        .then((resp) => resp.json())
        .then(function(res) {
            console.log('Submitted like request: '+JSON.stringify(res));
        })
        .catch(function (reason) { console.error('Could not like post: '+JSON.stringify(reason)); });
}