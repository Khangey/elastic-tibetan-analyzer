# API Reference

Complete reference for using the Tibetan Analyzer in Elasticsearch.

## Analyzer API

### Test Analyzer

**Endpoint:** `POST /_analyze`

**Request:**
```json
POST /_analyze
{
  "analyzer": "tibetan_analyzer",
  "text": "བཀྲ་ཤིས་བདེ་ལེགས།"
}
```

**Response:**
```json
{
  "tokens": [
    {
      "token": "བཀྲ་ཤིས་བདེ་ལེགས",
      "start_offset": 0,
      "end_offset": 16,
      "type": "word",
      "position": 0
    },
    {
      "token": "།",
      "start_offset": 16,
      "end_offset": 17,
      "type": "word",
      "position": 1
    }
  ]
}
```

## Index Management

### Create Index with Tibetan Analyzer

```json
PUT /tibetan_index
{
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
      "tags": {
        "type": "keyword"
      }
    }
  }
}
```

### Update Index Settings (Requires Reindex)

```json
POST /tibetan_index/_close

PUT /tibetan_index/_settings
{
  "analysis": {
    "analyzer": {
      "my_tibetan": {
        "type": "tibetan_analyzer"
      }
    }
  }
}

POST /tibetan_index/_open
```

### Check Index Settings

```json
GET /tibetan_index/_settings
```

### View Mappings

```json
GET /tibetan_index/_mapping
```

## Document Operations

### Index a Document

```json
POST /tibetan_index/_doc
{
  "title": "བོད་ཀྱི་རིག་གནས།",
  "content": "བོད་ནི་རིག་གནས་ཕྱུག་པའི་རྒྱལ་ཁབ་ཅིག་ཡིན།",
  "tags": ["culture", "tibet"]
}
```

### Index with ID

```json
PUT /tibetan_index/_doc/1
{
  "title": "སློབ་གྲྭ།",
  "content": "སློབ་གྲྭ་ནི་སློབ་སྦྱོང་བྱེད་སའི་གནས།"
}
```

### Get Document

```json
GET /tibetan_index/_doc/1
```

### Update Document

```json
POST /tibetan_index/_update/1
{
  "doc": {
    "tags": ["education", "school"]
  }
}
```

### Delete Document

```json
DELETE /tibetan_index/_doc/1
```

## Search API

### Basic Match Query

```json
GET /tibetan_index/_search
{
  "query": {
    "match": {
      "content": "རིག་གནས"
    }
  }
}
```

### Match with Options

```json
GET /tibetan_index/_search
{
  "query": {
    "match": {
      "content": {
        "query": "རིག་གནས",
        "operator": "and",
        "fuzziness": "AUTO"
      }
    }
  }
}
```

### Multi-Match Query

```json
GET /tibetan_index/_search
{
  "query": {
    "multi_match": {
      "query": "བོད",
      "fields": ["title^2", "content"],
      "type": "best_fields"
    }
  }
}
```

**Multi-match types:**
- `best_fields` (default): Best matching field score
- `most_fields`: Sum of all field scores
- `cross_fields`: Treats fields as one big field
- `phrase`: Match as phrase across fields
- `phrase_prefix`: Prefix match as phrase

### Match Phrase Query

```json
GET /tibetan_index/_search
{
  "query": {
    "match_phrase": {
      "content": "སློབ་གྲྭ་ཆེན་མོ"
    }
  }
}
```

### Bool Query

```json
GET /tibetan_index/_search
{
  "query": {
    "bool": {
      "must": [
        { "match": { "content": "རིག་གནས" }}
      ],
      "should": [
        { "match": { "content": "སྲོལ་རྒྱུན" }}
      ],
      "must_not": [
        { "match": { "content": "དེང་རབས" }}
      ],
      "filter": [
        { "term": { "tags": "culture" }}
      ]
    }
  }
}
```

### Query String

```json
GET /tibetan_index/_search
{
  "query": {
    "query_string": {
      "query": "རིག་གནས AND སྲོལ་རྒྱུན",
      "default_field": "content"
    }
  }
}
```

## Filtering and Sorting

### Filter by Term

```json
GET /tibetan_index/_search
{
  "query": {
    "bool": {
      "filter": [
        { "term": { "tags": "education" }}
      ]
    }
  }
}
```

### Range Filter

```json
GET /tibetan_index/_search
{
  "query": {
    "bool": {
      "filter": [
        {
          "range": {
            "created_date": {
              "gte": "2024-01-01",
              "lte": "2024-12-31"
            }
          }
        }
      ]
    }
  }
}
```

### Sort Results

```json
GET /tibetan_index/_search
{
  "query": {
    "match": {
      "content": "རིག་གནས"
    }
  },
  "sort": [
    { "_score": "desc" },
    { "created_date": "desc" }
  ]
}
```

## Pagination

### From/Size Pagination

```json
GET /tibetan_index/_search
{
  "query": {
    "match_all": {}
  },
  "from": 0,
  "size": 10
}
```

### Search After (Better for Deep Pagination)

```json
GET /tibetan_index/_search
{
  "query": {
    "match_all": {}
  },
  "size": 10,
  "sort": [
    { "_id": "asc" }
  ],
  "search_after": ["previous_doc_id"]
}
```

## Highlighting

### Basic Highlighting

```json
GET /tibetan_index/_search
{
  "query": {
    "match": {
      "content": "རིག་གནས"
    }
  },
  "highlight": {
    "fields": {
      "content": {}
    }
  }
}
```

### Custom Highlighting

```json
GET /tibetan_index/_search
{
  "query": {
    "match": {
      "content": "རིག་གནས"
    }
  },
  "highlight": {
    "fields": {
      "content": {
        "fragment_size": 150,
        "number_of_fragments": 3,
        "pre_tags": ["<strong>"],
        "post_tags": ["</strong>"]
      }
    }
  }
}
```

## Aggregations

### Terms Aggregation

```json
GET /tibetan_index/_search
{
  "size": 0,
  "aggs": {
    "popular_tags": {
      "terms": {
        "field": "tags",
        "size": 10
      }
    }
  }
}
```

### Date Histogram

```json
GET /tibetan_index/_search
{
  "size": 0,
  "aggs": {
    "docs_over_time": {
      "date_histogram": {
        "field": "created_date",
        "calendar_interval": "month"
      }
    }
  }
}
```

### Nested Aggregations

```json
GET /tibetan_index/_search
{
  "size": 0,
  "aggs": {
    "tags": {
      "terms": {
        "field": "tags"
      },
      "aggs": {
        "avg_length": {
          "avg": {
            "script": "doc['content'].value.length()"
          }
        }
      }
    }
  }
}
```

## Bulk Operations

### Bulk Index

```json
POST /_bulk
{ "index": { "_index": "tibetan_index" }}
{ "title": "ཨ", "content": "དང་པོ།" }
{ "index": { "_index": "tibetan_index" }}
{ "title": "བ", "content": "གཉིས་པ།" }
{ "index": { "_index": "tibetan_index" }}
{ "title": "ག", "content": "གསུམ་པ།" }
```

### Bulk Update and Delete

```json
POST /_bulk
{ "update": { "_index": "tibetan_index", "_id": "1" }}
{ "doc": { "tags": ["updated"] }}
{ "delete": { "_index": "tibetan_index", "_id": "2" }}
```

## Admin Operations

### Refresh Index

```json
POST /tibetan_index/_refresh
```

### Flush Index

```json
POST /tibetan_index/_flush
```

### Force Merge

```json
POST /tibetan_index/_forcemerge?max_num_segments=1
```

### Reindex

```json
POST /_reindex
{
  "source": {
    "index": "old_index"
  },
  "dest": {
    "index": "tibetan_index"
  }
}
```

## Plugin Management

### List Plugins

```json
GET /_cat/plugins?v
```

### Check Plugin Info

```json
GET /_nodes/plugins
```

## Monitoring

### Index Stats

```json
GET /tibetan_index/_stats
```

### Cluster Health

```json
GET /_cluster/health
```

### Node Info

```json
GET /_nodes/stats
```

## Error Responses

### Common Error Codes

**400 Bad Request:**
```json
{
  "error": {
    "type": "parsing_exception",
    "reason": "Unknown query type..."
  }
}
```

**404 Not Found:**
```json
{
  "error": {
    "type": "index_not_found_exception",
    "reason": "no such index [tibetan_index]"
  }
}
```

## Response Format

### Search Response Structure

```json
{
  "took": 5,
  "timed_out": false,
  "_shards": {
    "total": 1,
    "successful": 1,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": {
      "value": 10,
      "relation": "eq"
    },
    "max_score": 1.5,
    "hits": [
      {
        "_index": "tibetan_index",
        "_id": "1",
        "_score": 1.5,
        "_source": {
          "title": "...",
          "content": "..."
        }
      }
    ]
  }
}
```

## Performance Tips

1. **Use filters for exact matches** (faster than queries)
2. **Limit `_source` fields** to reduce network transfer
3. **Use `size: 0` for aggregation-only** searches
4. **Enable query caching** for repeated queries
5. **Use scroll API** for large result sets

## See Also

- [Query Examples](./query-examples.md) - Practical search patterns
- [Analyzer Usage](./analyzer-usage.md) - How the analyzer works
- [Quick Start](./quick-start.md) - Getting started guide

