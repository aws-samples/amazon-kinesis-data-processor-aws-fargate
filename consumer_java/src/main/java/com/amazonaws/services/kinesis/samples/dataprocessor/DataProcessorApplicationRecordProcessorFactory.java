/**
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */

package com.amazonaws.services.kinesis.samples.dataprocessor;

import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;

public class DataProcessorApplicationRecordProcessorFactory implements ShardRecordProcessorFactory{
    /**
     * {@inheritDoc}
     */
    @Override
    public ShardRecordProcessor shardRecordProcessor() {
        return new DataProcessorApplicationRecordProcessor();
    }

}