#!/bin/sh
python3 /usr/bin/set_properties.py
chmod 777 /usr/bin/record_processor.py
`python3 /usr/local/lib/python3.9/site-packages/samples/amazon_kclpy_helper.py --print_command --java /usr/bin/java --properties /usr/bin/record_processor.properties --log-configuration /usr/bin/logback.xml` &
mkdir -p /app/logs
touch /app/logs/record_processor.log
exec tail -F /app/logs/record_processor.log
