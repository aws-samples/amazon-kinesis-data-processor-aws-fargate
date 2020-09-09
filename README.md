# Building a scalable streaming data processor with Amazon Kinesis on AWS Fargate

In this blog post, you will see how to build a scalable producer and consumer application for an [Amazon Kinesis Data Stream (KDS)](https://docs.aws.amazon.com/streams/latest/dev/introduction.html) running on [AWS Fargate](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/AWS_Fargate.html). Amazon Kinesis is a fully managed and scalable data stream which enables you to ingest, buffer and process data in real-time. AWS Fargate is a serverless compute engine for containers that works with AWS container orchestration services like [Amazon Elastic Container Service (ECS)](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/Welcome.html), which allows us to easily run, scale and secure containerized applications. Further, this example will leverage the [Kinesis Producer Library (KPL)](https://docs.aws.amazon.com/streams/latest/dev/developing-producers-with-kpl.html#developing-producers-with-kpl-role) and [Kinesis Client Library (KCL)](https://docs.aws.amazon.com/streams/latest/dev/shared-throughput-kcl-consumers.html#shared-throughput-kcl-consumers-overview) to ingest data into the stream and to process it. These libraries help manage the heavy lifting required for retry logic, record aggregation, checkpointing, shard enumeration and other complex tasks for distributed computing, so you can focus on application development.

## Architectural Overview

There are 4 components in the architecture:

1. Producer group (data ingestion)
2. Stream
3. Consumer group (data processor)
4. Kinesis auto scaling Application

![](architecture.png)

For more details of the individual components please refer to associated Blog Post on the [AWS Big Data Blog](https://aws.amazon.com/blogs/big-data/).

## Deployment Guide

### Prequisites

For this walkthrough, you need the following prerequisites: 
* An AWS account

### Step 1: Deploy the CloudFormation Stack

Deploy the CloudFormation Stack by clicking the Launch Stack Button.The template is launched in the US East (N. Virginia) Region by default. To launch it in a different AWS Region, use the region selector in the console navigation bar. The following regions are supported: US East (N. Virgina), US East (Ohio), US West (N.California), US West (Oregon), Asia Pacific (Singapore), Asia Pacific (Sydney), Europe (Frankfurt), Europe (Ireland).

[<img src="https://s3.amazonaws.com/cloudformation-examples/cloudformation-launch-stack.png">](https://console.aws.amazon.com/cloudformation/home?region=us-east-1#/stacks/new?&templateURL=https://flomair-dataprocessor-source.s3-us-west-2.amazonaws.com/deployment.yaml)

Alternatively you can download the [CloudFormation Template File](https://flomair-dataprocessor-source.s3-us-west-2.amazonaws.com/deployment.yaml) and deploy it manually. When asked to provide an IPv4 CIDR Range enter the CIDR range that will be able to send records to your application. You can change that also later on by the [Security Groups Inbound Rule](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_SecurityGroups.html#AddRemoveRules) of the Application Load Balancer.

### Step 2: Send Records to Kinesis

You have several options to send records to Kinesis. You can do it from the CLI, any API Client that can send REST requests, or you can use a load testing solution like [Distributed Load Testing on AWS](https://aws.amazon.com/solutions/distributed-load-testing-on-aws/) or [Artillery](https://artillery.io/). With load testing additional charges for requests occur, as a guideline 10,000 requests per second for 10 minutes will generate an AWS bill of less than $5. To do a POST request via curl run the following command and replace ALB_ENDPOINT with the DNS record of your ALB. You can find it in the CloudFormation stack’s outputs section. Ensure you have a JSON element “data”. Otherwise the application will not be able to process the record.
```bash
curl --location --request POST 'http://ALB-DNS-record' --header 'Content-Type: application/json' --data-raw '{"data":"This is a load testing record"}
```

### Step 3: Monitor your Stream and Applications

With the resource creation via the CloudFormation Template a CloudWatch Dashboard has been created. You can find this in the AWS Console within CloudWatch or by clicking the Link in the CloudFormation Stack's output section.

![](dashboard.png)


### Cleanup

To avoid additional cost, ensure that the provisioned resources are decommissioned. To do that delete the images in the ECR repository, the CloudFormation stack and any remaining resources that were not deleted by the CloudFormation stack.


## License

This library is licensed under the MIT-0 License. See the LICENSE file.
