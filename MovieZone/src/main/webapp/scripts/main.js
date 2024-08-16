(function() {


    var user_id = localStorage.getItem('user_id');


    function init() {
        $('nearby-btn').addEventListener('click', loadTrendingMovies);
	   	$('search-submit').addEventListener('click', searchMovies);
	    $('search-input').addEventListener('keydown', function(event) {
	        if (event.key === 'Enter') {
	            searchMovies(); 
	        }
	    });
	    $('fav-btn').addEventListener('click', loadLikedMovies);
	    $('recommend-btn').addEventListener('click', loadRecommendedMovies);
	
	    var welcomeMsg = document.getElementById('welcome-msg');
	    welcomeMsg.innerHTML = 'Welcome to MovieZone, ' + user_id + '!';
	    
	    loadTrendingMovies();
        
    }


    function activeBtn(btnId) {
        var btns = document.getElementsByClassName('main-nav-btn');

        for (var i = 0; i < btns.length; i++) {
            btns[i].className = btns[i].className.replace(/\bactive\b/, '');
        }

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
    
    function $(tag, options) {
        if (!options) {
            return document.getElementById(tag);
        }

        var element = document.createElement(tag);

        for (var option in options) {
            if (options.hasOwnProperty(option)) {
                element[option] = options[option];
            }
        }

        return element;
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

    
    function loadTrendingMovies() {
        console.log('loadTrendingMovies');
        activeBtn('nearby-btn');

        // The request parameters
        var url = './trending';
        var params = 'user_id=' + user_id;
        var req = JSON.stringify({});

        showLoadingMessage('Loading trending movies...');

        ajax('GET', url + '?' + params, req,
            // successful callback
            function(res) {
                var movies = JSON.parse(res);
                if (!movies || movies.length === 0) {
                    showWarningMessage('No trending movie.');
                } else {
                    listMovies(movies);
                }
            },
            function() {
                showErrorMessage('Cannot load trending movies.');
            });
    }

   
   
    function searchMovies() {
        console.log('searchMovies');
        activeBtn('search-btn');

        // Get search query
        var query = $('search-input').value;

        if (query === '') {
            showWarningMessage('Please enter a movie name.');
            return;
        }

        var url = './search';
        var params = 'term=' + encodeURIComponent(query) + "&user_id=" + user_id;

        showLoadingMessage('Searching for movies...');

        ajax('GET', url + '?' + params, null,
            function(res) {
                var movies = JSON.parse(res);
                if (!movies || movies.length === 0) {
                    showWarningMessage('No movie found.');
                } else {
                    listMovies(movies);
                }
            },
            function() {
                showErrorMessage('Cannot search movies.');
            });
    }
   
   

    function loadLikedMovies() {
        activeBtn('fav-btn');
        var url = './liked';
        var params = 'user_id=' + user_id;
        var req = JSON.stringify({});
        showLoadingMessage('Loading liked movies...');

        ajax('GET', url + '?' + params, req, function(res) {
            var movies = JSON.parse(res);
            if (!movies || movies.length === 0) {
                showWarningMessage('No liked movie.');
            } else {
                listMovies(movies);
            }
        }, function() {
            showErrorMessage('Cannot load liked movies.');
        });
    }
    
    
    function loadRecommendedMovies() {
        activeBtn('recommend-btn');

        var url = './recommend';
        var params = 'user_id=' + user_id;
        var req = JSON.stringify({});

        showLoadingMessage('Loading recommended movies...');

        ajax('GET', url + '?' + params, req, function(res) {
            var movies = JSON.parse(res);
            if (!movies || movies.length === 0) {
                showWarningMessage('No recommended movies.');
            } else {
                listMovies(movies);
            }
        }, function() {
            showErrorMessage('Cannot load recommended movies.');
        });
    }
   
   
   
    function changeFavoriteMovie(movie_id) {
        var li = $('movie-' + movie_id);
        var favIcon = $('fav-icon-' + movie_id);
        var liked = li.dataset.liked !== 'true';

        var url = './liked';
        var req = JSON.stringify({
            user_id: user_id,
            liked: [movie_id]
        });
        var method = liked ? 'POST' : 'DELETE';

        ajax(method, url, req,
            function(res) {
                var result = JSON.parse(res);
                if (result.result === 'SUCCESS') {
                    li.dataset.liked = liked;
                    favIcon.className = liked ? 'fa fa-heart' : 'fa fa-heart-o';
                }
            });
    }
   
   
    function listMovies(movies) {
        // Clear the current results
        var movieList = $('movie-list');
        movieList.innerHTML = '';

        for (var i = 0; i < movies.length; i++) {
            addMovie(movieList, movies[i]);
        }
    }




    
    function addMovie(movieList, movie) {
        var movie_id = movie.movie_id;

        var li = $('li', {
            id: 'movie-' + movie_id,
            className: 'movie'
        });

        li.dataset.movie_id = movie_id;
        li.dataset.watched = movie.watched;
       
        if (movie.poster_url) {
            li.appendChild($('img', {
                src: movie.poster_url,
                style: 'height:250px; width:180px; object-fit:cover;'
            }));
        } else {
            li.appendChild($(
                'img', {
                    src: 'https://assets-cdn.github.com/images/modules/logos_page/GitHub-Mark.png',
                    style: 'height:250px; width:180px; object-fit:cover;'
                }))
        }
        var section = $('div', {});

        var title = $('a', {
            href: movie.poster_url,
            target: '_blank',
            className: 'movie-title'
        });
        title.innerHTML = movie.title;
        section.appendChild(title);
        
	    var ratingContainer = $('div', {
	        className: 'movie-rating'
	    });
	    var star = $('i', {
	        className: 'fa fa-star'
	    });
	    ratingContainer.appendChild(star);
	    var rating = $('p', {});
	    rating.innerHTML = movie.rating;
	    ratingContainer.appendChild(rating);
	    section.appendChild(ratingContainer);
	    
	    var genre = $('p', {
	        className: 'movie-genre'
	    });
	    genre.innerHTML = 'Genres: ' + movie.genres.join(', ');
	    section.appendChild(genre);
	
	    var releaseDate = $('p', {
	        className: 'movie-release-date'
	    });
	    releaseDate.innerHTML = 'Release Date: ' + movie.release_date;
	    section.appendChild(releaseDate);
	    
	    var overview = $('p', {
	        className: 'movie-overview'
	    });
	    overview.innerHTML = movie.overview;
	    section.appendChild(overview);

	    li.appendChild(section);
	
	    var favLink = $('p', {
	        className: 'fav-link'
	    });
	    favLink.onclick = function() {
	        changeFavoriteMovie(movie_id);
	    };
	    favLink.appendChild($('i', {
	        id: 'fav-icon-' + movie_id,
	        className: movie.liked ? 'fa fa-heart' : 'fa fa-heart-o'
	    }));
	    li.appendChild(favLink);
	
	    movieList.appendChild(li);
    }

    	
    init();

})();
