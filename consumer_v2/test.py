import logging

logging.basicConfig(filename='record_processor.log',
                    level=logging.INFO,
                    format='%(asctime)s [%(module)s] %(levelname)s  %(funcName)s - %(message)s',
                    datefmt='%H:%M:%S'
                    )

def record_processor():
    logging.info('test')
record_processor()