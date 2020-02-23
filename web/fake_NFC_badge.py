import json
import time
import sys
import asyncio
import websockets

async def nfcPoll(websocket, path):
    while True:
        for i in range(1,11):
            if i == 1:
                print("sending Thibault fake badge !")
                badge = {}
                badge["id"] = "123456789"
            elif i == 6 :
                print("sending Louis fake badge !")
                badge = {}
                badge["id"] = "101112131"
            else :
                print("sending nok fake badge !")
                badge = {}
                badge["id"] = "00000000" + str(i)
            print(json.dumps(badge))
            await websocket.send(json.dumps(badge))
            time.sleep(1)


start_server = websockets.serve(nfcPoll, "", 3001)

print("starting fake NFC server...")
asyncio.get_event_loop().run_until_complete(start_server)
asyncio.get_event_loop().run_forever()
