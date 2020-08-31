#!/bin/sh
`python3 /usr/bin/amazon_kclpy_helper.py --print_command --java /usr/bin/java --properties app/record_processor.properties --log-configuration app/logback.xml` &
sleep 10
touch /app/record_processor.log
exec tail -f /app/record_processor.log