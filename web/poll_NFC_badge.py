import nxppy
import json
import time
import sys
import asyncio
import websockets


mifare = nxppy.Mifare()

async def nfcPoll(websocket, path):
    while True:
        try:
            print("found badge !")
            uid = mifare.select()
            badge = {}
            badge["id"] = uid
            print(json.dumps(badge))
            await websocket.send(json.dumps(badge))
        except nxppy.SelectError:
            # SelectError is raised if no card is in the field.
            print("no badge found")
            pass
        time.sleep(.1)


start_server = websockets.serve(nfcPoll, "", 3001)

print("starting server...")
asyncio.get_event_loop().run_until_complete(start_server)
asyncio.get_event_loop().run_forever()
