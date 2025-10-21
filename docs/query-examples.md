# Query Examples for Kibana Dev Tools

All examples below can be directly copy-pasted into Kibana Dev Tools Console.

## 📊 Basic Statistics

### Count Documents in Each Index

```json
GET /tibetan_docs/_count
GET /tibetan_qa/_count
GET /tibetan_medical_qa/_count
```

### View Index Mappings

```json
GET /tibetan_qa/_mapping
```

## 🔍 Simple Search Queries

### 1. Search by Single Word

**Search for "school" (སློབ་གྲྭ) in questions:**

```json
GET /tibetan_qa/_search
{
  "query": {
    "match": {
      "question": "སློབ་གྲྭ"
    }
  },
  "size": 3,
  "_source": ["question", "answer"]
}
```

### 2. Search Across Multiple Fields

**Search for "medicine" in both questions and answers:**

```json
GET /tibetan_medical_qa/_search
{
  "query": {
    "multi_match": {
      "query": "གསོ་རིག",
      "fields": ["question", "answer"]
    }
  },
  "size": 3
}
```

### 3. Search All Text Fields

**Search for "Tibet" across title, content, question, and answer:**

```json
GET /tibetan_qa/_search
{
  "query": {
    "multi_match": {
      "query": "བོད",
      "fields": ["title", "content", "question", "answer"]
    }
  },
  "size": 5,
  "_source": ["title", "question"]
}
```

## 🎯 Phrase Search

### Exact Phrase Match

**Search for exact phrase "heart disease" (སྙིང་གཟེར་ནད):**

```json
GET /tibetan_medical_qa/_search
{
  "query": {
    "match_phrase": {
      "question": "སྙིང་གཟེར་ནད"
    }
  },
  "size": 3
}
```

## 🔗 Boolean Queries

### Must Match (AND Logic)

**Documents must contain both "culture" AND "tradition":**

```json
GET /tibetan_qa/_search
{
  "query": {
    "bool": {
      "must": [
        { "match": { "content": "རིག་གནས" }},
        { "match": { "content": "སྲོལ་རྒྱུན" }}
      ]
    }
  },
  "size": 3
}
```

### Should Match (OR Logic)

**Documents containing "food" OR "clothing":**

```json
GET /tibetan_qa/_search
{
  "query": {
    "bool": {
      "should": [
        { "match": { "content": "ཟས" }},
        { "match": { "content": "གོས" }}
      ],
      "minimum_should_match": 1
    }
  },
  "size": 5
}
```

### Must Not (Exclusion)

**Documents about medicine but NOT about heart:**

```json
GET /tibetan_medical_qa/_search
{
  "query": {
    "bool": {
      "must": [
        { "match": { "answer": "གསོ་རིག" }}
      ],
      "must_not": [
        { "match": { "question": "སྙིང" }}
      ]
    }
  },
  "size": 3
}
```

## 🌟 Highlighting Results

### Highlight Matched Terms

**Show where the search term appears in the text:**

```json
GET /tibetan_qa/_search
{
  "query": {
    "match": {
      "content": "རིག་གནས"
    }
  },
  "highlight": {
    "fields": {
      "content": {}
    },
    "pre_tags": ["<em>"],
    "post_tags": ["</em>"]
  },
  "size": 2,
  "_source": ["title", "question"]
}
```

## 📈 Aggregations

### Count by Category

```json
GET /tibetan_qa/_search
{
  "size": 0,
  "aggs": {
    "popular_topics": {
      "terms": {
        "field": "category",
        "size": 10
      }
    }
  }
}
```

### Statistics on Text Length

```json
GET /tibetan_medical_qa/_search
{
  "size": 0,
  "aggs": {
    "question_stats": {
      "stats": {
        "script": {
          "source": "doc['question.keyword'].value.length()"
        }
      }
    }
  }
}
```

## 🎨 Sorting Results

### Sort by Question Number (Medical QA)

```json
GET /tibetan_medical_qa/_search
{
  "query": {
    "match_all": {}
  },
  "sort": [
    {
      "question_number": {
        "order": "asc"
      }
    }
  ],
  "size": 5
}
```

## 🔎 Fuzzy Search

### Allow Spelling Variations

```json
GET /tibetan_qa/_search
{
  "query": {
    "match": {
      "question": {
        "query": "སློབ",
        "fuzziness": "AUTO"
      }
    }
  },
  "size": 3
}
```

## 📚 Real-World Example Queries

### Example 1: Search for Traditional Arts

```json
GET /tibetan_qa/_search
{
  "query": {
    "multi_match": {
      "query": "རིག་གནས་སྲོལ་རྒྱུན",
      "fields": ["title^2", "content", "question"],
      "type": "best_fields"
    }
  },
  "highlight": {
    "fields": {
      "content": {},
      "question": {}
    }
  },
  "size": 5,
  "_source": ["title", "question", "answer"]
}
```

### Example 2: Medical Symptoms Search

```json
GET /tibetan_medical_qa/_search
{
  "query": {
    "bool": {
      "should": [
        { "match": { "question": "ནད་རྟགས" }},
        { "match": { "answer": "མངོན་རྟགས" }}
      ]
    }
  },
  "size": 5,
  "_source": ["question_number", "question", "answer"]
}
```

### Example 3: Find Definitions

```json
GET /tibetan_qa/_search
{
  "query": {
    "bool": {
      "should": [
        { "match_phrase": { "question": "གོ་དོན་གང་ཡིན" }},
        { "match_phrase": { "question": "ཅི་ཡིན" }}
      ]
    }
  },
  "size": 5,
  "_source": ["question", "answer"]
}
```

## 💡 Tips for Better Searches

1. **Use `_source` to limit returned fields:**
   ```json
   "_source": ["title", "question"]
   ```

2. **Boost important fields with `^2`:**
   ```json
   "fields": ["title^2", "content"]
   ```

3. **Use `size` to control result count:**
   ```json
   "size": 10
   ```

4. **Combine filters for precise results:**
   ```json
   "bool": { "must": [...], "filter": [...] }
   ```

## 🔄 Pagination

### Get Next Page of Results

```json
GET /tibetan_qa/_search
{
  "query": {
    "match": {
      "content": "བོད"
    }
  },
  "from": 10,
  "size": 10
}
```

## 🛠️ Testing the Analyzer

### Analyze Different Text

```json
POST /_analyze
{
  "analyzer": "tibetan_analyzer",
  "text": "ང་ཚོས་སློབ་གྲྭར་འགྲོ་དགོས།"
}
```

### Compare with Standard Analyzer

```json
POST /_analyze
{
  "analyzer": "standard",
  "text": "བཀྲ་ཤིས་བདེ་ལེགས།"
}
```

## 📋 Copy-Paste Test Set

Here are 5 ready-to-test queries (just paste one at a time):

```json
# 1. Medical search
GET /tibetan_medical_qa/_search
{"query": {"match": {"question": "སྙིང་གཟེར"}}, "size": 3}

# 2. Multi-field search
GET /tibetan_qa/_search
{"query": {"multi_match": {"query": "བོད", "fields": ["title", "content"]}}, "size": 5}

# 3. Phrase search
GET /tibetan_qa/_search
{"query": {"match_phrase": {"content": "སློབ་གྲྭ"}}, "size": 3}

# 4. Highlighted search
GET /tibetan_qa/_search
{"query": {"match": {"content": "རིག་གནས"}}, "highlight": {"fields": {"content": {}}}, "size": 2}
```

