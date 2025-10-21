# Documentation Index

Complete documentation for the Elasticsearch Tibetan Analyzer Plugin.

## 📚 Documentation

**Language:** [English](#english-documentation) | [中文](#中文文档)

---

### English Documentation

#### Getting Started
- **[Quick Start Guide](./quick-start.md)** - 5-minute setup and first queries
  - Docker setup
  - Verify installation
  - Test with sample data
  - Troubleshooting

#### Core Documentation
- **[Query Examples](./query-examples.md)** ⭐ Most Important
  - Ready-to-use Kibana queries
  - Search patterns and techniques
  - Real-world examples
  - Non-sensitive test queries
  
- **[Analyzer Usage](./analyzer-usage.md)**
  - How the analyzer works
  - Testing tokenization
  - Index configuration
  - Custom dictionary

- **[API Reference](./api-reference.md)**
  - Complete API documentation
  - All query types
  - Bulk operations
  - Admin operations

## 🚀 Quick Links

### For First-Time Users
1. Start here: [Quick Start Guide](./quick-start.md)
2. Open Kibana: http://localhost:5601/app/dev_tools#/console
3. Try examples: [Query Examples](./query-examples.md)

### For Developers
1. Understand: [Analyzer Usage](./analyzer-usage.md)
2. Reference: [API Reference](./api-reference.md)
3. Source code: `src/main/java/org/tocharian/`

## 📖 Documentation Structure

```
docs/
├── README.md              # This file - documentation index
├── quick-start.md         # 5-minute setup guide
├── query-examples.md      # Kibana Dev Tools queries ⭐
├── analyzer-usage.md      # How to use the analyzer
└── api-reference.md       # Complete API reference
```

## 💡 Common Tasks

### Test the Analyzer
```json
POST /_analyze
{
  "analyzer": "tibetan_analyzer",
  "text": "བཀྲ་ཤིས་བདེ་ལེགས།"
}
```
See: [Quick Start Guide](./quick-start.md#step-4-test-the-analyzer)

### Search Documents
```json
GET /tibetan_medical_qa/_search
{
  "query": {
    "match": {
      "question": "སྙིང་གཟེར"
    }
  },
  "size": 3
}
```
See: [Query Examples](./query-examples.md#simple-search-queries)

### Create Custom Index
```json
PUT /my_index
{
  "settings": {
    "analysis": {
      "analyzer": {
        "my_tibetan": {
          "type": "tibetan_analyzer"
        }
      }
    }
  }
}
```
See: [Analyzer Usage](./analyzer-usage.md#using-in-index-settings)

## 🎯 Sample Data

Three indices are automatically created:

| Index | Documents | Description |
|-------|-----------|-------------|
| `tibetan_docs` | 5 | Simple greetings |
| `tibetan_qa` | 2000 | General knowledge Q&A |
| `tibetan_medical_qa` | 239 | Medical knowledge Q&A |

**Total: 2,244 documents** ready for testing!

## 🔍 Example Queries

All examples are non-sensitive and use educational/cultural content:

1. **Search for "school"**: `GET /tibetan_qa/_search?q=སློབ་གྲྭ`
2. **Medical terms**: `GET /tibetan_medical_qa/_search?q=སྙིང་གཟེར`
3. **Cultural topics**: `GET /tibetan_qa/_search?q=རིག་གནས`

See full list: [Query Examples](./query-examples.md#copy-paste-test-set)

## 🛠️ Development

### Build Plugin
```bash
./gradlew clean build
```

### Run Tests
```bash
./gradlew test
```

### Docker Environment
```bash
docker-compose up -d      # Start
docker-compose down -v    # Stop and clean
```

## 📞 Support

- **Issues**: https://github.com/TocharianOU/elastic-tibetan-analyzer/issues
- **Source**: https://github.com/TocharianOU/elastic-tibetan-analyzer

## 📝 License

Apache License 2.0 - See [LICENSE](../LICENSE) file

## 🌟 Features

- ✅ Dictionary-based tokenization (65,000+ entries)
- ✅ Longest-match algorithm
- ✅ Grammatical particle handling
- ✅ Suffix processing
- ✅ Custom dictionary support
- ✅ Docker test environment
- ✅ 2,244 sample documents

---

### 中文文档

#### 快速入门
- **[快速开始指南](./quick-start_zh.md)** - 5分钟设置和首次查询
  - Docker 设置
  - 验证安装
  - 使用样本数据测试
  - 故障排除

#### 核心文档
- **[查询示例](./query-examples_zh.md)** ⭐ 最重要
  - 可直接使用的 Kibana 查询
  - 搜索模式和技巧
  - 真实世界示例
  - 无敏感内容的测试查询
  
- **[分词器使用](./analyzer-usage_zh.md)**
  - 分词器工作原理
  - 测试分词
  - 索引配置
  - 自定义词典

- **[API 参考](./api-reference_zh.md)**
  - 完整 API 文档
  - 所有查询类型
  - 批量操作
  - 管理操作

---

**Need help?** Start with the [Quick Start Guide](./quick-start.md) | **需要帮助？** 从[快速开始指南](./quick-start_zh.md)开始！

