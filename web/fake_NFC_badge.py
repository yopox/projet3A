import json
import time
import sys
import asyncio
import websockets
import database_NFC_id_checker

async def nfcPoll(websocket, path):
    while True:
        for i in range(1,11):
            if i == 1:
                print("sending Thibault's fake badge !")
                badge = {}
                badge["id"] = "123456789"
            elif i == 6:
                print("sending Louis's fake badge !")
                badge = {}
                badge["id"] = "101112131"
            else :
                print("sending nok fake badge !")
                badge = {}
                badge["id"] = "00000000" + str(i)
            user = {}
            user["id"] = database_NFC_id_checker.databaseChecker(badge["id"])
            if user["id"] != None:
                await websocket.send(json.dumps(user))
            time.sleep(1)


start_server = websockets.serve(nfcPoll, "", 3001)

print("starting fake NFC server...")
asyncio.get_event_loop().run_until_complete(start_server)
asyncio.get_event_loop().run_forever()
