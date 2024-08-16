(function() {

    var user_id = null;
 

    function init() {
        document.getElementById('login-form').addEventListener('submit', function(event) {
        event.preventDefault();
        var userId = document.getElementById('user-id').value;
        var password = document.getElementById('password').value;
        verifyLogin(userId, password);
    	});   
    }



    
    function activeBtn(btnId) {
        var btns = document.getElementsByClassName('main-nav-btn');

        // deactivate all navigation buttons
        for (var i = 0; i < btns.length; i++) {
            btns[i].className = btns[i].className.replace(/\bactive\b/, '');
        }

        // active the one that has id = btnId
        var btn = $(btnId);
        btn.className += ' active';
    }

    function showLoadingMessage(msg) {
        var movieList = $('movie-list');
        movieList.innerHTML = '<p class="notice"><i class="fa fa-spinner fa-spin"></i> ' +
            msg + '</p>';
    }

    function showWarningMessage(msg) {
        var movieList = $('movie-list');
        movieList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-triangle"></i> ' +
            msg + '</p>';
    }

    function showErrorMessage(msg) {
        var movieList = $('movie-list');
        movieList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-circle"></i> ' +
            msg + '</p>';
    }


    function hideElement(element) {
        element.style.display = 'none';
    }

    function showElement(element, style) {
        var displayStyle = style ? style : 'block';
        element.style.display = displayStyle;
    }

   
    function ajax(method, url, data, callback, errorHandler) {
        var xhr = new XMLHttpRequest();

        xhr.open(method, url, true);

        xhr.onload = function() {
        	if (xhr.status === 200) {
        		callback(xhr.responseText);
        	} else {
        		errorHandler();
        	}
        };

        xhr.onerror = function() {
            console.error("The request couldn't be completed.");
            errorHandler();
        };

        if (data === null) {
            xhr.send();
        } else {
            xhr.setRequestHeader("Content-Type",
                "application/json;charset=utf-8");
            xhr.send(data);
        }
    }

    
    function verifyLogin(userId, password) {
        var url = './login';
        var params = 'user_id=' + userId + "&" + 'password=' + password;
        var req = JSON.stringify({});
        ajax('POST', url + '?' + params, req, function(res) {
            var result = JSON.parse(res);
            if (result.success) {
                localStorage.setItem('user_id', userId);
                window.location.href = 'home.html';
            } else {
                document.getElementById('login-error').innerText = 'Invalid user ID or password';
            }
        }, function() {
            document.getElementById('login-error').innerText = 'Error logging in';
        });
    }


    	
    init();

})();
