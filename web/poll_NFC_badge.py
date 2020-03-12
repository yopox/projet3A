import nxppy
import json
import time
import sys
import asyncio
import websockets
import database_NFC_id_checker


mifare = nxppy.Mifare()

async def nfcPoll():
    while True:
        try:
            print("found badge !")
            uid = mifare.select()
            if (len(uid)%2 != 0 ):
                uid = str(0) + uid
            new_uid = ""
            for i in range(len(uid)//2) :
                new_uid = uid[int(2*i)] + uid[int(2*i+1)] + new_uid
            uid = new_uid
            badge = {}
            user = {}
            badge["id"] = uid
            user["username"] = database_NFC_id_checker.databaseChecker(badge["id"])
            user["badgeid"] = badge["id"]
            return user
        except nxppy.SelectError:
            # SelectError is raised if no card is in the field.
            pass
        await asyncio.sleep(.7)


async def checkOnce():
    return await nfcPoll()

async def checkClass(websocket):
    users_list = []
    try:
        while True:
            user = await nfcPoll()
            users_list.append(user)
            await websocket.send(json.dumps(user))
    finally:
        return users_list

async def handler(websocket, path):
    # Awaiting connection type
    # 0: Once
    # 1: Class verification
    type_of_connection = await websocket.recv()
    type_of_connection = json.loads(type_of_connection)
    if type_of_connection["id"] == 0:
        print("Type of connection : once")
        user = await checkOnce()
        await websocket.send(json.dumps(user))
        
    elif type_of_connection["id"] == 1:
        print("Type of connection : class")
        await checkClass(websocket)
        print("Closing Connection...")



start_server = websockets.serve(handler, "", 3001)

print("starting fake NFC server...")
asyncio.get_event_loop().run_until_complete(start_server)
asyncio.get_event_loop().run_forever()