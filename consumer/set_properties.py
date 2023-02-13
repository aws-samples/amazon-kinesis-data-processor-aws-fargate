#
# Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: MIT-0
#
# This script sets configuration properties for the KCL

import os

with open('/usr/bin/sample.properties', 'r') as file :
    filedata = file.read()
    filedata = filedata.replace('sample_kclpy_app.py', 'record_processor.py')
    filedata = filedata.replace('us-east-1', os.environ['REGION'])
    filedata = filedata.replace('kclpysample', os.environ['STREAM_NAME'])
    filedata = filedata.replace('PythonKCLSample', os.environ['APPLICATION_NAME'])

with open('/usr/bin/record_processor.properties', 'w') as file:
    file.write(filedata)
