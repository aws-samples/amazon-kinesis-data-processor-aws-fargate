#
# Build stage
#

FROM maven:3.6.0-jdk-11-slim AS build
WORKDIR /usr/local/kinesis_scaling
RUN apt-get update        
RUN apt-get install -y git
RUN git clone --branch V.9.6.0 https://github.com/awslabs/amazon-kinesis-scaling-utils.git .
RUN mvn dependency:go-offline
RUN mvn clean package assembly:assembly

#
# Package stage
#
FROM openjdk:11-jre-slim
COPY --from=build /usr/local/kinesis_scaling/target/KinesisScalingUtils-.9.6.0-complete.jar /usr/local/lib/KinesisScalingUtils-.9.6.0-complete.jar
ENTRYPOINT java -Dconfig-file-url=${CONFIG_FILE} -cp /usr/local/lib/KinesisScalingUtils-.9.6.0-complete.jar com.amazonaws.services.kinesis.scaling.auto.AutoscalingController