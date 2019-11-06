/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thehumblefool;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A starter example for writing Beam programs.
 *
 * <p>
 * The example takes two strings, converts them to their upper-case
 * representation and logs them.
 *
 * <p>
 * To run this starter example locally using DirectRunner, just execute it
 * without any additional parameters from your favorite development environment.
 *
 * <p>
 * To run this starter example using managed resource in Google Cloud Platform,
 * you should specify the following command-line options:
 * --project=<YOUR_PROJECT_ID>
 * --stagingLocation=<STAGING_LOCATION_IN_CLOUD_STORAGE> --runner=DataflowRunner
 */
public class StarterPipeline {

    private static final Logger LOG = LoggerFactory.getLogger(StarterPipeline.class);

    public static void main(String[] args) {
        Pipeline pipeline = Pipeline.create(PipelineOptionsFactory.fromArgs(args).withValidation().create());
        //        Map<String, Object> config = new HashMap<>();
        //        config.put("group.id", "my-beam-app");
        //        config.put("enable.auto.commit", true);
        //        pipeline 
        //                .apply("kafka-read", KafkaIO.<String, String>read()
        //                        .withBootstrapServers("localhost:9092")
        //                        .withTopic("rndm-num")
        //                        .withKeyDeserializer(StringDeserializer.class)
        //                        .withValueDeserializer(StringDeserializer.class)
        //                        .updateConsumerProperties(config)
        //                        .withoutMetadata()
        //                )
        //                .apply("values-only", Values.<String>create())
        //                .apply("10-sec-window", Window.<String>into(FixedWindows.of(Duration.standardSeconds(10))))
        //        .apply("even-odd-extractor", ParDo.of(new DoFn<String, Void>() {
        //            @DoFn.ProcessElement
        //            public void oddOrEven(DoFn.ProcessContext pc) {
        ////            public void oddOrEven(String num) {  <-- this is not working although directed by docs
        //                String num = pc.element();
        //                if (Integer.parseInt(num) % 2 == 0) {
        //                    System.out.println(num + " is even");
        //                } else {
        //                    System.out.println(num + " is odd");
        //                }
        //            }
        //        }));
        //                .apply("logging", org.apache.beam.sdk.transforms.ParDo.of(new DoFn<String, Void>() {
        //
        //                }));

        PCollection<String> nums = pipeline.apply("nums.txt-read", TextIO.read().from("/home/TheHumbleFool/Documents/nums.txt"));

        /**
         * prints odd/even
         */
        nums.apply("even-odd-print", ParDo.of(new DoFn<String, Void>() {
            @DoFn.ProcessElement
            public void oddOrEven(@DoFn.Element String num) {
                if (Integer.parseInt(num) % 2 == 0) {
                    System.out.println(num + " is even");
                } else {
                    System.out.println(num + " is odd");
                }
            }
        }));

        System.out.println();
        
        /**
         * prints num count
         */
        nums.apply("num-count-transform", Count.<String>perElement())
                .apply("num-count-print", ParDo.of(new DoFn<KV<String, Long>, Void>() {
                    @DoFn.ProcessElement
                    public void printElementCount(@DoFn.Element KV<String, Long> kv) {
                        System.out.println("Count for element: " + kv.getKey() + " is: " + kv.getValue());
                    }
                }));
        
        PipelineOptions options = pipeline.getOptions();
        System.out.println(options.getJobName());
        System.out.println(options.getOptionsId());
        System.out.println(options.getRunner());
        System.out.println(options.getUserAgent());

        pipeline.run();

        System.out.println("exited");
    }
}
