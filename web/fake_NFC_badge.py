import json
import time
import sys
import asyncio
import websockets
import database_utils
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
    user["username"] = database_utils.databaseChecker(badge["id"])
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
            if (user not in users_list):
                users_list.append(user)
            await websocket.send(json.dumps(user))
    finally:
        return users_list




async def handler(websocket, path):
    # Awaiting connection type
    # 0: Once
    # 1: Class Entrance verification
    # 2: Class Exit Verification
    type_of_connection = await websocket.recv()
    type_of_connection = json.loads(type_of_connection)

    # Once 
    if type_of_connection["type"] == 0:
        print("Type of connection : once")
        classID = None
        user = await checkOnce()
        await websocket.send(json.dumps(user))

    # Class Entrance and Exit
    else :
        classID = type_of_connection["id"]
        users = await checkClass(websocket)


        file = open("testfile.txt","a") 
        if (type_of_connection["type"] == 1):
            print("Type of connection : class entry")
            database_utils.saveClassEntry(classID,users)

            
        if (type_of_connection["type"] == 2):
            print("Type of connection : class exit")
            database_utils.saveClassExit(classID,users)


        file.writelines(["\n" + str(x) for x in users])
        file.close()
        
    print("Closing Connection...")
    


start_server = websockets.serve(handler, "", 3001)

print("starting fake NFC server...")
asyncio.get_event_loop().run_until_complete(start_server)
asyncio.get_event_loop().run_forever()
