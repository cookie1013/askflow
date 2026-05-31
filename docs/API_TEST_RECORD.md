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


## 向量 RAG 与真实大模型接口测试记录

### 1. 文档向量化接口

#### 接口

```http
POST /api/kb/documents/{documentId}/vectorize
```

#### 测试命令

```powershell
curl.exe -X POST "http://localhost:8081/api/kb/documents/1/vectorize"
```

#### 预期结果

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "documentId": 1,
    "documentTitle": "短链跳转失败排查手册",
    "chunkCount": 3,
    "indexedCount": 3
  }
}
```

#### 测试结论

已通过。

系统能够从 MySQL 查询文档 chunk，并调用 FastAPI `/vector/upsert` 将 chunk 写入 ChromaDB 向量库。

---

### 2. 上传文档后自动向量化

#### 接口

```http
POST /api/kb/spaces/{spaceId}/documents/upload
```

#### 测试命令

```powershell
curl.exe -X POST "http://localhost:8081/api/kb/spaces/1/documents/upload" `
  -F "file=@askflow-auto-vector-demo.md;type=text/markdown" `
  -F "chunkSize=120"
```

#### 预期结果

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "document": {
      "parseStatus": "PARSED",
      "chunkCount": 1
    },
    "chunks": [],
    "indexedCount": 1,
    "vectorStatus": "SUCCESS"
  }
}
```

#### 测试结论

已通过。

上传文档后，系统能够自动完成：

```text
上传文件
  ↓
创建 Document
  ↓
自动解析 chunk
  ↓
调用 FastAPI /vector/upsert
  ↓
写入 ChromaDB
```

上传后的新文档无需手动调用 vectorize 接口，即可直接参与 RAG 问答。

---

### 3. 向量 RAG 问答接口：相关问题测试

#### 接口

```http
POST /api/chat/ask
```

#### 测试问题

```json
{
  "spaceId": 1,
  "question": "缓存失效瞬间大量请求同时回源数据库怎么办？"
}
```

#### 预期结果

* `code = 200`
* `debug.mode = rag_real_llm`
* `debug.retrieval = enabled`
* `debug.context_count <= 3`
* `debug.retrieval_scores` 返回召回分数
* `citations` 返回引用片段
* 回答提到“互斥锁”或“逻辑过期”

#### 测试结论

已通过。

系统能够通过 ChromaDB 语义检索召回 `askflow-auto-vector-demo` 文档中的缓存击穿相关片段，并调用 DeepSeek 真实大模型生成答案。

---

### 4. 向量 RAG 问答接口：语义等价问题测试

#### 测试问题

```json
{
  "spaceId": 1,
  "question": "缓存里面没有查到原始链接时怎么办？"
}
```

#### 预期结果

* 能够召回 Redis 未命中相关 chunk
* 回答包含“回源 MySQL 查询”
* 回答包含“回填缓存”
* citations 包含 `短链跳转失败排查手册`

#### 测试结论

已通过。

虽然问题中没有直接出现“Redis 未命中”关键词，但系统仍能通过向量检索召回 Redis miss 相关知识片段，说明语义检索能力生效。

---

### 5. 向量 RAG 问答接口：访问统计问题测试

#### 测试问题

```json
{
  "spaceId": 1,
  "question": "访问统计为什么不要同步写数据库？"
}
```

#### 预期结果

* 能够召回 RocketMQ 异步统计相关 chunk
* 回答说明同步写库会影响跳转主链路性能
* 回答提到 RocketMQ 异步发送访问事件
* citations 包含访问统计相关片段

#### 测试结论

已通过。

系统成功召回 RocketMQ 异步统计相关知识，并由真实大模型总结出异步解耦、降低主链路延迟的结论。

---

### 6. 向量 RAG 问答接口：无关问题兜底测试

#### 测试问题

```json
{
  "spaceId": 1,
  "question": "员工报销流程是什么？"
}
```

#### 预期结果

```json
{
  "retrieval": "empty",
  "context_count": 0,
  "retrieval_scores": [],
  "citations": []
}
```

回答应说明：

```text
知识库中没有检索到足够依据。
```

#### 测试结论

已通过。

当问题与知识库内容无关时，系统不会强行召回无关 chunk，也不会编造答案，能够正确触发无依据兜底。

---

### 7. RAG 质量控制测试

#### 当前配置

| 配置项          | 当前值                                   |
| ------------ | ------------------------------------- |
| topK         | 3                                     |
| min_score    | 0.35                                  |
| Embedding 模型 | paraphrase-multilingual-MiniLM-L12-v2 |
| 向量库          | ChromaDB                              |
| 生成模型         | deepseek-v4-flash                     |
| 生成模式         | rag_real_llm                          |

#### 测试结论

已通过。

系统通过 `topK` 控制最多进入 prompt 的上下文数量，通过 `min_score` 过滤低相关片段，并在 debug 中返回 `retrieval_scores`，便于观察每个 chunk 的召回质量。

---

### 8. 真实大模型生成测试

#### 测试现象

返回结果中的 debug 包含：

```json
{
  "mode": "rag_real_llm",
  "llm_provider": "real",
  "model": "deepseek-v4-flash"
}
```

#### 测试结论

已通过。

系统已经从早期 mock 模板回答升级为真实大模型生成。FastAPI 会根据检索到的 context_chunks 构造 prompt，并调用 DeepSeek OpenAI-compatible 接口生成答案。

---

### 9. 当前接口测试总结

本阶段完成以下能力验证：

* 文档 chunk 可以写入 ChromaDB 向量库
* 上传文档后可以自动向量化
* `/api/chat/ask` 已切换为向量 RAG 主链路
* 相关问题能够召回正确知识片段
* 语义相近但关键词不同的问题也能正确召回
* 无关问题能够触发兜底回答
* citations 能返回答案依据
* retrieval_scores 能返回召回分数
* DeepSeek 真实大模型生成链路已跑通

当前 AskFlow 已形成完整 RAG 问答闭环：

```text
文档上传
  ↓
自动解析 chunk
  ↓
自动向量化
  ↓
ChromaDB 语义检索
  ↓
topK / min_score 质量控制
  ↓
DeepSeek 真实大模型生成
  ↓
返回 answer + citations + retrieval_scores
```

