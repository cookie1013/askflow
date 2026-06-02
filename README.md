# AskFlow 企业知识库智能问答系统

AskFlow 是一个面向企业文档知识管理与智能问答场景的 RAG 知识库系统。系统支持知识库 Space 管理、文档上传、自动解析、文本切片、关键词检索、RAG 问答、引用来源返回、问答会话历史管理，以及文档和知识库的软删除能力。

## 技术栈

后端：

* Java 17
* Spring Boot
* Spring Data JPA
* MySQL
* RESTful API
* FastAPI AI Service

AI 服务：

* Python
* FastAPI
* RAG Mock Answer
* Context Chunks Prompt Construction

## 核心功能

### 1. 知识库 Space 管理

* 创建知识库 Space
* 查询知识库列表
* 查看知识库详情
* 软删除知识库
* Space 删除后，对应文档和 chunk 同步失效
* 删除后的 Space 不再参与检索
* 删除后的 Space 名称可以重新使用

### 2. 文档管理

* 支持手动创建文档
* 支持上传 `.txt` / `.md` 文档
* 上传后自动读取文档内容
* 自动清理 UTF-8 BOM 隐藏字符
* 按指定 chunkSize 自动切分知识片段
* 自动更新文档解析状态和 chunk 数量
* 支持文档软删除
* 文档删除后，对应 chunk 同步失效
* 删除后的文档不再参与检索
* 删除后的文档标题可以重新使用

### 3. 知识片段检索

* 基于 spaceId 和 keyword 检索有效 chunk
* 仅检索 status = 1 的有效知识片段
* Space 或 Document 被软删除后，对应内容不会被检索到
* 检索结果包含 chunkId、documentId、documentTitle、chunkIndex、content、tokenCount 等信息

### 4. RAG 智能问答

* 用户提交问题后，后端根据 keyword 检索相关 chunk
* 将检索结果封装为 context_chunks
* 调用 AI 服务生成答案
* 返回 answer、citations、debug 等信息
* citations 中包含引用片段、文档名称和 chunkId
* 支持无检索结果时的兜底回答

### 5. QA 会话历史管理

* 问答时自动创建会话
* 保存用户问题和 AI 回复
* 支持按 Space 查看会话列表
* 支持查看某个会话下的消息历史
* 支持续问时复用 sessionId
* 支持会话软删除
* 会话删除后，对应消息同步失效
* 删除后的会话不能继续追问

## 项目结构

```text
askflow
├── askflow-backend
│   ├── controller
│   ├── service
│   ├── service.impl
│   ├── repository
│   ├── entity
│   ├── dto
│   ├── client
│   └── common
└── askflow-ai-service
    ├── main.py
    └── rag
```

## 主要接口

### 知识库 Space

```http
POST   /api/kb/spaces
GET    /api/kb/spaces
GET    /api/kb/spaces/{id}
DELETE /api/kb/spaces/{id}
```

### 文档管理

```http
POST   /api/kb/spaces/{spaceId}/documents
GET    /api/kb/spaces/{spaceId}/documents
GET    /api/kb/documents/{id}
DELETE /api/kb/documents/{id}
POST   /api/kb/spaces/{spaceId}/documents/upload
GET    /api/kb/documents/{id}/chunks
```

### 知识检索

```http
GET /api/kb/search?spaceId=1&keyword=Redis
```

### RAG 问答

```http
POST /api/chat/ask
```

请求示例：

```json
{
  "spaceId": 1,
  "question": "Redis 未命中时系统会怎么处理？",
  "keyword": "Redis"
}
```

响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "answer": "根据知识库检索到的内容...",
    "sessionId": 5,
    "citations": [],
    "debug": {
      "retrieval": "enabled",
      "context_count": 3
    }
  }
}
```

### QA 会话

```http
GET    /api/qa/sessions?spaceId=1
GET    /api/qa/sessions/{id}/messages
DELETE /api/qa/sessions/{id}
```

## 启动方式

### 1. 启动后端

```powershell
cd D:\askflow\askflow-backend

$env:ASKFLOW_DB_USERNAME="askflow_user"
$env:ASKFLOW_DB_PASSWORD="Askflow@123456"

.\mvnw spring-boot:run
```

后端默认端口：

```text
http://localhost:8081
```

### 2. 启动 AI 服务

```powershell
cd D:\askflow\askflow-ai-service
python main.py
```

AI 服务默认地址：

```text
http://localhost:8000
```

## 项目亮点

* 实现从文档上传、自动解析、chunk 切分、检索到 RAG 问答的完整知识接入闭环。
* 文档上传阶段自动清理 BOM 隐藏字符，避免 Windows 文件编码导致 chunk 内容异常。
* 支持 Space、Document、Chunk 多级软删除，删除后数据仍保留但不再参与业务检索。
* RAG 问答返回引用来源，支持追踪答案依据的文档片段。
* 支持 QA 会话历史管理，能够保存用户问题、AI 回复、引用信息和 debug 信息。
* 支持续问复用 sessionId，删除后的会话禁止继续使用，保证会话状态一致性。
## RAG 问答能力

AskFlow 已从早期关键词检索问答升级为基于 Embedding 的向量 RAG 问答链路。

当前 RAG 流程：

```text
用户问题
  ↓
FastAPI 生成 query embedding
  ↓
ChromaDB 语义检索 topK 知识片段
  ↓
根据 min_score 过滤低相关片段
  ↓
构造上下文增强 prompt
  ↓
调用 DeepSeek 真实大模型生成答案
  ↓
返回 answer、citations、retrieval_scores 和 debug 信息
