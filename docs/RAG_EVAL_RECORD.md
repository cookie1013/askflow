# AskFlow RAG 评估记录

## 1. 评估目标

本测试用于验证 AskFlow 在企业知识库问答场景下的 RAG 效果，重点关注以下能力：

- 语义检索是否能召回正确知识片段
- 低相关问题是否能正确拒答或兜底
- citations 是否能返回答案依据
- retrieval_scores 是否能反映 chunk 相关性
- 真实大模型生成结果是否严格基于知识库上下文

当前配置：

| 配置项 | 当前值 |
|---|---|
| Embedding 模型 | paraphrase-multilingual-MiniLM-L12-v2 |
| 向量库 | ChromaDB |
| 生成模型 | deepseek-v4-flash |
| 默认 topK | 3 |
| 默认 min_score | 0.35 |
| 生成模式 | rag_real_llm |

---

## 2. 测试用例汇总

| 编号 | 问题 | 期望召回内容 | 期望结果 |
|---|---|---|---|
| Q1 | 缓存失效瞬间大量请求同时回源数据库怎么办？ | 缓存击穿、互斥锁、逻辑过期 | 正确回答，引用自动向量化测试文档 |
| Q2 | Redis 未命中时系统会怎么处理？ | Redis 未命中、回源 MySQL、回填缓存 | 正确回答，引用短链排查手册 |
| Q3 | 缓存里面没有查到原始链接时怎么办？ | Redis 未命中、回源 MySQL、回填缓存 | 语义召回成功 |
| Q4 | 访问统计为什么不要同步写数据库？ | RocketMQ 异步统计、PV、访问日志 | 正确回答，引用 RocketMQ 片段 |
| Q5 | 短链跳转失败有哪些原因？ | 短码不存在、禁用、过期、限流 | 正确回答 |
| Q6 | Sentinel 限流会影响短链跳转吗？ | Sentinel 限流 | 能召回限流相关片段 |
| Q7 | 公司年假制度是什么？ | 无相关知识 | 应拒答或说明知识库无依据 |
| Q8 | 员工报销流程是什么？ | 无相关知识 | 应拒答或说明知识库无依据 |
| Q9 | RocketMQ 在访问统计中起什么作用？ | 异步更新 PV 和访问日志 | 正确回答 |
| Q10 | 原始链接不可访问会导致什么问题？ | 短链跳转失败原因 | 正确回答 |

---

## 3. 详细测试记录

### Q1：缓存失效瞬间大量请求同时回源数据库怎么办？

请求：

```json
{
  "spaceId": 1,
  "question": "缓存失效瞬间大量请求同时回源数据库怎么办？"
}
```

期望：

* `code = 200`
* `debug.mode = rag_real_llm`
* `debug.retrieval = enabled`
* `context_count <= 3`
* citations 中包含 `askflow-auto-vector-demo`
* 回答提到互斥锁或逻辑过期

实际结果：

```text
已通过。
```

关键现象：

* `mode = rag_real_llm`
* `retrieval = enabled`
* `context_count = 3`
* citations 命中 `askflow-auto-vector-demo`
* 回答正确提到互斥锁或逻辑过期策略

备注：

```text
该问题用于验证语义检索能否召回“缓存击穿”相关知识。
```

---

### Q2：Redis 未命中时系统会怎么处理？

请求：

```json
{
  "spaceId": 1,
  "question": "Redis 未命中时系统会怎么处理？"
}
```

期望：

* 召回 Redis 未命中相关 chunk
* 回答包含“回源 MySQL 查询并回填缓存”
* citations 包含短链跳转失败排查手册

实际结果：

```text
已通过。
```

关键现象：

* `code = 200`
* `mode = rag_real_llm`
* `retrieval = enabled`
* `context_count = 1`
* 命中 chunk：`chunk_id = 4`
* 最高召回分数：`score = 0.5195`
* citations 命中 `短链跳转失败排查手册`
* 回答包含“回源 MySQL 查询”和“回填 Redis 缓存”

---

### Q3：缓存里面没有查到原始链接时怎么办？

请求：

```json
{
  "spaceId": 1,
  "question": "缓存里面没有查到原始链接时怎么办？"
}
```

期望：

* 虽然没有直接出现“Redis 未命中”关键词，但能语义召回 Redis 未命中相关 chunk
* 回答说明回源 MySQL 并回填缓存

实际结果：

```text
已通过。
```

关键现象：

* `code = 200`
* `mode = rag_real_llm`
* `retrieval = enabled`
* `context_count = 2`
* 命中 chunk：`chunk_id = 4`，`score = 0.5166`
* 辅助命中 chunk：`chunk_id = 3`，`score = 0.3712`
* 回答说明缓存未命中时系统会回源 MySQL 查询，并将结果回填到缓存中

备注：

```text
该问题没有直接使用“Redis 未命中”关键词，但系统仍能召回 Redis 未命中相关片段，说明向量语义检索生效。
```

---

### Q4：访问统计为什么不要同步写数据库？

请求：

```json
{
  "spaceId": 1,
  "question": "访问统计为什么不要同步写数据库？"
}
```

期望：

* 召回 RocketMQ 异步统计相关 chunk
* 回答说明统计不适合放在跳转主链路中同步写库
* 回答提到通过 RocketMQ 发送访问事件，由消费者异步更新 PV 和访问日志

实际结果：

```text
已通过。
```

关键现象：

* `code = 200`
* `mode = rag_real_llm`
* `retrieval = enabled`
* `context_count = 3`
* 第一召回 chunk：`chunk_id = 5`，`score = 0.7072`
* 第二召回 chunk：`chunk_id = 16`，`score = 0.6300`
* citations 命中 RocketMQ 异步统计相关片段
* 回答说明同步写库会影响跳转主链路性能，并建议通过 RocketMQ 异步发送访问事件

---

### Q5：短链跳转失败有哪些原因？

请求：

```json
{
  "spaceId": 1,
  "question": "短链跳转失败有哪些原因？"
}
```

期望：

* 回答包含短码不存在、短链被禁用、短链已过期、原始链接不可访问、Sentinel 限流等原因

实际结果：

```text
已通过。
```

关键现象：

* `code = 200`
* `mode = rag_real_llm`
* `retrieval = enabled`
* `context_count = 2`
* 第一召回 chunk：`chunk_id = 3`，`score = 0.7541`
* citations 命中 `短链跳转失败排查手册`
* 回答完整列出了短码不存在、短链被禁用、短链已过期、原始链接不可访问、Sentinel 限流等原因

---

### Q6：Sentinel 限流会影响短链跳转吗？

请求：

```json
{
  "spaceId": 1,
  "question": "Sentinel 限流会影响短链跳转吗？"
}
```

期望：

* 能召回 Sentinel 限流相关片段
* 回答说明系统触发 Sentinel 限流时可能导致请求被限制

实际结果：

```text
已通过。
```

关键现象：

* `code = 200`
* `mode = rag_real_llm`
* `retrieval = enabled`
* `context_count = 2`
* 第一召回 chunk：`chunk_id = 3`，`score = 0.5516`
* citations 命中 Sentinel 限流相关片段
* 回答说明 Sentinel 限流确实可能影响短链跳转，并给出了排查建议

备注：

```text
第二个召回片段 score = 0.3576，接近 min_score = 0.35 的边界，相关性偏弱但未影响最终答案。后续可考虑将 min_score 提高到 0.4 进行对比实验。
```

---

### Q7：公司年假制度是什么？

请求：

```json
{
  "spaceId": 1,
  "question": "公司年假制度是什么？"
}
```

期望：

* `retrieval = empty`
* `context_count = 0`
* citations 为空
* 回答说明知识库中没有检索到足够依据

实际结果：

```text
已通过。
```

关键现象：

* `code = 200`
* `mode = rag_real_llm`
* `retrieval = empty`
* `context_count = 0`
* `citations = []`
* 回答说明知识库中没有检索到足够依据

备注：

```text
该问题用于验证无关问题兜底能力。
```

---

### Q8：员工报销流程是什么？

请求：

```json
{
  "spaceId": 1,
  "question": "员工报销流程是什么？"
}
```

期望：

* 不应强行编造报销流程
* 应说明知识库中没有足够依据

实际结果：

```text
已通过。
```

关键现象：

* `code = 200`
* `mode = rag_real_llm`
* `retrieval = empty`
* `context_count = 0`
* `retrieval_scores = []`
* `citations = []`
* 回答为“知识库中没有检索到足够依据。”

备注：

```text
该问题进一步验证了低相关问题不会强行召回无关 chunk，min_score 过滤策略有效。
```

---

### Q9：RocketMQ 在访问统计中起什么作用？

请求：

```json
{
  "spaceId": 1,
  "question": "RocketMQ 在访问统计中起什么作用？"
}
```

期望：

* 回答说明 RocketMQ 用于发送访问事件
* 消费者异步更新 PV 和访问日志
* citations 包含 RocketMQ 相关 chunk

实际结果：

```text
已通过。
```

关键现象：

* `code = 200`
* `mode = rag_real_llm`
* `retrieval = enabled`
* `context_count = 2`
* 第一召回 chunk：`chunk_id = 5`，`score = 0.7640`
* 第二召回 chunk：`chunk_id = 16`，`score = 0.5713`
* citations 命中 RocketMQ 异步统计相关片段
* 回答说明 RocketMQ 用于异步解耦，由消费者异步更新 PV 和访问日志，避免统计写入阻塞跳转主链路

---

### Q10：原始链接不可访问会导致什么问题？

请求：

```json
{
  "spaceId": 1,
  "question": "原始链接不可访问会导致什么问题？"
}
```

期望：

* 回答说明原始链接不可访问可能导致短链跳转失败
* citations 包含短链跳转失败原因相关 chunk

实际结果：

```text
已通过。
```

关键现象：

* `code = 200`
* `mode = rag_real_llm`
* `retrieval = enabled`
* `context_count = 3`
* 第一召回 chunk：`chunk_id = 3`，`score = 0.4932`
* 第二召回 chunk：`chunk_id = 4`，`score = 0.4633`
* 第三召回 chunk：`chunk_id = 5`，`score = 0.3522`
* citations 命中 `短链跳转失败排查手册`
* 回答说明原始链接不可访问是导致短链跳转失败的常见原因之一

备注：

```text
第三个召回片段 score = 0.3522，接近 min_score = 0.35 的边界，相关性较弱。后续可通过提高 min_score 或引入 rerank 进一步减少边界片段进入上下文。
```

---

## 4. 当前结论

本轮 RAG 评估共测试 10 个问题，其中：

| 类型         | 数量 | 结果                                  |
| ---------- | -: | ----------------------------------- |
| 相关业务问题     |  8 | 均能召回相关知识片段并生成正确回答                   |
| 无关问题       |  2 | 均能触发无依据兜底，不产生编造回答                   |
| 引用溯源       | 10 | 相关问题均返回 citations，无关问题 citations 为空 |
| 检索分数 debug | 10 | 均返回 retrieval_scores 或空数组           |
| 真实大模型生成    | 10 | 均使用 rag_real_llm 模式                 |

当前 AskFlow 已经完成从关键词 RAG 到向量 RAG 的升级，并接入真实大模型生成。系统能够对语义相近问题进行召回，例如“缓存里面没有查到原始链接时怎么办？”能够命中 Redis 未命中相关知识片段；同时也能对知识库中没有依据的问题进行兜底回答，例如“员工报销流程是什么？”返回“知识库中没有检索到足够依据”。

当前配置下，`topK = 3`、`min_score = 0.35` 能够保证主要相关问题被召回，同时过滤掉明显无关问题。但在 Q6 和 Q10 中仍出现了接近阈值边界的弱相关片段，说明后续仍有优化空间。

后续优化方向：

* 将 `min_score` 从 0.35 提高到 0.4，观察边界片段是否减少
* 引入 rerank 模型，对向量召回结果进行二次重排
* 增加混合检索：keyword + vector，提高关键词明确问题的稳定性
* 增加批量评估脚本，自动统计召回命中率、拒答准确率和 citation 覆盖率
* 扩展更多业务文档，验证系统在更大知识库下的召回质量
