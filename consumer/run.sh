#!/bin/sh
python3.8 app/set_properties.py
`python3.8 /usr/bin/amazon_kclpy_helper.py --print_command --java /usr/bin/java --properties app/record_processor.properties --log-configuration app/logback.xml` &
touch /app/logs/record_processor.log
exec tail -f /app/logs/record_processor.log
