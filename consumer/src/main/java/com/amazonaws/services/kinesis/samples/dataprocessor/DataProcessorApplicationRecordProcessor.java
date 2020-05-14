/**
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not
 * use this file except in compliance with the License. A copy of the License
 * is located at
 *
 *     http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */

package com.amazonaws.services.kinesis.samples.dataprocessor;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import software.amazon.kinesis.exceptions.InvalidStateException;
import software.amazon.kinesis.exceptions.ShutdownException;
import software.amazon.kinesis.exceptions.ThrottlingException;
import software.amazon.kinesis.lifecycle.events.InitializationInput;
import software.amazon.kinesis.lifecycle.events.LeaseLostInput;
import software.amazon.kinesis.processor.RecordProcessorCheckpointer;
import software.amazon.kinesis.lifecycle.events.ProcessRecordsInput;
import software.amazon.kinesis.lifecycle.events.ShardEndedInput;
import software.amazon.kinesis.lifecycle.events.ShutdownRequestedInput;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.retrieval.KinesisClientRecord;

public class DataProcessorApplicationRecordProcessor implements ShardRecordProcessor{
    private static final Log logger = LogFactory.getLog("DataProcessorApplicationRecordProcessor");

    private String kinesisShardId;

     // Reporting interval
     private static final long REPORTING_INTERVAL_MILLIS = 60000L; // 1 minute
     private long nextReportingTimeInMillis;
 
     // Checkpointing interval
     private static final long CHECKPOINT_INTERVAL_MILLIS = 60000L; // 1 minute
     private long nextCheckpointTimeInMillis;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(InitializationInput initializationInput) {
        kinesisShardId = initializationInput.shardId();
        logger.info("Initializing record processor for shard: " + kinesisShardId);
        logger.info("Initializing @ Sequence: " + initializationInput.extendedSequenceNumber().toString());

        nextReportingTimeInMillis = System.currentTimeMillis() + REPORTING_INTERVAL_MILLIS;
        nextCheckpointTimeInMillis = System.currentTimeMillis() + CHECKPOINT_INTERVAL_MILLIS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {
        //Call processRecord for each record 
        processRecordsInput.records().forEach(r -> processRecord(r));

    }
    private void processRecord(KinesisClientRecord record) {
        /* 
        * Implement your Record processing logic here
        *  In this demo we just log the payload of the message. 
        */

        byte[] arr = new byte[record.data().remaining()];
        record.data().get(arr);
        String recordData = new String(arr);
        logger.info("Logged record:" + recordData);
    }
    public void leaseLost(LeaseLostInput leaseLostInput) {
        logger.info("Lost lease, so terminating.");
    }
    public void shardEnded(ShardEndedInput shardEndedInput) {
        try {
            // Important to checkpoint after reaching end of shard, so we can start processing data from child shards.
            logger.info("Reached shard end checkpointing.");
            shardEndedInput.checkpointer().checkpoint();
        } catch (ShutdownException | InvalidStateException e) {
            logger.error("Exception while checkpointing at shard end. Giving up.", e);
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdownRequested(ShutdownRequestedInput shutdownRequestedInput) {
        logger.info("Scheduler is shutting down, checkpointing.");
        checkpoint(shutdownRequestedInput.checkpointer());

    }
    private void checkpoint(RecordProcessorCheckpointer checkpointer) {
        logger.info("Checkpointing shard " + kinesisShardId);
        try {
            checkpointer.checkpoint();
        } catch (ShutdownException se) {
            // Ignore checkpoint if the processor instance has been shutdown (fail over).
            logger.info("Caught shutdown exception, skipping checkpoint.", se);
        } catch (ThrottlingException e) {
            // Skip checkpoint when throttled. In practice, consider a backoff and retry policy.
            logger.error("Caught throttling exception, skipping checkpoint.", e);
        } catch (InvalidStateException e) {
            // This indicates an issue with the DynamoDB table (check for table, provisioned IOPS).
            logger.error("Cannot save checkpoint to the DynamoDB table used by the Amazon Kinesis Client Library.", e);
        }
    }
}