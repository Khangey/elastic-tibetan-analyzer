# Quick Start Guide - Tibetan Analyzer

## 5-Minute Setup

### Step 1: Start the Environment

```bash
cd elastic-tibetan-analyzer
docker-compose up -d
```

Wait about 30 seconds for services to initialize.

### Step 2: Open Kibana Dev Tools

1. Open browser: http://localhost:5601
2. Click **Management** → **Dev Tools** (or directly: http://localhost:5601/app/dev_tools#/console)

### Step 3: Verify Plugin Installation

```json
GET /_cat/plugins?v
```

Expected output:
```
name             component           version
es-tibetan-test  tibetan-analyzer-plugin v1.0-es8.7+
```

### Step 4: Test the Analyzer

Copy and paste this into Kibana Dev Tools:

```json
POST /_analyze
{
  "analyzer": "tibetan_analyzer",
  "text": "བཀྲ་ཤིས་བདེ་ལེགས།"
}
```

Expected result:
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

✅ **Success!** The analyzer is working correctly.

## Explore Sample Data

Three indices are automatically created with sample data:

### Check Available Indices

```json
GET /_cat/indices?v&h=index,docs.count
```

Expected output:
```
index                docs.count
tibetan_docs         5
tibetan_qa           2000
tibetan_medical_qa   239
```

### View Sample Documents

**Simple greeting:**
```json
GET /tibetan_docs/_search
{
  "size": 1
}
```

**Medical knowledge:**
```json
GET /tibetan_medical_qa/_search
{
  "query": {
    "match": {
      "question": "སྙིང་གཟེར"
    }
  },
  "size": 2
}
```

## Next Steps

- [Query Examples](./query-examples.md) - Learn different search patterns
- [Analyzer Usage](./analyzer-usage.md) - Understand how the analyzer works
- [API Reference](./api-reference.md) - Complete API documentation

## Troubleshooting

**If services don't start:**
```bash
docker-compose down -v
docker-compose up -d
```

**If plugin is not loaded:**
```bash
docker-compose restart elasticsearch
```

**View logs:**
```bash
docker-compose logs -f elasticsearch
```

