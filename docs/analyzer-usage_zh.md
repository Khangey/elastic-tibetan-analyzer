# 分词器使用指南

## 理解藏文分词器

藏文分词器使用**基于词典的最长匹配算法**将藏文文本分割成有意义的词。

### 工作原理

1. **规范化**：清理和准备文本
2. **音节提取**：识别藏文音节（由 tsek `་` 分隔）
3. **词典查找**：搜索最长的匹配词
4. **后缀处理**：处理语法助词和后缀

## 测试分词器

### 基础分析

```json
POST /_analyze
{
  "analyzer": "tibetan_analyzer",
  "text": "བཀྲ་ཤིས་བདེ་ལེགས།"
}
```

**结果：**
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

### 更多示例

#### 示例 1: 简单句子

```json
POST /_analyze
{
  "analyzer": "tibetan_analyzer",
  "text": "ང་སློབ་གྲྭར་འགྲོ།"
}
```

**分词结果：** `ང`、`སློབ་གྲྭ`、`ར`、`འགྲོ`、`།`

#### 示例 2: 带助词

```json
POST /_analyze
{
  "analyzer": "tibetan_analyzer",
  "text": "ང་ཚོས་བོད་སྐད་སློབ་སྦྱོང་བྱེད་བཞིན་ཡོད།"
}
```

**注意：** 语法助词（ས、ར 等）被正确分离。

#### 示例 3: 医学术语

```json
POST /_analyze
{
  "analyzer": "tibetan_analyzer",
  "text": "སྙིང་གཟེར་ནད་ཀྱི་བཅོས་ཐབས།"
}
```

## 在索引设置中使用

### 方法 1: 全局分词器（简单）

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

### 方法 2: 自定义命名分词器

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

### 方法 3: 多分词器设置

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

## 索引自己的数据

### 创建索引

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

### 索引文档

```json
POST /my_docs/_doc
{
  "title": "བོད་ཀྱི་རིག་གནས།",
  "content": "བོད་ནི་རིག་གནས་ཕྱུག་པའི་རྒྱལ་ཁབ་ཅིག་ཡིན།",
  "author": "མིང་མེད།",
  "date": "2024-01-01"
}
```

### 搜索文档

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

## 批量索引

### 准备 NDJSON 文件

创建 `data.ndjson`：
```json
{"index": {"_index": "my_docs"}}
{"title": "སློབ་གྲྭ།", "content": "སློབ་གྲྭ་ནི་སློབ་སྦྱོང་བྱེད་སའི་གནས་ཡིན།"}
{"index": {"_index": "my_docs"}}
{"title": "གསོ་རིག", "content": "གསོ་རིག་ནི་ནད་གཞི་སེལ་བའི་ཐབས་ལམ་ཡིན།"}
```

### 通过 API 导入

```json
POST /_bulk
{ "index": { "_index": "my_docs" }}
{ "title": "སློབ་གྲྭ།", "content": "སློབ་གྲྭ་ནི་སློབ་སྦྱོང་བྱེད་སའི་གནས་ཡིན།" }
{ "index": { "_index": "my_docs" }}
{ "title": "གསོ་རིག", "content": "གསོ་རིག་ནི་ནད་གཞི་སེལ་བའི་ཐབས་ལམ་ཡིན།" }
```

## 分词器性能

### 词典大小

- **总词条数**：约 65,000+ 词
- **类别**： 
  - 单音节词
  - 双音节词
  - 三音节词
  - 四音节词
  - 动词
  - 助词
  - 自定义词

### 分词速度

典型性能：
- **短文本**（< 100 音节）：< 1ms
- **中等文本**（100-500 音节）：1-5ms
- **长文本**（500+ 音节）：5-20ms

## 常见问题和解决方案

### 问题 1: 文本未分词

**问题：** 文本显示为单个词元
```json
POST /_analyze
{
  "analyzer": "tibetan_analyzer",
  "text": "test"
}
```

**解决方案：** 确保文本为藏文 Unicode（U+0F00 - U+0FFF）

### 问题 2: 过度分词

**问题：** 词被分割成单个音节

**原因：** 词不在词典中

**解决方案：** 添加到自定义词典 `src/main/resources/dictionaries/custom_dictionary.txt`

### 问题 3: 分词错误

**示例：**
```
输入：  སློབ་གྲྭ་ཆེན་མོ
输出：  སློབ་གྲྭ | ཆེན | མོ
期望：  སློབ་གྲྭ་ཆེན་མོ
```

**解决方案：** 将完整短语添加到自定义词典

## 高级：自定义词典

### 添加自定义词

1. 编辑：`src/main/resources/dictionaries/custom_dictionary.txt`
2. 每行添加一个词：
   ```
   སློབ་གྲྭ་ཆེན་མོ
   དཔེ་མཛོད་ཁང
   རིག་གནས་ཁང
   ```
3. 重新构建插件：
   ```bash
   ./gradlew clean build
   ```
4. 重新安装插件

### 测试自定义词

```json
POST /_analyze
{
  "analyzer": "tibetan_analyzer",
  "text": "སློབ་གྲྭ་ཆེན་མོ"
}
```

## 与其他分词器对比

### 标准分词器（不适合藏文）

```json
POST /_analyze
{
  "analyzer": "standard",
  "text": "བཀྲ་ཤིས་བདེ་ལེགས།"
}
```

**结果：** 按 tsek `་` 分割，失去词义

### 藏文分词器（正确）

```json
POST /_analyze
{
  "analyzer": "tibetan_analyzer",
  "text": "བཀྲ་ཤིས་བདེ་ལེགས།"
}
```

**结果：** 识别完整短语作为问候语

## 最佳实践

1. ✅ **在映射中用于藏文字段**
2. ✅ **使用真实数据测试**再投入生产
3. ✅ **添加特定领域术语**到自定义词典
4. ✅ **使用 `_profile` API 监控**查询性能
5. ❌ **不要用于藏英混合文本**（使用多字段）

## 下一步

- [查询示例](./query-examples_zh.md) - 学习搜索模式
- [API 参考](./api-reference_zh.md) - 完整 API 文档
- [快速开始](./quick-start_zh.md) - 设置指南

