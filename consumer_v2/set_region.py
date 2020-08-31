#
# Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: MIT-0
#

import os

with open('app/record_processor.properties', 'r') as file :
    filedata = file.read()
    filedata = filedata.replace('AWS_REGION', os.environ['AWS_REGION'])

with open('app/record_processor.properties', 'w') as file:
    file.write(filedata)
