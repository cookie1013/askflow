---
tags:
- sentence-transformers
- sentence-similarity
- feature-extraction
- generated_from_trainer
- dataset_size:56
- loss:MultipleNegativesRankingLoss
base_model: sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2
widget:
- source_sentence: 缓存击穿和 Redis 未命中有什么应对方案？
  sentences:
  - '# 自动向量化测试文档

    当短链缓存击穿风险较高时，可以使用互斥锁或逻辑过期策略，避免大量请求在缓存失效瞬间同时回源数据库。

    如果短链访问统计不适合同步写库，可以通过 RocketMQ 发送访问事件，由消费者异步更新 PV 和访问日志。'
  - '# 自动向量化测试文档

    当短链缓存击穿风险较高时，可以使用互斥锁或逻辑过期策略，避免大量请求在缓存失效瞬间同时回源数据库。

    如果短链访问统计不适合同步写库，可以通过 RocketMQ 发送访问事件，由消费者异步更新 PV 和访问日志。'
  - 短链跳转失败的常见原因包括：短码不存在、短链被禁用、短链已过期、原始链接不可访问，或者系统触发了 Sentinel 限流。
- source_sentence: Redis 未命中时系统会怎么处理？
  sentences:
  - '# 自动向量化测试文档

    当短链缓存击穿风险较高时，可以使用互斥锁或逻辑过期策略，避免大量请求在缓存失效瞬间同时回源数据库。

    如果短链访问统计不适合同步写库，可以通过 RocketMQ 发送访问事件，由消费者异步更新 PV 和访问日志。'
  - 访问统计通常不适合放在跳转主链路中同步写库，可以通过 RocketMQ 发送访问事件，由消费者异步更新 PV 和访问日志。
  - 当短链访问请求到达系统时，后端会先校验短链状态和过期时间，再尝试从 Redis 读取原始链接。如果 Redis 未命中，则回源 MySQL 查询并回填缓存。
- source_sentence: PV 为什么异步更新？
  sentences:
  - 访问统计通常不适合放在跳转主链路中同步写库，可以通过 RocketMQ 发送访问事件，由消费者异步更新 PV 和访问日志。
  - 短链跳转失败的常见原因包括：短码不存在、短链被禁用、短链已过期、原始链接不可访问，或者系统触发了 Sentinel 限流。
  - 访问统计通常不适合放在跳转主链路中同步写库，可以通过 RocketMQ 发送访问事件，由消费者异步更新 PV 和访问日志。
- source_sentence: 短码无效和原始链接不可访问都属于什么问题？
  sentences:
  - 访问统计通常不适合放在跳转主链路中同步写库，可以通过 RocketMQ 发送访问事件，由消费者异步更新 PV 和访问日志。
  - 当短链访问请求到达系统时，后端会先校验短链状态和过期时间，再尝试从 Redis 读取原始链接。如果 Redis 未命中，则回源 MySQL 查询并回填缓存。
  - 短链跳转失败的常见原因包括：短码不存在、短链被禁用、短链已过期、原始链接不可访问，或者系统触发了 Sentinel 限流。
- source_sentence: 为什么缓存失效时不能让所有请求都查库？
  sentences:
  - 访问统计通常不适合放在跳转主链路中同步写库，可以通过 RocketMQ 发送访问事件，由消费者异步更新 PV 和访问日志。
  - '# 自动向量化测试文档

    当短链缓存击穿风险较高时，可以使用互斥锁或逻辑过期策略，避免大量请求在缓存失效瞬间同时回源数据库。

    如果短链访问统计不适合同步写库，可以通过 RocketMQ 发送访问事件，由消费者异步更新 PV 和访问日志。'
  - '# 自动向量化测试文档

    当短链缓存击穿风险较高时，可以使用互斥锁或逻辑过期策略，避免大量请求在缓存失效瞬间同时回源数据库。

    如果短链访问统计不适合同步写库，可以通过 RocketMQ 发送访问事件，由消费者异步更新 PV 和访问日志。'
pipeline_tag: sentence-similarity
library_name: sentence-transformers
---

# SentenceTransformer based on sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2

This is a [sentence-transformers](https://www.SBERT.net) model finetuned from [sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2](https://huggingface.co/sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2). It maps sentences & paragraphs to a 384-dimensional dense vector space and can be used for retrieval.

## Model Details

### Model Description
- **Model Type:** Sentence Transformer
- **Base model:** [sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2](https://huggingface.co/sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2) <!-- at revision e8f8c211226b894fcb81acc59f3b34ba3efd5f42 -->
- **Maximum Sequence Length:** 128 tokens
- **Output Dimensionality:** 384 dimensions
- **Similarity Function:** Cosine Similarity
- **Supported Modality:** Text
<!-- - **Training Dataset:** Unknown -->
<!-- - **Language:** Unknown -->
<!-- - **License:** Unknown -->

### Model Sources

- **Documentation:** [Sentence Transformers Documentation](https://sbert.net)
- **Repository:** [Sentence Transformers on GitHub](https://github.com/huggingface/sentence-transformers)
- **Hugging Face:** [Sentence Transformers on Hugging Face](https://huggingface.co/models?library=sentence-transformers)

### Full Model Architecture

```
SentenceTransformer(
  (0): Transformer({'transformer_task': 'feature-extraction', 'modality_config': {'text': {'method': 'forward', 'method_output_name': 'last_hidden_state'}}, 'module_output_name': 'token_embeddings', 'architecture': 'BertModel'})
  (1): Pooling({'embedding_dimension': 384, 'pooling_mode': 'mean', 'include_prompt': True})
)
```

## Usage

### Direct Usage (Sentence Transformers)

First install the Sentence Transformers library:

```bash
pip install -U sentence-transformers
```
Then you can load this model and run inference.
```python
from sentence_transformers import SentenceTransformer

# Download from the 🤗 Hub
model = SentenceTransformer("sentence_transformers_model_id")
# Run inference
sentences = [
    '为什么缓存失效时不能让所有请求都查库？',
    '# 自动向量化测试文档\n当短链缓存击穿风险较高时，可以使用互斥锁或逻辑过期策略，避免大量请求在缓存失效瞬间同时回源数据库。\n如果短链访问统计不适合同步写库，可以通过 RocketMQ 发送访问事件，由消费者异步更新 PV 和访问日志。',
    '# 自动向量化测试文档\n当短链缓存击穿风险较高时，可以使用互斥锁或逻辑过期策略，避免大量请求在缓存失效瞬间同时回源数据库。\n如果短链访问统计不适合同步写库，可以通过 RocketMQ 发送访问事件，由消费者异步更新 PV 和访问日志。',
]
embeddings = model.encode(sentences)
print(embeddings.shape)
# [3, 384]

# Get the similarity scores for the embeddings
similarities = model.similarity(embeddings, embeddings)
print(similarities)
# tensor([[1.0000, 0.6050, 0.6050],
#         [0.6050, 1.0000, 1.0000],
#         [0.6050, 1.0000, 1.0000]])
```
<!--
### Direct Usage (Transformers)

<details><summary>Click to see the direct usage in Transformers</summary>

</details>
-->

<!--
### Downstream Usage (Sentence Transformers)

You can finetune this model on your own dataset.

<details><summary>Click to expand</summary>

</details>
-->

<!--
### Out-of-Scope Use

*List how the model may foreseeably be misused and address what users ought not to do with the model.*
-->

<!--
## Bias, Risks and Limitations

*What are the known or foreseeable issues stemming from this model? You could also flag here known failure cases or weaknesses of the model.*
-->

<!--
### Recommendations

*What are recommendations with respect to the foreseeable issues? For example, filtering explicit content.*
-->

## Training Details

### Training Dataset

#### Unnamed Dataset

* Size: 56 training samples
* Columns: <code>sentence_0</code> and <code>sentence_1</code>
* Approximate statistics based on the first 56 samples:
  |          | sentence_0                                                                        | sentence_1                                                                        |
  |:---------|:----------------------------------------------------------------------------------|:----------------------------------------------------------------------------------|
  | type     | string                                                                            | string                                                                            |
  | modality | text                                                                              | text                                                                              |
  | details  | <ul><li>min: 9 tokens</li><li>mean: 14.75 tokens</li><li>max: 18 tokens</li></ul> | <ul><li>min: 41 tokens</li><li>mean: 54.3 tokens</li><li>max: 79 tokens</li></ul> |
* Samples:
  | sentence_0                          | sentence_1                                                                                                                               |
  |:------------------------------------|:-----------------------------------------------------------------------------------------------------------------------------------------|
  | <code>逻辑过期策略适合解决什么问题？</code>        | <code># 自动向量化测试文档<br>当短链缓存击穿风险较高时，可以使用互斥锁或逻辑过期策略，避免大量请求在缓存失效瞬间同时回源数据库。<br>如果短链访问统计不适合同步写库，可以通过 RocketMQ 发送访问事件，由消费者异步更新 PV 和访问日志。</code> |
  | <code>缓存击穿风险较高时应该怎么处理？</code>       | <code># 自动向量化测试文档<br>当短链缓存击穿风险较高时，可以使用互斥锁或逻辑过期策略，避免大量请求在缓存失效瞬间同时回源数据库。<br>如果短链访问统计不适合同步写库，可以通过 RocketMQ 发送访问事件，由消费者异步更新 PV 和访问日志。</code> |
  | <code>短链访问先查 Redis 还是 MySQL？</code> | <code>当短链访问请求到达系统时，后端会先校验短链状态和过期时间，再尝试从 Redis 读取原始链接。如果 Redis 未命中，则回源 MySQL 查询并回填缓存。</code>                                              |
* Loss: [<code>MultipleNegativesRankingLoss</code>](https://sbert.net/docs/package_reference/sentence_transformer/losses.html#multiplenegativesrankingloss) with these parameters:
  ```json
  {
      "scale": 20.0,
      "similarity_fct": "cos_sim",
      "gather_across_devices": false,
      "directions": [
          "query_to_doc"
      ],
      "partition_mode": "joint",
      "hardness_mode": null,
      "hardness_strength": 0.0
  }
  ```

### Training Hyperparameters
#### Non-Default Hyperparameters

- `multi_dataset_batch_sampler`: round_robin

#### All Hyperparameters
<details><summary>Click to expand</summary>

- `per_device_train_batch_size`: 8
- `num_train_epochs`: 3
- `max_steps`: -1
- `learning_rate`: 5e-05
- `lr_scheduler_type`: linear
- `lr_scheduler_kwargs`: None
- `warmup_steps`: 0
- `optim`: adamw_torch_fused
- `optim_args`: None
- `weight_decay`: 0.0
- `adam_beta1`: 0.9
- `adam_beta2`: 0.999
- `adam_epsilon`: 1e-08
- `optim_target_modules`: None
- `gradient_accumulation_steps`: 1
- `average_tokens_across_devices`: True
- `max_grad_norm`: 1
- `label_smoothing_factor`: 0.0
- `bf16`: False
- `fp16`: False
- `bf16_full_eval`: False
- `fp16_full_eval`: False
- `tf32`: None
- `gradient_checkpointing`: False
- `gradient_checkpointing_kwargs`: None
- `torch_compile`: False
- `torch_compile_backend`: None
- `torch_compile_mode`: None
- `use_liger_kernel`: False
- `liger_kernel_config`: None
- `use_cache`: False
- `neftune_noise_alpha`: None
- `torch_empty_cache_steps`: None
- `auto_find_batch_size`: False
- `log_on_each_node`: True
- `logging_nan_inf_filter`: True
- `include_num_input_tokens_seen`: no
- `log_level`: passive
- `log_level_replica`: warning
- `disable_tqdm`: False
- `project`: huggingface
- `trackio_space_id`: None
- `trackio_bucket_id`: None
- `trackio_static_space_id`: None
- `per_device_eval_batch_size`: 8
- `prediction_loss_only`: True
- `eval_on_start`: False
- `eval_do_concat_batches`: True
- `eval_use_gather_object`: False
- `eval_accumulation_steps`: None
- `include_for_metrics`: []
- `batch_eval_metrics`: False
- `save_only_model`: False
- `save_on_each_node`: False
- `enable_jit_checkpoint`: False
- `push_to_hub`: False
- `hub_private_repo`: None
- `hub_model_id`: None
- `hub_strategy`: every_save
- `hub_always_push`: False
- `hub_revision`: None
- `load_best_model_at_end`: False
- `ignore_data_skip`: False
- `restore_callback_states_from_checkpoint`: False
- `full_determinism`: False
- `seed`: 42
- `data_seed`: None
- `use_cpu`: False
- `accelerator_config`: {'split_batches': False, 'dispatch_batches': None, 'even_batches': True, 'use_seedable_sampler': True, 'non_blocking': False, 'gradient_accumulation_kwargs': None}
- `parallelism_config`: None
- `dataloader_drop_last`: False
- `dataloader_num_workers`: 0
- `dataloader_pin_memory`: True
- `dataloader_persistent_workers`: False
- `dataloader_prefetch_factor`: None
- `remove_unused_columns`: True
- `label_names`: None
- `train_sampling_strategy`: random
- `length_column_name`: length
- `ddp_find_unused_parameters`: None
- `ddp_bucket_cap_mb`: None
- `ddp_broadcast_buffers`: False
- `ddp_static_graph`: None
- `ddp_backend`: None
- `ddp_timeout`: 1800
- `fsdp`: []
- `fsdp_config`: {'min_num_params': 0, 'xla': False, 'xla_fsdp_v2': False, 'xla_fsdp_grad_ckpt': False}
- `deepspeed`: None
- `debug`: []
- `skip_memory_metrics`: True
- `do_predict`: False
- `resume_from_checkpoint`: None
- `warmup_ratio`: None
- `local_rank`: -1
- `prompts`: None
- `batch_sampler`: batch_sampler
- `multi_dataset_batch_sampler`: round_robin
- `router_mapping`: {}
- `learning_rate_mapping`: {}

</details>

### Training Time
- **Training**: 33.2 seconds

### Framework Versions
- Python: 3.10.20
- Sentence Transformers: 5.5.1
- Transformers: 5.9.0
- PyTorch: 2.12.0+cpu
- Accelerate: 1.13.0
- Datasets: 4.8.5
- Tokenizers: 0.22.2

## Citation

### BibTeX

#### Sentence Transformers
```bibtex
@inproceedings{reimers-2019-sentence-bert,
    title = "Sentence-BERT: Sentence Embeddings using Siamese BERT-Networks",
    author = "Reimers, Nils and Gurevych, Iryna",
    booktitle = "Proceedings of the 2019 Conference on Empirical Methods in Natural Language Processing",
    month = "11",
    year = "2019",
    publisher = "Association for Computational Linguistics",
    url = "https://arxiv.org/abs/1908.10084",
}
```

#### MultipleNegativesRankingLoss
```bibtex
@misc{oord2019representationlearningcontrastivepredictive,
      title={Representation Learning with Contrastive Predictive Coding},
      author={Aaron van den Oord and Yazhe Li and Oriol Vinyals},
      year={2019},
      eprint={1807.03748},
      archivePrefix={arXiv},
      primaryClass={cs.LG},
      url={https://arxiv.org/abs/1807.03748},
}
```

<!--
## Glossary

*Clearly define terms in order to be accessible across audiences.*
-->

<!--
## Model Card Authors

*Lists the people who create the model card, providing recognition and accountability for the detailed work that goes into its construction.*
-->

<!--
## Model Card Contact

*Provides a way for people who have updates to the Model Card, suggestions, or questions, to contact the Model Card authors.*
-->