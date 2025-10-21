# Kibana Dev Tools 查询示例

以下所有示例都可以直接复制粘贴到 Kibana Dev Tools 控制台中使用。

## 📊 基础统计

### 统计各索引文档数

```json
GET /tibetan_docs/_count
GET /tibetan_qa/_count
GET /tibetan_medical_qa/_count
```

### 查看索引映射

```json
GET /tibetan_qa/_mapping
```

## 🔍 简单搜索查询

### 1. 按单个词搜索

**在问题中搜索"学校"（སློབ་གྲྭ）：**

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

### 2. 跨多个字段搜索

**在问题和答案中同时搜索"医学"：**

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

### 3. 搜索所有文本字段

**在标题、内容、问题和答案中搜索"西藏"：**

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

## 🎯 短语搜索

### 精确短语匹配

**搜索精确短语"心脏病"（སྙིང་གཟེར་ནད）：**

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

## 🔗 布尔查询

### 必须匹配（AND 逻辑）

**文档必须同时包含"文化"和"传统"：**

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

### 应该匹配（OR 逻辑）

**文档包含"食物"或"衣服"：**

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

### 必须不匹配（排除）

**关于医学但不包含心脏的文档：**

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

## 🌟 高亮结果

### 高亮匹配词

**显示搜索词在文本中的位置：**

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

## 📈 聚合

### 按分类统计

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

### 文本长度统计

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

## 🎨 排序结果

### 按问题编号排序（医学问答）

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

## 🔎 模糊搜索

### 允许拼写变化

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

## 📚 实际应用查询示例

### 示例 1: 搜索传统艺术

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

### 示例 2: 医学症状搜索

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

### 示例 3: 查找定义

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

## 💡 更好搜索的技巧

1. **使用 `_source` 限制返回字段：**
   ```json
   "_source": ["title", "question"]
   ```

2. **用 `^2` 提升重要字段权重：**
   ```json
   "fields": ["title^2", "content"]
   ```

3. **使用 `size` 控制结果数量：**
   ```json
   "size": 10
   ```

4. **组合过滤器获得精确结果：**
   ```json
   "bool": { "must": [...], "filter": [...] }
   ```

## 🔄 分页

### 获取下一页结果

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

## 🛠️ 测试分词器

### 分析不同文本

```json
POST /_analyze
{
  "analyzer": "tibetan_analyzer",
  "text": "ང་ཚོས་སློབ་གྲྭར་འགྲོ་དགོས།"
}
```

### 与标准分词器对比

```json
POST /_analyze
{
  "analyzer": "standard",
  "text": "བཀྲ་ཤིས་བདེ་ལེགས།"
}
```

## 📋 复制粘贴测试集

这里有5个可以直接测试的查询（一次粘贴一个）：

```json
# 1. 医学搜索
GET /tibetan_medical_qa/_search
{"query": {"match": {"question": "སྙིང་གཟེར"}}, "size": 3}

# 2. 多字段搜索
GET /tibetan_qa/_search
{"query": {"multi_match": {"query": "བོད", "fields": ["title", "content"]}}, "size": 5}

# 3. 短语搜索
GET /tibetan_qa/_search
{"query": {"match_phrase": {"content": "སློབ་གྲྭ"}}, "size": 3}

# 4. 高亮搜索
GET /tibetan_qa/_search
{"query": {"match": {"content": "རིག་གནས"}}, "highlight": {"fields": {"content": {}}}, "size": 2}
```

