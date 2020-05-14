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

import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Value;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.*;

import com.amazonaws.services.kinesis.producer.*;

@RestController
public class InputEventController {

    //Read Config from env variables
    String region = System.getenv("REGION");
    static String streamName = System.getenv("STREAM_NAME");



    private final KinesisProducerConfiguration config = new KinesisProducerConfiguration().setRegion(region);
    private final KinesisProducer kinesis = new KinesisProducer(config);

    //Healthcheck
    @GetMapping(value="/healthcheck")
    public void returnHealthy() {
        //Returns HTTP Status 200 
    }

    //Handler for post requests
    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public void processInputEvent(@RequestBody Map<String, Object> payload) throws UnsupportedEncodingException {
        String element = (String) payload.get("data");
        String key = UUID.randomUUID().toString();
        ByteBuffer data = ByteBuffer.wrap(element.getBytes("UTF-8"));
        kinesis.addUserRecord(streamName, key, data);
        /*
        * You can implement a synchronous or asynchronous response to results
        * https://docs.aws.amazon.com/streams/latest/dev/kinesis-kpl-writing.html 
        */

        return;
    }
} 