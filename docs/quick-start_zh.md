# 快速开始指南 - 藏文分词器

## 5分钟快速设置

### 步骤 1: 启动环境

```bash
cd elastic-tibetan-analyzer
docker-compose up -d
```

等待约30秒，让服务初始化完成。

### 步骤 2: 打开 Kibana Dev Tools

1. 打开浏览器访问：http://localhost:5601
2. 点击 **Management** → **Dev Tools**（或直接访问：http://localhost:5601/app/dev_tools#/console）

### 步骤 3: 验证插件安装

```json
GET /_cat/plugins?v
```

预期输出：
```
name             component           version
es-tibetan-test  tibetan-analyzer-plugin v1.0-es8.7+
```

### 步骤 4: 测试分词器

复制以下内容粘贴到 Kibana Dev Tools：

```json
POST /_analyze
{
  "analyzer": "tibetan_analyzer",
  "text": "བཀྲ་ཤིས་བདེ་ལེགས།"
}
```

预期结果：
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

✅ **成功！** 分词器正常工作。

## 探索样本数据

启动时自动创建了三个包含样本数据的索引：

### 查看可用索引

```json
GET /_cat/indices?v&h=index,docs.count
```

预期输出：
```
index                docs.count
tibetan_docs         5
tibetan_qa           2000
tibetan_medical_qa   239
```

### 查看样本文档

**简单问候语：**
```json
GET /tibetan_docs/_search
{
  "size": 1
}
```

**医学知识：**
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

## 下一步

- [查询示例](./query-examples_zh.md) - 学习不同的搜索模式
- [分词器使用](./analyzer-usage_zh.md) - 理解分词器工作原理
- [API 参考](./api-reference_zh.md) - 完整 API 文档

## 故障排除

**如果服务无法启动：**
```bash
docker-compose down -v
docker-compose up -d
```

**如果插件未加载：**
```bash
docker-compose restart elasticsearch
```

**查看日志：**
```bash
docker-compose logs -f elasticsearch
```

