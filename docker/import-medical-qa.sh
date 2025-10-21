#!/bin/sh
set -e

ES_URL="http://elasticsearch:9200"
INDEX_NAME="tibetan_medical_qa"

echo "========================================="
echo "Tibetan Medical QA Data Import Script"
echo "========================================="

# Wait for Elasticsearch to be ready
echo "Waiting for Elasticsearch to be ready..."
until curl -s "$ES_URL/_cluster/health" > /dev/null; do 
    echo "Elasticsearch not ready yet, waiting..."
    sleep 2
done

echo "Elasticsearch is ready!"
echo ""

# Check if index already exists
if curl -s -f "$ES_URL/$INDEX_NAME" > /dev/null 2>&1; then
    echo "Index '$INDEX_NAME' already exists. Deleting..."
    curl -X DELETE "$ES_URL/$INDEX_NAME"
    echo ""
fi

# Create index with medical QA-specific mappings
echo "Creating index '$INDEX_NAME' with Tibetan analyzer mappings..."
RESPONSE=$(curl -s -w "\n%{http_code}" -X PUT "$ES_URL/$INDEX_NAME" -H 'Content-Type: application/json' -d '{
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
      "question_number": {
        "type": "keyword"
      },
      "question": {
        "type": "text",
        "analyzer": "my_tibetan",
        "fields": {
          "keyword": {
            "type": "keyword"
          }
        }
      },
      "answer": {
        "type": "text",
        "analyzer": "my_tibetan"
      },
      "category": {
        "type": "keyword"
      },
      "source": {
        "type": "keyword"
      }
    }
  }
}')

HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | head -n-1)

if [ "$HTTP_CODE" = "200" ]; then
    echo "Index created successfully!"
else
    echo "Failed to create index (HTTP $HTTP_CODE)"
    echo "$BODY"
    exit 1
fi

echo ""

# Import data using bulk API
echo "Importing Medical QA data from /tibetan-medical-qa-data.json..."
BULK_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$ES_URL/$INDEX_NAME/_bulk" \
  -H 'Content-Type: application/x-ndjson' \
  --data-binary @/tibetan-medical-qa-data.json)

BULK_HTTP_CODE=$(echo "$BULK_RESPONSE" | tail -n1)
BULK_BODY=$(echo "$BULK_RESPONSE" | head -n-1)

if [ "$BULK_HTTP_CODE" = "200" ]; then
    echo "Data imported successfully!"
    
    # Extract stats from response
    ERRORS=$(echo "$BULK_BODY" | grep -o '"errors":[^,]*' | head -1)
    TOOK=$(echo "$BULK_BODY" | grep -o '"took":[0-9]*' | head -1)
    
    echo "$TOOK ms"
    echo "$ERRORS"
else
    echo "Failed to import data (HTTP $BULK_HTTP_CODE)"
    echo "$BULK_BODY" | head -20
    exit 1
fi

echo ""

# Refresh index
echo "Refreshing index..."
curl -s -X POST "$ES_URL/$INDEX_NAME/_refresh" > /dev/null

# Show final statistics
echo ""
echo "========================================="
echo "Import Statistics"
echo "========================================="

COUNT_RESPONSE=$(curl -s "$ES_URL/$INDEX_NAME/_count")
echo "Document count: $(echo "$COUNT_RESPONSE" | grep -o '"count":[0-9]*' | cut -d: -f2)"

echo ""
echo "Sample categories:"
curl -s -X POST "$ES_URL/$INDEX_NAME/_search?size=0" -H 'Content-Type: application/json' -d '{
  "aggs": {
    "categories": {
      "terms": {
        "field": "category",
        "size": 5
      }
    }
  }
}' | grep -A 3 '"buckets"'

echo ""
echo "========================================="
echo "Import completed successfully!"
echo "========================================="
echo ""
echo "Test queries:"
echo "  curl \"http://localhost:9200/$INDEX_NAME/_search?q=question:སྙིང་གཟེར&size=3\""
echo "  curl \"http://localhost:9200/$INDEX_NAME/_search?q=answer:གསོ་རིག&size=5\""
echo ""

