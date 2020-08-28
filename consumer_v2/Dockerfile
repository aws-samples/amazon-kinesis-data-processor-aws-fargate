FROM amazonlinux:2

RUN yum install -y java-1.8.0-openjdk.x86_64 &&\
    yum install -y amazon-linux-extras &&\
    amazon-linux-extras install python3 &&\
    yum install -y git &&\
    pip3 install amazon_kclpy

RUN git clone --depth 1 --branch v2.0.1 https://github.com/awslabs/amazon-kinesis-client-python /kclpy &&\
    mv /kclpy/amazon_kclpy / &&\
    mkdir app &&\
    mv /kclpy/samples/amazon_kclpy_helper.py /app &&\
    rm -rf /kclpy

COPY record_processor.properties record_processor.py setup.py /app/

RUN python3 app/setup.py install

ENTRYPOINT `python3 /usr/bin/amazon_kclpy_helper.py --print_command --java /usr/bin/java --properties app/record_processor.properties`
