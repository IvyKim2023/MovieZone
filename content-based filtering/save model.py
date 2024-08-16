import numpy as np 
import pandas as pd  
import json

import re
from sklearn.feature_extraction.text import TfidfVectorizer

from sklearn.metrics.pairwise import linear_kernel
import joblib


data = pd.read_csv("TMDB_movie_dataset.csv")
data.dropna(subset=['genres','title','overview','keywords'],inplace=True,axis=0)
data = data[data['vote_average'] >= 7.5]

data = data.reset_index(drop=True)

total_rows = len(data)
print(f"Total number of rows: {total_rows}")

data_copy = data.copy()

data['title'] = [re.sub(r'[^\w\s]', '', t) for t in data['title']]
data['genres'] = [re.sub(',',' ',re.sub(' ','',t)) for t in data['genres']]
data['overview'] = [re.sub(r'[^\w\s]', '', t) for t in data['overview']]
data['keywords'] = [re.sub(',',' ',re.sub(' ','',t)) for t in data['keywords']]

data["combined"] = data['title'] + '  ' + data['genres'] + ' ' + data['overview'] + ' ' + data['keywords']
data.drop(['genres','overview','keywords'],axis=1,inplace=True)

# Content Similarity
vectorizer = TfidfVectorizer()
matrix = vectorizer.fit_transform(data["combined"])
cosine_similarities = linear_kernel(matrix,matrix)
movie_title = data['title']
indices = pd.Series(data.index, index=data['title'])

joblib.dump((vectorizer, cosine_similarities, indices, data, data_copy), 'movie_recommender_model.pkl')
print("Model saved successfully.")

