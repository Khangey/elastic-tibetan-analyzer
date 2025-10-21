# 文档索引

Elasticsearch 藏文分词器插件的完整文档。

## 📚 文档列表

### 快速入门
- **[快速开始指南](./quick-start_zh.md)** - 5分钟设置和首次查询
  - Docker 设置
  - 验证安装
  - 使用样本数据测试
  - 故障排除

### 核心文档
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

## 🚀 快速链接

### 首次使用
1. 从这里开始：[快速开始指南](./quick-start_zh.md)
2. 打开 Kibana：http://localhost:5601/app/dev_tools#/console
3. 尝试示例：[查询示例](./query-examples_zh.md)

### 开发者
1. 理解原理：[分词器使用](./analyzer-usage_zh.md)
2. API 参考：[API 参考](./api-reference_zh.md)
3. 源代码：`src/main/java/org/tocharian/`

## 📖 文档结构

```
docs/
├── README.md              # 英文文档索引
├── README_zh.md           # 本文件 - 中文文档索引
├── quick-start.md         # 英文快速开始指南
├── quick-start_zh.md      # 中文快速开始指南
├── query-examples.md      # 英文查询示例 ⭐
├── query-examples_zh.md   # 中文查询示例 ⭐
├── analyzer-usage.md      # 英文分词器使用
├── analyzer-usage_zh.md   # 中文分词器使用
├── api-reference.md       # 英文 API 参考
└── api-reference_zh.md    # 中文 API 参考
```

## 💡 常见任务

### 测试分词器
```json
POST /_analyze
{
  "analyzer": "tibetan_analyzer",
  "text": "བཀྲ་ཤིས་བདེ་ལེགས།"
}
```
参见：[快速开始指南](./quick-start_zh.md#步骤-4-测试分词器)

### 搜索文档
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
参见：[查询示例](./query-examples_zh.md#简单搜索查询)

### 创建自定义索引
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
参见：[分词器使用](./analyzer-usage_zh.md#在索引设置中使用)

## 🎯 样本数据

自动创建三个索引：

| 索引 | 文档数 | 说明 |
|------|--------|------|
| `tibetan_docs` | 5 | 简单问候语 |
| `tibetan_qa` | 2000 | 通用知识问答 |
| `tibetan_medical_qa` | 239 | 医学知识问答 |

**总计：2,244 条文档**，随时可用于测试！

## 🔍 查询示例

所有示例都使用中性内容（教育、医学、文化）：

1. **搜索"学校"**：`GET /tibetan_qa/_search?q=སློབ་གྲྭ`
2. **医学术语**：`GET /tibetan_medical_qa/_search?q=སྙིང་གཟེར`
3. **文化话题**：`GET /tibetan_qa/_search?q=རིག་གནས`

查看完整列表：[查询示例](./query-examples_zh.md#复制粘贴测试集)

## 🛠️ 开发

### 构建插件
```bash
./gradlew clean build
```

### 运行测试
```bash
./gradlew test
```

### Docker 环境
```bash
docker-compose up -d      # 启动
docker-compose down -v    # 停止并清理
```

## 📞 支持

- **问题反馈**：https://github.com/TocharianOU/elastic-tibetan-analyzer/issues
- **源代码**：https://github.com/TocharianOU/elastic-tibetan-analyzer

## 📝 许可证

Apache License 2.0 - 查看 [LICENSE](../LICENSE) 文件

## 🌟 功能特性

- ✅ 基于词典的分词（65,000+ 词条）
- ✅ 最长匹配算法
- ✅ 语法助词处理
- ✅ 后缀处理
- ✅ 自定义词典支持
- ✅ Docker 测试环境
- ✅ 2,244 条样本文档

---

**需要帮助？** 从[快速开始指南](./quick-start_zh.md)开始！

