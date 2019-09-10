#!/usr/bin/env python
# Usage: python cors_http_server.py <dir> <port>

import sys
import os
os.chdir(sys.argv[1])

try:
    # try to use Python 3
    from http.server import HTTPServer, SimpleHTTPRequestHandler, test as test_orig
    def test (*args):
        test_orig(*args, port=int(sys.argv[2]))
except ImportError: 
	  # fall back to Python 2
    from BaseHTTPServer import HTTPServer, test
    from SimpleHTTPServer import SimpleHTTPRequestHandler

class CORSRequestHandler (SimpleHTTPRequestHandler):
    def end_headers (self):
        self.send_header('Access-Control-Allow-Origin', '*')
        SimpleHTTPRequestHandler.end_headers(self)

if __name__ == '__main__':
    test(CORSRequestHandler, HTTPServer)