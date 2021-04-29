#!/bin/sh
python3 /usr/bin/set_properties.py
chmod 777 /usr/bin/record_processor.py
`python3 /usr/bin/amazon_kclpy_helper.py --print_command --java /usr/bin/java --properties /usr/bin/record_processor.properties --log-configuration /usr/bin/logback.xml` &
touch /app/logs/record_processor.log
exec tail -f /app/logs/record_processor.log