import json
import time
import sys
import asyncio
import websockets
import database_NFC_id_checker
from random import randrange

connected = set()


async def fakeNFCServer():
    i = randrange(1,11)
    if i == 1:
        print("sending Thibault's badge !")
        badge = {}
        badge["id"] = "805A4232305704"
    elif i == 6:
        print("sending Louis's fake badge !")
        badge = {}
        badge["id"] = "805A423A821E04"
    else :
        print("sending nok fake badge !")
        badge = {}
        badge["id"] = "00000000" + str(i)
    user = {}
    user["username"] = database_NFC_id_checker.databaseChecker(badge["id"])
    user["badgeid"] = badge["id"]
    await asyncio.sleep(.5)
    return user

async def checkOnce():
    return await fakeNFCServer()

async def checkClass(websocket):
    users_list = []
    try:
        while True:
            user = await fakeNFCServer()
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
