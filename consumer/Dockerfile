FROM amazonlinux:2

RUN yum install -y java-1.8.0-openjdk.x86_64 &&\
    yum install -y amazon-linux-extras &&\
    amazon-linux-extras install python3 &&\
    yum install -y git &&\
    pip3 install amazon_kclpy

RUN git clone --depth 1 --branch v2.0.1 https://github.com/awslabs/amazon-kinesis-client-python /kclpy &&\
    mv /kclpy/amazon_kclpy / &&\
    mkdir -p app/logs &&\
    mv /kclpy/samples/amazon_kclpy_helper.py /app &&\
    rm -rf /kclpy

COPY record_processor.properties record_processor.py setup.py logback.xml run.sh  set_properties.py /app/

RUN python3 app/setup.py install &&\
    chmod 755 /app/run.sh


ENTRYPOINT ["sh", "./app/run.sh"]
