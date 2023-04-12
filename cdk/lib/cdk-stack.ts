import * as cdk from 'aws-cdk-lib';
import {Construct} from 'constructs';
import * as ec2 from 'aws-cdk-lib/aws-ec2';
import * as kinesis from 'aws-cdk-lib/aws-kinesis';
import {StreamEncryption, StreamMode} from 'aws-cdk-lib/aws-kinesis';
import * as ecs from 'aws-cdk-lib/aws-ecs';
import * as iam from 'aws-cdk-lib/aws-iam';
import * as logs from 'aws-cdk-lib/aws-logs';
import {Compatibility,} from 'aws-cdk-lib/aws-ecs';
import * as ecsPatterns from 'aws-cdk-lib/aws-ecs-patterns';
import * as path from "path";
import {CfnOutput, Duration} from "aws-cdk-lib";

export class CdkStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const APPLICATION_NAME = 'DataProcessorConsumer'

    const vpc = new ec2.Vpc(this, 'vpc');

    const stream = new kinesis.Stream(this, 'stream', {
      encryption: StreamEncryption.MANAGED,
      streamMode: StreamMode.ON_DEMAND
    })

    const cluster = new ecs.Cluster(this, 'cluster', {
      vpc
    })

    const producerTaskDefinition = new ecs.FargateTaskDefinition(this, 'producerTaskDefintion', {
      family: 'producer',
      cpu: 2048,
      memoryLimitMiB: 4096,
    })

    const producerContainer = producerTaskDefinition.addContainer('producerContainer', {
      image: ecs.ContainerImage.fromAsset(path.join(__dirname, '../../producer')),
      logging: ecs.LogDrivers.awsLogs({
        streamPrefix: 'kinesisProducer',
        logRetention: logs.RetentionDays.ONE_WEEK
      }),
      cpu: 2048,
      memoryLimitMiB: 4096,
      environment: {
        'REGION': this.region,
        'STREAM_NAME': stream.streamName
      }
    })

    producerContainer.addPortMappings({
      containerPort: 8080,
      protocol: ecs.Protocol.TCP
    })

    stream.grantWrite(producerTaskDefinition.taskRole)
    //producerTaskDefinition.taskRole. add cloudwatch metrics permissions  cloudwatch:PutMetricData


    const producer = new ecsPatterns.ApplicationLoadBalancedFargateService(this, 'producerService', {
      taskDefinition: producerTaskDefinition,
      openListener: false,
      assignPublicIp: false,
      cluster

    })
    producer.targetGroup.configureHealthCheck({
      path: '/healthcheck',
      port: '8080',
      timeout: Duration.seconds(5),
      interval: Duration.seconds(6),
    })

    const producerScaler = producer.service.autoScaleTaskCount({
      minCapacity: 1,
      maxCapacity: 10
    })

    producerScaler.scaleOnCpuUtilization('cpuScalingProducer', {
      targetUtilizationPercent: 70
    })

    const consumerRole = new iam.Role(this, 'consumerRole', {
      assumedBy: new iam.ServicePrincipal('ecs-tasks.amazonaws.com')
    })

    const consumerPolicy = new iam.ManagedPolicy(this, 'consumerIamPolicy', {
      statements: [
        new iam.PolicyStatement({
          actions: ['cloudwatch:PutMetricData', 'kinesis:ListShards', 'kinesis:DescribeLimits'],
          effect: iam.Effect.ALLOW,
          resources: ['*']
        }),
        new iam.PolicyStatement({
          actions: ['kinesis:DescribeStreamConsumer', 'kinesis:SubscribeToShard'],
          effect: iam.Effect.ALLOW,
          resources: [`${stream.streamArn}/consumer/*`, stream.streamArn]
        }),
        new iam.PolicyStatement({
          actions: ["kinesis:DeregisterStreamConsumer", "dynamodb:PutItem", "dynamodb:DeleteItem", "kinesis:GetShardIterator", "dynamodb:Scan", "dynamodb:UpdateItem", "dynamodb:DeleteTable", "kinesis:RegisterStreamConsumer", "dynamodb:CreateTable", "kinesis:DescribeStreamSummary", "dynamodb:DescribeTable", "dynamodb:GetItem", "kinesis:GetRecords", "dynamodb:UpdateTable", "dynamodb:GetRecords"],
          effect: iam.Effect.ALLOW,
          resources: [`arn:aws:dynamodb:${this.region}:${this.account}:table/${APPLICATION_NAME}`, stream.streamArn]
        })
      ]
    })

    const consumerTaskDefinition = new ecs.TaskDefinition(this, 'consumerTaskDefinition', {
      compatibility: Compatibility.FARGATE,
      cpu: '2048',
      memoryMiB: '4096'
    })

    consumerTaskDefinition.addContainer('consumerContainer', {
      image: ecs.ContainerImage.fromAsset(path.join(__dirname, '../../consumer')),
      logging: ecs.LogDrivers.awsLogs({
        streamPrefix: 'kclConsumerPython',
        logRetention: logs.RetentionDays.ONE_WEEK
      }),
      environment: {
        'STREAM_NAME': stream.streamName,
        'REGION': this.region,
        'APPLICATION_NAME': APPLICATION_NAME
      }
    })

    const consumer = new ecs.FargateService(this, 'consumerService', {
      cluster,
      taskDefinition: consumerTaskDefinition,
      assignPublicIp: false,
    })

    const consumerScaling = consumer.autoScaleTaskCount({
      maxCapacity: 10,
      minCapacity: 1
    })

    consumerScaling.scaleOnCpuUtilization('cpuScalingConsumer', {
      targetUtilizationPercent: 70
    })

    const albEndpoint = new CfnOutput(this, 'albEndpoint', {
      value: producer.loadBalancer.loadBalancerDnsName
    })

  }
}
