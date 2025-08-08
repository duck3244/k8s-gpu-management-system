-- 6시간 전 메트릭
INSERT INTO GPU_USAGE_METRICS VALUES ('metric-' || TO_CHAR(SYSDATE - 6/24, 'YYYYMMDDHH24MISS') || '-001', 'gpu-001-001', NULL, 'alloc-001', 82.1, 19968, 24576, 81.25, 70.2, 318.2, 63.0, 2095, 1745, 15420.8, 12650.3, 2, SYSDATE - 6/24, 'nvidia-smi', SYSDATE - 6/24, SYSDATE - 6/24);
INSERT INTO GPU_USAGE_METRICS VALUES ('metric-' || TO_CHAR(SYSDATE - 6/24, 'YYYYMMDDHH24MISS') || '-002', 'gpu-001-002', NULL, 'alloc-002', 87.9, 21504, 24576, 87.50, 69.8, 308.8, 64.0, 2085, 1735, 15320.5, 12450.2, 3, SYSDATE - 6/24, 'nvidia-smi', SYSDATE - 6/24, SYSDATE - 6/24);

-- 12시간 전 메트릭
INSERT INTO GPU_USAGE_METRICS VALUES ('metric-' || TO_CHAR(SYSDATE - 12/24, 'YYYYMMDDHH24MISS') || '-001', 'gpu-001-001', NULL, 'alloc-001', 79.5, 18944, 24576, 77.08, 68.9, 312.9, 61.0, 2080, 1730, 14950.2, 12100.8, 1, SYSDATE - 12/24, 'nvidia-smi', SYSDATE - 12/24, SYSDATE - 12/24);
INSERT INTO GPU_USAGE_METRICS VALUES ('metric-' || TO_CHAR(SYSDATE - 12/24, 'YYYYMMDDHH24MISS') || '-002', 'gpu-001-002', NULL, 'alloc-002', 85.2, 20480, 24576, 83.33, 68.5, 305.5, 62.0, 2075, 1725, 14850.8, 11950.5, 2, SYSDATE - 12/24, 'nvidia-smi', SYSDATE - 12/24, SYSDATE - 12/24);

-- ============================================================================
-- 10. 워크로드 프로파일 데이터
-- ============================================================================
INSERT INTO WORKLOAD_PROFILES VALUES ('profile-llm-training', 'Large Language Model Training', 'Training', 40, 80, '8.0', 'Ampere,Hopper', 'N', 1, '{"min_memory_bandwidth": "1500 GB/s", "min_fp16_performance": "100 TFLOPS"}', '{"max_power": "400W", "cooling": "active"}', 'low', '{"max_training_time": "7 days", "checkpoint_interval": "1 hour"}', 'Profile for training large language models with 7B+ parameters', 'ml-team-lead', SYSDATE, SYSDATE);

INSERT INTO WORKLOAD_PROFILES VALUES ('profile-inference-api', 'Real-time Inference API', 'Inference', 8, 16, '7.0', 'Turing,Ampere,Ada Lovelace', 'Y', 4, '{"max_latency": "50ms", "min_throughput": "1000 requests/sec"}', '{"shared_memory": "enabled", "batch_size": "max 32"}', 'high', '{"availability": "99.9%", "response_time": "< 100ms"}', 'Profile for real-time inference APIs with strict latency requirements', 'api-team-lead', SYSDATE, SYSDATE);

INSERT INTO WORKLOAD_PROFILES VALUES ('profile-research-notebook', 'Research Jupyter Notebook', 'Research', 4, 8, '6.1', 'Pascal,Turing,Ampere', 'N', 2, '{"interactive": true, "jupyter_support": true}', '{"cpu_cores": "4-8", "storage": "SSD preferred"}', 'medium', '{"session_timeout": "8 hours", "auto_save": "enabled"}', 'Profile for research and experimentation notebooks', 'research-lead', SYSDATE, SYSDATE);

INSERT INTO WORKLOAD_PROFILES VALUES ('profile-computer-vision', 'Computer Vision Training', 'Training', 16, 32, '7.5', 'Turing,Ampere,Ada Lovelace', 'Y', 2, '{"min_tensor_cores": 200, "image_processing": "optimized"}', '{"dataset_size": "< 1TB", "augmentation": "GPU accelerated"}', 'medium', '{"training_time": "< 48 hours", "accuracy_target": "> 95%"}', 'Profile for computer vision model training with image datasets', 'cv-team-lead', SYSDATE, SYSDATE);

INSERT INTO WORKLOAD_PROFILES VALUES ('profile-llm-inference', 'LLM Inference Service', 'Inference', 20, 40, '8.0', 'Ampere,Hopper', 'Y', 3, '{"model_size": "7B-70B", "quantization": "FP16/INT8"}', '{"context_length": "4096-32768", "batch_processing": "enabled"}', 'low', '{"throughput": "> 50 tokens/sec", "availability": "99.5%"}', 'Profile for serving large language models in production', 'llm-team-lead', SYSDATE, SYSDATE);

-- ============================================================================
-- 11. GPU 호환성 데이터
-- ============================================================================
INSERT INTO GPU_COMPATIBILITY VALUES ('compat-rtx4090-cuda12', 'RTX4090', '12.0', '525.60.13', '545.99.99', 'v1.25+', 'containerd', 'Ubuntu', '20.04/22.04', 'Y', 0.0, NULL, NULL, TO_DATE('2023-08-01', 'YYYY-MM-DD'), 'Fully compatible with CUDA 12.0 and latest drivers', SYSDATE);

INSERT INTO GPU_COMPATIBILITY VALUES ('compat-a100-cuda11', 'A100-40GB', '11.8', '515.65.01', '535.99.99', 'v1.24+', 'containerd', 'Ubuntu', '18.04/20.04/22.04', 'Y', 0.0, NULL, NULL, TO_DATE('2023-06-15', 'YYYY-MM-DD'), 'Recommended for production workloads', SYSDATE);

INSERT INTO GPU_COMPATIBILITY VALUES ('compat-h100-cuda12', 'H100', '12.0', '525.85.12', '545.99.99', 'v1.26+', 'containerd', 'Ubuntu', '20.04/22.04', 'Y', 0.0, NULL, NULL, TO_DATE('2023-09-01', 'YYYY-MM-DD'), 'Latest generation with full feature support', SYSDATE);

INSERT INTO GPU_COMPATIBILITY VALUES ('compat-v100-cuda11-legacy', 'V100', '11.8', '470.57.02', '515.99.99', 'v1.22+', 'docker', 'Ubuntu', '18.04/20.04', 'Y', 5.0, 'Performance degradation with newer CUDA versions', 'Use CUDA 11.2 for optimal performance', TO_DATE('2023-05-01', 'YYYY-MM-DD'), 'Legacy support - consider migration to newer GPUs', SYSDATE);

INSERT INTO GPU_COMPATIBILITY VALUES ('compat-rtx3080-k8s', 'RTX3080', '11.7', '510.47.03', '535.99.99', 'v1.25+', 'containerd', 'Ubuntu', '20.04/22.04', 'Y', 2.0, 'Occasional driver stability issues under heavy load', 'Update to driver 525+ for better stability', TO_DATE('2023-07-15', 'YYYY-MM-DD'), 'Good for development and testing workloads', SYSDATE);

-- ============================================================================
-- 12. GPU 벤치마크 데이터
-- ============================================================================
INSERT INTO GPU_BENCHMARKS VALUES ('bench-rtx4090-pytorch', 'RTX4090', 'ML_TRAINING', 'PyTorch ResNet-50', 1250.5, 'images/sec', 32, 'FP16', 'PyTorch 2.0', 120.0, TO_DATE('2023-08-15', 'YYYY-MM-DD'), 'Ubuntu 22.04, 32GB RAM, NVMe SSD', '{"cpu": "Intel i9-13900K", "ram": "32GB DDR5", "storage": "2TB NVMe"}', '{"pytorch": "2.0.1", "cuda": "12.1", "cudnn": "8.9"}', 'Excellent performance for training workloads', SYSDATE);

INSERT INTO GPU_BENCHMARKS VALUES ('bench-a100-transformer', 'A100-40GB', 'ML_TRAINING', 'BERT Large Training', 485.2, 'samples/sec', 16, 'FP16', 'PyTorch 2.0', 180.0, TO_DATE('2023-07-20', 'YYYY-MM-DD'), 'Ubuntu 20.04, 128GB RAM, NVMe SSD', '{"cpu": "AMD EPYC 7742", "ram": "128GB DDR4", "storage": "4TB NVMe"}', '{"pytorch": "2.0.0", "cuda": "11.8", "cudnn": "8.7"}', 'Optimal for large model training', SYSDATE);

INSERT INTO GPU_BENCHMARKS VALUES ('bench-h100-llm', 'H100', 'ML_TRAINING', 'LLaMA-7B Training', 2850.8, 'tokens/sec', 8, 'BF16', 'PyTorch 2.1', 300.0, TO_DATE('2023-09-10', 'YYYY-MM-DD'), 'Ubuntu 22.04, 256GB RAM, NVMe SSD', '{"cpu": "Intel Xeon Platinum 8480+", "ram": "256GB DDR5", "storage": "8TB NVMe"}', '{"pytorch": "2.1.0", "cuda": "12.2", "cudnn": "8.9"}', 'Best-in-class performance for LLM training', SYSDATE);

INSERT INTO GPU_BENCHMARKS VALUES ('bench-rtx4090-inference', 'RTX4090', 'ML_INFERENCE', 'YOLO v8 Object Detection', 1580.3, 'FPS', 1, 'FP16', 'TensorRT 8.6', 60.0, TO_DATE('2023-08-25', 'YYYY-MM-DD'), 'Ubuntu 22.04, 32GB RAM, NVMe SSD', '{"cpu": "Intel i9-13900K", "ram": "32GB DDR5", "storage": "2TB NVMe"}', '{"tensorrt": "8.6.1", "cuda": "12.1", "onnx": "1.14"}', 'Excellent for real-time inference applications', SYSDATE);

INSERT INTO GPU_BENCHMARKS VALUES ('bench-a100-mig-inference', 'A100-40GB', 'ML_INFERENCE', 'BERT Base Inference (MIG 1g.5gb)', 125.7, 'queries/sec', 8, 'FP16', 'TensorRT 8.5', 45.0, TO_DATE('2023-07-30', 'YYYY-MM-DD'), 'Ubuntu 20.04, 128GB RAM, NVMe SSD', '{"cpu": "AMD EPYC 7742", "ram": "128GB DDR4", "storage": "4TB NVMe"}', '{"tensorrt": "8.5.3", "cuda": "11.8", "triton": "2.35"}', 'Cost-effective inference with MIG partitioning', SYSDATE);

-- ============================================================================
-- 데이터 삽입 완료 및 통계 정보 업데이트
-- ============================================================================

-- 테이블 통계 정보 업데이트
EXEC DBMS_STATS.GATHER_SCHEMA_STATS('GPU_ADMIN');

-- 삽입된 데이터 확인
SELECT 'GPU_MODELS' AS TABLE_NAME, COUNT(*) AS ROW_COUNT FROM GPU_MODELS
UNION ALL
SELECT 'GPU_NODES', COUNT(*) FROM GPU_NODES
UNION ALL
SELECT 'GPU_DEVICES', COUNT(*) FROM GPU_DEVICES
UNION ALL
SELECT 'MIG_PROFILES', COUNT(*) FROM MIG_PROFILES
UNION ALL
SELECT 'MIG_INSTANCES', COUNT(*) FROM MIG_INSTANCES
UNION ALL
SELECT 'GPU_ALLOCATIONS', COUNT(*) FROM GPU_ALLOCATIONS
UNION ALL
SELECT 'ALERT_RULES', COUNT(*) FROM ALERT_RULES
UNION ALL
SELECT 'SYSTEM_ALERTS', COUNT(*) FROM SYSTEM_ALERTS
UNION ALL
SELECT 'GPU_USAGE_METRICS', COUNT(*) FROM GPU_USAGE_METRICS
UNION ALL
SELECT 'WORKLOAD_PROFILES', COUNT(*) FROM WORKLOAD_PROFILES
UNION ALL
SELECT 'GPU_COMPATIBILITY', COUNT(*) FROM GPU_COMPATIBILITY
UNION ALL
SELECT 'GPU_BENCHMARKS', COUNT(*) FROM GPU_BENCHMARKS
ORDER BY TABLE_NAME;

-- 클러스터별 통계 확인
SELECT 
    CLUSTER_NAME,
    COUNT(*) AS NODE_COUNT,
    SUM(TOTAL_GPUS) AS TOTAL_GPUS,
    SUM(AVAILABLE_GPUS) AS AVAILABLE_GPUS,
    ROUND(AVG(CASE WHEN TOTAL_GPUS > 0 THEN ((TOTAL_GPUS - AVAILABLE_GPUS) * 100.0 / TOTAL_GPUS) ELSE 0 END), 2) AS AVG_UTILIZATION_PCT
FROM GPU_NODES
WHERE NODE_STATUS = 'ACTIVE'
GROUP BY CLUSTER_NAME
ORDER BY CLUSTER_NAME;

-- GPU 모델별 통계 확인
SELECT 
    gm.MODEL_NAME,
    gm.ARCHITECTURE,
    COUNT(gd.DEVICE_ID) AS DEVICE_COUNT,
    SUM(CASE WHEN gd.DEVICE_STATUS IN ('ACTIVE', 'MIG_ENABLED') THEN 1 ELSE 0 END) AS ACTIVE_DEVICES
FROM GPU_MODELS gm
LEFT JOIN GPU_DEVICES gd ON gm.MODEL_ID = gd.MODEL_ID
GROUP BY gm.MODEL_NAME, gm.ARCHITECTURE
ORDER BY DEVICE_COUNT DESC;

-- 현재 할당 상태 확인
SELECT 
    RESOURCE_TYPE,
    STATUS,
    COUNT(*) AS ALLOCATION_COUNT,
    ROUND(AVG(CASE WHEN COST_PER_HOUR IS NOT NULL THEN COST_PER_HOUR ELSE 0 END), 2) AS AVG_COST_PER_HOUR
FROM GPU_ALLOCATIONS
GROUP BY RESOURCE_TYPE, STATUS
ORDER BY RESOURCE_TYPE, STATUS;

-- 최근 메트릭 평균값 확인
SELECT 
    'Current Hour' AS TIME_PERIOD,
    ROUND(AVG(GPU_UTILIZATION_PCT), 2) AS AVG_GPU_UTIL,
    ROUND(AVG(MEMORY_UTILIZATION_PCT), 2) AS AVG_MEM_UTIL,
    ROUND(AVG(TEMPERATURE_C), 2) AS AVG_TEMP,
    ROUND(AVG(POWER_DRAW_W), 2) AS AVG_POWER
FROM GPU_USAGE_METRICS
WHERE TIMESTAMP >= SYSDATE - 1/24;

COMMIT;

-- 샘플 데이터 삽입 완료 메시지
SELECT 'Sample data insertion completed successfully - ' || TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') AS MESSAGE FROM DUAL;============================================================================
-- K8s GPU Management System Sample Data
-- 테스트 및 데모용 샘플 데이터
-- ============================================================================

-- 기존 데이터 삭제 (순서 중요)
DELETE FROM GPU_USAGE_METRICS;
DELETE FROM GPU_ALLOCATIONS;
DELETE FROM SYSTEM_ALERTS;
DELETE FROM ALERT_RULES;
DELETE FROM GPU_BENCHMARKS;
DELETE FROM GPU_COMPATIBILITY;
DELETE FROM WORKLOAD_PROFILES;
DELETE FROM MIG_INSTANCES;
DELETE FROM MIG_PROFILES;
DELETE FROM GPU_DEVICES;
DELETE FROM GPU_NODES;
DELETE FROM GPU_MODELS;

-- ============================================================================
-- 1. GPU 모델 데이터
-- ============================================================================
INSERT INTO GPU_MODELS VALUES ('RTX4090', 'GeForce RTX 4090', 'NVIDIA', 'Ada Lovelace', 24, 16384, 512, 128, 2235, 2520, 1008.0, 'GDDR6X', 450, 'PCIe 4.0', 'N', 0, '8.9', 2022, 'Gaming', NULL, SYSDATE, SYSDATE);
INSERT INTO GPU_MODELS VALUES ('RTX4080', 'GeForce RTX 4080', 'NVIDIA', 'Ada Lovelace', 16, 9728, 304, 76, 2205, 2505, 716.8, 'GDDR6X', 320, 'PCIe 4.0', 'N', 0, '8.9', 2022, 'Gaming', NULL, SYSDATE, SYSDATE);
INSERT INTO GPU_MODELS VALUES ('RTX3090', 'GeForce RTX 3090', 'NVIDIA', 'Ampere', 24, 10496, 328, 82, 1395, 1695, 936.2, 'GDDR6X', 350, 'PCIe 4.0', 'N', 0, '8.6', 2020, 'Gaming', NULL, SYSDATE, SYSDATE);
INSERT INTO GPU_MODELS VALUES ('A100-40GB', 'Tesla A100 40GB', 'NVIDIA', 'Ampere', 40, 6912, 432, 0, 765, 1410, 1555.0, 'HBM2e', 400, 'PCIe 4.0', 'Y', 7, '8.0', 2020, 'Datacenter', NULL, SYSDATE, SYSDATE);
INSERT INTO GPU_MODELS VALUES ('A100-80GB', 'Tesla A100 80GB', 'NVIDIA', 'Ampere', 80, 6912, 432, 0, 765, 1410, 2039.0, 'HBM2e', 400, 'PCIe 4.0', 'Y', 7, '8.0', 2021, 'Datacenter', NULL, SYSDATE, SYSDATE);
INSERT INTO GPU_MODELS VALUES ('H100', 'Tesla H100', 'NVIDIA', 'Hopper', 80, 16896, 528, 0, 1350, 1780, 3350.0, 'HBM3', 700, 'PCIe 5.0', 'Y', 7, '9.0', 2022, 'Datacenter', NULL, SYSDATE, SYSDATE);
INSERT INTO GPU_MODELS VALUES ('V100', 'Tesla V100', 'NVIDIA', 'Volta', 32, 5120, 640, 0, 1245, 1370, 900.0, 'HBM2', 300, 'PCIe 3.0', 'N', 0, '7.0', 2017, 'Datacenter', TO_DATE('2025-12-31', 'YYYY-MM-DD'), SYSDATE, SYSDATE);
INSERT INTO GPU_MODELS VALUES ('RTX3080', 'GeForce RTX 3080', 'NVIDIA', 'Ampere', 10, 8704, 272, 68, 1440, 1710, 760.3, 'GDDR6X', 320, 'PCIe 4.0', 'N', 0, '8.6', 2020, 'Gaming', NULL, SYSDATE, SYSDATE);

-- ============================================================================
-- 2. MIG 프로파일 데이터 (A100, H100용)
-- ============================================================================
INSERT INTO MIG_PROFILES VALUES ('A100-1g5gb', 'A100-40GB', '1g.5gb', 1, 1, 5, 7, 14.3, 'Light Inference', 'Small inference workloads', SYSDATE);
INSERT INTO MIG_PROFILES VALUES ('A100-2g10gb', 'A100-40GB', '2g.10gb', 2, 2, 10, 3, 28.6, 'Medium Inference', 'Medium inference workloads', SYSDATE);
INSERT INTO MIG_PROFILES VALUES ('A100-3g20gb', 'A100-40GB', '3g.20gb', 3, 4, 20, 2, 42.9, 'Large Inference', 'Large inference workloads', SYSDATE);
INSERT INTO MIG_PROFILES VALUES ('A100-4g20gb', 'A100-40GB', '4g.20gb', 4, 4, 20, 1, 57.1, 'Training', 'Small training workloads', SYSDATE);
INSERT INTO MIG_PROFILES VALUES ('A100-7g40gb', 'A100-40GB', '7g.40gb', 7, 8, 40, 1, 100.0, 'Full Training', 'Full GPU for training', SYSDATE);

INSERT INTO MIG_PROFILES VALUES ('H100-1g10gb', 'H100', '1g.10gb', 1, 1, 10, 7, 14.3, 'Light Inference', 'Small inference workloads for H100', SYSDATE);
INSERT INTO MIG_PROFILES VALUES ('H100-2g20gb', 'H100', '2g.20gb', 2, 2, 20, 3, 28.6, 'Medium Inference', 'Medium inference workloads for H100', SYSDATE);
INSERT INTO MIG_PROFILES VALUES ('H100-3g40gb', 'H100', '3g.40gb', 3, 4, 40, 2, 42.9, 'Large Inference', 'Large inference workloads for H100', SYSDATE);
INSERT INTO MIG_PROFILES VALUES ('H100-4g40gb', 'H100', '4g.40gb', 4, 4, 40, 1, 57.1, 'Training', 'Training workloads for H100', SYSDATE);
INSERT INTO MIG_PROFILES VALUES ('H100-7g80gb', 'H100', '7g.80gb', 7, 8, 80, 1, 100.0, 'Full Training', 'Full H100 for large training', SYSDATE);

-- ============================================================================
-- 3. GPU 노드 데이터
-- ============================================================================
INSERT INTO GPU_NODES VALUES ('node-gpu-001', 'k8s-gpu-worker-01', 'gpu-cluster-prod', '192.168.1.101', 8, 3, 'ACTIVE', 'v1.28.2', '24.0.5', '535.154.05', '{"gpu.type":"RTX4090","zone":"us-west-1a","instance.type":"g5.8xlarge"}', '{"nvidia.com/gpu":"NoSchedule"}', SYSDATE, SYSDATE);
INSERT INTO GPU_NODES VALUES ('node-gpu-002', 'k8s-gpu-worker-02', 'gpu-cluster-prod', '192.168.1.102', 8, 5, 'ACTIVE', 'v1.28.2', '24.0.5', '535.154.05', '{"gpu.type":"RTX4090","zone":"us-west-1b","instance.type":"g5.8xlarge"}', '{"nvidia.com/gpu":"NoSchedule"}', SYSDATE, SYSDATE);
INSERT INTO GPU_NODES VALUES ('node-gpu-003', 'k8s-gpu-worker-03', 'gpu-cluster-prod', '192.168.1.103', 4, 1, 'ACTIVE', 'v1.28.2', '24.0.5', '535.154.05', '{"gpu.type":"A100","zone":"us-west-1a","instance.type":"p4d.2xlarge"}', '{"nvidia.com/gpu":"NoSchedule"}', SYSDATE, SYSDATE);
INSERT INTO GPU_NODES VALUES ('node-gpu-004', 'k8s-gpu-worker-04', 'gpu-cluster-prod', '192.168.1.104', 4, 2, 'ACTIVE', 'v1.28.2', '24.0.5', '535.154.05', '{"gpu.type":"A100","zone":"us-west-1b","instance.type":"p4d.2xlarge"}', '{"nvidia.com/gpu":"NoSchedule"}', SYSDATE, SYSDATE);
INSERT INTO GPU_NODES VALUES ('node-gpu-005', 'k8s-gpu-worker-05', 'gpu-cluster-dev', '192.168.2.101', 4, 4, 'ACTIVE', 'v1.28.2', '24.0.5', '535.154.05', '{"gpu.type":"RTX3080","zone":"us-west-1a","env":"dev"}', '{"nvidia.com/gpu":"NoSchedule"}', SYSDATE, SYSDATE);
INSERT INTO GPU_NODES VALUES ('node-gpu-006', 'k8s-gpu-worker-06', 'gpu-cluster-dev', '192.168.2.102', 4, 2, 'ACTIVE', 'v1.28.2', '24.0.5', '535.154.05', '{"gpu.type":"RTX3080","zone":"us-west-1b","env":"dev"}', '{"nvidia.com/gpu":"NoSchedule"}', SYSDATE, SYSDATE);
INSERT INTO GPU_NODES VALUES ('node-gpu-007', 'k8s-gpu-worker-07', 'gpu-cluster-research', '192.168.3.101', 2, 0, 'ACTIVE', 'v1.28.2', '24.0.5', '535.154.05', '{"gpu.type":"H100","zone":"us-west-1a","env":"research"}', '{"nvidia.com/gpu":"NoSchedule"}', SYSDATE, SYSDATE);
INSERT INTO GPU_NODES VALUES ('node-gpu-008', 'k8s-gpu-worker-08', 'gpu-cluster-prod', '192.168.1.105', 4, 0, 'MAINTENANCE', 'v1.28.2', '24.0.5', '535.154.05', '{"gpu.type":"V100","zone":"us-west-1c","maintenance":"scheduled"}', '{"nvidia.com/gpu":"NoSchedule","maintenance":"true"}', SYSDATE, SYSDATE);

-- ============================================================================
-- 4. GPU 장비 데이터
-- ============================================================================
-- node-gpu-001 (RTX 4090 x8)
INSERT INTO GPU_DEVICES VALUES ('gpu-001-001', 'node-gpu-001', 'RTX4090', 0, 'RTX4090-001-SN', '0000:01:00.0', 'GPU-12345678-1234-1234-1234-123456789001', 'ACTIVE', 72.5, 83.0, 320.5, 450.0, '535.154.05', 'v1.2.3', 'v1.0.1', TO_DATE('2023-01-15', 'YYYY-MM-DD'), NULL, TO_DATE('2026-01-15', 'YYYY-MM-DD'), 1599.99, 36, SYSDATE, SYSDATE);
INSERT INTO GPU_DEVICES VALUES ('gpu-001-002', 'node-gpu-001', 'RTX4090', 1, 'RTX4090-002-SN', '0000:02:00.0', 'GPU-12345678-1234-1234-1234-123456789002', 'ACTIVE', 71.2, 83.0, 315.2, 450.0, '535.154.05', 'v1.2.3', 'v1.0.1', TO_DATE('2023-01-15', 'YYYY-MM-DD'), NULL, TO_DATE('2026-01-15', 'YYYY-MM-DD'), 1599.99, 36, SYSDATE, SYSDATE);
INSERT INTO GPU_DEVICES VALUES ('gpu-001-003', 'node-gpu-001', 'RTX4090', 2, 'RTX4090-003-SN', '0000:03:00.0', 'GPU-12345678-1234-1234-1234-123456789003', 'ACTIVE', 69.8, 83.0, 305.8, 450.0, '535.154.05', 'v1.2.3', 'v1.0.1', TO_DATE('2023-01-15', 'YYYY-MM-DD'), NULL, TO_DATE('2026-01-15', 'YYYY-MM-DD'), 1599.99, 36, SYSDATE, SYSDATE);
INSERT INTO GPU_DEVICES VALUES ('gpu-001-004', 'node-gpu-001', 'RTX4090', 3, 'RTX4090-004-SN', '0000:04:00.0', 'GPU-12345678-1234-1234-1234-123456789004', 'ACTIVE', 73.1, 83.0, 325.1, 450.0, '535.154.05', 'v1.2.3', 'v1.0.1', TO_DATE('2023-01-15', 'YYYY-MM-DD'), NULL, TO_DATE('2026-01-15', 'YYYY-MM-DD'), 1599.99, 36, SYSDATE, SYSDATE);
INSERT INTO GPU_DEVICES VALUES ('gpu-001-005', 'node-gpu-001', 'RTX4090', 4, 'RTX4090-005-SN', '0000:05:00.0', 'GPU-12345678-1234-1234-1234-123456789005', 'ACTIVE', 70.5, 83.0, 310.5, 450.0, '535.154.05', 'v1.2.3', 'v1.0.1', TO_DATE('2023-01-15', 'YYYY-MM-DD'), NULL, TO_DATE('2026-01-15', 'YYYY-MM-DD'), 1599.99, 36, SYSDATE, SYSDATE);
INSERT INTO GPU_DEVICES VALUES ('gpu-001-006', 'node-gpu-001', 'RTX4090', 5, 'RTX4090-006-SN', '0000:06:00.0', 'GPU-12345678-1234-1234-1234-123456789006', 'ACTIVE', 74.2, 83.0, 330.2, 450.0, '535.154.05', 'v1.2.3', 'v1.0.1', TO_DATE('2023-01-15', 'YYYY-MM-DD'), NULL, TO_DATE('2026-01-15', 'YYYY-MM-DD'), 1599.99, 36, SYSDATE, SYSDATE);
INSERT INTO GPU_DEVICES VALUES ('gpu-001-007', 'node-gpu-001', 'RTX4090', 6, 'RTX4090-007-SN', '0000:07:00.0', 'GPU-12345678-1234-1234-1234-123456789007', 'ACTIVE', 68.9, 83.0, 298.9, 450.0, '535.154.05', 'v1.2.3', 'v1.0.1', TO_DATE('2023-01-15', 'YYYY-MM-DD'), NULL, TO_DATE('2026-01-15', 'YYYY-MM-DD'), 1599.99, 36, SYSDATE, SYSDATE);
INSERT INTO GPU_DEVICES VALUES ('gpu-001-008', 'node-gpu-001', 'RTX4090', 7, 'RTX4090-008-SN', '0000:08:00.0', 'GPU-12345678-1234-1234-1234-123456789008', 'ACTIVE', 72.8, 83.0, 318.8, 450.0, '535.154.05', 'v1.2.3', 'v1.0.1', TO_DATE('2023-01-15', 'YYYY-MM-DD'), NULL, TO_DATE('2026-01-15', 'YYYY-MM-DD'), 1599.99, 36, SYSDATE, SYSDATE);

-- node-gpu-003 (A100-40GB x4)
INSERT INTO GPU_DEVICES VALUES ('gpu-003-001', 'node-gpu-003', 'A100-40GB', 0, 'A100-001-SN', '0000:01:00.0', 'GPU-A1001234-1234-1234-1234-123456789001', 'MIG_ENABLED', 65.5, 85.0, 380.5, 400.0, '535.154.05', 'v2.1.5', 'v2.0.3', TO_DATE('2023-03-10', 'YYYY-MM-DD'), NULL, TO_DATE('2026-03-10', 'YYYY-MM-DD'), 15000.00, 36, SYSDATE, SYSDATE);
INSERT INTO GPU_DEVICES VALUES ('gpu-003-002', 'node-gpu-003', 'A100-40GB', 1, 'A100-002-SN', '0000:02:00.0', 'GPU-A1001234-1234-1234-1234-123456789002', 'MIG_ENABLED', 64.2, 85.0, 375.2, 400.0, '535.154.05', 'v2.1.5', 'v2.0.3', TO_DATE('2023-03-10', 'YYYY-MM-DD'), NULL, TO_DATE('2026-03-10', 'YYYY-MM-DD'), 15000.00, 36, SYSDATE, SYSDATE);
INSERT INTO GPU_DEVICES VALUES ('gpu-003-003', 'node-gpu-003', 'A100-40GB', 2, 'A100-003-SN', '0000:03:00.0', 'GPU-A1001234-1234-1234-1234-123456789003', 'ACTIVE', 63.8, 85.0, 370.8, 400.0, '535.154.05', 'v2.1.5', 'v2.0.3', TO_DATE('2023-03-10', 'YYYY-MM-DD'), NULL, TO_DATE('2026-03-10', 'YYYY-MM-DD'), 15000.00, 36, SYSDATE, SYSDATE);
INSERT INTO GPU_DEVICES VALUES ('gpu-003-004', 'node-gpu-003', 'A100-40GB', 3, 'A100-004-SN', '0000:04:00.0', 'GPU-A1001234-1234-1234-1234-123456789004', 'ACTIVE', 66.1, 85.0, 385.1, 400.0, '535.154.05', 'v2.1.5', 'v2.0.3', TO_DATE('2023-03-10', 'YYYY-MM-DD'), NULL, TO_DATE('2026-03-10', 'YYYY-MM-DD'), 15000.00, 36, SYSDATE, SYSDATE);

-- node-gpu-007 (H100 x2)
INSERT INTO GPU_DEVICES VALUES ('gpu-007-001', 'node-gpu-007', 'H100', 0, 'H100-001-SN', '0000:01:00.0', 'GPU-H1001234-1234-1234-1234-123456789001', 'MIG_ENABLED', 75.5, 90.0, 650.5, 700.0, '535.154.05', 'v3.0.1', 'v3.0.0', TO_DATE('2023-06-01', 'YYYY-MM-DD'), NULL, TO_DATE('2026-06-01', 'YYYY-MM-DD'), 25000.00, 36, SYSDATE, SYSDATE);
INSERT INTO GPU_DEVICES VALUES ('gpu-007-002', 'node-gpu-007', 'H100', 1, 'H100-002-SN', '0000:02:00.0', 'GPU-H1001234-1234-1234-1234-123456789002', 'MIG_ENABLED', 74.2, 90.0, 645.2, 700.0, '535.154.05', 'v3.0.1', 'v3.0.0', TO_DATE('2023-06-01', 'YYYY-MM-DD'), NULL, TO_DATE('2026-06-01', 'YYYY-MM-DD'), 25000.00, 36, SYSDATE, SYSDATE);

-- 추가 GPU 장비들 (간략히)
INSERT INTO GPU_DEVICES VALUES ('gpu-002-001', 'node-gpu-002', 'RTX4090', 0, 'RTX4090-021-SN', '0000:01:00.0', 'GPU-12345678-1234-1234-1234-123456789021', 'ACTIVE', 71.5, 83.0, 315.5, 450.0, '535.154.05', 'v1.2.3', 'v1.0.1', TO_DATE('2023-01-20', 'YYYY-MM-DD'), NULL, TO_DATE('2026-01-20', 'YYYY-MM-DD'), 1599.99, 36, SYSDATE, SYSDATE);
INSERT INTO GPU_DEVICES VALUES ('gpu-005-001', 'node-gpu-005', 'RTX3080', 0, 'RTX3080-051-SN', '0000:01:00.0', 'GPU-30801234-1234-1234-1234-123456789051', 'ACTIVE', 68.5, 83.0, 285.5, 320.0, '535.154.05', 'v1.1.8', 'v1.0.0', TO_DATE('2022-08-15', 'YYYY-MM-DD'), NULL, TO_DATE('2025-08-15', 'YYYY-MM-DD'), 699.99, 36, SYSDATE, SYSDATE);

-- ============================================================================
-- 5. MIG 인스턴스 데이터
-- ============================================================================
-- A100 GPU에 MIG 인스턴스 생성
INSERT INTO MIG_INSTANCES VALUES ('mig-gpu-003-001-01', 'gpu-003-001', 'A100-1g5gb', 0, 'MIG-GPU-003-001-01-UUID', 'Y', 'ACTIVE', SYSDATE, SYSDATE, SYSDATE);
INSERT INTO MIG_INSTANCES VALUES ('mig-gpu-003-001-02', 'gpu-003-001', 'A100-1g5gb', 1, 'MIG-GPU-003-001-02-UUID', 'Y', 'ACTIVE', SYSDATE, SYSDATE, SYSDATE);
INSERT INTO MIG_INSTANCES VALUES ('mig-gpu-003-001-03', 'gpu-003-001', 'A100-2g10gb', 2, 'MIG-GPU-003-001-03-UUID', 'Y', 'ACTIVE', SYSDATE, SYSDATE, SYSDATE);
INSERT INTO MIG_INSTANCES VALUES ('mig-gpu-003-001-04', 'gpu-003-001', 'A100-3g20gb', 3, 'MIG-GPU-003-001-04-UUID', 'N', 'ACTIVE', SYSDATE, NULL, NULL);

INSERT INTO MIG_INSTANCES VALUES ('mig-gpu-003-002-01', 'gpu-003-002', 'A100-4g20gb', 0, 'MIG-GPU-003-002-01-UUID', 'Y', 'ACTIVE', SYSDATE, SYSDATE, SYSDATE);
INSERT INTO MIG_INSTANCES VALUES ('mig-gpu-003-002-02', 'gpu-003-002', 'A100-3g20gb', 1, 'MIG-GPU-003-002-02-UUID', 'Y', 'ACTIVE', SYSDATE, SYSDATE, SYSDATE);

-- H100 GPU에 MIG 인스턴스 생성
INSERT INTO MIG_INSTANCES VALUES ('mig-gpu-007-001-01', 'gpu-007-001', 'H100-2g20gb', 0, 'MIG-GPU-007-001-01-UUID', 'Y', 'ACTIVE', SYSDATE, SYSDATE, SYSDATE);
INSERT INTO MIG_INSTANCES VALUES ('mig-gpu-007-001-02', 'gpu-007-001', 'H100-2g20gb', 1, 'MIG-GPU-007-001-02-UUID', 'Y', 'ACTIVE', SYSDATE, SYSDATE, SYSDATE);
INSERT INTO MIG_INSTANCES VALUES ('mig-gpu-007-001-03', 'gpu-007-001', 'H100-3g40gb', 2, 'MIG-GPU-007-001-03-UUID', 'Y', 'ACTIVE', SYSDATE, SYSDATE, SYSDATE);

INSERT INTO MIG_INSTANCES VALUES ('mig-gpu-007-002-01', 'gpu-007-002', 'H100-7g80gb', 0, 'MIG-GPU-007-002-01-UUID', 'Y', 'ACTIVE', SYSDATE, SYSDATE, SYSDATE);

-- ============================================================================
-- 6. GPU 할당 데이터
-- ============================================================================
INSERT INTO GPU_ALLOCATIONS VALUES ('alloc-001', 'ai-training', 'pytorch-training-001', 'pytorch-container', 'Training', 'FULL_GPU', 'gpu-001-001', 24, 24, 'high', SYSDATE, SYSDATE + 7, NULL, 'ALLOCATED', 2.50, 420.00, 'ai-team-001', 'ml-team', 'project-nlp-001', SYSDATE, SYSDATE);
INSERT INTO GPU_ALLOCATIONS VALUES ('alloc-002', 'ai-training', 'pytorch-training-002', 'pytorch-container', 'Training', 'FULL_GPU', 'gpu-001-002', 24, 24, 'high', SYSDATE, SYSDATE + 7, NULL, 'ALLOCATED', 2.50, 420.00, 'ai-team-002', 'ml-team', 'project-nlp-001', SYSDATE, SYSDATE);
INSERT INTO GPU_ALLOCATIONS VALUES ('alloc-003', 'ai-inference', 'inference-service-001', 'inference-container', 'Inference', 'MIG_INSTANCE', 'mig-gpu-003-001-01', 5, 5, 'medium', SYSDATE, SYSDATE + 30, NULL, 'ALLOCATED', 0.75, 540.00, 'inference-team', 'ai-team', 'project-recommendation', SYSDATE, SYSDATE);
INSERT INTO GPU_ALLOCATIONS VALUES ('alloc-004', 'ai-inference', 'inference-service-002', 'inference-container', 'Inference', 'MIG_INSTANCE', 'mig-gpu-003-001-02', 5, 5, 'medium', SYSDATE, SYSDATE + 30, NULL, 'ALLOCATED', 0.75, 540.00, 'inference-team', 'ai-team', 'project-recommendation', SYSDATE, SYSDATE);
INSERT INTO GPU_ALLOCATIONS VALUES ('alloc-005', 'research', 'research-notebook-001', 'jupyter-container', 'Research', 'MIG_INSTANCE', 'mig-gpu-007-001-01', 20, 20, 'low', SYSDATE, SYSDATE + 14, NULL, 'ALLOCATED', 1.25, 420.00, 'researcher-001', 'research-team', 'project-vision-transformer', SYSDATE, SYSDATE);
INSERT INTO GPU_ALLOCATIONS VALUES ('alloc-006', 'development', 'dev-training-001', 'pytorch-container', 'Development', 'FULL_GPU', 'gpu-005-001', 10, 10, 'low', SYSDATE, SYSDATE + 3, NULL, 'ALLOCATED', 1.00, 72.00, 'dev-team-001', 'dev-team', 'project-prototype', SYSDATE, SYSDATE);

-- 과거 할당 이력 (해제된 것들)
INSERT INTO GPU_ALLOCATIONS VALUES ('alloc-101', 'ai-training', 'completed-training-001', 'pytorch-container', 'Training', 'FULL_GPU', 'gpu-001-003', 24, 24, 'high', SYSDATE - 10, SYSDATE - 3, SYSDATE - 3, 'RELEASED', 2.50, 420.00, 'ai-team-003', 'ml-team', 'project-completed', SYSDATE - 10, SYSDATE);

-- ============================================================================
-- 7. 알림 규칙 데이터
-- ============================================================================
INSERT INTO ALERT_RULES VALUES ('rule-temp-high', 'GPU Temperature High', 'TEMPERATURE', 'DEVICE', 'device_status=ACTIVE', 80.0, '>', 'HIGH', 5, 'email,slack', 'Y', SYSDATE, SYSDATE);
INSERT INTO ALERT_RULES VALUES ('rule-power-critical', 'GPU Power Critical', 'POWER', 'DEVICE', 'device_status=ACTIVE', 400.0, '>', 'CRITICAL', 1, 'email,slack,pagerduty', 'Y', SYSDATE, SYSDATE);
INSERT INTO ALERT_RULES VALUES ('rule-utilization-low', 'GPU Utilization Low', 'UTILIZATION', 'DEVICE', 'allocation_status=ALLOCATED', 10.0, '<', 'MEDIUM', 30, 'email', 'Y', SYSDATE, SYSDATE);
INSERT INTO ALERT_RULES VALUES ('rule-memory-high', 'GPU Memory High', 'MEMORY', 'DEVICE', 'device_status=ACTIVE', 90.0, '>', 'HIGH', 5, 'email,slack', 'Y', SYSDATE, SYSDATE);
INSERT INTO ALERT_RULES VALUES ('rule-node-gpu-low', 'Node Available GPU Low', 'NODE_CAPACITY', 'NODE', 'node_status=ACTIVE', 1.0, '<=', 'MEDIUM', 15, 'email', 'Y', SYSDATE, SYSDATE);

-- ============================================================================
-- 8. 시스템 알림 데이터
-- ============================================================================
INSERT INTO SYSTEM_ALERTS VALUES ('alert-001', 'rule-temp-high', 'TEMPERATURE', 'HIGH', 'DEVICE', 'gpu-001-006', 'GPU 온도가 임계값 80°C를 초과했습니다 (현재: 84.2°C)', 'Device: gpu-001-006, Node: node-gpu-001, Temperature: 84.2°C', 84.2, 80.0, 'ACTIVE', SYSDATE, NULL, NULL, NULL, NULL);
INSERT INTO SYSTEM_ALERTS VALUES ('alert-002', 'rule-power-critical', 'POWER', 'CRITICAL', 'DEVICE', 'gpu-007-001', 'GPU 전력 소모가 위험 수준입니다 (현재: 685.5W)', 'Device: gpu-007-001, Node: node-gpu-007, Power: 685.5W', 685.5, 400.0, 'ACKNOWLEDGED', SYSDATE - 1, SYSDATE, 'admin', NULL, NULL);
INSERT INTO SYSTEM_ALERTS VALUES ('alert-003', 'rule-utilization-low', 'UTILIZATION', 'MEDIUM', 'DEVICE', 'gpu-001-008', 'GPU 사용률이 낮습니다. 자원 낭비가 의심됩니다', 'Device: gpu-001-008, Allocation: alloc-001, Utilization: 8.5%', 8.5, 10.0, 'ACTIVE', SYSDATE, NULL, NULL, NULL, NULL);
INSERT INTO SYSTEM_ALERTS VALUES ('alert-004', 'rule-node-gpu-low', 'NODE_CAPACITY', 'MEDIUM', 'NODE', 'node-gpu-001', '노드의 사용 가능한 GPU가 부족합니다', 'Node: node-gpu-001, Available GPUs: 1', 1.0, 1.0, 'RESOLVED', SYSDATE - 2, SYSDATE - 1, 'admin', SYSDATE, 'admin');

-- ============================================================================
-- 9. GPU 사용량 메트릭 데이터 (최근 24시간)
-- ============================================================================
-- 현재 시점 메트릭
INSERT INTO GPU_USAGE_METRICS VALUES ('metric-' || TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS') || '-001', 'gpu-001-001', NULL, 'alloc-001', 85.7, 20480, 24576, 83.33, 72.5, 320.5, 65.0, 2100, 1750, 15230.5, 12450.2, 2, SYSDATE, 'nvidia-smi', SYSDATE, SYSDATE);
INSERT INTO GPU_USAGE_METRICS VALUES ('metric-' || TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS') || '-002', 'gpu-001-002', NULL, 'alloc-002', 92.3, 22528, 24576, 91.67, 71.2, 315.2, 68.0, 2095, 1745, 16125.8, 13200.1, 3, SYSDATE, 'nvidia-smi', SYSDATE, SYSDATE);
INSERT INTO GPU_USAGE_METRICS VALUES ('metric-' || TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS') || '-003', 'gpu-003-001', 'mig-gpu-003-001-01', 'alloc-003', 78.5, 4608, 5120, 90.00, 65.5, 95.2, 45.0, 1350, 1215, 8950.3, 7200.5, 1, SYSDATE, 'nvidia-smi', SYSDATE, SYSDATE);

-- 1시간 전 메트릭
INSERT INTO GPU_USAGE_METRICS VALUES ('metric-' || TO_CHAR(SYSDATE - 1/24, 'YYYYMMDDHH24MISS') || '-001', 'gpu-001-001', NULL, 'alloc-001', 88.2, 21504, 24576, 87.50, 71.8, 325.8, 67.0, 2105, 1755, 15850.2, 12980.5, 2, SYSDATE - 1/24, 'nvidia-smi', SYSDATE - 1/24, SYSDATE - 1/24);
INSERT INTO GPU_USAGE_METRICS VALUES ('metric-' || TO_CHAR(SYSDATE - 1/24, 'YYYYMMDDHH24MISS') || '-002', 'gpu-001-002', NULL, 'alloc-002', 90.1, 22016, 24576, 89.58, 70.5, 310.5, 66.0, 2090, 1740, 15720.1, 12850.8, 3, SYSDATE - 1/24, 'nvidia-smi', SYSDATE - 1/24, SYSDATE - 1/24);

--