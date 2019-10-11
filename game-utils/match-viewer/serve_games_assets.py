#!/usr/bin/env python

import argparse
import os
import sys

parser = argparse.ArgumentParser()
parser.add_argument("dir", help="Root dir for the HTTP server", nargs=1)
parser.add_argument("-p", "--port", type=int, help="Port to bind the HTTP server", required=False, default=3333)
parser.add_argument("-b", "--bind", help="Bind address of HTTP server", required=False, default="127.0.0.1")
cli_args = parser.parse_args()

os.chdir(cli_args.dir[0])

try:
    # try to use Python 3
    from http.server import HTTPServer, SimpleHTTPRequestHandler, test as test_orig
    def test (*args):
        test_orig(*args, bind=cli_args.bind, port=cli_args.port)
except ImportError: 
	  # fall back to Python 2
    from BaseHTTPServer import HTTPServer, test
    from SimpleHTTPServer import SimpleHTTPRequestHandler

class CORSRequestHandler(SimpleHTTPRequestHandler):
    def end_headers (self):
        self.send_header('Access-Control-Allow-Origin', '*')
        SimpleHTTPRequestHandler.end_headers(self)

if __name__ == '__main__':
    test(CORSRequestHandler, HTTPServer)