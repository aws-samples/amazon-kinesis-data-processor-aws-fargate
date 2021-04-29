#
# Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: MIT-0
#
# This script sets configuration properties for the KCL

import os

with open('/usr/bin/record_processor.properties', 'r') as file :
    filedata = file.read()
    filedata = filedata.replace('AWS_REGION', os.environ['REGION'])
    filedata = filedata.replace('STREAM_NAME', os.environ['STREAM_NAME'])
    filedata = filedata.replace('APPLICATION_NAME', os.environ['APPLICATION_NAME'])

with open('/usr/bin/record_processor.properties', 'w') as file:
    file.write(filedata)
