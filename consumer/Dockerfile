FROM public.ecr.aws/amazonlinux/amazonlinux:2

RUN yum install -y java-1.8.0-openjdk.x86_64 &&\
    yum install -y python3.x86_64 &&\
    yum install -y git &&\
    python3 -m pip install amazon_kclpy

RUN git clone --depth 1 --branch v2.0.1 https://github.com/awslabs/amazon-kinesis-client-python /kclpy &&\
    mv /kclpy/amazon_kclpy / &&\
    mkdir -p app/logs &&\
    mv /kclpy/samples/amazon_kclpy_helper.py /usr/bin &&\
    rm -rf /kclpy

COPY record_processor.properties record_processor.py setup.py logback.xml run.sh  set_properties.py /usr/bin/

RUN python3 /usr/bin/setup.py install &&\
    chmod 777 /usr/bin


ENTRYPOINT ["sh", "/usr/bin/run.sh"]
