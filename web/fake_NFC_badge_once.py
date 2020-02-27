import json
import time
import sys
import asyncio
import websockets
import database_NFC_id_checker
from random import randrange

async def nfcPoll(websocket, path):
    while True: 
        i = randrange(1,11)
        print("sending nok fake badge !")
        badge = {}
        badge["id"] = "00000000" + str(i)
        user = {}
        user["username"] = database_NFC_id_checker.databaseChecker(badge["id"])
        user["badgeid"] = badge["id"]
        await websocket.send(json.dumps(user))
        break


start_server = websockets.serve(nfcPoll, "localhost", 3001)

print("starting fake NFC server...")
asyncio.get_event_loop().run_until_complete(start_server)
asyncio.get_event_loop().run_forever
