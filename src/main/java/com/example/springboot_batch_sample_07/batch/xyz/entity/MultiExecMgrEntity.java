package com.example.springboot_batch_sample_07.batch.xyz.entity;

import lombok.Data;

@Data
public class MultiExecMgrEntity {

    private Integer id;
    private Integer reqId;
    private Integer multiProcNo;
    private String procTarget;
    private String procType;
    private String states;
    private String errorInfo;
}
