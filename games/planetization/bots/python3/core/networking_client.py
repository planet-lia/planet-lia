import argparse
import json
import traceback
import websockets

from core.response import Response


async def connect(bot):
    parser = argparse.ArgumentParser()
    parser.add_argument("-p", "--port", help="Identification token with which bot can connect", required=False)
    parser.add_argument("-t", "--token", help="Port of match generator to which to connect", required=False)
    args = parser.parse_args()

    if args.port is None:
        args.port = "8887"
    if args.token is None:
        args.token = "_"

    async with websockets.connect("ws://localhost:" + args.port, extra_headers={'token': args.token}) as websocket:
        try:
            while True:
                message = await websocket.recv()
                data = json.loads(message)

                response = Response(data['__uid'])
                try:
                    if data['__type'] == 'INITIAL':
                        bot.setup(data)

                    elif data['__type'] == 'UPDATE':
                        bot.update(data, response)

                    response_json = json.dumps(response.__dict__)\
                        .replace("_Response__uid", "__uid")
                    await websocket.send(response_json)
                except Exception as e:
                    print(e)
                    traceback.print_exc()

        except websockets.ConnectionClosed:
            print("Connection closed. Exiting...")
        except Exception:
            traceback.print_exc()
