FROM public.ecr.aws/amazonlinux/amazonlinux:2023
RUN yum install python3-pip git java-11-amazon-corretto-headless -y
RUN git clone --depth 1 --branch v2.1.1 https://github.com/awslabs/amazon-kinesis-client-python
RUN pip3 install setuptools
RUN cd amazon-kinesis-client-python &&\
    python3 setup.py download_jars &&\
    python3 setup.py install &&\
    chmod 777 /usr/bin

COPY record_processor.py logback.xml run.sh  set_properties.py sample.properties /usr/bin/
ENTRYPOINT ["sh", "/usr/bin/run.sh"]


