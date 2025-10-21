#!/bin/sh
set -e

ES_URL="http://elasticsearch:9200"
INDEX_NAME="tibetan_docs"

echo "Waiting for Elasticsearch to be ready..."
sleep 10

echo "Creating index with Tibetan analyzer..."
curl -X PUT "$ES_URL/$INDEX_NAME" -H 'Content-Type: application/json' -d '{
  "settings": {
    "number_of_shards": 1,
    "number_of_replicas": 0,
    "analysis": {
      "analyzer": {
        "my_tibetan": {
          "type": "tibetan_analyzer"
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "title": {
        "type": "text",
        "analyzer": "my_tibetan"
      },
      "content": {
        "type": "text",
        "analyzer": "my_tibetan"
      },
      "category": {
        "type": "keyword"
      }
    }
  }
}'

echo "\nIndexing sample documents..."
curl -X POST "$ES_URL/$INDEX_NAME/_bulk" -H 'Content-Type: application/x-ndjson' --data-binary @/sample-data.json

echo "\nSetup completed successfully!"

