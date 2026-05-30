# AskFlow 接口测试记录

## 1. 文档上传与自动解析

### 请求

```http
POST /api/kb/spaces/1/documents/upload
```

### 测试结果

* 文档上传成功
* 自动创建 Document
* 自动读取文件内容
* 自动去除 BOM 隐藏字符
* 自动切分 chunk
* document.parseStatus = PARSED
* document.chunkCount 正常更新

---

## 2. 知识检索

### 请求

```http
GET /api/kb/search?spaceId=1&keyword=RocketMQ
```

### 测试结果

* 能够返回匹配 keyword 的 chunk
* 返回结果包含 chunkId、documentId、documentTitle、chunkIndex、content、tokenCount
* 被软删除文档的 chunk 不会出现在检索结果中
* 被软删除 Space 下的 chunk 不会出现在检索结果中

---

## 3. RAG 问答

### 请求

```http
POST /api/chat/ask
```

### 请求体

```json
{
  "spaceId": 1,
  "question": "Redis 未命中时系统会怎么处理？",
  "keyword": "Redis"
}
```

### 测试结果

* 接口返回 200
* 返回 answer
* 返回 sessionId
* 返回 citations
* 返回 debug 信息
* debug.context_count 与检索到的上下文数量一致

---

## 4. 文档软删除

### 请求

```http
DELETE /api/kb/documents/{id}
```

### 测试结果

* 删除接口返回 200
* 再查文档详情返回 document not found
* 文档列表不再展示该文档
* 文档 chunk 列表返回空数组
* 搜索接口不再返回该文档的 chunk
* 知识库 documentCount 正确减少
* 软删除后同名文档可以重新上传

---

## 5. Space 软删除

### 请求

```http
DELETE /api/kb/spaces/{id}
```

### 测试结果

* 删除接口返回 200
* 再查 Space 详情返回 space not found
* Space 列表不再展示该知识库
* 搜索该 Space 返回 space not found
* Space 下的文档和 chunk 同步失效
* 软删除后同名 Space 可以重新创建

---

## 6. QA 会话历史

### 6.1 创建问答会话

请求：

```http
POST /api/chat/ask
```

测试结果：

* 首次问答自动创建 session
* 返回 sessionId
* qa_session 中新增会话记录
* qa_message 中保存 USER 和 ASSISTANT 两条消息

### 6.2 查看会话列表

请求：

```http
GET /api/qa/sessions?spaceId=1
```

测试结果：

* 返回当前 Space 下的有效会话
* 被软删除的会话不再出现

### 6.3 查看会话消息

请求：

```http
GET /api/qa/sessions/{id}/messages
```

测试结果：

* 返回 USER 和 ASSISTANT 消息
* ASSISTANT 消息包含 citationsJson 和 debugJson

### 6.4 连续追问

请求：

```json
{
  "spaceId": 1,
  "sessionId": 4,
  "question": "那为什么要回填缓存？",
  "keyword": "缓存"
}
```

测试结果：

* 返回的 sessionId 与请求中的 sessionId 一致
* 消息数量增加
* 追问记录追加到同一个会话中

### 6.5 会话软删除

请求：

```http
DELETE /api/qa/sessions/{id}
```

测试结果：

* 删除接口返回 200
* 会话列表不再显示该会话
* 再查该会话消息返回 session not found
* 删除后的 sessionId 不能继续追问
* qa_session.status = 0
* qa_message.status = 0
