# API 参考文档

完整的藏文分词器在 Elasticsearch 中使用的参考文档。

## 分词器 API

### 测试分词器

**端点：** `POST /_analyze`

**请求：**
```json
POST /_analyze
{
  "analyzer": "tibetan_analyzer",
  "text": "བཀྲ་ཤིས་བདེ་ལེགས།"
}
```

**响应：**
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

## 索引管理

### 创建带藏文分词器的索引

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

### 更新索引设置（需要重新索引）

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

### 检查索引设置

```json
GET /tibetan_index/_settings
```

### 查看映射

```json
GET /tibetan_index/_mapping
```

## 文档操作

### 索引文档

```json
POST /tibetan_index/_doc
{
  "title": "བོད་ཀྱི་རིག་གནས།",
  "content": "བོད་ནི་རིག་གནས་ཕྱུག་པའི་རྒྱལ་ཁབ་ཅིག་ཡིན།",
  "tags": ["culture", "tibet"]
}
```

### 指定 ID 索引

```json
PUT /tibetan_index/_doc/1
{
  "title": "སློབ་གྲྭ།",
  "content": "སློབ་གྲྭ་ནི་སློབ་སྦྱོང་བྱེད་སའི་གནས།"
}
```

### 获取文档

```json
GET /tibetan_index/_doc/1
```

### 更新文档

```json
POST /tibetan_index/_update/1
{
  "doc": {
    "tags": ["education", "school"]
  }
}
```

### 删除文档

```json
DELETE /tibetan_index/_doc/1
```

## 搜索 API

### 基本匹配查询

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

### 带选项的匹配

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

### 多字段匹配查询

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

**多匹配类型：**
- `best_fields`（默认）：最佳匹配字段分数
- `most_fields`：所有字段分数之和
- `cross_fields`：将字段视为一个大字段
- `phrase`：跨字段短语匹配
- `phrase_prefix`：前缀短语匹配

### 短语匹配查询

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

### 布尔查询

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

### 查询字符串

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

## 过滤和排序

### 按词条过滤

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

### 范围过滤

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

### 排序结果

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

## 分页

### From/Size 分页

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

### Search After（更适合深分页）

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

## 高亮显示

### 基本高亮

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

### 自定义高亮

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

## 聚合

### 词条聚合

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

### 日期直方图

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

### 嵌套聚合

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

## 批量操作

### 批量索引

```json
POST /_bulk
{ "index": { "_index": "tibetan_index" }}
{ "title": "ཨ", "content": "དང་པོ།" }
{ "index": { "_index": "tibetan_index" }}
{ "title": "བ", "content": "གཉིས་པ།" }
{ "index": { "_index": "tibetan_index" }}
{ "title": "ག", "content": "གསུམ་པ།" }
```

### 批量更新和删除

```json
POST /_bulk
{ "update": { "_index": "tibetan_index", "_id": "1" }}
{ "doc": { "tags": ["updated"] }}
{ "delete": { "_index": "tibetan_index", "_id": "2" }}
```

## 管理操作

### 刷新索引

```json
POST /tibetan_index/_refresh
```

### 冲刷索引

```json
POST /tibetan_index/_flush
```

### 强制合并

```json
POST /tibetan_index/_forcemerge?max_num_segments=1
```

### 重新索引

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

## 插件管理

### 列出插件

```json
GET /_cat/plugins?v
```

### 检查插件信息

```json
GET /_nodes/plugins
```

## 监控

### 索引统计

```json
GET /tibetan_index/_stats
```

### 集群健康

```json
GET /_cluster/health
```

### 节点信息

```json
GET /_nodes/stats
```

## 错误响应

### 常见错误代码

**400 错误请求：**
```json
{
  "error": {
    "type": "parsing_exception",
    "reason": "Unknown query type..."
  }
}
```

**404 未找到：**
```json
{
  "error": {
    "type": "index_not_found_exception",
    "reason": "no such index [tibetan_index]"
  }
}
```

## 响应格式

### 搜索响应结构

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

## 性能提示

1. **使用过滤器进行精确匹配**（比查询快）
2. **限制 `_source` 字段**以减少网络传输
3. **仅聚合搜索使用 `size: 0`**
4. **启用查询缓存**用于重复查询
5. **使用 scroll API** 处理大结果集

## 另见

- [查询示例](./query-examples_zh.md) - 实用搜索模式
- [分词器使用](./analyzer-usage_zh.md) - 分词器工作原理
- [快速开始](./quick-start_zh.md) - 入门指南

