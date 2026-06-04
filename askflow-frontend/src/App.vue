<template>
  <div class="page">
    <div class="header">
      <div>
        <h2>AskFlow RAG Trace Console</h2>
        <p>可观测 RAG 问答链路：检索模式、召回片段、引用来源与耗时分析</p>
      </div>

      <div class="toolbar">
        <el-input-number v-model="spaceId" :min="1" />
        <el-button type="primary" @click="loadTraces">刷新 Trace</el-button>
      </div>
    </div>

    <div class="main">
      <el-card class="left-card" shadow="never">
        <template #header>
          <div class="card-title">
            <span>Trace 列表</span>
            <el-tag type="info">{{ traces.length }} 条</el-tag>
          </div>
        </template>

        <el-table
          :data="traces"
          height="calc(100vh - 190px)"
          highlight-current-row
          @row-click="handleTraceClick"
        >
          <el-table-column prop="id" label="ID" width="60" />

          <el-table-column label="问题" min-width="220">
            <template #default="{ row }">
              <div class="question-cell">{{ row.question }}</div>
              <div class="sub-info">{{ row.createdAt }}</div>
            </template>
          </el-table-column>

          <el-table-column label="模式" width="100">
            <template #default="{ row }">
              <el-tag
                :type="modeTagType(row.retrievalMode)"
                class="trace-tag"
              >
                {{ row.retrievalMode }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="contextCount" label="Ctx" width="70" />

          <el-table-column label="耗时" width="110">
            <template #default="{ row }">
              {{ row.totalTimeMs }} ms
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card class="right-card" shadow="never">
        <template #header>
          <div class="card-title">
            <span>Trace 详情</span>
            <el-tag v-if="detail" :type="modeTagType(detail.retrievalMode)">
              {{ detail.retrievalMode }}
            </el-tag>
          </div>
        </template>

        <el-empty v-if="!detail" description="点击左侧 Trace 查看详情" />

        <div v-else class="detail">
          <section class="section">
            <h3>问题</h3>
            <p class="question">{{ detail.question }}</p>
          </section>

          <section class="section">
            <h3>回答</h3>
            <div class="answer">{{ detail.answer }}</div>
          </section>

          <section class="section">
            <h3>链路指标</h3>
            <div class="metrics">
              <el-statistic title="检索耗时" :value="detail.retrievalTimeMs || 0" suffix="ms" />
              <el-statistic title="生成耗时" :value="detail.generationTimeMs || 0" suffix="ms" />
              <el-statistic title="总耗时" :value="detail.totalTimeMs || 0" suffix="ms" />
              <el-statistic title="上下文数" :value="detail.contextCount || 0" />
            </div>
          </section>

          <section class="section">
            <h3>Retrieval Scores</h3>

            <el-table :data="detail.retrievalScores || []" border size="small">
              <el-table-column prop="chunk_id" label="Chunk ID" width="100" />
              <el-table-column prop="document_name" label="Document" min-width="180" />
              <el-table-column label="Score" width="120">
                <template #default="{ row }">
                  {{ formatScore(row.score) }}
                </template>
              </el-table-column>
              <el-table-column label="Source" width="110">
                <template #default="{ row }">
                  <el-tag :type="sourceTagType(row.source)">
                    {{ row.source || 'unknown' }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </section>

          <section class="section">
            <h3>Citations</h3>

            <el-collapse>
              <el-collapse-item
                v-for="(item, index) in detail.citations || []"
                :key="index"
                :title="citationTitle(item, index)"
              >
                <div class="chunk-content">{{ item.content }}</div>
              </el-collapse-item>
            </el-collapse>
          </section>

          <section class="section">
            <h3>Retrieved Chunks</h3>

            <el-timeline>
              <el-timeline-item
                v-for="chunk in detail.chunks || []"
                :key="chunk.chunkId"
                :timestamp="'Rank ' + chunk.rankNo"
                placement="top"
              >
                <el-card shadow="never" class="chunk-card">
                  <div class="chunk-header">
                    <span>
                      Chunk {{ chunk.chunkId }} · {{ chunk.documentTitle }}
                    </span>
                    <div>
                      <el-tag size="small" :type="sourceTagType(chunk.source)">
                        {{ chunk.source || 'unknown' }}
                      </el-tag>
                      <el-tag size="small" type="info">
                        score {{ formatScore(chunk.score) }}
                      </el-tag>
                    </div>
                  </div>

                  <div class="chunk-content">
                    {{ chunk.contentSnapshot }}
                  </div>
                </el-card>
              </el-timeline-item>
            </el-timeline>
          </section>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getRagTraceDetail, listRagTraces } from './api/ragTrace'

const spaceId = ref(1)
const traces = ref([])
const detail = ref(null)

async function loadTraces() {
  try {
    traces.value = await listRagTraces(spaceId.value)

    if (traces.value.length > 0) {
      await loadDetail(traces.value[0].id)
    } else {
      detail.value = null
    }
  } catch (error) {
    console.error(error)
  }
}

async function handleTraceClick(row) {
  await loadDetail(row.id)
}

async function loadDetail(id) {
  try {
    detail.value = await getRagTraceDetail(id)
  } catch (error) {
    console.error(error)
    ElMessage.error('加载 Trace 详情失败')
  }
}

function modeTagType(mode) {
  if (mode === 'hybrid') return 'success'
  if (mode === 'vector') return 'primary'
  if (mode === 'keyword') return 'warning'
  return 'info'
}

function sourceTagType(source) {
  if (source === 'hybrid') return 'success'
  if (source === 'vector') return 'primary'
  if (source === 'keyword') return 'warning'
  return 'info'
}

function formatScore(score) {
  if (score === null || score === undefined) {
    return '-'
  }

  return Number(score).toFixed(4)
}

function citationTitle(item, index) {
  const doc = item.document_name || item.documentName || 'unknown document'
  const chunkId = item.chunk_id || item.chunkId || '-'
  return `[${index + 1}] ${doc} / chunk ${chunkId}`
}

onMounted(() => {
  loadTraces()
})
</script>

<style scoped>
.page {
  height: 100vh;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.header {
  height: 88px;
  padding: 18px 22px;
  background: linear-gradient(135deg, #1f2937, #374151);
  color: white;
  border-radius: 14px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h2 {
  margin: 0;
  font-size: 24px;
}

.header p {
  margin: 8px 0 0;
  color: #d1d5db;
}

.toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
}

.main {
  flex: 1;
  display: grid;
  grid-template-columns: 42% 58%;
  gap: 16px;
  min-height: 0;
}

.left-card,
.right-card {
  height: calc(100vh - 124px);
  overflow: hidden;
}

.right-card :deep(.el-card__body) {
  height: calc(100% - 56px);
  overflow: auto;
}

.card-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.question-cell {
  font-weight: 600;
  line-height: 1.4;
}

.sub-info {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}

.detail {
  padding-right: 6px;
}

.section {
  margin-bottom: 26px;
}

.section h3 {
  margin: 0 0 12px;
  font-size: 17px;
  color: #1f2937;
}

.question {
  padding: 12px;
  background: #f3f4f6;
  border-radius: 8px;
  line-height: 1.7;
}

.answer {
  padding: 14px;
  line-height: 1.8;
  background: #f8fafc;
  border-left: 4px solid #409eff;
  border-radius: 8px;
  white-space: pre-wrap;
}

.metrics {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}

.metrics :deep(.el-statistic) {
  padding: 14px;
  background: #f8fafc;
  border-radius: 10px;
}

.chunk-card {
  background: #fbfdff;
}

.chunk-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  font-weight: 600;
  margin-bottom: 10px;
}

.chunk-header > div {
  display: flex;
  gap: 6px;
}

.chunk-content {
  line-height: 1.8;
  color: #374151;
  white-space: pre-wrap;
}
</style>