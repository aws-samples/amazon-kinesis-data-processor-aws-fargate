#!/bin/sh
python3 /usr/bin/set_properties.py
chmod 777 /usr/bin/record_processor.py
`python3 /usr/bin/amazon_kclpy_helper.py --print_command --java /usr/bin/java --properties /usr/bin/sample.properties --log-configuration /usr/bin/logback.xml` &
touch /app/logs/record_processor.log
exec tail -F /app/logs/record_processor.log
