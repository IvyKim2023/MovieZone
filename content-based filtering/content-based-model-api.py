from flask import Flask, request, jsonify
import numpy as np # linear algebra
import pandas as pd # data processing, CSV file I/O (e.g. pd.read_csv)
import json

# For Text
import re
from sklearn.feature_extraction.text import TfidfVectorizer

# Capture similarity 
from sklearn.metrics.pairwise import linear_kernel

app = Flask(__name__)


import joblib
import json
import pandas as pd

# Load the model components
vectorizer, cosine_similarities, indices, data, data_copy = joblib.load('movie_recommender_model.pkl')

def content_recommender(titles):
    
    # Collect indices for all input titles
    indices_list = [indices[title] for title in titles if title in indices]

    # Collect similarity scores for all input titles
    sim_scores = []
    for idx in indices_list:
        sim_scores.extend(list(enumerate(cosine_similarities[idx])))

    print(sim_scores)


    sim_scores_agg = {}
    for i, score in sim_scores:
        if isinstance(score, np.ndarray):
            for j in range(len(score)):
                if j not in sim_scores_agg:
                    sim_scores_agg[j] = 0  
                sim_scores_agg[j] += score[j]
        else:
            if i in sim_scores_agg:
                sim_scores_agg[i] += score
            else:
                sim_scores_agg[i] = score

    print(sim_scores_agg)

    # Sort the movies by aggregated similarity score
    sim_scores_agg = sorted(sim_scores_agg.items(), key=lambda x: x[1], reverse=True)

    # Select top 40 movies (excluding the input movies)
    sim_scores_agg = sim_scores_agg[1:41]
    movie_indices = [i[0] for i in sim_scores_agg]
    # print(movie_indices)

    # Return the titles of the recommended movies
    recommended_movies = data_copy.iloc[movie_indices]
    recommended_movies = recommended_movies[recommended_movies['vote_average'] > 8]

    result = recommended_movies.to_dict(orient='records')

    return result


@app.route('/predict', methods=['POST'])
def predict():
    data = request.json
    print("Received data:", data)
    titles = data.get('titles', [])
    recommendations = content_recommender(titles)
    return jsonify(recommendations)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8090, debug=True)