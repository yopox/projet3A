import nxppy
import json
import time
import sys
import asyncio
import websockets
import database_NFC_id_checker


mifare = nxppy.Mifare()

async def nfcPoll(websocket, path):
    while True:
        try:
            print("found badge !")
            uid = mifare.select()
            badge = {}
            user = {}
            badge["id"] = uid
            user["username"] = database_NFC_id_checker.databaseChecker(badge["id"])
            user["badgeid"] = badge["id"]
            await websocket.send(json.dumps(user))
            return
        except nxppy.SelectError:
            # SelectError is raised if no card is in the field.
            pass
        time.sleep(.1)


start_server = websockets.serve(nfcPoll, "", 3001)

print("starting server...")
asyncio.get_event_loop().run_until_complete(start_server)
asyncio.get_event_loop().run_forever()
