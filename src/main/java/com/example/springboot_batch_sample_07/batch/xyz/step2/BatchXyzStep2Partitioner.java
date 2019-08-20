package com.example.springboot_batch_sample_07.batch.xyz.step2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import com.example.springboot_batch_sample_07.batch.xyz.entity.MultiExecMgrEntity;

public class BatchXyzStep2Partitioner implements Partitioner {

    private List<MultiExecMgrEntity> entityList;

    public List<MultiExecMgrEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<MultiExecMgrEntity> entityList) {
        this.entityList = entityList;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> map = new HashMap<>();

        List<String> dataKeys = entityList.stream().map(BatchXyzStep2Partitioner::getDataKeys).distinct().collect(Collectors.toList());
        int i = 0;
        for (String dataKey : dataKeys) {
            List<MultiExecMgrEntity> partitionedEntityList = entityList.stream().filter(entity -> getDataKeys(entity).equals(dataKey)).collect(Collectors.toList());
            ExecutionContext executionContext = new ExecutionContext();
            executionContext.put("partitionedEntityList", partitionedEntityList);
            map.put("BatchXyzStep2Partitioner:" + i++, executionContext);
        }

        return map;
    }

    private static String getDataKeys(MultiExecMgrEntity entity) {
        return String.join("_", String.valueOf(entity.getReqId()), String.valueOf(entity.getMultiProcNo()));
    }
}
