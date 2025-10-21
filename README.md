# Elasticsearch Tibetan Analyzer Plugin

**Languages:** [English](#elasticsearch-tibetan-analyzer-plugin) | [中文版](./README_zh.md)

---

A dictionary-based Tibetan language analyzer plugin for Elasticsearch 8.7+, implementing longest-match word segmentation algorithm.

## Features

- ✅ Dictionary-based word segmentation (65,000+ entries)
- ✅ Longest-match tokenization algorithm
- ✅ Tibetan grammatical particle and suffix handling
- ✅ Custom dictionary support
- ✅ Docker test environment included

## Quick Start with Docker

The fastest way to test the plugin:

```bash
# Start Elasticsearch + Kibana with Tibetan analyzer
docker-compose up -d

# Wait for services to be ready (~30 seconds)
docker-compose ps

# Test the analyzer
curl -X POST "http://localhost:9200/_analyze" -H 'Content-Type: application/json' -d '{
  "analyzer": "tibetan_analyzer",
  "text": "ང་ཚོས་སློབ་གྲྭར་འགྲོ་དགོས།"
}'

# Access Kibana Dev Tools
# http://localhost:5601/app/dev_tools#/console

# Stop services
docker-compose down
```

**Default Data**: When you start the services, the following indices are automatically created:
- **tibetan_docs**: 5 simple demo documents (greetings and common phrases)
- **tibetan_qa**: 2000 comprehensive Q&A pairs (history, culture, geography, etc.)
- **tibetan_medical_qa**: 239 traditional Tibetan medical knowledge Q&A pairs

All data is imported automatically on first startup - no additional steps required!

## 📖 Documentation

Complete documentation is available in the `docs/` directory:

- **[Quick Start Guide](./docs/quick-start.md)** - 5-minute setup
- **[Query Examples](./docs/query-examples.md)** ⭐ - Ready-to-use Kibana queries
- **[Analyzer Usage](./docs/analyzer-usage.md)** - How the analyzer works
- **[API Reference](./docs/api-reference.md)** - Complete API documentation

**New users:** Start with [Quick Start Guide](./docs/quick-start.md), then try [Query Examples](./docs/query-examples.md) in Kibana Dev Tools!

### Quick Search Examples

```bash
# Count documents in each index
curl "http://localhost:9200/tibetan_docs/_count"        # 5 docs
curl "http://localhost:9200/tibetan_qa/_count"          # 2000 docs
curl "http://localhost:9200/tibetan_medical_qa/_count"  # 239 docs

# Search general Q&A
curl -X POST "http://localhost:9200/tibetan_qa/_search" -H 'Content-Type: application/json' -d '{
  "query": {"match": {"question": "ལོ་རྒྱུས"}},
  "size": 5
}'

# Search medical Q&A
curl -X POST "http://localhost:9200/tibetan_medical_qa/_search" -H 'Content-Type: application/json' -d '{
  "query": {"match": {"question": "སྙིང་གཟེར"}},
  "size": 3
}'

# Multi-field search across all text fields
curl -X POST "http://localhost:9200/tibetan_qa/_search" -H 'Content-Type: application/json' -d '{
  "query": {
    "multi_match": {
      "query": "བོད་ཀྱི་ལོ་རྒྱུས",
      "fields": ["title", "content", "question", "answer"]
    }
  },
  "size": 10
}'
```

## Sample Data

All test data is pre-generated and included in the `docker/` directory:

| File | Size | Records | Description | Index Name |
|------|------|---------|-------------|------------|
| `docker/sample-data.json` | 1.3KB | 5 | Demo greetings and phrases | `tibetan_docs` |
| `docker/tibetan-qa-data.json` | 6.7MB | 2000 | General knowledge Q&A (history, culture, geography) | `tibetan_qa` |
| `docker/tibetan-medical-qa-data.json` | 290KB | 239 | Traditional Tibetan medicine Q&A | `tibetan_medical_qa` |

**Data Sources:**
- General Q&A: Derived from public Tibetan knowledge base
- Medical Q&A: Extracted from traditional Tibetan medical literature (གསོ་རིག་རྒྱུན་ཤེས།)

**Note:** Conversion scripts (`convert-qa-csv.py`, `convert-medical-qa.py`) are included for reference but not required for normal use.

## Build from Source

### Prerequisites

- Java 17+
- Gradle 8.0+

### Build Steps

```bash
# Clone repository
git clone https://github.com/TocharianOU/elastic-tibetan-analyzer.git
cd elastic-tibetan-analyzer

# Build plugin
./gradlew clean build

# Plugin artifacts will be in:
# - build/distributions/tibetan-analyzer-plugin-v1.0-es8.7+.zip
# - releases/v1.0-es8.7+/
```

## Manual Installation

### Install Plugin

```bash
# Install from local file
bin/elasticsearch-plugin install file:///path/to/tibetan-analyzer-plugin-v1.0-es8.7+.zip

# Restart Elasticsearch
```

### Create Index with Tibetan Analyzer

```json
PUT /tibetan_test
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
      "content": {
        "type": "text",
        "analyzer": "my_tibetan"
      }
    }
  }
}
```

### Test Tokenization

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
    { "token": "བཀྲ་ཤིས་བདེ་ལེགས", "position": 0 },
    { "token": "།", "position": 1 }
  ]
}
```

## Example Tokenization Results

| Input | Tokens | Translation |
|-------|--------|-------------|
| `ང་ཚོས་སློབ་གྲྭར་འགྲོ་དགོས།` | `ང་ཚོ` `ས` `སློབ་གྲྭ` `ར` `འགྲོ་དགོས` `།` | We go to school |
| `ངའི་ཕ་མ་གཉིས་དགོན་པར་ཕྱིན་པ་རེད།` | `ང` `འི` `ཕ་མ` `གཉིས` `དགོན་པ` `ར` `ཕྱིན་པ` `རེད` `།` | My parents went to the temple |
| `བོད་ཀྱི་ལོ་རྒྱུས།` | `བོད` `ཀྱི` `ལོ་རྒྱུས` `།` | Tibetan history |

## Algorithm

- **Longest-match segmentation**: Prioritizes longer words (4→3→2→1 syllables)
- **Dictionary-based lookup**: 65,000+ entries across multiple categories
- **Suffix handling**: Automatically splits grammatical particles (ས, ར, འི, etc.)
- **Unicode-aware**: Proper handling of Tibetan combining characters

## Custom Dictionary

Add custom words to `src/main/resources/dictionaries/custom_dictionary.txt` (one word per line):

```
བོད་ལྗོངས་
རྒྱལ་ས་
ལྷ་ས་
```

Rebuild the plugin after adding custom words.

## Dictionary Statistics

- Verbs: 3,117
- 1-syllable words: 1,560
- 2-syllable words: 38,647
- 3-syllable words: 11,663
- 4-syllable words: 10,232
- Particles: 38
- Suffixes: 10

## Compatibility

- Elasticsearch: 8.7.0+
- Lucene: 9.5+
- Java: 17+

## License

Apache License 2.0

## Contributing

Issues and pull requests are welcome at: https://github.com/TocharianOU/elastic-tibetan-analyzer

---

**Note**: This plugin uses dictionary-based segmentation. For best results with domain-specific texts, consider adding custom vocabulary to the custom dictionary.
