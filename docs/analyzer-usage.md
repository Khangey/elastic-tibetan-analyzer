# Analyzer Usage Guide

## Understanding the Tibetan Analyzer

The Tibetan analyzer uses a **dictionary-based longest-match algorithm** to segment Tibetan text into meaningful words.

### How It Works

1. **Normalization**: Cleans and prepares the text
2. **Syllable Extraction**: Identifies Tibetan syllables (separated by tsek `་`)
3. **Dictionary Lookup**: Searches for the longest matching words
4. **Suffix Handling**: Processes grammatical particles and suffixes

## Testing the Analyzer

### Basic Analysis

```json
POST /_analyze
{
  "analyzer": "tibetan_analyzer",
  "text": "བཀྲ་ཤིས་བདེ་ལེགས།"
}
```

**Result:**
```json
{
  "tokens": [
    {
      "token": "བཀྲ་ཤིས་བདེ་ལེགས",
      "type": "word",
      "position": 0
    },
    {
      "token": "།",
      "type": "word",
      "position": 1
    }
  ]
}
```

### More Examples

#### Example 1: Simple Sentence

```json
POST /_analyze
{
  "analyzer": "tibetan_analyzer",
  "text": "ང་སློབ་གྲྭར་འགྲོ།"
}
```

**Tokens:** `ང`, `སློབ་གྲྭ`, `ར`, `འགྲོ`, `།`

#### Example 2: With Particles

```json
POST /_analyze
{
  "analyzer": "tibetan_analyzer",
  "text": "ང་ཚོས་བོད་སྐད་སློབ་སྦྱོང་བྱེད་བཞིན་ཡོད།"
}
```

**Notice:** Grammatical particles (ས, ར, etc.) are separated correctly.

#### Example 3: Medical Terms

```json
POST /_analyze
{
  "analyzer": "tibetan_analyzer",
  "text": "སྙིང་གཟེར་ནད་ཀྱི་བཅོས་ཐབས།"
}
```

## Using in Index Settings

### Method 1: Global Analyzer (Simple)

```json
PUT /my_tibetan_index
{
  "settings": {
    "analysis": {
      "analyzer": {
        "default": {
          "type": "tibetan_analyzer"
        }
      }
    }
  }
}
```

### Method 2: Custom Named Analyzer

```json
PUT /my_tibetan_index
{
  "settings": {
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
      }
    }
  }
}
```

### Method 3: Multi-Analyzer Setup

```json
PUT /my_tibetan_index
{
  "settings": {
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
        "analyzer": "my_tibetan",
        "fields": {
          "keyword": {
            "type": "keyword"
          },
          "english": {
            "type": "text",
            "analyzer": "english"
          }
        }
      }
    }
  }
}
```

## Index Your Own Data

### Create Index

```json
PUT /my_docs
{
  "settings": {
    "number_of_shards": 1,
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
      "author": {
        "type": "keyword"
      },
      "date": {
        "type": "date"
      }
    }
  }
}
```

### Index a Document

```json
POST /my_docs/_doc
{
  "title": "བོད་ཀྱི་རིག་གནས།",
  "content": "བོད་ནི་རིག་གནས་ཕྱུག་པའི་རྒྱལ་ཁབ་ཅིག་ཡིན།",
  "author": "མིང་མེད།",
  "date": "2024-01-01"
}
```

### Search It

```json
GET /my_docs/_search
{
  "query": {
    "match": {
      "content": "རིག་གནས"
    }
  }
}
```

## Bulk Indexing

### Prepare NDJSON File

Create `data.ndjson`:
```json
{"index": {"_index": "my_docs"}}
{"title": "སློབ་གྲྭ།", "content": "སློབ་གྲྭ་ནི་སློབ་སྦྱོང་བྱེད་སའི་གནས་ཡིན།"}
{"index": {"_index": "my_docs"}}
{"title": "གསོ་རིག", "content": "གསོ་རིག་ནི་ནད་གཞི་སེལ་བའི་ཐབས་ལམ་ཡིན།"}
```

### Import via API

```json
POST /_bulk
{ "index": { "_index": "my_docs" }}
{ "title": "སློབ་གྲྭ།", "content": "སློབ་གྲྭ་ནི་སློབ་སྦྱོང་བྱེད་སའི་གནས་ཡིན།" }
{ "index": { "_index": "my_docs" }}
{ "title": "གསོ་རིག", "content": "གསོ་རིག་ནི་ནད་གཞི་སེལ་བའི་ཐབས་ལམ་ཡིན།" }
```

## Analyzer Performance

### Dictionary Size

- **Total Entries**: ~65,000+ words
- **Categories**: 
  - 1-syllable words
  - 2-syllable words
  - 3-syllable words
  - 4-syllable words
  - Verbs
  - Particles
  - Custom words

### Tokenization Speed

Typical performance:
- **Short text** (< 100 syllables): < 1ms
- **Medium text** (100-500 syllables): 1-5ms
- **Long text** (500+ syllables): 5-20ms

## Common Issues and Solutions

### Issue 1: Text Not Segmented

**Problem:** Text appears as single tokens
```json
POST /_analyze
{
  "analyzer": "tibetan_analyzer",
  "text": "test"
}
```

**Solution:** Ensure text is in Tibetan Unicode (U+0F00 - U+0FFF)

### Issue 2: Over-Segmentation

**Problem:** Words split into individual syllables

**Cause:** Word not in dictionary

**Solution:** Add to custom dictionary at `src/main/resources/dictionaries/custom_dictionary.txt`

### Issue 3: Wrong Segmentation

**Example:**
```
Input:  སློབ་གྲྭ་ཆེན་མོ
Output: སློབ་གྲྭ | ཆེན | མོ
Want:   སློབ་གྲྭ་ཆེན་མོ
```

**Solution:** Add the full phrase to custom dictionary

## Advanced: Custom Dictionary

### Add Custom Words

1. Edit: `src/main/resources/dictionaries/custom_dictionary.txt`
2. Add one word per line:
   ```
   སློབ་གྲྭ་ཆེན་མོ
   དཔེ་མཛོད་ཁང
   རིག་གནས་ཁང
   ```
3. Rebuild plugin:
   ```bash
   ./gradlew clean build
   ```
4. Reinstall plugin

### Test Custom Words

```json
POST /_analyze
{
  "analyzer": "tibetan_analyzer",
  "text": "སློབ་གྲྭ་ཆེན་མོ"
}
```

## Comparison with Other Analyzers

### Standard Analyzer (Not Good for Tibetan)

```json
POST /_analyze
{
  "analyzer": "standard",
  "text": "བཀྲ་ཤིས་བདེ་ལེགས།"
}
```

**Result:** Splits by tsek `་`, loses word meaning

### Tibetan Analyzer (Correct)

```json
POST /_analyze
{
  "analyzer": "tibetan_analyzer",
  "text": "བཀྲ་ཤིས་བདེ་ལེགས།"
}
```

**Result:** Recognizes full phrase as greeting

## Best Practices

1. ✅ **Use for Tibetan text fields** in mappings
2. ✅ **Test with real data** before production
3. ✅ **Add domain-specific terms** to custom dictionary
4. ✅ **Monitor query performance** with `_profile` API
5. ❌ **Don't use for mixed Tibetan-English** (use multi-field)

## Next Steps

- [Query Examples](./query-examples.md) - Learn search patterns
- [API Reference](./api-reference.md) - Complete API docs
- [Quick Start](./quick-start.md) - Setup guide

